package it.mapsgroup.segnaler.camunda.rest.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import it.mapsgroup.segnaler.camunda.rest.client.vo.BasicUser;
import it.mapsgroup.segnaler.camunda.rest.client.vo.ProcessA1Request;
import it.mapsgroup.segnaler.camunda.rest.client.vo.ProcessA2Request;
import it.mapsgroup.segnaler.camunda.rest.client.vo.ProcessInstance;
import it.mapsgroup.segnaler.camunda.rest.client.vo.Task;
import it.mapsgroup.segnaler.camunda.rest.client.vo.TaskWithParentProcess;
import it.mapsgroup.segnaler.camunda.rest.client.vo.User;
import it.mapsgroup.segnaler.camunda.rest.client.vo.UserCredentials;
import it.mapsgroup.segnaler.camunda.rest.client.vo.UserProfile;
import it.mapsgroup.segnaler.camunda.rest.client.vo.variables.ProcessCustomVariables;
import it.mapsgroup.segnaler.camunda.util.JsonDataParser;
import it.mapsgroup.segnaler.camunda.util.JsonFormatter;


/*
 * https://spring.io/blog/2009/03/27/rest-in-spring-3-resttemplate/
 * https://docs.spring.io/spring-framework/docs/3.0.x/javadoc-api/org/springframework/web/client/RestTemplate.html
 * https://docs.camunda.org/manual/7.15/reference/rest/
 */
public class SegnalERRestClient {
	
	static RestTemplate restTemplate;

	public static void main(String[] args) {
		restTemplate = new RestTemplate();
		
		boolean auto = false;
		
		if (auto) {
			// Faccio partire 2 istanze del processo ProcessA1
			startProcessA1("Business key: assegnato a Pino", "Pino", "Pino, fai andare le cose", true);
			startProcessA1("Business key: assegnato a Gino", "Gino", "Gino, controlla Pino", true);
			
			Task[] tasks = listTasks();
			
			UserProfile[] users = listUsers();
			
			if (!_contains(users, "level1")) {
				createUser("level1", "Level", "One", "level1@my-mail.com");
			}
			if (!_contains(users, "level2")) {
				createUser("level2", "Level", "Two", "level2@my-mail.com");
			}
			
			assignTask(tasks[0], "level1");
			assignTask(tasks[1], "level2");
		}
		
		boolean stayIn = true;
		while (stayIn) {
			try {
				stayIn = mainAskForUserAction();
			} catch (Exception e) {
				System.err.println("Problemi a eseguire un task: " + e.getMessage());
			}
		}

	}

	private static boolean _contains(UserProfile[] users, String username) {
		if (users == null || users.length == 0) {
			return false;
		}
		
		for (UserProfile up : users) {
			if (up.id.equalsIgnoreCase(username)) {
				System.out.println("User " + username + " already exists (" + up.id + ")");
				return true;
			}
		}
		
		return false;
	}

	/*
	 * Ritorna i task ATTIVI (delete_reason_ is null)
	 */
	private static Task[] listTasks() {
		Task[] tasks = listAllTasks();
		System.out.println("\r\n\r\n\r\nCi sono " + tasks.length + " task");
		for (Task task : tasks) {
			System.out.println(task);
		}
		System.out.println("Ecco stampati i " + tasks.length + " task");
		return tasks;
	}
	
	private static TaskWithParentProcess _getTaskDetail() throws Exception {
		String taskId = readFromInputLine("Inserisci l'id del task: ");
		
		TaskWithParentProcess taskDetail = getTaskDetail(taskId);
		System.out.println("Recuperato il dettaglio del task con i dati del processo che lo contiene:");
		System.out.println(taskDetail);
		return taskDetail;
	}
	
	private static TaskWithParentProcess getTaskDetail(String taskId) {
		try {
			String taskJson = restTemplate.getForObject("http://localhost:8080/engine-rest/task?taskId=" + taskId, String.class);
			
			TaskWithParentProcess[] tasks = (TaskWithParentProcess[]) JsonDataParser.parseObjectOrArray(taskJson, TaskWithParentProcess[].class);
			
			if (tasks == null || tasks.length != 1) {
				throw new RuntimeException("Paer il taskId " + taskId + " mi aspettavo un task invece ..." );
			}
			
			TaskWithParentProcess task = tasks[0];
			//ProcessCustomVariables processCustomVariables = getProcessCustomVariables(task.processInstanceId);
			
			task.parentProcess = getProcessById(task.processInstanceId);
			task.parentProcess.variables = getProcessCustomVariables(task.processInstanceId);
			
			System.out.println("recuperato il task (con attaccato il suo parent process): " + task);
			return task;
		} catch (Exception e) {
			System.err.println("Problema a recuperare il task " + taskId + ": " + e.getMessage());
			return null;
		}
	}

	private static void deleteTask() throws Exception {
		String taskId = readFromInputLine("Inserisci l'id del task da cancellare (* per provare a cancellarli tutti): ");
		int deleted = 0, notDeleted = 0;
		
		if (taskId.equals("*")) {
			Task[] tasks = listAllTasks();
			for (Task task : tasks) {
				boolean ok =_deleteTaskById(task.id);
				if (ok) deleted ++; else notDeleted ++;				
			}
			
		} else {
			boolean ok =_deleteTaskById(taskId);
			if (ok) deleted ++; else notDeleted ++;
		}

		System.out.println("Task cancellati = " + deleted + ", task NON cancellati " + notDeleted);
	}
	
	private static boolean _deleteTaskById(String taskId) {
		try {
			restTemplate.delete("http://localhost:8080/engine-rest/task/" + taskId);
			
			System.out.println("Cancellato il task " + taskId);
			return true;
		} catch (Exception e) {
			System.err.println("Problema a cancellare il task " + taskId + ": " + e.getMessage());
			return false;
		}
		
	}
	
	private static void deleteProcessInstances() throws Exception {
		ProcessInstance[] pis = listAllProcessInstances();
		int deleted = 0, notDeleted = 0;

		for (ProcessInstance pi : pis) {
			boolean ok = _deleteProcessInstanceById(pi.id);
			if (ok) deleted ++; else notDeleted ++;				
		}

		System.out.println("Istanze di processo cancellate = " + deleted + ", NON cancellate = " + notDeleted);

	}
	
	private static boolean _deleteProcessInstanceById(String processInstanceId) {
		try {
			restTemplate.delete("http://localhost:8080/engine-rest/process-instance/" + processInstanceId);
			
			System.out.println("Cancellata la istanza processo " + processInstanceId);
			return true;
		} catch (Exception e) {
			System.err.println("Problema a cancellare la istanza processo " + processInstanceId + ": " + e.getMessage());
			return false;
		}
		
	}
	
	
	
	public static boolean mainAskForUserAction() throws Exception {
		System.out.println();
		System.out.println();
		System.out.println("============= Possibili opzioni ===================================================");
		System.out.println("1 ..... Elenco dei processi ATTIVI (tutte le versioni)             [act_re_procdef]");
		System.out.println("2.1 ... Fai partire un'istanza del processo ProcessA1");
		System.out.println("2.2 ... Fai partire un'istanza del processo ProcessA2");
		System.out.println("3 ..... Elenco dei task                                           [act_hi_taskinst]");       
		System.out.println("3a .... Dettaglio di un task                                                       ");       
		System.out.println("4 ..... Elenco degli utenti");
		System.out.println("4a .... Dettaglio utente");
		System.out.println("4b .... Crea utente");
		System.out.println("5 ..... Assegna task a un utente");
		System.out.println("6 ..... Aggiorna task");
		System.out.println("7 ..... Cancella task(s)");
		System.out.println("8 ..... Cancella tutte le istanze di processo");
		System.out.println("===================================================================================");
		System.out.println();
		System.out.println();
		
		String choice = readFromInputLine("Digita il codice dell'azione richiesta ('q' per uscire-quit): ");

		if (choice.equalsIgnoreCase("q")) {
			return false;
		}
		
		if (choice.equals("1")) {
			listProcesses();
		} else if (choice.equals("2.1")) {
			_startProcessA1();
		} else if (choice.equals("2.2")) {
			_startProcessA2();
		} else if (choice.equals("3")) {
			listTasks();
		} else if (choice.equals("3a")) {
			_getTaskDetail();
		} else if (choice.equals("4")) {
			listUsers();
		} else if (choice.equals("4a")) {
			getUser();
		} else if (choice.equals("4b")) {
			createUser();
		} else if (choice.equals("5")) {
			assignTaskToUser();
		} else if (choice.equals("6")) {
			updateTask();
		} else if (choice.equals("7")) {
			deleteTask();
		} else if (choice.equals("8")) {
			deleteProcessInstances();
		} else {
			System.err.println("Valore " + choice + " ignoto o non implementato");
		}
		
		return true;
	}
		
	private static String readFromInputLine(String hint) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

		System.out.print(hint);
		String value = reader.readLine();
		
		if (value == null || value.equals("")) {
			value = "Better use readFromInputLine(String hint, String defaultValue)";
		}

		return value;
	}
	
	private static String readFromInputLine(String hint, String defaultValue) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

		System.out.print(hint + ": ");
		String value = reader.readLine();
		
		if (value == null || value.equals("")) {
			value = defaultValue;
		}

		return value;
	}
	
	/*
	    {
        "id": "ProcessA1:8:60329a6a-ba49-11eb-8eaa-3ef8623e7f01",
        "key": "ProcessA1",
        "category": "http://bpmn.io/schema/bpmn",
        "description": null,
        "name": null,
        "version": 8,
        "resource": "processA1.bpmn",
        "deploymentId": "5fdefc24-ba49-11eb-8eaa-3ef8623e7f01",
        "diagram": null,
        "suspended": false,
        "tenantId": null,
        "versionTag": null,
        "historyTimeToLive": null,
        "startableInTasklist": true
    }
	 */
	private static void listProcesses() {
		Map<String, String> vars = new HashMap<String, String>();
		String result = restTemplate.getForObject("http://localhost:8080/engine-rest/process-instance", String.class, vars);
		
		it.mapsgroup.segnaler.camunda.rest.client.vo.Process[] processes = (it.mapsgroup.segnaler.camunda.rest.client.vo.Process[]) JsonDataParser.parseObjectOrArray(result, it.mapsgroup.segnaler.camunda.rest.client.vo.Process[].class);
		System.out.println("\r\n\r\n\r\nThere are " + processes.length + " ACTIVE processes");
		for (it.mapsgroup.segnaler.camunda.rest.client.vo.Process process : processes) {
			
			/*
			 * Per ogni processo prendo le variabili
			 */
//			String variables = restTemplate.getForObject("http://localhost:8080/engine-rest/process-instance/" + process.id + "/variables", String.class, vars);
//			ProcessCustomVariables customVariables = (ProcessCustomVariables)JsonDataParser.parseObjectOrArray(variables, ProcessCustomVariables.class);
//			
//			process.variables = customVariables;
			process.variables = getProcessCustomVariables(process.id);
			System.out.println(process.variables);

		}
		
		// Loggo tutto (processi e variabili)
		
		System.out.println("\r\n\r\n\r\nThere are " + processes.length + " ACTIVE processes");
		for (it.mapsgroup.segnaler.camunda.rest.client.vo.Process process : processes) {
			System.out.println(process);
			//System.out.println(process.processCustomVariables);
		}
	}
	
	public static it.mapsgroup.segnaler.camunda.rest.client.vo.Process getProcessById(String processId) {
		Map<String, String> vars = new HashMap<String, String>();
		String result = restTemplate.getForObject("http://localhost:8080/engine-rest/process-instance/" + processId, String.class, vars);
		
		it.mapsgroup.segnaler.camunda.rest.client.vo.Process process = (it.mapsgroup.segnaler.camunda.rest.client.vo.Process) JsonDataParser.parseObjectOrArray(result, it.mapsgroup.segnaler.camunda.rest.client.vo.Process.class);
		
		return process;
	}
	
	//private static Task
	
	private static ProcessCustomVariables getProcessCustomVariables(String processId) {
		Map<String, String> vars = new HashMap<String, String>();
		String variables = restTemplate.getForObject("http://localhost:8080/engine-rest/process-instance/" + processId + "/variables", String.class, vars);
		ProcessCustomVariables customVariables = (ProcessCustomVariables)JsonDataParser.parseObjectOrArray(variables, ProcessCustomVariables.class);
		
		return customVariables;
	}
	
	private static UserProfile[] listUsers() {
		Map<String, String> vars = new HashMap<String, String>();
		String result = restTemplate.getForObject("http://localhost:8080/engine-rest/user", String.class, vars);
		
		//System.out.println(JsonFormatter.convertFlatJsonToFormattedJson(result));
		
		UserProfile[] users = (UserProfile[]) JsonDataParser.parseObjectOrArray(result, UserProfile[].class);
		for (UserProfile user : users) {
			System.out.println("User id = " + user.id + ", name + surname = " + user.firstName + " " + user.lastName);
		}
		
		return users;
	}

	private static void assignTaskToUser() throws Exception {
		String taskKey = readFromInputLine("Inserisci l'id del task: ");
		String userId = readFromInputLine("Inserisci l'id dell'utente: ");
		
		Task task = getTaskDetail(taskKey);
		
		assignTask(task, userId);
	}


	/*
	 * https://docs.camunda.org/manual/7.3/api-references/rest/#task-set-assignee
	 */
	private static void assignTask(Task task, String userId) {
		BasicUser user = new BasicUser();
		user.userId = userId;
		
		try {
			//task.businessId = "businessId = " + new Date();
			task.description = "Task " + task.id + " assigned to " + userId + " at " + new Date();
			//task.nomeSoggetto = "(Inserisci il nome del soggetto)";
			restTemplate.put("http://localhost:8080/engine-rest/task/" + task.id + "/", task);

			restTemplate.postForEntity("http://localhost:8080/engine-rest/task/" + task.id + "/assignee", user, null);
		} catch (Exception e) {
			System.err.println("Cannot assign task " + task.id + " to user " + userId + ": "+ e.getMessage());
		}
		
	}

	/*
	 * https://docs.camunda.org/manual/7.15/reference/rest/user/post-create/
	 */
	private static void createUser(String userId, String firstName, String lastName, String email) {
		User user = new User();
		UserProfile userProfile = new UserProfile();
		userProfile.id = userId;
		userProfile.firstName = firstName;
		userProfile.lastName = lastName;
		userProfile.email = email;
		user.profile = userProfile;
		UserCredentials userCredentials = new UserCredentials();
		userCredentials.password = userId; // same as username
		user.credentials = userCredentials;
		
		try {
			restTemplate.postForEntity("http://localhost:8080/engine-rest/user/create", user, null);
		} catch (Exception e) {
			System.err.println("Cannot create user " + userId + ": "+ e.getMessage());
		}
		
	}

	private static void _startProcess() throws Exception {
		String taskKey = readFromInputLine("Inserisci la key del processo: ");
		
		//startProcess(taskKey);
		
		System.out.println("Processo " + taskKey + " avviato");
	}

	private static void _startProcessA1() throws Exception {
		
		String bk = readFromInputLine("Valore della business key", "Business Key " + _random(10, 99));
//		String ns = readFromInputLine("Nome Soggetto", "Nome " + _random(100, 999));
//		String ts = readFromInputLine("Testo della segnalazione", "Non è andata bene la " + _random(1000, 9999));
		
		//startProcessA1(bk, ns, ts, true);
		startProcessA1(bk, new ASimpleBusinessClass("Diego", "Pascotto", "Via Ferrara 7"));
		
		System.out.println("Processo A1 avviato");
	}
	
	private static void _startProcessA2() throws Exception {
		
		String bk = readFromInputLine("Native Object ID", "NativeObjectID_ " + _random(10, 99));
		String nm = readFromInputLine("Nome", "Diego");
		String sn = readFromInputLine("Cognome", "Pascotto");
		String in = readFromInputLine("Indirizzo", "Via Senato, " + _random(10, 99) + ", 430" + _random(10, 99) + " Tarsogno (PR)");

		startProcessA2(bk, new ASimpleBusinessClass(nm, sn, in));
		
		System.out.println("Processo A2 avviato");
	}
	
	
	private static String _random(int low, int high) {
		Random r = new Random();
		int result = r.nextInt(high-low) + low;
		
		return "" + result;
	}

	/*
	 * https://docs.camunda.org/manual/7.3/api-references/rest/#process-definition-start-process-instance
	 * 
	 *  id	String	The id of the process instance.
		definitionId	String	The id of the process definition.
		businessKey	String	The business key of the process instance.
		caseInstanceId	String	The case instance id of the process instance.
		tenantId	String	The tenant id of the process instance.
		ended	Boolean	A flag indicating whether the instance is still running or not.
		suspended	Boolean	A flag indicating whether the instance is suspended or not.
		links	Object	A JSON array containing links to interact with the instance.
	 */
	private static void startProcess_OLD(String key, Task task) {
		//it.mapsgroup.segnaler.camunda.rest.client.vo.Process process = new it.mapsgroup.segnaler.camunda.rest.client.vo.Process();
		
		//ProcessRequest processRequest = new ProcessRequest();
		//processRequest.businessKey = task.businessKey;
		//processRequest.variables = JsonFormatter.to
		
		//ResponseEntity<it.mapsgroup.segnaler.camunda.rest.client.vo.Process> response = restTemplate.postForEntity("http://localhost:8080/engine-rest/process-definition/key/" + key + "/start", processRequest, null);
		
		//System.out.println("Process created: " + response);
	}

	@Deprecated
	private static void startProcessA1(String bizKey, String nomeSoggetto, String testoSegnalazione, boolean eUnaFigata) {
		try {
			String taskKey = "ProcessA1";
			ProcessA1Request a1 = new ProcessA1Request();
			
//			a1.businessKey = bizKey;
			
//			a1.variables.nomeSoggetto = CustomVariableValueAndType.asString(nomeSoggetto);
//			a1.variables.testoSegnalazione = CustomVariableValueAndType.asString(testoSegnalazione);
//			a1.variables.eUnaFigata = CustomVariableValueAndType.asBoolean(eUnaFigata);

			ResponseEntity<it.mapsgroup.segnaler.camunda.rest.client.vo.Process> response = restTemplate.postForEntity("http://localhost:8080/engine-rest/process-definition/key/" + taskKey + "/start", a1, null);
			System.out.println("ProcessA1 created: " + response);
		} catch (Exception e) {
			System.err.println("Impossibile fare partire il processo A1: " + e.getMessage());
		}
	}

	private static void startProcessA1(String bizKey, Object myReadOnlyParameters) {
		try {
			String taskKey = "ProcessA1";
			ProcessA1Request a1 = new ProcessA1Request();
			
//			a1.businessKey = bizKey;
			//a1.variables.businessInputAttributes = CustomVariableValueAndType.asString(ObjectToJson.toJson(myReadOnlyParameters));
			
			
			ResponseEntity<it.mapsgroup.segnaler.camunda.rest.client.vo.Process> response = restTemplate.postForEntity("http://localhost:8080/engine-rest/process-definition/key/" + taskKey + "/start", a1, null);
			System.out.println("ProcessA1 created: " + response);
		} catch (Exception e) {
			System.err.println("Impossibile fare partire il processo A1: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private static void startProcessA2(String nativeObjectId, Object myObject) {
		try {
			String processKey = "ProcessA2";
			ProcessA2Request a2 = new ProcessA2Request(nativeObjectId, "A2");
			
			a2.addBusinessInputVariables(myObject);
			
			
			ResponseEntity<it.mapsgroup.segnaler.camunda.rest.client.vo.Process> response = restTemplate.postForEntity("http://localhost:8080/engine-rest/process-definition/key/" + processKey + "/start", a2, null);
			System.out.println("ProcessA2 created: " + response);
		} catch (Exception e) {
			System.err.println("Impossibile fare partire il processo A2: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static Task[] listAllTasks() {
		Map<String, String> vars = new HashMap<String, String>();
		
		String result = restTemplate.getForObject("http://localhost:8080/engine-rest/task", String.class, vars);
		
		//System.out.println(JsonFormatter.convertFlatJsonToFormattedJson(result));
		
		return (Task[])JsonDataParser.parseObjectOrArray(result, Task[].class);
	}
	
	public static ProcessInstance[] listAllProcessInstances() {
		Map<String, String> vars = new HashMap<String, String>();
		
		String result = restTemplate.getForObject("http://localhost:8080/engine-rest/history/process-instance", String.class, vars);
		
		System.out.println(JsonFormatter.convertFlatJsonToFormattedJson(result));
		
		return (ProcessInstance[])JsonDataParser.parseObjectOrArray(result, ProcessInstance[].class);
	}
	
	/*
	 * https://docs.camunda.org/manual/7.3/api-references/rest/#task-update-a-task
	 */
	private static void updateTask() throws Exception {
		// Aggiorno i campi custom del task del processo A1 (nomeSoggetto, segnalazione)
		String taskId = readFromInputLine("Inserisci l'ID del task: ");
		
		Task task = getTaskById(taskId);
		task.description = "Task aggiornato via API Java il " + new Date();
		//task.nomeSoggetto = "Nome soggetto generato via API Java il " + new Date();
		
		restTemplate.postForEntity("http://localhost:8080/engine-rest/task/" + taskId + "/", task, null);
		
	}

	@Deprecated // fatto quello by ID
	private static Task getTaskById(String taskId) {
		Task[] tasks = listAllTasks();
		for (Task task : tasks) {
			if (task.id.equals(taskId)) {
				return task;
			}
		}
		throw new RuntimeException("Task id " + taskId + " does not exist");
	}

	public static void getUser() throws Exception {
		String username = readFromInputLine("Username? ");
		
		UserProfile user = _getUser(username);
		if (user == null) {
			System.err.println("L'utente non esiste, crearlo [Y/N]?");
			String choice = readFromInputLine("L'utente non esiste, crearlo? [Y/N] ");
			if (choice.equalsIgnoreCase("Y")) {
				_createUser(username, "_" + username, "_", null);
				user = _getUser(username);
			} else if (choice.equalsIgnoreCase("N")) {
				System.out.println("Utente " + username + " non creato");
				return;
			} else {
				throw new RuntimeException("Do the right thing");
			}
			
			return;
		}
		
		System.out.println(user.toString());
	}

	private static UserProfile _getUser(String username) {
		Map<String, String> vars = new HashMap<String, String>();
		String result = restTemplate.getForObject("http://localhost:8080/engine-rest/user?id=" + username, String.class, vars);
		
		UserProfile[] arrayWithOne = (UserProfile[]) JsonDataParser.parseObjectOrArray(result, UserProfile[].class);
		if (arrayWithOne == null || arrayWithOne.length == 0) {
			System.err.println("Utente " + username + " non esiste");
			return null;
		}
		if (arrayWithOne.length > 1) {
			throw new RuntimeException("Ci sono troppi utenti con lo username " + username + ", " + arrayWithOne.length);
		}
		
		UserProfile user = arrayWithOne[0];
		
		System.out.println("\r\n\r\n");
		System.out.println("User id = " + user.id + ", name + surname = " + user.firstName + " " + user.lastName);
		System.out.println("\r\n\r\n");
		
		return user;
	}

	public static void createUser() throws Exception {
		String username = readFromInputLine("Username: ", null);
		String firstName = readFromInputLine("First name: ", "?");
		String lastName = readFromInputLine("Last name: ", "?");
		String email = readFromInputLine("email address: ", "?");
		
		_createUser(username, firstName, lastName, email);
		
		System.out.println("User " + username + " created");
	}

	private static void _createUser(String username) {
		createUser(username, null, null, null);
	}

	private static void _createUser(String username, String firstName, String lastName, String email) {
		createUser(username, firstName, lastName, email);
	}


}



class ASimpleBusinessClass {
	
	public ASimpleBusinessClass(String name, String surname, String address) {
		super();
		this.name = name;
		this.surname = surname;
		this.address = address;
	}
	public String name;
	public String surname;
	public String address;
//	@Override
//	public String toString() {
//		return "ASimpleBusinessClass [name=" + name + ", surname=" + surname + ", address=" + address + "]";
//	}
	
	
}




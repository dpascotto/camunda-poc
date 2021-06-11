package it.mapsgroup.segnaler.camunda.rest.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import it.mapsgroup.segnaler.camunda.rest.client.vo.BasicUser;
import it.mapsgroup.segnaler.camunda.rest.client.vo.Group;
import it.mapsgroup.segnaler.camunda.rest.client.vo.Modification;
import it.mapsgroup.segnaler.camunda.rest.client.vo.Modifications;
import it.mapsgroup.segnaler.camunda.rest.client.vo.ProcessA1Request;
import it.mapsgroup.segnaler.camunda.rest.client.vo.ProcessA2Request;
import it.mapsgroup.segnaler.camunda.rest.client.vo.ProcessInstance;
import it.mapsgroup.segnaler.camunda.rest.client.vo.ProcessRequest;
import it.mapsgroup.segnaler.camunda.rest.client.vo.Task;
import it.mapsgroup.segnaler.camunda.rest.client.vo.TaskWithParentProcess;
import it.mapsgroup.segnaler.camunda.rest.client.vo.User;
import it.mapsgroup.segnaler.camunda.rest.client.vo.UserCredentials;
import it.mapsgroup.segnaler.camunda.rest.client.vo.UserProfile;
import it.mapsgroup.segnaler.camunda.rest.client.vo.variables.CustomVariable;
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

		/*
		boolean auto = false;
		if (auto) {
			// Faccio partire 2 istanze del processo ProcessA1
			startProcessA1("Business key: assegnato a Pino", "Pino", "Pino, fai andare le cose", true);
			startProcessA1("Business key: assegnato a Gino", "Gino", "Gino, controlla Pino", true);
			
			Task[] tasks = listTasks();
			
			UserProfile[] users = listUsers();
			
			createUserSoft(users, "level1", "Level", "One", "level1@my-mail.com");
			createUserSoft(users, "level2", "Level", "Two", "level2@my-mail.com");
			
			assignTask(tasks[0], "level1");
			assignTask(tasks[1], "level2");
		}
		*/
		
		
		/*
		 * Auto-2: creazione dei gruppi, degli utenti e assegnazione degli utenti ai gruppi
		 */
		boolean auto2 = true;
		if (auto2) {
			UserProfile[] users = listUsers();

			createUserSoft(users, "qui", "Qui", "Paolino", "qui@disney.com");
			createUserSoft(users, "quo", "Quo", "Paolino", "quo@disney.com");
			createUserSoft(users, "qua", "Qua", "Paolino", "qua@disney.com");
			createUserSoft(users, "paperino", "Paperino", "Paolino", "paperino@disney.com");
			createUserSoft(users, "paperina", "Paperina", "Paperinella", "paperina@disney.com");
			createUserSoft(users, "paperone", "Paperon", "De' Paperoni", "paperone@disney.com");
			
			createGroup("A1L1", "Processo A1, Livello 1");
			createGroup("A1L2", "Processo A1, Livello 2");
			createGroup("A1L3", "Processo A1, Livello 3");
			
			addUserToGroup("qui", "A1L1");
			addUserToGroup("quo", "A1L1");
			addUserToGroup("qua", "A1L1");
			addUserToGroup("paperino", "A1L2");
			addUserToGroup("paperina", "A1L2");
			addUserToGroup("paperone", "A1L3");

		}
		
		/*
		 * Auto-3: flusso del processo A1
		 */
		boolean auto3 = true;
		if (auto3) {
			//startProcessA1(null, args);
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

	private static void createUserSoft(UserProfile[] users, String username, String name, String surname, String email) {
		if (!_contains(users, username)) {
			createUser(username, name, surname, email);
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
	private static Task[] listTasks() throws Exception {
		Task[] tasks = _listAllTasks();
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
		return getTaskDetail(taskId, null);
	}
	
	private static TaskWithParentProcess getTaskDetail(String taskId, String candidateUserId) {
		try {
			String taskJson = null;
			if (candidateUserId == null) {
				System.out.println("Cerco il task avente id = " + taskId);
				taskJson = restTemplate.getForObject("http://localhost:8080/engine-rest/task?taskId=" + taskId, String.class);
			} else {
				System.out.println("Cerco il task avente id = " + taskId + " e verifico che sia assegnato/assegnabile  a " + candidateUserId);
				taskJson = restTemplate.getForObject("http://localhost:8080/engine-rest/task?taskId=" + taskId + "&candidateUser=" + candidateUserId, String.class);
			}
			
			TaskWithParentProcess[] tasks = (TaskWithParentProcess[]) JsonDataParser.parseObjectOrArray(taskJson, TaskWithParentProcess[].class);
			
			if (tasks == null || tasks.length == 0) {
				throw new RuntimeException("Il task " + taskId + " non esiste oppure non è assegnabile a " + candidateUserId);
			}

			if (tasks.length > 1) {
				throw new RuntimeException("Il task " + taskId + " ha trovato troppi task (dai, impossibile)");
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

	@Deprecated
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
	
	private static void deleteActiveProcessInstances() throws Exception {
		ProcessInstance[] pis = listAllActiveProcessInstances();
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
		System.out.println("1 ..... Elenco dei processi ATTIVI (tutte le versioni) ");
		System.out.println("2.1 ... Fai partire un'istanza del processo ProcessA1");
		System.out.println("2.2 ... Fai partire un'istanza del processo ProcessA2");
		System.out.println("3 ..... Elenco dei task                                          ");       
		System.out.println("3a .... Dettaglio di un task                                                       ");       
		System.out.println("4 ..... Elenco degli utenti");
		System.out.println("4a .... Dettaglio utente");
		System.out.println("4b .... Crea utente");
		System.out.println("5 ..... Assegna task a un utente");
		System.out.println("6 ..... Completa task e aggiorna processo");
		//System.out.println("7 ..... Cancella task(s)");
		System.out.println("7 ..... Cancella tutte le istanze di processo attive");
		System.out.println("8.1 ... Crea / aggiorna un gruppo");
		System.out.println("8.2 ... Cancella un gruppo");
		System.out.println("9.1 ... Assegna utente a un gruppo");
		System.out.println("9.2 ... Rimuovi utente da un gruppo");
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
			_createUser();
		} else if (choice.equals("5")) {
			_assignTaskToUser();
		} else if (choice.equals("6")) {
			//updateTask();
			//updateProcess();
			completeTask();
//		} else if (choice.equals("7")) {
//			deleteTask();
		} else if (choice.equals("7")) {
			deleteActiveProcessInstances();
		} else if (choice.equals("8.1")) {
			_createOrUpdateAGroup();
		} else if (choice.equals("8.2")) {
			_deleteAGroup();
		} else if (choice.equals("9.1")) {
			_addUserToAGroup();
		} else if (choice.equals("9.2")) {
			_removeUserFromAGroup();
		} else {
			System.err.println("Valore " + choice + " ignoto o non implementato");
		}
		
		return true;
	}
		
	@Deprecated
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
		// Aggiungo il dettaglio con una chiamata aggiuntiva:
		process.variables = getProcessCustomVariables(process.id);

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

	private static void _assignTaskToUser() throws Exception {
		String taskKey = readFromInputLine("Inserisci l'id del task: ", null);
		String userId = readFromInputLine("Inserisci l'id dell'utente: ", null);
		
		if (taskKey == null) throw new RuntimeException("ID task obbligatorio");
		if (userId == null) throw new RuntimeException("Username obbligatoria");
		
		Task task = getTaskDetail(taskKey, userId);
		
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
	private static void createUserWithDefualtCredentials(String userId, String firstName, String lastName, String email) {
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
		
		String bk = readFromInputLine("Native Object ID", "NativeObjectID_ " + _random(10, 99));
		String nm = readFromInputLine("Nome", "Diego");
		String sn = readFromInputLine("Cognome", "Pascotto");
		String in = readFromInputLine("Indirizzo", "Via Ferrara, " + _random(1, 9) + ", 431" + _random(10, 99) + " Parma");
		
		startProcessA1(bk, new ASimpleBusinessClass(nm, sn, in));
		
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
	private static void startProcessA1_OLD(String bizKey, String nomeSoggetto, String testoSegnalazione, boolean eUnaFigata) {
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

	private static void startProcessA1_OLD(String bizKey, Object myReadOnlyParameters) {
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
	
	private static void startProcessA1(String nativeObjectId, Object myObject) {
		try {
			String processKey = "ProcessA1";
			ProcessRequest a1 = new ProcessRequest(nativeObjectId, "A1");
			
			a1.addBusinessInputVariables(myObject);
			
			
			ResponseEntity<it.mapsgroup.segnaler.camunda.rest.client.vo.Process> response = restTemplate.postForEntity("http://localhost:8080/engine-rest/process-definition/key/" + processKey + "/start", a1, null);
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
	
	public static Task[] _listAllTasks() throws Exception {
		String userId = readFromInputLine("Inserisci lo username candidato (vuoto per vedere tutti i task): ", "");
		
		if (userId != null && !userId.equals("")) {
			return listAllTasksForUser(userId);			
		} else {
			return listAllTasks();
		}
	}

	public static Task[] listAllTasks() {
		Map<String, String> vars = new HashMap<String, String>();
		
		String result = restTemplate.getForObject("http://localhost:8080/engine-rest/task", String.class, vars);
		
		//System.out.println(JsonFormatter.convertFlatJsonToFormattedJson(result));
		
		return (Task[])JsonDataParser.parseObjectOrArray(result, Task[].class);
	}
	
	public static Task[] listAllTasksForUser(String user) {
		Map<String, String> vars = new HashMap<String, String>();
		
		String result = restTemplate.getForObject("http://localhost:8080/engine-rest/task?candidateUser=" + user, String.class, vars);
		
		//System.out.println(JsonFormatter.convertFlatJsonToFormattedJson(result));
		
		return (Task[])JsonDataParser.parseObjectOrArray(result, Task[].class);
	}
	
	public static ProcessInstance[] listAllActiveProcessInstances() {
		Map<String, String> vars = new HashMap<String, String>();
		
		String result = restTemplate.getForObject("http://localhost:8080/engine-rest/history/process-instance?active=true", String.class, vars);
		
		System.out.println(JsonFormatter.convertFlatJsonToFormattedJson(result));
		
		return (ProcessInstance[])JsonDataParser.parseObjectOrArray(result, ProcessInstance[].class);
	}
	
	/*
	 * https://docs.camunda.org/manual/7.3/api-references/rest/#task-update-a-task
	 */
	@Deprecated
	private static void updateTask() throws Exception {
		// Aggiorno i campi custom del task del processo A1 (nomeSoggetto, segnalazione)
		String taskId = readFromInputLine("Inserisci l'ID del task: ");
		
		Task task = getTaskById(taskId);
		task.description = "Task aggiornato via API Java il " + new Date();
		//task.nomeSoggetto = "Nome soggetto generato via API Java il " + new Date();
		
		restTemplate.postForEntity("http://localhost:8080/engine-rest/task/" + taskId + "/", task, null);
		
	}
	
	/*
	 * Si modificano solo i businessInputAttributes
	 * E' modificata solo la property di nome businessInputAttributes del processo
	 * (la visualizzazione del task dall'interfaccia nativa di Camunda fa vedere il valore sul task, non modificato)
	 */
	@Deprecated // tutto collassato in submit task e update process
	private static void updateProcess() throws Exception {
		String processId = readFromInputLine("Inserisci il Process ID: ");
		it.mapsgroup.segnaler.camunda.rest.client.vo.Process process = getProcessById(processId);

		ProcessCustomVariables variables = process.variables;
		System.out.println("Variabili del processo:\r\n" + variables);
		System.out.println("Variabili del processo - Business Input Attributes:\r\n" + variables.businessInputAttributes.toString());
		
		String biaMod = readFromInputLine("Inserisci il nuovo valore di businessInputAttributes:\r\n");
		Modification modification = new Modification();
		modification.modifications = new Modifications();
		modification.modifications.businessInputAttributes = CustomVariable.asString(biaMod);
		
		restTemplate.postForEntity("http://localhost:8080/engine-rest/process-instance/" + processId + "/variables", modification, null);
		System.out.println("Update effettuato");
		
		// Per sicurezza faccio la get del processo e la stampo
		
		it.mapsgroup.segnaler.camunda.rest.client.vo.Process updatedProcess = getProcessById(processId);
		System.out.println("Processo aggiornato: " + updatedProcess.toString());
	}
	
	private static void completeTask() throws Exception {
		/*
		 * Per prima cosa estraggo il process a cui appartiene
		 */
		String taskId = readFromInputLine("Inserisci il task ID: ");
		TaskWithParentProcess task = getTaskDetail(taskId);
		it.mapsgroup.segnaler.camunda.rest.client.vo.Process process = task.parentProcess;
		
		/*
		 * Sottometto il task SENZA cambiare le property
		 */
		restTemplate.postForEntity("http://localhost:8080/engine-rest/task/" + taskId + "/submit-form", task, null);
		
		/*
		 * Visualizzo le variabili correnti del processo
		 */
		ProcessCustomVariables variables = process.variables;
		System.out.println("Variabili del processo:\r\n" + variables);
		System.out.println("Variabili del processo - Business Input Attributes:\r\n" + variables.businessInputAttributes.toString());
		
		/*
		 * Chiedo le nuove variabili (solo la parte custom: businessInputAttributes)
		 */
		String biaMod = readFromInputLine("Inserisci il nuovo valore di businessInputAttributes:\r\n");
		Modification modification = new Modification();
		modification.modifications = new Modifications();
		modification.modifications.businessInputAttributes = CustomVariable.asString(biaMod);
		
		/*
		 * Aggiorno il process in modo che le variabili modificate
		 */
		restTemplate.postForEntity("http://localhost:8080/engine-rest/process-instance/" + process.id + "/variables", modification, null);
		System.out.println("Update effettuato");
		
		// Per sicurezza faccio la get del processo e la stampo		
		it.mapsgroup.segnaler.camunda.rest.client.vo.Process updatedProcess = getProcessById(process.id);
		System.out.println("Processo aggiornato: " + updatedProcess.toString());
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
				createUser(username, "_" + username, "_", null);
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

	public static void _createUser() throws Exception {
		String username = readFromInputLine("Username: ", null);
		String firstName = readFromInputLine("First name: ", "?");
		String lastName = readFromInputLine("Last name: ", "?");
		String email = readFromInputLine("email address: ", "?");
		
		createUser(username, firstName, lastName, email);
		
		System.out.println("User " + username + " created");
	}

	private static void createUser(String username) {
		createUserWithDefualtCredentials(username, null, null, null);
	}

	private static void createUser(String username, String firstName, String lastName, String email) {
		createUserWithDefualtCredentials(username, firstName, lastName, email);
	}

	private static void _createOrUpdateAGroup() throws Exception{
		String id = readFromInputLine("Id del gruppo: ", null);
		String na = readFromInputLine("Nome del gruppo: ", null);
		
		createOrUpdateGroup(id, na);
		
	}

	private static void createOrUpdateGroup(String id, String na) {
		Group group = getGroupById(id);
		if (group == null) {
			System.out.println("Il gruppo " + id + " non esiste, lo creo");
			createGroup(id, na);
		} else {
			System.out.println("Il gruppo " + id + " esiste già, lo aggiorno");
			updateGroup(id, na);
		}
		
	}

	private static void createGroup(String id, String na) {
		Group newGroup = new Group();
		newGroup.id = id;
		newGroup.name = na;
		newGroup.type = "Created via API";
		
		try {
			restTemplate.postForEntity("http://localhost:8080/engine-rest/group/create", newGroup, null);
		} catch (Exception e) {
			System.err.println("Il gruppo " + id + " probabilmente già esiste");
		}
		
		System.out.println("Gruppo " + id + " creato");
	}

	private static Group getGroupById(String id) {
		Map<String, String> vars = new HashMap<String, String>();
		String result;
		try {
			result = restTemplate.getForObject("http://localhost:8080/engine-rest/group/" + id, String.class, vars);
			Group group = (Group) JsonDataParser.parseObjectOrArray(result, Group.class);
			return group;
		} catch (HttpClientErrorException.NotFound e) {
			System.err.println("Il gruppo " + id + " non esiste");
		}
		
		return null;
	}

	private static void _deleteAGroup() throws Exception{
		String id = readFromInputLine("Id del gruppo che vuoi cancellare: ", null);
		
		deleteGroup(id);
		
	}

	private static void deleteGroup(String id) {
		restTemplate.delete("http://localhost:8080/engine-rest/group/" + id);
		
		System.out.println("Gruppo " + id + " cancellato");
	}
	
	private static void updateGroup(String id, String na) {
		Group groupToBeUpdated = getGroupById(id);
		groupToBeUpdated.name = na;
		
		restTemplate.put("http://localhost:8080/engine-rest/group/" + id, groupToBeUpdated);
		
		System.out.println("Gruppo " + id + " aggiornato");
	}

	private static void _addUserToAGroup() throws Exception {
		String idp = readFromInputLine("Username della persona: ", null);
		String idg = readFromInputLine("Id del gruppo: ", null);
		
		addUserToGroup(idp, idg);
		
	}

	private static void addUserToGroup(String username, String groupId) {
		Group g = getGroupById(groupId);
		if (g == null) {
			System.err.println("Il gruppo " + groupId + " non esiste");
			return;
		}
		
		try {
			restTemplate.put("http://localhost:8080/engine-rest/group/" + groupId + "/members/" + username, null);
			System.out.println("Gruppo " + groupId + ", assegnato utente " + username);
		} catch (RestClientException e) {
			System.err.println("Il gruppo " + groupId + " probabilmente ha già l'utente " + username);
		}
	}

	private static void _removeUserFromAGroup() throws Exception {
		String idp = readFromInputLine("Username della persona: ", null);
		String idg = readFromInputLine("Id del gruppo: ", null);
		
		removeUserFromGroup(idp, idg);
		
	}

	private static void removeUserFromGroup(String username, String groupId) {
		Group g = getGroupById(groupId);
		if (g == null) {
			System.err.println("Il gruppo " + groupId + " non esiste");
			return;
		}
		
		try {
			restTemplate.delete("http://localhost:8080/engine-rest/group/" + groupId + "/members/" + username);
			System.out.println("Gruppo " + groupId + ", rimosso utente " + username);
		} catch (RestClientException e) {
			System.err.println("Il gruppo " + groupId + " probabilmente non aveva l'utente " + username);
		}
		
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




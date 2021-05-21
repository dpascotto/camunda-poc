package it.mapsgroup.segnaler.camunda.rest.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import it.mapsgroup.segnaler.camunda.rest.client.vo.BasicUser;
import it.mapsgroup.segnaler.camunda.rest.client.vo.Task;
import it.mapsgroup.segnaler.camunda.rest.client.vo.User;
import it.mapsgroup.segnaler.camunda.rest.client.vo.UserCredentials;
import it.mapsgroup.segnaler.camunda.rest.client.vo.UserProfile;
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
		
		// Faccio partire 2 istanze del processo ProcessA1
		startProcess("ProcessA1");
		startProcess("ProcessA1");
		
		Task[] tasks = listTasks();
		
		createUser("level1", "Level", "One", "level1@my-mail.com");
		createUser("level2", "Level", "Two", "level2@my-mail.com");
		
		assignTask(tasks[0], "level1");
		assignTask(tasks[1], "level2");
		
		boolean stayIn = true;
		while (stayIn) {
			try {
				stayIn = askForUserAction();
			} catch (Exception e) {
				System.err.println("Problemi a eseguire un task: " + e.getMessage());
			}
		}

	}

	private static Task[] listTasks() {
		Task[] tasks = listAllTasks();
		for (Task task : tasks) {
			System.out.println(task.id + " - " + task.name + " (process " + task.processDefinitionId + ") - " + (task.assignee != null ? "Assegnato a: " + task.assignee : "NON ASSEGNATO") + 
					" - Nome Soggetto = " + task.nomeSoggetto);
		}
		return tasks;
	}
	
	public static boolean askForUserAction() throws Exception {
		System.out.println();
		System.out.println();
		System.out.println("============= Possibili opzioni =============");
		System.out.println("1 ..... Elenco dei processi");
		System.out.println("2 ..... Fai partire un'istanza (task) del processo");
		System.out.println("3 ..... Elenco dei task");
		System.out.println("4 ..... Elenco degli utenti");
		System.out.println("5 ..... Assegna task a un utente");
		System.out.println("6 ..... Aggiorna task");
		System.out.println("=============================================");
		System.out.println();
		System.out.println();
		
		String choice = readFromInputLine("Digita il codice dell'azione richiesta ('q' per uscire-quit): ");

		if (choice.equalsIgnoreCase("q")) {
			return false;
		}
		
		if (choice.equals("1")) {
			listProcesses();
		} else if (choice.equals("2")) {
			_startProcess();
		} else if (choice.equals("3")) {
			listTasks();
		} else if (choice.equals("4")) {
			listUsers();
		} else if (choice.equals("5")) {
			assignTaskToUser();
		} else if (choice.equals("6")) {
			updateTask();
		} else {
			System.err.println("Valore " + choice + " ignoto o non implementato");
		}
		
		return true;
	}
		
	private static String readFromInputLine(String hint) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

		System.out.print(hint);
		String value = reader.readLine();

		return value;
	}
	
	private static void listProcesses() {
		Map<String, String> vars = new HashMap<String, String>();
		String result = restTemplate.getForObject("http://localhost:8080/engine-rest/process-definition", String.class, vars);
		
		//System.out.println(JsonFormatter.convertFlatJsonToFormattedJson(result));
		
		it.mapsgroup.segnaler.camunda.rest.client.vo.Process[] processes = (it.mapsgroup.segnaler.camunda.rest.client.vo.Process[]) JsonDataParser.parseArray(result, it.mapsgroup.segnaler.camunda.rest.client.vo.Process[].class);
		for (it.mapsgroup.segnaler.camunda.rest.client.vo.Process process : processes) {
			System.out.println("Process id = " + process.id + ", key = " + process.key);
		}
	}
	
	private static void listUsers() {
		Map<String, String> vars = new HashMap<String, String>();
		String result = restTemplate.getForObject("http://localhost:8080/engine-rest/user", String.class, vars);
		
		System.out.println(JsonFormatter.convertFlatJsonToFormattedJson(result));
		
		UserProfile[] users = (UserProfile[]) JsonDataParser.parseArray(result, UserProfile[].class);
		for (UserProfile user : users) {
			System.out.println("User id = " + user.id + ", name + surname = " + user.firstName + " " + user.lastName);
		}
	}

	private static void assignTaskToUser() throws Exception {
		String taskKey = readFromInputLine("Inserisci l'id del task: ");
		String userId = readFromInputLine("Inserisci l'id dell'utente: ");
		
		Task task = new Task();
		task.id = taskKey;
		
		assignTask(task, userId);
	}


	/*
	 * https://docs.camunda.org/manual/7.3/api-references/rest/#task-set-assignee
	 */
	private static void assignTask(Task task, String userId) {
		BasicUser user = new BasicUser();
		user.userId = userId;
		
		try {
			task.description = "Task " + task.id + " assigned to " + userId + " at " + new Date();
			task.nomeSoggetto = "(Inserisci il nome del soggetto)";
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
		
		startProcess(taskKey);
		
		System.out.println("Processo " + taskKey + " avviato");
	}

	/*
	 * https://docs.camunda.org/manual/7.3/api-references/rest/#process-definition-start-process-instance
	 */
	private static void startProcess(String key) {
		it.mapsgroup.segnaler.camunda.rest.client.vo.Process process = new it.mapsgroup.segnaler.camunda.rest.client.vo.Process();
		
		ResponseEntity<it.mapsgroup.segnaler.camunda.rest.client.vo.Process> response = restTemplate.postForEntity("http://localhost:8080/engine-rest/process-definition/key/" + key + "/start", process, null);
	}


	public static Task[] listAllTasks() {
		Map<String, String> vars = new HashMap<String, String>();
		
		String result = restTemplate.getForObject("http://localhost:8080/engine-rest/task", String.class, vars);
		
		System.out.println(JsonFormatter.convertFlatJsonToFormattedJson(result));
		
		return (Task[])JsonDataParser.parseArray(result, Task[].class);
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

	private static Task getTaskById(String taskId) {
		Task[] tasks = listAllTasks();
		for (Task task : tasks) {
			if (task.id.equals(taskId)) {
				return task;
			}
		}
		throw new RuntimeException("Task id " + taskId + " does not exist");
	}



}

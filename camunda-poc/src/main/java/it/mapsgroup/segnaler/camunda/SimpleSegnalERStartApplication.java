package it.mapsgroup.segnaler.camunda;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.camunda.bpm.spring.boot.starter.event.PostDeployEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.EventListener;

import it.mapsgroup.segnaler.camunda.rest.client.SegnalERRestClient;

@SpringBootApplication
@EnableProcessApplication
public class SimpleSegnalERStartApplication {
	
//	@Bean
//	public TaskDelegate taskDelegate() {
//		return new TaskDelegate();
//	}

	@Autowired
	private RuntimeService runtimeService;
	
	@EventListener
	private void processPostDeploy(PostDeployEvent event) {
//		startProcess("loan-request-receival"); // User Task
//		startProcess("payment-retrieval"); // Java Task
		// Faccio partire due processi dello stesso tipo
//		startProcess("ProcessA1"); 
//		startProcess("ProcessA1"); 
		// Se tutto è commentato OK, faccio partire i processi da SegnalERRestClient
		
	}

	private void startProcess(String processKey) {
		System.out.println("[SegnalERStartApplication::startProcess] Starting process: " + processKey + "...");

		runtimeService.startProcessInstanceByKey(processKey);
	}	


	public static void main(String[] args) {
		System.out.println("[SegnalERStartApplication::main] Starting Camunda application...");
		
		SpringApplication.run(SimpleSegnalERStartApplication.class, args);
		
		// Esegue la app client
		System.out.println("[SegnalERStartApplication::main] Starting client application...");
		SegnalERRestClient.main(null);

	}
}

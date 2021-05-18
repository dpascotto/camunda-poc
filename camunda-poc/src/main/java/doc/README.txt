 - https://docs.camunda.org/get-started/spring-boot/project-setup/
 	- esempio basic
 - https://docs.camunda.org/get-started/spring-boot/configuration/
    - Avanzamento del caso precedente ma con user / password / nome cablati in application.yaml (demo/demo)
 - https://docs.camunda.org/get-started/spring-boot/model/
 	- Creare in /src/main/resources/ il file processes.xml (può essere vuoto)
 	- Aggiornare il pom.xml esistente (camunda-external-task-client, e jaxb-api)
 	- Aprire Camunda Modeler (applicazione che permette di disegnare i flussi BPMN)
 	- Fare un Camunda model basico e salvarlo in /resources/processes (demoUserTask.bpmn) - NB: 
 	  il task deve essere uno user task (selezione con la chiave inglese, icona della persona)
 	- Configurare il task usando il Camunda Modeler: 
 	 	- Selezionare il task (lo scatolotto rettangolare smussato)
 		- cambiare l'ID in CheckTheRequestId
  	- Configurare le proprietà per l'esecuzione:
 		- Selezionare tutto il processo (perdere la selezione sul task cliccando sul bianco)
 		- rinominare l'ID del processo (loan-request-receival)
 		- name = Loan Request Receival
 		- NB: settare Executable = true
 	- Registrare il task nella start application:
 		- Nella start class:
 			- Mettere la annotation @EnableProcessApplication
 			- Aggiungere un attributo di tipo RuntimeService (@Autowired)
 			- Aggiungere un metodo taggato come @EventListener in cui viene invocato:
 			  	runtimeService.startProcessInstanceByKey("loan-request-receival")
 			  (viene fatta partire un'istanza del task)
 	- Eseguire il run, puntare a http://localhost:8080/, eseguire il login (demo/demo). Deve essere visibile un 
 	  task di nome Loan Request Receival. Per poterlo eseguire cliccare su 'Claim'. Se si clicca su 
 	  'Complete' il task risulta eseguito e sparisce dalla lista.
 - https://docs.camunda.org/get-started/java-process-app/service-task/
    - Variazione dell'esempio precedente con esecuzione di una classe Java
    	- riferimento: demoJavaTask.bpmn
    	- Task manuale + task di servizio associato alla classe Java 
    	    it.mapsgroup.segnaler.camunda.demo.javaclass.ProcessRequestDelegate
     	- Nella class Java dichiarata implementazione dell'interfaccia JavaDelegate, necessario
     	  implementare il metodo execute
 		
 		
 	
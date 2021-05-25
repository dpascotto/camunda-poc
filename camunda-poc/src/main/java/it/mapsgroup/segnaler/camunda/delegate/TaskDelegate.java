package it.mapsgroup.segnaler.camunda.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
public class TaskDelegate implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		
		
		Object businessId = execution.getVariable("businessId");
		Object nomeSoggetto = execution.getVariable("nomeSoggetto");
		Object testoSegnalazione = execution.getVariable("testoSegnalazione");
		
		execution.setVariable("testoSegnalazione", "Aggiornato il task businessId = " + businessId + ", nome soggetto: " + nomeSoggetto + ", testo segnalazione: " + testoSegnalazione);
		
	}

}

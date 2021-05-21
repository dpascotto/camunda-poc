package it.mapsgroup.segnaler.camunda.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
public class TaskDelegate implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		
		
		Object businessId = execution.getVariable("businessId");
		
		execution.setVariable("testoSegnalazione", "Aggiornato il task businessId = " + businessId);
		
	}

}

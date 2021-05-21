package it.mapsgroup.segnaler.camunda.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.mapsgroup.segnaler.camunda.service.IdGeneratorService;

/*
 * https://forum.camunda.org/t/autowired-not-working-in-javadelegate/4999/7
 * 
 * E' stato necessario mettere la annotation @Component altrimenti idGeneratorService
 * risultava null
 */
@Component
public class IdGeneratorDelegate implements JavaDelegate {
	
	@Autowired
	private IdGeneratorService idGeneratorService;

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		String id = idGeneratorService.generateId();
		
		System.out.println("ID generato: " + id);
		
		execution.setVariable("businessId", id);
		
	}

}

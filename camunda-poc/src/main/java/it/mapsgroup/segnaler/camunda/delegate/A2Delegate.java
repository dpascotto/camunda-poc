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
public class A2Delegate implements JavaDelegate {
	
	@Autowired
	private IdGeneratorService idGeneratorService;

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		String id = null;
		
		String businessKey = execution.getBusinessKey();
		if (businessKey != null && !businessKey.equals("") ) {
			System.out.println("Business key già assegnata: " + businessKey);
			id = businessKey;
		} else {
			id = idGeneratorService.generateId();
			System.out.println("Business key / ID generato: " + id);
		}
		execution.setVariable("businessId", id);
		execution.setVariable("peppino", "dicapri");
		
		Object variables = execution.getVariable("variables");
		System.out.println("variables: " + variables);
		
//		String businessInputAttributesJson = (String)execution.getVariable("businessInputAttributesJson");
//
//		System.out.println("businessInputAttributesJson: " + businessInputAttributesJson);
	}

}

// https://docs.camunda.org/get-started/java-process-app/service-task/

package it.mapsgroup.segnaler.camunda.demo.javaclass;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class ProcessRequestDelegate implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		System.out.println("[ProcessRequestDelegate::execute] Executing ProcessRequestDelegate...");
		
	}

}

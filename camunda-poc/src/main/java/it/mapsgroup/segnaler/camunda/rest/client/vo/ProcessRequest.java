package it.mapsgroup.segnaler.camunda.rest.client.vo;

import it.mapsgroup.segnaler.camunda.service.CamundaVariable;
import it.mapsgroup.segnaler.camunda.rest.client.vo.variables.ProcessCustomVariables;

/*
 * https://docs.camunda.org/manual/7.15/reference/rest/process-definition/post-start-process-instance/
 */
public abstract class ProcessRequest extends GenericVo {
	public String businessKey;
	
	//@CamundaVariable(camundaName = "processType", getExpression = "getCiccio")
	//public String processType = "?";
	
	//public CustomProcessAttributes variables = new CustomProcessAttributes();
	
	public ProcessCustomVariables variables = new ProcessCustomVariables();

}

package it.mapsgroup.segnaler.camunda.rest.client.vo;

import it.mapsgroup.segnaler.camunda.rest.client.vo.variables.CustomVariable;
import it.mapsgroup.segnaler.camunda.rest.client.vo.variables.ProcessCustomVariables;
import it.mapsgroup.segnaler.camunda.util.ObjectToJson;

// TODO mettere nel commento se questa serve solo all'INSTANZIAMENTO del Process (a differenza della classe Process
// che dovrebbe corrispondere al solo output)

/*
 * https://docs.camunda.org/manual/7.15/reference/rest/process-definition/post-start-process-instance/
 */
public class ProcessRequest extends GenericRequest {
	/*
	 * From https://docs.camunda.org/manual/7.15/reference/rest/process-definition/post-start-process-instance/
	 * :
	 * The business key the process instance is to be initialized with. 
	 * The business key uniquely identifies the process instance in the context of the given process definition.
	 */
	private String businessKey;
		
	private ProcessCustomVariables variables = new ProcessCustomVariables();

	
	public ProcessRequest() {
		super();
	}
		
	public ProcessRequest(String businessKey, String processType) {
		super();
		this.businessKey = businessKey;
		this.variables.nativeObjectId = CustomVariable.asString(businessKey);
		this.variables.processType = CustomVariable.asString(processType);
	}
	
	public void addBusinessInputVariables(Object businessInputAttributes) {
		this.variables.businessInputAttributes = CustomVariable.asString(ObjectToJson.toJson(businessInputAttributes));

	}
	
	// Getters
	public String getBusinessKey() {
		return businessKey;
	}
	
	public ProcessCustomVariables getVariables() {
		return variables;
	}


	

}

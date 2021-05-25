package it.mapsgroup.segnaler.camunda.rest.client.vo;

public class Process {
	public String id;
	
	@Deprecated
	public String key;
	
	public String definitionId;	//String	The id of the process definition.
	public String businessKey;	//	String	The business key of the process instance.
	public String caseInstanceId;	//	String	The case instance id of the process instance.
	public String tenantId;	//	String	The tenant id of the process instance.
	public boolean ended;	//	Boolean	A flag indicating whether the instance is still running or not.
	public boolean suspended;	//	Boolean	A flag indicating whether the instance is suspended or not.
	public Object links;	//	Object	A JSON array containing links to interact with the instance.
	
	@Override
	public String toString() {
		return "Process [id=" + id + ", key=" + key + ", definitionId=" + definitionId + ", businessKey=" + businessKey
				+ ", caseInstanceId=" + caseInstanceId + ", tenantId=" + tenantId + ", ended=" + ended + ", suspended="
				+ suspended + ", links=" + links + "]";
	}
	
	

}

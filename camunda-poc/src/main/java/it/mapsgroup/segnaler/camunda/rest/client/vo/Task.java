package it.mapsgroup.segnaler.camunda.rest.client.vo;

import it.mapsgroup.segnaler.camunda.util.ObjectToJson;

public class Task extends GenericVo {
	public String id;
	public String name;
	public String assignee;
	public String description;
	public String processDefinitionId;
	
	// Campi custom applicativo
	public CustomTaskAttributes variables = new CustomTaskAttributes();
	
	
	
	public static void main(String[] args) {
		
		
		Task task = new Task();
		task.description = "descizione-del-task";
		
		task.variables.nomeSoggetto = CustomVariableValueAndType.asString("proverbi-famosi-storpiati");
		task.variables.testoSegnalazione = CustomVariableValueAndType.asString("Tanto va la gatta al largo che la recupera il bagnino");
		
		
		String json = ObjectToJson.toJson(task);
		System.out.println(json);

	}

}

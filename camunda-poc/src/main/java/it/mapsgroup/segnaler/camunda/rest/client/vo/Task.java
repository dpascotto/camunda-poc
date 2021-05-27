package it.mapsgroup.segnaler.camunda.rest.client.vo;

import it.mapsgroup.segnaler.camunda.util.ObjectToJson;

/*
 * https://docs.camunda.org/manual/7.15/reference/rest/task/get-query/
 */
public class Task extends GenericVo {
	// act_ru_task.id_
	public String id;
	
	// act_ru_task.execution_id_
	// The id of the execution the task belongs to.
	public String executionId;
	
	// act_ru_task.proc_inst_id
	public String processInstanceId;
	
	// act_ru_task.proc_def_id
	public String processDefinitionId;
	
	// act_ru_task.name_
	// Nome (opzionale) del task (ex: Inserisci Dati Segnalazione)
	public String name;
	
	// act_ru_task.description_
	public String description;
	
	// act_ru_task.task_def_key_
	// Nome esteso del task (obbligatorio), ex: InserisciDatiSegnalazione (è una PK)
	public String taskDefinitionKey;
	
	// act_ru_task.assignee_
	// Username dell'assegnatario (ex: level1)
	public String assignee;
	
	// act_ru_task.create_time_
	public String created;
	
	
	
	// Campi custom applicativo
	@Deprecated // ha senso solo su Process
	public CustomTaskAttributes variables = new CustomTaskAttributes();

	@Override
	public String toString() {
		return "Task [id=" + id + ", executionId=" + executionId + ", processInstanceId=" + processInstanceId
				+ ", processDefinitionId=" + processDefinitionId + ", name=" + name + ", description=" + description
				+ ", taskDefinitionKey=" + taskDefinitionKey + ", assignee=" + assignee + ", created=" + created
				+ ", variables=" + variables + "]";
	}











	public static void main(String[] args) {
		
		
		Task task = new Task();
		task.description = "descizione-del-task";
		
		task.variables.nomeSoggetto = CustomVariableValueAndType.asString("proverbi-famosi-storpiati");
		task.variables.testoSegnalazione = CustomVariableValueAndType.asString("Tanto va la gatta al largo che la recupera il bagnino");
		
		
		String json = ObjectToJson.toJson(task);
		System.out.println(json);

	}

}

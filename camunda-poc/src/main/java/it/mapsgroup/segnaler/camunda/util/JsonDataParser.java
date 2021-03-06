package it.mapsgroup.segnaler.camunda.util;

import com.google.gson.Gson;

import it.mapsgroup.segnaler.camunda.rest.client.vo.Task;
import it.mapsgroup.segnaler.camunda.rest.client.vo.variables.CustomVariable;
import it.mapsgroup.segnaler.camunda.rest.client.vo.variables.ProcessCustomVariables;


public class JsonDataParser {
	
	private static final String arrayOfTasks = 
					  "[ {\r\n"
					+ "  \"id\" : \"eada1e6c-b492-11eb-9c94-3ef8623e7f01\",\r\n"
					+ "  \"name\" : \"Inserisci Dati Segnalazione\",\r\n"
					+ "  \"assignee\" : null,\r\n"
					+ "  \"created\" : \"2021-05-14T11:01:06.332+0200\",\r\n"
					+ "  \"due\" : null,\r\n"
					+ "  \"followUp\" : null,\r\n"
					+ "  \"delegationState\" : null,\r\n"
					+ "  \"description\" : null,\r\n"
					+ "  \"executionId\" : \"ead75f49-b492-11eb-9c94-3ef8623e7f01\",\r\n"
					+ "  \"owner\" : null,\r\n"
					+ "  \"parentTaskId\" : null,\r\n"
					+ "  \"priority\" : 50,\r\n"
					+ "  \"processDefinitionId\" : \"ProcessA1:1:bf4a89b8-b492-11eb-9c94-3ef8623e7f01\",\r\n"
					+ "  \"processInstanceId\" : \"ead75f49-b492-11eb-9c94-3ef8623e7f01\",\r\n"
					+ "  \"taskDefinitionKey\" : \"InserisciDatiSegnalazione\",\r\n"
					+ "  \"caseExecutionId\" : null,\r\n"
					+ "  \"caseInstanceId\" : null,\r\n"
					+ "  \"caseDefinitionId\" : null,\r\n"
					+ "  \"suspended\" : false,\r\n"
					+ "  \"formKey\" : null,\r\n"
					+ "  \"tenantId\" : null\r\n"
					+ "}, {\r\n"
					+ "  \"id\" : \"eae39450-b492-11eb-9c94-3ef8623e7f01\",\r\n"
					+ "  \"name\" : \"Inserisci Dati Segnalazione\",\r\n"
					+ "  \"assignee\" : null,\r\n"
					+ "  \"created\" : \"2021-05-14T11:01:06.394+0200\",\r\n"
					+ "  \"due\" : null,\r\n"
					+ "  \"followUp\" : null,\r\n"
					+ "  \"delegationState\" : null,\r\n"
					+ "  \"description\" : null,\r\n"
					+ "  \"executionId\" : \"eae2f80d-b492-11eb-9c94-3ef8623e7f01\",\r\n"
					+ "  \"owner\" : null,\r\n"
					+ "  \"parentTaskId\" : null,\r\n"
					+ "  \"priority\" : 50,\r\n"
					+ "  \"processDefinitionId\" : \"ProcessA1:1:bf4a89b8-b492-11eb-9c94-3ef8623e7f01\",\r\n"
					+ "  \"processInstanceId\" : \"eae2f80d-b492-11eb-9c94-3ef8623e7f01\",\r\n"
					+ "  \"taskDefinitionKey\" : \"InserisciDatiSegnalazione\",\r\n"
					+ "  \"caseExecutionId\" : null,\r\n"
					+ "  \"caseInstanceId\" : null,\r\n"
					+ "  \"caseDefinitionId\" : null,\r\n"
					+ "  \"suspended\" : false,\r\n"
					+ "  \"formKey\" : null,\r\n"
					+ "  \"tenantId\" : null\r\n"
					+ "} ]";

	public static void main(String[] args) {
		main_parseArray();
		System.out.println("\r\n-------------------------\r\n");
		main_parseObject();
	}
	
	public static void main_parseArray() {
		try {
			Object object = parseObjectOrArray(arrayOfTasks, Task[].class);
			for (Task task : (Task[])object) {
				System.out.println(task.id + " - " + task.name);
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		}

	}

	public static Object parseObjectOrArray(String json, Class classType) throws RuntimeException {

		Gson gson = new Gson();
		
		Object objectOrArray = gson.fromJson(json, classType);
		
		return objectOrArray;
	}
	
	public static void main_parseObject () {
		String myFunnyJson = "{\"colore\":\"beige\",\"dimensione\":15}";
		
		Object myFunnyObject = parseObjectOrArray(myFunnyJson, CippaLippa3.class);
		
		System.out.print(myFunnyJson + " --> ");
		System.out.println(myFunnyObject);
	}

}

class CippaLippa3 {
	public String colore;
	public int dimensione;

	@Override
	public String toString() {
		return "CippaLippa3 [colore=" + colore + ", dimensione=" + dimensione + "]";
	}
}

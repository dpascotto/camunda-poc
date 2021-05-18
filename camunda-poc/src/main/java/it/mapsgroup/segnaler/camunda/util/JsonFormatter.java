package it.mapsgroup.segnaler.camunda.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonFormatter {

	public static void main(String[] args) {
		String indented = null, indented2 = null;

		indented = convertFlatJsonToFormattedJson("{\"nome\":\"Pippo\",\"eUnCane\":true}");
		System.out.println("\r\n\r\nEasy case:\r\n");
		System.out.println(indented);
		indented2 = convertFlatJsonToFormattedJson(indented);
		_assertEquals(indented, indented2);

		indented = convertFlatJsonToFormattedJson("{\"id\":\"8d632001-b3b8-11eb-b688-3ef8623e7f01\",\"name\":\"Inserisci Dati Segnalazione\",\"assignee\":null,\"created\":\"2021-05-13T08:57:59.236+0200\",\"due\":null,\"followUp\":null,\"delegationState\":null,\"description\":null,\"executionId\":\"8d5f9d8e-b3b8-11eb-b688-3ef8623e7f01\",\"owner\":null,\"parentTaskId\":null,\"priority\":50,\"processDefinitionId\":\"ProcessA1:1:8d58e6cd-b3b8-11eb-b688-3ef8623e7f01\",\"processInstanceId\":\"8d5f9d8e-b3b8-11eb-b688-3ef8623e7f01\",\"taskDefinitionKey\":\"InserisciDatiSegnalazione\",\"caseExecutionId\":null,\"caseInstanceId\":null,\"caseDefinitionId\":null,\"suspended\":false,\"formKey\":null,\"tenantId\":null}\r\n");
		System.out.println("\r\n\r\nReal case (1 item:\\r\\n)");
		System.out.println(indented);
		indented2 = convertFlatJsonToFormattedJson(indented);
		_assertEquals(indented, indented2);

		indented = convertFlatJsonToFormattedJson("[{\"id\":\"8d632001-b3b8-11eb-b688-3ef8623e7f01\",\"name\":\"Inserisci Dati Segnalazione\",\"assignee\":null,\"created\":\"2021-05-13T08:57:59.236+0200\",\"due\":null,\"followUp\":null,\"delegationState\":null,\"description\":null,\"executionId\":\"8d5f9d8e-b3b8-11eb-b688-3ef8623e7f01\",\"owner\":null,\"parentTaskId\":null,\"priority\":50,\"processDefinitionId\":\"ProcessA1:1:8d58e6cd-b3b8-11eb-b688-3ef8623e7f01\",\"processInstanceId\":\"8d5f9d8e-b3b8-11eb-b688-3ef8623e7f01\",\"taskDefinitionKey\":\"InserisciDatiSegnalazione\",\"caseExecutionId\":null,\"caseInstanceId\":null,\"caseDefinitionId\":null,\"suspended\":false,\"formKey\":null,\"tenantId\":null},{\"id\":\"a37e7512-b3b8-11eb-b688-3ef8623e7f01\",\"name\":\"Inserisci Dati Segnalazione\",\"assignee\":null,\"created\":\"2021-05-13T08:58:36.325+0200\",\"due\":null,\"followUp\":null,\"delegationState\":null,\"description\":null,\"executionId\":null,\"owner\":null,\"parentTaskId\":null,\"priority\":0,\"processDefinitionId\":null,\"processInstanceId\":null,\"taskDefinitionKey\":null,\"caseExecutionId\":null,\"caseInstanceId\":null,\"caseDefinitionId\":null,\"suspended\":false,\"formKey\":null,\"tenantId\":null}]\r\n");
		System.out.println("\r\n\r\nReal case (array with 2 items):\\r\\n");
		System.out.println(indented);
		indented2 = convertFlatJsonToFormattedJson(indented);
		_assertEquals(indented, indented2);

	}
	
	private static void _assertEquals(String a, String b) {
		if (!a.equals(b)) {
			throw new RuntimeException("Formatting an already formatted Json changes the format:\r\na\r\nb");
		}
		
	}

	/**
	 * Converte json piatto in json formattato. Se si passa in input un json già formattato
	 * non succede nulla
	 * 
	 * https://stackoverflow.com/questions/14515994/convert-json-string-to-pretty-print-json-output-using-jackson
	 * 
	 * @param flatJson
	 * @return
	 */
	public static String convertFlatJsonToFormattedJson(String flatJson) {
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			Object json = mapper.readValue(flatJson, Object.class);
			String indented = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
			
			return indented;
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

}

package it.mapsgroup.segnaler.camunda.rest.client.vo;

import it.mapsgroup.segnaler.camunda.util.ObjectToJson;

@Deprecated
public class ProcessA1Request extends ProcessRequest {
	
	// Campi custom applicativo
	@Deprecated
	//public CustomProcessAttributes variables = new CustomProcessAttributes();

	public static void main(String[] args) {
		ProcessA1Request a1 = new ProcessA1Request();
		//a1.businessKey = "chiave-degli-affari";
		
//		a1.variables.nomeSoggetto = CustomVariableValueAndType.asString("nome-soggetto-1");
//		a1.variables.testoSegnalazione = CustomVariableValueAndType.asString("E naufragar m'è dolce in questo mare");
//		a1.variables.eUnaFigata = CustomVariableValueAndType.asBoolean(true);
		//a1.businessInputAttributes = ObjectToJson.toJson(new CippaLippa());
		
		
		String json = ObjectToJson.toJson(a1);
		System.out.println(json);
	}

}

class CippaLippa {
	public String name = "Cippa";
	public String surname = "Lippa";
}

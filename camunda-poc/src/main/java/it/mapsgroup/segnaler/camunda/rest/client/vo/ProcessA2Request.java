package it.mapsgroup.segnaler.camunda.rest.client.vo;

import it.mapsgroup.segnaler.camunda.rest.client.vo.variables.CustomVariable;
import it.mapsgroup.segnaler.camunda.util.ObjectToJson;

public class ProcessA2Request extends ProcessRequest {
	
	public static void main(String[] args) {
		ProcessA2Request a2 = new ProcessA2Request();
		
		a2.businessKey = "MainRunPK";
//		a2.processType = "A2_UNIT_TEST";
//		a2.variables.businessInputAttributes = CustomVariableValueAndType.asString(ObjectToJson.toJson(new CippaLippa2()));
		a2.variables.processType = CustomVariable.asString("A2_TEST");
		a2.variables.businessInputAttributes = CustomVariable.asString(new CippaLippa2().toString());
		
		
		String json = ObjectToJson.toJson(a2);
		System.out.println(json);
	}

}

class CippaLippa2 {
	public String name = "Main";
	public String surname = "RUN";
}

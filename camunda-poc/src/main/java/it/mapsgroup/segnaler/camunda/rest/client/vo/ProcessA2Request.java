package it.mapsgroup.segnaler.camunda.rest.client.vo;

import java.util.Random;

import it.mapsgroup.segnaler.camunda.rest.client.vo.variables.CustomVariable;
import it.mapsgroup.segnaler.camunda.util.ObjectToJson;

public class ProcessA2Request extends ProcessRequest {
	
	public ProcessA2Request(String businessKey) {
		super(businessKey);
	}

	public static void main(String[] args) {
		ProcessA2Request a2 = new ProcessA2Request("LaMiaBusinessKey");
		
		//a2.businessKey = "MainRunPK";

		//a2.variables.nativeObjectId = CustomVariable.asString("ID_SISTEMA_CHIAMANTE_" + _random(100, 999));
		//a2.variables.processType = CustomVariable.asString("A2_TEST");
		//a2.variables.businessInputAttributes = CustomVariable.asString(new CippaLippa2().toString());
		a2.setProcessType("A2_TEST");
		a2.setBusinessInputAttributes(new CippaLippa2().toString());
		
		
		String json = ObjectToJson.toJson(a2);
		System.out.println(json);
	}
	
	private static String _random(int low, int high) {
		Random r = new Random();
		int result = r.nextInt(high-low) + low;
		
		return "" + result;
	}


}

class CippaLippa2 {
	public String name = "Main";
	public String surname = "RUN";
	
	@Override
	public String toString() {
		return "CippaLippa2 [name=" + name + ", surname=" + surname + "]";
	}
	
}

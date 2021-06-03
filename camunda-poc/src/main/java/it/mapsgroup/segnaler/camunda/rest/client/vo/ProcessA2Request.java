package it.mapsgroup.segnaler.camunda.rest.client.vo;

import java.util.Random;

import it.mapsgroup.segnaler.camunda.util.ObjectToJson;

public class ProcessA2Request extends ProcessRequest {
	
	public ProcessA2Request(String businessKey, String processType) {
		super(businessKey, processType);
	}

	public static void main(String[] args) {
		ProcessA2Request a2 = new ProcessA2Request("LaMiaBusinessKey", "A2_TEST");
		
		a2.addBusinessInputVariables(new CippaLippa2());
		
		
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

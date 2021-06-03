package it.mapsgroup.segnaler.camunda.rest.client.vo.variables;

// TODO mettere riferimento alla documentazione
public class CustomVariable {
	public String type;
	public Object value;
	
	public static CustomVariable asString(String value) {
		
		CustomVariable cv = new CustomVariable();
		cv.value = value;
		cv.type = "String";
		
		return cv;
	}

	public static CustomVariable asBoolean(boolean value) {
		CustomVariable cv = new CustomVariable();
		cv.value = value;
		cv.type = "Boolean";
		
		return cv;
	}

	
	public String toString() {
		return value + " (" + type + ")";
	}
}

package it.mapsgroup.segnaler.camunda.rest.client.vo.variables;

public class CustomVariable {
	public String type;
	public String value;
	
	public String toString() {
		return value + " (" + type + ")";
	}
}

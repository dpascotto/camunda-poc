package it.mapsgroup.segnaler.camunda.rest.client.vo;

public enum VariableType {
	String("String"),
	Boolean("boolean");
	
	public final String label;
	
	private VariableType(String label) {
		this.label = label;
	}
}

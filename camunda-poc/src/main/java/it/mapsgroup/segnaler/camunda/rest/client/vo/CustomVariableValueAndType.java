package it.mapsgroup.segnaler.camunda.rest.client.vo;

// https://docs.camunda.org/manual/7.15/reference/rest/process-definition/post-start-process-instance/
public class CustomVariableValueAndType {
	public Object value;
	public VariableType type;
	
	@Deprecated
	public void setString(String value) {
		this.value = value;
		this.type = VariableType.String;
	}

	public static CustomVariableValueAndType asString(String value) {
		
		CustomVariableValueAndType cvvat = new CustomVariableValueAndType();
		cvvat.value = value;
		cvvat.type = VariableType.String;
		
		return cvvat;
	}

	public static CustomVariableValueAndType asBoolean(boolean value) {
		CustomVariableValueAndType cvvat = new CustomVariableValueAndType();
		cvvat.value = value;
		cvvat.type = VariableType.Boolean;
		
		return cvvat;
	}

}

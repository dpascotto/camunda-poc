package it.mapsgroup.segnaler.camunda.rest.client.vo;

@Deprecated
public class CustomTaskAttributes implements CustomVariables {
	public CustomVariableValueAndType nomeSoggetto = new CustomVariableValueAndType();
	public CustomVariableValueAndType testoSegnalazione = new CustomVariableValueAndType();
	
//	public String toReadableString() {
//		return "nomeSoggetto = " + (nomeSoggetto != null ? nomeSoggetto.value : "null") +
//					", testoSegnalazione = " + (testoSegnalazione != null ? testoSegnalazione.value : "null");
//	}
//
}

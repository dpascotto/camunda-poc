package it.mapsgroup.segnaler.camunda.rest.client.vo.variables;

public class ProcessCustomVariables {
	public CustomVariable businessId;
	public CustomVariable nomeSoggetto;
	public CustomVariable testoSegnalazione;
	public CustomVariable eUnaFigata;
	@Override
	public String toString() {
		String indentedColumn = ""
				+ "\r\n\tbusinessId ........... " + businessId
				+ "\r\n\tnomeSoggetto ......... " + nomeSoggetto
				+ "\r\n\ttestoSegnalazione .... " + testoSegnalazione
				+ "\r\n\teUnaFigata ........... " + eUnaFigata;
		
		return indentedColumn;
	}

	
	
}

package it.mapsgroup.segnaler.camunda.rest.client.vo.variables;

/*
 * Variabili custom del processo
 */
public class ProcessCustomVariables {
	/*
	 *  PK del "processo" nel sistema chiamante (cioè dell'istanza dell'oggetto
	 *  che sta avendo il flusso approvativo dal processo Appian)
	 *  Uno stesso oggetto del sistema chiamante potrebbe appartenere a più flussi
	 *  distinti di Appian (la chiusura del flusso su Appian non implica necessariamente
	 *  la non-riapertura manuale nel sistema chiamante)
	 */
	public CustomVariable nativeObjectId;
	
	/*
	 * Tipo di processo (ex 'A2')
	 */
	public CustomVariable processType;
	
	/*
	 * Condiderando che non utilizziamo il front end di Camunda,
	 * verrà passato in input un json iniziale con i valori di partenza
	 * di business
	 */
	public CustomVariable businessInputAttributes;
	
	@Override
	public String toString() {
		String indentedColumn = ""
				+ "\r\n\tnativeObjectId .................. " + nativeObjectId
				+ "\r\n\tprocessType ..................... " + processType
				+ "\r\n\tbusinessInputAttributes ......... " + businessInputAttributes;
		
		return indentedColumn;
	}

	
	
}

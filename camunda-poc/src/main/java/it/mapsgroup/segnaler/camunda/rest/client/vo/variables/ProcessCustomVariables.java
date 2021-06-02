package it.mapsgroup.segnaler.camunda.rest.client.vo.variables;

/*
 * Variabili custom del processo
 */
public class ProcessCustomVariables {
	// PK del processo nel sistema chiamante
	public CustomVariable businessId;
	
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
				+ "\r\n\tbusinessId ...................... " + businessId
				+ "\r\n\tprocessType ..................... " + processType
				+ "\r\n\tbusinessInputAttributes ......... " + businessInputAttributes;
		
		return indentedColumn;
	}

	
	
}

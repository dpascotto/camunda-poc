package it.mapsgroup.segnaler.camunda.rest.client.vo;

import it.mapsgroup.segnaler.camunda.rest.client.vo.variables.*;

// TODO mettere nel commento se la si usa solo alla richiesta della lista di processi

// http://localhost:8080/engine-rest/process-instance
/*
 *    {
        "links": [],
        "id": "801ffb68-bef0-11eb-9fe4-3ef8623e7f01",
        "definitionId": "ProcessA1:10:ccb7c9c5-be2b-11eb-9fe4-3ef8623e7f01",
        "businessKey": "Pippo",
        "caseInstanceId": null,
        "ended": false,
        "suspended": false,
        "tenantId": null
    },

 */
public class Process {
	public String id;
	
	public String definitionId;
	
	public String businessKey;
	
	public boolean ended;
	
	public ProcessCustomVariables variables;

	@Override
	public String toString() {
		return "Process [id=" + id + ", definitionId=" + definitionId + ", businessKey=" + businessKey + ", ended="
				+ ended + ", variables=" + variables + "]";
	}



	
	
	
	
	

}

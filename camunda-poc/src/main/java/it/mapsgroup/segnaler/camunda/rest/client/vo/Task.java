package it.mapsgroup.segnaler.camunda.rest.client.vo;

public class Task {
	public String id;
	public String name;
	public String assignee;
	public String description;
	public String processDefinitionId;
	
	// Campi custom applicativo
	public String nomeSoggetto;
	public String testoSegnalazione;
}

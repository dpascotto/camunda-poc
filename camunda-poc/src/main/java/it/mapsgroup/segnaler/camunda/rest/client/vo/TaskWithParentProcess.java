package it.mapsgroup.segnaler.camunda.rest.client.vo;

public class TaskWithParentProcess extends Task {
	public Process parentProcess;
	
	public String getParentProcessBusinessKey() {
		if (parentProcess != null) {
			return parentProcess.businessKey;
		}
		
		return "!! parent process is null !!";
	}

	@Override
	public String toString() {
		String task = super.toString();
		task += "\r\n\tParent process:";
		task += "\r\n\t" + this.parentProcess.toString();
		
		return task;
	}
	
	
}

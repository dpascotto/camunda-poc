package it.mapsgroup.segnaler.camunda.rest.client.vo;

import it.mapsgroup.segnaler.camunda.service.CamundaVariable;

public abstract class GenericVo {
	@CamundaVariable(camundaName = "businessId", getExpression = "pirulino")
	public String businessId;
}

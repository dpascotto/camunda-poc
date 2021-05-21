package it.mapsgroup.segnaler.camunda.service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = {ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CamundaVariable {
	public String camundaName() default "";
	public String getExpression() default "";
}

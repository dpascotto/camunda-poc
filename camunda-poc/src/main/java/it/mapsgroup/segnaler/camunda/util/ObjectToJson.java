package it.mapsgroup.segnaler.camunda.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import it.mapsgroup.segnaler.camunda.rest.client.vo.Task;

public class ObjectToJson {

	public static void main(String[] args) {
		Task task = new Task();
		
		
		String json = toJson(task);
		System.out.println(json);

	}
	
	public static String toJson(Object object) {
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String json = "";
		try {
			json = ow.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			System.err.println("Cannot generate JSON from object " + object + " of type " + (object != null ? object.getClass().getName(): null));
		}
		
		return json;
	}

}

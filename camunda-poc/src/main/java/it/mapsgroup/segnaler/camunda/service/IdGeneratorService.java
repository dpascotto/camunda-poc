package it.mapsgroup.segnaler.camunda.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class IdGeneratorService {
	
	/**
	 * Genera automaticamente un ID univoco
	 * 
	 * @return
	 */
	public String generateId() {
		String id = UUID.randomUUID().toString();
		
		System.out.println("Assigned ID: " + id);
		return id;
	}
	
	public static void main(String argh[]) {
		IdGeneratorService idgs = new IdGeneratorService();
		for(int i = 0; i < 50; i++) {
			System.out.println(idgs.generateId());
		}
	}
}

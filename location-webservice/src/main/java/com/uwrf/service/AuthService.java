package com.uwrf.service;

import java.util.concurrent.ConcurrentHashMap;

public class AuthService {
	private static ConcurrentHashMap<String, String> authMap = new ConcurrentHashMap<>();
	
	public static boolean authenticate(String email, String password) {
		if (!authMap.containsKey(email)) {
			register(email, password);
		}
		
		return authMap.get(email).equals(password);
	}
	
	private static synchronized void register(String email, String password) {
		if (!authMap.containsKey(email)) {
			authMap.put(email, password);
		}
	}
	
}

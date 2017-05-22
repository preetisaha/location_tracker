package com.uwrf.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.uwrf.service.data.Message;

public class MessageService {
	private Map<String, Message> messages = new ConcurrentHashMap<>();
	
	public Message getMessage(String email) {
		return messages.get(email);
	}
	
	public Message addMessage(Message message) {
		
		if (messages.containsKey(message.getEmail()))
			return null;
		
		messages.put(message.getEmail(), message);
		return message;
		
	}	

	public Message updateMessage(Message message) {
		
		if (!messages.containsKey(message.getEmail()))
			return null;
		
		messages.put(message.getEmail(), message);
		return message;
		
	}
	
	public Message removeMessage(String email) {
		if (!messages.containsKey(email))
			return null;
		return messages.remove(email);
	}
}

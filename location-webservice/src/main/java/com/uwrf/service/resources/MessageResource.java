package com.uwrf.service.resources;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.uwrf.error.ApplicationException;
import com.uwrf.error.ApplicationException.ERROR;
import com.uwrf.service.MessageService;
import com.uwrf.service.data.Message;

@Singleton
@Path("/api")
public class MessageResource {

	MessageService messageService = new MessageService();
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String ping(@Context HttpServletRequest servletRequest) {
	    return "OK";
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/get")
	public Message getMessage(@Context HttpServletRequest servletRequest) throws ApplicationException {
		Message message = messageService.getMessage(servletRequest.getAttribute("email").toString());
		if (message == null) throw new ApplicationException(ERROR.Resource_Not_Available);
		return message;
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/add")
	public Message addMessage(@Context HttpServletRequest servletRequest, Message message) throws ApplicationException {
		
		Message newMessage = messageService.addMessage(message);
		if (newMessage == null) throw new ApplicationException(ERROR.Invalid_Request);
		return newMessage;
		
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/update")
	public Message updateMessage(@Context HttpServletRequest servletRequest, Message message) throws ApplicationException {
				
		Message newMessage = messageService.updateMessage(message);
		if (newMessage == null) throw new ApplicationException(ERROR.Invalid_Request);
		return newMessage;
		
	}
	
	@DELETE
	@Path("/delete")
	@Produces(MediaType.APPLICATION_JSON)
	public Message deleteMessage(@Context HttpServletRequest servletRequest) throws ApplicationException {
		Message message = messageService.removeMessage(servletRequest.getAttribute("email").toString());
		if (message == null) throw new ApplicationException(ERROR.Invalid_Request);
		return message;
	}
}


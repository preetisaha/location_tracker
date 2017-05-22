package com.uwrf;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.uwrf.error.ApplicationException;
import com.uwrf.error.ApplicationException.ERROR;

@Provider
public class ApplicationExceptionHandler implements ExceptionMapper<ApplicationException> 
{
    @Override
    public Response toResponse(ApplicationException exception) 
    {
    	switch (exception.getError()) {
    		case Resource_Not_Available: {
    			return Response.status(Status.NOT_FOUND).build();
    		}
    		case Invalid_Request: {
    			return Response.status(Status.BAD_REQUEST).build();
    		}
    		default: {
    			return Response.status(Status.ACCEPTED).build();
    		}
    	}
    	
    }
}

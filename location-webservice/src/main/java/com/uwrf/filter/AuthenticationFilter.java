package com.uwrf.filter;

import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.internal.util.Base64;

import com.uwrf.service.AuthService;

@Provider
public class AuthenticationFilter implements javax.ws.rs.container.ContainerRequestFilter
{
     
    @Context
    private ResourceInfo resourceInfo;
     
    private static final String AUTH_PROPERTY = "Authorization";
    private static final String AUTH_SCHEME = "Basic";
      
    @Override
    public void filter(ContainerRequestContext requestContext)
    {
        final MultivaluedMap<String, String> headers = requestContext.getHeaders();
        final List<String> authorization = headers.get(AUTH_PROPERTY);

        if(authorization == null || authorization.isEmpty())
        {
            requestContext.abortWith(getAccessDeniedResponse());
            return;
        }
          
        final String encodedToken = authorization.get(0).replaceFirst(AUTH_SCHEME + " ", "");
        String emailAndPassword = new String(Base64.decode(encodedToken.getBytes()));;

        final StringTokenizer emailAndPasswordTokenizer = new StringTokenizer(emailAndPassword, ":");
        final String email = emailAndPasswordTokenizer.nextToken();
        final String password = emailAndPasswordTokenizer.nextToken();
          
        if( ! AuthService.authenticate(email, password) )
        {
            requestContext.abortWith(getAuthFailedResponse());
            return;
        } else {
        	requestContext.setProperty("email", email);
        }
    }
    
    private Response getAccessDeniedResponse() {
    	return Response.status(Response.Status.FORBIDDEN).entity("Access Denied").build();
    }
    
    private Response getAuthFailedResponse() {
    	return Response.status(Response.Status.UNAUTHORIZED).entity("Authentication Failed").build();
    }
    
}

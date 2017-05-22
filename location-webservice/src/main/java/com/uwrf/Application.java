package com.uwrf;

import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.ResourceConfig;

import com.uwrf.filter.AuthenticationFilter;
 
public class Application extends ResourceConfig{

	public Application() 
    {
        packages("com.uwrf");
        register(LoggingFilter.class);
 
        register(AuthenticationFilter.class);
    }
}

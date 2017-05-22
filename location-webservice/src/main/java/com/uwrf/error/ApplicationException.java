package com.uwrf.error;

import java.io.Serializable;

public class ApplicationException extends Exception implements Serializable
{
	public static enum ERROR {
		Resource_Not_Available,
		Invalid_Request
	}
	
    private static final long serialVersionUID = 1L;
    private ERROR error;
    
    public ApplicationException(ERROR error) {
        super();
        this.error = error;
    }
    
    public ERROR getError() {
    	return error;
    }
}

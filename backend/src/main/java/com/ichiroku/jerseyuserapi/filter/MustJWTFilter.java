package com.ichiroku.jerseyuserapi.filter;

import java.io.IOException;

import javax.annotation.Priority;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.NotAuthorizedException;

import java.security.Key;
import java.security.Principal;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.Priorities;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

//import javax.inject.Inject;
//import java.util.logging.Logger;


@MustJWT
@Provider
@Priority(Priorities.AUTHENTICATION)
public class MustJWTFilter implements ContainerRequestFilter {

    //@Inject
    //private Logger logger;
	
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		System.out.println("JWT FILTER");
		// Get the HTTP Authorization header from the request
        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        // Check if the HTTP Authorization header is present and formatted correctly
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new NotAuthorizedException("Authorization header must be provided");
        }
        
        // Extract the token from the HTTP Authorization header
        String token = authorizationHeader.substring("Bearer".length()).trim();
        
        // Validate the token
        try {
			String keyString = "48fFbvhtDbXN/ZOzt+LbQLl4BDVtNHY+eItoSGER9To=";
			byte[] encodedKey = keyString.getBytes();
	        Key key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "HmacSHA256");
        	Jws<Claims> jws = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        	final String subject = jws.getBody().getSubject();
        	final SecurityContext currentSecurityContext = requestContext.getSecurityContext();
        	requestContext.setSecurityContext(new SecurityContext() {

        	        @Override
        	        public Principal getUserPrincipal() {
        	            return new Principal() {
        	            	@Override
        	            	public String getName() {        	            		
        	            		return subject;
        	            	}
        	            };
        	        }

        	    @Override
        	    public boolean isUserInRole(String role) {
        	    	//No roles in this api
        	        return true;
        	    }

        	    @Override
        	    public boolean isSecure() {
        	        return currentSecurityContext.isSecure();
        	    }

        	    @Override
        	    public String getAuthenticationScheme() {
        	        return "Bearer";
        	    }
        	});
        } catch (Exception e) {
        	requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
	}
	
}

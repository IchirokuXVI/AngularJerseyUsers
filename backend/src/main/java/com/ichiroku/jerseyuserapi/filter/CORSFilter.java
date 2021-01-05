package com.ichiroku.jerseyuserapi.filter;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
@PreMatching
public class CORSFilter implements ContainerRequestFilter, ContainerResponseFilter {

	@Override
	public void filter(ContainerRequestContext request) throws IOException {
        System.out.println("CORS REQUEST FILTER");
        // If it's a preflight request, we abort the request with
        // a 200 status, and the CORS headers are added in the
        // response filter method below.
        if (isPreflightRequest(request)) {
            System.out.println("CORS REQUEST FILTER PREFLIGHT");
            request.abortWith(Response.ok().build());
        }
	}
	
    private boolean isPreflightRequest(ContainerRequestContext request) {
        return request.getHeaderString("Origin") != null
                && request.getMethod().equalsIgnoreCase("OPTIONS");
    }
	
	@Override
	public void filter(ContainerRequestContext request, ContainerResponseContext response)
			throws IOException {
        System.out.println("CORS RESPONSE FILTER");
        // if there is no Origin header, then it is not a
        // cross origin request. We don't do anything.
        if (request.getHeaderString("Origin") == null) {
            System.out.println("ORIGIN NULL");
            return;
        }
        if (isPreflightRequest(request)) {
            System.out.println("CORS RESPONSE FILTER PREFLIGHT");
            response.getHeaders().add("Access-Control-Allow-Credentials", "true");
            response.getHeaders().add("Access-Control-Allow-Methods",
                "GET, POST, PUT, DELETE, OPTIONS, HEAD");
            response.getHeaders().add("Access-Control-Allow-Headers",
                "Origin, X-Requested-With, Content-Type, Accept, Authorization, X-CSRF-Token, " +
                "Accept-Version, Content-Length, Content-MD5,  Date, X-Api-Version, X-File-Name, X-Skip-Interceptor");
        }
        response.getHeaders().add("Access-Control-Allow-Origin", "*");
	}

}

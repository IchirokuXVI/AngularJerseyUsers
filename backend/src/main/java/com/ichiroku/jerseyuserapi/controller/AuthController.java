package com.ichiroku.jerseyuserapi.controller;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;

import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.ichiroku.jerseyuserapi.filter.MustJWT;
import com.ichiroku.jerseyuserapi.model.RefreshToken;
import com.ichiroku.jerseyuserapi.model.User;
import com.ichiroku.jerseyuserapi.service.RefreshTokenService;
import com.ichiroku.jerseyuserapi.service.UserService;
import com.ichiroku.jerseyuserapi.util.Util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Path("/auth")
public class AuthController {
	
	private final UserService userServ;
	private final RefreshTokenService tokenServ;
	
	public AuthController() {
		this.userServ = new UserService();
		this.tokenServ = new RefreshTokenService();
	}
	
	
	@GET
	@MustJWT
	public Response check(@Context SecurityContext securityContext) {
		return Response.ok().build();
	}
	
	@POST
	public Response issueToken(@Context HttpHeaders headers, @FormParam("grant_type") String grantType, @FormParam("username") String username, @FormParam("password") String password) {
		System.out.println("START ISSUE TOKEN");
		boolean issueToken = false;
		User user = null;
		try {
			String keyString = "48fFbvhtDbXN/ZOzt+LbQLl4BDVtNHY+eItoSGER9To=";
			byte[] encodedKey = keyString.getBytes();
	        Key key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "HmacSHA256");
			if (grantType.equalsIgnoreCase("refresh_token")) { // RFC 6749 Section 6. Refreshing an Access Token
				String authorizationHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION);
		        // Check if the HTTP Authorization header is present and formatted correctly
		        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
		            throw new NotAuthorizedException("Authorization header must be provided");
		        }
		        String token = authorizationHeader.substring("Bearer".length()).trim();
		        @SuppressWarnings("unused")
				Jws<Claims> jws = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token); // If the token is expired or invalid an exception would be thrown
	        	RefreshToken dbToken = new RefreshToken();
	        	dbToken.setSignature(jws.getSignature());
	        	issueToken = tokenServ.isValid(dbToken);
	        	tokenServ.revoke(dbToken);
	        	user = tokenServ.getUser(dbToken);
	        	// user = serv.getUser(jws.getBody().getSubject()); //Both ways works for finding the user, either through the username in the token or resolving the user through the token signature in database
			} else if (grantType.equalsIgnoreCase("password")) { // RFC 6749 Section 4.3. Resource Owner Password Credentials Grant
		        // Simple data validation
		        if(username == null || username.trim().equals("")) {
		            throw new NotAuthorizedException("Username not provided");
		        }
		        
		        if(password == null || password.trim().equals("")) {
		            throw new NotAuthorizedException("Password not provided");
		        }
				user = userServ.getUser(username, password);
				issueToken = user != null;
			}
			if (issueToken) {
				String accessToken = Jwts.builder()
						.claim("userId", user.getId())
						.setSubject(user.getUsername())
						.setIssuedAt(new Date())
						.setExpiration(new Date(System.currentTimeMillis() + 3 * 60 * 1000)) //3 minutes
						.signWith(key)
						.compact();
				String refreshToken = Jwts.builder()
						.claim("userId", user.getId())
						.setIssuedAt(new Date())
						.setExpiration(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000)) //1 week / 7 days
						.signWith(key)
						.compact();
				String signature = refreshToken.split("\\.")[2];
				RefreshToken dbToken = new RefreshToken();
				dbToken.setSignature(signature);
				tokenServ.create(dbToken, user);
				HashMap<String, String> tokens = new HashMap<String, String>();
				tokens.put("accessToken", accessToken);
				tokens.put("refreshToken", refreshToken);
				return Response.ok(tokens).build();
			} else {
				throw new Exception();
			}
		} catch (Exception e) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
	}
}

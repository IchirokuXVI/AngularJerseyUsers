package com.ichiroku.jerseyuserapi.controller;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.ichiroku.jerseyuserapi.filter.MustJWT;
import com.ichiroku.jerseyuserapi.model.CustomDesignOptions;
import com.ichiroku.jerseyuserapi.model.User;
import com.ichiroku.jerseyuserapi.service.CustomDesignOptionsService;
import com.ichiroku.jerseyuserapi.service.UserService;
import com.ichiroku.jerseyuserapi.util.Util;



@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Path("/users")
public class UserController {
	
	private final UserService userServ;
	private final CustomDesignOptionsService optServ;
	
	public UserController() {
		this.userServ = new UserService();
		this.optServ = new CustomDesignOptionsService();
	}
	
	@GET
	@MustJWT
	@Path("/{username}")
	public User getUser(@PathParam("username") String username) {
		return userServ.getUser(username);
	}
	
	@POST
	public Response createUser(@FormParam("username") String username, @FormParam("password") String password) {
		User user = new User();
		user.setUsername(username);
		String hashedPassword = Util.digest(password);
		user.setPassword(hashedPassword);
		if (userServ.create(user)) {
	        return Response.status(Response.Status.CREATED).entity(user).build();
		} else {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
	}
	
	@PUT
	@MustJWT
	public Response updateUser(@Context SecurityContext context, @FormParam("password") String password, @FormParam("newUsername") String newUsername, @FormParam("newPassword") String newPassword) {
		User user;
		try {
			System.out.println(context.getUserPrincipal().getName());
			user = userServ.getUser(context.getUserPrincipal().getName(), password);
		} catch (Exception e) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Wrong password").build();
		}
		try {
			User userCheck = userServ.getUser(newUsername);
			if (userCheck != null && !userCheck.getUsername().equalsIgnoreCase(context.getUserPrincipal().getName())) {
				throw new Exception();
			}
			if (user != null) {
				if (newPassword != null && !newPassword.trim().isEmpty()) {
					user.setPassword(Util.digest(newPassword));
				}
				if (newUsername != null && !newUsername.trim().isEmpty()) {
					user.setUsername(newUsername);
				}
				
				userServ.updateUser(user);
				return Response.ok(user).build();
			}
		} catch (Exception e) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Username already taken").build();
		}
		return null;
	}
	
	@GET
	@MustJWT
	@Path("/options")
	public List<CustomDesignOptions> getOptions(@Context SecurityContext context) {
		User user = userServ.getUser(context.getUserPrincipal().getName());
		// Description isn't important so it is not sent
		return userServ.getOptions(user);
	}
	
	@POST
	@MustJWT
	@Path("/options")
	public Response updateOptions(@Context SecurityContext context, @FormParam("options") List<Integer> optionsId) {
		User user = userServ.getUser(context.getUserPrincipal().getName());
		ArrayList<CustomDesignOptions> options = new ArrayList<CustomDesignOptions>();
		for (int optionId : optionsId) {
			options.add(optServ.get(optionId));
		}
		try {
			userServ.updateOptions(user, options);
			return Response.ok(options).build();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Response.status(Response.Status.BAD_REQUEST).build();
	}
	
	@DELETE
	@MustJWT
	public Response deleteUser(@Context SecurityContext context, @FormParam("password") String password) {
		User user;
		try {
			user = userServ.getUser(context.getUserPrincipal().getName(), password);
			userServ.deleteUser(user);
			return Response.ok().build();
		} catch (Exception e) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
	}
}

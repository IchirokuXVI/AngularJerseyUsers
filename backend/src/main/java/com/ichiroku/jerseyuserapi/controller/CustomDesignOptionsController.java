package com.ichiroku.jerseyuserapi.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.ichiroku.jerseyuserapi.filter.MustJWT;
import com.ichiroku.jerseyuserapi.model.CustomDesignOptions;
import com.ichiroku.jerseyuserapi.service.CustomDesignOptionsService;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Path("/userOptions")
public class CustomDesignOptionsController {
	private final CustomDesignOptionsService optService;
	
	
	public CustomDesignOptionsController()  {
		this.optService = new CustomDesignOptionsService();
	}
	
	@GET
	public List<CustomDesignOptions> getOptions() {
		return optService.getAll();
	}
}

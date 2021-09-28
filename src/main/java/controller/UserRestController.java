package controller;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import entity.User;
import service.UserService;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserRestController {
	
	@Inject
	UserService userService;
	
	@POST
	@Path("/register")
	public User createUser(User user) {
		return userService.registerUser(user);
	}
	
	@GET
	@Path("/{email}")
	public User getUser(@PathParam("email") String email) {
		return userService.getUser(email);
	}
	
}

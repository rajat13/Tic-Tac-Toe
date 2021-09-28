package org.creditshelf.tictactoe.controller;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.creditshelf.tictactoe.entity.Player;
import org.creditshelf.tictactoe.service.PlayerService;

@Path("/player")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PlayerRestController {
	
	@Inject
	PlayerService playerService;
	
	@POST
	@Path("/register")
	public Player createPlayer(Player player) {
		return playerService.createPlayer(player);
	}
	
	@GET
	@Path("/{email}")
	public Player getPlayer(@PathParam("email") String email) {
		return playerService.getPlayer(email);
	}
	
}

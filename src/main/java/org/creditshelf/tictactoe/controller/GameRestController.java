package org.creditshelf.tictactoe.controller;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.creditshelf.tictactoe.entity.Game;
import org.creditshelf.tictactoe.entity.Move;
import org.creditshelf.tictactoe.entity.Player;
import org.creditshelf.tictactoe.exception.GameDoesNotExistException;
import org.creditshelf.tictactoe.exception.IllegalMoveException;
import org.creditshelf.tictactoe.exception.InvalidGameJoinRequestException;
import org.creditshelf.tictactoe.service.GameService;
import org.jboss.logging.Logger;

@Path("/game")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GameRestController {

	private static Logger LOG = Logger.getLogger(GameRestController.class);
	
	@Inject
	GameService gameService;

	@POST
	@Path("/create")
	public Game createGame(Player player) throws InvalidGameJoinRequestException {
		LOG.info(String.format("New Game request by Player : %s", player.getEmail()));
		Game game =  gameService.createGame(player);
		LOG.info(String.format("Game %s created Successfully, primary player : %s", game.getGameId(), player.getEmail()));
		return game;
	}

	@POST
	@Path("/join/{GameId}")
	public Game joinGame(@PathParam("GameId") Long gameId, Player player) throws InvalidGameJoinRequestException, GameDoesNotExistException {
		LOG.info(String.format("Game Join request by Player : %s for Game Id : %s", player.getEmail(), gameId));
		Game game = gameService.joinGame(gameId, player);
		LOG.info(String.format("Player : %s successfully joined the game %s", player.getEmail(), gameId));
		return game;
	}

	@POST
	@Path("/play")
	public Game playMove(Move move) throws GameDoesNotExistException, IllegalMoveException {
		LOG.info(String.format("Move %s initiated", move.toString()));
		Game game = gameService.playMove(move);
		return game;
	}

	@GET
	@Path("/{gameId}")
	public Game getGame(@PathParam("gameId") Long gameId) throws GameDoesNotExistException {
		LOG.info(String.format("Get Game : %s", gameId));
		Game game = gameService.getGame(gameId);
		return game;
	}
}

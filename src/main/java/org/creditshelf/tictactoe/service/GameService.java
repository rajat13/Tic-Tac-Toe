package org.creditshelf.tictactoe.service;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.creditshelf.tictactoe.dao.GameDao;
import org.creditshelf.tictactoe.entity.Game;
import org.creditshelf.tictactoe.entity.Move;
import org.creditshelf.tictactoe.entity.Player;
import org.creditshelf.tictactoe.exception.GameDoesNotExistException;
import org.creditshelf.tictactoe.exception.IllegalMoveException;
import org.creditshelf.tictactoe.exception.InvalidGameJoinRequestException;
import org.creditshelf.tictactoe.utils.GameUtils;
import org.jboss.logging.Logger;

@ApplicationScoped
public class GameService {

	private static final Logger LOG = Logger.getLogger(GameService.class);
	
	@Inject
	GameDao gameDao;

	@Inject
	PlayerService playerService;

	/**
	 *
	 * @param gameId
	 * @return Game with gameID Stored in DB
	 * @throws GameDoesNotExistException
	 */
	public Game getGame(Long gameId) throws GameDoesNotExistException {
		LOG.debug(String.format("fetching Game with gameId: %s", gameId));

		Game game = gameDao.getGame(gameId);

		if(game==null) {
			throw new GameDoesNotExistException(String.format("Game with id: %s does not exist", gameId));
		}

		return game;
	}

	/**
	 *
	 * @param move (specifying user, x, y, symbol)
	 * @return Play Move, Update Board, Status and return updated Game
	 * @throws GameDoesNotExistException
	 * @throws IllegalMoveException
	 */
	public Game playMove(Move move) throws GameDoesNotExistException, IllegalMoveException {
		LOG.debug(String.format("New Move with GameId: %s PlayerId: %s X: %s Y %s Symbol %s", move.getGameid(), move.getPlayer(), move.getX(), move.getY(), move.getSymbol()));

		Game game = gameDao.getGame(move.getGameid());
		Player primary = game.getPrimaryPlayer();
		Player secondary = game.getSecondaryPlayer();

		Player currentTurn = move.getPlayer().equals(primary.getEmail())?primary:secondary;
		Player nextTurn = currentTurn==primary?secondary:primary;

		GameUtils.validateMove(game,move);
		game = GameUtils.updateBoard(game, move);
		game = GameUtils.manageWin(game, move, currentTurn);
		game.setTurn(nextTurn);
		gameDao.updateGame(game);
		return game;
	}

	/**
	 *
	 * @param player
	 * @return Create a new Game with player as the primary player and return it.
	 * @throws InvalidGameJoinRequestException
	 */
	public Game createGame(Player player) throws InvalidGameJoinRequestException {
		LOG.debug(String.format("Creating New Game with Primary Player: %s", player.getEmail()));

		Game game = new Game();
		Player primaryPlayer = playerService.getPlayer(player.getEmail());

		if(primaryPlayer == null){
			throw new InvalidGameJoinRequestException(String.format("Player %s does not exist", player.getEmail()));
		}

		game.setPrimaryPlayer(primaryPlayer);
		game.setTurn(primaryPlayer);
		gameDao.createGame(game);

		LOG.debug(String.format("Created New Game with Primary Player: %s and Game Id: %s", primaryPlayer.getEmail(), game.getGameId()));
		return game;
	}

	/**
	 *
	 * @param gameId
	 * @param secondaryPlayer
	 * @return Secondary Player wants to join the game with gameId, update the game and return it.
	 * @throws InvalidGameJoinRequestException
	 * @throws GameDoesNotExistException
	 */
	public Game joinGame(Long gameId, Player secondaryPlayer) throws InvalidGameJoinRequestException, GameDoesNotExistException {
		LOG.debug(String.format("Joining Game with Secondary Player: %s and Game Id: %s", secondaryPlayer.getEmail(), gameId));

		Game game = gameDao.getGame(gameId);
		Player fetchedPlayer = playerService.getPlayer(secondaryPlayer.getEmail());

		GameUtils.validateGameJoinRequest(game, secondaryPlayer, fetchedPlayer);

		game.setSecondaryPlayer(fetchedPlayer);
		game.setStatus(GameStatus.IN_PROGRESS);
		gameDao.updateGame(game);

		LOG.debug(String.format("Secondary Player: %s joined game %s successfully", secondaryPlayer.getEmail(), gameId));
		return game;
	}
}

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
import org.jboss.logging.Logger;

@ApplicationScoped
public class GameService {

	private static final Logger LOG = Logger.getLogger(GameService.class);
	
	@Inject
	GameDao gameDao;

	@Inject
	PlayerService playerService;
	
	public Game getGame(Long gameId) throws GameDoesNotExistException {
		LOG.debug(String.format("fetching Game with gameId: %s", gameId));
		Game game = gameDao.getGame(gameId);
		if(game==null) {
			throw new GameDoesNotExistException(String.format("Game with id: %s does not exist", gameId));
		}
		return game;
	}

	public Game playMove(Move move) throws GameDoesNotExistException, IllegalMoveException {
		LOG.debug(String.format("New Move with GameId: %s PlayerId: %s X: %s Y %s Symbol %s", move.getGameid(), move.getPlayer(), move.getX(), move.getY(), move.getSymbol()));
		Game game = gameDao.getGame(move.getGameid());
		validateMove(game,move);
		makeMove(game, move);
		manageWin(game, move);
		toggleTurn(game);
		gameDao.updateGame(game);
		return game;
	}

	public Game createGame(Player user) {
		LOG.debug(String.format("Creating New Game with Primary Player: %s", user.getEmail()));
		Game game = new Game();
		Player player = playerService.getOrCreatePlayer(user);
		game.setPrimaryplayer(player);
		game.setTurn(player);
		gameDao.createGame(game);
		LOG.debug(String.format("Created New Game with Primary Player: %s and Game Id: %s", player.getEmail(), game.getGameId()));
		return game;
	}

	public Game joinGame(Long gameId, Player user) throws InvalidGameJoinRequestException, GameDoesNotExistException {
		LOG.debug(String.format("Joining Game with Secondary Player: %s and Game Id: %s", user.getEmail(), gameId));

		Game game = gameDao.getGame(gameId);
		
		if (game == null) {
			throw new GameDoesNotExistException("Game Does not Exist");
		}

		String primaryplayer = game.getPrimaryplayer();
		if (primaryplayer.contentEquals(user.getEmail())) {
			throw new InvalidGameJoinRequestException("Primary Player is same as Secondary Player");
		}
		String secondaryPlayer = game.getSecondaryPlayer();
		if (secondaryPlayer != null && !secondaryPlayer.contentEquals(user.getEmail())) {
			throw new InvalidGameJoinRequestException("Both Players are already assigned");
		}

		if (secondaryPlayer != null && secondaryPlayer.contentEquals(user.getEmail())) {
			return game;
		}

		Player player = playerService.getOrCreatePlayer(user);
		game.setSecondaryplayer(player);
		game.setStatus(GameStatus.IN_PROGRESS);
		gameDao.updateGame(game);
		LOG.debug(String.format("Secondary Player: %s joined game %s successfully", player.getEmail(), gameId));
		return game;
	}


	private void validateMove(Game game, Move move) throws GameDoesNotExistException, IllegalMoveException {
		if (game == null) {
			throw new GameDoesNotExistException("Game Does not Exist");
		}
		if(game.getStatus()!=GameStatus.IN_PROGRESS) {
			throw new IllegalMoveException(String.format("Game Status is %s", game.getStatus(), GameStatus.IN_PROGRESS));
		}
		if(move.getX()<0||move.getY()<0||move.getX()>=Game.BOARD_SIZE||move.getY()>=Game.BOARD_SIZE) {
			throw new IllegalMoveException(String.format("Move Coordinates Out of bounds X : %s, Y :%s", move.getX(), move.getY()));	
		}
		if(move.getSymbol()!=Game.SymbolX&&move.getSymbol()!=Game.SymbolO) {
			throw new IllegalMoveException(String.format("Illegal Symbol %s must be %s or %s", move.getSymbol(), Game.SymbolO, Game.SymbolX));			
		}
		if(!move.getPlayer().contentEquals(game.getTurn())) {
			throw new IllegalMoveException(String.format("Next Turn must be %s", game.getTurn()));
		}
	}

	private void toggleTurn(Game game) {
		String email = game.getPrimaryplayer();

		if (game.getTurn().contentEquals(game.getPrimaryplayer())) {
			email = game.getSecondaryPlayer();
		}

		Player nextTurn = playerService.getPlayer(email);
		game.setTurn(nextTurn);
	}

	private void manageWin(Game game, Move move) {

		String[] values = game.getBoard().split(",");
		int row = move.getX();
		int col = move.getY();

		boolean isWinner = checkRow(values, row) || checkCol(values, col)
				|| checkDiagonal(values, row, col, Integer.toString(move.getSymbol()));
		if (isWinner) {
			game.setStatus(GameStatus.FINISHED);
			game.setWinner(playerService.getPlayer(move.getPlayer()));
			LOG.debug(String.format("Player: %s won, GameId: %s ,Total Moves: %s ", move.getPlayer(), move.getGameid(), game.getMoveCount()));
			return;
		}

		boolean isDraw = game.getMoveCount() == 9;
		if (isDraw) {
			LOG.debug(String.format("Game Ends in Draw, GameId: %s ,Total Moves: %s ", move.getGameid(), game.getMoveCount()));
			game.setStatus(GameStatus.DRAW);
		}
	}

	private boolean checkCol(String[] values, int col) {
		String intial = values[col];
		for (int row = 0; row < 3; row++) {
			int index = row * 3 + col;
			if (!values[index].contentEquals(intial)) {
				return false;
			}
		}
		return true;
	}

	private boolean checkDiagonal(String[] values, int row, int col, String symbol) {
		boolean diagonal1 = true, diagonal2 = true;
		for (int i = 0; i < 3; i++) {
			if (!values[i * 3 + i].contentEquals(symbol)) {
				diagonal1 = false;
				break;
			}
		}
		for (int i = 0; i < 3; i++) {
			if (!values[i * 3 + 2 - i].contentEquals(symbol)) {
				diagonal2 = false;
				break;
			}
		}
		return diagonal1 || diagonal2;
	}

	private boolean checkRow(String[] values, int row) {
		String intial = values[row * 3];
		for (int col = 0; col < 3; col++) {
			int index = (row) * 3 + col;
			if (!values[index].contentEquals(intial)) {
				return false;
			}
		}
		return true;
	}

	private void makeMove(Game game, Move move) throws IllegalMoveException {
		String[] values = game.getBoard().split(",");
		int index = move.getX() * 3 + move.getY();
		if(values[index].contentEquals(Integer.toString(game.SymbolO))||values[index].contentEquals(Integer.toString(game.SymbolX))) {
			throw new IllegalMoveException("Invalid Move: Position is already occupied");
		}
		values[index] = Integer.toString(move.getSymbol());
		StringBuilder sb = new StringBuilder();
		for (String key : values) {
			sb.append(key);
			sb.append(",");
		}
		sb.deleteCharAt(sb.length() - 1);
		game.setBoard(sb.toString());
		game.setMoveCount(game.getMoveCount() + 1);
	}

}

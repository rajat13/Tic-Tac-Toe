package tictactoe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.creditshelf.tictactoe.entity.Game;
import org.creditshelf.tictactoe.entity.Move;
import org.creditshelf.tictactoe.entity.Player;
import org.creditshelf.tictactoe.exception.GameDoesNotExistException;
import org.creditshelf.tictactoe.exception.IllegalMoveException;
import org.creditshelf.tictactoe.exception.InvalidGameJoinRequestException;
import org.creditshelf.tictactoe.service.GameService;
import org.creditshelf.tictactoe.service.GameStatus;
import org.creditshelf.tictactoe.service.PlayerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class GameServiceTest {

	private static final String PLAYER1_EMAIL = "player1@email.com";
	private static final String PLAYER1_NAME = "player1";
	private static final String PLAYER2_NAME = "player2";
	private static final String PLAYER2_EMAIL = "player2@email.com";
	private static final Long INVALID_GAME_ID = -1L;

	/*
	 * Moves Arrays will be used to construct player moves for testing {x,y}->
	 * refers to x and y coordinates {x0,y0},{x1,y1} ->Starting move is always for
	 * player1, next for player2 and vice versa
	 */
	private static int[][] rowMoves = { { 0, 0 }, { 1, 0 }, { 0, 1 }, { 1, 1 }, { 0, 2 } };
	private static int[][] colMoves = { { 0, 0 }, { 0, 1 }, { 1, 0 }, { 0, 2 }, { 2, 0 } };
	private static int[][] diagonalMoves = { { 0, 0 }, { 1, 0 }, { 1, 1 }, { 0, 1 }, { 2, 2 } };
	private static int[][] drawMoves = { { 0, 1 }, { 0, 0 }, { 1, 1 }, { 0, 2 }, { 1, 2 }, { 1, 0 }, { 2, 0 }, { 2, 1 },
			{ 2, 2 } };

	@Inject
	GameService gameService;

	@Inject
	PlayerService playerService;

	private Player player1, player2;
	
	@BeforeEach
	public void initialize() {
		player1 = playerService.getOrCreatePlayer(new Player(PLAYER1_EMAIL, PLAYER1_NAME));
		player2 = playerService.getOrCreatePlayer(new Player(PLAYER2_EMAIL, PLAYER2_NAME));
	}

	/*
	 * Success Test Scenarios
	 */
	
	@Test
	public void testCreateAndJoinMultiples() throws InvalidGameJoinRequestException, GameDoesNotExistException {
		Game game1 = createAndJoinNewGame(player1, player2);
		Game game2 = createAndJoinNewGame(player1, player2);
		Game game3 = createAndJoinNewGame(player1, player2);
		checkValidGame(game1, GameStatus.IN_PROGRESS);
		checkValidGame(game2, GameStatus.IN_PROGRESS);
		checkValidGame(game3, GameStatus.IN_PROGRESS);
		Assertions.assertNotEquals(game1.getGameId(), game2.getGameId());
		Assertions.assertNotEquals(game2.getGameId(), game3.getGameId());
		Assertions.assertNotEquals(game1.getGameId(), game3.getGameId());
	}

	@Test
	public void testWinGameByColumnCompletion() throws GameDoesNotExistException, IllegalMoveException, InvalidGameJoinRequestException {
		Game game = createAndJoinNewGame(player1, player2);
		List<Move> player1WinColumn1Moves = generateMoves(game.getGameId(), colMoves);
		for (Move move : player1WinColumn1Moves) {
			game = gameService.playMove(move);
		}
		assertEquals(game.getStatus(), GameStatus.FINISHED);
		assertEquals(game.getWinner(), player1.getEmail());
	}

	@Test
	public void testWinGameByRowCompletion() throws GameDoesNotExistException, IllegalMoveException, InvalidGameJoinRequestException {
		Game game = createAndJoinNewGame(player1, player2);
		List<Move> player1WinColumn1Moves = generateMoves(game.getGameId(), rowMoves);
		for (Move move : player1WinColumn1Moves) {
			game = gameService.playMove(move);
		}
		assertEquals(game.getStatus(), GameStatus.FINISHED);
		assertEquals(game.getWinner(), player1.getEmail());
	}

	@Test
	public void testWinGameByDiagonalCompletion() throws GameDoesNotExistException, IllegalMoveException, InvalidGameJoinRequestException {
		Game game = createAndJoinNewGame(player1, player2);
		List<Move> player1WinColumn1Moves = generateMoves(game.getGameId(), diagonalMoves);
		for (Move move : player1WinColumn1Moves) {
			game = gameService.playMove(move);
		}
		assertEquals(game.getStatus(), GameStatus.FINISHED);
		assertEquals(game.getWinner(), player1.getEmail());
	}

	@Test
	public void testGameDraw() throws GameDoesNotExistException, IllegalMoveException, InvalidGameJoinRequestException {
		Game game = createAndJoinNewGame(player1, player2);
		List<Move> player1WinColumn1Moves = generateMoves(game.getGameId(), drawMoves);
		for (Move move : player1WinColumn1Moves) {
			game = gameService.playMove(move);
		}
		assertEquals(game.getStatus(), GameStatus.DRAW);
	}

	/*
	 * Failure Test Scenarios
	 */

	@Test
	public void testGameDoesNotExist() {
		assertThrows(GameDoesNotExistException.class, ()->gameService.getGame(INVALID_GAME_ID));
	}
	
	@Test
	public void testGameIsFinished() throws InvalidGameJoinRequestException, GameDoesNotExistException, IllegalMoveException {
		Game finishedGame = getFinishedGame();
		Move move = new Move(finishedGame.getGameId(), player1.getEmail(), Game.SymbolO, 0,0);
		assertThrows(IllegalMoveException.class, ()->gameService.playMove(move));
	}
	
	@Test
	public void testIllegalNextTurn() throws InvalidGameJoinRequestException, GameDoesNotExistException {
		Game finishedGame = createAndJoinNewGame(player1, player2);
		Move move = new Move(finishedGame.getGameId(), player2.getEmail(), Game.SymbolO, 0,0);
		assertThrows(IllegalMoveException.class, ()->gameService.playMove(move));
	}

	@Test
	public void testIllegalCoordinates() throws InvalidGameJoinRequestException, GameDoesNotExistException {
		Game finishedGame = createAndJoinNewGame(player1, player2);
		Move move = new Move(finishedGame.getGameId(), player1.getEmail(), Game.SymbolO, -1,-1);
		assertThrows(IllegalMoveException.class, ()->gameService.playMove(move));
	}

	@Test
	public void testIllegalSymbol() throws InvalidGameJoinRequestException, GameDoesNotExistException {
		Game finishedGame = createAndJoinNewGame(player1, player2);
		Move move = new Move(finishedGame.getGameId(), player1.getEmail(), 3, 0,0);
		assertThrows(IllegalMoveException.class, ()->gameService.playMove(move));
	}

	/*
	 * Utility Methods
	 */
	
	public Game createAndJoinNewGame(Player player1, Player player2) throws InvalidGameJoinRequestException, GameDoesNotExistException {
		Game game = gameService.createGame(player1);
		gameService.joinGame(game.getGameId(), player2);
		Game createdGame = gameService.getGame(game.getGameId());
		return createdGame;
	}

	public void checkValidGame(Game game, GameStatus status) {
		Assertions.assertNotNull(game);
		Assertions.assertEquals(game.getPrimaryplayer(), player1.getEmail());
		Assertions.assertEquals(game.getSecondaryPlayer(), player2.getEmail());
		Assertions.assertEquals(game.getStatus(), status);
	}

	public List<Move> generateMoves(Long gameId, int[][] moves) {
		List<Move> list = new ArrayList<Move>();
		Boolean isFirstPlayerTurn = true;
		for (int[] m : moves) {
			if (isFirstPlayerTurn) {
				list.add(new Move(gameId, player1.getEmail(), Game.SymbolX, m[0], m[1]));
				isFirstPlayerTurn = false;
			} else {
				list.add(new Move(gameId, player2.getEmail(), Game.SymbolO, m[0], m[1]));
				isFirstPlayerTurn = true;
			}
		}
		return list;
	}
	
	private Game getFinishedGame() throws InvalidGameJoinRequestException, GameDoesNotExistException, IllegalMoveException {
		Game game = createAndJoinNewGame(player1, player2);
		List<Move> player1WinColumn1Moves = generateMoves(game.getGameId(), colMoves);
		for (Move move : player1WinColumn1Moves) {
			game = gameService.playMove(move);
		}
		return game;
	}

}
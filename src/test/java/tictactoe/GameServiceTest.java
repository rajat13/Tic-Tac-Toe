package tictactoe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

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
public class GameServiceTest extends AbstractCommonTest{

	private static final String PLAYER1_EMAIL = "player1@email.com";
	private static final String PLAYER1_NAME = "player1";
	private static final String PLAYER2_NAME = "player2";
	private static final String PLAYER2_EMAIL = "player2@email.com";
	private static final Long INVALID_GAME_ID = -1L;
	private static final int THREAD_COUNT = 100;

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

	private Player player1, player2;

	@BeforeEach
	public void initialize() throws Exception {
		player1 = getOrCreatePlayer(new Player(PLAYER1_EMAIL, PLAYER1_NAME));
		player2 = getOrCreatePlayer(new Player(PLAYER2_EMAIL, PLAYER2_NAME));
	}

	/*
	 * Success Test Scenarios
	 */

	/*
	Test Case Executing 100 Games in Parallel using Executor Service and Countdown Latch.
	 */
	@Test
	public void testCreateAndPlayMultipleGamesInParallel() throws Exception {
		ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
		CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
		for(int i=0;i<THREAD_COUNT;i++){
			executorService.submit(new GameRunner(latch));
		}
		latch.await(1, TimeUnit.MINUTES);
	}

	class GameRunner implements Runnable {

		CountDownLatch latch;

		GameRunner(CountDownLatch latch){
			this.latch = latch;
		}

		@Override
		public void run() {
			try{
				Game game = createAndJoinNewGame(player1, player2);
				List<Move> moves = generateMoves(game.getGameId(), drawMoves);
				for (Move move : moves) {
					game = applyMove(move);
					if (game.getStatus() != GameStatus.IN_PROGRESS){
						break;
					}
				}
				latch.countDown();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	@Test
	public void testWinGameByColumnCompletion() throws Exception {
		Game game = createAndJoinNewGame(player1, player2);
		List<Move> player1WinColumn1Moves = generateMoves(game.getGameId(), colMoves);
		for (Move move : player1WinColumn1Moves) {
			game = applyMove(move);
		}
		assertEquals(game.getStatus(), GameStatus.FINISHED);
		assertEquals(game.getWinner().getEmail(), player1.getEmail());
	}


	@Test
	public void testWinGameByRowCompletion() throws Exception {
		Game game = createAndJoinNewGame(player1, player2);
		List<Move> player1WinColumn1Moves = generateMoves(game.getGameId(), rowMoves);
		for (Move move : player1WinColumn1Moves) {
			game = applyMove(move);
		}
		assertEquals(game.getStatus(), GameStatus.FINISHED);
		assertEquals(game.getWinner().getEmail(), player1.getEmail());
	}

	@Test
	public void testWinGameByDiagonalCompletion() throws Exception {
		Game game = createAndJoinNewGame(player1, player2);
		List<Move> player1WinColumn1Moves = generateMoves(game.getGameId(), diagonalMoves);
		for (Move move : player1WinColumn1Moves) {
			game = applyMove(move);
		}
		assertEquals(game.getStatus(), GameStatus.FINISHED);
		assertEquals(game.getWinner().getEmail(), player1.getEmail());
	}

	@Test
	public void testGameDraw() throws Exception {
		Game game = createAndJoinNewGame(player1, player2);
		List<Move> player1WinColumn1Moves = generateMoves(game.getGameId(), drawMoves);
		for (Move move : player1WinColumn1Moves) {
			game = applyMove(move);
		}
		assertEquals(game.getStatus(), GameStatus.DRAW);
	}

	/*
	 * Failure Test Scenarios
	 */

	@Test
	public void testGameDoesNotExist() {
		assertThrows(Exception.class, ()->getGame(INVALID_GAME_ID));
	}
	
	@Test
	public void testGameIsFinished() throws Exception {
		Game finishedGame = getFinishedGame();
		Move move = new Move(finishedGame.getGameId(), player1.getEmail(), 0,0);
		assertThrows(Exception.class, ()->applyMove(move));
	}
	
	@Test
	public void testIllegalNextTurn() throws Exception {
		Game finishedGame = createAndJoinNewGame(player1, player2);
		Move move = new Move(finishedGame.getGameId(), player2.getEmail(), 0,0);
		assertThrows(Exception.class, ()->applyMove(move));
	}

	@Test
	public void testIllegalCoordinates() throws Exception {
		Game finishedGame = createAndJoinNewGame(player1, player2);
		Move move = new Move(finishedGame.getGameId(), player1.getEmail(), -1,-1);
		assertThrows(Exception.class, ()->applyMove(move));
	}

	/*
	 * Utility Methods
	 */
	
	public Game createAndJoinNewGame(Player player1, Player player2) throws Exception {
		Game game = createGame(player1);
		return joinGame(game.getGameId(), player2);
	}


	public void checkValidGame(Game game, GameStatus status) {
		Assertions.assertNotNull(game);
		Assertions.assertEquals(game.getPrimaryPlayer().getEmail(), player1.getEmail());
		Assertions.assertEquals(game.getSecondaryPlayer().getEmail(), player2.getEmail());
		Assertions.assertEquals(game.getStatus(), status);
	}

	public List<Move> generateMoves(Long gameId, int[][] moves) {
		List<Move> list = new ArrayList<Move>();
		Boolean isFirstPlayerTurn = true;
		for (int[] m : moves) {
			if (isFirstPlayerTurn) {
				list.add(new Move(gameId, player1.getEmail(), m[0], m[1]));
				isFirstPlayerTurn = false;
			} else {
				list.add(new Move(gameId, player2.getEmail(), m[0], m[1]));
				isFirstPlayerTurn = true;
			}
		}
		return list;
	}
	
	private Game getFinishedGame() throws Exception {
		Game game = createAndJoinNewGame(player1, player2);
		List<Move> player1WinColumn1Moves = generateMoves(game.getGameId(), colMoves);
		for (Move move : player1WinColumn1Moves) {
			game = applyMove(move);
		}
		return game;
	}
}
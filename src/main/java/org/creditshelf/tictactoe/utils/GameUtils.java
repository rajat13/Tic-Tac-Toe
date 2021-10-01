package org.creditshelf.tictactoe.utils;

import org.creditshelf.tictactoe.entity.Game;
import org.creditshelf.tictactoe.entity.Move;
import org.creditshelf.tictactoe.entity.Player;
import org.creditshelf.tictactoe.exception.GameDoesNotExistException;
import org.creditshelf.tictactoe.exception.IllegalMoveException;
import org.creditshelf.tictactoe.exception.InvalidGameJoinRequestException;
import org.creditshelf.tictactoe.service.GameStatus;

public class GameUtils {

    /**
     *
     * @param game
     * @param move
     * @throws GameDoesNotExistException
     * @throws IllegalMoveException
     */
    public static void validateMove(Game game, Move move) throws GameDoesNotExistException, IllegalMoveException {
        if (game == null) {
            throw new GameDoesNotExistException("Game Does not Exist");
        }
        if(game.getStatus()!= GameStatus.IN_PROGRESS) {
            throw new IllegalMoveException(String.format("Game Status is %s", game.getStatus(), GameStatus.IN_PROGRESS));
        }
        if(move.getX()<0||move.getY()<0||move.getX()>=Game.BOARD_SIZE||move.getY()>=Game.BOARD_SIZE) {
            throw new IllegalMoveException(String.format("Move Coordinates Out of bounds X : %s, Y :%s", move.getX(), move.getY()));
        }
        if(move.getSymbol()!=Game.SymbolX&&move.getSymbol()!=Game.SymbolO) {
            throw new IllegalMoveException(String.format("Illegal Symbol %s must be %s or %s", move.getSymbol(), Game.SymbolO, Game.SymbolX));
        }
        if(!move.getPlayer().contentEquals(game.getTurn().getEmail())) {
            throw new IllegalMoveException(String.format("Next Turn must be %s", game.getTurn()));
        }
    }

    /**
     *
     * @param game
     * @param move
     * @param player
     * @return Check if the Game is a win or draw, update the object and return it.
     */
    public static Game manageWin(Game game, Move move, Player player) {

        String[] values = game.getBoard().split(",");
        int row = move.getX();
        int col = move.getY();

        boolean isWinner = isRowCompleted(values, row) || isColumnCompleted(values, col) || isDiagonalCompleted(values, row, col, Integer.toString(move.getSymbol()));
        boolean isDraw = !isWinner && game.getMoveCount() == 9;

        if (isWinner) {
            game.setStatus(GameStatus.FINISHED);
            game.setWinner(player);
        }else if(isDraw){
            game.setStatus(GameStatus.DRAW);
        }

        return game;
    }

    /**
     *
     * @param game
     * @param user
     * @param fetched
     * @throws GameDoesNotExistException
     * @throws InvalidGameJoinRequestException
     */
    public static void validateGameJoinRequest(Game game, Player user, Player fetched) throws GameDoesNotExistException, InvalidGameJoinRequestException {

        if (game == null) {
            throw new GameDoesNotExistException("Game Does not Exist");
        }

        if(fetched == null){
            throw new InvalidGameJoinRequestException(String.format("Player %s does not exist", user.getEmail()));
        }

        String primaryPlayer = game.getPrimaryPlayer()==null?null:game.getPrimaryPlayer().getEmail();
        if (primaryPlayer!=null && primaryPlayer.contentEquals(user.getEmail())) {
            throw new InvalidGameJoinRequestException("Primary Player is same as Secondary Player");
        }
        String secondaryPlayer = game.getSecondaryPlayer()==null?null:game.getSecondaryPlayer().getEmail();
        if (secondaryPlayer != null && !secondaryPlayer.contentEquals(user.getEmail())) {
            throw new InvalidGameJoinRequestException("Both Players are already assigned");
        }

    }

    /**
     *
     * @param values
     * @param col
     * @return Whether the Column col is completed or not
     */
    private static boolean isColumnCompleted(String[] values, int col) {
        String intial = values[col];
        for (int row = 0; row < 3; row++) {
            int index = row * 3 + col;
            if (!values[index].contentEquals(intial)) {
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @param values
     * @param row
     * @param col
     * @param symbol
     * @return True if Any of the two diagonals are complete.
     */
    private static boolean isDiagonalCompleted(String[] values, int row, int col, String symbol) {
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

    /**
     *
     * @param values
     * @param row
     * @return True if Row row is completely filled.
     */
    private static boolean isRowCompleted(String[] values, int row) {
        String intial = values[row * 3];
        for (int col = 0; col < 3; col++) {
            int index = (row) * 3 + col;
            if (!values[index].contentEquals(intial)) {
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @param game
     * @param move
     * @return Make move, Update Board and Game and return it.
     * @throws IllegalMoveException
     */
    public static Game updateBoard(Game game, Move move) throws IllegalMoveException {

        // Board is Represented as String, we convert it to array of String tokens for arithmatic.
        String[] values = game.getBoard().split(",");
        int index = move.getX() * 3 + move.getY();
        if(values[index].contentEquals(Integer.toString(game.SymbolO))||values[index].contentEquals(Integer.toString(game.SymbolX))) {
            throw new IllegalMoveException("Invalid Move: Position is already occupied");
        }
        values[index] = Integer.toString(move.getSymbol());
        String board = createBoardFromArray(values);
        game.setBoard(board);
        game.setMoveCount(game.getMoveCount() + 1);
        return game;
    }

    /**
     *
     * @param values
     * @return
     * Board is represented as String
     * Convert String from Array of Integer Tokens
     *
     */
    private static String createBoardFromArray(String[] values) {
        StringBuilder sb = new StringBuilder();
        for (String key : values) {
            sb.append(key);
            sb.append(",");
        }
        return sb.substring(0,sb.length()-1);
    }

}

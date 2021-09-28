package org.creditshelf.tictactoe.entity;

public class Move {
	private Long gameid;
	private String player;
	private int x, y;
	private int symbol;

	Move() {

	}

	public Move(Long gameId, String player, int symbol, int x, int y) {
		this.gameid = gameId;
		this.player = player;
		this.symbol = symbol;
		this.x = x;
		this.y = y;
	}

	public Long getGameid() {
		return gameid;
	}

	public int getSymbol() {
		return symbol;
	}

	public String getPlayer() {
		return player;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	@Override
	public String toString() {
		return "Move [gameid=" + gameid + ", player=" + player + ", x=" + x + ", y=" + y + ", symbol=" + symbol + "]";
	}

}

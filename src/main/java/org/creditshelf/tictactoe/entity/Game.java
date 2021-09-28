package org.creditshelf.tictactoe.entity;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.creditshelf.tictactoe.service.GameStatus;

@Entity
@Cacheable
@Table(name = "Game")
public class Game {

	private static final String EMPTY_BOARD = "0,0,0,0,0,0,0,0,0";
	public static final Integer BOARD_SIZE = 3;
	public static final Integer SymbolX=1;
	public static final Integer SymbolO=2;
	
	@Id
	@GeneratedValue
	@Column(name = "GAME_ID", nullable = false)
	private Long gameId;

	@Column(name = "STATUS", nullable = false)
	private GameStatus status;

	@Column(name = "BOARD", nullable = false)
	private String board;

	@Column(name = "MOVE_COUNT", nullable = false)
	private Integer moveCount;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "PRIMARY_PLAYER_ID", referencedColumnName = "EMAIL")
	private Player primaryplayer;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "SECONDARY_PLAYER_ID", referencedColumnName = "EMAIL")
	private Player secondaryPlayer;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "TURN_PLAYER_ID", referencedColumnName = "EMAIL")
	private Player turn;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "WINNER_PLAYER_ID", referencedColumnName = "EMAIL")
	private Player winner;

	public Long getGameId() {
		return gameId;
	}

	public Integer getMoveCount() {
		return moveCount;
	}

	public GameStatus getStatus() {
		return status;
	}

	public String getBoard() {
		return board;
	}

	public String getPrimaryplayer() {
		return primaryplayer.getEmail();
	}

	public String getSecondaryPlayer() {
		return secondaryPlayer == null ? null : secondaryPlayer.getEmail();
	}

	public String getTurn() {
		return turn.getEmail();
	}

	public String getWinner() {
		return winner == null ? null : winner.getEmail();
	}

	public void setPrimaryplayer(Player user) {
		this.primaryplayer = user;
	}

	public void setMoveCount(int count) {
		this.moveCount = count;
	}

	public void setTurn(Player user) {
		this.turn = user;
	}

	public void setSecondaryplayer(Player user) {
		this.secondaryPlayer = user;
	}

	public void setBoard(String board) {
		this.board = board;
	}

	public void setStatus(GameStatus status) {
		this.status = status;
	}

	public void setWinner(Player winner) {
		this.winner = winner;
	}

	public Game() {
		super();
		this.status = GameStatus.NEW;
		this.board = new String(EMPTY_BOARD);
		this.moveCount = 0;
	}

	@Override
	public String toString() {
		return "Game [gameId=" + gameId + ", status=" + status + ", board=" + board + ", primaryplayer=" + primaryplayer
				+ ", secondaryPlayer=" + secondaryPlayer + ", turn=" + turn + ", winner=" + winner + "]";
	}

}

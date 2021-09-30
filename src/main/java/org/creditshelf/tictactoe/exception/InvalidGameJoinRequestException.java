package org.creditshelf.tictactoe.exception;

public class InvalidGameJoinRequestException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6024204810411139321L;

	public InvalidGameJoinRequestException(String message) {
		super(message);
	}
}

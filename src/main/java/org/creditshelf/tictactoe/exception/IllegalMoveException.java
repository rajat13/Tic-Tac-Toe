package org.creditshelf.tictactoe.exception;

public class IllegalMoveException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3125219374470291657L;

	public IllegalMoveException(String message) {
		super(message);
	}
}

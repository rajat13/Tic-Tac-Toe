package org.creditshelf.tictactoe.exception;

public class GameDoesNotExistException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6024204810411139321L;
	
	public GameDoesNotExistException(String message) {
		super(message);
	}

}

package org.creditshelf.tictactoe.dao;

import org.creditshelf.tictactoe.entity.Game;

public interface GameDao {

	public void createGame(Game game);

	public void updateGame(Game game);

	public Game getGame(Long id);

}

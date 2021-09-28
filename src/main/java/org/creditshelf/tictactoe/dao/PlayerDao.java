package org.creditshelf.tictactoe.dao;

import org.creditshelf.tictactoe.entity.Player;

public interface PlayerDao {

	public void createPlayer(Player player);

	public Player getPlayer(String email);

}

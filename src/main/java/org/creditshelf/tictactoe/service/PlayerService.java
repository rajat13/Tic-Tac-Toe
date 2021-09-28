package org.creditshelf.tictactoe.service;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.creditshelf.tictactoe.dao.PlayerDao;
import org.creditshelf.tictactoe.entity.Player;

@ApplicationScoped
public class PlayerService {

	@Inject
	PlayerDao playerDao;

	public Player createPlayer(Player player) {
		playerDao.createPlayer(player);
		return player;
	}

	public Player getPlayer(String email) {
		return playerDao.getPlayer(email);
	}

	public Player getOrCreatePlayer(Player input) {
		Player player = getPlayer(input.getEmail());
		if (player == null) {
			player = createPlayer(input);
		}
		return player;
	}
}

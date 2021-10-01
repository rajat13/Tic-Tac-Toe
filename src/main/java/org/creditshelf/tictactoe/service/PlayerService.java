package org.creditshelf.tictactoe.service;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.creditshelf.tictactoe.dao.PlayerDao;
import org.creditshelf.tictactoe.entity.Player;

@ApplicationScoped
public class PlayerService {

	@Inject
	PlayerDao playerDao;

	/**
	 *
	 * @param player
	 * @return Create New Player Entry in DB and return it.
	 */
	public Player createPlayer(Player player) {
		playerDao.createPlayer(player);
		return player;
	}

	/**
	 *
	 * @param email
	 * @return Fetch player with email as primary Key in DB and return it.
	 */
	public Player getPlayer(String email) {
		return playerDao.getPlayer(email);
	}
}

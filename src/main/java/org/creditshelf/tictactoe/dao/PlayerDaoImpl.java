package org.creditshelf.tictactoe.dao;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.transaction.Transactional;

import org.creditshelf.tictactoe.entity.Player;

@ApplicationScoped
public class PlayerDaoImpl implements PlayerDao{

	@Inject
	EntityManager manager;
	
	@Override
	@Transactional
	public void createPlayer(Player player) {
		manager.persist(player);
	}

	@Override
	public Player getPlayer(String email) {
		return manager.find(Player.class, email);
	}

}

package org.creditshelf.tictactoe.dao;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.creditshelf.tictactoe.entity.Game;

@ApplicationScoped
public class GameDaoImpl implements GameDao {

	@Inject
	EntityManager manager;

	@Override
	@Transactional
	public void createGame(Game game) {
		manager.persist(game);
	}

	@Override
	@Transactional
	public void updateGame(Game game) {
		manager.merge(game);
	}

	@Override
	public Game getGame(Long id) {
		return manager.find(Game.class, id);
	}

}

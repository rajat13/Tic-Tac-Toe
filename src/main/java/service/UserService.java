package service;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import entity.User;

@ApplicationScoped
public class UserService {

	@Inject
	EntityManager manager;

	@Transactional
	public User registerUser(User user) {
		manager.persist(user);
		return user;
	}

	public User getUser(String email) {
		System.out.println(email);
		User user = manager.find(User.class, email);
		return user;
	}
}

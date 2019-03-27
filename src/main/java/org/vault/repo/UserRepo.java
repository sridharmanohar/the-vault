package org.vault.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.vault.model.User;

@org.springframework.stereotype.Repository
public interface UserRepo extends Repository<User, Integer>{

	List<User> findAll();
	
	User findById(int id);

	List<User> findByFirstnameAndLastname(String firstname, String lastname);
	
	@Transactional
	@Modifying
	void save(User user);
}

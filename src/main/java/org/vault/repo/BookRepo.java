package org.vault.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.vault.model.Book;

@org.springframework.stereotype.Repository
public interface BookRepo extends Repository<Book, Integer>{

	List<Book> findAll();
	
	Book findById(int id);
	
	@Transactional
	@Modifying
	void save(Book book);
	
}

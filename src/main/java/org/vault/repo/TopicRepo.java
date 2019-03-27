package org.vault.repo;

import java.util.List;

import java.util.Set;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.vault.model.Topic;

@org.springframework.stereotype.Repository
public interface TopicRepo extends Repository<Topic, Integer>{

	List<Topic> findAll();
	
	Topic findById(int id);
	
	List<Topic> findByTopicname(String topicname);
	
	@Transactional
	@Modifying
	void save(Topic topic);
}

package org.vault.repo;

import org.springframework.data.repository.Repository;


import org.vault.model.TopicGroup;

@org.springframework.stereotype.Repository
public interface TopicGroupRepo extends Repository<TopicGroup, Integer>{

	void save(TopicGroup topicGroup);
}

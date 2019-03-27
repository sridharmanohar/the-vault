package org.vault.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;


/**
 * The persistent class for the topics database table.
 * 
 */
@Entity
@Table(name="topics")
//@NamedQuery(name="Topic.findAll", query="SELECT t FROM Topic t")
public class Topic implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	private String topicname;

	//bi-directional many-to-one association to TopicGroup
	@OneToMany(mappedBy="topic", fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	private Set<TopicGroup> topicGroups = new HashSet<TopicGroup>();

	//bi-directional many-to-one association to TopicTemplate
	@OneToMany(mappedBy="topic", fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	@Fetch(FetchMode.SUBSELECT)
	private List<TopicTemplate> topicTemplates = new ArrayList<TopicTemplate>();
	
	public Topic() {
	}

	public Topic(String topicname, List<TopicTemplate> topicTemplates) {
		this.topicname = topicname;
		this.topicTemplates = topicTemplates;
		this.topicTemplates.forEach(x -> x.setTopic(this));
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTopicname() {
		return this.topicname;
	}

	public void setTopicname(String topicname) {
		this.topicname = topicname;
	}

	public Set<TopicGroup> getTopicGroups() {
		return topicGroups;
	}

	public void setTopicGroups(Set<TopicGroup> topicGroups) {
		this.topicGroups = topicGroups;
	}

	public List<TopicTemplate> getTopicTemplates() {
		return topicTemplates;
	}

	public void setTopicTemplates(List<TopicTemplate> topicTemplates) {
		this.topicTemplates = topicTemplates;
	}

	
	
	
}
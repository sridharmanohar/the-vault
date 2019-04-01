package org.vault.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * 1. The persistent class for the users database table.
 * 
 * 2. This table contains the user firstname and lastname.
 * 
 */
@Entity
@Table(name = "users")
//@NamedQuery(name="User.findAll", query="SELECT u FROM User u")
public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String firstname;

	private String lastname;

	// bi-directional many-to-one association to TopicGroup
	@OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private List<TopicGroup> topicGroups = new ArrayList<TopicGroup>();

	public User() {
	}

	public User(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		System.out.println("val being set:" + id);
		this.id = id;
	}

	public String getFirstname() {
		return this.firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return this.lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public List<TopicGroup> getTopicGroups() {
		return this.topicGroups;
	}

	public void setTopicGroups(List<TopicGroup> topicGroups) {
		System.out.println("in normal set");
		this.topicGroups = topicGroups;
	}

	public TopicGroup addTopicGroup(TopicGroup topicGroup) {
		getTopicGroups().add(topicGroup);
		topicGroup.setUser(this);

		return topicGroup;
	}

	public TopicGroup removeTopicGroup(TopicGroup topicGroup) {
		getTopicGroups().remove(topicGroup);
		topicGroup.setUser(null);

		return topicGroup;
	}
}
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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * 1. The persistent class for the topic_templates database table.
 * 
 * 2. This table has topic id and the topic name.
 * 
 * 3. The one arg. overloaded const. is used while updating the secret topic
 * name.
 * 
 */
@Entity
@Table(name = "topic_templates")
//@NamedQuery(name="TopicTemplate.findAll", query="SELECT t FROM TopicTemplate t")
public class TopicTemplate implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String propname;

	// bi-directional many-to-one association to TopicDetail
	@OneToMany(mappedBy = "topicTemplate", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private List<TopicDetail> topicDetails = new ArrayList<TopicDetail>();

	// bi-directional many-to-one association to Topic
	@ManyToOne
	@JoinColumn(name = "topicid")
	private Topic topic = new Topic();

	public TopicTemplate() {
	}

	public TopicTemplate(String propname) {
		this.propname = propname;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPropname() {
		return this.propname;
	}

	public void setPropname(String propname) {
		this.propname = propname;
	}

	public List<TopicDetail> getTopicDetails() {
		return topicDetails;
	}

	public void setTopicDetails(List<TopicDetail> topicDetails) {
		this.topicDetails = topicDetails;
	}

	public Topic getTopic() {
		return this.topic;
	}

	public void setTopic(Topic topic) {
		this.topic = topic;
	}

}
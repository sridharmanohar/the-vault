package org.vault.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 1. The persistent class for the topic_details database table.
 * 
 * 2. This table contains topic_templates id, topic_group id and topic name.
 * 
 * 3. The overloaded constructor with 4 arguments is used for an update
 * scenario.
 * 
 * 4. The overloaded constructor with 2 args is used for an insert scenario when
 * a new topic group entry has to be made during a new user secret addition.
 * 
 */
@Entity
@Table(name = "topic_details")
//@NamedQuery(name="TopicDetail.findAll", query="SELECT t FROM TopicDetail t")
public class TopicDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String propvalue;

	// bi-directional many-to-one association to TopicGroup
	@ManyToOne
	@JoinColumn(name = "topicgrpid")
	private TopicGroup topicGroup = new TopicGroup();

	// bi-directional many-to-one association to TopicTemplate
	@ManyToOne
	@JoinColumn(name = "templateid")
	private TopicTemplate topicTemplate = new TopicTemplate();

	public TopicDetail() {
	}

	public TopicDetail(int id, String propval, int topicgrpid, int topictempid) {
		System.out.println("fetched vals:" + id + "," + propval + "," + topicgrpid + "," + topictempid);
		this.id = id;
		this.propvalue = propval;
		this.topicGroup.setId(topicgrpid);
		this.topicTemplate.setId(topictempid);
	}

	public TopicDetail(String propval, int topictempid) {
		System.out.println("fetched vals:" + propval + ", " + topictempid);
		this.propvalue = propval;
		this.topicTemplate.setId(topictempid);
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPropvalue() {
		return this.propvalue;
	}

	public void setPropvalue(String propvalue) {
		this.propvalue = propvalue;
	}

	public TopicGroup getTopicGroup() {
		return this.topicGroup;
	}

	public void setTopicGroup(TopicGroup topicGroup) {
		this.topicGroup = topicGroup;
	}

	public TopicTemplate getTopicTemplate() {
		return this.topicTemplate;
	}

	public void setTopicTemplate(TopicTemplate topicTemplate) {
		this.topicTemplate = topicTemplate;
	}

}
package org.vault.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;


/**
 * The persistent class for the topic_groups database table.
 * 
 */
@Entity
@Table(name="topic_groups")
//@NamedQuery(name="TopicGroup.findAll", query="SELECT t FROM TopicGroup t")
public class TopicGroup implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	//bi-directional many-to-one association to TopicDetail
	@OneToMany(mappedBy="topicGroup", fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	@Fetch(FetchMode.SUBSELECT)
	private List<TopicDetail> topicDetails = new ArrayList<TopicDetail>();

	//bi-directional many-to-one association to Topic
	@ManyToOne
	@JoinColumn(name="topicid")
	private Topic topic = new Topic();

	//bi-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name="userid")
	private User user = new User();

	public TopicGroup() {
	}
	
	public TopicGroup(int id, List<TopicDetail> topicDetails, int topicid, int userid) {
		System.out.println("id:"+id);
		System.out.println("td list:"+topicDetails.size());
		System.out.println("topic id"+topicid);
		System.out.println("user id"+ userid);
		this.id = id;
		this.topicDetails = topicDetails;
		this.topic.setId(topicid);
		this.user.setId(userid);
	}
	
	public TopicGroup(List<TopicDetail> topicDetails, int topicid, int userid) {
		this.topicDetails = topicDetails;
		this.topic.setId(topicid);
		this.user.setId(userid);
	}

	public TopicGroup(int topicid, int userid) {
		this.topic.setId(topicid);
		this.user.setId(userid);
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
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

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	// solution to the problem where parent(topic_groups) and child(topic_details)
	// are to be inserted in one go i.e. first an entry is made into topic_groups and
	// using this pk an entry is made into topic_details.
	// If you do not map the child instances to the parent, while inserting into child, the pk of the
	// parent will not be found.
	public void addTopicDetails(TopicDetail topicDetail) {
		topicDetails.add(topicDetail);
		topicDetail.setTopicGroup(this);
	}
	
}
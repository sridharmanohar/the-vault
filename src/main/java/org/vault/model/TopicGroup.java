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

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 * 1. The persistent class for the topic_groups database table. This is a
 * critical table which basically contains the topic id and user id.
 * 
 * 2. Consider a system with 2 Users - A and B and 3 Topics - 1, 2 and 3. If
 * User A is associated with 2 instances of Topic 1 (for instance If topic 1 is
 * Email and User has multiple email addresses) then this table will have two
 * entries of Topic 1 associated with User A. And the primary key of this table
 * (i.e. topic group id) will be stored in the Topic Details table which will
 * tell us which Email key-Value pairs belong to which entry of Topic-User
 * group.
 * 
 * 3. This table contains the topic id and the user id.
 * 
 * 4. The overloaded constructor with 4 args. is used while making an update to
 * the user secrets (i.e. updates to topic and topic_details) where it becomes
 * mandatory to supply the details of topic_groups also because of the
 * relationship (even though there are no actual updates to the topic_groups
 * table).
 * 
 * 5. The overloaded constructor with 3 args. is used while creating a new user
 * secret, when it becomes necessary to make the new grouping mapping into this
 * table as well.
 * 
 * 6. The addTopicDetails method is added to solve the problem where
 * parent(topic_groups) and child(topic_details) are to be inserted in one go
 * i.e. first an entry is made into topic_groups and using this pk an entry is
 * made into topic_details. If you do not map the child instances to the parent,
 * while inserting into child, the pk of the parent will not be found. This
 * happens while adding a new User Secret where you will have to make an entry
 * into the topic_groups table and also into the topic_details table at the same
 * time and to make an insert into topic_details table you need the pk of the
 * topic_groups table which just inserted.
 * 
 */
@Entity
@Table(name = "topic_groups")
//@NamedQuery(name="TopicGroup.findAll", query="SELECT t FROM TopicGroup t")
public class TopicGroup implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	// bi-directional many-to-one association to TopicDetail
	@OneToMany(mappedBy = "topicGroup", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@Fetch(FetchMode.SUBSELECT)
	private List<TopicDetail> topicDetails = new ArrayList<TopicDetail>();

	// bi-directional many-to-one association to Topic
	@ManyToOne
	@JoinColumn(name = "topicid")
	private Topic topic = new Topic();

	// bi-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name = "userid")
	private User user = new User();

	public TopicGroup() {
	}

	public TopicGroup(int id, List<TopicDetail> topicDetails, int topicid, int userid) {
		System.out.println("id:" + id);
		System.out.println("td list:" + topicDetails.size());
		System.out.println("topic id" + topicid);
		System.out.println("user id" + userid);
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
	// are to be inserted in one go i.e. first an entry is made into topic_groups
	// and
	// using this pk an entry is made into topic_details.
	// If you do not map the child instances to the parent, while inserting into
	// child, the pk of the
	// parent will not be found.
	public void addTopicDetails(TopicDetail topicDetail) {
		topicDetails.add(topicDetail);
		topicDetail.setTopicGroup(this);
	}

}
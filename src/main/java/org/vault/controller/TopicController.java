package org.vault.controller;

import java.util.List;


import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.vault.model.Topic;
import org.vault.model.TopicTemplate;
import org.vault.repo.TopicRepo;
import org.vault.validations.TopicValidator;

@Controller
public class TopicController {

	private static final Logger LOGGER = LoggerFactory.getLogger(TopicController.class);
	
	/**
	 * Constants used as keys in model objects.
	 */
	private static final String TOPICS_LIST_MODEL_KEY = "alltopics";
	private static final String TOPIC_MODEL_KEY = "topic";
	private static final String DUPLICATE_MSG = "duplicate";
	private static final String SUCCESS_MODEL_KEY = "successMessage";

	/**
	 * Constants used for Views.
	 */
	private static final String SHOW_ALL_TOPICS_VIEW = "showAllVaultTopics.html";
	private static final String SECRET_TOPIC_EDIT_VIEW = "topicKeys";
	private static final String ADD_TOPIC_VIEW = "addTopic.html";

	@Autowired
	private TopicRepo topicRepo;

	/**
	 * 1. This is webdatabinder method. And gets called whenever the key associated
	 * with the @InitBinder is added to the model or whenever such a key is referred
	 * in the @ModelAttribute method param.
	 * 
	 * 2. Whenever it gets invoked, the validator is set.
	 * 
	 * 3. The validator set here will get called when the method with
	 * a @ModelAttribute param is also associated with a @Validated annot.
	 * 
	 * @param webDataBinder
	 */
	@InitBinder(TOPIC_MODEL_KEY)
	public void initTopicBinder(WebDataBinder webDataBinder) {
		LOGGER.debug("init binder topic");
		webDataBinder.setDisallowedFields("id");
		webDataBinder.setValidator(new TopicValidator());
	}

	/**
	 * 1. This method processes the request to show all vault topics.
	 * 
	 * 2. Fetches all vault topics from the db and the List<Topic>s to the model.
	 * And transfers the control to the desired view.
	 * 
	 * @param model
	 * @return
	 */
	@GetMapping("/showAllVaultTopics")
	public String showAllVaultTopics(Model model) {
		List<Topic> alltopics = this.topicRepo.findAll();
		model.addAttribute(TOPICS_LIST_MODEL_KEY, alltopics);
		return SHOW_ALL_TOPICS_VIEW;
	}

	/**
	 * 1. This method processes the request to view the edit page of Topic and it's
	 * Keys and also gives the ability to add new keys to the topic.
	 * 
	 * 2. Fetches the Topic and it's Templates (keys) from the db.
	 * 
	 * 3. Since what you get is a list and because of the internal query that is run
	 * by hibernate, if fetches duplicates, to overcome that, the entire list is
	 * dumped into Set to weed out dups. and then re-dump that unique Set into the
	 * List to make it available for index-based iteration in the view.
	 * 
	 * 4. Now, add the unique List of TopicTemplates to the already fetched topic
	 * object.
	 * 
	 * 5. Add the Topic object to the model and transfer the control to the desired
	 * view.
	 * 
	 * @param id
	 * @param model
	 * @return
	 */
	@GetMapping("/topicKeys/{topicId}")
	public String editTopic(@PathVariable(name = "topicId") int id, Model model) {
		Topic topic = this.topicRepo.findById(id);
		Set<TopicTemplate> tempSet = topic.getTopicTemplates().stream().collect(Collectors.toSet());
		List<TopicTemplate> tempList = tempSet.stream().collect(Collectors.toList());
		topic.setTopicTemplates(tempList);
		model.addAttribute(TOPIC_MODEL_KEY, topic);
		return SECRET_TOPIC_EDIT_VIEW;
	}

	/**
	 * 1. This method processes the request to edit the Secret (Topic) Name and
	 * their Keys (Templates).
	 * 
	 * 2. The field level validations are conducted by the corresponding validator.
	 * 
	 * 3. If there are any errors recorded by the validator then the control is
	 * transferred back to the view (along w/ the error messages - set by the
	 * validator).
	 * 
	 * 4. Every time the control is transferred back to the view, the Topic Id needs
	 * to be set, since this is not being mapped in the view and is only being sent
	 * as the request parameter, this needs to be explicitly set every time we send
	 * the control back to the view, only the topic id will be available in the view
	 * and will be again sent as the request param in the next submit.
	 * 
	 * 5a. If the request to add a new template param (key) is sent, then the
	 * HttpServletRequest param will contain the desired param and then a new
	 * TopicTemplate instance will be created. The existing Topic object (which is
	 * captured in the method params.) is set into this new TopicTemplate instance.
	 * This is done to ensure that this TopicTemplate belongs to an existing Topic.
	 * 
	 * 5b. After which, we fetch the List of TopicTemplates from the Topic object
	 * and add the newly created TopicTemplate instance to it. And, as always, we
	 * set the topic id before sending the control back to the view.
	 * 
	 * 6a. When the topic edit request is submitted, and if there are no validation
	 * errors, then, we check if there are any duplicate topics (secrets) existing
	 * with the same name and if the result is empty then we proceed with save db
	 * call to make an update to the topics table, and updates to the
	 * topic_templates table and an insert into the topic_templates table (if there
	 * is a new topic template (key) being added).
	 * 
	 * 6b. If there are any duplicates found, then we validate by comparing their
	 * topic id's if the found duplicate is the same as the one being modified, if
	 * yes, then we proceed with the db save and after successful completion we set
	 * the success message into the model and transfer the control to the view to
	 * display the message.
	 * 
	 * 6c. If the found duplicate is validated to be a different one from the one
	 * being updated and hence the vault system cannot at point of time contain two
	 * secret topics with the same name, we reject the save and set the
	 * bindingResult with the error message (to be mapped with the
	 * messages.properties file) and transfer the control back to the view to
	 * display the error message.
	 * 
	 * 7. The null mentioned in the end will not be reached in any case.
	 * 
	 * @param id
	 * @param topic
	 * @param bindingResult
	 * @param httpServletRequest
	 * @param model
	 * @return
	 */
	@PostMapping("/topicKeys/{topicId}")
	public String editTopicProcess(@PathVariable(name = "topicId") int id,
			@Validated @ModelAttribute(TOPIC_MODEL_KEY) Topic topic, BindingResult bindingResult,
			HttpServletRequest httpServletRequest, Model model) {
		if (bindingResult.hasErrors()) {
			LOGGER.debug("topic validator haserrors");
			topic.setId(id);
			return SECRET_TOPIC_EDIT_VIEW;
		}
		String addRowIndicator = httpServletRequest.getParameter("addRow");
		if (addRowIndicator != null) {
			TopicTemplate tt = new TopicTemplate();
			tt.setTopic(topic);
			topic.getTopicTemplates().add(tt);
			topic.setId(id);
			LOGGER.debug("new row added..");
			return SECRET_TOPIC_EDIT_VIEW;
		} else {
			LOGGER.debug("topic name" , topic.getTopicname());
			List<Topic> topics = this.topicRepo.findByTopicname(topic.getTopicname());
			if (topics.isEmpty()) {
				LOGGER.debug("no dups found.");
				topic.setId(id);
				this.topicRepo.save(topic);
				model.addAttribute(SUCCESS_MODEL_KEY, "Op. Successful");
				return SECRET_TOPIC_EDIT_VIEW;
			} else {
				LOGGER.debug("dup found, validating it's authenticity..");
				for (Topic t : topics) {
					if (t.getId() == id) {
						LOGGER.debug("this dup is same as the one i am currently editing, no probs.");
						topic.setId(id);
						this.topicRepo.save(topic);
						model.addAttribute(SUCCESS_MODEL_KEY, "Op. Successful");
						return SECRET_TOPIC_EDIT_VIEW;
					} else {
						LOGGER.debug("duplicate topic, reject");
						topic.setId(id);
						bindingResult.reject(DUPLICATE_MSG, new String[] { "Topic" }, null);
						return SECRET_TOPIC_EDIT_VIEW;
					}
				}

			}
			return null;
		}
	}

	/**
	 * 1. This method takes the request to add a new topic.
	 * 
	 * 2. It adds a new topic instance to the model and transfers the control to the
	 * desired view.
	 * 
	 * @param model
	 * @return
	 */
	@GetMapping("/addTopic")
	public String addTopic(Model model) {
		model.addAttribute(TOPIC_MODEL_KEY, new Topic());
		return ADD_TOPIC_VIEW;
	}

	/**
	 * 1. This method processes the request of adding new topic (secret) and it's
	 * templates (keys).
	 * 
	 * 2. The UI field level validations for this method are done by its
	 * corresponding validator.
	 * 
	 * 3. If there are any errors that are recorded by the validator then the
	 * control will be transferred back to the view along with the error messages.
	 * 
	 * 4. In case of an adding a new row (new key) request, a new TopicTemplate
	 * instance will be created and this will be added to the list of topictemplates
	 * within the Topic.
	 * 
	 * 5. If the request is for submitting the form for the creation of new topic
	 * (and topic templates), then a db call is made to determine if there are any
	 * existing topics with the same name. If found, the operation will be rejected
	 * with an bindingResult error and control transferred back to the view. If
	 * there are no such dups, then the db save call will happen and a new entry
	 * will be made in topics table and multiple entries will be made (depending on
	 * the no.of new topic templates) in topic_templates table.
	 * 
	 * @param topic
	 * @param bindingResult
	 * @param httpServletRequest
	 * @param model
	 * @return
	 */
	@PostMapping("/addTopic")
	public String processAddTopic(@Validated @ModelAttribute(name = TOPIC_MODEL_KEY) Topic topic,
			BindingResult bindingResult, HttpServletRequest httpServletRequest, Model model) {
		if (bindingResult.hasErrors())
			return ADD_TOPIC_VIEW;

		if (httpServletRequest.getParameter("addRow") != null) {
			TopicTemplate tt = new TopicTemplate();
			topic.getTopicTemplates().add(tt);
			return ADD_TOPIC_VIEW;
		} else {
			LOGGER.debug("topic name:" , topic.getTopicname());
			List<Topic> topics = this.topicRepo.findByTopicname(topic.getTopicname());
			if (topics.isEmpty()) {
				LOGGER.debug("no dups found.");
				Topic t1 = new Topic(topic.getTopicname(), topic.getTopicTemplates());
				this.topicRepo.save(t1);
				model.addAttribute(SUCCESS_MODEL_KEY, "Op. Successful.");
				return ADD_TOPIC_VIEW;
			} else {
				LOGGER.debug("dup found, reject.");
				bindingResult.reject(DUPLICATE_MSG, new String[] { "Topic" }, null);
				return ADD_TOPIC_VIEW;
			}
		}
	}

}

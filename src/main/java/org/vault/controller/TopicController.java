package org.vault.controller;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
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

@Controller
public class TopicController {

	private final TopicRepo topicRepo;

	@Autowired
	@Qualifier("topicValidator")
	private Validator validator;

	public TopicController(TopicRepo topicRepo) {
		this.topicRepo = topicRepo;
	}

	@InitBinder("topic1")
	public void initTopicBinder(WebDataBinder webDataBinder) {
		System.out.println("init binder");
		webDataBinder.setDisallowedFields("id");
		webDataBinder.setValidator(validator);
	}

	@GetMapping("/showAllVaultTopics")
	public String adminDisplayTopics(Model model) {
		List<Topic> alltopics = this.topicRepo.findAll();
		model.addAttribute("alltopics", alltopics);
		return "showAllVaultTopics";
	}

	@GetMapping("/topicKeys/{topicId}")
	public String editTopic(@PathVariable(name = "topicId") int id, Model model) {
		Topic topic = this.topicRepo.findById(id);
		Set<TopicTemplate> tempSet = topic.getTopicTemplates().stream().collect(Collectors.toSet());
		List<TopicTemplate> tempList = tempSet.stream().collect(Collectors.toList());
		topic.setTopicTemplates(tempList);
		model.addAttribute("topic1", topic);
		return "topicKeys";
	}

	@PostMapping("/topicKeys/{topicId}")
	public String editTopicProcess(@PathVariable(name = "topicId") int id,
			@Validated @ModelAttribute("topic1") Topic topic, BindingResult bindingResult,
			HttpServletRequest httpServletRequest) {
		if (bindingResult.hasErrors()) {
			System.out.println("inside haserrors");
			return "topicKeys";
		}
		String addRowIndicator = httpServletRequest.getParameter("addRow");
		if (addRowIndicator != null) {
			System.out.println("add row");
			TopicTemplate tt = new TopicTemplate();
			tt.setTopic(topic);
			topic.getTopicTemplates().add(tt);
			topic.setId(id);
			System.out.println("new row added");
			return "topicKeys";
		} else {
			System.out.println("topic name" + topic.getTopicname());
			List<Topic> topics = this.topicRepo.findByTopicname(topic.getTopicname());
			if (topics.isEmpty()) {
				System.out.println("setting id");
				topic.setId(id);
				this.topicRepo.save(topic);
				return "commonDashboard";
			} else {
				System.out.println("duplicate topic");
				bindingResult.reject("duplicate", "Topic already exists");
				return "topicKeys";
			}
		}
	}

	@GetMapping("/addTopic")
	public String addTopic(Model model) {
		model.addAttribute("newtopic", new Topic());
		return "addTopic";
	}

	@PostMapping("/addTopic")
	public String processAddTopic(@Validated @ModelAttribute(name = "newtopic") Topic topic,
			BindingResult bindingResult, HttpServletRequest httpServletRequest) {
		if (bindingResult.hasErrors())
			return "addTopic";

		if (httpServletRequest.getParameter("addRow") != null) {
			System.out.println("about to add row");
			TopicTemplate tt = new TopicTemplate();
			topic.getTopicTemplates().add(tt);
			return "addTopic";
		} else {
			System.out.println("topic name:" + topic.getTopicname());
			List<Topic> topics = this.topicRepo.findByTopicname(topic.getTopicname());
			if (topics.isEmpty()) {
				System.out.println("no dups found.");
				System.out.println("about to call const.");
				Topic t1 = new Topic(topic.getTopicname(), topic.getTopicTemplates());
				System.out.println("about to call save...");
				this.topicRepo.save(t1);
				return "commonDashboard";
			} else {
				bindingResult.reject("duplicate", "Topic already exists");
				return "addTopic";
			}
		}
	}



}

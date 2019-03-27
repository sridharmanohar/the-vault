package org.vault.controller;

import java.util.ArrayList;
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
import org.vault.model.TopicDetail;
import org.vault.model.TopicGroup;
import org.vault.model.TopicTemplate;
import org.vault.model.User;
import org.vault.repo.TopicGroupRepo;
import org.vault.repo.TopicRepo;
import org.vault.repo.UserRepo;
import org.vault.validations.UserValidator;

@Controller
public class UserController {
	@Autowired
	private UserRepo userrepo;

	@Autowired
	private TopicGroupRepo topicGroupRepo; 

	@Autowired
	private TopicRepo topicRepo;
	
//	@Autowired
//	@Qualifier("userValidator")
//	private Validator validator;
	
	
	@InitBinder("user")
	private void initUserBinder(WebDataBinder webDataBinder) {
		webDataBinder.setDisallowedFields("id");
//		webDataBinder.setValidator(validator);
		webDataBinder.setValidator(new UserValidator());
	}

	@GetMapping("/showAllVaultUsers")
	public String adminUserDisplay(Model model) {
		System.out.println("in adminUserDisplay COntroller");
		List<User> user = this.userrepo.findAll();
		model.addAttribute("allusers", user);
		return "showAllVaultUsers.html";
	}
	
	@GetMapping("/addUser")
	public String addUser(User user) {
		return "addUser";
	}
	
	@PostMapping("/addUser")
	public String addUserProcess(@Validated User user, BindingResult bindingResult)  {
		if(bindingResult.hasErrors())
			return "addUser.html";
		System.out.println("Inside of Add User...and params are:"+user.getFirstname()+" "+user.getLastname());
		List<User> users = this.userrepo.findByFirstnameAndLastname(user.getFirstname(), user.getLastname());
		System.out.println("Call to db completed to find out duplicates.");
		if(users.isEmpty()) {
			this.userrepo.save(user);
			return "commonDashboard";
		}else {
			bindingResult.reject("duplicate", "user already exists.");
			return "addUser.html";
		}
	}

	@GetMapping("/userDetails/{userId}")
	public String showUserDetails(@PathVariable(name="userId") int id, Model model) {
		System.out.println("edit user");
		User user = this.userrepo.findById(id);
		model.addAttribute("user", user);
		return "userDetails.html";
	}

	@PostMapping("/userDetails/{userId}")
	public String processUserDetails(@PathVariable(name="userId") int id, @Validated @ModelAttribute("user") User user, BindingResult bindingResult) {
		if(bindingResult.hasErrors())
			return "userDetails.html";
		System.out.println("Inside of Edit User...and params are:"+user.getFirstname()+" "+user.getLastname());
		System.out.println("user id param:"+id);
		System.out.println("user id obj:"+user.getId());
		List<User> users = this.userrepo.findByFirstnameAndLastname(user.getFirstname(), user.getLastname());
		System.out.println("Call to db completed to find out duplicates.");
		if(users.isEmpty()) {
			//If we don't do this to set id and directly proceed with the save then a new instance will be created.
			//For jpa provider (hibernate) to know that this is an existing instance set the id.
			user.setId(id);
			this.userrepo.save(user);
			return "commonDashboard";
		}else {
			bindingResult.reject("duplicate", "user already exists.");
			return "userDetails.html";
		}
	}

	@GetMapping("/userSecrets/{userId}")
	public String showUserSecrets(@PathVariable("userId") int userid, Model model) {
		System.out.println("user id is:"+userid);
		User user = this.userrepo.findById(userid);
		if(user != null) {
			System.out.println("User Name:"+user.getFirstname());
			Set<TopicGroup> setTopicGroup = user.getTopicGroups().stream().collect(Collectors.toSet());
			List<TopicGroup> listTopicGroup = setTopicGroup.stream().collect(Collectors.toList());
			user.setTopicGroups(listTopicGroup);
//			for(TopicGroup topicGroup : user.getTopicGroups()) {
//				System.out.println("Topic Name:"+topicGroup.getTopic().getTopicname());
//				for(TopicDetail topicDetail : topicGroup.getTopicDetails()) {
//					System.out.println("PropName:"+topicDetail.getTopicTemplate().getPropname()+", Propvalue:"+topicDetail.getPropvalue());
//				}
//			}
			model.addAttribute("userMappings", user);
			return "userSecrets.html";
		}else {
			return "commonDashboard";
		}
	}
	
	@PostMapping("/userSecrets")
	public String processUserSecrets(@ModelAttribute("userMappings") User user) {
		List<TopicDetail> listTD = new ArrayList<TopicDetail>();
		System.out.println("size of tg:"+ user.getTopicGroups().size());
		System.out.println("about to being looping...");
		for(TopicGroup tg : user.getTopicGroups()) {
			for(TopicDetail td : tg.getTopicDetails()) {
			    System.out.println("td id:"+td.getId());		
				System.out.println("td propval:"+td.getPropvalue());
				System.out.println("td tg id:"+td.getTopicGroup().getId());
				System.out.println("td tt id:"+td.getTopicTemplate().getId());
				TopicDetail tdObj = new TopicDetail(td.getId(), td.getPropvalue(), td.getTopicGroup().getId(), td.getTopicTemplate().getId());
				System.out.println("adding td into list");
				listTD.add(tdObj);
			}
			System.out.println("tg id:"+ tg.getId());
			System.out.println("size of list:"+ listTD.size());
			System.out.println("tg tp id:"+ tg.getTopic().getId());
			System.out.println("tg usr id:"+ user.getId());
			long start = System.currentTimeMillis();
			System.out.println("about to call db save....");
			this.topicGroupRepo.save(new TopicGroup(tg.getId(), listTD, tg.getTopic().getId(), user.getId()));
			long end = System.currentTimeMillis();
			System.out.println("total time taken to save:"+(end-start));
		}
		return "commonDashboard";
	}
	
	@GetMapping("/addSecret/{userId}")
	public String showNewMappingsTemp(@PathVariable("userId") int id, Model model) {
		List<Topic> listTopics = this.topicRepo.findAll();
		System.out.println("user id from req:"+ id);
		model.addAttribute("topicList", listTopics);
		model.addAttribute("newTopic", new Topic());
		model.addAttribute("forUser", new User(id));
		return "addSecret";
	}
	
	@PostMapping("/addSecret/{userId}")
	public String userNewMappingProcess(@PathVariable("userId") int id,@ModelAttribute("newTopic") Topic newtopic, HttpServletRequest httpServletRequest, Model model) {
		System.out.println("inside mapping");
		System.out.println("fetched in path:"+id);
		if(httpServletRequest.getParameter("viewTemplate") != null) {
			List<Topic> listTopics = this.topicRepo.findAll();
			System.out.println("size of listopics:"+ listTopics.size());
			model.addAttribute("topicList", listTopics);
			model.addAttribute("forUser", new User(id));
			System.out.println("inside mapping1");
			return "addSecret";
		}else {
			System.out.println("topic id:"+newtopic.getId());
			System.out.println("user id is:"+id);
			TopicGroup  tg = new TopicGroup(newtopic.getId(), id);
			System.out.println("tt size:"+ newtopic.getTopicTemplates().size());
			for(TopicTemplate tt : newtopic.getTopicTemplates()) {
				System.out.println("td size:"+ tt.getTopicDetails().size());
				for(TopicDetail td : tt.getTopicDetails()) {
					TopicDetail td1 = new TopicDetail(td.getPropvalue(), td.getTopicTemplate().getId());
					tg.addTopicDetails(td1);
					System.out.println("topic temp id:"+td.getTopicTemplate().getId());
					System.out.println("propvals"+td.getPropvalue());
				}
			}
			System.out.println("calling save...");
			this.topicGroupRepo.save(tg);
		}
		return "commonDashboard";
	}
	
	@GetMapping("/")
	public String showCommonDashboard() {
		return "commonDashboard.html";
	}
}

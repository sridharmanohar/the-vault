package org.vault.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

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
import org.vault.model.TopicDetail;
import org.vault.model.TopicGroup;
import org.vault.model.TopicTemplate;
import org.vault.model.User;
import org.vault.repo.TopicGroupRepo;
import org.vault.repo.TopicRepo;
import org.vault.repo.UserRepo;
import org.vault.validations.CustomValidationUtil;
import org.vault.validations.UserValidator;

@Controller
public class UserController {

	
	private static final String DUPLICATE_MSG = "duplicate";
	
	// Views
	private static final String USER_DETAILS_VIEW = "userDetails.html";
	private static final String DASHBOARD_VIEW = "commonDashboard.html";
	private static final String USER_SECRETS_VIEW = "userSecrets.html";
	private static final String ADD_SECRET_VIEW = "addSecret";
	
	// Model Attribute Keys
	private static final String USER_SECRETS_MODEL_KEY = "userMappings";
	private static final String USER_MODEL_KEY = "user";
	private static final String SUCCESS_MODEL_KEY = "successMessage";
	
	private static final int MIN_LENGTH = 3;
	private static final int MAX_LENGTH = 90;
	private static final String SECRET_VAL_SIZE_MSG = "All Secrets";
	private static final String FIELD_SIZE_MSG_KEY = "field.size";

	@Autowired
	private UserRepo userrepo;

	@Autowired
	private TopicGroupRepo topicGroupRepo;

	@Autowired
	private TopicRepo topicRepo;

	@InitBinder(USER_MODEL_KEY)
	private void initUserBinder(WebDataBinder webDataBinder) {
		System.out.println("user binder inoked");
		webDataBinder.setDisallowedFields("id");
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
	public String addUserProcess(@Validated User user, BindingResult bindingResult) {
		if (bindingResult.hasErrors())
			return "addUser.html";
		System.out.println("Inside of Add User...and params are:" + user.getFirstname() + " " + user.getLastname());
		List<User> users = this.userrepo.findByFirstnameAndLastname(user.getFirstname(), user.getLastname());
		System.out.println("Call to db completed to find out duplicates.");
		if (users.isEmpty()) {
			this.userrepo.save(user);
			return "commonDashboard";
		} else {
			bindingResult.reject("duplicate", "user already exists.");
			return "addUser.html";
		}
	}

	@GetMapping("/userDetails/{userId}")
	public String showUserDetails(@PathVariable(name = "userId") int id, Model model) {
		System.out.println("edit user");
		User user = this.userrepo.findById(id);
		model.addAttribute(USER_MODEL_KEY, user);
		return USER_DETAILS_VIEW;
	}

	@PostMapping("/userDetails/{userId}")
	public String processUserDetails(@PathVariable(name = "userId") int id,
			@Validated @ModelAttribute(name = USER_MODEL_KEY) User user, BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors())
			return USER_DETAILS_VIEW;
		System.out.println("Inside of Edit User...and params are:" + user.getFirstname() + " " + user.getLastname());
		System.out.println("user id param:" + id);
		System.out.println("user id obj:" + user.getId());
		List<User> users = this.userrepo.findByFirstnameAndLastname(user.getFirstname(), user.getLastname());
		System.out.println("Call to db completed to find out duplicates.");
		if (users.isEmpty()) {
			// If we don't set id and directly proceed with the save then a new instance will be created.
			user.setId(id);
			this.userrepo.save(user);
			model.addAttribute(SUCCESS_MODEL_KEY, "Op. Successful.");
			return USER_DETAILS_VIEW;
		} else {
			bindingResult.reject(DUPLICATE_MSG, new String[] { "User" }, null);
			return USER_DETAILS_VIEW;
		}
	}

	@GetMapping("/userSecrets/{userId}")
	public String showUserSecrets(@PathVariable("userId") int userid, Model model) {
		System.out.println("user id is:" + userid);
		User user = this.userrepo.findById(userid);
		System.out.println("User Name:" + user.getFirstname());
		// Getting duplicates because of the internal query it is forming while fetch.
		// Hence used Set to weed out dups.
		Set<TopicGroup> setTopicGroup = user.getTopicGroups().stream().collect(Collectors.toSet());
		// Dumping the Set contents into List in order to enable ordering, which is
		// necessary when I play with this List in thymeleaf.
		List<TopicGroup> listTopicGroup = setTopicGroup.stream().collect(Collectors.toList());
		user.setTopicGroups(listTopicGroup);
		model.addAttribute(USER_SECRETS_MODEL_KEY, user);
		return USER_SECRETS_VIEW;
	}

	@PostMapping("/userSecrets/{userId}")
	public String processUserSecrets(@ModelAttribute(name = USER_SECRETS_MODEL_KEY) User user, BindingResult bindingResult, Model model) {
		List<TopicDetail> listTD = new ArrayList<TopicDetail>();
		System.out.println("size of tg:" + user.getTopicGroups().size());

		for (TopicGroup tg : user.getTopicGroups()) {
			for (TopicDetail td : tg.getTopicDetails()) {

				System.out.println("td id:" + td.getId());
				System.out.println("td propval:" + td.getPropvalue().length());
				System.out.println("td tg id:" + td.getTopicGroup().getId());
				System.out.println("td tt id:" + td.getTopicTemplate().getId());

				// Unable to do field level validation because the object supplied to model is
				// different hence the validator that will be called will also not be
				// supportive of the required i.e. in this case, a TopicDetail object.
				boolean fieldLength = CustomValidationUtil.isFieldLengthValid(td.getPropvalue().length(), 3, 90);
				System.out.println("field len:" + fieldLength);
				if (!fieldLength) {
					bindingResult.reject(FIELD_SIZE_MSG_KEY, new String[] { SECRET_VAL_SIZE_MSG,
							Integer.toString(MIN_LENGTH), Integer.toString(MAX_LENGTH) }, null);
					return USER_SECRETS_VIEW;
				}

				TopicDetail tdObj = new TopicDetail(td.getId(), td.getPropvalue(), td.getTopicGroup().getId(),
						td.getTopicTemplate().getId());
				System.out.println("adding td into list");
				listTD.add(tdObj);
			}

			System.out.println("tg id:" + tg.getId());
			System.out.println("size of list:" + listTD.size());
			System.out.println("tg tp id:" + tg.getTopic().getId());
			System.out.println("tg usr id:" + user.getId());
			long start = System.currentTimeMillis();
			System.out.println("about to call db save....");
			this.topicGroupRepo.save(new TopicGroup(tg.getId(), listTD, tg.getTopic().getId(), user.getId()));
			long end = System.currentTimeMillis();
			System.out.println("total time taken to save:" + (end - start));
		}
		model.addAttribute(SUCCESS_MODEL_KEY, "Op. Successful.");
		return USER_SECRETS_VIEW;
	}

	@GetMapping("/addSecret/{userId}")
	public String showAddSecret(@PathVariable("userId") int id, Model model) {
		List<Topic> listTopics = this.topicRepo.findAll();
		System.out.println("user id from req:" + id);
		// to show secret topics in the drop-down.
		model.addAttribute("topicList", listTopics);
		model.addAttribute("newTopic", new Topic());
		model.addAttribute("forUser", new User(id));
		return ADD_SECRET_VIEW;
	}

	@PostMapping("/addSecret/{userId}")
	public String userNewMappingProcess(@PathVariable("userId") int id, @ModelAttribute("newTopic") Topic newtopic,
			HttpServletRequest httpServletRequest, Model model) {
		System.out.println("inside mapping");
		System.out.println("fetched in path:" + id);
		
		// This is true when user selects a template to view.
		// Will be null when the user submits the form with secret key-value details.
		if (httpServletRequest.getParameter("viewTemplate") != null) {
			List<Topic> listTopics = this.topicRepo.findAll();
			System.out.println("size of listopics:" + listTopics.size());
			// setting the attributes values again to make them available in the view again.
			model.addAttribute("topicList", listTopics);
			model.addAttribute("forUser", new User(id));
			System.out.println("inside mapping1");
			return "addSecret";
		} else {
			System.out.println("topic id:" + newtopic.getId());
			System.out.println("user id is:" + id);
			TopicGroup tg = new TopicGroup(newtopic.getId(), id);
			System.out.println("tt size:" + newtopic.getTopicTemplates().size());
			for (TopicTemplate tt : newtopic.getTopicTemplates()) {
				System.out.println("td size:" + tt.getTopicDetails().size());
				for (TopicDetail td : tt.getTopicDetails()) {
					TopicDetail td1 = new TopicDetail(td.getPropvalue(), td.getTopicTemplate().getId());
					tg.addTopicDetails(td1);
					System.out.println("topic temp id:" + td.getTopicTemplate().getId());
					System.out.println("propvals" + td.getPropvalue());
				}
			}
			System.out.println("calling save...");
			this.topicGroupRepo.save(tg);
		}
		return "commonDashboard";
	}

	@GetMapping("/")
	public String showCommonDashboard() {
		return DASHBOARD_VIEW;
	}
}

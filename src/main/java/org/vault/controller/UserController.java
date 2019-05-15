package org.vault.controller;

import java.util.ArrayList;
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
import org.vault.model.TopicDetail;
import org.vault.model.TopicGroup;
import org.vault.model.TopicTemplate;
import org.vault.model.User;
import org.vault.repo.TopicGroupRepo;
import org.vault.repo.TopicRepo;
import org.vault.repo.UserRepo;
import org.vault.validations.CustomValidationUtil;
import org.vault.validations.TopicValidator;
import org.vault.validations.UserValidator;

/**
 * @author sridhar
 *
 */
@Controller
public class UserController {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
	
	/**
	 * Constants used for Views.
	 */
	private static final String USER_DETAILS_VIEW = "userDetails.html";
	private static final String DASHBOARD_VIEW = "commonDashboard.html";
	private static final String USER_SECRETS_VIEW = "userSecrets.html";
	private static final String ADD_SECRET_VIEW = "addSecret.html";
	private static final String SHOW_ALL_VAULT_USERS_VIEW = "showAllVaultUsers.html";
	private static final String ADD_USER_VIEW = "addUser.html";

	/**
	 * Constants used as keys in model objects.
	 */
	private static final String USER_SECRETS_MODEL_KEY = "userMappings";
	private static final String USER_OBJ_MODEL_KEY = "user";
	private static final String SUCCESS_MODEL_KEY = "successMessage";
	private static final String TOPIC_LIST_MODEL_KEY = "topicList";
	private static final String TOPIC_OBJ_MODEL_KEY = "newTopic";
	private static final String DUPLICATE_MSG = "duplicate";

	/**
	 * Constants used for setting keys in bindingResult or to call other validation
	 * util methods with some fixed parameter values.
	 */
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

	/**
	 * 1. This gets invoked whenever an attribute into the model with the key as
	 * mentioned in the @InitBinder.
	 * 
	 * 2. Also, gets invoked whenever there is an method param with
	 * an @ModelAttribute with the name same as the name given in the @InitBinder.
	 * 
	 * 3. The validator set here is called when the method param with the
	 * matching @ModelAttributed is found with an @Validated annot.
	 * 
	 * @param webDataBinder
	 */
	@InitBinder(USER_OBJ_MODEL_KEY)
	private void initUserBinder(WebDataBinder webDataBinder) {
		LOGGER.debug("user binder inoked");
		webDataBinder.setDisallowedFields("id");
		webDataBinder.setValidator(new UserValidator());
	}

	/**
	 * 1. This gets invoked whenever an attribute into the model with the key as
	 * mentioned in the @InitBinder.
	 * 
	 * 2. Also, gets invoked whenever there is an method param with
	 * an @ModelAttribute with the name same as the name given in the @InitBinder.
	 * 
	 * 3. The validator set here is called when the method param with the
	 * matching @ModelAttributed is found with an @Validated annot.
	 * 
	 * @param webDataBinder
	 */
	@InitBinder(TOPIC_OBJ_MODEL_KEY)
	private void initTopicBinder(WebDataBinder webDataBinder) {
		LOGGER.debug("topic binder inoked");
		webDataBinder.setValidator(new TopicValidator());
	}

	/**
	 * 1. This methods shows all vault users.
	 * 
	 * 2. Fetches all the users from the repo. and adds the List<Users> into the
	 * model.
	 * 
	 * 3. Transfers the control to the desired view - ShowAllVaultUsers view.
	 * 
	 * @param model
	 * @return
	 */
	@GetMapping("/showAllVaultUsers")
	public String showAllVaultUsers(Model model) {
		List<User> user = this.userrepo.findAll();
		model.addAttribute("allusers", user);
		return SHOW_ALL_VAULT_USERS_VIEW;
	}

	/**
	 * 1. This method receives the Add new User request.
	 * 
	 * 2. Adds a new User object to the model and directs the control to the desired
	 * view.
	 *
	 * @param user
	 * @return
	 */
	@GetMapping("/addUser")
	public String addUser(Model model) {
		model.addAttribute(USER_OBJ_MODEL_KEY, new User());
		return ADD_USER_VIEW;
	}

	/**
	 * 1. This method processes the Add new User request.
	 * 
	 * 2. Validations are performed by the Validator and in case of any errors the
	 * control is transferred back to the view with the messages.
	 * 
	 * 3. If there are no errors found by the validator, then a db call is made to
	 * find any users with the same first and last names. If exists, then an error
	 * is set in the bindingResult and the control transferred backt to the view. If
	 * no, then the new user is saved and the success message is added to the model
	 * and sent back to the view to display the success message.
	 * 
	 * @param user
	 * @param bindingResult
	 * @param model
	 * @return
	 */
	@PostMapping("/addUser")
	public String addUserProcess(@Validated @ModelAttribute(name = USER_OBJ_MODEL_KEY) User user,
			BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors())
			return ADD_USER_VIEW;
		LOGGER.debug("Inside of {} and Firstname is {} and Lastname is {}", "addUserProcess", user.getFirstname(), user.getLastname());
		List<User> users = this.userrepo.findByFirstnameAndLastname(user.getFirstname(), user.getLastname());
		if (users.isEmpty()) {
			this.userrepo.save(user);
			LOGGER.info("User Saved Successfully.");
			model.addAttribute(SUCCESS_MODEL_KEY, "Op. Successful");
			return ADD_USER_VIEW;
		} else {
			bindingResult.reject(DUPLICATE_MSG, new String[] { "User" }, null);
			return ADD_USER_VIEW;
		}
	}

	/**
	 * 1. This method receives the request to show user details page for the
	 * provided user id.
	 * 
	 * 2. Fetches all user details from the vault repo. for the given user id.
	 * 
	 * 3. Adds the user object to the model.
	 * 
	 * 4. Directs control to the desired view.
	 * 
	 * @param id
	 * @param model
	 * @return
	 */
	@GetMapping("/userDetails/{userId}")
	public String showUserDetails(@PathVariable(name = "userId") int id, Model model) {
		LOGGER.debug("edit user");
		User user = this.userrepo.findById(id);
		model.addAttribute(USER_OBJ_MODEL_KEY, user);
		return USER_DETAILS_VIEW;
	}

	/**
	 * 1. This method processes the User details edited.
	 * 
	 * 2. Validations for this method are performed by the User validator. Hence,
	 * the use of @Validated annot.
	 * 
	 * 3. If there are any errors in the binding result, control is transferred back
	 * to the view with error message. Error message is set in the validator.
	 * 
	 * 4. Before we save the updated user details, a db call is made to find any
	 * users with given first name and last name. This is to ensure, no two users
	 * end up having the same name in the repo.
	 * 
	 * 5. In case of duplicate Users, a corresponding error message is set into the
	 * binding result and control transferred back to the view.
	 * 
	 * 5. While saving the updated user details to the repo., we ensure we set the
	 * user id into User object, this is to make sure a new user entry is not
	 * created and hibernate understands this as an update rather than an insert.
	 * 
	 * @param id
	 * @param user
	 * @param bindingResult
	 * @param model
	 * @return
	 */
	@PostMapping("/userDetails/{userId}")
	public String processUserDetails(@PathVariable(name = "userId") int id,
			@Validated @ModelAttribute(name = USER_OBJ_MODEL_KEY) User user, BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors())
			return USER_DETAILS_VIEW;
		List<User> users = this.userrepo.findByFirstnameAndLastname(user.getFirstname(), user.getLastname());
		LOGGER.debug("Call to db completed to find out duplicates.");
		if (users.isEmpty()) {
			user.setId(id);
			this.userrepo.save(user);
			LOGGER.info("User details updated successfully.");
			model.addAttribute(SUCCESS_MODEL_KEY, "Op. Successful.");
			return USER_DETAILS_VIEW;
		} else {
			bindingResult.reject(DUPLICATE_MSG, new String[] { "User" }, null);
			return USER_DETAILS_VIEW;
		}
	}

	/**
	 * 
	 * 1. The TopicGroups fetched had duplicates because User has a
	 * List<TopicGroups> and also because of the kind of join query it formed, dups
	 * were fetched. <br/>
	 * <br/>
	 * 
	 * 2. To overcome that, had to dump the List<TopicGroup>s into Set, to weed out
	 * dups. <br/>
	 * <br/>
	 * 
	 * 3. And then again, dump the unique set of TopicGroups into List. <br/>
	 * <br/>
	 * 
	 * 4. List is required for index-based iteration in view, not possible to do so
	 * with Set.
	 * 
	 * @param userid
	 * @param model
	 * @return
	 */
	@GetMapping("/userSecrets/{userId}")
	public String showUserSecrets(@PathVariable("userId") int userid, Model model) {
		User user = this.userrepo.findById(userid);
		Set<TopicGroup> setTopicGroup = user.getTopicGroups().stream().collect(Collectors.toSet());
		List<TopicGroup> listTopicGroup = setTopicGroup.stream().collect(Collectors.toList());
		user.setTopicGroups(listTopicGroup);
		model.addAttribute(USER_SECRETS_MODEL_KEY, user);
		return USER_SECRETS_VIEW;
	}

	/**
	 * 1. This method processes the User Secrets i.e. updates to secret values.
	 * 
	 * 2. Was Unable to do field level validation because the object supplied to
	 * model is different hence the validator that will be called will also not be
	 * supportive of the required i.e. in this case, a TopicDetail object.
	 * 
	 * 3. Init. TopicDetails with the required info.
	 * 
	 * 4. Added all TopicDetails to the List.
	 * 
	 * 5. Saved the TopicGroup object and it's related List of TopicDetails. To
	 * ensure a new entry is not created, TopicGroup Id is supplied. Now only
	 * updates will be made into topic_details table.
	 * 
	 * @param user
	 * @param bindingResult
	 * @param model
	 * @return
	 */
	@PostMapping("/userSecrets/{userId}")
	public String processUserSecrets(@ModelAttribute(name = USER_SECRETS_MODEL_KEY) User user,
			BindingResult bindingResult, Model model) {
		List<TopicDetail> listTD = new ArrayList<TopicDetail>();
		LOGGER.debug("size of tg:", user.getTopicGroups().size());

		for (TopicGroup tg : user.getTopicGroups()) {
			for (TopicDetail td : tg.getTopicDetails()) {
				System.out.println("td id:" + td.getId());
				boolean fieldLength = CustomValidationUtil.isFieldLengthValid(td.getPropvalue().length(), MIN_LENGTH,
						MAX_LENGTH);
				LOGGER.debug("field len:", fieldLength);
				if (!fieldLength) {
					bindingResult.reject(FIELD_SIZE_MSG_KEY, new String[] { SECRET_VAL_SIZE_MSG,
							Integer.toString(MIN_LENGTH), Integer.toString(MAX_LENGTH) }, null);
					return USER_SECRETS_VIEW;
				}
				TopicDetail tdObj = new TopicDetail(td.getId(), td.getPropvalue(), td.getTopicGroup().getId(),
						td.getTopicTemplate().getId());
				LOGGER.debug("adding td into list");
				listTD.add(tdObj);
			}

			LOGGER.debug("tg id:", tg.getId());
			LOGGER.debug("size of list:", listTD.size());
			long start = System.currentTimeMillis();
			this.topicGroupRepo.save(new TopicGroup(tg.getId(), listTD, tg.getTopic().getId(), user.getId()));
			long end = System.currentTimeMillis();
			LOGGER.debug("total time taken to save:" + (end - start));
		}
		model.addAttribute(SUCCESS_MODEL_KEY, "Op. Successful.");
		return USER_SECRETS_VIEW;
	}

	/**
	 * 1. This method receives the request from the view to Add a new User Secret.
	 * 
	 * 2. Fetches the List of Secret Topics available in the Vault repo.
	 * 
	 * 3. This listofTopics is added to the model for iteration in the view,
	 * 
	 * 4. A new Topic object is also attached to the model so that the view can map
	 * the listofTopics values to the model.
	 * 
	 * 5. The User id for whom the request is being processed is also added to the
	 * model as a model attribute.
	 * 
	 * 6. This method then transfers the control to the view - Add Secret View.
	 * 
	 * @param id
	 * @param model
	 * @return
	 */
	@GetMapping("/addSecret/{userId}")
	public String showAddSecret(@PathVariable("userId") int id, Model model) {
		List<Topic> listTopics = this.topicRepo.findAll();
		model.addAttribute(TOPIC_LIST_MODEL_KEY, listTopics);
		model.addAttribute(TOPIC_OBJ_MODEL_KEY, new Topic());
		model.addAttribute(USER_OBJ_MODEL_KEY, new User(id));
		return ADD_SECRET_VIEW;
	}

	/**
	 * 1. This method processes the Add a new User Secret request.
	 * 
	 * 2. A db call is made to find the list of all topics and then that is added to
	 * the model along with the user id. This is a duplicate effort (already done in
	 * the corresponding @GetRequest but had to be done here again because when the
	 * user selects a Topic in the view and submits the form to view its template,
	 * then while sending back the request back to the view (from the controller),
	 * If I am not re-setting all this required data, then this won't be available
	 * in the view. This is also required in case of binding errors, when the
	 * controls rocks back-and-forth i.e. between the view and the controller. Have
	 * to find a more efficient way of doing this.
	 * 
	 * 3. When the user submits the form with the request to view the selected topic
	 * template, then the HttpServletRequest will have the mentioned param.
	 * 
	 * 4. Since in order to add a new secret, we need to make an entry into the
	 * topic_groups and topic_details table, we need to populate the corresponding
	 * objects with the required info.
	 * 
	 * 5. TopicGroup is init. with the Topic Id and the User Id.
	 * 
	 * 6. TopicDetail is init. with the propvalue (secret) and the Topic Template Id
	 * (secret key id).
	 * 
	 * 7. The list of all such TopicDetails (secrets) is added into the TopicGroup
	 * and then it is saved.
	 * 
	 * 8. Validation to ensure the propvalues (secrets) are all within the valid
	 * range is also done within this method only. Field level validation could not
	 * be done because the desired object was not added to the model and hence not
	 * available to the Validator.
	 * 
	 * @param id
	 * @param newtopic
	 * @param bindingResult
	 * @param httpServletRequest
	 * @param model
	 * @return
	 */
	@PostMapping("/addSecret/{userId}")
	public String processAddSecret(@PathVariable("userId") int id,
			@ModelAttribute(name = TOPIC_OBJ_MODEL_KEY) Topic newtopic, BindingResult bindingResult,
			HttpServletRequest httpServletRequest, Model model) {

		List<Topic> listTopics = this.topicRepo.findAll();
		model.addAttribute(TOPIC_LIST_MODEL_KEY, listTopics);
		model.addAttribute(USER_OBJ_MODEL_KEY, new User(id));

		if (httpServletRequest.getParameter("viewTemplate") != null) {
			return ADD_SECRET_VIEW;
		} else {

			TopicGroup tg = new TopicGroup(newtopic.getId(), id);

			for (TopicTemplate tt : newtopic.getTopicTemplates()) {
				for (TopicDetail td : tt.getTopicDetails()) {

					boolean fieldLength = CustomValidationUtil.isFieldLengthValid(td.getPropvalue().length(),
							MIN_LENGTH, MAX_LENGTH);
					if (!fieldLength) {
						bindingResult.reject(FIELD_SIZE_MSG_KEY, new String[] { SECRET_VAL_SIZE_MSG,
								Integer.toString(MIN_LENGTH), Integer.toString(MAX_LENGTH) }, null);
						return ADD_SECRET_VIEW;
					}

					TopicDetail td1 = new TopicDetail(td.getPropvalue(), td.getTopicTemplate().getId());
					tg.addTopicDetails(td1);
				}
			}
			this.topicGroupRepo.save(tg);
		}
		model.addAttribute(SUCCESS_MODEL_KEY, "Op. Successful.");
		return ADD_SECRET_VIEW;
	}

	/**
	 * 1. This is where the first request from the view comes to - the controller
	 * sends the request to the Dashboard view.
	 * 
	 * @return
	 */
	@GetMapping("/")
	public String showCommonDashboard() {
		return DASHBOARD_VIEW;
	}
}

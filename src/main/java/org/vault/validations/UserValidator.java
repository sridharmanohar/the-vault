package org.vault.validations;

import org.slf4j.Logger;


import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.vault.model.User;

/**
 * This class is an implementation of Spring Validator.
 * 
 * @author sridhar
 *
 */
@Component
public class UserValidator implements Validator {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserValidator.class);
	
	private static final int MIN_LENGTH = 2;
	private static final int MAX_LENGTH = 15;
	private static final String FIRST_NAME_FIELD = "firstname";
	private static final String LAST_NAME_FIELD = "lastname";
	private static final String FIRST_NAME_MSG = "First Name";
	private static final String LAST_NAME_MSG = "Last Name";
	private static final String FIELD_SIZE_MSG_KEY = "field.size";

	/**
	 * This overridden method is invoked every time the @InitBinder method with the
	 * desired key is invoked, which happens whenever that particular key is added
	 * into a model object.
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		LOGGER.debug("inside supports of UserValidator");
		return User.class.equals(clazz);
	}

	/**
	 * 1. This overridden is called whenever a method param with @Validated annot.
	 * is called which is also mapped with a matching @ModelAttribute key.
	 * 
	 * 2. This method checks whether the firstname and lastname fields of User are
	 * within the field length validation limits are not. If not, then a field
	 * level error is raised and control transferred to the controller and then to
	 * the view to display the error.
	 */
	@Override
	public void validate(Object target, Errors errors) {
		LOGGER.debug("inside user validator.....");
		User user = (User) target;

		LOGGER.debug("first name:" , user.getFirstname().length());

		if (!CustomValidationUtil.isFieldLengthValid(user.getFirstname().length(), MIN_LENGTH, MAX_LENGTH))
			errors.rejectValue(FIRST_NAME_FIELD, FIELD_SIZE_MSG_KEY,
					new String[] { FIRST_NAME_MSG, Integer.toString(MIN_LENGTH), Integer.toString(MAX_LENGTH) }, null);

		if (!CustomValidationUtil.isFieldLengthValid(user.getLastname().length(), MIN_LENGTH, MAX_LENGTH))
			errors.rejectValue(LAST_NAME_FIELD, FIELD_SIZE_MSG_KEY,
					new String[] { LAST_NAME_MSG, Integer.toString(MIN_LENGTH), Integer.toString(MAX_LENGTH) }, null);
	}
}

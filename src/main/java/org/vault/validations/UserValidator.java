package org.vault.validations;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.vault.model.User;

@Component
public class UserValidator implements Validator {

	private static final int MIN_LENGTH = 2;
	private static final int MAX_LENGTH = 15;
	private static final String FIRST_NAME_FIELD = "firstname";
	private static final String LAST_NAME_FIELD = "lastname";
	private static final String FIRST_NAME_MSG = "First Name";
	private static final String LAST_NAME_MSG = "Last Name";
	private static final String FIELD_SIZE_MSG_KEY = "field.size";

	@Override
	public boolean supports(Class<?> clazz) {
		System.out.println("inside supports");
		return User.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		System.out.println("inside user validator.....");
		User user = (User) target;

		System.out.println("first name:"+user.getFirstname().length());
		
		if (!CustomValidationUtil.isFieldLengthValid(user.getFirstname().length(), MIN_LENGTH, MAX_LENGTH))
			errors.rejectValue(FIRST_NAME_FIELD, FIELD_SIZE_MSG_KEY,
					new String[] { FIRST_NAME_MSG, Integer.toString(MIN_LENGTH), Integer.toString(MAX_LENGTH) }, null);

		if (!CustomValidationUtil.isFieldLengthValid(user.getLastname().length(), MIN_LENGTH, MAX_LENGTH))
			errors.rejectValue(LAST_NAME_FIELD, FIELD_SIZE_MSG_KEY,
					new String[] { LAST_NAME_MSG, Integer.toString(MIN_LENGTH), Integer.toString(MAX_LENGTH) }, null);
	}
}

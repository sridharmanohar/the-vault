package org.vault.validations;

import org.springframework.stereotype.Component;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.vault.model.User;

@Component
public class UserValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		System.out.println("inside supports");
		return User.class.equals(clazz);
	}
	
	@Override
	public void validate(Object target, Errors errors) {
		System.out.println("inside validator.....");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstname", "firstname.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastname", "lastname.required");
		
		User user = (User) target;
		int firstnamelength = user.getFirstname().length();
		int lastnamelength = user.getLastname().length();
		
		if(firstnamelength > 0 && (firstnamelength < 2 | firstnamelength > 15))
			errors.rejectValue("firstname", "firstname.size");
		
		if(lastnamelength > 0 && (lastnamelength < 2 | lastnamelength > 15))
			errors.rejectValue("lastname", "lastname.size");
	}
}

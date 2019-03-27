package org.vault.validations;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.vault.model.TopicTemplate;

@Component
public class TopicTemplateValidator implements Validator {

	private static final String REQUIRED = "required";
	
	@Override
	public boolean supports(Class<?> clazz) {
		return TopicTemplate.class.equals(clazz);
	}
	
	@Override
	public void validate(Object target, Errors errors) {
		System.out.println("inside validator of tt");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "propname", REQUIRED);
//		errors.reject(REQUIRED);
		TopicTemplate topicTemplate = (TopicTemplate) target;
		
		int topiclength = topicTemplate.getPropname().length();
		
		if(topiclength > 0 && (topiclength < 3 | topiclength > 15))
			errors.rejectValue("propname", "propname.size");
		
	}
	
}

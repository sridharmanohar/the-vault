package org.vault.validations;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.vault.model.Topic;

@Component
public class TopicValidator implements Validator {

	private static final String REQUIRED = "required";
	
	@Override
	public boolean supports(Class<?> clazz) {
		return Topic.class.equals(clazz);
	}
	
	@Override
	public void validate(Object target, Errors errors) {
		System.out.println("inside topic validator");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "topicname", REQUIRED);

		Topic topic = (Topic) target;
		
		int topiclength = topic.getTopicname().length();
		
		if(topiclength > 0 && (topiclength < 3 | topiclength > 15))
			errors.rejectValue("topicname", "topicname.size");
	}
}

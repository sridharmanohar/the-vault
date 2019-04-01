package org.vault.validations;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.vault.model.Topic;
import org.vault.model.TopicTemplate;

/**
 * This class is an implementation of Spring Validator.
 * 
 * @author sridhar
 *
 */
@Component
public class TopicValidator implements Validator {

	private static final Logger LOGGER = LoggerFactory.getLogger(TopicValidator.class);	
	
	private static final int MIN_LENGTH = 3;
	private static final int MAX_LENGTH = 40;
	private static final String TOPIC_NAME_FIELD = "topicname";
	private static final String FIELD_SIZE_MSG_KEY = "field.size";

	/**
	 * This overridden method is invoked every time the @InitBinder method with
	 * the desired key is invoked, which happens whenever that particular key is
	 * added into a model object.
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		LOGGER.debug("Topic Validator supports");
		return Topic.class.equals(clazz);
	}

	/**
	 * 1. This overridden is called whenever a method param with @Validated annot.
	 * is called which is also mapped with a matching @ModelAttribute key.
	 * 
	 * 2. We first check to ensure that Topic object is not null by checking the
	 * name attribute. If it is null, then we straightaway raise a field level error
	 * and transfer control to the controller which then transfers the control to
	 * the view with the error message.
	 * 
	 * 3. If the topic name is not null, if the name is passing the field length
	 * validations, if yes, then we iterate through each topic template (keys) and
	 * check if the keys are also passing the field length validation check, if even
	 * one key does not pass the length validation then immediately a global error
	 * is raised and further processing is halted and control transferred to
	 * controller and from there to view along with the global error.
	 * 
	 * 4. I could have even used a field level error for topictemplate length
	 * validation but then I din't find a way to map such a field level error the
	 * model in the view hence restricted myself to global error in such
	 * parent-child validation errors.
	 */
	@Override
	public void validate(Object target, Errors errors) {
		LOGGER.debug("inside topic validator");

		Topic topic = (Topic) target;

		if (topic.getTopicname() != null) {
			LOGGER.debug("topic name not null");
			if (!CustomValidationUtil.isFieldLengthValid(topic.getTopicname().length(), MIN_LENGTH, MAX_LENGTH)) {
				LOGGER.debug("topic name length validation failed.");	
				errors.rejectValue(TOPIC_NAME_FIELD, FIELD_SIZE_MSG_KEY,
						new String[] { "Topic Name", Integer.toString(MIN_LENGTH), Integer.toString(MAX_LENGTH) },
						null);
			} else {
				for (TopicTemplate tt : topic.getTopicTemplates()) {
					if (tt.getPropname() != null & !CustomValidationUtil.isFieldLengthValid(tt.getPropname().length(),
							MIN_LENGTH, MAX_LENGTH)) {
						LOGGER.debug("topic template length validation failed.");
						errors.reject(FIELD_SIZE_MSG_KEY, new String[] { "All Topic Keys", Integer.toString(MIN_LENGTH),
								Integer.toString(MAX_LENGTH) }, null);
						break;
					}
				}
			}
		} else {
			LOGGER.debug("topic name was null");
			errors.rejectValue(TOPIC_NAME_FIELD, FIELD_SIZE_MSG_KEY,
					new String[] { "Topic Name", Integer.toString(MIN_LENGTH), Integer.toString(MAX_LENGTH) }, null);
		}
	}

}

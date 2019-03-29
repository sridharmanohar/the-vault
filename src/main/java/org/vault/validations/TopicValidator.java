package org.vault.validations;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.vault.model.Topic;
import org.vault.model.TopicTemplate;

@Component
public class TopicValidator implements Validator {

	private static final int MIN_LENGTH = 3;
	private static final int MAX_LENGTH = 40;
	private static final String TOPIC_NAME_FIELD = "topicname";
	private static final String FIELD_SIZE_MSG_KEY = "field.size";

	@Override
	public boolean supports(Class<?> clazz) {
		System.out.println("Topic Validator supports");
		return Topic.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		System.out.println("inside topic validator");

		Topic topic = (Topic) target;

		if (topic.getTopicname() != null) {
			System.out.println("topic name not null");
			if (!CustomValidationUtil.isFieldLengthValid(topic.getTopicname().length(), MIN_LENGTH, MAX_LENGTH)) {
				System.out.println("topic name length validation failed.");
				errors.rejectValue(TOPIC_NAME_FIELD, FIELD_SIZE_MSG_KEY,
						new String[] { "Topic Name", Integer.toString(MIN_LENGTH), Integer.toString(MAX_LENGTH) },
						null);
			} else {
				for (TopicTemplate tt : topic.getTopicTemplates()) {
					if (tt.getPropname() != null & !CustomValidationUtil.isFieldLengthValid(tt.getPropname().length(),
							MIN_LENGTH, MAX_LENGTH)) {
						System.out.println("topic template length validation failed.");
						errors.reject(FIELD_SIZE_MSG_KEY, new String[] { "All Topic Keys", Integer.toString(MIN_LENGTH),
								Integer.toString(MAX_LENGTH) }, null);
						break;
					}
				}
			}
		} else {
			System.out.println("topic name was null");
			errors.rejectValue(TOPIC_NAME_FIELD, FIELD_SIZE_MSG_KEY,
					new String[] { "Topic Name", Integer.toString(MIN_LENGTH), Integer.toString(MAX_LENGTH) }, null);
		}
	}

}

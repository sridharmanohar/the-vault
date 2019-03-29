package org.vault.validations;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.vault.model.TopicDetail;

public class TopicDetailValidator implements Validator {
	
	@Override
	public boolean supports(Class<?> clazz) {
		System.out.println("inside topic detail validator");
		return TopicDetail.class.equals(clazz);
	}


	@Override
		public void validate(Object target, Errors errors) {
			
			TopicDetail topicDetail = (TopicDetail) target;

			System.out.println("prop valu in validator is:"+topicDetail.getPropvalue());
		}
}

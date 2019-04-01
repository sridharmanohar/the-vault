package org.vault.validations;

public abstract class CustomValidationUtil {

	/**
	 * Utility method to check if the passed-in field length is it within the min
	 * and max ranges. The min and max ranges are inclusive.
	 * 
	 * @param fieldLength
	 * @param minLength
	 * @param maxLength
	 * @return
	 */
	public static boolean isFieldLengthValid(int fieldLength, int minLength, int maxLength) {
		boolean result = (fieldLength >= minLength) && (fieldLength <= maxLength) ? true : false;
		return result;
	}

}

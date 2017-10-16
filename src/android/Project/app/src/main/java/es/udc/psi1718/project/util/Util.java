package es.udc.psi1718.project.util;

/**
 * Some useful functions
 */
public final class Util {


	/**
	 * Private constructor to prevent instantiation
	 */
	private Util() {
	}


	/**
	 * Checks if a given String is empty or no
	 *
	 * @param text String you want to check
	 *
	 * @return
	 */
	public static Boolean isEmptyString(String text) {
		return text.trim().length() <= 0;
	}
}

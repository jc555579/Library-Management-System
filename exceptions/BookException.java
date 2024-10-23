package exceptions;

public class BookException extends Exception {

	/**
	 * This Exception will handle all possible errors about Book.
	 */
	private static final long serialVersionUID = 1L;

	public BookException(String message) {
		super(message);
	}
}

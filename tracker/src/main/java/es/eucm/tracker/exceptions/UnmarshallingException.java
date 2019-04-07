package es.eucm.tracker.exceptions;

public class UnmarshallingException extends TrackerException {

	/**
	 * @see java.io.Serializable
	 */
	private static final long serialVersionUID = 8531279979694329862L;

	public UnmarshallingException(String message) {
		super(message);
	}
	
	public UnmarshallingException(String message, Throwable cause) {
		super(message, cause);
	}

}

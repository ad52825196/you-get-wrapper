public class NoExecutableFileFoundException extends Exception {
	private static final long serialVersionUID = 1L;
	
	NoExecutableFileFoundException() {
		super();
	}
	
	NoExecutableFileFoundException(String message, Exception cause) {
		super(message, cause);
	}
}

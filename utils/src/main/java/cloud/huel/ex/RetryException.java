package cloud.huel.ex;

/**
 * @author 张晓华
 * @date 2022-10-23
 */
public class RetryException extends RuntimeException {

	public RetryException() {
		super();
	}


	public RetryException(String message) {
		super(message);
	}


	public RetryException(String message, Throwable cause) {
		super(message, cause);
	}

	public RetryException(Throwable cause) {
		super(cause);
	}

	
	protected RetryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}

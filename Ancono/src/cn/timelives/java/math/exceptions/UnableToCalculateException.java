/**
 * 
 */
package cn.timelives.java.math.exceptions;

/**
 * An exception that describes the situation that the math calculation is unable to finish 
 * based on the available calculator or the selected number model. 
 * @author liyicheng
 *
 */
public final class UnableToCalculateException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2438088837891314278L;

	/**
	 * 
	 */
	public UnableToCalculateException() {
	}

	/**
	 * @param message
	 */
	public UnableToCalculateException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public UnableToCalculateException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public UnableToCalculateException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public UnableToCalculateException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
	
	
	
}

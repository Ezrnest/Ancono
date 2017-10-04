/**
 * 
 */
package cn.timelives.java.math.exceptions;

/**
 * @author liyicheng
 *
 */
public class NumberValueException extends RuntimeException {
	private String expr;
	/**
	 * 
	 */
	private static final long serialVersionUID = 8724288820557054251L;

	/**
	 * 
	 */
	public NumberValueException() {
	}

	/**
	 * @param message
	 */
	public NumberValueException(String message) {
		super(message);
		
	}


	/**
	 * @param message
	 * @param cause
	 */
	public NumberValueException(String message,String expr) {
		super(message);
		this.expr = expr;
	}
	
	/**
	 * Gets the expression that cause this exception, {@code null} value
	 *  is possible.
	 * @return the expression
	 */
	public String getExpression() {
		return expr;
	}

}

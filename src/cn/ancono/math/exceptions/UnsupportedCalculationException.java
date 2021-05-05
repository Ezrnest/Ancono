/**
 *
 */
package cn.ancono.math.exceptions;

import cn.ancono.math.numberModels.api.RealCalculator;

/**
 * Exception to throw when a {@link RealCalculator} cannot do such calculation to the
 * number type.
 *
 * @author lyc
 */
public class UnsupportedCalculationException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = -5561158946212443201L;

    /**
     * Constructs an UnsupportedCalculationException with no detail message.
     */
    public UnsupportedCalculationException() {
    }

    /**
     * Constructs an UnsupportedCalculationException with the specified
     * detail message.
     *
     * @param message
     *            the detail message
     */
    public UnsupportedCalculationException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and
     * cause.
     *
     * <p>
     * Note that the detail message associated with <code>cause</code> is
     * <i>not</i> automatically incorporated in this exception's detail
     * message.
     *
     * @param message
     *            the detail message (which is saved for later retrieval by
     *            the {@link Throwable#getMessage()} method).
     * @param cause
     *            the cause (which is saved for later retrieval by the
     *            {@link Throwable#getCause()} method). (A <tt>null</tt>
     *            value is permitted, and indicates that the cause is
     *            nonexistent or unknown.)
     * @since 1.5
     */
    public UnsupportedCalculationException(String message, Throwable cause) {
        super(message, cause);
    }

//	/*
//	 * @see java.lang.Throwable#fillInStackTrace()
//	 */
//	@Override
//	public synchronized Throwable fillInStackTrace() {
//		return this;
//	}

}

/**
 * 
 */
package cn.timelives.java.math.exceptions;

import cn.timelives.java.math.MathCalculator;

/**
 * Throws when the calculation of a math calculator is not satisfied.For example,
 * if the result of {@code 1+2} and the result of {@code 1+1+1} doesn't match, but 
 * the user requires that the result must match, then this  
 * kind of exception can be thrown. 
 * Usually, the unsatisfied calculation exception 
 * is caused by unsuitable calculator or some kind of precision fault.<br>
 * The calculator can be get through the method {@link #getMathCalculator()} 
 * if the {@link MathCalculator} is applied.
 * @author liyicheng
 *
 */
public final class UnsatisfiedCalculationResultException extends RuntimeException{
	 /**
	 * 
	 */
	private static final long serialVersionUID = -7587468617714312865L;

	private final MathCalculator<?> mc;
	/**
     * Constructs an UnsatisfiedCalculationResultException with no detail message.
     */
    public UnsatisfiedCalculationResultException(MathCalculator<?> mc) {
    	this.mc = mc;
    }

    /**
     * Constructs an UnsatisfiedCalculationResultException with the specified
     * detail message.
     *
     * @param message the detail message
     */
    public UnsatisfiedCalculationResultException(String message) {
        super(message);
        mc = null;
    }

    /**
     * Constructs a new exception with the specified detail message and
     * cause.
     *
     * <p>Note that the detail message associated with <code>cause</code> is
     * <i>not</i> automatically incorporated in this exception's detail
     * message.
     *
     * @param  message the detail message (which is saved for later retrieval
     *         by the {@link Throwable#getMessage()} method).
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link UnsatisfiedCalculationResultException#getMathCalculator()} method).  (A <tt>null</tt> value
     *         is permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     * @since 1.5
     */
    public UnsatisfiedCalculationResultException(String message, MathCalculator<?> cause) {
        super(message);
        this.mc = cause;
    }
    /**
     * Returns the MathCalculator that caused UnsatisfiedCalculationResultException.
     * @return a MathCalculator, or {@code null}
     */
    public MathCalculator<?> getMathCalculator(){
    	return mc;
    }
    
    
}

/**
 * 2017-10-08
 */
package cn.timelives.java.math;

import java.util.function.Predicate;

/**
 * Represents a predicate (boolean-valued function) of one argument, whose 
 * method is {@link #isSolution(Object)}.
 * @author liyicheng
 * 2017-10-08 11:22
 * @see Predicate
 */
public interface SolutionPredicate<T> {
	
	/**
	 * Determines whether {@code x} is one of the solutions.
	 * @param x the input argument
	 * @return {@code true} if {@code x} is a solution
	 */
	boolean isSolution(T x);
	
}

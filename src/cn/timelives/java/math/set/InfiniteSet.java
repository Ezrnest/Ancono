/**
 * 2017-09-17
 */
package cn.timelives.java.math.set;

/**
 * Infinite set contains infinite elements.
 * @author liyicheng
 * 2017-09-17 11:57
 *
 */
public interface InfiniteSet<T> extends MathSet<T>{
	/**
	 * Describes the cardinal of the set of all integers, which is zero.
	 */
	final int CARDINAL_INTEGER = 0;
	/**
	 * Describes the cardinal of the set of all quotients, which is zero.
	 */
	final int CARDINAL_QUOTIENT = CARDINAL_INTEGER;
	
	/**
	 * Describes the cardinal of real number, which is one.
	 */
	final int CARDINAL_REAL= 1;
	
	
	
	/**
	 * Returns the cardinal number of this set. Returns 0 to indicate this 
	 * set is equal to <i>N</i> in cardinal. The cardinal number must be non-negative
	 * @return a non-negative integer 
	 */
	int cardinalNumber();
	
}

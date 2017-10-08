/**
 * 2017-10-08
 */
package cn.timelives.java.math;

import cn.timelives.java.math.set.MathSet;

/**
 * @author liyicheng
 * 2017-10-08 11:48
 *
 */
public interface Solveable<T> extends SolutionPredicate<T> {
	
	/**
	 * Gets the solution of this SolutionPredicate, which means 
	 * 
	 * @return
	 */
	MathSet<T> getSolution();
}

/**
 * 2017-10-07
 */
package cn.timelives.java.math.calculus;

import cn.timelives.java.math.function.SVFunction;

/**
 * A derivable function is a single variable function that 
 * provides method derive
 * @author liyicheng
 * 2017-10-07 14:39
 *
 */
public interface Derivable<T> extends SVFunction<T>{
	
	/**
	 * Returns the derivative of this function as 
	 * a SVFunction
	 * @return {@literal f'(x)}
	 */
	SVFunction<T> derive();
	
}

/**
 * 2017-10-07
 */
package cn.timelives.java.math.algebra.calculus;

import cn.timelives.java.math.function.MathFunction;
import cn.timelives.java.math.function.SVFunction;
import org.jetbrains.annotations.NotNull;

/**
 * A derivable function is a single variable function that 
 * provides method derive
 * @author liyicheng
 * 2017-10-07 14:39
 *
 */
public interface Derivable<T, R, S extends MathFunction<T, R>> extends MathFunction<T, R> {
	
	/**
	 * Returns the derivative of this function as 
	 * a SVFunction
	 * @return {@literal f'(x)}
	 */
    @NotNull
	S derive();
	
}

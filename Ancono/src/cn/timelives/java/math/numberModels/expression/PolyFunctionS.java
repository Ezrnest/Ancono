/**
 * 2017-11-25
 */
package cn.timelives.java.math.numberModels.expression;

import java.util.function.UnaryOperator;

import cn.timelives.java.math.exceptions.UnsupportedCalculationException;
import cn.timelives.java.math.numberModels.Polynomial;

/**
 * A single-parameter function, which corresponds to {@link Node.SFunction}.
 * @author liyicheng
 * 2017-11-25 19:08
 *
 */
public interface PolyFunctionS extends UnaryOperator<Polynomial>{
	/**
	 * Returns the result of applying the function, or throws UnsupportedCalculationException if the result cannot be 
	 * returned as a Polynomial. 
	 * @param p a Polynomial
	 * @return the result of applying the function
	 */
	public Polynomial apply(Polynomial p) throws UnsupportedCalculationException;
}

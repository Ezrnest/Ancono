/**
 * 2017-11-25
 */
package cn.timelives.java.math.numberModels.expression;

import cn.timelives.java.math.exceptions.UnsupportedCalculationException;
import cn.timelives.java.math.numberModels.Multinomial;

import java.util.function.UnaryOperator;

/**
 * A single-parameter function, which corresponds to {@link Node.SFunction}.
 * @author liyicheng
 * 2017-11-25 19:08
 *
 */
public interface PolyFunctionS extends UnaryOperator<Multinomial>{
	/**
	 * Returns the result of applying the function, or throws UnsupportedCalculationException if the result cannot be 
	 * returned as a Multinomial.
	 * @param p a Multinomial
	 * @return the result of applying the function
	 */
	public Multinomial apply(Multinomial p) throws UnsupportedCalculationException;
}

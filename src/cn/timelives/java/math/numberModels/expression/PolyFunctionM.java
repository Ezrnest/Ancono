/**
 * 2017-11-25
 */
package cn.timelives.java.math.numberModels.expression;

import cn.timelives.java.math.exceptions.UnsupportedCalculationException;
import cn.timelives.java.math.numberModels.Multinomial;

import java.util.function.Function;

/**
 * @author liyicheng
 * 2017-11-25 19:13
 *
 */
public interface PolyFunctionM extends Function<Multinomial[], Multinomial> {
	/**
	 * Returns the result of applying the function, or throws UnsupportedCalculationException if the result cannot be 
	 * returned as a Multinomial.
	 * @param ps an array of Multinomial as parameters.
	 * @return the result of applying the function
	 */
	@Override
    Multinomial apply(Multinomial[] ps)throws UnsupportedCalculationException;
}

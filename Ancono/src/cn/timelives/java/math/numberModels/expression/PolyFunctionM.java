/**
 * 2017-11-25
 */
package cn.timelives.java.math.numberModels.expression;

import java.util.function.Function;

import cn.timelives.java.math.exceptions.UnsupportedCalculationException;
import cn.timelives.java.math.numberModels.Polynomial;

/**
 * @author liyicheng
 * 2017-11-25 19:13
 *
 */
public interface PolyFunctionM extends Function<Polynomial[],Polynomial>{
	/**
	 * Returns the result of applying the function, or throws UnsupportedCalculationException if the result cannot be 
	 * returned as a Polynomial. 
	 * @param ps an array of Polynomial as parameters.
	 * @return the result of applying the function
	 */
	@Override
	Polynomial apply(Polynomial[] ps)throws UnsupportedCalculationException;
}

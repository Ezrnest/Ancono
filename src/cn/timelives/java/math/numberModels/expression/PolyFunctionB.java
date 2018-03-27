/**
 * 2017-11-25
 */
package cn.timelives.java.math.numberModels.expression;

import cn.timelives.java.math.exceptions.UnsupportedCalculationException;
import cn.timelives.java.math.numberModels.Polynomial;

import java.util.function.BinaryOperator;

/**
 * A double-parameter function
 * @author liyicheng
 * 2017-11-25 19:11
 *
 */
public interface PolyFunctionB extends BinaryOperator<Polynomial>{
	/**
	 * Returns the result of applying the function, or throws UnsupportedCalculationException if the result cannot be 
	 * returned as a Polynomial. 
	 * @param p1 a Polynomial
	 * @param p2 another Polynomial
	 * @return the result of applying the function
	 */
	public Polynomial apply(Polynomial p1, Polynomial p2)throws UnsupportedCalculationException;
}

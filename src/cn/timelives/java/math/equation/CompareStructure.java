/**
 * 2017-10-08
 */
package cn.timelives.java.math.equation;

import cn.timelives.java.math.MathCalculatorHolder;
import cn.timelives.java.math.SolutionPredicate;
import cn.timelives.java.math.equation.inequation.Inequation;
import cn.timelives.java.math.function.MathFunction;
import cn.timelives.java.math.numberModels.MathCalculator;

/**
 * A CompareStructure is the super class of {@link Equation} and {@link Inequation}, which
 * is composed of a function and an operator : <pre>f(x) <i>op</i> 0</pre> where 
 * the operation is one of the {@link Type}
 * @author liyicheng
 * 2017-10-08 11:27
 *
 * @param <T> the {@link MathCalculator} type
 * @param <S> the input of the compare structure\
 * @see Type
 */
public interface CompareStructure<T,S> 
extends MathCalculatorHolder<T>,SolutionPredicate<S>
{
	/**
	 * Gets the MathFunction of the left part of the compare structure.
	 * @return
	 */
	MathFunction<S,T> asFunction();
	
	/**
	 * Returns the type of the operation.
	 * @return the type
	 */
	Type getOperationType();
	
	/**
	 * Determines whether the give variable {@code x} is one of the 
	 * solution of this compare structure.
	 */
	@Override
	boolean isSolution(S x);
}

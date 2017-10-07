package cn.timelives.java.math.numberModels;

import java.util.List;

import cn.timelives.java.math.FlexibleMathObject;

/**
 * A simplifier is used for simplify a number or a set of numbers. 
 * The simplifier may 
 * have different performance while simplifying, and the actual process should 
 * be specific when any Simplifier is required. <p>
 * @author lyc
 * @see Simplifiable
 */
@FunctionalInterface
public interface Simplifier<T> {
	/**
	 * Input a list of numbers to simplify, and return a list of simplified numbers,
	 * the number and order of numbers should be the same.<p>
	 * This simplify method should be equal to multiply a non-zero value to all 
	 * of the numbers to make the result simpler. For example, for the input {@code [2,4,6]}, the 
	 * simplifier may  multiply number {@code 1/2} so the result will be {@code [1,2,3]}, which 
	 * should be simpler than the input. 
	 * @param numbers arguments, the content may be changed.
	 * @return a list of numbers.
	 */
	public List<T> simplify(List<T> numbers);
	
	/**
	 * Simplifies a single number. This simplify operation is optional so 
	 * a default implement is applied.
	 * @param x a number
	 * @return the result
	 */
	public default T simplify(T x){
		return x;
	}
	
	@SuppressWarnings("unchecked")
	public static <T,S extends FlexibleMathObject<T>> S singleSimplify(Simplifier<T> s, S x){
		return (S) x.mapTo(s::simplify,x.getMathCalculator());
	}

	
}

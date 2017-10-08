/**
 * 2017-10-06
 */
package cn.timelives.java.math.equation;

import java.util.List;
import java.util.function.Predicate;

import cn.timelives.java.math.MathCalculatorHolder;
import cn.timelives.java.math.equation.CompareStructure.Type;
import cn.timelives.java.math.function.MathFunction;
import cn.timelives.java.math.numberModels.MathCalculator;

/**
 * Multiple variable compare structure, using a list as input.
 * @author liyicheng
 * 2017-10-06 09:56
 *
 */
public interface MVCompareStructure<T> extends MathCalculatorHolder<T> 
,CompareStructure<T, List<T>>{
	
	
	/**
	 * Gets the number of variables in this CompareStructure. 
	 * @return the variable count.
	 */
	int getVariableCount();
	
	/**
	 * Determines whether the given list of variables is one of the solutions.
	 * The size of the list should be equal to the number of the variables and the order is 
	 * considered.
	 * @param x a list of variable
	 * @return {@code true} if {@code x} is solution.
	 */
	boolean isSolution(List<T> x);
	/**
	 * Gets the MathFunction of the left part of the compare structure.
	 * @return
	 */
	MathFunction<List<T>,T> asFunction();
	
}

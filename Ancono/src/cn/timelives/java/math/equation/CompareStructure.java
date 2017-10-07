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
 *  A CompareStructure is the super class of {@link Equation} and {@link Inequation}, which 
 * is composed of a function and an operator : <pre>f(x) <i>op</i> 0</pre> where 
 * the operation is one of the {@link CompareStructure.Type}
 * @author liyicheng
 * 2017-10-06 09:56
 *
 */
public interface CompareStructure<T> extends MathCalculatorHolder<T>{
	/**
	 * An enumeration that describes all the types of a compare structure.
	 * <ul>
	 * <li>Greater
	 * <li>Greater or Equal
	 * <li>Less
	 * <li>Less or Equal
	 * <li>Equal
	 * <li>Not Equal
	 * </ul>
	 * @author liyicheng
	 * 2017-10-06 19:09
	 *
	 */
	public enum Type{
		GREATER(">") {
			@Override
			public boolean matches(int signum) {
				return signum>0;
			}
		},
		GREATER_OR_EQUAL(">=") {
			@Override
			public boolean matches(int signum) {
				return signum>=0;
			}
		},
		LESS("<") {
			@Override
			public boolean matches(int signum) {
				return signum<0;
			}
		},
		LESS_OR_EQUAL("<=") {
			@Override
			public boolean matches(int signum) {
				return signum<=0;
			}
		},
		EQUAL("=") {
			@Override
			public boolean matches(int signum) {
				return signum == 0;
			}
		},
		NOT_EQUAL("!="){
			/*
			 * @see cn.timelives.java.math.Inequation.Type#matches(int)
			 */
			@Override
			public boolean matches(int signum) {
				return signum!=0;
			}
		};
		private final String operation;
		
		private Type(String op){
			this.operation = op;
		}
		
		/**
		 * Determines whether the {@code signum}, which is often the 
		 * result of {@link MathCalculator#compare(Object, Object)}, 
		 * matches the inequation operation type.
		 * @param signum
		 * @return
		 */
		public abstract boolean matches(int signum);
		/**
		 * Gets the String representing this operation. 
		 */
		public String toString() {
			return operation;
		}
	}
	
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
	MathFunction<List<T>,T> getFunction();
	/**
	 * Returns the type of the operation.
	 * @return the type
	 */
	Type getOperationType();
}

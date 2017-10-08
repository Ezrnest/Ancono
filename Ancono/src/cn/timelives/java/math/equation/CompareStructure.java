/**
 * 2017-10-08
 */
package cn.timelives.java.math.equation;

import java.util.List;

import cn.timelives.java.math.MathCalculatorHolder;
import cn.timelives.java.math.SolutionPredicate;
import cn.timelives.java.math.equation.CompareStructure.Type;
import cn.timelives.java.math.function.MathFunction;
import cn.timelives.java.math.numberModels.MathCalculator;

/**
 * A CompareStructure is the super class of {@link Equation} and {@link Inequation}, which 
 * is composed of a function and an operator : <pre>f(x) <i>op</i> 0</pre> where 
 * the operation is one of the {@link CompareStructure.Type}
 * @author liyicheng
 * 2017-10-08 11:27
 *
 * @param <T> the {@link MathCalculator} type
 * @param <S> the input of the compare structure
 */
public interface CompareStructure<T,S> 
extends MathCalculatorHolder<T>,SolutionPredicate<S>
{
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

/**
 * 2017-10-06
 */
package cn.timelives.java.math.equation;

import java.util.List;
import java.util.function.Function;

import cn.timelives.java.math.FlexibleMathObject;
import cn.timelives.java.math.function.MathFunction;
import cn.timelives.java.math.numberModels.MathCalculator;
import cn.timelives.java.math.numberModels.NumberFormatter;

/**
 *
 *
 */
public abstract class AbstractCompareStructure<T> extends FlexibleMathObject<T>
implements CompareStructure<T>{
	/**
	 * 
	 */
	protected final Type op;
	/**
	 * @param mc
	 * @param op the operation type
	 */
	protected AbstractCompareStructure(MathCalculator<T> mc,Type op) {
		super(mc);
		this.op = op;
	}
	/**
	 * Returns {@code mc.compare(y, mc.getZero())}
	 * @param y a number
	 * @return  {@code mc.compare(y, mc.getZero())}
	 */
	protected int compareZero(T y) {
		return mc.compare(y, mc.getZero());
	}
	/**
	 * Determines whether the given list of variables is one of the solutions.
	 * The size of the list should be equal to the number of the variables and the order is 
	 * considered.
	 * @param x a list of variable
	 * @return {@code true} if {@code x} is solution.
	 */
	public boolean isSolution(List<T> x) {
		return op.matches(compareZero(getFunction().apply(x)));
	}
	/**
	 * Returns the type of the operation.
	 * @return the type
	 */
	public Type getOperationType() {
		return op;
	}
	
	public abstract <N> AbstractCompareStructure<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator);
	
}

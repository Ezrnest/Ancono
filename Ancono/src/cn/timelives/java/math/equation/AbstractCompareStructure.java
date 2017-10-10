/**
 * 2017-10-08
 */
package cn.timelives.java.math.equation;

import cn.timelives.java.math.FlexibleMathObject;
import cn.timelives.java.math.numberModels.MathCalculator;

/**
 * @author liyicheng
 * 2017-10-08 11:34
 *
 */
public abstract class AbstractCompareStructure<T,S> extends FlexibleMathObject<T> 
implements CompareStructure<T, S>{
	/**
	 * The type of operation.
	 */
	protected final Type op;
	/**
	 * @param mc
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
	public boolean isSolution(S x) {
		return op.matches(compareZero(asFunction().apply(x)));
	}
	/**
	 * Returns the type of the operation.
	 * @return the type
	 */
	public Type getOperationType() {
		return op;
	}
	
}

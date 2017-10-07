/**
 * 2017-10-06
 */
package cn.timelives.java.math.equation;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import cn.timelives.java.math.numberModels.MathCalculator;

/**
 * Describes inequation in math. An inequation is presented as 
 *  <pre> left <i>op</i> right</pre>, 
 * @author liyicheng
 * 2017-10-06 08:49
 *
 */
public abstract class Inequation<T> extends AbstractCompareStructure<T> {
	
	private static final Set<Type> inoperation = 
			Collections.unmodifiableSet(
					EnumSet.of(Type.GREATER, Type.GREATER_OR_EQUAL, Type.LESS, Type.LESS_OR_EQUAL));
	
	/**
	 * @param mc
	 * @param op must be one of the 
	 */
	protected Inequation(MathCalculator<T> mc,Type op) {
		super(mc,check(op));
	}
	
	@Override
	public abstract <N> Inequation<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator);
	
	/**
	 * Determines whether the {@code type} is one of the inequation type.
	 * @param type
	 * @return
	 */
	public static boolean isOperation(Type type) {
		return inoperation.contains(type);
	}
	/**
	 * Gets all the supported operations.
	 * @return a set of operations.
	 */
	public static Set<Type> getOperation(){
		return inoperation;
	}
	
	private static Type check(Type t) {
		if(!isOperation(t)) {
			throw new IllegalArgumentException("Must be one of the operations for inequation!");
		}
		return t;
	}
	
}

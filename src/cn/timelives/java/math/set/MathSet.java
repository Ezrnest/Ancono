/**
 * 
 */
package cn.timelives.java.math.set;


import cn.timelives.java.math.MathObject;
import cn.timelives.java.math.MathCalculator;
import cn.timelives.java.math.function.Bijection;
import cn.timelives.java.math.function.MathFunction;

/**
 * Math set is a type of set that contains elements which are unique from each other, but 
 * only provides {@link #contains(Object)} method. The MathSet should always uses {@link MathCalculator#isEqual(Object, Object)}
 * method to test whether the two elements is the identity.<p>
 * A MathSet should always be immutable, which is the identity as the general idea for {@link MathObject}.
 * Therefore, add, remove union and intersect operations are not provided. 
 * @author liyicheng
 *
 */
public interface MathSet<T>{

	/**
	 * Determines whether this set contains this set.
	 * @param t an object
	 * @return {@code true} if this set contains the object, otherwise {@code false}.
	 */
	boolean contains(T t);

	/**
	 * Maps this set to type s.
	 *
	 * @param f   a function that defines a bijection between T and S.
	 * @param <S> the new type S
	 * @return a MathSet of type S
	 */
	default <S> MathSet<S> mapTo(Bijection<T, S> f) {
		return s -> this.contains(f.deply(s));
	}
}

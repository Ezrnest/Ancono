package cn.timelives.java.math.function;

import cn.timelives.java.math.set.MathSet;
import cn.timelives.java.math.set.MathSets;

import java.util.function.Function;
/**
 * MathFunction is an interface indicates math functions. Math function is a kind of special function.
 * This function must perform like a real math function:
 * <ul>
 * <li>It does NOT make change to parameter: Any parameter should not be changed in this function. 
 * <li>It is <tt>consistent</tt>:If this function is applied with same parameters for multiple times,the 
 * result should be the same.
 * </ul>
 * 
 * @author lyc
 *
 * @param <P> parameter type 
 * @param <R> result type
 */
@FunctionalInterface
public interface MathFunction<P,R> extends Function<P,R>{
	@Override
	R apply(P x);
	
	/**
	 * Returns the domain of this MathFunction, the 
	 * implementor should override this method to specify the domain.
	 * @return a MathSet representing the domain
	 */
	default MathSet<P> domain() {
		return MathSets.universe();
	}
	
	@SuppressWarnings("rawtypes")
	static final MathFunction same = new MathFunction() {
		@Override
		public Object apply(Object t) {
			return t;
		}
		
	};
	/**
	 * Returns a type-safe MathFunction whose result is the parameter itself.
	 * @return a MathFunction
	 */
	@SuppressWarnings("unchecked")
	public static <T> MathFunction<T,T> identity(){
		return same;
	}
	
	
	
}

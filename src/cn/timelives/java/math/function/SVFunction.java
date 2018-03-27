/**
 * 
 */
package cn.timelives.java.math.function;

/**
 * Describes the function {@code f(x)} in the plane which only contains single variable. 
 * @author liyicheng
 *
 */
public interface SVFunction<T> extends MathFunction<T, T>{
	
	/**
	 * Returns the result of {@code f(x)}.
	 * @param x variable x
	 * @return {@code f(x)}
	 */
	@Override
	T apply(T x);
	
}

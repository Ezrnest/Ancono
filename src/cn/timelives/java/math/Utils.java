/**
 * 2017-10-07
 */
package cn.timelives.java.math;

import cn.timelives.java.math.numberModels.MathCalculator;

import java.util.Comparator;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * Contains some utilities for MathCalculator
 * @author liyicheng
 * 2017-10-07 15:19
 *
 */
public final class Utils {

	/**
	 * 
	 */
	private Utils() {
	}
	
	/**
	 * Returns a BiPredicate:
	 * {@code (x,y)->mc.isEqual(x, mapper.apply(y))}
	 * @param mc a {@link MathCalculator}
	 * @param mapper the map function
	 * @return
	 */
	public static <T,S> BiPredicate<T, S> mappedIsEqual(MathCalculator<T> mc,Function<S,T> mapper){
		return (x,y)->mc.isEqual(x, mapper.apply(y));
	}
	
	public static <T> T max(T a,T b,Comparator<T> mc){
		int comp = mc.compare(a,b);
		return comp>0? a : b;
	}
	public static <T> T min(T a,T b,Comparator<T> mc){
		int comp = mc.compare(a,b);
		return comp<0? a : b;
	}
}

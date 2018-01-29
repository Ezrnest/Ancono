/**
 * 2017-10-07
 */
package cn.timelives.java.math;

import java.util.function.BiPredicate;
import java.util.function.Function;

import cn.timelives.java.math.numberModels.MathCalculator;

/**
 * Contains some utilities.
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
	

}

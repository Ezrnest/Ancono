/**
 * 2017-09-22
 */
package cn.timelives.java.math.numberModels;

/**
 * Provides some utility methods.
 * @author liyicheng
 * 2017-09-22 20:35
 *
 */
public final class Calculators {
	/**
	 * 
	 */
	private Calculators() {
	}
	/**
	 * Determines whether the two numbers are the same in sign, which means they are both positive, negative or zero. 
	 * @param x
	 * @param y
	 * @param mc
	 * @return
	 */
	public static <T> boolean isSameSign(T x,T y,MathCalculator<T> mc){
		T z = mc.getZero();
		return mc.compare(x, z) == mc.compare(y, z);
	}
	
	/**
	 * Returns the sign number of {@code x}.
	 * @param x
	 * @param mc
	 * @return
	 */
	public static <T> int signum(T x,MathCalculator<T> mc){
		return mc.compare(x, mc.getZero());
	}
	
	public static <T> boolean isPositive(T x,MathCalculator<T> mc){
		return signum(x,mc) > 0;
	}
	
	public static <T> boolean isNegative(T x,MathCalculator<T> mc){
		return signum(x,mc) < 0;
	}
	
	public static <T> T square(T x,MathCalculator<T> mc){
		return mc.multiply(x, x);
	}
	
	public static <T> T cube(T x,MathCalculator<T> mc){
		return mc.multiply(x, mc.multiply(x, x));
	}
	
	public static <T> T doubleOf(T x,MathCalculator<T> mc){
		return mc.multiplyLong(x, 2l);
	}
	
	public static <T> T half(T x,MathCalculator<T> mc){
		return mc.divideLong(x, 2l);
	}
	
	public static <T> T plus1(T x,MathCalculator<T> mc){
		return mc.add(x, mc.getOne());
	}
	public static <T> T minus1(T x,MathCalculator<T> mc){
		return mc.add(x, mc.getOne());
	}
}

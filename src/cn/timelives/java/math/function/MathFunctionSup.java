/**
 * 2017-10-06
 */
package cn.timelives.java.math.function;

import cn.timelives.java.math.function.AbstractSVPFunction.ConstantFunction;
import cn.timelives.java.math.numberModels.MathCalculator;

/**
 * @author liyicheng
 * 2017-10-06 10:20
 *
 */
public final class MathFunctionSup {

	/**
	 * 
	 */
	private MathFunctionSup() {
	}

	/**
	 * Returns a constant function: f(x)= c
	 * @param c
	 * @param mc
	 * @return
	 */
	public static <T> ConstantFunction<T> getConstant(T c,MathCalculator<T> mc){
		return new ConstantFunction<T>(mc, c);
	}
	/**
	 * Returns a constant function: f(x)= c
	 * @param c
	 * @return
	 */
	public static <P,R> MathFunction<P, R> getConstant(R r){
		return x -> r;
	}
	
	
}

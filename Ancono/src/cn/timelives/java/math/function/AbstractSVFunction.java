/**
 * 2017-10-06
 */
package cn.timelives.java.math.function;

import java.util.function.Function;

import cn.timelives.java.math.FlexibleMathObject;
import cn.timelives.java.math.numberModels.MathCalculator;

/**
 * @author liyicheng
 * 2017-10-06 10:02
 * 
 */
public abstract class AbstractSVFunction<T> extends FlexibleMathObject<T> implements SVFunction<T>{

	/**
	 * @param mc
	 */
	protected AbstractSVFunction(MathCalculator<T> mc) {
		super(mc);
	}
	
	/*
	 * @see cn.timelives.java.math.FlexibleMathObject#mapTo(java.util.function.Function, cn.timelives.java.math.numberModels.MathCalculator)
	 */
	@Override
	public abstract <N> AbstractSVFunction<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator);

}

/**
 * 
 */
package cn.timelives.java.math.function;

import java.util.function.Function;

import cn.timelives.java.math.FlexibleMathObject;
import cn.timelives.java.math.numberModels.MathCalculator;
import cn.timelives.java.math.numberModels.NumberFormatter;

/**
 * A constant function is a type of MathFunction that 
 * always returns the same result.
 * @author 
 *
 */
public final class ConstantFunction<T> extends AbstractSVFunction<T> implements SVFunction<T>{
	private final T r;
	public ConstantFunction(MathCalculator<T> mc,T r){
		super(mc);
		this.r = r;
	}
	
	
	/*
	 * @see cn.timelives.java.math.function.MathFunction#apply(java.lang.Object)
	 */
	@Override
	public T apply(T x) {
		return r;
	}
	/**
	 * Returns the result.
	 * @return
	 */
	public T getResult() {
		return r;
	}


	/*
	 * @see cn.timelives.java.math.FlexibleMathObject#mapTo(java.util.function.Function, cn.timelives.java.math.numberModels.MathCalculator)
	 */
	@Override
	public <N> ConstantFunction<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator) {
		return new ConstantFunction<N>(newCalculator,mapper.apply(r));
	}


	/*
	 * @see cn.timelives.java.math.FlexibleMathObject#valueEquals(cn.timelives.java.math.FlexibleMathObject)
	 */
	@Override
	public boolean valueEquals(FlexibleMathObject<T> obj) {
		if(!(obj instanceof ConstantFunction)) {
			return false;
		}
		return mc.isEqual(r, ((ConstantFunction<T>)obj).r);
	}

	/*
	 * @see cn.timelives.java.math.FlexibleMathObject#toString(cn.timelives.java.math.numberModels.NumberFormatter)
	 */
	@Override
	public String toString(NumberFormatter<T> nf) {
		return "f(x)="+nf.format(r, mc);
	}
}

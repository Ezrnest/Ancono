/**
 * 2018-02-27
 */
package cn.timelives.java.math.abstractAlgebra.calculator;

import cn.timelives.java.math.numberModels.MathCalculator;
import cn.timelives.java.math.numberModels.MathCalculatorAdapter;

/**
 * A GroupCalculator is a calculator specialized for group.
 * @author liyicheng
 * 2018-02-27 17:41
 *
 */
public interface GroupCalculator<T> extends MonoidCalculator<T> {
	
	/**
	 * Returns the inverse of the element x.
	 * @param x
	 * @return
	 */
	public T inverse(T x);
	
	
	/*
	 */
	@Override
	default T gpow(T x, long n) {
		if(n == 0) {
			return getIdentity();
		}
		if(n>0) {
			return MonoidCalculator.super.gpow(x, n);
		}else {
			T t = MonoidCalculator.super.gpow(x, -n);
			return inverse(t);
		}
	}
	
	/**
	 * Returns a {@link MathCalculator} from the GroupCalculator, mapping the group's operation to "add" in MathCalculator
	 * @param gc
	 * @return
	 */
	public static <T> MathCalculator<T> toMathCalculatorAdd(GroupCalculator<T> gc){
		return new MathCalculatorAdapter<T>() {
			/*
			 * @see cn.timelives.java.math.numberModels.MathCalculatorAdapter#isEqual(java.lang.Object, java.lang.Object)
			 */
			@Override
			public boolean isEqual(T para1, T para2) {
				return gc.isEqual(para1, para2);
			}
			
			/*
			 * @see cn.timelives.java.math.numberModels.MathCalculatorAdapter#add(java.lang.Object, java.lang.Object)
			 */
			@Override
			public T add(T para1, T para2) {
				return gc.apply(para1, para2);
			}
			
			/*
			 * @see cn.timelives.java.math.numberModels.MathCalculatorAdapter#negate(java.lang.Object)
			 */
			@Override
			public T negate(T para) {
				return gc.inverse(para);
			}
			
			/*
			 * @see cn.timelives.java.math.numberModels.MathCalculatorAdapter#getZero()
			 */
			@Override
			public T getZero() {
				return gc.getIdentity();
			}
			
			/*
			 * @see cn.timelives.java.math.numberModels.MathCalculatorAdapter#multiplyLong(java.lang.Object, long)
			 */
			@Override
			public T multiplyLong(T p, long l) {
				return gc.gpow(p, l);
			}
		};
	}
}

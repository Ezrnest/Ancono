/**
 * 2017-10-06
 */
package cn.timelives.java.math;

import cn.timelives.java.math.numberModels.MathCalculator;

/**
 * SVPEquation stands for <i>single variable polynomial inequation</i>.
 * Generally, the inequation can be shown as 
 * <pre>an*x^n + ... + a1*x + a0 <i>op</i> 0 , (an!=0,n>0)</pre>
 * where <i>op</i> is one of the inequation operation(Inequation{@link #getInquationType()}).
 * @author liyicheng
 * 2017-10-06 09:33
 *
 */
public abstract class SVPInequation<T> extends SingleVInquation<T> implements Multinomial<T>{
	
	/**
	 * @param mc
	 * @param op
	 */
	protected SVPInequation(MathCalculator<T> mc, Type op) {
		super(mc, op);
	}
	
	
	
}

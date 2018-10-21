/**
 * 2017-10-07
 */
package cn.timelives.java.math.calculus;

import cn.timelives.java.math.function.SVFunction;

/**
 * An integrable function is a single variable function that 
 * provides method integrate.
 * @author liyicheng
 * 2017-10-07 14:46
 *
 */
public interface Integrable<T> extends SVFunction<T> {
	/**
	 * Returns one of integrations of this function,
	 * the constant may be ignored.
	 * @return integration of {@code this}
	 */
	SVFunction<T> integrate();
}

/**
 * 
 */
package cn.timelives.java.math.prob;

/**
 * Represents density functions {@code p(x)},
 * it is required that {@code p(x)>= 0 } and <text>��<sub>-��</sub><sup>+��</sup>p(x)dx = 1</text> 
 * <p>
 * This function should be the differential of a corresponding distribution function.
 * @author liyicheng
 *
 */
public interface DensityFunction {
	/**
	 * Computes the value p(x)
	 * @param x the parameter
	 * @return p(x)
	 */
	double compute(double x);
}

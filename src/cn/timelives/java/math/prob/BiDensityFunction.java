/**
 * 
 */
package cn.timelives.java.math.prob;

/**
 * Represents density functions {@code p(x,y)},
 * it is required that {@code p(x,y)>= 0 } and <text>�ҡ�p(x,y)dx = 1</text> 
 * <p>
 * This function should be the differential of a corresponding distribution function.
 * @author liyicheng
 *
 */
public interface BiDensityFunction {
	/**
	 * Computes the value p(x,y)
	 * @param x the parameter
	 * @param y the parameter
	 * @return p(x,y)
	 */
	double compute(double x,double y);
}

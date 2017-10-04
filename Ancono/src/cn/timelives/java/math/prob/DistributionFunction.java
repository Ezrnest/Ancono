/**
 * 
 */
package cn.timelives.java.math.prob;

/**
 * Represents distribution functions, the function {@code F(x)} is 
 * required to be 
 * <ul><li>(i) Monotone increasing 
 * <li>(ii) {@code lim F(x)=0,x¡ú-¡Þ},{@code lim F(x)=1,x¡ú+¡Þ}
 * <li>(iii) F(x-0) = F(x)
 * </ul>
 * @author liyicheng
 *
 */
public interface DistributionFunction {
	
	/**
	 * Computes the value F(x)
	 * @param x the parameter
	 * @return F(x)
	 */
	double compute(double x);
	
	
}

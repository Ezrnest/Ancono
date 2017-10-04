/**
 * 
 */
package cn.timelives.java.math.prob;

import java.math.BigDecimal;
import java.math.MathContext;
import static cn.timelives.java.math.prob.PFunctions.*;
/**
 * A class provides basic distributions.
 * @author liyicheng
 *
 */
public final class Distributions {

	/**
	 * 
	 */
	private Distributions() {
	}
	/**
	 * Returns the binomial distribution:{@code b(k;n,p)}.<br>
	 * The number is equal to the probability of getting {@code k} times 
	 * in {@code n} Bernoulli trails.
	 * <pre>(n,k)*p^k*(1-p)^(n-k)</pre>
	 * where (n,k) means the binomial coefficient.  
	 * @param k the number of event excepted to happen
	 * @param n the total number of trails
	 * @param p the probability
	 * @return {@code b(k;n,p)}
	 */
	public static double binomial(int k,int n,double p){
		if(p<0||p>1){
			throw new IllegalArgumentException("p<0||p>1");
		}
		long c = combination(n, k);
		double d = Math.pow(p, k);
		d *= c;
		d *= Math.pow(1d-p, n-k);
		return d;
	}
	
	/**
	 * Returns the binomial distribution:{@code b(k;n,p)}.<br>
	 * The number is equal to the probability of getting {@code k} times 
	 * in {@code n} Bernoulli trails.
	 * <pre>(n,k)*p^k*(1-p)^(n-k)</pre>
	 * where (n,k) means the binomial coefficient.<p>  
	 * Using default precision  of 
	 * {@link MathContext#DECIMAL128}, this method supports larger input and has more accuracy than
	 *  {@link #dstBinomial(int, int, double)}.
	 * @param k the number of event excepted to happen
	 * @param n the total number of trails
	 * @param p the probability
	 * @return {@code b(k;n,p)}
	 */
	public static BigDecimal binomialB(int k,int n,double p){
		if(p<0||p>1){
			throw new IllegalArgumentException("p<0||p>1");
		}
		BigDecimal bd = new BigDecimal(combinationB(n, k));
		BigDecimal pd = new BigDecimal(p);
		bd = bd.multiply(pd.pow(k,MathContext.DECIMAL128));
		pd = BigDecimal.ONE.subtract(pd);
		bd = bd.multiply(pd.pow(n-k,MathContext.DECIMAL128));
		return bd;
	}
	
	/**
	 * Returns the geometry distribution:{@code g(k;p)},<br/>
	 * The result is the probability of the first appearance of the event 
	 * is at the k-th trial.
	 * <pre>(1-p)^(k-1)*p</pre> 
	 * @param k the k-th trial
	 * @param p the probability
	 * @return {@code g(k;p)}
	 */
	public static double geometry(int k,double p){
		return Math.pow(1d-p, k-1) * p;
	}
	
	/**
	 * Returns the geometry distribution:{@code g(k;p)}<br/>
	 * The result is the probability of the first appearance of the event 
	 * is at the k-th trial.
	 * <pre>(1-p)^(k-1)*p</pre> 
	 * @param k the k-th trial
	 * @param p the probability
	 * @return {@code g(k;p)}
	 */
	public static BigDecimal geometryB(int k,double p){
		BigDecimal bd = new BigDecimal(p);
		BigDecimal q = BigDecimal.ONE.subtract(bd);
		return bd.multiply(q.pow(k-1, MathContext.DECIMAL128));
	}
	/**
	 * Returns the Pascal distribution:{@code f(k;r,p)}<br/>
	 * The result is equal to the probability of the r-th success 
	 * happens at the k-th trial.
	 * <pre>(k-1,r-1)*p^r*(1-p)^(k-r)</pre>
	 * where (n,k) means the binomial coefficient.  
	 * @param k k-th trail
	 * @param r r-th success
	 * @param p the probability
	 * @return {@code f(k;r,p)}
	 */
	public static double pasca(int k,int r,double p){
		long l = combination(k-1, r-1);
		double d = Math.pow(p, r);
		d *= l;
		return Math.pow(1-p, k-r)*d;
	}
	/**
	 * Returns Poisson distribution:{@code p(k;λ)}.<br>
	 * This is a good approximation of the binomial distribution(b(k;n,p)) when 
	 * the probability is small enough. The result is calculated as follow:
	 * <p>
	 * e<sup>-λ</sup> * λ<sup>k</sup>/k!
	 * <p>
	 * 
	 * @param k the parameter k
	 * @param λ the multiplication {@code n*p}
	 * @return {@code p(k;λ)}
	 */
	public static double poisson(int k,double λ){
		double re = Math.pow(λ, k);
		re *= Math.exp(-λ);
		return re / factorial(k);
	}
	
	/**
	 * Returns the density function of normal distribution with the given 
	 * parameter:{@code N(μ,σ<sup>2</sup>)}
	 * <p>
	 * 1/σ√(2π) * e<sup>-(x-μ)<sup>2</sup>/2σ<sup>2</sup></sup>
	 * @param μ determines where the maximum value is 
	 * @param σ determines the shape of the function
	 * @return function: {@code p(x)}
	 */
	public static DensityFunction normalDensityFunction(double μ,double σ){
		return x -> normalDensity(μ, σ, x);
	}
	/**
	 * Computes the density function of normal distribution with the given 
	 * parameter: {@code N(μ,σ<sup>2</sup>)}
	 * <p>
	 * 1/σ√(2π) * e<sup>-(x-μ)<sup>2</sup>/2σ<sup>2</sup></sup>
	 * @param μ determines where the maximum value is 
	 * @param σ determines the shape of the function
	 * @param x value to compute
	 * @return {@code p(x)}
	 */
	public static double normalDensity(double μ,double σ,double x){
		double t = (x-μ) / σ;
		t =  t * t / 2d;
		return _sqr_2_pi * Math.exp(-t) / σ;
	}
	private static final double _sqr_2_pi = 1/Math.sqrt(Math.PI+Math.PI);
	
	/**
	 * Computes the density function of standard distribution.
	 * 1/√(2π) * e<sup>-x<sup>2</sup>/2</sup>
	 * @param x 
	 * @return {@code φ(x)}
	 */
	public static double normalDensityStandard(double x){
		return _sqr_2_pi * Math.exp(-x*x/2d);
	}
	/**
	 * Computes the density function of exponent distribution.
	 * <p>
	 * λe<sup>-λx</sup>
	 * @param λ an constant {@code λ>0}
	 * @param x
	 * @return {@code p(x)}
	 */
	public static double exponentDensity(double λ,double x){
		if(x<0){
			return 0d;
		}
		return λ * Math.exp(-λ*x);
	}
	/**
	 * Returns the density function of exponent distribution.
	 * <p>
	 * λe<sup>-λx</sup>
	 * or 0 if {@code x<0}.
	 * @param λ an constant {@code λ>0} (no checking in the method)
	 * @param x
	 * @return function: {@code p(x)}
	 */
	public static DensityFunction exponentDensityFunction(double λ){
		return x -> exponentDensity(λ, x);
	}
	/**
	 * Computes the exponent distribution.
	 * <p>
	 * 1-e<sup>-λx</sup>
	 * or 0 if {@code x<0}.
	 * @param λ an constant {@code λ>0} (no checking in the method)
	 * @param x
	 * @return {@code p(x)}
	 */
	public static double exponent(double λ,double x){
		if(x<0){
			return 0;
		}
		return -Math.expm1(λ*x);
	}
	/**
	 * Returns the exponent distribution function.
	 * <p>
	 * 1-e<sup>-λx</sup>
	 * or 0 if {@code x<0}.
	 * @param λ an constant {@code λ>0} (no checking in the method)
	 * @param x
	 * @return {@code p(x)}
	 */
	public static DistributionFunction exponentDistributionFunction(double λ){
		return x -> exponent(λ, x);
	}
	/**
	 * Returns the equidistribution density function.
	 * <pre>
	 * p(x) = 1/(b-a), a<=x<=b
	 *        0        x<a || x > b
	 * </pre>
	 * Throws an exception if {@code a>=b}.
	 * @param a the lower bound
	 * @param b the upper bound
	 * @return density function
	 */
	public static DensityFunction equidistributionDensity(double a,double b){
		if(a>=b){
			throw new IllegalArgumentException("a>=b");
		}
		final double t = 1/(b-a);
		return x->{
			if(x<a || x > b){
				return 0;
			}else{
				return t;
			}
		};
	}
	/**
	 * Returns the equidistribution function.
	 * <pre>
	 * p(x) = 0,           x<=a
	 *        (x-a)/(b-a), a < x <= b
	 *        1            x>b
	 * </pre>
	 * Throws an exception if {@code a>=b}.
	 * @param a the lower bound
	 * @param b the upper bound
	 * @return density function
	 */
	public static DistributionFunction equidistribution(double a,double b){
		if(a>=b){
			throw new IllegalArgumentException("a>=b");
		}
		final double t = 1/(b-a);
		return x->{
			if(x <=a){
				return 0;
			}else if(x <= b){
				return (x-a)*t;
			}else{
				return 1;
			}
		};
	}
	
	/**
	 * Computes the density function of binary normal distribution :
	 * @param μ1
	 * @param μ2
	 * @param σ1
	 * @param σ2
	 * @param ρ
	 * @param x
	 * @param y
	 * @return
	 */
	public static double binormalDensity(double μ1,double μ2,double σ1,double σ2,double ρ,double x,double y){
		double pt = 1 - ρ*ρ;
		double re = 1 / (2*Math.PI * σ1 * σ2 * Math.sqrt(pt));
		double x_u1 = x - μ1;
		double y_u2 = y-μ2;
		double exp = x_u1 * x_u1 / (σ1*σ1) - 2 * ρ * x_u1 * y_u2 / (σ1 * σ2) + y_u2 * y_u2 / (σ2*σ2);
		exp *= - 1d / 2d / pt;
		re *= Math.exp(exp);
		return re;
	}
	/**
	 * Returns the density function of binary normal distribution :
	 * @param μ1
	 * @param μ2
	 * @param σ1
	 * @param σ2
	 * @param ρ
	 * @param x
	 * @param y
	 * @return
	 */
	public static BiDensityFunction binormalDensityFunction(double μ1,double μ2,double σ1,double σ2,double ρ){
		return (x,y) -> binormalDensity(μ1, μ2, σ1, σ2, ρ, x, y);
	}
}

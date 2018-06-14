/**
 * 2018-03-05
 */
package cn.timelives.java.math.function;

import cn.timelives.java.math.property.Invertible;

/**
 * A bijection function is a function between the elements of two sets, 
 * where each element of one set is paired with exactly one element of the other set,
 *  and each element of the other set is paired with exactly one element of the first set.
 * <p> 
 * In addition to method {@code apply(P)}, an inverted method of {@code deply(R)} is added. 
 * <P>
 * See: <a href="https://en.wikipedia.org/wiki/Bijection">Bijection</a>
 * @author liyicheng
 * 2018-03-05 17:35
 * 
 */
public interface Bijection<P,R> extends MathFunction<P, R>,Invertible<Bijection<R,P>>{

	/**
	 * "Deplies" the function, returns such {@code x} that {@code this.apply(x)=y}.
	 * @param y 
	 * @return <pre>f<sup>-1</sup>(y)</pre>
	 */
	P deply(R y);
	
	
	/*
	 * @see cn.timelives.java.math.property.Invertible#inverse()
	 */
	@Override
	default Bijection<R, P> inverse() {
		Bijection<P,R> f = this;
		return new Bijection<R, P>() {

			@Override
			public P apply(R x) {
				return f.deply(x);
			}

			@Override
			public R deply(P y) {
				return f.apply(y);
			}
			
			/*
			 * @see cn.timelives.java.math.function.Bijection#inverse()
			 */
			@Override
			public Bijection<P, R> inverse() {
				return f;
			}
		};
	}
}

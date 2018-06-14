/**
 * 2018-02-27
 */
package cn.timelives.java.math.algebra.abstractAlgebra.calculator;

/**
 * A GroupCalculator is a calculator specialized for group.
 * @author liyicheng
 * 2018-02-27 17:41
 *
 */
public interface GroupCalculator<T> extends MonoidCalculator<T> {
	
	/**
	 * Returns the inverse of the element x.
	 * @param x an element
	 * @return
	 */
	public T inverse(T x);
	
	/**
	 * Returns {@code x^n}, which is well defined.
	 * <p>
	 * <ul>
	 * <li>If {@code n=0}, returns the identity element.
	 * <li>If {@code n>0}, returns the same result as defined in semigroup.
	 * <lI>If {@code n<0}, returns {@code inverse(gpow(x,-n))}.
	 * <ul>
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
	



}

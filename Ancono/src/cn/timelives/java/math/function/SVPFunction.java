/**
 * 
 */
package cn.timelives.java.math.function;

import java.util.Iterator;


/**
 * Single value polynomial function. Generally, the equation can be shown as 
 * <pre>an*x^n + ... + a1*x + a0 , (an!=0,n>0)</pre>
 * @author liyicheng
 *
 */
public interface SVPFunction<T> extends SVFunction<T>, Iterable<T>{
	
	/**
	 * Returns the coefficient {@code x^n},if {@code n==0} then the 
	 * coefficient {@code a0} will be returned.
	 * @param n
	 * @return
	 * @throws IndexOutOfBoundsException if {@code n} is bigger than {@code getMaxPower()} or 
	 * it is smaller than 0.
	 */
	public T getCoefficient(int n);
	
	/**
	 * Returns the max power of x in this equation.
	 * @return an integer number indicates the max power.
	 * 
	 */
	int getMaxPower();
	
	/** 
	 * Iterators the coefficient from the lowest one(a0).
	 */
	@Override
	public default Iterator<T> iterator() {
		return new It<>(this);
	}
	
}
class It<T> implements Iterator<T>{
	private final SVPFunction<T> f;
	private final int max;
	private int n;
	/**
	 * 
	 */
	public It(SVPFunction<T> f) {
		this.f = f;
		this.max = f.getMaxPower();
	}
	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return n<=max;
	}
	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	@Override
	public T next() {
		return f.getCoefficient(n++);
	}
	
	
}

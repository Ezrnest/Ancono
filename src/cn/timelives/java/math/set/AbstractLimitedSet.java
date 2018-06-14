/**
 * 2017-09-08
 */
package cn.timelives.java.math.set;

import cn.timelives.java.math.MathObject;
import cn.timelives.java.math.MathCalculator;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.function.Function;

/**
 * A limited set is a set with limited elements. The 
 * order of the elements in this set should be persistent 
 * so that the method {@link #get(BigInteger)} will always returns 
 * the same value. The usage of {@link #get(BigInteger)} is because  
 * potential huge sets may overflow even {@code long}. The 
 * index given in the method get is required to be 0 or positive.
 *  
 * @author liyicheng
 * 2017-09-08 15:48
 *
 */
public abstract class AbstractLimitedSet<T> extends AbstractCountableSet<T> implements FiniteSet<T> {
	
	/**
	 * @param mc
	 */
	protected AbstractLimitedSet(MathCalculator<T> mc) {
		super(mc);
	}

	/**
	 * Gets an elements from this set. The method 
	 * should always returns the same value.
	 * @param index the index of the element.
	 * @return a number
	 */
	public T get(long index){
		return get(BigInteger.valueOf(index));
	}
	
	/**
	 * Gets an elements from this set. The method 
	 * should always returns the same value.
	 * @param index the index of the element.
	 * @return a number
	 */
	public abstract T get(BigInteger index);
	
	/**
	 * @see cn.timelives.java.math.set.CountableSet#isInfinite()
	 */
	@Override
	public boolean isFinite() {
		return true;
	}
	/**
	 * Returns a list iterator which iterates this limited set.
	 * @return a list iterator
	 */
	public abstract ListIterator<T> listIterator();
	
	/**
	 * @see cn.timelives.java.math.set.CountableSet#iterator()
	 */
	@Override
	public Iterator<T> iterator() {
		return listIterator();
	}
	/**
	 * Add an element to the set and returns a newly created set.
	 * @param element
	 * @return
	 */
	public AbstractLimitedSet<T> add(T element){
		if(contains(element)){
			return this;
		}
		return null;
	}
	
	@Override
	public abstract <N> AbstractLimitedSet<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator);
	
	
	/**
	 * @see MathObject#valueEquals(MathObject)
	 */
	@Override
	public boolean valueEquals(MathObject<T> obj) {
		return false;
	}
	
	/**
	 * @see MathObject#valueEquals(MathObject, java.util.function.Function)
	 */
	@Override
	public <N> boolean valueEquals(MathObject<N> obj, Function<N, T> mapper) {
		return super.valueEquals(obj, mapper);
	}
}

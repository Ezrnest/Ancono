/**
 * 2017-09-08
 */
package cn.timelives.java.math.set;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ListIterator;
import java.util.function.Function;

import cn.timelives.java.math.FieldMathObject;
import cn.timelives.java.math.numberModels.MathCalculator;
import cn.timelives.java.math.numberModels.NumberFormatter;

/**
 * @author liyicheng
 * 2017-09-08 17:36
 *
 */
public final class SingletonSet<T> extends AbstractLimitedSet<T> {
	private final T element;
	/**
	 * @param mc
	 */
	protected SingletonSet(MathCalculator<T> mc,T ele) {
		super(mc);
		this.element = ele;
	}
	/**
	 * @see cn.timelives.java.math.set.FiniteSet#get(long)
	 */
	@Override
	public T get(long index) {
		if(index == 0){
			return element;
		}
		throw new IndexOutOfBoundsException("Index="+index+",size=1");
	}
	/**
	 * @see cn.timelives.java.math.set.FiniteSet#get(java.math.BigInteger)
	 */
	@Override
	public T get(BigInteger index) {
		if(BigInteger.ZERO.equals(index)){
			return element;
		}
		throw new IndexOutOfBoundsException("Index="+index+",size=1");
	}

	/**
	 * @see cn.timelives.java.math.set.FiniteSet#listIterator()
	 */
	@Override
	public ListIterator<T> listIterator() {
		return Collections.singletonList(element).listIterator();
	}

	/**
	 * @see cn.timelives.java.math.set.FiniteSet#mapTo(java.util.function.Function, cn.timelives.java.math.numberModels.MathCalculator)
	 */
	@Override
	public <N> SingletonSet<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator) {
		return new SingletonSet<>(newCalculator,mapper.apply(element));
	}

	/**
	 * @see cn.timelives.java.math.set.CountableSet#size()
	 */
	@Override
	public long size() {
		return 1;
	}
	
	/**
	 * @see cn.timelives.java.math.set.CountableSet#sizeAsBigInteger()
	 */
	@Override
	public BigInteger sizeAsBigInteger() {
		return BigInteger.ONE;
	}
	/**
	 * @see cn.timelives.java.math.set.MathSet#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(T t) {
		return mc.isEqual(element, t);
	}
	/**
	 * @see cn.timelives.java.math.set.FiniteSet#add(java.lang.Object)
	 */
	@Override
	public AbstractLimitedSet<T> add(T element) {
		ArrayList<T> list = new ArrayList<>(2);
		list.add(this.element);
		list.add(element);
		return new CollectionSet<>(mc, list);
	}
	/**
	 * Gets the element.
	 * @return the element
	 */
	public T get(){
		return element;
	}
	
	
	
	/**
	 * @see cn.timelives.java.math.FieldMathObject#valueEquals(cn.timelives.java.math.FieldMathObject)
	 */
	@Override
	public boolean valueEquals(FieldMathObject<T> obj) {
		if(this == obj){
			return true;
		}
		if(!(obj instanceof AbstractLimitedSet)){
			return false;
		}
		AbstractLimitedSet<T> ls = (AbstractLimitedSet<T>) obj;
		return ls.size()==1 && mc.isEqual(element, ls.get(0));
	}

	/**
	 * @see cn.timelives.java.math.FieldMathObject#toString(cn.timelives.java.math.numberModels.NumberFormatter)
	 */
	@Override
	public String toString(NumberFormatter<T> nf) {
		return "{"+nf.format(element, mc)+"}";
	}

}

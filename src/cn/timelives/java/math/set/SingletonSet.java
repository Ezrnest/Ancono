/**
 * 2017-09-08
 */
package cn.timelives.java.math.set;

import cn.timelives.java.math.MathObject;
import cn.timelives.java.math.MathCalculator;
import cn.timelives.java.math.numberModels.api.FlexibleNumberFormatter;
import cn.timelives.java.math.numberModels.api.NumberFormatter;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ListIterator;
import java.util.function.Function;

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
	 */
	@NotNull
    @Override
    public <N> SingletonSet<N> mapTo(@NotNull Function<T, N> mapper, @NotNull MathCalculator<N> newCalculator) {
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
        return getMc().isEqual(element, t);
	}
	/**
	 */
	@Override
	public AbstractLimitedSet<T> add(T element) {
		ArrayList<T> list = new ArrayList<>(2);
		list.add(this.element);
		list.add(element);
        return new CollectionSet<>(getMc(), list);
	}
	/**
	 * Gets the element.
	 * @return the element
	 */
	public T get(){
		return element;
	}
	
	
	
	/**
	 * @see MathObject#valueEquals(MathObject)
	 */
	@Override
    public boolean valueEquals(@NotNull MathObject<T> obj) {
		if(this == obj){
			return true;
		}
		if(!(obj instanceof AbstractLimitedSet)){
			return false;
		}
		AbstractLimitedSet<T> ls = (AbstractLimitedSet<T>) obj;
        return ls.size() == 1 && getMc().isEqual(element, ls.get(0));
	}

	/**
	 * @see MathObject#toString(NumberFormatter)
	 */
	@Override
    public String toString(@NotNull FlexibleNumberFormatter<T, MathCalculator<T>> nf) {
        return "{" + nf.format(element, getMc()) + "}";
	}

}

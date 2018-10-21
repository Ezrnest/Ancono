/**
 * 2017-09-10
 */
package cn.timelives.java.math.set;

import cn.timelives.java.math.MathCalculatorHolder;
import cn.timelives.java.math.algebra.abstractAlgebra.EqualRelation;
import cn.timelives.java.math.algebra.abstractAlgebra.GroupCalculators;
import cn.timelives.java.math.algebra.abstractAlgebra.calculator.EqualPredicate;
import cn.timelives.java.math.function.Bijection;
import cn.timelives.java.math.numberModels.Calculators;
import cn.timelives.java.utilities.CollectionSup;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * @author liyicheng 2017-09-10 14:38
 *
 */
public interface FiniteSet<T> extends CountableSet<T> {
	/**
     * Gets an elements from this set. The method should always returns the identity
	 * value.
	 * 
	 * @param index
	 *            the index of the element.
	 * @return a number
	 */
	T get(long index);

	/**
     * Gets an elements from this set. The method should always returns the identity
	 * value.
	 * 
	 * @param index
	 *            the index of the element.
	 * @return a number
	 */
	T get(BigInteger index);

	/**
	 * Returns a list iterator which iterates this limited set.
	 * 
	 * @return a list iterator
	 */
	ListIterator<T> listIterator();

    @NotNull
    @Override
    default Iterator<T> iterator(){
        return listIterator();
    }

    /**
	 * @see cn.timelives.java.math.set.CountableSet#isFinite()
	 */
	@Override
	default boolean isFinite() {
		return true;
	}

    @NotNull
    @Override
    default <S> FiniteSet<S> mapTo(@NotNull Bijection<T, S> f){
	    return new MappedFiniteSet<>(this,f);
    }



}

class MappedFiniteSet<S,T> implements FiniteSet<T>{
    private final FiniteSet<S> ori;
    private final Bijection<S,T> f;

    MappedFiniteSet(FiniteSet<S> ori, Bijection<S,T> f){
        this.ori = ori;
        this.f = f;
    }

    @Override
    public T get(long index) {
        return f.apply(ori.get(index));
    }

    @Override
    public T get(BigInteger index) {
        return f.apply(ori.get(index));
    }

    @Override
    public ListIterator<T> listIterator() {
        return CollectionSup.mappedListIterator(ori.listIterator(),f);
    }

    @Override
    public long size() {
        return ori.size();
    }

    @Override
    public BigInteger sizeAsBigInteger() {
        return ori.sizeAsBigInteger();
    }

    @Override
    public boolean contains(T t) {
        return ori.contains(f.deply(t));
    }
}


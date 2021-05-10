/**
 * 2017-09-08
 */
package cn.ancono.math.set;

import cn.ancono.math.IMathObject;
import cn.ancono.math.MathObjectReal;
import cn.ancono.math.MathSymbol;
import cn.ancono.math.algebra.abs.calculator.EqualPredicate;
import cn.ancono.math.numberModels.api.NumberFormatter;
import cn.ancono.math.numberModels.api.RealCalculator;
import cn.ancono.utilities.CollectionSup;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Function;

/**
 * A collection set is a set that is created from a collection.
 *
 * @author liyicheng
 * 2017-09-08 17:01
 */
public class CollectionSet<T> extends AbstractLimitedSet<T> {
    private final List<T> list;

    /**
     * Creates a collection set, the collection can be modified after this method.
     *
     * @param mc
     */
    CollectionSet(EqualPredicate<T> mc, Collection<T> coll) {
        super(mc);
        list = new ArrayList<>(coll);
    }

    /**
     * Creates a collection set with a list which should not be modified.
     *
     * @param mc
     * @param list
     */
    CollectionSet(EqualPredicate<T> mc, List<T> list) {
        super(mc);
        this.list = list;
    }

    /**
     * @see cn.ancono.math.set.FiniteSet#get(java.math.BigInteger)
     */
    @Override
    public T get(BigInteger index) {
        int val = index.intValueExact();
        return list.get(val);
    }

    /**
     * @see cn.ancono.math.set.CountableSet#iterator()
     */
    @Override
    public ListIterator<T> listIterator() {
        return list.listIterator();
    }

    /**
     * @see cn.ancono.math.set.CountableSet#size()
     */
    @Override
    public long size() {
        return list.size();
    }

    /**
     * @see cn.ancono.math.set.MathSet#contains(java.lang.Object)
     */
    @Override
    public boolean contains(T t) {
        var mc = getCalculator();
        for (T e : list) {
            if (mc.isEqual(t, e)) {
                return true;
            }
        }
        return false;
    }


    /**
     * @see MathObjectReal#mapTo(RealCalculator, Function)
     */
    @NotNull
    @Override
    public <N> CollectionSet<N> mapTo(@NotNull EqualPredicate<N> newCalculator, @NotNull Function<T, N> mapper) {
        List<N> nlist = new ArrayList<>(list.size());
        for (T t : list) {
            nlist.add(mapper.apply(t));
        }
        return new CollectionSet<>(newCalculator, nlist);
    }

    @Override
    public boolean valueEquals(@NotNull IMathObject<T> obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof CollectionSet) {
            CollectionSet<T> cs = (CollectionSet<T>) obj;
            if (cs.size() != size()) {
                return false;
            }
            return CollectionSup.collectionEqualSorted(list, cs.list, getCalculator()::isEqual);
        }

        return false;
    }


    @NotNull
    @Override
    public String toString(@NotNull NumberFormatter<T> nf) {
        if (size() == 0) {
            return MathSymbol.EMPTY_SET;
        }
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        for (T t : list) {
            sb.append(nf.format(t));
            sb.append(',');
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append('}');
        return sb.toString();
    }

//    @NotNull
//    @Override
//    public <S> CollectionSet<S> mapTo(@NotNull Bijection<T, S> f) {
//        return new CollectionSet<>(RealCalculator.Companion.mappedCalculator(getCalculator(), f),
//                CollectionSup.mapList(list, f));
//    }
}

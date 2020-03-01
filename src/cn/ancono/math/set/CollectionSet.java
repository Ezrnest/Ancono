/**
 * 2017-09-08
 */
package cn.ancono.math.set;

import cn.ancono.math.MathCalculator;
import cn.ancono.math.MathObject;
import cn.ancono.math.MathSymbol;
import cn.ancono.math.function.Bijection;
import cn.ancono.math.numberModels.api.FlexibleNumberFormatter;
import cn.ancono.utilities.ArraySup;
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
    CollectionSet(MathCalculator<T> mc, Collection<T> coll) {
        super(mc);
        list = new ArrayList<>(coll);
    }

    /**
     * Creates a collection set with a list which should not be modified.
     *
     * @param mc
     * @param list
     */
    CollectionSet(MathCalculator<T> mc, List<T> list) {
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
        for (T e : list) {
            if (getMc().isEqual(t, e)) {
                return true;
            }
        }
        return false;
    }


    /**
     * @see MathObject#mapTo(java.util.function.Function, MathCalculator)
     */
    @NotNull
    @Override
    public <N> CollectionSet<N> mapTo(@NotNull Function<T, N> mapper, @NotNull MathCalculator<N> newCalculator) {
        List<N> nlist = new ArrayList<>(list.size());
        for (T t : list) {
            nlist.add(mapper.apply(t));
        }
        return new CollectionSet<>(newCalculator, nlist);
    }

    /**
     * @see MathObject#valueEquals(MathObject)
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean valueEquals(@NotNull MathObject<T> obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof CollectionSet) {
            CollectionSet<T> cs = (CollectionSet<T>) obj;
            if (cs.size() != size()) {
                return false;
            }
            return ArraySup.arrayEqualNoOrder(list.toArray(), cs.list.toArray(), (x, y) -> getMc().isEqual((T) x, (T) y));
        }

        return super.valueEquals(obj);
    }

    /**
     * @see MathObject#valueEquals(MathObject, java.util.function.Function)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <N> boolean valueEquals(@NotNull MathObject<N> obj, @NotNull Function<N, T> mapper) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof CollectionSet) {
            CollectionSet<N> cs = (CollectionSet<N>) obj;
            if (cs.size() != size()) {
                return false;
            }
            return ArraySup.arrayEqualNoOrder(list.toArray(), ArraySup.modifyAll(list.toArray(), (x) ->
                    mapper.apply((N) x)), (x, y) -> getMc().isEqual((T) x, (T) y));
        }

        return super.valueEquals(obj, mapper);
    }

    @NotNull
    @Override
    public String toString(@NotNull FlexibleNumberFormatter<T, MathCalculator<T>> nf) {
        if (size() == 0) {
            return MathSymbol.EMPTY_SET;
        }
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        for (T t : list) {
            sb.append(nf.format(t, getMc()));
            sb.append(',');
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append('}');
        return sb.toString();
    }

    @NotNull
    @Override
    public <S> CollectionSet<S> mapTo(@NotNull Bijection<T, S> f) {
        return new CollectionSet<>(MathCalculator.Companion.mappedCalculator(getMathCalculator(), f),
                CollectionSup.mapList(list, f));
    }
}

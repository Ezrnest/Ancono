/**
 * 2017-09-08
 */
package cn.ancono.math.set;

import cn.ancono.math.algebra.abs.GroupCalculators;
import cn.ancono.math.algebra.abs.calculator.EqualPredicate;
import kotlin.Pair;
import kotlin.Triple;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Provides several utility methods for sets.
 *
 * @author liyicheng
 * 2017-09-08 16:31
 */
public final class MathSets {

    private MathSets() {
        throw new AssertionError("No instance!");
    }


    public static <T> SingletonSet<T> singleton(T t, EqualPredicate<T> mc) {
        return new SingletonSet<>(mc, t);
    }

    @SafeVarargs
    public static <T> CollectionSet<T> asSet(EqualPredicate<T> mc, T... ts) {
        List<T> list = new ArrayList<>(ts.length);
        Outer:
        for (T t : ts) {
            for (T t0 : list) {
                if (mc.isEqual(t, t0)) {
                    //equal
                    continue Outer;
                }
            }
            list.add(t);
        }
        return new CollectionSet<>(GroupCalculators.toMathCalculatorEqual(mc), list);
    }

    @SuppressWarnings("unchecked")
    @SafeVarargs
    public static <T> CollectionSet<T> asSet(T... ts) {
        return asSet(EqualPredicate.naturalEqual((Class<T>) ts[0].getClass()), ts);
    }

    /**
     * Returns a CollectionSet created from the given Collection.
     * A copy of {@code coll} will be created.
     */
    public static <T> CollectionSet<T> fromCollection(Collection<T> coll, EqualPredicate<T> mc) {
        List<T> list = new ArrayList<>(coll.size());
        Outer:
        for (T t : coll) {
            for (T t0 : list) {
                if (mc.isEqual(t, t0)) {
                    //equal
                    continue Outer;
                }
            }
            list.add(t);
        }
        return new CollectionSet<>(GroupCalculators.toMathCalculatorEqual(mc), coll);
    }

    /**
     * Returns the universe set in math, which contains all the elements. The set will
     * returns "Ω" when toString() is called, which indicates is an
     * universe set in math.
     *
     * @return Ω
     */
    @SuppressWarnings("unchecked")
    public static <T> MathSet<T> universe() {
        return (MathSet<T>) UNIVERSE;
    }

    /**
     * Returns the empty set in math, which contains no element. The set will
     * returns "∅" when toString() is called, which indicates is an
     * empty set in math.
     *
     * @return ∅
     */
    @SuppressWarnings("unchecked")
    public static <T> MathSet<T> empty() {
        return (MathSet<T>) EMPTY;
    }


    private static final Universe<?> UNIVERSE = new Universe<>();
    private static final Empty<?> EMPTY = new Empty<>();


    static final class Universe<T> implements MathSet<T> {
        private Universe() {
        }

        @Override
        public boolean contains(T t) {
            Objects.requireNonNull(t);
            return true;
        }


        /**
         * Returns "Ω", which indicates this is an
         * universe set in math.
         */
        @Override
        public String toString() {
            return "Ω";
        }
    }

    static final class Empty<T> implements FiniteSet<T> {

        /**
         * @see cn.ancono.math.set.FiniteSet#get(long)
         */
        @Override
        public T get(long index) {
            throw new IndexOutOfBoundsException("Empty");
        }

        /**
         * @see cn.ancono.math.set.FiniteSet#get(java.math.BigInteger)
         */
        @Override
        public T get(BigInteger index) {
            throw new IndexOutOfBoundsException("Empty");
        }

        /**
         * @see cn.ancono.math.set.FiniteSet#listIterator()
         */
        @Override
        public ListIterator<T> listIterator() {
            return Collections.emptyListIterator();
        }

        /**
         * @see cn.ancono.math.set.CountableSet#iterator()
         */
        @NotNull
        @Override
        public Iterator<T> iterator() {
            return Collections.emptyListIterator();
        }

        /**
         * @see cn.ancono.math.set.CountableSet#stream()
         */
        @Override
        public Stream<T> stream() {
            return Stream.empty();
        }


        /**
         * @see cn.ancono.math.set.CountableSet#size()
         */
        @Override
        public long size() {
            return 0;
        }

        /**
         * @see cn.ancono.math.set.CountableSet#sizeAsBigInteger()
         */
        @Override
        public BigInteger sizeAsBigInteger() {
            return BigInteger.ZERO;
        }

        /**
         * @see cn.ancono.math.set.MathSet#contains(java.lang.Object)
         */
        @Override
        public boolean contains(T t) {
            return false;
        }

        /**
         * @see cn.ancono.math.set.CountableSet#isFinite()
         */
        @Override
        public boolean isFinite() {
            return true;
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "∅";
        }

    }


    /**
     * Determines whether the set contains all the elements in s2.
     */
    public static <T> boolean containsAll(MathSet<T> set, FiniteSet<T> s2) {
        for (T t : s2) {
            if (!set.contains(t)) {
                return false;
            }
        }
        return true;
    }

    @SafeVarargs
    public static <T> MathSet<T> unionOf(MathSet<T>... sets) {
        if (sets.length == 0) {
            return empty();
        }
        if (sets.length == 2) {
            //special simplification
            if (sets[0] == sets[1]) {
                return sets[0];
            }
        }
        for (MathSet<T> s : sets) {
            if (s == UNIVERSE) {
                return universe();
            }
        }
        return new CombinedSet<>(Arrays.asList(sets), CombinedSet.OperatorType.UNION);
    }

    @SafeVarargs
    public static <T> MathSet<T> intersectOf(MathSet<T>... sets) {
        if (sets.length == 0) {
            return universe();
        }
        if (sets.length == 2) {
            //special simplification
            if (sets[0] == sets[1]) {
                return sets[0];
            }
        }
        for (MathSet<T> s : sets) {
            if (s == EMPTY) {
                return empty();
            }
        }
        return new CombinedSet<>(Arrays.asList(sets), CombinedSet.OperatorType.INTERSECT);
    }

    /**
     * Returns the cartesian product of s1 and s2.
     *
     * @param s1 a set
     * @param s2 another set
     */
    public static <T, S> MathSet<Pair<T, S>> cartesianProduct(MathSet<T> s1, MathSet<S> s2) {
        return p -> s1.contains(p.getFirst()) && s2.contains(p.getSecond());
    }

    /**
     * Returns the cartesian product of s1, s2 and s3.
     */
    public static <T, S, R> MathSet<Triple<T, S, R>> cartesianProduct(MathSet<T> s1, MathSet<S> s2, MathSet<R> s3) {
        return p -> s1.contains(p.getFirst()) && s2.contains(p.getSecond()) && s3.contains(p.getThird());
    }

    /**
     * Returns the cartesian product of several MathSets.
     */
    @SafeVarargs
    public static <T> MathSet<List<T>> cartesianProduct(MathSet<T>... sets) {
        if (sets.length == 0) {
            throw new IllegalArgumentException("sets.length == 0");
        }

        return list -> {
            if (list.size() != sets.length) {
                return false;
            }
            int i = 0;
            for (T t : list) {
                if (!sets[i].contains(t)) {
                    return false;
                }
                i++;
            }
            return true;
        };
    }

    /**
     * Returns the cartesian product of two FiniteSets.
     */
    public static <T, S> FiniteSet<Pair<T, S>> cartesianProduct(FiniteSet<T> s1, FiniteSet<S> s2) {
        var list = new ArrayList<Pair<T, S>>(Math.toIntExact(s1.size() * s2.size()));
        for (var t : s1) {
            for (var s : s2) {
                list.add(new Pair<>(t, s));
            }
        }

        return new FiniteSet<>() {
            @Override
            public Pair<T, S> get(long index) {
                return list.get(Math.toIntExact(index));
            }

            @Override
            public Pair<T, S> get(BigInteger index) {
                return list.get(index.intValueExact());
            }

            @Override
            public ListIterator<Pair<T, S>> listIterator() {
                return Collections.unmodifiableList(list).listIterator();
            }

            @Override
            public long size() {
                return list.size();
            }

            @Override
            public BigInteger sizeAsBigInteger() {
                return BigInteger.valueOf(size());
            }

            @Override
            public boolean contains(Pair<T, S> tsPair) {
                return s1.contains(tsPair.getFirst()) && s2.contains(tsPair.getSecond());
            }
        };
    }

//    /**
//     * Returns the descarts product of s1 and s2.
//     * @param s1 a set
//     * @param s2 another set
//     */
//    public static <T,S> MathSet<Pair<T,S>> descartesProduct(MathSet<T> s1, MathSet<S> s2){
//        return p -> s1.contains(p.getFirst()) && s2.contains(p.getSecond());
//    }


    static final class CombinedSet<T> implements MathSet<T> {


        enum OperatorType {
            UNION, INTERSECT
        }

        private final Collection<MathSet<T>> sets;
        private final OperatorType type;

        CombinedSet(Collection<MathSet<T>> sets, OperatorType type) {
            this.sets = sets;
            this.type = type;
        }

        @Override
        public boolean contains(T t) {
            switch (type) {
                case UNION: {
                    for (MathSet<T> s : sets) {
                        if (s.contains(t)) {
                            return true;
                        }
                    }
                    return false;
                }
                case INTERSECT: {
                    for (MathSet<T> s : sets) {
                        if (!s.contains(t)) {
                            return false;
                        }
                    }
                    return true;
                }
            }
            throw new AssertionError();
        }

    }


//    @SuppressWarnings("unchecked")
//    public static <T> List<FiniteSet<T>> partition(FiniteSet<T> set, EqualRelation<T> er) {
//        EqualPredicate<T> mc;
//        if (set instanceof MathCalculatorHolder) {
//            mc = ((MathCalculatorHolder<T>) set).getCalculator();
//        } else {
//            mc = EqualPredicate.naturalEqual();
//        }
//        List<List<T>> parted = CollectionSup.partition(set, er);
//        return CollectionSup.mapList(parted, x ->
//                MathSets.fromCollection(x, mc));
//    }

    public static <T> FiniteSet<T> filter(FiniteSet<T> set, EqualPredicate<T> er, Predicate<T> filter) {
        var list = new ArrayList<T>();
        for (var t : set) {
            if (filter.test(t)) {
                list.add(t);
            }
        }
        return new CollectionSet<>(GroupCalculators.toMathCalculatorEqual(er), list);
    }

    public static <T, R> FiniteSet<R> map(FiniteSet<T> set, EqualPredicate<R> er, Function<T, R> mapper) {
        var list = new ArrayList<R>(Math.toIntExact(set.size()));
        for (var t : set) {
            list.add(mapper.apply(t));
        }
        return new CollectionSet<>(GroupCalculators.toMathCalculatorEqual(er), list);
    }
}

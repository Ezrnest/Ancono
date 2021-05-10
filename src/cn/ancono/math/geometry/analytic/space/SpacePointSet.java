package cn.ancono.math.geometry.analytic.space;

import cn.ancono.math.AbstractMathObject;
import cn.ancono.math.IMathObject;
import cn.ancono.math.MathObjectReal;
import cn.ancono.math.algebra.abs.calculator.EqualPredicate;
import cn.ancono.math.numberModels.api.NumberFormatter;
import cn.ancono.math.set.InfiniteSet;
import cn.ancono.math.set.MathSet;
import cn.ancono.utilities.ArraySup;
import cn.ancono.utilities.CollectionSup;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Space point set is an object that like line,plain or surface. It
 * contains points and thus {@link #contains(SPoint)} method must
 * be implemented. The point set also provides basic set operations such as
 * {@link #intersect(SpacePointSet)} and {@link #union(SpacePointSet)}, which is
 * implemented by default in this abstract class, and proper overriding is
 * recommended.<p>
 * This class also provides basic point sets by method
 * {@link #getEmptySet(EqualPredicate)} and {@link #getUniverseSet(EqualPredicate)}.
 *
 * @param <T>
 * @author liyicheng
 */
public abstract class SpacePointSet<T> extends AbstractMathObject<T, EqualPredicate<T>> implements MathSet<SPoint<T>> {

    protected SpacePointSet(EqualPredicate<T> mc) {
        super(mc);
    }

    /**
     * Determines whether the given point is in this set of point.
     *
     * @param p a point
     * @return {@code true} if this point set contains the point.
     */
    public abstract boolean contains(SPoint<T> p);

    /* (non-Javadoc)
     * @see cn.ancono.math.FlexibleMathObject#mapTo(java.util.function.Function, cn.ancono.math.number_models.MathCalculator)
     */
    @NotNull
    @Override
    public abstract <N> SpacePointSet<N> mapTo(@NotNull EqualPredicate<N> newCalculator, @NotNull Function<T, N> mapper);

    /**
     * Returns the intersect of the two space point sets.
     *
     * @param set a space point set.
     * @return the intersect of the two sets.
     */
    public SpacePointSet<T> intersect(SpacePointSet<T> set) {
        return SpacePointSet.intersectOf(this, set);
    }

    /**
     * Returns the union of the two space point sets.
     *
     * @param set a space point set.
     * @return the union of the two sets.
     */
    public SpacePointSet<T> union(SpacePointSet<T> set) {
        return SpacePointSet.unionOf(this, set);
    }

    /* (non-Javadoc)
     * @see cn.ancono.math.FlexibleMathObject#toString(cn.ancono.math.number_models.NumberFormatter)
     */
    @NotNull
    @Override
    public String toString(@NotNull NumberFormatter<T> nf) {
        return this.getClass().getName();
    }

    /**
     * Return a UniversePointSet which contains all the points.
     *
     * @param mc a {@link EqualPredicate}
     * @return a space point set
     */
    @SuppressWarnings("unchecked")
    public static <T> UniversePointSet<T> getUniverseSet(EqualPredicate<T> mc) {
        UniversePointSet<T> u = (UniversePointSet<T>) usets.get(mc);
        if (u == null) {
            u = new UniversePointSet<>(mc);
            usets.put(mc, u);
        }
        return u;
    }

    /**
     * Return a EmptyPointSet which contains no point.
     *
     * @param mc a {@link EqualPredicate}
     * @return a space point set
     */
    @SuppressWarnings("unchecked")
    public static <T> EmptyPointSet<T> getEmptySet(EqualPredicate<T> mc) {
        EmptyPointSet<T> u = (EmptyPointSet<T>) esets.get(mc);
        if (u == null) {
            u = new EmptyPointSet<>(mc);
            esets.put(mc, u);
        }
        return u;
    }

    /**
     * Returns an empty set if it is {@code null}, or return the given set.
     *
     * @param set
     * @param mc  a {@link EqualPredicate}
     * @return a set, not null.
     */
    public static <T> SpacePointSet<T> cenvertNull(SpacePointSet<T> set, EqualPredicate<T> mc) {
        if (set == null) {
            return getEmptySet(mc);
        }
        return set;
    }

    /**
     * Determines whether the given set is EmptyPointSet.
     *
     * @param set
     * @return true if it is.
     */
    public static boolean isEmptySet(SpacePointSet<?> set) {
        return set instanceof EmptyPointSet;
    }

    /**
     * Determines whether the given set is UniversePointSet.
     *
     * @param set
     * @return true if it is.
     */
    public static boolean isUniverseSet(SpacePointSet<?> set) {
        return set instanceof UniversePointSet;
    }

    private static final Map<EqualPredicate<?>, UniversePointSet<?>> usets = new HashMap<>();
    private static final Map<EqualPredicate<?>, EmptyPointSet<?>> esets = new HashMap<>();

    /**
     * Returns a new intersect set of the two sets.
     * <p>The {@link EqualPredicate} will be taken from the first parameter of {@link MathObjectReal}.
     *
     * @param s1 a space point set.
     * @param s2 another space point set.
     * @return the intersect set
     */
    public static <T> SpacePointSet<T> intersectOf(SpacePointSet<T> s1, SpacePointSet<T> s2) {
        List<SpacePointSet<T>> list = new ArrayList<>(2);
        if (isEmptySet(s1) || isEmptySet(s2)) {
            return getEmptySet(s1.getCalculator());
        }
        list.add(s1);
        list.add(s2);
        return new CombinedSpacePointSet<>(s1.getCalculator(), list, CombinedSpacePointSet.INTERSECT);
    }

    /**
     * Returns a new intersect set of the sets.
     * <p>The {@link EqualPredicate} will be taken from the first parameter of {@link MathObjectReal}.
     *
     * @param sets
     * @return an intersect set
     */
    @SafeVarargs
    public static <T> SpacePointSet<T> intersectOf(SpacePointSet<T>... sets) {
        List<SpacePointSet<T>> list = new ArrayList<>(sets.length);
        EqualPredicate<T> mc = sets[0].getCalculator();
        for (SpacePointSet<T> sps : sets) {
            if (isEmptySet(sps)) {
                return getEmptySet(mc);
            }
            list.add(sps);
        }
        return new CombinedSpacePointSet<>(mc, list, CombinedSpacePointSet.INTERSECT);
    }

    /**
     * Returns a new union set of the two sets.
     * <p>The {@link EqualPredicate} will be taken from the first parameter of {@link MathObjectReal}.
     *
     * @param s1 a space point set.
     * @param s2 another space point set.
     * @return the union set
     */
    public static <T> SpacePointSet<T> unionOf(SpacePointSet<T> s1, SpacePointSet<T> s2) {
        List<SpacePointSet<T>> list = new ArrayList<>(2);
        list.add(s1);
        list.add(s2);
        if (isUniverseSet(s1) || isUniverseSet(s2)) {
            return getUniverseSet(s1.getCalculator());
        }
        return new CombinedSpacePointSet<>(s1.getCalculator(), list, CombinedSpacePointSet.UNION);
    }

    /**
     * Returns a new union set of the sets.
     * <p>The {@link EqualPredicate} will be taken from the first parameter of {@link MathObjectReal}.
     *
     * @param sets
     * @return an union set
     */
    @SafeVarargs
    public static <T> SpacePointSet<T> unionOf(SpacePointSet<T>... sets) {
        List<SpacePointSet<T>> list = new ArrayList<>(sets.length);
        EqualPredicate<T> mc = sets[0].getCalculator();
        for (SpacePointSet<T> sps : sets) {
            if (isUniverseSet(sps)) {
                return getUniverseSet(mc);
            }
            list.add(sps);
        }
        return new CombinedSpacePointSet<>(sets[0].getCalculator(), list, CombinedSpacePointSet.UNION);
    }


    public static final class UniversePointSet<T> extends SpacePointSet<T> implements InfiniteSet<SPoint<T>> {
        /**
         * @param mc
         */
        UniversePointSet(EqualPredicate<T> mc) {
            super(mc);
        }

        /* (non-Javadoc)
         * @see cn.ancono.cn.ancono.utilities.math.spaceAG.SpacePointSet#contains(cn.ancono.cn.ancono.utilities.math.spaceAG.SPoint)
         */
        @Override
        public boolean contains(SPoint<T> p) {
            return true;
        }

        /**
         * @see cn.ancono.math.set.InfiniteSet#cardinality()
         */
        @Override
        public int cardinality() {
            return CARDINAL_REAL;
        }

        /* (non-Javadoc)
         * @see cn.ancono.cn.ancono.utilities.math.FlexibleMathObject#mapTo(java.util.function.Function, cn.ancono.cn.ancono.utilities.math.MathCalculator)
         */
        @NotNull
        @Override
        public <N> UniversePointSet<N> mapTo(@NotNull EqualPredicate<N> newCalculator, @NotNull Function<T, N> mapper) {
            return new UniversePointSet<>(newCalculator);
        }

        /* (non-Javadoc)
         * @see cn.ancono.cn.ancono.utilities.math.FlexibleMathObject#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof UniversePointSet) {
                UniversePointSet<?> set = (UniversePointSet<?>) obj;
                return getCalculator().equals(set.getCalculator());
            }
            return false;
        }

        /* (non-Javadoc)
         * @see cn.ancono.cn.ancono.utilities.math.FlexibleMathObject#hashCode()
         */
        @Override
        public int hashCode() {
            return getCalculator().hashCode();
        }


        @Override
        public boolean valueEquals(@NotNull IMathObject<T> obj) {
            return obj instanceof UniversePointSet;
        }

        /* (non-Javadoc)
         * @see cn.ancono.math.FlexibleMathObject#toString(cn.ancono.math.number_models.NumberFormatter)
         */
        @NotNull
        @Override
        public String toString(@NotNull NumberFormatter<T> nf) {
            return "Universe set";
        }

        /* (non-Javadoc)
         * @see cn.ancono.cn.ancono.utilities.math.spaceAG.SpacePointSet#union(cn.ancono.cn.ancono.utilities.math.spaceAG.SpacePointSet)
         */
        @Override
        public UniversePointSet<T> union(SpacePointSet<T> set) {
            return this;
        }

        /* (non-Javadoc)
         * @see cn.ancono.cn.ancono.utilities.math.spaceAG.SpacePointSet#intersect(cn.ancono.cn.ancono.utilities.math.spaceAG.SpacePointSet)
         */
        @Override
        public SpacePointSet<T> intersect(SpacePointSet<T> set) {
            return set;
        }
    }

    public static final class EmptyPointSet<T> extends SpacePointSet<T> {
        /**
         * @param mc
         */
        EmptyPointSet(EqualPredicate<T> mc) {
            super(mc);
        }

        /* (non-Javadoc)
         * @see cn.ancono.cn.ancono.utilities.math.spaceAG.SpacePointSet#contains(cn.ancono.cn.ancono.utilities.math.spaceAG.SPoint)
         */
        @Override
        public boolean contains(SPoint<T> p) {
            return false;
        }

        /* (non-Javadoc)
         * @see cn.ancono.cn.ancono.utilities.math.FlexibleMathObject#mapTo(java.util.function.Function, cn.ancono.cn.ancono.utilities.math.MathCalculator)
         */
        @NotNull
        @Override
        public <N> EmptyPointSet<N> mapTo(@NotNull EqualPredicate<N> newCalculator, @NotNull Function<T, N> mapper) {
            return new EmptyPointSet<>(newCalculator);
        }

        /* (non-Javadoc)
         * @see cn.ancono.cn.ancono.utilities.math.FlexibleMathObject#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof EmptyPointSet) {
                EmptyPointSet<?> set = (EmptyPointSet<?>) obj;
                return getCalculator().equals(set.getCalculator());
            }
            return false;
        }

        /* (non-Javadoc)
         * @see cn.ancono.cn.ancono.utilities.math.FlexibleMathObject#hashCode()
         */
        @Override
        public int hashCode() {
            return getCalculator().hashCode();
        }

        @Override
        public boolean valueEquals(@NotNull IMathObject<T> obj) {
            return obj instanceof EmptyPointSet;
        }


        @NotNull
        @Override
        public String toString(@NotNull NumberFormatter<T> nf) {
            return "Empty set";
        }

        /* (non-Javadoc)
         * @see cn.ancono.cn.ancono.utilities.math.spaceAG.SpacePointSet#intersect(cn.ancono.cn.ancono.utilities.math.spaceAG.SpacePointSet)
         */
        @Override
        public EmptyPointSet<T> intersect(SpacePointSet<T> set) {
            return this;
        }

        /* (non-Javadoc)
         * @see cn.ancono.cn.ancono.utilities.math.spaceAG.SpacePointSet#union(cn.ancono.cn.ancono.utilities.math.spaceAG.SpacePointSet)
         */
        @Override
        public SpacePointSet<T> union(SpacePointSet<T> set) {
            return set;
        }
    }

    static final class CombinedSpacePointSet<T> extends SpacePointSet<T> {
        private final List<SpacePointSet<T>> list;
        private static final int INTERSECT = 1, UNION = -1;
        private final int flag;

        /**
         * @param mc
         */
        CombinedSpacePointSet(EqualPredicate<T> mc, List<SpacePointSet<T>> list, int flag) {
            super(mc);
            this.list = list;
            this.flag = flag;
        }

        /* (non-Javadoc)
         * @see cn.ancono.cn.ancono.utilities.math.spaceAG.SpacePointSet#contains(cn.ancono.cn.ancono.utilities.math.spaceAG.SPoint)
         */
        @Override
        public boolean contains(SPoint<T> p) {
            switch (flag) {
                case INTERSECT: {
                    for (SpacePointSet<T> set : list) {
                        if (!set.contains(p)) {
                            return false;
                        }
                    }
                    return true;
                }
                case UNION: {
                    for (SpacePointSet<T> set : list) {
                        if (set.contains(p)) {
                            return true;
                        }
                    }
                    return false;
                }
                default: {
                    throw new AssertionError();
                }
            }
        }

        /* (non-Javadoc)
         * @see cn.ancono.cn.ancono.utilities.math.FlexibleMathObject#mapTo(java.util.function.Function, cn.ancono.cn.ancono.utilities.math.MathCalculator)
         */
        @NotNull
        @Override
        public <N> CombinedSpacePointSet<N> mapTo(@NotNull EqualPredicate<N> newCalculator, @NotNull Function<T, N> mapper) {
            List<SpacePointSet<N>> ln = new ArrayList<>(list.size());
            for (SpacePointSet<T> set : list) {
                ln.add(set.mapTo(newCalculator, mapper));
            }
            return new CombinedSpacePointSet<>(newCalculator, ln, flag);
        }

        /* (non-Javadoc)
         * @see cn.ancono.cn.ancono.utilities.math.FlexibleMathObject#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof CombinedSpacePointSet) {
                CombinedSpacePointSet<?> isp = (CombinedSpacePointSet<?>) obj;

                return this.flag == isp.flag && ArraySup.arrayEqualNoOrder(
                        list.toArray(), isp.list.toArray(), (e1, e2) -> e1 == e2 || e1.equals(e2));
            }
            return false;
        }

        /* (non-Javadoc)
         * @see cn.ancono.cn.ancono.utilities.math.FlexibleMathObject#hashCode()
         */
        @Override
        public int hashCode() {
            return list.hashCode();
        }


        @Override
        public boolean valueEquals(@NotNull IMathObject<T> obj) {
            if (obj instanceof CombinedSpacePointSet) {
                CombinedSpacePointSet<T> isp = (CombinedSpacePointSet<T>) obj;
                return this.flag == isp.flag && CollectionSup.listEqual(
                        list, isp.list, SpacePointSet::valueEquals);
            }
            return false;
        }

        /* (non-Javadoc)
         * @see cn.ancono.math.FlexibleMathObject#toString(cn.ancono.math.number_models.NumberFormatter)
         */
        @NotNull
        @Override
        public String toString(@NotNull NumberFormatter<T> nf) {
            return "CombinedSpacePointSet:" + (flag == INTERSECT ? "Intersect" : "Union") + ":" + list.toString();
        }
    }
}


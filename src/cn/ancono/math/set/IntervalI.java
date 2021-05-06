package cn.ancono.math.set;

import cn.ancono.math.MathObject;
import cn.ancono.math.algebra.abs.calculator.AbelGroupCal;
import cn.ancono.math.algebra.abs.calculator.EqualPredicate;
import cn.ancono.math.algebra.abs.calculator.TotalOrderPredicate;
import cn.ancono.math.numberModels.api.NumberFormatter;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.function.Function;

public final class IntervalI<T> extends Interval<T> {

    /**
     * decide the type of this interval
     */
    private final int type;

    private final T left, right;

    static final int LEFT_OPEN_MASK = 0x2,
            RIGHT_OPEN_MASK = 0x4,
            BOTH_OPEN_MASK = LEFT_OPEN_MASK | RIGHT_OPEN_MASK;

    /**
     * Create a new Interval with the given arguments.
     *
     * @param mc              the math calculator,only compare methods will be used.
     * @param downerBound     the downer bound of this interval, or {@code null} to indicate unlimited.
     * @param upperBound      the upper bound of this interval, or {@code null} to indicate unlimited.
     * @param downerInclusive determines whether downer should be inclusive
     * @param upperInclusive  determines whether upper should be inclusive
     */
    public IntervalI(TotalOrderPredicate<T> mc, T downerBound, T upperBound, boolean downerInclusive, boolean upperInclusive) {
        super(mc);
        int type = 0;
        if (downerBound == null || !downerInclusive) {
            type |= LEFT_OPEN_MASK;
        }
        if (upperBound == null || !upperInclusive) {
            type |= RIGHT_OPEN_MASK;
        }

        if (downerBound != null && upperBound != null) {
            @SuppressWarnings("unchecked")
            int t = ((Comparator<T>) mc).compare(downerBound, upperBound);
            if (t > 0) {
                throw new IllegalArgumentException("downerBound > upperBound");
            }
            if (t == 0 && (!downerInclusive || !upperInclusive)) {
                throw new IllegalArgumentException("downerBound==upperBound but not a closed interval");
            }
        }
        this.left = downerBound;
        this.right = upperBound;
        this.type = type;
    }


    IntervalI(TotalOrderPredicate<T> mc, T left, T right, int type) {
        super(mc);
        this.left = left;
        this.right = right;
        this.type = type;
    }


    @Override
    public boolean contains(T n) {
        if (left == null) {
            if (right == null) {
                return true;
            }
            int t = getCalculator().compare(n, right);
            if ((type & RIGHT_OPEN_MASK) == RIGHT_OPEN_MASK) {
                // right open
                return t < 0;
            } else {
                return t < 0 || t == 0;
            }
        } else if (right == null) {
            int t = getCalculator().compare(left, n);
            if ((type & LEFT_OPEN_MASK) == LEFT_OPEN_MASK) {
                //left open
                return t < 0;
            } else {
                return t <= 0;
            }
        }
        int rl = getCalculator().compare(left, n);
        if ((type & LEFT_OPEN_MASK) == LEFT_OPEN_MASK) {
            if (rl >= 0) {
                return false;
            }
        } else {
            if (rl > 0) {
                return false;
            }
        }
        int rr = getCalculator().compare(n, right);
        if ((type & RIGHT_OPEN_MASK) == RIGHT_OPEN_MASK) {
            return rr < 0;
        } else {
            return rr <= 0;
        }

    }


    @Override
    public T upperBound() {
        return right;
    }


    @Override
    public boolean isUpperBoundInclusive() {
        return (type & RIGHT_OPEN_MASK) == 0;
    }


    @Override
    public T downerBound() {
        return left;
    }


    @Override
    public boolean isDownerBoundInclusive() {
        return (type & LEFT_OPEN_MASK) == 0;
    }


    @SuppressWarnings("unchecked")
    @Override
    public T lengthOf() {
        if (left == null || right == null)
            return null;
        return ((AbelGroupCal<T>) getCalculator()).subtract(right, left);
    }


    private boolean inRangeExclusive(T n) {
        if (right == null) {
            if (left == null) {
                return true;
            }
            return getCalculator().compare(left, n) < 0;
        }
        if (left == null) {
            return getCalculator().compare(n, right) < 0;
        }

        int t = getCalculator().compare(left, n);
        if (t >= 0) {
            return false;
        }
        t = getCalculator().compare(n, right);
        return t < 0;
    }


    private void thr(T n) throws IllegalArgumentException {
        throw new IllegalArgumentException("n = " + n);
    }


    @Override
    public Interval<T> downerPart(T n) {
        if (inRangeExclusive(n)) {
            return new IntervalI<>(getCalculator(), left, n, type);
        }
        thr(n);
        return null;
    }


    @Override
    public Interval<T> downerPart(T n, boolean include) {
        if (inRangeExclusive(n)) {
            return new IntervalI<>(getCalculator(), left, n, (type & LEFT_OPEN_MASK) | (include ? 0 : RIGHT_OPEN_MASK));
        }
        thr(n);
        return null;
    }


    @Override
    public Interval<T> upperPart(T n) {
        if (inRangeExclusive(n)) {
            return new IntervalI<>(getCalculator(), n, right, type);
        }
        thr(n);
        return null;
    }


    @Override
    public Interval<T> upperPart(T n, boolean include) {
        if (inRangeExclusive(n)) {
            return new IntervalI<>(getCalculator(), n, right, (type & RIGHT_OPEN_MASK) | (include ? 0 : LEFT_OPEN_MASK));
        }
        thr(n);
        return null;
    }


    @Override
    public Interval<T> expandUpperBound(T n) {
        if (getCalculator().compare(right, n) < 0) {
            return new IntervalI<>(getCalculator(), left, n, type);
        }
        thr(n);
        return null;
    }


    @Override
    public Interval<T> expandUpperBound(T n, boolean include) {
        if (getCalculator().compare(right, n) < 0) {
            return new IntervalI<>(getCalculator(), left, n, (type & LEFT_OPEN_MASK) | (include ? 0 : RIGHT_OPEN_MASK));
        }
        thr(n);
        return null;
    }


    @Override
    public Interval<T> expandDownerBound(T n) {
        if (getCalculator().compare(left, n) < 0) {
            return new IntervalI<>(getCalculator(), n, right, type);
        }
        thr(n);
        return null;
    }


    @Override
    public Interval<T> expandDownerBound(T n, boolean include) {
        if (getCalculator().compare(left, n) < 0) {
            return new IntervalI<>(getCalculator(), n, right, (type & RIGHT_OPEN_MASK) | (include ? 0 : LEFT_OPEN_MASK));
        }
        thr(n);
        return null;
    }


    @Override
    public Interval<T> sameTypeInterval(T downerBound, T upperBound) {
        if (downerBound == null || upperBound == null) {
            throw new NullPointerException();
        }
        return new IntervalI<>(getCalculator(), downerBound, upperBound, type);
    }


    @Override
    public boolean contains(Interval<T> iv) {
        T iL = iv.downerBound();
        T iR = iv.upperBound();
        //left side judge
        if (left != null) {
            int t = getCalculator().compare(left, iL);
            if (t > 0) {
                return false;
            }
            if (t == 0 && iv.isDownerBoundInclusive() && !isDownerBoundInclusive()) {
                return false;
            }
        }
        if (right != null) {
            int t = getCalculator().compare(iR, right);
            if (t > 0) {
                return false;
            }
            if (t == 0 && iv.isUpperBoundInclusive() && !isUpperBoundInclusive()) {
                return false;
            }
        }
        return true;
    }


    @Override
    public Interval<T> intersect(Interval<T> iv) {
        T iL = iv.downerBound();
        T iR = iv.upperBound();
        if ((right == null || iL == null || getCalculator().compare(right, iL) >= 0) &&
                (iR == null || left == null || getCalculator().compare(iR, left) >= 0)) {
            if (getCalculator().compare(left, iL) < 0) {
                if (getCalculator().isEqual(right, iL) && (!isUpperBoundInclusive() || !iv.isDownerBoundInclusive())) {
                    return null;
                }
                return new IntervalI<>
                        (getCalculator(), iL, right, iv.isDownerBoundInclusive(), isUpperBoundInclusive());
            } else {
                if (getCalculator().isEqual(left, iR) && (!isDownerBoundInclusive() || !iv.isUpperBoundInclusive())) {
                    return null;
                }
                return new IntervalI<>
                        (getCalculator(), left, iR, isDownerBoundInclusive(), iv.isUpperBoundInclusive());
            }
        }
        return null;

    }


    @NotNull
    @Override
    public String toString() {
        return toString(NumberFormatter.defaultFormatter());
    }

    /* (non-Javadoc)
     * @see cn.ancono.math.Interval#toString(cn.ancono.math.number_models.NumberFormatter)
     */
    @NotNull
    @Override
    public String toString(@NotNull NumberFormatter<T> nf) {
        StringBuilder sb = new StringBuilder();
        if (isDownerBoundInclusive()) {
            sb.append('[');
        } else {
            sb.append('(');
        }
        if (left == null) {
            sb.append("-∞");
        } else {
            sb.append(nf.format(left));
        }
        sb.append(',');
        if (right == null) {
            sb.append("+∞");
        } else {
            sb.append(nf.format(right));
        }
        if (isUpperBoundInclusive()) {
            sb.append(']');
        } else {
            sb.append(')');
        }
        return sb.toString();
    }

    @NotNull
    @Override
    public <N> Interval<N> mapTo(@NotNull EqualPredicate<N> newCalculator, @NotNull Function<T, N> mapper) {
        return new IntervalI<>((TotalOrderPredicate<N>) newCalculator, mapper.apply(left), mapper.apply(right), type);
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Interval) {
            Interval<?> iv = (Interval<?>) obj;
            if (isDownerBoundInclusive() == iv.isDownerBoundInclusive()
                    && isUpperBoundInclusive() == iv.isUpperBoundInclusive()) {
                return getCalculator().equals(iv.getCalculator()) && (left == null ? iv.downerBound() == null : left.equals(iv.downerBound())) &&
                        (right == null ? iv.upperBound() == null : right.equals(iv.upperBound()));
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = getCalculator().hashCode();
        hash = hash + 31 * type;
        hash = hash * 37 + left.hashCode();
        hash = hash * 37 + right.hashCode();
        return hash;
    }


//    @Override
//    public <N> boolean valueEquals(@NotNull MathObject<N> obj, @NotNull Function<N, T> mapper) {
//        if (obj instanceof Interval) {
//            Interval<N> iv = (Interval<N>) obj;
//            if (isDownerBoundInclusive() == iv.isDownerBoundInclusive()
//                    && isUpperBoundInclusive() == iv.isUpperBoundInclusive()) {
//                N iL = iv.downerBound();
//                N iR = iv.upperBound();
//                if (mappingSideNotEquals(mapper, iL, left)) return false;
//                return !mappingSideNotEquals(mapper, iR, right);
//
//            }
//        }
//        return false;
//    }
//
//    private <N> boolean mappingSideNotEquals(@NotNull Function<N, T> mapper, N iR, T right) {
//        if (iR == null) {
//            return right != null;
//        } else if (right == null) {
//            return true;
//        } else {
//            T iRM = mapper.apply(iR);
//            return !getCalculator().isEqual(iRM, right);
//        }
//    }

    @Override
    public boolean valueEquals(@NotNull MathObject<T, TotalOrderPredicate<T>> obj) {
        if (obj instanceof Interval) {
            Interval<T> iv = (Interval<T>) obj;
            if (isDownerBoundInclusive() == iv.isDownerBoundInclusive()
                    && isUpperBoundInclusive() == iv.isUpperBoundInclusive()) {
                T iL = iv.downerBound();
                T iR = iv.upperBound();
                if (sideNotEquals(iL, left)) return false;
                return !sideNotEquals(iR, right);

            }
        }
        return false;
    }

    private boolean sideNotEquals(T side1, T side2) {
        if (side1 == null) {
            return side2 != null;
        } else if (side2 == null) {
            return true;
        } else {
            return !getCalculator().isEqual(side1, side2);
        }
    }


}

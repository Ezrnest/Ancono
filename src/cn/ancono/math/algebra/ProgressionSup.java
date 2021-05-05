package cn.ancono.math.algebra;

import cn.ancono.math.MathCalculator;
import cn.ancono.math.algebra.abs.calculator.EqualPredicate;
import cn.ancono.math.algebra.abs.calculator.FieldCalculator;
import cn.ancono.math.algebra.abs.calculator.RingCalculator;
import cn.ancono.math.numberModels.api.FlexibleNumberFormatter;
import cn.ancono.utilities.ArraySup;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static cn.ancono.math.algebra.Progression.UNLIMITED;


/**
 * Contains some basic math progressions.
 *
 * @author lyc
 */
public final class ProgressionSup {
    private ProgressionSup() {
        throw new AssertionError();
    }

    public static class ArithmeticProgression<T> extends Progression<T> {

        private final T k, b;

        /**
         * a_n = k*n + b
         *
         * @param k
         * @param b
         */
        private ArithmeticProgression(T k, T b, RingCalculator<T> mc) {
            super(mc, UNLIMITED);
            this.k = k;
            this.b = b;
        }

        private ArithmeticProgression(T k, T b, long size, RingCalculator<T> mc) {
            super(mc, size);
            this.k = k;
            this.b = b;
        }

        @NotNull
        @Override
        public RingCalculator<T> getCalculator() {
            return (RingCalculator<T>) super.getCalculator();
        }

        @Override
        public T get(long index) {
            if (inRange(index)) {
                var mc = (RingCalculator<T>) getCalculator();
                return mc.add(mc.multiplyLong(k, index), b);
            }
            throw new IndexOutOfBoundsException("for index:" + index);
        }


        @Override
        public T[] toArray() {
            if (length == UNLIMITED) {
                return null;
            }
            if (length > ArraySup.MAX_ARRAY_SIZE) {
                throw new IllegalArgumentException("Progression length exceeds max array length");
            }
            int len = (int) length;
            @SuppressWarnings("unchecked")
            T[] arr = (T[]) new Object[len];
            T t = b;
            var mc = getCalculator();
            for (int i = 0; i < len; i++) {
                t = mc.add(t, k);
                arr[i] = t;
            }
            return arr;
        }

        @NotNull
        @Override
        public Iterator<T> iterator() {
            return new AddIterator(0);
        }


        private class AddIterator extends AbstractIndexIterator<T> {
            AddIterator(long start) {
                super(start, length);
                cur = get(index);
            }

            AddIterator(long start, long end) {
                super(start, end, length);
                cur = get(index);
            }

            private T cur;

            @Override
            public T next() {
                if (hasNext()) {
                    T re = cur;
                    cur = getCalculator().add(cur, k);
                    index++;
                    return re;
                }
                throw new NoSuchElementException();
            }

        }

        @Override
        public Iterator<T> iteratorFrom(long fromIndex) {
            return new AddIterator(fromIndex);
        }

        @Override
        public Iterator<T> iteratorRange(long fromIndex, long endIndex) {
            return new AddIterator(fromIndex, endIndex);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ArithmeticProgression) {
                try {
                    @SuppressWarnings("unchecked")
                    ArithmeticProgression<T> ap = (ArithmeticProgression<T>) obj;
                    return ap.length == length && getCalculator().isEqual(ap.b, b) && getCalculator().isEqual(ap.k, k);
                } catch (ClassCastException x) {
                    //class cast different
                    return false;
                }

            }
            return super.equals(obj);
        }

        /* (non-Javadoc)
         * @see cn.ancono.math.FlexibleMathObject#toString(cn.ancono.math.number_models.NumberFormatter)
         */
        @Override
        public String toString(@NotNull FlexibleNumberFormatter<T> nf) {
            StringBuilder sb = new StringBuilder();
            sb.append("ArithmeticProgression:");
            sb.append("a[n]=(").append(nf.format(k))
                    .append(")n").append("+")
                    .append(nf.format(b));
            return sb.toString();
        }

        @Override
        public Progression<T> limit(long limit) {
            if ((limit > 0) && (length == UNLIMITED || limit <= length)) {
                return new ArithmeticProgression<>(k, b, limit, getCalculator());
            }
            throw new IllegalArgumentException();
        }

        @Override
        public T sumOf(long start, long end) {
            //use the arithmetic progression sum formula :
            // average of the first number and the last and multiply them with the count of number
            var mc = (FieldCalculator<T>) getCalculator();
            T sum = mc.add(get(start), get(end - 1));
            long count = end - start;
            if (count % 2 == 0) {
                //not necessary to divide the number.
                return mc.multiplyLong(sum, count / 2);
            }
            try {
                sum = mc.divideLong(sum, 2);
                sum = mc.multiplyLong(sum, count);
                return sum;
            } catch (RuntimeException re) {
                return super.sumOf(start, end);
            }
        }


        @NotNull
        @Override
        public <N> Progression<N> mapTo(
                @NotNull EqualPredicate<N> newCalculator, @NotNull Function<T, N> mapper) {
            return new ArithmeticProgression<>(mapper.apply(k), mapper.apply(b), length, (RingCalculator<N>) newCalculator);
        }

//        @Override
//        public <N> boolean valueEquals(@NotNull MathObject<N> obj,
//                                       @NotNull Function<N, T> mapper) {
//            if (obj instanceof ArithmeticProgression) {
//                ArithmeticProgression<N> ap = (ArithmeticProgression<N>) obj;
//                var mc = getCalculator();
//                return ap.length == this.length && mc.isEqual(mapper.apply(ap.k), k) && mc.isEqual(mapper.apply(ap.b), b);
//            }
//            return false;
//        }
    }

    public static class ConstantProgression<T> extends Progression<T> {
        private final T constant;


        private ConstantProgression(T constant, long size, EqualPredicate<T> mc) {
            super(mc, size);
            this.constant = constant;
        }

        @Override
        public T get(long index) {
            if (inRange(index)) {
                return constant;
            }
            throw new IndexOutOfBoundsException("for index:" + index);
        }

        @Override
        public T[] toArray() {
            if (length == UNLIMITED) {
                return null;
            }
            if (length > ArraySup.MAX_ARRAY_SIZE) {
                throw new IllegalArgumentException("Progression length exceeds max array length");
            }
            int len = (int) length;
            @SuppressWarnings("unchecked")
            T[] arr = (T[]) new Object[len];
            for (int i = 0; i < len; i++) {
                arr[i] = constant;
            }
            return arr;
        }

        @NotNull
        @Override
        public Iterator<T> iterator() {
            return new ConsIterator<T>(0, this);
        }

        @Override
        public Iterator<T> iteratorFrom(long fromIndex) {
            return new ConsIterator<T>(fromIndex, length, this);
        }

        @Override
        public Iterator<T> iteratorRange(long fromIndex, long endIndex) {
            return new ConsIterator<T>(fromIndex, endIndex, this);
        }

        private static class ConsIterator<T> extends AbstractIndexIterator<T> {
            private final T cons;


            ConsIterator(long startFrom, ConstantProgression<T> conp) {
                super(startFrom, conp.length);
                cons = conp.constant;
            }

            ConsIterator(long startFrom, long endWith, ConstantProgression<T> conp) {
                super(startFrom, endWith, conp.length);
                cons = conp.constant;
            }

            @Override
            public boolean hasNext() {
                return bound == UNLIMITED || index < bound;
            }

            @Override
            public T next() {
                if (hasNext()) {
                    return cons;
                }
                throw new NoSuchElementException();
            }

        }


        @Override
        public Progression<T> limit(long limit) {
            if (limit <= length) {
                return new ConstantProgression<T>(constant, limit, getCalculator());
            }
            throw new IllegalArgumentException();
        }

    }

    /**
     * This kind of geometric progression allows zero as its all elements but {@code q != 0}
     *
     * @param <T>
     * @author lyc
     */
    public static class GeometricProgression<T> extends Progression<T> {

        private final T a0, q;


        /**
         * Construct a new geometric progression with the general formula {@literal a_n = a0 * q ^ n}.
         * Index starts from 0.
         *
         * @param mc
         * @param length
         * @param a0
         * @param q
         */
        protected GeometricProgression(RingCalculator<T> mc, long length, T a0, T q) {
            super(mc, length);
            this.a0 = a0;
            if (mc.isEqual(mc.getZero(), q)) {
                throw new ArithmeticException("q = 0");
            }
            this.q = q;
        }

        @NotNull
        @Override
        public RingCalculator<T> getCalculator() {
            return (RingCalculator<T>) super.getCalculator();
        }

        @Override
        public T get(long index) {
            if (inRange(index)) {
                var mc = getCalculator();
                return mc.multiply(a0, mc.pow(q, index));
            }
            throw new IndexOutOfBoundsException("For index : " + index);
        }

        @NotNull
        @Override
        public Iterator<T> iterator() {
            return new MulIterator(0);
        }

        @Override
        public Iterator<T> iteratorFrom(long fromIndex) {
            return new MulIterator(fromIndex);
        }

        @Override
        public Iterator<T> iteratorRange(long fromIndex, long endIndex) {
            return new MulIterator(fromIndex, endIndex);
        }


        private class MulIterator extends AbstractIndexIterator<T> {

            private T cur;

            MulIterator(long startFrom) {
                super(startFrom, length);
                cur = get(index);
            }

            MulIterator(long startFrom, long endWith) {
                super(startFrom, endWith, length);
                cur = get(index);
            }

            @Override
            public T next() {
                var mc = getCalculator();
                if (hasNext()) {
                    T re = cur;
                    cur = mc.multiply(cur, q);
                    return re;
                }
                throw new NoSuchElementException();
            }
        }

        @Override
        public T sumOf(long start, long end) {
            var mc = (FieldCalculator<T>) getCalculator();
            if (inRange(start) && inRange(end - 1) && start < end) {
                //use the general formula of geometric progression
                if (mc.isEqual(a0, mc.getZero())) {
                    return mc.getZero();
                }
                long count = end - start;
                if (mc.isEqual(q, mc.getOne())) {
                    return mc.multiplyLong(a0, count);
                }
                T upper = mc.multiply(a0, mc.subtract(mc.pow(q, end), mc.pow(q, start)));
                return mc.divide(upper, mc.subtract(q, mc.getOne()));
            }
            throw new IllegalArgumentException();
        }


        @NotNull
        @Override
        public <N> Progression<N> mapTo(@NotNull EqualPredicate<N> newCalculator, @NotNull Function<T, N> mapper) {
            N a0N = mapper.apply(a0);
            N qN = mapper.apply(q);
            return new GeometricProgression<>((RingCalculator<N>) newCalculator, length, a0N, qN);
        }
    }

    /**
     * Create an arithmetic progression with the first element {@code a} and the common difference {@code d}.
     * The general formula will be {@literal a_n = a0 + n*d}
     *
     * @param a0 the first element
     * @param d  the common difference
     * @param mc a MathCalculator
     * @return an ArithmeticProgression
     */
    public static <T> ArithmeticProgression<T> asFirstElementAndDifferece(T a0, T d, RingCalculator<T> mc) {
        return new ArithmeticProgression<>(d, a0, mc);
    }

    /**
     * Create an arithmetic progression of the general formula {@literal a_n = k * n + b}
     *
     * @param k  a factor
     * @param b  another factor
     * @param mc a MathCalculator
     * @return an ArithmeticProgression
     */
    public static <T> ArithmeticProgression<T> createArithmeticProgression(T k, T b, RingCalculator<T> mc) {
        return new ArithmeticProgression<>(k, b, mc);
    }

    /**
     * Create a new GeometricProgression with the general formula {@code p_n = a0 * k ^ n}.
     *
     * @param a0 factor 1
     * @param k  factor 2 , non-zero
     * @param mc a MathCalculator
     * @return a new GeometricProgression
     */
    public static <T> GeometricProgression<T> createGeometricProgression(T a0, T k, RingCalculator<T> mc) {
        return new GeometricProgression<T>(mc, UNLIMITED, a0, k);
    }

    /**
     * Create a constant progression with the constant {@code constant}.
     *
     * @param mc       a MathCalculator
     * @param constant the constant
     * @return a new ConstantProgression
     */
    public static <T> ConstantProgression<T> asConstant(EqualPredicate<T> mc, T constant) {
        return new ConstantProgression<>(constant, UNLIMITED, mc);
    }

    public static <T> ConstantProgression<T> asConstantLimited(T constant, long size, EqualPredicate<T> mc) {
        return new ConstantProgression<>(constant, size, mc);
    }

    /**
     * Gets a collector of stream which provides a progression.
     *
     * @param mc  a {@link MathCalculator}
     * @param <T> the type of the number
     * @return a progression
     */
    public static <T> Collector<T, ?, Progression<T>> getCollector(MathCalculator<T> mc) {
        return Collectors.collectingAndThen(Collectors.toList(), list -> Progression.fromList(list, mc));
    }

    public static <T> Progression<T> fibonacci(T first, T second, MathCalculator<T> mc) {
        return Progression.createProgressionRecur2(mc::add, first, second,
                Progression.UNLIMITED, mc);
    }
}

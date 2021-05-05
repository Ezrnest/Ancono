/*
 *
 */
package cn.ancono.math.geometry.analytic.plane;

import cn.ancono.math.FMathObject;
import cn.ancono.math.MathObject;
import cn.ancono.math.algebra.abs.calculator.EqualPredicate;
import cn.ancono.math.algebra.abs.calculator.FieldCalculator;
import cn.ancono.math.algebra.abs.calculator.RingCalculator;
import cn.ancono.math.algebra.linear.AbstractVector;
import cn.ancono.math.algebra.linear.LinearEquationSolution;
import cn.ancono.math.algebra.linear.Matrix;
import cn.ancono.math.algebra.linear.Vector;
import cn.ancono.math.function.MathFunction;
import cn.ancono.math.numberModels.api.FlexibleNumberFormatter;
import cn.ancono.math.numberModels.api.RealCalculator;
import cn.ancono.math.numberModels.api.VectorModel;
import kotlin.jvm.functions.Function1;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequencesKt;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * The vector with two argument (x,y) in plane. Always serves as a column vector.
 *
 * @author liyicheng
 */
@SuppressWarnings("SuspiciousNameCombination")
public final class PVector<T> extends AbstractVector<T> implements VectorModel<T, PVector<T>> {
    public final T x, y;
    private T length, lengthSq;

    /**
     *
     */
    protected PVector(T x, T y, RingCalculator<T> mc) {
        super(mc, 2);
        this.x = x;
        this.y = y;
    }


    /* (non-Javadoc)
     * @see cn.ancono.math.AbstractVector#getNumber(int)
     */
    @Override
    public T get(int index) {
        return switch (index) {
            case 0 -> x;
            case 1 -> y;
            default -> throw new IndexOutOfBoundsException("Index=" + index + " is out of bounds");
        };
    }


    /* (non-Javadoc)
     * @see cn.ancono.math.AbstractVector#toArray()
     */

    /* (non-Javadoc)
     * @see cn.ancono.math.AbstractVector#toArray(java.lang.Object[])
     */
    public T[] toArray(T[] arr) {
        if (arr.length < 2) {
            arr = Arrays.copyOf(arr, 2);
        }
        arr[0] = x;
        arr[1] = y;
        return arr;
    }

    @Override
    public @NotNull List<T> toList() {
        return Arrays.asList(x, y);
    }

    @SuppressWarnings("unchecked")
    @NotNull
    @Override
    public Sequence<T> elementSequence() {
        return SequencesKt.sequenceOf(x, y);
    }

    /* (non-Javadoc)
     * @see cn.ancono.math.AbstractVector#calLength()
     */
    public T norm() {
        if (length == null) {
            var mc = (RealCalculator<T>) getCalculator();
            length = mc.squareRoot(normSq());
        }
        return length;
    }


    /* (non-Javadoc)
     * @see cn.ancono.math.AbstractVector#calLengthSq()
     */
    public T normSq() {
        if (lengthSq == null) {
            var mc = getCalculator();
            lengthSq = mc.add(mc.multiply(x, x), mc.multiply(y, y));
        }
        return lengthSq;
    }


    /* (non-Javadoc)
     * @see cn.ancono.math.AbstractVector#applyFunction(cn.ancono.math.MathFunction)
     */
    @Override
    public @NotNull PVector<T> applyAll(@NotNull Function1<? super T, ? extends T> f) {
        return new PVector<T>(f.invoke(x), f.invoke(y), getCalculator());
    }


    /* (non-Javadoc)
     * @see cn.ancono.math.Matrix#negative()
     */
    @Override
    public @NotNull PVector<T> negate() {
        var mc = getCalculator();
        return new PVector<T>(mc.negate(x), mc.negate(y), mc);
    }

    /* (non-Javadoc)
     * @see cn.ancono.math.Matrix#transportMatrix()
     */

    @Override
    public @NotNull PVector<T> multiply(long n) {
        var mc = getCalculator();
        return new PVector<>(mc.multiplyLong(x, n), mc.multiplyLong(y, n), mc);
    }

    @Override
    public @NotNull PVector<T> multiply(T n) {
        var mc = getCalculator();
        return new PVector<>(mc.multiply(x, n), mc.multiply(y, n), mc);
    }

    /**
     * Returns the value of x in the vector.
     *
     * @return x
     */
    public T getX() {
        return x;
    }

    /**
     * Returns the value of y in the vector.
     *
     * @return y
     */
    public T getY() {
        return y;
    }

    /**
     * Returns {@code this + s}.
     *
     * @param s another SVector
     * @return this + s
     */
    public @NotNull PVector<T> add(PVector<T> s) {
        var mc = getCalculator();
        return new PVector<>(mc.add(x, s.x), mc.add(y, s.y), mc);
    }

    /**
     * Returns {@code this - s}.
     *
     * @param s another SVector
     * @return this - s
     */
    public @NotNull PVector<T> subtract(PVector<T> s) {
        var mc = getCalculator();
        return new PVector<>(mc.subtract(x, s.x), mc.subtract(y, s.y), mc);
    }

    @NotNull
    @Override
    public PVector<T> divide(T k) {
        var mc = (FieldCalculator<T>) getCalculator();
        return new PVector<>(mc.divide(x, k), mc.divide(y, k), mc);
    }

    /**
     * Returns the inner(scalar) product of {@code this} and {@code s}, which
     * is equal to <pre>
     * this · s
     * </pre>
     *
     * @param s
     * @return this · s
     */
    public T innerProduct(PVector<T> s) {
        var mc = getCalculator();
        return mc.add(mc.multiply(x, s.x), mc.multiply(y, s.y));
    }

    /**
     * Returns the angle of {@code this} and {@code s}.
     * <pre> arccos(this · s / (|this| |s|))</pre>
     *
     * @param s
     * @return <pre> arccos(this · s / (|this| |s|))</pre>
     */
    public <R> R angle(PVector<T> s, MathFunction<T, R> arccos) {
        var mc = (FieldCalculator<T>) getCalculator();
        T pro = innerProduct(s);
        pro = mc.divide(pro, mc.multiply(norm(), s.norm()));
        return arccos.apply(pro);
    }

    /**
     * Returns the cos value of the angle of {@code this} and {@code s}.
     * <pre>this · s / (|this| |s|)</pre>
     *
     * @param s
     * @return <pre>this · s / (|this| |s|)</pre>
     */
    public T angleCos(PVector<T> s) {
        T pro = innerProduct(s);
        var mc = (FieldCalculator<T>) getCalculator();
        return mc.divide(pro, mc.multiply(norm(), s.norm()));
    }

    @Override
    public boolean isLinearRelevant(@NotNull PVector<T> v) {
        return isParallel(v);
    }

    /**
     * Determines whether the two vectors are parallel.
     */
    public boolean isParallel(PVector<T> s) {
        var mc = (FieldCalculator<T>) getCalculator();
        return mc.isEqual(mc.multiply(x, s.y), mc.multiply(y, s.x));
    }

    /**
     * Determines whether the two vectors are perpendicular.
     *
     * @param s
     * @return
     */
    public boolean isPerpendicular(PVector<T> s) {
        var mc = getCalculator();
        return mc.isZero(innerProduct(s));
    }

    /**
     * Returns a unit vector which is parallel to this vector.
     *
     * @return an unit vector
     */
    public PVector<T> unitVector() {
        T length = norm();
        var mc = (FieldCalculator<T>) getCalculator();
        PVector<T> s = new PVector<>(mc.divide(x, length),
                mc.divide(y, length),
                mc);
        s.length = mc.getOne();
        s.lengthSq = mc.getOne();
        return s;
    }

    /**
     * Returns a SVector that has the identity direct of this but length is given.
     *
     * @param len the length
     * @return a new SVector
     */
    public PVector<T> parallel(T len) {
        T length = norm();
        var mc = (FieldCalculator<T>) getCalculator();
        PVector<T> s = new PVector<>(mc.multiply(len, mc.divide(x, length)),
                mc.multiply(len, mc.divide(y, length)),
                mc);
        s.length = len;
        return s;
    }

    /**
     * Determines whether this vector is a zero vector.
     */
    public boolean isZero() {
        var mc = getCalculator();
        return mc.isZero(x) && mc.isZero(y);
    }

    /**
     * Rotate this vector by {@code angle} in the anti-clockwise direction. The result is:
     * <pre>
     * (cos x -sinx)( x )
     * (sin x cos x)( y )
     * </pre>
     *
     * @param angle the rotation angle, in the anti-clockwise direction.
     * @return a new vector after rotation.
     */
    public PVector<T> rotate(T angle) {
        //The rotate matrix is
        //(cos x -sinx)
        //(sin x cos x)
        var mc = (RealCalculator<T>) getCalculator();
        return TransMatrix.rotate(angle, mc).transform(this);
    }

    @NotNull
    @Override
    public <N> PVector<N> mapTo(@NotNull EqualPredicate<N> newCalculator, @NotNull Function<T, N> mapper) {
        PVector<N> sn = new PVector<>(mapper.apply(x), mapper.apply(y), (RingCalculator<N>) newCalculator);
        if (length != null) {
            sn.length = mapper.apply(length);
        }
        if (lengthSq != null) {
            sn.lengthSq = mapper.apply(lengthSq);
        }
        return sn;
    }


    @NotNull
    @Override
    public String toString(@NotNull FlexibleNumberFormatter<T> nf) {
        return '(' + nf.format(x) +
                ',' + nf.format(y) +
                ')';
    }

    private int hashCode = 0;

    @Override
    public int hashCode() {
        if (hashCode == 0) {
            int hash = 1;
            hash = hash * 31 + x.hashCode();
            hash = hash * 31 + y.hashCode();
            hashCode = hash;
        }
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof PVector) {
            PVector<?> sv = (PVector<?>) obj;
            return x.equals(sv.x) && y.equals(sv.y);
        }
        return super.equals(obj);
    }

    @Override
    public boolean valueEquals(@NotNull FMathObject<T, RingCalculator<T>> obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof PVector) {
            PVector<T> s = (PVector<T>) obj;
            var mc = getCalculator();
            return mc.isEqual(x, s.x) &&
                    mc.isEqual(y, s.y);
        }
        return super.valueEquals(obj);
    }

    /**
     * Returns the reduce of the vector, try to reduce {@code this}
     * into <pre>ax + by </pre>
     * This three vector must not be parallel.
     *
     * @param x
     * @param y
     * @return a PVector of (a,b)
     * @see
     */
    public PVector<T> reduce(PVector<T> x, PVector<T> y) {
        if (x.isParallel(y)) {
            throw new IllegalArgumentException("Parallel");
        }
//Matrix.so
        LinearEquationSolution<T> sol = Matrix.solveLinear(Matrix.fromVectors(true, x, y), this);
        if (sol.isSingle()) {
            throw new ArithmeticException("Not single?");
        }
        return fromVector(sol.getSpecial());
    }


    /**
     * Create a vector with the given x y  arguments.
     *
     * @param x
     * @param y
     * @param mc a {@link RealCalculator}
     * @return a new SVector
     */
    public static <T> PVector<T> valueOf(T x, T y, RingCalculator<T> mc) {
        if (x == null || y == null) {
            throw new NullPointerException("");
        }
        return new PVector<>(x, y, mc);
    }

    /**
     * Returns a vector of
     * <pre>
     * __
     * AB
     * </pre>
     *
     * @param A  point A
     * @param B  point B
     * @param mc a {@link RealCalculator}
     * @return a new vector
     */
    public static <T> PVector<T> vector(Point<T> A, Point<T> B, RingCalculator<T> mc) {
        return new PVector<>(mc.subtract(B.x, A.x), mc.subtract(B.y, A.y), mc);
    }

    /**
     * Returns a vector of
     * <pre>
     * __
     * AB
     * </pre>
     * <p>The {@link RealCalculator} will be taken from the first parameter of {@link MathObject}
     *
     * @param A point A
     * @param B point B
     * @return a new vector
     */
    public static <T> PVector<T> vector(Point<T> A, Point<T> B) {
        return vector(A, B, A.getCalculator());
    }

    /**
     * Create a vector with the array,if the
     * array's length is not equal to 2, only the first three element will be considered.
     *
     * @param xy
     * @param mc a {@link RealCalculator}
     * @return a new SVector
     */
    public static <T> PVector<T> vector(T[] xy, RealCalculator<T> mc) {
        if (xy.length < 2) {
            throw new IllegalArgumentException("Not enough length");
        }
        //check not null
        if (xy[0] != null && xy[1] != null) {
            return new PVector<>(xy[0], xy[1], mc);
        }
        throw new NullPointerException("null in xy");

    }

    /**
     * Returns the sum of the vectors, this method is generally faster than add the
     * vectors one by one because it reduce the cost to create new objects.
     *
     */
    @SafeVarargs
    public static <T> PVector<T> sum(PVector<T>... vectors) {
        RingCalculator<T> mc = vectors[0].getCalculator();
        var arr = new ArrayList<T>(vectors.length);
        for (PVector<T> vector : vectors) {
            arr.add(vector.x);
        }
        T xm = mc.sum(arr);
        for (int i = 0; i < vectors.length; i++) {
            arr.set(i, vectors[i].y);
        }
        T ym = mc.sum(arr);
        return new PVector<T>(xm, ym, mc);
    }

    /**
     * Create the SVector through another vector, the method only considers
     * the first two dimensions of the given vector.
     * <p>Notice: The MathCalculator of the new vector will be the identity as the vector's.
     *
     * @param v a vector whose size is bigger than or equal to 2.
     * @return a new SVector
     */
    public static <T> PVector<T> fromVector(Vector<T> v) {
        if (v.getSize() < 2) {
            throw new IllegalArgumentException("Too small");
        }
        return new PVector<T>(v.get(0), v.get(1), v.getCalculator());
    }

    /**
     * Returns a vector according to the list first three elements.
     *
     * @param list a list
     * @param mc   a {@link RealCalculator}
     * @return a new vector
     */
    public static <T> PVector<T> fromList(List<T> list, RealCalculator<T> mc) {
        return new PVector<T>(list.get(0), list.get(1), mc);
    }

    /**
     * Gets a zero vector:{@literal (0,0)}
     *
     * @param mc
     * @return a new vector
     */
    public static <T> PVector<T> zeroVector(RealCalculator<T> mc) {
        @SuppressWarnings("unchecked")
        PVector<T> v = (PVector<T>) zvs.get(mc);
        if (v == null) {
            T z = mc.getZero();
            v = new PVector<T>(z, z, mc);
            zvs.put(mc, v);
        }
        return v;

    }

    private static final Map<RealCalculator<?>, PVector<?>> zvs = new ConcurrentHashMap<>();

}

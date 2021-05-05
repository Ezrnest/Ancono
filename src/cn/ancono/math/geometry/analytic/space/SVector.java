package cn.ancono.math.geometry.analytic.space;

import cn.ancono.math.AbstractMathObject;
import cn.ancono.math.FMathObject;
import cn.ancono.math.MathCalculator;
import cn.ancono.math.MathObject;
import cn.ancono.math.algebra.abs.calculator.EqualPredicate;
import cn.ancono.math.algebra.abs.calculator.FieldCalculator;
import cn.ancono.math.algebra.abs.calculator.RingCalculator;
import cn.ancono.math.algebra.linear.*;
import cn.ancono.math.function.MathFunction;
import cn.ancono.math.numberModels.Calculators;
import cn.ancono.math.numberModels.api.AlgebraModel;
import cn.ancono.math.numberModels.api.FlexibleNumberFormatter;
import kotlin.jvm.functions.Function1;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequencesKt;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * The SVector is a column vector with length of 3,
 * and it is the basic part for the relationship management of plane, line and other
 * space objects.
 * <p>
 * The vector can be commonly shown as (x,y,z), where the first element (0 for index) is
 * the length in x coordinate, and the second element for y, the last for z.<p>
 * The vector provides operations like add, minus, opposite, and products. <p>
 * Generally speaking, the vector is often used when creating points, lines, planes and there
 * are lots of method providing a SVector as the result.
 *
 * @param <T>
 * @author liyicheng
 */
@SuppressWarnings("SuspiciousNameCombination")
public final class SVector<T> extends AbstractVector<T> implements AlgebraModel<T, SVector<T>> {

    final T x, y, z;

    private T length;

    private T lenSq;

    SVector(T[] vec, RingCalculator<T> mc) {
        this(vec[0], vec[1], vec[2], mc);
    }

    SVector(T x, T y, T z, RingCalculator<T> mc) {
        super(mc, 3);
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public @NotNull SVector<T> negate() {
        var mc = getCalculator();
        return new SVector<>(mc.negate(x), mc.negate(y), mc.negate(z), mc);
    }


    @Override
    public @NotNull SVector<T> multiply(long n) {
        var mc = getCalculator();
        return new SVector<>(mc.multiplyLong(x, n), mc.multiplyLong(y, n), mc.multiplyLong(z, n), mc);
    }


    @Override
    public @NotNull SVector<T> multiply(T n) {
        var mc = getCalculator();
        return new SVector<>(mc.multiply(x, n), mc.multiply(y, n), mc.multiply(z, n), mc);
    }

    @Override
    public boolean isLinearRelevant(@NotNull SVector<T> v) {
        var mc = getCalculator();
        return mc.isZero(mc.subtract(mc.multiply(x, v.y), mc.multiply(v.x, y)));
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
     * Returns the value of z in the vector.
     *
     * @return z
     */
    public T getZ() {
        return z;
    }

    /**
     * Returns an array of the element.
     *
     * @return a new array of type T
     */
    public T[] toArray() {
        @SuppressWarnings("unchecked")
        T[] arr = (T[]) Array.newInstance(x.getClass(), 3);
        arr[0] = x;
        arr[1] = y;
        arr[2] = z;
        return arr;
    }

    /* (non-Javadoc)
     * @see cn.ancono.math.AbstractVector#toArray(java.lang.Object[])
     */
    public T[] toArray(T[] arr) {
        if (arr.length < 3) {
            arr = Arrays.copyOf(arr, 3);
        }
        arr[0] = x;
        arr[1] = y;
        arr[2] = z;
        return arr;
    }

    @Override
    public @NotNull List<T> toList() {
        return Arrays.asList(x, y, z);
    }

    /**
     * Returns {@code this + s}.
     *
     * @param s another SVector
     * @return this + s
     */
    public @NotNull SVector<T> add(SVector<T> s) {
        var mc = getCalculator();
        return new SVector<>(mc.add(x, s.x), mc.add(y, s.y), mc.add(z, s.z), mc);
    }

    /**
     * Returns {@code this - s}.
     *
     * @param s another SVector
     * @return this - s
     */
    public @NotNull SVector<T> subtract(SVector<T> s) {
        var mc = getCalculator();
        return new SVector<>(mc.subtract(x, s.x), mc.subtract(y, s.y), mc.subtract(z, s.z), mc);
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
    public T innerProduct(SVector<T> s) {
        var mc = getCalculator();
        return mc.add(mc.add(mc.multiply(x, s.x), mc.multiply(y, s.y)), mc.multiply(z, s.z));
    }

    /**
     * Returns the outer product of {@code this} and {@code s}, which
     * is the result of<pre>
     * this × s
     * </pre>
     *
     * @param s
     * @return this × s
     */
    public SVector<T> outerProduct(SVector<T> s) {
        var mc = getCalculator();
        T nx = mc.subtract(mc.multiply(y, s.z), mc.multiply(s.y, z));
        T ny = mc.subtract(mc.multiply(z, s.x), mc.multiply(s.z, x));
        T nz = mc.subtract(mc.multiply(x, s.y), mc.multiply(s.x, y));
        return new SVector<T>(nx, ny, nz, mc);
    }

    /**
     * Returns the length of {@code this}.
     * <pre>|this|</pre>
     *
     * @return |this|
     */
    @Override
    public T norm() {
        if (length == null) {
            var mc = (MathCalculator<T>) getCalculator();
            length = mc.squareRoot(normSq());
        }
        return length;
    }

    @Override
    public T normSq() {
        if (lenSq == null) {
            lenSq = innerProduct(this);
        }
        return lenSq;
    }


    @Override
    public T get(int i) {
        return switch (i) {
            case 0 -> x;
            case 1 -> y;
            case 2 -> z;
            default -> throw new IndexOutOfBoundsException("for index:" + i);
        };
    }

    @SuppressWarnings("unchecked")
    @NotNull
    @Override
    public Sequence<T> elementSequence() {
        return SequencesKt.sequenceOf(x, y, z);
    }

    @NotNull
    @Override
    public SVector<T> applyAll(@NotNull Function1<? super T, ? extends T> f) {
        var mc = getCalculator();
        return new SVector<>(f.invoke(x), f.invoke(y), f.invoke(z), mc);
    }

    @NotNull
    @Override
    public SVector<T> divide(T t) {
        var mc = (FieldCalculator<T>) getCalculator();
        return new SVector<>(
                mc.divide(x, t),
                mc.divide(y, t),
                mc.divide(z, t),
                mc);
    }

    @NotNull
    @Override
    public SVector<T> multiply(@NotNull SVector<T> y) {
        return outerProduct(y);
    }

    /**
     * Returns the angle of {@code this} and {@code s}.
     * <pre> arccos(this �� s / (|this| |s|))</pre>
     *
     * @param s
     * @return <pre> arccos(this �� s / (|this| |s|))</pre>
     */
    public <R> R angle(SVector<T> s, MathFunction<T, R> arccos) {
        T pro = innerProduct(s);
        var mc = (FieldCalculator<T>) getCalculator();
        pro = mc.divide(pro, mc.multiply(norm(), s.norm()));
        return arccos.apply(pro);
    }

    /**
     * Returns the cos value of the angle of {@code this} and {@code s}.
     * <pre>this �� s / (|this| |s|)</pre>
     *
     * @param s
     * @return <pre>this �� s / (|this| |s|)</pre>
     */
    public T angleCos(SVector<T> s) {
        var mc = (FieldCalculator<T>) getCalculator();
        T pro = innerProduct(s);
        return mc.divide(pro, mc.multiply(norm(), s.norm()));
    }

    /**
     * Determines whether the two vectors are parallel.
     *
     * @param s
     * @return
     */
    public boolean isParallel(SVector<T> s) {
        var mc = (FieldCalculator<T>) getCalculator();
        if (!mc.isZero(x)) {
            if (mc.isZero(s.x)) {
                return false;
            }
            return mc.isEqual(mc.multiply(x, s.y), mc.multiply(y, s.x)) &&
                    mc.isEqual(mc.multiply(x, s.z), mc.multiply(z, s.x));
        }
        //x == 0
        if (!mc.isZero(s.x)) {
            return false;
        }
        return mc.isEqual(mc.multiply(y, s.z), mc.multiply(z, s.y));
    }

    /**
     * Determines whether the given vector is in the identity direction of this vector, which means
     * the vector the result of {@code this.angleCos(s)} will be 1. If {@code s} is a zero vector,
     * an exception will be thrown.
     */
    public boolean isOfSameDirection(SVector<T> s) {
        if (s.isZero()) {
            throw new ArithmeticException("s==0");
        }
        if (!isParallel(s)) {
            return false;
        }
        T t1 = x, t2 = s.x;
        var mc = (MathCalculator<T>) getCalculator();
        if (mc.isZero(t1)) {
            if (mc.isZero(y)) {
                t1 = z;
                t2 = s.z;
            } else {
                t1 = y;
                t2 = s.y;
            }
        }
        return Calculators.isSameSign(t1, t2, mc);
    }

    /**
     * Determines whether the two vectors are perpendicular.
     *
     * @param s
     * @return
     */
    public boolean isPerpendicular(SVector<T> s) {
        return getCalculator().isZero(innerProduct(s));
    }

    /**
     * Returns a unit vector which is parallel to this vector.
     *
     * @return an unit vector
     */
    public SVector<T> unitVector() {
        var mc = (FieldCalculator<T>) getCalculator();
        T length = norm();
        SVector<T> s = new SVector<>(mc.divide(x, length),
                mc.divide(y, length),
                mc.divide(z, length), mc);
        s.length = mc.getOne();
        s.lenSq = mc.getOne();
        return s;
    }

    /**
     * Returns a SVector that has the identity direct of this but length is given.
     *
     * @param len the length
     * @return a new SVector
     */
    public SVector<T> parallel(T len) {
        var mc = (FieldCalculator<T>) getCalculator();
        T length = norm();
        SVector<T> s = new SVector<>(mc.multiply(len, mc.divide(x, length)),
                mc.multiply(len, mc.divide(y, length)),
                mc.multiply(len, mc.divide(z, length)), mc);
        s.length = len;
        return s;
    }

    /**
     * Determines whether this vector is a zero vector.
     *
     * @return
     */
    public boolean isZero() {
        var mc = getCalculator();
        return mc.isZero(x) && mc.isZero(y) && mc.isZero(z);
    }


    /**
     * Returns the 'projection' of v.<p>
     * The returned vector {@code r} will on this plane of this and v
     * and be perpendicular to {@code this}.<pre>
     *          ^\
     *          | \ <-v
     *  result->|  \
     *          <----
     *            ^
     *           this
     * </pre>
     *
     * @param v a vector
     * @return
     */
    public SVector<T> perpendicular(SVector<T> v) {
        var mc = (FieldCalculator<T>) getCalculator();
        T k = mc.negate(mc.divide(innerProduct(v), normSq()));
        T nx = mc.add(v.x, mc.multiply(k, x));
        T ny = mc.add(v.y, mc.multiply(k, y));
        T nz = mc.add(v.z, mc.multiply(k, z));
        return new SVector<T>(nx, ny, nz, mc);
    }

    /**
     * Returns the point that this vector represents.
     *
     * @return
     */
    public SPoint<T> asPoint() {
        return SPoint.valueOf(this);
    }

    @NotNull
    @Override
    public <N> SVector<N> mapTo(@NotNull EqualPredicate<N> newCalculator, @NotNull Function<T, N> mapper) {
        SVector<N> sn = new SVector<>(mapper.apply(x), mapper.apply(y), mapper.apply(z), (RingCalculator<N>) newCalculator);
        if (length != null) {
            sn.length = mapper.apply(length);
        }
        if (lenSq != null) {
            sn.lenSq = mapper.apply(lenSq);
        }
        return sn;
    }


    private int hashCode = 0;

    @Override
    public int hashCode() {
        if (hashCode == 0) {
            int hash = 1;
            hash = hash * 31 + x.hashCode();
            hash = hash * 31 + y.hashCode();
            hash = hash * 31 + z.hashCode();
            hashCode = hash;
        }
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SVector) {
            SVector<?> sv = (SVector<?>) obj;
            return x.equals(sv.x) && y.equals(sv.y) && z.equals(sv.z);
        }
        return super.equals(obj);
    }

//    @Override
//    public <N> boolean valueEquals(@NotNull MathObject<N> obj, @NotNull Function<N, T> mapper) {
//        if (obj instanceof SVector) {
//            SVector<N> s = (SVector<N>) obj;
//            return mc.isEqual(x, mapper.apply(s.x)) &&
//                    mc.isEqual(y, mapper.apply(s.y)) &&
//                    mc.isEqual(z, mapper.apply(s.z));
//        }
//        return false;
//    }


    @Override
    public boolean valueEquals(@NotNull FMathObject<T, RingCalculator<T>> obj) {
        if (obj instanceof SVector) {
            var mc = getCalculator();
            SVector<T> s = (SVector<T>) obj;
            return mc.isEqual(x, s.x) &&
                    mc.isEqual(y, s.y) &&
                    mc.isEqual(z, s.z);
        }
        return false;
    }

    /**
     * Returns the reduce of the vector, try to reduce {@code this}
     * into <pre>ax + by + cz</pre>
     * This three vector must not be parallel.
     *
     * @param x
     * @param y
     * @param z
     * @return a SVector of (a,b,c)
     * @see
     */
    public SVector<T> reduce(SVector<T> x, SVector<T> y, SVector<T> z) {

        LinearEquationSolution<T> sol = Matrix.solveLinear(Matrix.fromVectors(true, x, y, z), this);
        if (sol.isInfinite()) {
            throw new ArithmeticException("The given vectors are parallel!");
        }
        if (sol.isEmpty()) {
            throw new ArithmeticException("Cannot reduce.");
        }
        return fromVector(sol.getSpecial());
    }
//	/**
//	 * Reduce this vector, but in two vectors, which means the three vectors must be on the 
//	 * identity plane.
//	 * @param x
//	 * @param y
//	 * @return
//	 */
//	public SVector<T> reduce(SVector<T> x,SVector<T> y){
//		
//	}

    /**
     * Create a vector with the given x y z arguments.
     *
     * @param x
     * @param y
     * @param z
     * @param mc a {@link MathCalculator}
     * @return a new SVector
     */
    public static <T> SVector<T> valueOf(T x, T y, T z, MathCalculator<T> mc) {
        if (x == null || y == null || z == null) {
            throw new NullPointerException("");
        }
        return new SVector<>(x, y, z, mc);
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
     * @param mc a {@link MathCalculator}
     * @return a new vector
     */
    public static <T> SVector<T> vector(SPoint<T> A, SPoint<T> B, MathCalculator<T> mc) {
        return new SVector<>(mc.subtract(B.x, A.x), mc.subtract(B.y, A.y), mc.subtract(B.z, A.z), mc);
    }

    /**
     * Returns a vector of
     * <pre>
     * __
     * AB
     * </pre>
     * <p>The {@link MathCalculator} will be taken from the first parameter of {@link MathObject}
     *
     * @param A point A
     * @param B point B
     * @return a new vector
     */
    public static <T> SVector<T> vector(SPoint<T> A, SPoint<T> B) {
        return vector(A, B, A.getCalculator());
    }

    /**
     * Create a vector with the array,if the
     * array's length is not equal to 3, only the first three element will be considered.
     *
     * @param xyz
     * @param mc  a {@link MathCalculator}
     * @return a new SVector
     */
    public static <T> SVector<T> vector(T[] xyz, MathCalculator<T> mc) {
        if (xyz.length < 3) {
            throw new IllegalArgumentException("Not enough length");
        }
        //check not null
        if (xyz[0] != null && xyz[1] != null && xyz[2] != null) {
            return new SVector<>(xyz[0], xyz[1], xyz[2], mc);
        }
        throw new NullPointerException("null in xyz");

    }

    /**
     * Returns the sum of the vectors, this method is generally faster than add the
     * vectors one by one because it reduce the cost to create new objects.
     *
     * @param vectors
     * @return
     */
    @SafeVarargs
    public static <T> SVector<T> sum(SVector<T>... vectors) {
        RingCalculator<T> mc = vectors[0].getCalculator();
        final int num = vectors.length;
        var arr = new ArrayList<T>(vectors.length);
        for (SVector<T> point : vectors) {
            arr.add(point.x);
        }
        T xm = mc.sum(arr);
        for (int i = 0; i < num; i++) {
            arr.set(i, vectors[i].y);
        }
        T ym = mc.sum(arr);
        for (int i = 0; i < num; i++) {
            arr.set(i, vectors[i].z);
        }
        T zm = mc.sum(arr);
        return new SVector<>(xm, ym, zm, mc);
    }

    /**
     * Create the SVector through another vector, the method only considers
     * the first three dimensions of the given vector.
     * <p>Notice: The MathCalculator of the new vector will be the identity as the vector's.
     *
     * @param v a vector whose size is bigger than or equal to 3.
     * @return a new SVector
     */
    public static <T> SVector<T> fromVector(Vector<T> v) {
        if (v.getSize() < 3) {
            throw new IllegalArgumentException("Too small");
        }
        return new SVector<T>(v.get(0), v.get(1), v.get(2), v.getCalculator());
    }

    /**
     * Returns a vector according to the list first three elements.
     *
     * @param list a list
     * @param mc   a {@link MathCalculator}
     * @return a new vector
     */
    public static <T> SVector<T> fromList(List<T> list, MathCalculator<T> mc) {
        return new SVector<T>(list.get(0), list.get(1), list.get(2), mc);
    }

    /**
     * Returns the mixed product of a,b,c which is equal to <pre>
     * (a �� b) �� c
     * </pre>
     *
     * @param a
     * @param b
     * @param c
     * @return result
     */
    public static <T> T mixedProduct(SVector<T> a, SVector<T> b, SVector<T> c) {
        return MatrixSup.det3(toMatrix(a, b, c), a.getCalculator());
    }

    /**
     * Returns a vector which is on the identity plane of {@code n �� v} and {@code v} and the cosine value of the
     * angle of it and {@code v} is equal to {@code cos}. The mix product of {@code (result �� v) �� n}
     * will be positive.
     *
     * @param v
     * @param n
     * @return
     */
    public static <T> SVector<T> angledVector(SVector<T> v, SVector<T> n, T tan) {
        var mc = (FieldCalculator<T>) v.getCalculator();
        SVector<T> perp = n.outerProduct(v);
        SVector<T> res = perp.multiply(v.norm());
        res = res.add(v.multiply(mc.divide(perp.norm(), tan)));
        return res;
    }

    /**
     * Returns the two possible vectors that are on the identity plane of {@code n �� v} and {@code v}
     * and the cosine value of the
     * angle of either of them and {@code v} is equal to {@code cos}.
     *
     * @param v
     * @param n
     * @param tan
     * @return
     */
    public static <T> List<SVector<T>> angledVectorTwo(SVector<T> v, SVector<T> n, T tan) {
        List<SVector<T>> list = new ArrayList<>(2);
        var mc = v.getCalculator();
        SVector<T> perp = n.outerProduct(v);
        SVector<T> res = perp.multiply(mc.multiply(tan, v.norm()));
        SVector<T> t = v.multiply(perp.norm());
        list.add(res.add(t));
        list.add(res.negate().add(t));
        return list;
    }

    /**
     * Returns the vector that is on the plane {@code pl} and has the angle of the
     * tangent value.
     *
     * @param vec
     * @param pl
     * @return
     */
    public static <T> SVector<T> angleSamePlane(SVector<T> vec, Plane<T> pl, T tan) {
        return angledVector(vec, pl.getNormalVector(), tan);
    }

    /**
     * Determines whether the three vectors are on the same plane.
     */
    public static <T> boolean isOnSamePlane(SVector<T> v1, SVector<T> v2, SVector<T> v3) {
        var mc = v1.getCalculator();
        return mc.isZero(SVector.mixedProduct(v1, v2, v3));
    }

    private static <T> T[][] toMatrix(SVector<T> x, SVector<T> y, SVector<T> z) {
        @SuppressWarnings("unchecked")
        T[][] mat = (T[][]) new Object[3][3];
        mat[0][0] = x.x;
        mat[0][1] = x.y;
        mat[0][2] = x.z;

        mat[1][0] = y.x;
        mat[1][1] = y.y;
        mat[1][2] = y.z;

        mat[2][0] = z.x;
        mat[2][1] = z.y;
        mat[2][2] = z.z;
        return mat;
    }

    /**
     * Create a new vector base, the three SVector must not be parallel.
     * <p>The {@link MathCalculator} will be taken from the first parameter of {@link MathObject}
     *
     * @param x
     * @param y
     * @param z
     * @return a new vector base
     */
    public static <T> VectorBasis<T> createBase(SVector<T> x, SVector<T> y, SVector<T> z) {
        var mc = (FieldCalculator<T>) x.getCalculator();
        T[][] mat = toMatrix(x, y, z);
        T d = MatrixSup.det3(mat, mc);
        if (mc.isZero(d)) {
            throw new IllegalArgumentException("They are on the identity plane");
        }
        return VectorBasis.createBase(Vector.of(x.toList(), mc),
                Vector.of(y.toList(), mc), Vector.of(z.toList(), mc));
    }


//    /**
//     * Describe a vector base in space
//     *
//     * @param <T>
//     * @author liyicheng
//     */
//    public static final class SVectorBasis<T> extends VectorBasis<T> {
//        private final SVector<T> x, y, z;
//
//        public SVectorBasis(SVector<T> x, SVector<T> y, SVector<T> z, T[][] mat, T D,
//                            MathCalculator<T> mc) {
//            super(mc);
//            this.x = x;
//            this.y = y;
//            this.z = z;
//            this.mat = mat;
//            this.D = D;
//        }
//
//        private final T D;
//        private T[][] mat;
//
//        @Override
//        public int getVectorLength() {
//            return 3;
//        }
//
//        @NotNull
//        @Override
//        public List<Vector<T>> getVectors() {
//            return Arrays.asList(x, y, z);
//        }
//
//        @NotNull
//        @Override
//        public Vector<T> reduce(@NotNull Vector<T> v) {
//            if (v.getSize() != 3) {
//                throw new IllegalArgumentException("v.size != 3");
//            }
//            return reduce(SVector.fromVector(v));
//        }
//
//        public SVector<T> reduce(SVector<T> s) {
//            var mc = mc;
//            @SuppressWarnings("unchecked")
//            T[] v = (T[]) new Object[]{s.x, s.y, s.z};
//            T[][] mt2 = mat.clone();
//            T[] t = mt2[0];
//            mt2[0] = v;
//            T D1 = MatrixSup.det3(mt2, mc);
//            mt2[0] = t;
//            t = mt2[1];
//            mt2[1] = v;
//            T D2 = MatrixSup.det3(mt2, mc);
//            mt2[1] = t;
//            mt2[2] = v;
//            T D3 = MatrixSup.det3(mt2, mc);
//            return new SVector<>(mc.divide(D1, D), mc.divide(D2, D), mc.divide(D3, D), mc);
//        }
//
//
//        @Override
//        public <N> @NotNull SVectorBasis<N> mapTo(@NotNull MathCalculator<N> newCalculator, @NotNull Function<T, N> mapper) {
//            N[][] ret = ArraySup.mapTo(mat, (T[] arr) ->
//                    ArraySup.mapTo(arr, mapper)
//            );
//            N d = MatrixSup.det3(ret, newCalculator);
//            return new SVectorBasis<>(x.mapTo(newCalculator, mapper),
//                    y.mapTo(newCalculator, mapper),
//                    z.mapTo(newCalculator, mapper), ret, d,
//                    newCalculator);
//        }
//
//        @Override
//        public boolean equals(Object obj) {
//            if (obj instanceof SVector.SVectorBasis) {
//                SVectorBasis<?> svb = (SVectorBasis<?>) obj;
//                return x.equals(svb.x) && y.equals(svb.y) && z.equals(svb.z);
//            }
//            return false;
//        }
//
//        @Override
//        public int hashCode() {
//            int hash = x.hashCode();
//            hash = hash * 37 + y.hashCode();
//            return hash * 37 + z.hashCode();
//        }
//
//        @Override
//        public boolean valueEquals(@NotNull MathObject<T> obj) {
//            if (obj instanceof SVector.SVectorBasis) {
//                SVectorBasis<T> svb = (SVectorBasis<T>) obj;
//                return x.valueEquals(svb.x) && y.valueEquals(svb.y) && z.valueEquals(svb.z);
//            }
//            return super.valueEquals(obj);
//        }
//
//        @Override
//        public <N> boolean valueEquals(@NotNull MathObject<N> obj, @NotNull Function<N, T> mapper) {
//            if (obj instanceof SVector.SVectorBasis) {
//                SVectorBasis<N> svb = (SVectorBasis<N>) obj;
//                return x.valueEquals(svb.x, mapper) && y.valueEquals(svb.y, mapper) && z.valueEquals(svb.z, mapper);
//            }
//            return super.valueEquals(obj, mapper);
//        }
//
//        /* (non-Javadoc)
//         * @see cn.ancono.math.FlexibleMathObject#toString(cn.ancono.math.number_models.NumberFormatter)
//         */
//        @Override
//        public @NotNull String toString(@NotNull FlexibleNumberFormatter<T> nf) {
//            return "SVectorBase";
//        }
//
//    }

    public static class SVectorGenerator<T> extends AbstractMathObject<T> {

        /**
         * @param mc
         */
        public SVectorGenerator(MathCalculator<T> mc) {
            super(mc);
        }

        /**
         * Returns a point
         *
         * @param x
         * @param y
         * @param z
         * @return
         */
        public SVector<T> of(T x, T y, T z) {
            return SVector.valueOf(x, y, z, getCalculator());
        }

        /* (non-Javadoc)
         * @see cn.ancono.cn.ancono.utilities.math.FlexibleMathObject#mapTo(java.util.function.Function, cn.ancono.cn.ancono.utilities.math.MathCalculator)
         */
        @NotNull
        @Override
        public <N> SVectorGenerator<N> mapTo(@NotNull MathCalculator<N> newCalculator, @NotNull Function<T, N> mapper) {
            return new SVectorGenerator<>(newCalculator);
        }

        /* (non-Javadoc)
         * @see cn.ancono.cn.ancono.utilities.math.FlexibleMathObject#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof SVectorGenerator) {
                var mc = getCalculator();
                return mc.equals(((SVectorGenerator<?>) obj).getCalculator());
            }
            return false;
        }

        /* (non-Javadoc)
         * @see cn.ancono.cn.ancono.utilities.math.FlexibleMathObject#hashCode()
         */
        @Override
        public int hashCode() {
            var mc = getCalculator();
            return mc.hashCode();
        }

        /* (non-Javadoc)
         * @see cn.ancono.cn.ancono.utilities.math.FlexibleMathObject#valueEquals(cn.ancono.cn.ancono.utilities.math.FlexibleMathObject)
         */
        @Override
        public boolean valueEquals(@NotNull MathObject<T> obj) {
            return equals(obj);
        }

        /* (non-Javadoc)
         * @see cn.ancono.cn.ancono.utilities.math.FlexibleMathObject#valueEquals(cn.ancono.cn.ancono.utilities.math.FlexibleMathObject, java.util.function.Function)
         */
        @Override
        public <N> boolean valueEquals(@NotNull MathObject<N> obj, @NotNull Function<N, T> mapper) {
            return equals(obj);
        }

        /* (non-Javadoc)
         * @see cn.ancono.math.FlexibleMathObject#toString(cn.ancono.math.number_models.NumberFormatter)
         */
        @Override
        public @NotNull String toString(@NotNull FlexibleNumberFormatter<T> nf) {
            return "SVectorGenerator";
        }
    }

//    /**
//     * Adds the several given vectors.<br>
//     * THis method will use the {@link MathCalculator} from the first vector.
//     *
//     * @param vs
//     * @return
//     */
//    @SafeVarargs
//    public static <T> SVector<T> add(SVector<T>... vs) {
//        SVector<T> v1 = vs[0];
//        MathCalculator<T> mc = v1.getMathCalculator();
//        T x, y, z;
//        Object[] arr = new Object[vs.length];
//        for (int i = 0; i < vs.length; i++) {
//            arr[i] = vs[i].getX();
//        }
//        x = mc.sum(arr);
//        for (int i = 0; i < vs.length; i++) {
//            arr[i] = vs[i].getY();
//        }
//        y = mc.sum(arr);
//        for (int i = 0; i < vs.length; i++) {
//            arr[i] = vs[i].getZ();
//        }
//        z = mc.sum(arr);
//        return new SVector<T>(x, y, z, mc);
//    }

//	public static void main(String[] args) {
//		MathCalculator<Double> mc = MathCalculatorAdapter.getCalculatorDouble();
//		SVector<Double> v1 = vector(1d,0d,0d,mc),v2 = vector(0d,1d,0d,mc),v3 = vector(0d,0d,1d,mc),
//				v4 = vector(1d,1d,1d,mc);
//		Printer.print(v4.reduce(v1, v2, v3));
//		SVectorBase<Double> b = SVectorBase.createBase(v1, v2, v3);
//		Printer.print(b.reduce(v4));
//	}


}

package cn.ancono.math.geometry.analytic.space;

import cn.ancono.math.AbstractMathObject;
import cn.ancono.math.MathCalculator;
import cn.ancono.math.MathObject;
import cn.ancono.math.algebra.linear.*;
import cn.ancono.math.algebra.linear.LinearEquationSolution.Situation;
import cn.ancono.math.function.MathFunction;
import cn.ancono.math.numberModels.Calculators;
import cn.ancono.math.numberModels.api.FlexibleNumberFormatter;
import cn.ancono.utilities.ArraySup;
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
public final class SVector<T> extends Vector<T> {

    final T x, y, z;

    private T length;

    private T lenSq;

    SVector(T[] vec, MathCalculator<T> mc) {
        super(3, false, mc);
        this.x = vec[0];
        this.y = vec[1];
        this.z = vec[2];
    }

    SVector(T x, T y, T z, MathCalculator<T> mc) {
        super(3, false, mc);
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public T[][] getValues() {
        @SuppressWarnings("unchecked")
        T[][] res = (T[][]) Array.newInstance(x.getClass(), 1, 3);
        res[0][0] = x;
        res[1][0] = y;
        res[2][0] = z;
        return res;
    }


    @Override
    public SVector<T> negative() {
        return new SVector<>(getMc().negate(x), getMc().negate(y), getMc().negate(z), getMc());
    }


    @Override
    public Vector<T> transpose() {
        @SuppressWarnings("unchecked")
        T[] arr = (T[]) Array.newInstance(x.getClass(), 3);
        arr[0] = x;
        arr[1] = y;
        arr[2] = z;
        return Vector.vOf(getMc(), true, arr);
    }


    @Override
    public SVector<T> multiplyNumber(long n) {
        return new SVector<>(getMc().multiplyLong(x, n), getMc().multiplyLong(y, n), getMc().multiplyLong(z, n), getMc());
    }


    @Override
    public SVector<T> multiplyNumber(T n) {
        return new SVector<>(getMc().multiply(x, n), getMc().multiply(y, n), getMc().multiply(z, n), getMc());
    }


    @Override
    public Matrix<T> cofactor(int r, int c) {
        throw new ArithmeticException("Too small for cofactor");
    }

    @Override
    public int getRowCount() {
        return 3;
    }

    @Override
    public int getColumnCount() {
        return 1;
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
    @Override
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
    @Override
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
    public List<T> toList() {
        return Arrays.asList(x, y, z);
    }

    /**
     * Returns {@code this + s}.
     *
     * @param s another SVector
     * @return this + s
     */
    public SVector<T> add(SVector<T> s) {
        return new SVector<>(getMc().add(x, s.x), getMc().add(y, s.y), getMc().add(z, s.z), getMc());
    }

    /**
     * Returns {@code this - s}.
     *
     * @param s another SVector
     * @return this - s
     */
    public SVector<T> subtract(SVector<T> s) {
        return new SVector<>(getMc().subtract(x, s.x), getMc().subtract(y, s.y), getMc().subtract(z, s.z), getMc());
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
        return getMc().add(getMc().add(getMc().multiply(x, s.x), getMc().multiply(y, s.y)), getMc().multiply(z, s.z));
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
        var mc = getMc();
        T nx = mc.subtract(mc.multiply(y, s.z), mc.multiply(s.y, z));
        T ny = mc.subtract(mc.multiply(z, s.x), mc.multiply(s.z, x));
        T nz = mc.subtract(mc.multiply(x, s.y), mc.multiply(s.x, y));
        return new SVector<T>(nx, ny, nz, getMc());
    }

    /**
     * Returns the length of {@code this}.
     * <pre>|this|</pre>
     *
     * @return |this|
     */
    @Override
    public T calLength() {
        if (length == null) {
            length = getMc().squareRoot(calLengthSq());
        }
        return length;
    }

    @Override
    public T calLengthSq() {
        if (lenSq == null) {
            lenSq = innerProduct(this);
        }
        return lenSq;
    }

    /**
     * This method will ignore j.
     */
    @Override
    public T get(int i, int j) {
        return get(i);
    }

    @Override
    public int getSize() {
        return 3;
    }

    @Override
    public T get(int i) {
        switch (i) {
            case 0:
                return x;
            case 1:
                return y;
            case 2:
                return z;
            default:
                throw new IndexOutOfBoundsException("for index:" + i);
        }
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
        pro = getMc().divide(pro, getMc().multiply(calLength(), s.calLength()));
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
        T pro = innerProduct(s);
        return getMc().divide(pro, getMc().multiply(calLength(), s.calLength()));
    }

    /**
     * Determines whether the two vectors are parallel.
     *
     * @param s
     * @return
     */
    public boolean isParallel(SVector<T> s) {
        if (!getMc().isZero(x)) {
            if (getMc().isZero(s.x)) {
                return false;
            }
            return getMc().isEqual(getMc().multiply(x, s.y), getMc().multiply(y, s.x)) &&
                    getMc().isEqual(getMc().multiply(x, s.z), getMc().multiply(z, s.x));
        }
        //x == 0
        if (!getMc().isZero(s.x)) {
            return false;
        }
        return getMc().isEqual(getMc().multiply(y, s.z), getMc().multiply(z, s.y));
    }

    /**
     * Determines whether the given vector is in the identity direction of this vector, which means
     * the vector the result of {@code this.angleCos(s)} will be 1. If {@code s} is a zero vector,
     * an exception will be thrown.
     *
     */
    public boolean isOfSameDirection(SVector<T> s) {
        if (s.isZeroVector()) {
            throw new ArithmeticException("s==0");
        }
        if (!isParallel(s)) {
            return false;
        }
        T t1 = x, t2 = s.x;
        if (getMc().isZero(t1)) {
            if (getMc().isZero(y)) {
                t1 = z;
                t2 = s.z;
            } else {
                t1 = y;
                t2 = s.y;
            }
        }
        return Calculators.isSameSign(t1, t2, getMc());
    }

    /**
     * Determines whether the two vectors are perpendicular.
     *
     * @param s
     * @return
     */
    public boolean isPerpendicular(SVector<T> s) {
        return getMc().isZero(innerProduct(s));
    }

    /**
     * Returns a unit vector which is parallel to this vector.
     *
     * @return an unit vector
     */
    @Override
    public SVector<T> unitVector() {
        T length = calLength();
        SVector<T> s = new SVector<>(getMc().divide(x, length),
                getMc().divide(y, length),
                getMc().divide(z, length), getMc());
        s.length = getMc().getOne();
        s.lenSq = getMc().getOne();
        return s;
    }

    /**
     * Returns a SVector that has the identity direct of this but length is given.
     *
     * @param len the length
     * @return a new SVector
     */
    public SVector<T> parallel(T len) {
        T length = calLength();
        SVector<T> s = new SVector<>(getMc().multiply(len, getMc().divide(x, length)),
                getMc().multiply(len, getMc().divide(y, length)),
                getMc().multiply(len, getMc().divide(z, length)), getMc());
        s.length = len;
        return s;
    }

    /**
     * Determines whether this vector is a zero vector.
     *
     * @return
     */
    public boolean isZeroVector() {
        var mc = getMathCalculator();
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
        T k = getMc().negate(getMc().divide(innerProduct(v), calLengthSq()));
        T nx = getMc().add(v.x, getMc().multiply(k, x));
        T ny = getMc().add(v.y, getMc().multiply(k, y));
        T nz = getMc().add(v.z, getMc().multiply(k, z));
        return new SVector<T>(nx, ny, nz, getMc());
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
    public <N> SVector<N> mapTo(@NotNull MathCalculator<N> newCalculator, @NotNull Function<T, N> mapper) {
        SVector<N> sn = new SVector<>(mapper.apply(x), mapper.apply(y), mapper.apply(z), newCalculator);
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

    @Override
    public <N> boolean valueEquals(@NotNull MathObject<N> obj, @NotNull Function<N, T> mapper) {
        if (obj instanceof SVector) {
            SVector<N> s = (SVector<N>) obj;
            return getMc().isEqual(x, mapper.apply(s.x)) &&
                    getMc().isEqual(y, mapper.apply(s.y)) &&
                    getMc().isEqual(z, mapper.apply(s.z));
        }
        return false;
    }

    @Override
    public boolean valueEquals(@NotNull MathObject<T> obj) {
        if (obj instanceof SVector) {
            SVector<T> s = (SVector<T>) obj;
            return getMc().isEqual(x, s.x) &&
                    getMc().isEqual(y, s.y) &&
                    getMc().isEqual(z, s.z);
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
        if (x.isParallel(y) || y.isParallel(z) || x.isParallel(z)) {
            throw new IllegalArgumentException("Parallel");
        }
        @SuppressWarnings("unchecked")
        T[][] mat = (T[][]) new Object[3][4];
        mat[0][0] = x.x;
        mat[0][1] = y.x;
        mat[0][2] = z.x;
        mat[0][3] = this.x;

        mat[1][0] = x.y;
        mat[1][1] = y.y;
        mat[1][2] = z.y;
        mat[1][3] = this.y;

        mat[2][0] = x.z;
        mat[2][1] = y.z;
        mat[2][2] = z.z;
        mat[2][3] = this.z;

        LinearEquationSolution<T> sol = MatrixSup.solveLinearEquation(mat, getMc());
        if (sol.getSolutionSituation() != Situation.UNIQUE) {
            throw new ArithmeticException("Not single?");
        }
        return fromVector(sol.getSpecialSolution());
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

    @Override
    public SVector<T> applyFunction(MathFunction<T, T> f) {
        return new SVector<>(f.apply(x), f.apply(y), f.apply(z), getMc());
    }

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
        return vector(A, B, A.getMathCalculator());
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
        MathCalculator<T> mc = vectors[0].getMc();
        @SuppressWarnings("unchecked")
        T[] arr = (T[]) Array.newInstance(vectors[0].x.getClass(), vectors.length);
        for (int i = 0; i < vectors.length; i++) {
            arr[i] = vectors[i].x;
        }
        T xm = mc.addX(arr);
        for (int i = 0; i < vectors.length; i++) {
            arr[i] = vectors[i].y;
        }
        T ym = mc.addX(arr);
        for (int i = 0; i < vectors.length; i++) {
            arr[i] = vectors[i].z;
        }
        T zm = mc.addX(arr);
        return new SVector<T>(xm, ym, zm, mc);
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
        return new SVector<T>(v.get(0), v.get(1), v.get(2), v.getMathCalculator());
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
        return MatrixSup.det3(toMatrix(a, b, c), a.getMc());
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
        MathCalculator<T> mc = v.getMathCalculator();
        SVector<T> perp = n.outerProduct(v);
        SVector<T> res = perp.multiplyNumber(v.calLength());
        res = res.add(v.multiplyNumber(mc.divide(perp.calLength(), tan)));
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
        MathCalculator<T> mc = v.getMathCalculator();
        SVector<T> perp = n.outerProduct(v);
        SVector<T> res = perp.multiplyNumber(mc.multiply(tan, v.calLength()));
        SVector<T> t = v.multiplyNumber(perp.calLength());
        list.add(res.add(t));
        list.add(res.negative().add(t));
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
        var mc = v1.getMathCalculator();
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
    public static <T> SVectorBasis<T> createBase(SVector<T> x, SVector<T> y, SVector<T> z) {
        MathCalculator<T> mc = x.getMc();
        T[][] mat = toMatrix(x, y, z);
        T d = MatrixSup.det3(mat, mc);
        if (mc.isZero(d)) {
            throw new IllegalArgumentException("They are on the identity plane");
        }
        return new SVectorBasis<>(x, y, z, mat, d, mc);
    }

    /**
     * Describe a vector base in space
     *
     * @param <T>
     * @author liyicheng
     */
    public static final class SVectorBasis<T> extends VectorBasis<T> {
        private final SVector<T> x, y, z;

        public SVectorBasis(SVector<T> x, SVector<T> y, SVector<T> z, T[][] mat, T D,
                            MathCalculator<T> mc) {
            super(mc);
            this.x = x;
            this.y = y;
            this.z = z;
            this.mat = mat;
            this.D = D;
        }

        private final T D;
        private T[][] mat;

        @Override
        public int getVectorLength() {
            return 3;
        }

        @NotNull
        @Override
        public List<Vector<T>> getVectors() {
            return Arrays.asList(x, y, z);
        }

        @NotNull
        @Override
        public Vector<T> reduce(@NotNull Vector<T> v) {
            if (v.getSize() != 3) {
                throw new IllegalArgumentException("v.size != 3");
            }
            return reduce(SVector.fromVector(v));
        }

        public SVector<T> reduce(SVector<T> s) {
            var mc = getMc();
            @SuppressWarnings("unchecked")
            T[] v = (T[]) new Object[]{s.x, s.y, s.z};
            T[][] mt2 = mat.clone();
            T[] t = mt2[0];
            mt2[0] = v;
            T D1 = MatrixSup.det3(mt2, mc);
            mt2[0] = t;
            t = mt2[1];
            mt2[1] = v;
            T D2 = MatrixSup.det3(mt2, mc);
            mt2[1] = t;
            mt2[2] = v;
            T D3 = MatrixSup.det3(mt2, mc);
            return new SVector<>(mc.divide(D1, D), mc.divide(D2, D), mc.divide(D3, D), mc);
        }


        @Override
        public <N> SVectorBasis<N> mapTo(@NotNull MathCalculator<N> newCalculator, @NotNull Function<T, N> mapper) {
            N[][] ret = ArraySup.mapTo(mat, (T[] arr) ->
                    ArraySup.mapTo(arr, mapper)
            );
            N d = MatrixSup.det3(ret, newCalculator);
            return new SVectorBasis<>(x.mapTo(newCalculator, mapper),
                    y.mapTo(newCalculator, mapper),
                    z.mapTo(newCalculator, mapper), ret, d,
                    newCalculator);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof SVector.SVectorBasis) {
                SVectorBasis<?> svb = (SVectorBasis<?>) obj;
                return x.equals(svb.x) && y.equals(svb.y) && z.equals(svb.z);
            }
            return false;
        }

        @Override
        public int hashCode() {
            int hash = x.hashCode();
            hash = hash * 37 + y.hashCode();
            return hash * 37 + z.hashCode();
        }

        @Override
        public boolean valueEquals(@NotNull MathObject<T> obj) {
            if (obj instanceof SVector.SVectorBasis) {
                SVectorBasis<T> svb = (SVectorBasis<T>) obj;
                return x.valueEquals(svb.x) && y.valueEquals(svb.y) && z.valueEquals(svb.z);
            }
            return super.valueEquals(obj);
        }

        @Override
        public <N> boolean valueEquals(@NotNull MathObject<N> obj, @NotNull Function<N, T> mapper) {
            if (obj instanceof SVector.SVectorBasis) {
                SVectorBasis<N> svb = (SVectorBasis<N>) obj;
                return x.valueEquals(svb.x, mapper) && y.valueEquals(svb.y, mapper) && z.valueEquals(svb.z, mapper);
            }
            return super.valueEquals(obj, mapper);
        }

        /* (non-Javadoc)
         * @see cn.ancono.math.FlexibleMathObject#toString(cn.ancono.math.number_models.NumberFormatter)
         */
        @Override
        public String toString(@NotNull FlexibleNumberFormatter<T, MathCalculator<T>> nf) {
            return "SVectorBase";
        }

    }

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
            return SVector.valueOf(x, y, z, getMc());
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
                return getMc().equals(((SVectorGenerator<?>) obj).getMc());
            }
            return false;
        }

        /* (non-Javadoc)
         * @see cn.ancono.cn.ancono.utilities.math.FlexibleMathObject#hashCode()
         */
        @Override
        public int hashCode() {
            return getMc().hashCode();
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
        public String toString(@NotNull FlexibleNumberFormatter<T, MathCalculator<T>> nf) {
            return "SVectorGenerator";
        }
    }

    /**
     * Adds the several given vectors.<br>
     * THis method will use the {@link MathCalculator} from the first vector.
     *
     * @param vs
     * @return
     */
    @SafeVarargs
    public static <T> SVector<T> add(SVector<T>... vs) {
        SVector<T> v1 = vs[0];
        MathCalculator<T> mc = v1.getMathCalculator();
        T x, y, z;
        Object[] arr = new Object[vs.length];
        for (int i = 0; i < vs.length; i++) {
            arr[i] = vs[i].getX();
        }
        x = mc.addX(arr);
        for (int i = 0; i < vs.length; i++) {
            arr[i] = vs[i].getY();
        }
        y = mc.addX(arr);
        for (int i = 0; i < vs.length; i++) {
            arr[i] = vs[i].getZ();
        }
        z = mc.addX(arr);
        return new SVector<T>(x, y, z, mc);
    }

//	public static void main(String[] args) {
//		MathCalculator<Double> mc = MathCalculatorAdapter.getCalculatorDouble();
//		SVector<Double> v1 = vector(1d,0d,0d,mc),v2 = vector(0d,1d,0d,mc),v3 = vector(0d,0d,1d,mc),
//				v4 = vector(1d,1d,1d,mc);
//		Printer.print(v4.reduce(v1, v2, v3));
//		SVectorBase<Double> b = SVectorBase.createBase(v1, v2, v3);
//		Printer.print(b.reduce(v4));
//	}


}

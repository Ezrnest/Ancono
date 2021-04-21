package cn.ancono.math.algebra.linear;

import cn.ancono.math.MathCalculator;
import cn.ancono.math.algebra.abs.calculator.FieldCalculator;
import cn.ancono.math.algebra.abs.calculator.LinearSpaceCalculator;
import cn.ancono.math.function.MathFunction;
import cn.ancono.math.numberModels.Calculators;
import cn.ancono.math.numberModels.api.FlexibleNumberFormatter;
import cn.ancono.utilities.ArraySup;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.function.IntFunction;

/**
 * A vector is a matrix but one dimension (row or column) is one in length.
 *
 * @param <T>
 * @author liyicheng
 */
public abstract class Vector<T> extends Matrix<T> {
    /**
     * Decide whether this vector is a row-vector which means column count is
     * the length of vec. Otherwise,the column count will be 1 and row count will
     * be vec.length.
     */
    protected final boolean isRow;

    protected Vector(int length, boolean isRow, MathCalculator<T> mc) {
        super(isRow ? 1 : length, isRow ? length : 1, mc);
        this.isRow = isRow;
    }

    /**
     * Returns the number of dimension of this vector.
     */
    public int getSize() {
        return isRow ? row : column;
    }

    /**
     * Determines whether the two vectors are of the identity size.
     *
     * @param v another vector.
     * @return {@code true} if they are the identity in size.
     */
    public boolean isSameSize(Vector<?> v) {
        return getSize() == v.getSize();
    }

    public abstract T get(int index);

    /**
     * Returns an array containing all of the elements in this vector in
     * proper sequence (from first to last element),.
     */
    public abstract Object[] toArray();

    /**
     * Returns an array containing all of the elements in this vector in
     * proper sequence (from first to last element), the runtime type of
     * the returned array is that of the specified array.  If the list fits
     * in the specified array, it is returned therein.  Otherwise, a new
     * array is allocated with the runtime type of the specified array and
     * the size of this list.
     */
    public abstract T[] toArray(T[] arr);

    /**
     * Gets an immutable list containing all the elements in this vector in order.
     */
    public abstract List<T> toList();

    /*
     * @see cn.ancono.math.algebra.abstractAlgebra.linearAlgebra.Matrix#getValues()
     */
    @Override
    public Object[][] getValues() {
        if (isRow) {
            Object[][] mat = new Object[1][];
            mat[0] = toArray();
            return mat;
        } else {
            int size = getSize();
            Object[][] mat = new Object[size][1];
            for (int i = 0; i < size; i++) {
                mat[i][0] = get(i);
            }
            return mat;
        }
    }

    /**
     * Returns the square of the Euclidean norm of this vector, which
     * is equal to the sum of square of each element.
     * <p></p>
     * This method is generally the same as <code>this.inner(this)</code>.
     *
     * @return <code>|this|^2</code>
     */
    public T normSq() {
        var mc = getMc();
        T re = mc.getZero();
        int size = getSize();
        for (int i = 0; i < size; i++) {
            T t = get(i);
            re = mc.add(mc.multiply(t, t), re);
        }
        return re;
    }

    /**
     * Return the Euclidean norm of this vector, which
     * is equal to the square root of the sum of square of each elements.
     * The result is non-negative.
     *
     * @return <code>|this|</code>
     */
    public T norm() {
        return getMc().squareRoot(normSq());
    }

    /**
     * Returns a unit vector of this vector's direction, throws an exception
     * if this vector is an zero vector.
     * <pre>
     * this/|this|
     * </pre>
     *
     * @return a vector
     */
    public Vector<T> unitVector() {
        var mc = getMc();
        var norm = norm();
        if (mc.isZero(norm)) {
            throw new ArithmeticException("This vector is zero!");
        }
        return this.multiplyNumber(mc.reciprocal(norm));
    }

    protected final void checkSameSize(Vector<?> v) {
        if (!isSameSize(v)) {
            throw new ArithmeticException("Different dimension:" + getSize() + ":" + v.getSize());
        }
    }

    /**
     * This method will return the inner product of {@code this} and {@code v}.
     * The size of the two vectors must be the identity while what kind of vector
     * (row or column) is ignored.
     *
     * @param v a vector
     * @return the inner(scalar) product of this two vectors.
     * @throws ArithmeticException if dimension doesn't match
     */
    public T inner(Vector<T> v) {
        checkSameSize(v);
        var mc = getMc();
        final int size = getSize();
        T re = mc.getZero();
        for (int i = 0; i < size; i++) {
            re = mc.add(mc.multiply(get(i), v.get(i)), re);
        }
        return re;
    }


    /**
     * Determines whether the two vectors are perpendicular.
     *
     * @param v another vector
     * @return {@code true} of the vectors are perpendicular
     */
    public boolean isPerpendicular(Vector<T> v) {
        return getMc().isZero(inner(v));
    }

    /**
     * Returns the angle of {@code this} and {@code v}.
     * <pre> arccos(this · v / (|this| |v|))</pre>
     *
     * @return <pre> arccos(this · v / (|this| |v|))</pre>
     */
    public T angle(Vector<T> v) {
        return getMc().arccos(angleCos(v));
    }

    /**
     * Returns the cos value of the angle of {@code this} and {@code v}.
     * <pre>this · v / (|this| |v|)</pre>
     *
     * @return <pre>this · v / (|this| |v|)</pre>
     */
    public T angleCos(Vector<T> v) {
        T pro = inner(v);
        return getMc().divide(pro, getMc().multiply(norm(), v.norm()));
    }


    /**
     * Determines whether this vector is a zero vector.
     */
    public boolean isZero() {
        var mc = getMc();
        for (int i = 0, size = getSize(); i < size; i++) {
            if (!mc.isZero(get(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Determines whether this vector is an unit vector.
     */
    public boolean isUnitVector() {
        var mc = getMc();
        return mc.isEqual(normSq(), mc.getOne());
    }


    /**
     * Determines whether the two vectors are parallel.
     * If any of the two vector is a zero vector , than
     * the method will return true.
     *
     * @param v a vector
     * @return {@code true} if {@code this // v}
     */
    public boolean isParallel(Vector<T> v) {
        // dimension check
        checkSameSize(v);
        if (isZero() || v.isZero()) {
            return true;
        }
        var mc = getMc();
        final int size = getSize();
        int not0 = 0;
        while (mc.isZero(get(not0))) {
            if (!mc.isZero(v.get(not0))) {
                return false;
            }
            not0++;
            if (not0 + 1 == size) {
                return true;
            }
        }
        T t1 = get(not0);
        T t2 = v.get(not0);
        for (int i = not0 + 1; i < size; i++) {
            if (!mc.isEqual(mc.multiply(t1, v.get(i)), mc.multiply(t2, get(i)))) {
                return false;
            }
        }
        return true;
    }

    /* (non-Javadoc)
     * @see cn.ancono.cn.ancono.utilities.math.Matrix#applyFunction(cn.ancono.cn.ancono.utilities.math.MathFunction)
     */
    @Override
    public abstract Vector<T> applyFunction(MathFunction<T, T> f);

    /**
     * Gets whether it is a row vector.
     */
    public boolean isRow() {
        return isRow;
    }

    /* (non-Javadoc)
     * @see cn.ancono.math.algebra.abstractAlgebra.linearAlgebra.Matrix#multiplyNumber(long)
     */
    @Override
    public abstract Vector<T> multiplyNumber(long n);

    /* (non-Javadoc)
     * @see cn.ancono.math.algebra.abstractAlgebra.linearAlgebra.Matrix#multiplyNumber(java.lang.Object)
     */
    @Override
    public abstract Vector<T> multiplyNumber(T n);

    /* (non-Javadoc)
     * @see cn.ancono.math.algebra.abstractAlgebra.linearAlgebra.Matrix#negative()
     */
    @Override
    public abstract Vector<T> negate();

    /* (non-Javadoc)
     * @see cn.ancono.math.algebra.abstractAlgebra.linearAlgebra.Matrix#transportMatrix()
     */
    @Override
    public abstract Vector<T> transpose();

    public Vector<T> toRowVector() {
        if (isRow) {
            return this;
        } else {
            return transpose();
        }
    }

    public Vector<T> toColumnVector() {
        if (isRow) {
            return transpose();
        } else {
            return this;
        }
    }

    /**
     * Returns a vector of the given size. The elements in the corresponding positions are equal to the elements
     * in this and remaining elements are set to zero.
     */
    public Vector<T> resize(int size) {
        if (size < 1) {
            throw new IllegalArgumentException("size < 1");
        }
        if (size == this.getSize()) {
            return this;
        }
        @SuppressWarnings("unchecked")
        T[] vec = (T[]) new Object[size];
        int pos = Math.min(size, this.getSize());
        for (int i = 0; i < pos; i++) {
            vec[i] = get(i);
        }
        var mc = getMc();
        for (int i = pos; i < size; i++) {
            vec[i] = mc.getZero();
        }
        return new DVector<>(vec, isRow, mc);
    }

    @NotNull
    @Override
    public String toString(@NotNull FlexibleNumberFormatter<T, MathCalculator<T>> nf) {
        StringBuilder sb = new StringBuilder();
        sb.append('(');
        for (int i = 0, size = getSize(); i < size; i++) {
            sb.append(nf.format(get(i), getMc())).append(',');
        }
        sb.setCharAt(sb.length() - 1, ')');
        return sb.toString();
    }

    /*
     * @see cn.ancono.math.algebra.abstractAlgebra.linearAlgebra.Matrix#mapTo(java.util.function.Function, cn.ancono.math.MathCalculator)
     */
    @NotNull
    @Override
    public abstract <N> Vector<N> mapTo(@NotNull MathCalculator<N> newCalculator, @NotNull Function<T, N> mapper);

    /**
     * Create a new vector with the given elements. A boolean representing whether the
     * vector should be a row-vector or column-vector is necessary. The vector returned by
     * this method generally has a better performance in contrast to the matrix return by simply call
     * {@link Matrix#of(Object[][], MathCalculator)} using a two-dimension array as parameter.
     * <p>For example , assume {@code fs} is
     * an array contains following values:[1,3,4,5],then {@code of(true,fs} will return a
     * matrix whose row count is one and column count is 4, while {@code createVector(false,fs} will
     * return a matrix with 4 rows and 1 column.
     *
     * @param isRow decides whether the vector return is a row-vector
     * @param fs    the numbers,null values will be considered as ZERO
     * @return a newly created vector
     * @see DVector#vOf(boolean, long[])
     */
    @SafeVarargs
    public static <T> Vector<T> vOf(MathCalculator<T> mc, boolean isRow,
                                    T... fs) {
        @SuppressWarnings("unchecked")
        T[] vec = (T[]) new Object[fs.length];
        for (int i = 0; i < vec.length; i++) {
            vec[i] = fs[i] == null ? mc.getZero() : fs[i];
        }
        return new DVector<>(vec, isRow, mc);
    }


    /**
     * Returns a column vector from the given elements.
     */
    public static <T> Vector<T> vOf(MathCalculator<T> mc, List<T> elements) {
        @SuppressWarnings("unchecked")
        T[] vec = (T[]) elements.toArray();
        for (int i = 0; i < vec.length; i++) {
            vec[i] = vec[i] == null ? mc.getZero() : vec[i];
        }
        return new DVector<>(vec, false, mc);
    }

    /**
     * Create a new vector with the given long array. A boolean representing whether the
     * vector should be a row-vector or column-vector is necessary. The vector returned by
     * this method generally has a better performance in contrast to the matrix return by simply call
     * {@link Matrix#of(Object[][], MathCalculator)} using a two-dimension array as parameter.
     * <p>For example , assume {@code ns} is
     * an array contains following values:[1,3,4,5],then {@code of(true,ns} will return a
     * matrix whose row count is one and column count is 4, while {@code of(false,ns} will
     * return a matrix with 4 rows and 1 column.
     *
     * @param isRow decides whether the vector return is a row-vector
     * @param ns    the numbers
     * @return a newly created vector
     * @see DVector#of(Object[][], MathCalculator)
     */
    public static Vector<Long> vOf(boolean isRow, long[] ns) {
        Long[] vec = new Long[ns.length];
        for (int i = 0; i < vec.length; i++) {
            vec[i] = ns[i];
        }
        return new DVector<>(vec, isRow, Calculators.longCal());
    }

    /**
     * Create a new column vector according to the array of fraction given.Null values will be considered
     * as {@link MathCalculator#getZero()}
     *
     * @param fs the numbers
     * @return a newly created column vector
     * @see #vOf(MathCalculator, boolean, Object[])
     */
    @SafeVarargs
    public static <T> Vector<T> vOf(MathCalculator<T> mc, T... fs) {
        return vOf(mc, false, fs);
    }

    /**
     * Create a new column vector according to the array of fraction given.
     *
     * @param arr the numbers
     * @return a newly created vector
     * @see #vOf(boolean, long[])
     */
    public static Vector<Long> vOf(long[] arr) {
        return vOf(false, arr);
    }


    /**
     * This method provides a more suitable implement for vector adding than {@link Matrix#add(Matrix, Matrix)},
     * this method will add the two vector and return a column vector as the result.
     *
     * @return a column vector as result
     * @throws ArithmeticException if dimension doesn't match
     */
    public static <T> Vector<T> addV(Vector<T> v1, Vector<T> v2) {
        return addV(v1, v2, false);
    }

    /**
     * A method similar to {@link #addV(Vector, Vector)}, but subtract.
     *
     * @return a column vector as result
     * @throws ArithmeticException if dimension doesn't match
     */
    public static <T> Vector<T> subtractV(Vector<T> v1, Vector<T> v2) {
        return subtractV(v1, v2, false);
    }

    /**
     * Adds two vectors, returning either a column vector or a row vector.
     *
     * @return a vector, whether it is row vector or column vector is determined by <code>asRow</code>
     * @throws ArithmeticException if dimension doesn't match
     */
    public static <T> Vector<T> addV(Vector<T> v1, Vector<T> v2, boolean asRow) {
        v1.checkSameSize(v2);
        final int size = v1.getSize();
        @SuppressWarnings("unchecked")
        T[] re = (T[]) new Object[size];
        MathCalculator<T> mc = v1.getMc();
        for (int i = 0; i < re.length; i++) {
            re[i] = mc.add(v1.get(i), v2.get(i));
        }
        return new DVector<>(re, asRow, mc);
    }

    /**
     * The inversion of {@link #addV(Vector, Vector, boolean)}.
     *
     * @return a column, whether it is row vector or column vector is determined by <code>asRow</code>
     * @throws ArithmeticException if dimension doesn't match
     */
    public static <T> Vector<T> subtractV(Vector<T> v1, Vector<T> v2, boolean asRow) {
        v1.checkSameSize(v2);
        final int size = v1.getSize();
        @SuppressWarnings("unchecked")
        T[] re = (T[]) new Object[size];
        MathCalculator<T> mc = v1.getMc();
        for (int i = 0; i < re.length; i++) {
            re[i] = mc.subtract(v1.get(i), v2.get(i));
        }
        return new DVector<>(re, asRow, mc);
    }


    /**
     * Provides a better efficiency for adding several vectors without creating
     * a new vector when adding each time.
     *
     * @return a column vector as result
     * @throws ArithmeticException if dimension doesn't match
     */
    @SafeVarargs
    public static <T> Vector<T> addAll(Vector<T>... vecs) {
        return addAll(vecs.length, vecs);
    }

    /**
     * Provides a better efficiency for adding several vectors without creating
     * a new vector when adding each time.
     *
     * @param n the number of vectors to add
     * @return a column vector as result
     * @throws ArithmeticException if dimension doesn't match
     */
    @SafeVarargs
    public static <T> Vector<T> addAll(int n, Vector<T>... vecs) {
        if (n < 1 || n > vecs.length) {
            throw new IllegalArgumentException();
        }
        final int size = vecs[0].getSize();
        @SuppressWarnings("unchecked")
        T[] re = (T[]) vecs[0].toArray();
        MathCalculator<T> mc = vecs[0].getMc();
        for (int j = 1; j < n; j++) {
            Vector<T> v = vecs[j];
            if (v.getSize() != size) {
                throw new IllegalArgumentException();
            }
            for (int i = 0; i < size; i++) {
                re[i] = mc.add(re[i], v.get(i));
            }
        }
        return new DVector<>(re, false, mc);
    }

    /**
     * Provides a better efficiency for adding several vectors without creating
     * a new vector when adding each time.
     *
     * @return a column vector as result
     * @throws ArithmeticException if dimension doesn't match
     */
    @SafeVarargs
    public static <T> Vector<T> addAll(Vector<T> v, Vector<T>... vecs) {
        final int size = v.getSize();
        @SuppressWarnings("unchecked")
        T[] re = (T[]) v.toArray();
        MathCalculator<T> mc = vecs[0].getMc();
        for (Vector<T> vt : vecs) {
            if (vt.getSize() != size) {
                throw new IllegalArgumentException();
            }
            for (int i = 0; i < size; i++) {
                re[i] = mc.add(re[i], vt.get(i));
            }
        }
        return new DVector<>(re, false, mc);
    }

    /**
     * Calculate the intersection angle of the two vector.Which is usually shown as {@literal <v1,v2>}.
     *
     * @param v1     a vector
     * @param v2     another vector
     * @param arccos a function to calculate arccos value of T
     * @return {@literal <v1,v2>}.
     * @throws ArithmeticException if one of the vectors is zero vector
     */
    public static <T, R> R intersectionAngle(Vector<T> v1, Vector<T> v2, MathFunction<T, R> arccos) {
        return arccos.apply(cosValueOfIntersectionAngle(v1, v2));
    }

    /**
     * Calculate the cos value of the intersection angle of the two vector.
     * Which is usually shown as {@literal cos<v1,v2>}.
     * The value will be in [-1,1].
     *
     * @param v1 a vector
     * @param v2 another vector
     * @return cos{@literal <v1,v2>}.
     * @throws ArithmeticException if one of the vectors is zero vector
     */
    public static <T> T cosValueOfIntersectionAngle(Vector<T> v1, Vector<T> v2) {
        T re = v1.inner(v2);
        MathCalculator<T> mc = v1.getMc();
        T d1 = v1.norm();
        T d2 = v2.norm();
        if (mc.isEqual(mc.getZero(), d1) || mc.isEqual(mc.getZero(), d2)) {
            throw new ArithmeticException("Zero vector");
        }
        return mc.divide(re, mc.multiply(d1, d2));
    }

    private static void checkPositiveLength(int length) {
        if (length < 1) {
            throw new IllegalArgumentException("length<=0");
        }
    }

    /**
     * Return a zero vector of the given length.The length of
     *
     * @return zero vector
     */
    public static <T> Vector<T> zeroVector(int length, boolean isRow, MathCalculator<T> mc) {
        checkPositiveLength(length);
        T zero = mc.getZero();
        @SuppressWarnings("unchecked")
        T[] f = (T[]) new Object[length];
        for (int i = 0; i < length; i++) {
            f[i] = zero;
        }
        return new DVector<>(f, isRow, mc);
    }

    /**
     * Return a zero column vector of the given length.
     */
    public static <T> Vector<T> zeroVector(int length, MathCalculator<T> mc) {
        return zeroVector(length, false, mc);
    }

    /**
     * Returns a unit column vector of the given length.
     */
    public static <T> Vector<T> unitVector(int length, int unitIndex, MathCalculator<T> mc) {
        if (length < 1) {
            throw new IllegalArgumentException("length < 1");
        }
        @SuppressWarnings("unchecked") T[] arr = (T[]) new Object[length];
        T zero = mc.getZero();
        T one = mc.getOne();
        Arrays.fill(arr, zero);
        arr[unitIndex] = one;
        return new DVector<>(arr, false, mc);
    }


    /**
     * Returns a list of all unit vectors of the given length.
     */
    public static <T> List<Vector<T>> unitVectors(int length, MathCalculator<T> mc) {
        if (length < 1) {
            throw new IllegalArgumentException("length < 1");
        }
        List<Vector<T>> list = new ArrayList<>(length);

        T one = mc.getOne();
        T zero = mc.getZero();
        for (int i = 0; i < length; i++) {
            @SuppressWarnings("unchecked")
            T[] arr = (T[]) new Object[length];
            Arrays.fill(arr, zero);
            arr[i] = one;
            list.add(new DVector<>(arr, false, mc));
        }
        return list;
    }


    /**
     * Returns a vector that is filled with the identity value.
     */
    public static <T> Vector<T> sameValueOf(int length, T value, boolean isRow, MathCalculator<T> mc) {
        checkPositiveLength(length);
        @SuppressWarnings("unchecked")
        T[] arr = (T[]) new Object[length];
        Arrays.fill(arr, value);
        return new DVector<>(arr, isRow, mc);
    }

    /**
     * Resize the given vector, expanding the vector on the left side and the right side.
     */
    public static <T> Vector<T> resizeOf(Vector<T> v, int leftExpansion, int rightExpansion) {
        if (leftExpansion + v.getSize() <= 0 || rightExpansion + v.getSize() <= 0) {
            throw new IllegalArgumentException();
        }
        var z = v.getMathCalculator().getZero();
        int size = v.getSize();
        int nSize = leftExpansion + rightExpansion + size;
        @SuppressWarnings("unchecked")
        T[] arr = (T[]) new Object[nSize];
        for (int i = 0; i < leftExpansion; i++) {
            arr[i] = z;
        }
        for (int i = leftExpansion; i < leftExpansion + size; i++) {
            if (i - leftExpansion < 0 || i < 0) {
                continue;
            }
            if (i >= nSize) {
                break;
            }
            arr[i] = v.get(i - leftExpansion);
        }
        for (int i = leftExpansion + size; i < nSize; i++) {
            arr[i] = z;
        }
        return new DVector<>(arr, v.isRow, v.getMathCalculator());
    }

    /**
     * Returns a column vector from the matrix.
     *
     * @param mat    a matrix
     * @param column from 0
     */
    public static <T> Vector<T> column(Matrix<T> mat, int column) {
        return mat.getColumn(column);
    }

    /**
     * Returns a row vector from the matrix.
     *
     * @param mat a matrix
     * @param row from 0
     */
    public static <T> Vector<T> row(Matrix<T> mat, int row) {
        return mat.getRow(row);
    }

    /**
     * Orthogonalizes the given vectors by using Schmidt method, it is required that the
     * given vectors are linear irrelevant.
     *
     * @param vs an array of vectors
     * @return a new list of vectors
     */
    @SafeVarargs
    public static <T> List<Vector<T>> orthogonalize(Vector<T>... vs) {
        //vs    : a1,a2,a3 ... an
        //list  : b1,b2,b3 ... bn
        //temp1 : -b1/b1^2 ... -bn/bn^2
        //temp2 : used when adding
        final int n = vs.length;
        if (n < 2) {
            return Arrays.asList(vs);
        }
        final int size = vs[0].getSize();
        //size check
        for (int i = 1; i < n; i++) {
            if (vs[i].getSize() != size) {
                throw new IllegalArgumentException("vector's length=" + vs[i].getSize() + " != " + n);
            }
        }

        MathCalculator<T> mc = vs[0].getMathCalculator();
        List<Vector<T>> list = new ArrayList<>(n);

        @SuppressWarnings("unchecked")
        Vector<T>[] temp1 = new Vector[n - 1];//temp1: b/b^2
        @SuppressWarnings("unchecked")
        Vector<T>[] temp2 = new Vector[n];

        list.add(vs[0]);
        //b1 = a1
        Vector<T> prev = vs[0];
        for (int i = 1; i < n; i++) {
            temp1[i - 1] = prev.multiplyNumber(mc.negate(
                    mc.reciprocal(prev.normSq())));

            Vector<T> vec = vs[i];
            for (int j = 0; j < i; j++) {
                temp2[j] = list.get(j).multiplyNumber(temp1[j].inner(vec));
            }
            temp2[i] = vec;
            Vector<T> result = addAll(i + 1, temp2);
            list.add(result);
            prev = result;
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    public static <T> List<Vector<T>> orthogonalize(List<Vector<T>> vectors) {
        return orthogonalize(vectors.toArray(new Vector[0]));
    }

    /**
     * Orthogonalizes the given vectors by using Schmidt method.
     */
    @SafeVarargs
    public static <T> List<Vector<T>> orthogonalizeAndUnit(Vector<T>... vs) {
        List<Vector<T>> list = orthogonalize(vs);
        for (int i = 0; i < list.size(); i++) {
            list.set(i, list.get(i).unitVector());
        }
        return list;
    }

    /**
     * Orthogonalizes the given vectors by using Schmidt method.
     */
    @SuppressWarnings("unchecked")
    public static <T> List<Vector<T>> orthogonalizeAndUnit(List<Vector<T>> vectors) {
        return orthogonalizeAndUnit(vectors.toArray(new Vector[0]));
    }

    /**
     * Returns a column vector whose n-th element is map.get(n)(0 for null), 0<= n <=length-1
     */
    public static <T> Vector<T> fromIndexMap(Map<Integer, T> map, int length, MathCalculator<T> mc) {
        @SuppressWarnings("unchecked")
        T[] arr = (T[]) new Object[length];
        T z = mc.getZero();
        for (int i = 0; i < length; i++) {
            T t = map.get(i);
            arr[i] = Objects.requireNonNullElse(t, z);
        }
        return new DVector<>(arr, false, mc);
    }

    /**
     * Returns a column vector whose n-th element is <code>f.apply(n)</code>, 0<= n <=length-1
     */
    public static <T> Vector<T> vOf(int length, MathCalculator<T> mc, IntFunction<T> f) {
        @SuppressWarnings("unchecked")
        T[] arr = (T[]) new Object[length];
        for (int i = 0; i < length; i++) {
            arr[i] = Objects.requireNonNull(f.apply(i));
        }
        return new DVector<>(arr, false, mc);
    }

    /**
     * Returns a column vector filled with the given number of the given size.
     *
     * @return a column vector `(c,...,c)^T`
     */
    public static <T> Vector<T> constant(T c, int size, MathCalculator<T> mc) {
        @SuppressWarnings("unchecked") T[] arr = (T[]) new Object[size];
        Arrays.fill(arr, c);
        return new DVector<>(arr, false, mc);
    }

    /**
     * Returns a column vector of ones.
     */
    public static <T> Vector<T> ones(int size, MathCalculator<T> mc) {
        return constant(mc.getOne(), size, mc);
    }


    /**
     * Returns the result of {@literal mat * v}, it is required the vector's size is
     * equal to the matrix's column count. This method will ignore whether the vector is
     * a row vector.
     *
     * @param mat a matrix
     * @param v   a vector
     * @return {@literal mat * v} as a column vector, which has the length of {@code mat.getRowCount()}.
     */
    public static <T> Vector<T> multiplyToVector(Matrix<T> mat, Vector<T> v) {
        if (mat.column != v.getSize()) {
            throw new IllegalArgumentException("mat.column != v.size");
        }
        var mc = mat.getMathCalculator();
        @SuppressWarnings("unchecked")
        T[] result = (T[]) new Object[mat.row];
        for (int i = 0; i < mat.row; i++) {
            T t = mc.getZero();
            for (int j = 0; j < mat.column; j++) {
                t = mc.add(t, mc.multiply(mat.get(i, j), v.get(j)));
            }
            result[i] = t;
        }
        return new DVector<>(result, false, mc);
    }

    /**
     * Returns the result of {@literal v * mat}, it is required the vector's size is
     * equal to the matrix's row count. This method will ignore whether the vector is
     * a column vector.
     *
     * @param v   a vector
     * @param mat a matrix
     * @return {@literal v * mat} as a row vector, which has the length of {@code mat.getColumnCount()}.
     */
    public static <T> Vector<T> multiplyByVector(Vector<T> v, Matrix<T> mat) {
        if (mat.row != v.getSize()) {
            throw new IllegalArgumentException("mat.column != v.size");
        }
        var mc = mat.getMathCalculator();
        @SuppressWarnings("unchecked")
        T[] result = (T[]) new Object[mat.column];
        for (int i = 0; i < mat.column; i++) {
            T t = mc.getZero();
            for (int j = 0; j < mat.row; j++) {
                t = mc.add(t, mc.multiply(v.get(j), mat.get(j, i)));
            }
            result[i] = t;
        }
        return new DVector<>(result, true, mc);
    }


    /**
     * Determines whether the given vectors are linear relevant. It is required that
     * all the given vectors haves the same size.
     *
     * @param vectors a series of vectors of the same size
     * @return <code>true</code> if they are linear relevant
     */
    @SafeVarargs
    public static <T> boolean isLinearRelevant(Vector<T>... vectors) {
        return isLinearRelevant(Arrays.asList(vectors));
//        return mat.calRank() >= size;
    }


    /**
     * Determines whether the given vectors are linear relevant. It is required that
     * all the given vectors haves the same size.
     *
     * @param vectors a series of vectors of the same size
     * @return <code>true</code> if they are linear relevant
     */
    public static <T> boolean isLinearRelevant(List<? extends Vector<T>> vectors) {
        checkNonEmptyAndSameSize(vectors);
        int size = vectors.get(0).getSize();
        if (vectors.size() > size) {
            return true;
        }
        var mat = fromVectors(true, vectors);
        int rank = mat.calRank();
        return rank < vectors.size();
    }

    private static <T> void checkNonEmptyAndSameSize(List<? extends Vector<T>> vectors) {
        if (vectors.isEmpty()) {
            throw new IllegalArgumentException("Empty Vector!");
        }
        int size = vectors.get(0).getSize();
        for (var v : vectors) {
            if (v.getSize() != size) {
                throw new IllegalArgumentException("Different size!");
            }
        }
    }

    /**
     * Returns a maximum linear irrelevant vector group of the given vectors.
     *
     * @param vectors a list of vectors
     */
    @SuppressWarnings("Duplicates")
    public static <T> VectorBasis<T> maximumLinearIrrelevant(List<Vector<T>> vectors) {
        checkNonEmptyAndSameSize(vectors);
        int size = vectors.size();
        Matrix<T> mat = Matrix.fromVectors(true, vectors);
        var result = mat.toStepMatrix();
        mat = result.result;
        int[] map = ArraySup.indexArray(size);
        for (var step : result.ops) {
            if (step.ope == MatrixOperation.Operation.EXCHANGE_ROW) {
                int t = map[step.arg0];
                map[step.arg0] = map[step.arg1];
                map[step.arg1] = t;
            }
        }
        ArrayList<Vector<T>> bases = new ArrayList<>(vectors.size());
        var mc = vectors.get(0).getMc();
        int row = 0;
        while (row < mat.row) {
            if (isEmptyRow(mat, row, mc)) {
                break;
            }
            bases.add(vectors.get(map[row]).toColumnVector());
            row++;
        }
        bases.trimToSize();
        return new DVectorBasis<>(mc, vectors.get(0).getSize(), bases);
    }

    /**
     * Returns a maximum linear irrelevant vector group of the given vectors.
     *
     * @param vectors an array of vectors
     */
    @SuppressWarnings("Duplicates")
    @SafeVarargs
    public static <T> VectorBasis<T> maximumLinearIrrelevant(Vector<T>... vectors) {
        return maximumLinearIrrelevant(Arrays.asList(vectors));
    }

    private static <T> boolean isEmptyRow(Matrix<T> mat, int row, MathCalculator<T> mc) {
        for (int i = 0; i < mat.column; i++) {
            if (!mc.isZero(mat.get(row, i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the rank of the given vectors.
     */
    @SafeVarargs
    public static <T> int rank(Vector<T>... vectors) {
        return rank(Arrays.asList(vectors));
    }

    /**
     * Returns the rank of a list of vectors.
     *
     * @param vectors a list of vectors
     */
    public static <T> int rank(List<Vector<T>> vectors) {
        if (vectors.isEmpty()) {
            return 0;
        }
        return Matrix.fromVectors(true, vectors).calRank();
    }

    /**
     * Gets a calculator for vectors.
     *
     * @param mc        an instance of LinearSpaceCalculator
     * @param dimension the dimension(size) of the vector
     */
    public static <T> LinearSpaceCalculator<T, Vector<T>> calculatorV(MathCalculator<T> mc, int dimension, boolean isRow) {
        return new VectorCalculator<>(mc, dimension, isRow);
    }

    /**
     * Gets a calculator for column vectors.
     *
     * @param mc        an instance of LinearSpaceCalculator
     * @param dimension the dimension(size) of the vector
     */
    public static <T> LinearSpaceCalculator<T, Vector<T>> calculatorV(MathCalculator<T> mc, int dimension) {
        return new VectorCalculator<>(mc, dimension, false);
    }


    /**
     * Gets the corresponding calculator for the corresponding type of vectors. This method is
     * equal to <code>Vector.calculator(v.getMathCalculator(), v.getSize())</code>
     *
     * @param v a vector
     */
    public static <T> LinearSpaceCalculator<T, Vector<T>> calculatorFor(Vector<T> v) {
        return calculatorV(v.getMathCalculator(), v.getSize(), v.isRow);
    }

    /**
     * Determines whether the two vectors are the same, ignoring the difference of column and row vector.
     */
    public static <T> boolean vectorEquals(Vector<T> u, Vector<T> v) {
        if (u.getSize() != v.getSize()) {
            return false;
        }
        var size = u.getSize();
        var mc = u.getMathCalculator();
        for (int i = 0; i < size; i++) {
            if (!mc.isEqual(u.get(i), v.get(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the vector of minimum norm in the Z-span of <code>a,b</code>, that is,
     * a vector <code>v</code> such that
     * <pre>|v| = min {|ma + nb| : m, n in Z}</pre>
     */
    public static Vector<Double> shortestSpan(Vector<Double> a, Vector<Double> b) {
        if (a.getSize() != b.getSize()) {
            throw new IllegalArgumentException("a.size != b.size!");
        }
        var A = a.normSq();
        var B = b.normSq();
        if (A < B) {
            var t = a;
            a = b;
            b = t;
            var t2 = A;
            A = B;
            B = t2;
        }
        while (true) {
            var n = a.inner(b);
            var r = Math.round(n / B);
            var T = A - 2 * r * n + r * r * B;
            if (T >= B) {
                return b;
            }
            var t = Vector.subtractV(a, b.multiplyNumber(r));
            a = b;
            b = t;
            A = B;
            B = T;
        }
    }


//	public static void main(String[] args) {
//	    var v1 = Vector.createVector(new long[]{1,3,14});
//	    v1 = Vector.resizeOf(v1,-1,1);
//	    print(v1);
//		MathCalculator<Double> mc = Calculators.getCalculatorDouble();
//		@SuppressWarnings("unchecked")
//		Vector<Double>[] vecs = new Vector[4];
//		vecs[0] = Vector.createVector(mc, 1d,1d,0d);
//        vecs[1] = Vector.createVector(mc, 2d,2d,0d);
//        vecs[2] = Vector.createVector(mc, 1d,0d,1d);
//        vecs[3] = Vector.createVector(mc, -1d,0d,0d);
////		print(addVectors(vecs));
//        print(maximumLinearIrrelevant(vecs));
////		List<Vector<Double>> list = orthogonalize(vecs);
////		print(list);
////		vecs = list.toArray(vecs);
////		print(vecs[0].innerProduct(vecs[1]));
////		print(vecs[0].innerProduct(vecs[2]));
////		print(vecs[2].innerProduct(vecs[1]));
//	}
}

class VectorCalculator<T> implements LinearSpaceCalculator<T, Vector<T>> {
    private final MathCalculator<T> mc;
    private final int dimension;
    private final boolean isRow;
    private final Vector<T> zero;

    public VectorCalculator(MathCalculator<T> mc, int dimension, boolean isRow) {
        this.mc = mc;
        this.dimension = dimension;
        this.isRow = isRow;
        zero = Vector.zeroVector(dimension, isRow, mc);
    }

    @NotNull
    @Override
    public FieldCalculator<T> getScalarCalculator() {
        return mc;
    }

    @NotNull
    @Override
    public Vector<T> scalarMultiply(@NotNull T k, @NotNull Vector<T> tVector) {
        return tVector.multiplyNumber(k);
    }

    @NotNull
    @Override
    public Vector<T> apply(@NotNull Vector<T> x, @NotNull Vector<T> y) {
        return Vector.addV(x, y, isRow);
    }

    @NotNull
    @Override
    public Vector<T> inverse(@NotNull Vector<T> x) {
        return x.negate();
    }

    @NotNull
    @Override
    public Vector<T> subtract(@NotNull Vector<T> x, @NotNull Vector<T> y) {
        return Vector.subtractV(x, y, isRow);
    }

    @NotNull
    @Override
    public Vector<T> getIdentity() {
        return zero;
    }

    @Override
    public boolean isEqual(@NotNull Vector<T> x, @NotNull Vector<T> y) {
        return Vector.vectorEquals(x, y);
    }

    @Override
    public boolean isLinearDependent(@NotNull Vector<T> u, @NotNull Vector<T> v) {
        return u.isParallel(v);
    }

    @Override
    public boolean isLinearDependent(@NotNull List<? extends Vector<T>> vectors) {
        return Vector.isLinearRelevant(vectors);
    }

    //    public static void main(String[] args) {
//        var mc = Calculators.getCalculatorDouble();
//        var vc = new VectorCalculator<Double>(mc, 2);
//        Printer.print(vc);
//    }
}
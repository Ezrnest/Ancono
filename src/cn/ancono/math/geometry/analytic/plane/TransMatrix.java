package cn.ancono.math.geometry.analytic.plane;

import cn.ancono.math.MathCalculator;
import cn.ancono.math.MathObject;
import cn.ancono.math.algebra.linear.AbstractMatrix;
import cn.ancono.math.algebra.linear.Matrix;
import cn.ancono.math.numberModels.api.AlgebraModel;
import cn.ancono.math.numberModels.api.GenMatrix;
import cn.ancono.math.property.Composable;
import kotlin.jvm.functions.Function1;
import kotlin.sequences.Sequence;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * A wrapper class for transformation matrices.
 *
 * @author liyicheng
 */
public final class TransMatrix<T> extends AbstractMatrix<T> implements GenMatrix<T>,
        Composable<TransMatrix<T>>, AlgebraModel<T, TransMatrix<T>> {

    private final Matrix<T> matrix;

    /**
     * This method won't check mat's size and all the changed to the mat
     * will be reflected to this Matrix.
     */
    TransMatrix(Matrix<T> mat) {
        super(mat.getMathCalculator(), mat.getRow(), mat.getColumn());
        matrix = mat;
    }

//    /**
//     * This method won't check mat's size and all the changed to the mat
//     * will be reflected to this Matrix.
//     *
//     */
//    @SuppressWarnings("unchecked")
//    TransMatrix(Matrix<T> data, MathCalculator<T> mc) {
//        super(2, 2, mc);
//        this.data = (T[][]) data.getValues();
//    }

//    /* (non-Javadoc)
//     * @see cn.ancono.math.Matrix#getNumber(int, int)
//     */
//    @Override
//    public T get(int i, int j) {
//        return matrix.get(i,j);
//    }

    @Override
    protected T getChecked(int i, int j) {
        return matrix.get(i, j);
    }





    /* (non-Javadoc)
     * @see cn.ancono.math.algebra.abstractAlgebra.linearAlgebra.Matrix#calDet()
     */

    //store the inverse of this 
    private TransMatrix<T> inversed;

    /**
     * @throws ArithmeticException if this method failed
     */
    @Override
    public @NotNull TransMatrix<T> inverse() {
        var mc = getMc();
        if (inversed == null) {
            T deno = det();
            if (mc.isZero(deno)) {
                throw new ArithmeticException("det == 0");
            }
            inversed = valueOf(matrix.inverse(), mc);
            inversed.inversed = this;
        }
        return inversed;
    }


    /**
     * Transforms the given vector��<br />
     * {@code this��v}
     *
     * @param v a vector
     * @return transformed vector
     */
    public PVector<T> transform(PVector<T> v) {
        var mc = getMc();
//        var v =
        T _x = mc.add(mc.multiply(get(0, 0), v.x), mc.multiply(get(0, 1), v.y));
        T _y = mc.add(mc.multiply(get(1, 0), v.x), mc.multiply(get(1, 1), v.y));
        return new PVector<T>(_x, _y, mc);
    }

    /**
     * Transforms the given point��<br />
     *
     * @param p a point
     * @return transformed vector
     */
    public Point<T> transform(Point<T> p) {
        return Point.fromVector(transform(PVector.valueOf(p.x, p.y, getMc())));
    }

//    /**
//     * Returns this��tm
//     *
//     * @param tm
//     * @return
//     */
//    TransMatrix<T> multiply0(TransMatrix<T> tm) {
//        T[][] mat = gd();
//        for (int i = 0; i < 2; i++) {
//            for (int j = 0; j < 2; j++) {
//                mat[i][j] = getMc().add(getMc().multiply(data[i][0], tm.data[0][j]),
//                        getMc().multiply(data[i][1], tm.data[1][j]));
//                ;
//            }
//        }
//        return new TransMatrix<>(mat, getMc());
//    }

    /**
     * Returns a TransformMatrix that is equivalent to a composed transformation
     * that first apply this and then {@code after}.
     *
     * @param after another matrix
     * @return
     */
    @NotNull
    public TransMatrix<T> andThen(@NotNull TransMatrix<T> after) {
        return after.multiply(this);
    }

    /*
     * @see cn.ancono.math.property.Composable#compose(cn.ancono.math.property.Composable)
     */
    @NotNull
    @Override
    public TransMatrix<T> compose(@NotNull TransMatrix<T> before) {
        return this.multiply(before);
    }


    @NotNull
    @Override
    public TransMatrix<T> multiply(@NotNull TransMatrix<T> y) {
        return new TransMatrix<>(matrix.multiply(y.matrix));
    }

    @NotNull
    public TransMatrix<T> divide(@NotNull TransMatrix<T> y) {
        return multiply(y.inverse());
    }

    @Override
    public boolean valueEquals(@NotNull MathObject<T> obj) {
        if (!(obj instanceof TransMatrix)) {
            return false;
        }
        var trans = (TransMatrix<T>) obj;
        return matrix.valueEquals(trans.matrix);
    }

    @NotNull
    @Override
    public <N> TransMatrix<N> mapTo(@NotNull MathCalculator<N> newCalculator, @NotNull Function<T, N> mapper) {
        return new TransMatrix<>(matrix.mapTo(newCalculator, mapper));
    }


    @Override
    public int getSize() {
        return 4;
    }

    @NotNull
    @Override
    public Sequence<T> elementSequence() {
        return matrix.elementSequence();
    }

    @NotNull
    @Override
    public TransMatrix<T> applyAll(@NotNull Function1<? super T, ? extends T> f) {
        return new TransMatrix<>(matrix.applyAll(f));
    }

    @NotNull
    @Override
    public TransMatrix<T> add(@NotNull TransMatrix<T> y) {
        return new TransMatrix<>(matrix.add(y.matrix));
    }

    @NotNull
    @Override
    public TransMatrix<T> subtract(@NotNull TransMatrix<T> y) {
        return new TransMatrix<>(matrix.subtract(y.matrix));
    }

    @NotNull
    @Override
    public TransMatrix<T> negate() {
        return new TransMatrix<>(matrix.negate());
    }

    @Override
    public boolean isZero() {
        return matrix.isZero();
    }

    @NotNull
    @Override
    public TransMatrix<T> multiply(T t) {
        return new TransMatrix<>(matrix.multiply(t));
    }

    @NotNull
    @Override
    public TransMatrix<T> divide(T t) {
        return new TransMatrix<>(matrix.divide(t));
    }

    @Override
    public boolean isLinearRelevant(@NotNull TransMatrix<T> tTransMatrix) {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates a new transform matrix, which is
     * <pre> (x.x  x.y)
     * (y.x  y.y)
     * </pre>
     *
     * @param x a vector
     * @param y another vector
     */
    public static <T> TransMatrix<T> fromVector(PVector<T> x, PVector<T> y) {
//        T[][] data = (T[][]) new Object[][]{
//                {x.x, x.y}, {y.x, y.y}
//        };
        return valueOf(x.x, y.y, y.x, y.y, x.getMathCalculator());
    }

    /**
     * Creates a new TransfromMatrix, which is
     * <pre> (a b)
     * (c d)
     * </pre>
     */
    public static <T> TransMatrix<T> valueOf(T a, T b, T c, T d, MathCalculator<T> mc) {
//        @SuppressWarnings("unchecked")
//        T[][] data = (T[][]) new Object[][]{
//                {a, b}, {c, d}
//        };
        var matrix = Matrix.of(2, 2, mc, a, b, c, d);
        return new TransMatrix<>(matrix);
    }

    /**
     * Creates a new TransfromMatrix, which is
     * <pre> (a b)
     * (c d)
     * </pre>
     */
    public static <T> TransMatrix<T> valueOf(GenMatrix<T> mat, MathCalculator<T> mc) {
        var a = mat.get(0, 0);
        var b = mat.get(0, 1);
        var c = mat.get(1, 0);
        var d = mat.get(1, 1);
        var matrix = Matrix.of(2, 2, mc, a, b, c, d);
        return new TransMatrix<>(matrix);
    }

    /**
     * Creates a TransformMatrix:
     * <pre>
     * (0 1)
     * (1 0)
     * </pre>
     */
    public static <T> TransMatrix<T> flipXY(MathCalculator<T> mc) {
        T z = mc.getZero();
        T o = mc.getOne();
        return valueOf(z, o, o, z, mc);
    }

    /**
     * Creates a TransformMatrix:
     * <pre>
     * (-1 0)
     * ( 0 1)
     * </pre>
     */
    public static <T> TransMatrix<T> flipX(MathCalculator<T> mc) {
        T z = mc.getZero();
        T o = mc.getOne();
        return valueOf(mc.negate(o), z, z, o, mc);
    }

    /**
     * Creates a TransformMatrix:
     * <pre>
     * (1  0)
     * (0 -1)
     * </pre>
     */
    public static <T> TransMatrix<T> flipY(MathCalculator<T> mc) {
        T z = mc.getZero();
        T o = mc.getOne();
        return valueOf(o, z, z, mc.negate(o), mc);
    }

    /**
     * Creates a TransformMatrix:
     * <pre>
     * (-1 0)
     * (0 -1)
     * </pre>
     */
    public static <T> TransMatrix<T> centralSymmetry(MathCalculator<T> mc) {
        T z = mc.getZero();
        T o = mc.negate(mc.getOne());
        return valueOf(o, z, z, o, mc);
    }

    /**
     * Creates a TransformMatrix that performs a rotate operation.The returned matrix(mat)
     * will fit the following result:
     * <pre>mat * (x,y)<sup>T</sup> = (x',y')<sup>T</sup></pre>
     * Where (x,y) is the coordinate before rotation, and (x',y') is the coordinate after rotation.
     *
     * @param angle the angle to rotate (anti-clockwise)
     * @param mc    a {@link MathCalculator}
     * @return a rotation matrix
     */
    public static <T> TransMatrix<T> rotate(T angle, MathCalculator<T> mc) {
        //(cos x -sinx)
        //(sin x cos x)
        T cos = mc.cos(angle);
        T sin = mc.sin(angle);
        return valueOf(cos, mc.negate(sin), sin, cos, mc);
    }

    /**
     * Returns a TransMatrix that rotates the vector parallel to the positive direction of x axis(such as (1,0))
     * to the given vector. The vector must be non-zero.
     */
    public static <T> TransMatrix<T> rotateXAxisTo(PVector<T> v) {
        if (v.isZero()) {
            throw new IllegalArgumentException("zero vector.");
        }
        v = v.unitVector();
        T cos = v.x, sin = v.y;
        MathCalculator<T> mc = v.getMathCalculator();
        return valueOf(cos, mc.negate(sin), sin, cos, mc);
    }

    /**
     * Returns a TransformMatrix that performs multiplication.
     * <pre>
     * (kx 0)
     * (0 ky)
     * </pre>
     */
    public static <T> TransMatrix<T> multiply(T kx, T ky, MathCalculator<T> mc) {
        T z = mc.getZero();
        return valueOf(kx, z, z, ky, mc);
    }

    /**
     * Returns a TransformMatrix that performs multiplication.
     * <pre>
     * (kx 0)
     * (0  1)
     * </pre>
     */
    public static <T> TransMatrix<T> multiplyX(T kx, MathCalculator<T> mc) {
        T z = mc.getZero();
        return valueOf(kx, z, z, mc.getOne(), mc);
    }

    /**
     * Returns a TransformMatrix that performs multiplication.
     * <pre>
     * (1 0 )
     * (0 ky)
     * </pre>
     */
    public static <T> TransMatrix<T> multiplyY(T ky, MathCalculator<T> mc) {
        T z = mc.getZero();
        return valueOf(mc.getOne(), z, z, ky, mc);
    }

    /**
     * Returns the identity transformation :
     * <pre>
     * (1 0)
     * (0 1)
     * </pre>
     *
     * @param mc a {@link MathCalculator}
     * @return a new TransMatrix
     */
    public static <T> TransMatrix<T> identityTrans(MathCalculator<T> mc) {
        T z = mc.getZero();
        T o = mc.getOne();
        return valueOf(o, z, z, o, mc);
    }


    //	//test
//	public static void main(String[] args) {
//		MathCalculator<Double> mc = Calculators.getCalculatorDoubleDev();
//		TransMatrix<Double> t1 = rotate(Math.PI/4,mc),
//				t2 = t1.andThen(t1);
//		t1.printMatrix();
//		Point<Double> p = Point.valueOf(1d, 0d, mc);
//		print(t2.transform(p));
//	}
}

package cn.ancono.math.algebra.linear

import cn.ancono.math.MathCalculator
import cn.ancono.math.MathObject
import cn.ancono.math.MathObjectExtend
import cn.ancono.math.algebra.abs.calculator.EUDCalculator
import cn.ancono.math.algebra.abs.calculator.UFDCalculator
import cn.ancono.math.algebra.abs.calculator.eval
import cn.ancono.math.equation.EquationSolver
import cn.ancono.math.equation.SVPEquation
import cn.ancono.math.exceptions.ExceptionUtil
import cn.ancono.math.exceptions.UnsupportedCalculationException
import cn.ancono.math.numberModels.Fraction
import cn.ancono.math.numberModels.MathCalculatorAdapter
import cn.ancono.math.numberModels.api.*
import cn.ancono.math.numberModels.structure.Polynomial
import cn.ancono.utilities.ArraySup
import cn.ancono.utilities.ModelPatterns
import cn.ancono.utilities.StringSup
import java.util.*
import java.util.function.Function
import kotlin.math.min

typealias TransformResult<T> = Pair<MutableMatrix<T>, List<MatrixOperation<T>>>

//Created by lyc at 2021-04-27 15:33


abstract class AbstractMatrix<T>(
        mc: MathCalculator<T>,
        final override val row: Int,
        final override val column: Int)
    : MathObjectExtend<T>(mc), GenMatrix<T> {

    protected fun checkIdx(i: Int, j: Int) {
        require(i in rowIndices && j in colIndices) {
            "Index out of bound for ($i,$j), shape = ($row,$column)."
        }
    }

    /**
     * Gets an element in this matrix.
     */
    override operator fun get(i: Int, j: Int): T {
        checkIdx(i, j)
        return getChecked(i, j)
    }

    protected abstract fun getChecked(i: Int, j: Int): T

    /**
     * Returns a copy of the values in this matrix as a nesting list.
     */
    open fun getValues(): Array<Array<Any?>> {
        return Array(row) { i ->
            Array(column) { j -> this[i, j] }
        }
    }

    /**
     * Applies the function to this matrix to get a
     */
    override fun applyAll(f: (T) -> T): AbstractMatrix<T> {
        return MatrixImpl.apply1(this, mathCalculator, f)
    }


    /**
     * Returns the adjugate matrix of this matrix as an immutable view.
     *
     * The `(i,j)`-th element in the
     * adjugate matrix `A*` of matrix `A` is equal to `(-1)^{i+j}`
     * times the cofactor of `(i,j)`-th element in `A`.
     *
     * @see cofactor
     */
    open fun adjugate(): AbstractMatrix<T> {
        TODO()
    }

    /**
     * Return the determinant of this matrix, `det(this)`.
     *
     * @return `det(this)`
     */
    open fun det(): T {
        return MatrixImpl.det(this)
    }

    open fun rank(): Int {
        return MatrixImpl.rank(this)
    }

    /**
     * Returns the trace if this matrix.
     *
     * @return `tr(this)`
     */
    open fun trace(): T {
        requireSquare()
        var z = this[0, 0]
        val mc = mathCalculator
        for (i in 1 until row) {
            z = mc.add(z, this[i, i])
        }
        return z
    }

    /**
     * Return the transpose of this matrix as an immutable view.
     *
     * @return `this^T`
     */
    open fun transpose(): AbstractMatrix<T> {
        return TransposeMatrixView(this)
    }

    /**
     * Returns the factor of this matrix as an immutable view.
     */
    open fun factor(rows: IntArray, columns: IntArray): AbstractMatrix<T> {
        return FactorMatrixView.factorOf(this, rows, columns)
    }

    /**
     * Gets a sub-matrix in this matrix as a view.
     *
     * @param rowEnd exclusive
     * @param colEnd exclusive
     */
    open fun subMatrix(rowStart: Int, colStart: Int, rowEnd: Int, colEnd: Int): AbstractMatrix<T> {
        return SubMatrixView.subMatrixOf(this, rowStart, rowEnd, colStart, colEnd)
    }

    /**
     * Returns the cofactor of this matrix as an immutable view.
     */
    open fun cofactor(row: Int, col: Int): AbstractMatrix<T> {
        return cofactor(intArrayOf(row), intArrayOf(column))
    }

    /**
     * Returns the cofactor of this matrix as an immutable view.
     */
    open fun cofactor(rows: IntArray, columns: IntArray): AbstractMatrix<T> {
        return FactorMatrixView.cofactorOf(this, rows, columns)
    }

    /**
     * Gets a column from this matrix as a new vector.
     */
    open fun getColumn(col: Int): Vector<T> {
        require(col in colIndices)
        return Vector.of(row, mc) { i ->
            this.getChecked(i, col)
        }
    }

    /**
     * Gets a row from this matrix as a new vector.
     */
    open fun getRow(row: Int): Vector<T> {
        require(row in rowIndices)
        return Vector.of(column, mc) { j ->
            this.getChecked(row, j)
        }
    }

    open fun columnVectors(): List<Vector<T>> {
        return (0 until column).map { r -> getColumn(r) }
    }

    open fun rowVectors(): List<Vector<T>> {
        return (0 until row).map { r -> getRow(r) }
    }


    /**
     * Returns the inverse of this matrix.
     * It is required that this matrix is square.
     *
     * The inverse of a square matrix `A` is a matrix `B` such
     * that `AB = I`, where `I` stands for identity matrix.
     *
     *
     * @throws ArithmeticException if this matrix is not invertible
     */
    open fun inverse(): AbstractMatrix<T> {
        requireSquare()
        return MatrixImpl.inverse(this)
    }

    /**
     * Determines whether this matrix is invertible.
     */
    open fun isInvertible(): Boolean {
        return mc.isUnit(det())
    }


    /**
     * Returns the null space of this matrix.
     *
     * The null space of a `(n,m)` matrix `A` is composed of vectors of size `m`
     * that is the solution to the homogeneous linear equation `Ax=0`.
     *
     *     null space = { x | Ax = 0 }
     */
    open fun nullSpace(): VectorBasis<T> {
        return MatrixImpl.solveHomo(this)
    }

    /**
     * Returns the column space of this matrix, which is the space generated by the
     * column vectors in this matrix.
     */
    open fun columnSpace(): VectorBasis<T> {
        return MatrixImpl.columnSpace(this)
    }

    /**
     * Returns the row space of this matrix, which is the space generated by the
     * row vectors in this matrix.
     */
    open fun rowSpace(): VectorBasis<T> {
        return MatrixImpl.columnSpace(transpose())
    }

    /**
     * Returns the infinity-norm of this matrix, which is the maximal of the absolute value of the elements.
     *
     * It is required that the math calculator supports `abs()` and `compare()`
     */
    open fun normInf(): T {
        var m = mc.zero
        for (i in rowIndices) {
            for (j in colIndices) {
                val t = mc.abs(this[i, j])
                if (mc.compare(t, m) > 0) {
                    m = t
                }
            }
        }
        return m
    }

    /**
     * Returns the p-norm of matrix, which is defined by:
     *
     *     sum(|a_{ij}^p|)^(1/p)
     *
     * It is required that the calculator supports `abs()` and `exp()`.
     */
    open fun norm(p: T = mc.of(2L)): T {
        val mc = mathCalculator
        var r = mc.zero
        for (i in 0 until row) {
            for (j in 0 until column) {
                r = mc.eval { r + exp(abs(get(i, j)), p) }
            }
        }
        return mc.exp(r, mc.reciprocal(p))
    }

    override fun toString(nf: FlexibleNumberFormatter<T, MathCalculator<T>>): String {
        val data = Array(row) { i ->
            Array<String>(column) { j ->
                nf.format(this[i, j], mc)
            }
        }
        return StringSup.formatMatrix(data)
    }

    override fun valueEquals(obj: MathObject<T>): Boolean {
        if (obj !is AbstractMatrix) {
            return false
        }
        if (!isSameShape(obj)) {
            return false
        }
        for (i in 0 until row) {
            for (j in 0 until column) {
                if (!mc.isEqual(this[i, j], obj[i, j])) {
                    return false
                }
            }
        }
        return true
    }

    override fun <N> mapTo(newCalculator: MathCalculator<N>, mapper: Function<T, N>): AbstractMatrix<N> {
        return MatrixImpl.apply1(this, newCalculator) {
            mapper.apply(it)
        }
    }
}

abstract class Matrix<T>(
        mc: MathCalculator<T>,
        row: Int,
        column: Int)
    : AbstractMatrix<T>(mc, row, column), AlgebraModel<T, Matrix<T>> {


    /*
    Conventions:
    i: always the index of row
    j: always the index of row

     */

    /**
     * Determines whether this matrix is all zeros.
     */
    override fun isZero(): Boolean {
        for (i in 0 until row) {
            for (j in 0 until column) {
                if (!mc.isZero(this[i, j])) {
                    return false
                }
            }
        }
        return true
    }


    override fun <N> mapTo(newCalculator: MathCalculator<N>, mapper: Function<T, N>): Matrix<N> {
        return MatrixImpl.apply1(this, newCalculator) {
            mapper.apply(it)
        }
    }


    /**
     * Applies the function to this matrix to get a
     */
    override fun applyAll(f: (T) -> T): Matrix<T> {
        return MatrixImpl.apply1(this, mathCalculator, f)
    }

    override fun add(y: Matrix<T>): Matrix<T> {
        return MatrixImpl.add(this, y)
    }

    override fun negate(): Matrix<T> {
        return MatrixImpl.negate(this)
    }

    override fun subtract(y: Matrix<T>): Matrix<T> {
        return MatrixImpl.subtract(this, y)
    }

    override fun multiply(k: T): Matrix<T> {
        return MatrixImpl.multiply(this, k)
    }

    override fun divide(k: T): Matrix<T> {
        return MatrixImpl.divide(this, k)
    }

    override fun multiply(y: Matrix<T>): Matrix<T> {
        return MatrixImpl.multiply(this, y)
    }

    override fun pow(n: Long): Matrix<T> {
        require(isSquare())
        if (n == 0L) {
            if (isZero()) {
                ExceptionUtil.zeroExponent()
            }
            return identity(row, mathCalculator)
        }
        return super.pow(n)
    }

    override fun transpose(): Matrix<T> {
        return TransposeMatrixView(this)
    }


    override fun factor(rows: IntArray, columns: IntArray): Matrix<T> {
        return FactorMatrixView.factorOf(this, rows, columns)
    }

    override fun subMatrix(rowStart: Int, colStart: Int, rowEnd: Int, colEnd: Int): Matrix<T> {
        return SubMatrixView.subMatrixOf(this, rowStart, rowEnd, colStart, colEnd)
    }

    override fun cofactor(row: Int, col: Int): Matrix<T> {
        return cofactor(intArrayOf(row), intArrayOf(column))
    }

    override fun cofactor(rows: IntArray, columns: IntArray): Matrix<T> {
        return FactorMatrixView.cofactorOf(this, rows, columns)
    }

    override fun adjugate(): Matrix<T> {
        TODO()
    }

    /*
    Primary transformations:
     */

    open fun toUpperTriangle(): Matrix<T> {
        return toUpperTriangleWay().first
    }

    open fun toUpperTriangleWay(): TransformResult<T> {
        val m = copyOf(this)
        val list = mutableListOf<MatrixOperation<T>>()
        MatrixImpl.toUpperTriangle(m, list)
        return m to list
    }

    open fun toEchelonWay(): TransformResult<T> {
        val m = copyOf(this)
        val list = mutableListOf<MatrixOperation<T>>()
        MatrixImpl.toEchelon(m, column, list)
        return m to list
    }

    open fun toEchelon(): Matrix<T> {
        return toEchelonWay().first
    }


    /*
    Inverse, pseudo inverse, column space...
     */

    override fun inverse(): Matrix<T> {
        return MatrixImpl.inverse(this)
    }

    /**
     * Computes the (general) LU decomposition of the given matrix `A` , returns a tuple of matrices
     * `(P,L,U)` such that
     * `PA = LU`, `P` is a permutation matrix, `L` is a lower triangular matrix with 1
     * as diagonal elements, and
     * `U` is a upper triangular matrix.
     *
     * It is required that the matrix is invertible.
     *
     * **Note**: This method is not designed for numerical computation but for demonstration.
     *
     * @return a tuple a matrices `(P,L,U)`
     */
    open fun decompLU(): Triple<Matrix<T>, Matrix<T>, Matrix<T>> {
        return MatrixSupKt.decompositionLU(this)
    }

    /**
     * Returns the QR-decomposition of this square matrix. `Q` is an orthogonal matrix and `R` is an
     * upper-triangle matrix. If this matrix is invertible, there is only one decomposition.
     *
     * @return `(Q, R)` as a pair
     */
    open fun decompQR(): Pair<Matrix<T>, Matrix<T>> {
        return MatrixImpl.decompQR(this)
    }

    /**
     * Decomposes a symmetric semi-positive definite matrix `A = L L^T`, where
     * `L` is a lower triangular matrix.
     *
     * @return a lower triangular matrix `L`.
     */
    open fun decompCholesky(): Matrix<T> {
        //Created by lyc at 2020-09-26 10:47
        return MatrixSupKt.decompositionCholesky(this);
    }

    /**
     * Decomposes a symmetric matrix `A = L D L^T`, where
     * `L` is a lower triangular matrix and `D` is a diagonal matrix.
     *
     * @return `(L, diag(D))`, where `L` is a lower triangular matrix, `diag(D)` is a vector of diagonal elements
     * of `D`.
     */
    open fun decompCholeskyD(): Pair<Matrix<T>, Vector<T>> {
        //Created by lyc at 2020-09-26 10:47
        return MatrixSupKt.decompositionCholeskyD(this);
    }

    /**
     * Returns the Frobenius normal form of this matrix.
     */
    open fun toFrobeniusForm(): Matrix<T> {
        return charMatrix().toFrobeniusForm(mathCalculator)
    }

    /**
     * Returns the congruence diagonal normal form `J` of this matrix `A` and the corresponding transformation `P`,
     * which satisfies
     *
     *     P.T * A * P = J
     *
     * @return `(J, P)`.
     */
    open fun toCongDiagForm(): Pair<Matrix<T>, Matrix<T>> {
        return MatrixImpl.toCongDiagonalForm(this)
    }

    /**
     * Transform this matrix to Hermit Form.
     *
     * It is required that the calculator is a
     * [cn.ancono.math.algebra.abs.calculator.EUDCalculator].
     */
    open fun toHermitForm(): Matrix<T> {
        return MatrixSupKt.toHermitForm(this)
    }

    /**
     * Transforms this matrix to Smith normal form, a diagonal matrix with the following property:
     *
     *     m[i,i] | m[i+1,i+1]  for i <= r,
     *     m[i,i] = 0, for i > r
     *
     *
     * It is required that the MathCalculator of this matrix is an [EUDCalculator].
     *
     * For example, the Smith normal form of matrix `[[1 2 3][4 5 6][7 8 9]]` can be
     * `diag(1,3,0)`
     */
    open fun toSmithForm(): Matrix<T> {
        //Created by lyc at 2020-03-10 14:54
        TODO()
//        return LambdaMatrixSup.toSmithForm(this)
    }

    /**
     * Transforms this matrix to (upper) Hessenberg form.
     */
    open fun toHessenbergForm(): Matrix<T> {
        TODO()
    }

    /**
     * Returns the characteristic matrix:
     * `λI-this`, which is a matrix of polynomial.
     */
    open fun charMatrix(): Matrix<Polynomial<T>> {
        return MatrixImpl.charMatrix(this, Polynomial.getCalculator(mathCalculator))
    }

    /**
     * Returns the characteristic polynomial of this matrix. It is required that
     * this matrix is a square matrix.
     */
    open fun charPoly(): Polynomial<T> {
        requireSquare()
        return charMatrix().det()
    }

    open fun charEquation(): SVPEquation<T> {
        requireSquare()
        return SVPEquation.fromPolynomial(charPoly())
    }

    /**
     * Computes the eigenvalues of this matrix.
     * <p>For example, assume {@code this} =
     * <pre>(1 0)
     * (0,1)</pre>
     * then this method will return a list of {@code [1,1]}
     *
     * @param equationSolver a MathFunction to solve the equation, the length of the list should be equal to
     *                       the degree of the equation.
     * @return a list of eigenvalues
     */
    fun eigenvalues(solver: EquationSolver<T, SVPEquation<T>>): List<T> {
        val equation = charEquation();
        return solver.solve(equation);
    }

    /**
     * Computes the eigenvalues of this matrix and their corresponding vectors.
     *
     * @param equationSolver a MathFunction to solve the equation, the length of the list should be equal to
     *                       the degree of the equation.
     */
    open fun eigenvaluesAndVectors(equationSolver: EquationSolver<T, SVPEquation<T>>)
            : List<Pair<T, Vector<T>>> {
        val eigenvalues = eigenvalues(equationSolver)
        //vectors
        val result: MutableList<Pair<T, Vector<T>>> = ArrayList(eigenvalues.size)
        val size = eigenvalues.size
        val mc = mc
        var i = 0
        while (i < size) {
            val x = eigenvalues[i]
            var times = 1
            while (++i < size) {
                val y = eigenvalues[i]
                if (mc.isEqual(x, y)) {
                    times++
                } else {
                    break
                }
            }
            val A: Matrix<T> = this.subtract(diag(x, row, mc))
            val basis = solveHomo(A)
            for (k1 in basis.vectors) {
                result.add(Pair(x, k1))
            }
            if (times > basis.rank) {
                for (k in basis.rank until times) {
                    result.add(Pair(x, basis.elements[0]))
                }
            }
        }
        return result
    }


    companion object {

        @JvmStatic
        fun <T> zero(row: Int, column: Int, mc: MathCalculator<T>): MutableMatrix<T> {
            return AMatrix.zero(row, column, mc)
        }

        @JvmStatic
        fun <T> of(row: Int, column: Int, mc: MathCalculator<T>, supplier: (Int, Int) -> T): MutableMatrix<T> {
            return AMatrix.of(row, column, mc, supplier)
        }


        /**
         * Create a matrix according to the given array.The row count of the matrix
         * will be the first dimension's length of the array,and the column count of
         * the matrix will be the second dimension's maximum length of the array.
         */
        @JvmStatic
        fun <T> of(mat: Array<Array<T>>, mc: MathCalculator<T>): MutableMatrix<T> {
            return AMatrix.of(mat, mc)
        }

        @JvmStatic
        fun <T> of(row: Int, column: Int, mc: MathCalculator<T>, elements: List<T>): MutableMatrix<T> {
            return AMatrix.of(row, column, mc, elements)
        }

        @SafeVarargs
        @JvmStatic
        fun <T> of(row: Int, column: Int, mc: MathCalculator<T>, vararg elements: T): MutableMatrix<T> {
            return AMatrix.of(row, column, mc, elements.asList())
        }


        fun <T> copyOf(m: GenMatrix<T>, mc: MathCalculator<T>): MutableMatrix<T> {
            return MatrixImpl.copyOf(m, mc)
        }

        fun <T> copyOf(m: AbstractMatrix<T>): MutableMatrix<T> {
            return MatrixImpl.copyOf(m)
        }


        fun <T> asMatrix(m: GenMatrix<T>, mc: MathCalculator<T>): Matrix<T> {
            if (m is Matrix) {
                return m
            }
            return copyOf(m, mc)
        }

        @JvmStatic
        fun <T> diag(elements: List<T>, mc: MathCalculator<T>): MutableMatrix<T> {
            return AMatrix.diag(elements, mc)
        }

        @JvmStatic
        fun <T> diag(elements: AbstractVector<T>): MutableMatrix<T> {
            return AMatrix.diag(elements.toList(), elements.mathCalculator)
        }

        @JvmStatic
        fun <T> diag(d: T, n: Int, mc: MathCalculator<T>): MutableMatrix<T> {
            return AMatrix.diag(d, n, mc)
        }

        @JvmStatic
        fun <T> identity(n: Int, mc: MathCalculator<T>): MutableMatrix<T> {
            return AMatrix.identity(n, mc)
        }

        /**
         * Concatenates two matrix `A, B` to a new matrix `(A, B)`.
         *
         * It is required that `A` and `B` have that same row count.
         */
        fun <T> concatColumn(a: AbstractMatrix<T>, b: AbstractMatrix<T>): MutableMatrix<T> {
            val expanded = AMatrix.zero(a.row, a.column + b.column, a.mathCalculator)
            val col = a.column
            expanded.setAll(0, 0, a)
            expanded.setAll(0, col, b)
            return expanded
        }

        /**
         * Multiplies several matrix, using dynamic programming to minimize time cost.
         */
        @JvmStatic
        @SafeVarargs
        fun <T> product(mats: List<Matrix<T>>): Matrix<T> {
            require(mats.isNotEmpty())
            val toModel = Function { x: Matrix<T> -> intArrayOf(x.row, x.column) }
            return ModelPatterns.reduceDP(0, mats.size,
                    { x: Int -> mats[x] },
                    { m1, m2 -> m1.multiply(m2) }, toModel,
                    { x: IntArray, y: IntArray -> intArrayOf(x[0], y[1]) }) { x: IntArray, y: IntArray -> x[0] * y[0] * y[1] }
        }

        /**
         * Multiplies several matrix, using dynamic programming to minimize time cost.
         */
        @JvmStatic
        @SafeVarargs
        fun <T> product(vararg mats: Matrix<T>): Matrix<T> {
            return product(mats.asList())
        }

        @JvmStatic
        fun <T> fromVectors(vectors: List<AbstractVector<T>>, asColumn: Boolean = true): Matrix<T> {
            return AMatrix.fromVectors(vectors, asColumn)
        }

        @JvmStatic
        @SafeVarargs
        fun <T> fromVectors(asColumn: Boolean = true, vararg vectors: AbstractVector<T>): Matrix<T> {
            return fromVectors(vectors.asList(), asColumn)
        }

        @JvmStatic
        fun <T> sylvesterDet(p1: Polynomial<T>, p2: Polynomial<T>): Matrix<T> {
            val n = p1.degree
            val m = p2.degree
            val size = m + n
            val result = zero(size, size, p1.mathCalculator)
            for (row in 0 until m) {
                for (i in 0..n) {
                    result[row, i + row] = p1[n - i]
                }
            }
            for (row in m until size) {
                for (i in 0..m) {
                    result[row, i + row - m] = p2[m - i]
                }
            }
            return result
        }


        @JvmStatic
        fun <T> calculator(row: Int, column: Int, mc: MathCalculator<T>): MatrixCal<T> {
            require(row > 0 && column > 0)
            if (row == column) {
                return calculator(row, mc)
            }
            return MatrixCal(mc, row, column)
        }

        @JvmStatic
        fun <T> calculator(n: Int, mc: MathCalculator<T>): SquareMatrixCal<T> {
            require(n > 0)
            return SquareMatrixCal(mc, n)
        }

        @JvmStatic
        fun <T> calculatorFor(m: Matrix<T>): MatrixCal<T> {
            return calculator(m.row, m.column, m.mathCalculator)
        }


        @JvmStatic
        fun <T> solveLinear(m: AbstractMatrix<T>, b: AbstractMatrix<T>): Triple<Matrix<T>, VectorBasis<T>, Boolean> {
            return MatrixImpl.solveLinear(m, b)
        }

        @JvmStatic
        fun <T> solveLinear(m: AbstractMatrix<T>, b: AbstractVector<T>): LinearEquationSolution<T> {
            return LinearEquationSolution.of(MatrixImpl.solveLinear(m, b))
        }

        @JvmStatic
        fun <T> solveLinearExpanded(expanded: AbstractMatrix<T>): LinearEquationSolution<T> {
            val (m, basis, so) = MatrixImpl.solveLinear(expanded.toMutable(), expanded.column - 1)
            return LinearEquationSolution.of(m.getColumn(0), basis, so)
        }

//        @JvmStatic
//        fun <T> solveLinearExpanded(expanded: AbstractMatrix<T>, column: Int = expanded.column-1): Triple<Matrix<T>, VectorBasis<T>, Boolean> {
//            return MatrixImpl.solveLinear(expanded.toMutable(),column)
//        }

        @JvmStatic
        fun <T> solveHomo(m: AbstractMatrix<T>): VectorBasis<T> {
            return MatrixImpl.solveHomo(m)
        }

        /**
         * Determines whether the two matrices are similar.
         */
        fun <T> isSimilar(a: Matrix<T>, b: Matrix<T>): Boolean {
            val pc = Polynomial.getCalculator(a.mathCalculator)
            var x = MatrixImpl.charMatrix(a, pc)
            var y = MatrixImpl.charMatrix(b, pc)
            x = x.toNormalForm()
            y = y.toNormalForm()
            return x.valueEquals(y)
        }
    }


}


abstract class MutableMatrix<T>(mc: MathCalculator<T>, row: Int, column: Int) : Matrix<T>(mc, row, column) {

    open operator fun set(i: Int, j: Int, x: T) {
        checkIdx(i, j)
        setChecked(i, j, x)
    }

    open fun setAll(r: Int, c: Int, m: GenMatrix<T>) {
        for (i in m.rowIndices) {
            for (j in m.rowIndices) {
                this[i + r, j + c] = m[i, j]
            }
        }
    }

    internal abstract fun setChecked(i: Int, j: Int, x: T)


    override fun applyAll(f: (T) -> T): MutableMatrix<T> {
        return MatrixImpl.apply1(this, mathCalculator, f)
    }

    override fun add(y: Matrix<T>): MutableMatrix<T> {
        return MatrixImpl.add(this, y)
    }

    override fun negate(): MutableMatrix<T> {
        return MatrixImpl.negate(this)
    }

    override fun subtract(y: Matrix<T>): MutableMatrix<T> {
        return MatrixImpl.subtract(this, y)
    }

    override fun multiply(k: T): MutableMatrix<T> {
        return MatrixImpl.multiply(this, k)
    }

    override fun divide(k: T): MutableMatrix<T> {
        return MatrixImpl.divide(this, k)
    }

    override fun multiply(y: Matrix<T>): MutableMatrix<T> {
        return MatrixImpl.multiply(this, y)
    }


    open operator fun plusAssign(y: Matrix<T>) {
        val mc = mathCalculator
        for (i in rowIndices) {
            for (j in colIndices) {
                this[i, j] = mc.add(this[i, j], y[i, j])
            }
        }
    }

    open operator fun minusAssign(y: Matrix<T>) {
        val mc = mathCalculator
        for (i in rowIndices) {
            for (j in colIndices) {
                this[i, j] = mc.subtract(this[i, j], y[i, j])
            }
        }
    }

    open operator fun timesAssign(k: T) {
        val mc = mathCalculator
        for (i in rowIndices) {
            for (j in colIndices) {
                this[i, j] = mc.multiply(k, this[i, j])
            }
        }
    }

    open operator fun divAssign(k: T) {
        val mc = mathCalculator
        for (i in rowIndices) {
            for (j in colIndices) {
                this[i, j] = mc.divide(this[i, j], k)
            }
        }
    }

    open fun transform(f: (T) -> T) {
        for (i in rowIndices) {
            for (j in colIndices) {
                this[i, j] = f(this[i, j])
            }
        }
    }

    open fun negateInplace() {
        transform { mc.negate(it) }
    }

    open fun copy(): MutableMatrix<T> {
        return AMatrix.copyOf(this, mathCalculator)
    }


    /*
    Primary operations
     */


    abstract fun multiplyAddRow(r1: Int, r2: Int, k: T, colStart: Int = 0, colEnd: Int = column)

    abstract fun multiplyAddCol(c1: Int, c2: Int, k: T, rowStart: Int = 0, rowEnd: Int = row)

    abstract fun swapRow(r1: Int, r2: Int, colStart: Int = 0, colEnd: Int = column)

    abstract fun swapCol(c1: Int, c2: Int, rowStart: Int = 0, rowEnd: Int = row)

    abstract fun multiplyRow(r: Int, k: T, colStart: Int = 0, colEnd: Int = column)

    abstract fun divideRow(r: Int, k: T, colStart: Int = 0, colEnd: Int = column)

    abstract fun multiplyCol(c: Int, k: T, rowStart: Int = 0, rowEnd: Int = row)

}


class AMatrix<T> internal constructor(
        mc: MathCalculator<T>, row: Int, column: Int,
        val data: Array<Any?>)
    : MutableMatrix<T>(mc, row, column) {

    init {
        require(row * column == data.size)
        require(data.isNotEmpty())
    }

    private fun toPos(i: Int, j: Int): Int {
        return i * column + j
    }

    override fun <N> mapTo(newCalculator: MathCalculator<N>, mapper: Function<T, N>): AMatrix<N> {
        val newData = Array<Any?>(data.size) {
            @Suppress("UNCHECKED_CAST")
            mapper.apply(data[it] as T)
        }
        return AMatrix(newCalculator, row, column, newData)
    }

    override fun getChecked(i: Int, j: Int): T {
        @Suppress("UNCHECKED_CAST")
        return data[toPos(i, j)] as T
    }

    override fun setChecked(i: Int, j: Int, x: T) {
        try {
            data[toPos(i, j)] = x

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun setAll(r: Int, c: Int, m: GenMatrix<T>) {
        if (m !is AMatrix) {
            super.setAll(r, c, m)
            return
        }
        val mData = m.data
        for (i in m.rowIndices) {
            val p = m.toPos(i, 0)
            val destPos = toPos(i + r, c)
            System.arraycopy(mData, p, data, destPos, m.column)
        }
    }

    override fun copy(): AMatrix<T> {
        return AMatrix(mathCalculator, row, column, data.clone())
    }

    override fun add(y: Matrix<T>): MutableMatrix<T> {
        if (y is AMatrix<T>) {
            return apply2(this, y, mc::add)
        }
        return super.add(y)
    }

    override fun negate(): MutableMatrix<T> {
        return apply1(this, mc::negate)
    }

    override fun subtract(y: Matrix<T>): MutableMatrix<T> {
        if (y is AMatrix<T>) {
            return apply2(this, y, mc::subtract)
        }
        return super.subtract(y)
    }

    override fun multiply(k: T): MutableMatrix<T> {
        return apply1(this) { mc.multiply(k, it) }
    }

    override fun divide(k: T): MutableMatrix<T> {
        return apply1(this) { mc.divide(k, it) }
    }

    override fun negateInplace() {
        for (i in data.indices) {
            @Suppress("UNCHECKED_CAST")
            data[i] = mc.negate(data[i] as T)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun multiplyAddRow(r1: Int, r2: Int, k: T, colStart: Int, colEnd: Int) {
        val s1 = toPos(r1, 0)
        val s2 = toPos(r2, 0)
        for (l in colStart until colEnd) {
            data[s2 + l] = mc.eval { (data[s2 + l] as T) + k * (data[s1 + l] as T) }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun multiplyAddCol(c1: Int, c2: Int, k: T, rowStart: Int, rowEnd: Int) {
        val l = toPos(rowStart, 0)
        for (r in rowStart until rowEnd) {
            data[l + c2] = mc.eval { (data[l + c2] as T) + k * (data[l + c1] as T) }
        }
    }

    override fun swapRow(r1: Int, r2: Int, colStart: Int, colEnd: Int) {
        val s1 = toPos(r1, 0)
        val s2 = toPos(r2, 0)
        for (l in colStart until colEnd) {
            val t = data[s1 + l]
            data[s1 + l] = data[s2 + l]
            data[s2 + l] = t
        }
    }

    override fun swapCol(c1: Int, c2: Int, rowStart: Int, rowEnd: Int) {
        var l = toPos(rowStart, 0)
        for (r in rowStart until rowEnd) {
            val t = data[l + c1]
            data[l + c1] = data[l + c2]
            data[l + c2] = t
            l += row
        }
    }

    override fun multiplyRow(r: Int, k: T, colStart: Int, colEnd: Int) {
        val d = toPos(r, 0)
        for (l in colStart until colEnd) {
            @Suppress("UNCHECKED_CAST")
            data[d + l] = mc.multiply(k, data[d + l] as T)
        }
    }

    override fun divideRow(r: Int, k: T, colStart: Int, colEnd: Int) {
        val d = toPos(r, 0)
        for (l in colStart until colEnd) {
            @Suppress("UNCHECKED_CAST")
            data[d + l] = mc.divide(data[d + l] as T, k)
        }
    }

    override fun multiplyCol(c: Int, k: T, rowStart: Int, rowEnd: Int) {
        for (r in rowStart until rowEnd) {
            val pos = toPos(r, c)
            @Suppress("UNCHECKED_CAST")
            data[pos] = mc.multiply(k, data[pos] as T)
        }


    }

    companion object {

        @Suppress("UNCHECKED_CAST")
        private inline fun <T> apply2(x: AMatrix<T>, y: AMatrix<T>, f: (T, T) -> T): AMatrix<T> {
            require(x.isSameShape(y))
            val d1 = x.data
            val d2 = y.data
            val ndata = Array<Any?>(d1.size) { k ->
                f(d1[k] as T, d2[k] as T)
            }
            return AMatrix(x.mc, x.row, x.column, ndata)
        }

        private inline fun <T> apply1(x: AMatrix<T>, f: (T) -> T): AMatrix<T> {
            val data = x.data
            val newData = Array<Any?>(data.size) { k ->
                @Suppress("UNCHECKED_CAST")
                f(data[k] as T)
            }
            return AMatrix(x.mc, x.row, x.column, newData)
        }

        fun <T> zero(row: Int, column: Int, mc: MathCalculator<T>): AMatrix<T> {
            require(row > 0 && column > 0)
            val data = ArraySup.fillArr(row * column, mc.zero, Any::class.java)
            return AMatrix(mc, row, column, data)
        }


        fun <T> copyOf(x: GenMatrix<T>, mc: MathCalculator<T>): AMatrix<T> {
            val copy = zero(x.row, x.column, mc)
            for (i in copy.rowIndices) {
                for (j in copy.colIndices) {
                    copy.setChecked(i, j, x[i, j])
                }
            }
            return copy
        }

        fun <T> copyOfRange(x: AMatrix<T>,
                            r0: Int, c0: Int,
                            r1: Int, c1: Int): AMatrix<T> {
            val r = r1 - r0
            val c = c1 - c0
            require(r > 0 && c > 0)
            val newData = arrayOfNulls<Any>(r * c)
            val data = x.data
            for (i in 0 until r) {
                val pos = x.toPos(i + r0, c0)
                System.arraycopy(data, pos, newData, i * c, c)
            }
            return AMatrix(x.mathCalculator, r, c, newData)
        }

        fun <T> copyOfRange(x: GenMatrix<T>, mc: MathCalculator<T>,
                            rowStart: Int, colStart: Int,
                            rowEnd: Int, colEnd: Int): AMatrix<T> {
            if (x is AMatrix) {
                return copyOfRange(x, rowStart, colStart, rowEnd, colEnd)
            }
            return of(rowEnd - rowStart, colEnd - colStart, mc) { i, j ->
                x[i + rowStart, j + colStart]
            }
        }

        fun <T> of(row: Int, column: Int, mc: MathCalculator<T>, supplier: (Int, Int) -> T): AMatrix<T> {
            require(row > 0 && column > 0)
            val data = arrayOfNulls<Any>(row * column)
            var pos = 0
            for (i in 0 until row) {
                for (j in 0 until column) {
                    data[pos++] = supplier(i, j)
                }
            }
            return AMatrix(mc, row, column, data)
        }

        /**
         * Create a matrix according to the given array.The row count of the matrix
         * will be the first dimension's length of the array,and the column count of
         * the matrix will be the second dimension's maximum length of the array.
         */
        fun <T> of(mat: Array<Array<T>>, mc: MathCalculator<T>): AMatrix<T> {
            Objects.requireNonNull(mat)
            val row = mat.size
            var column = -1
            for (arr in mat) {
                column = column.coerceAtLeast(arr.size)
            }
            val result = zero(row, column, mc)
            for (i in 0 until row) {
                mat[i].copyInto(result.data, i * column)
            }
            return result
        }

        fun <T> of(row: Int, column: Int, mc: MathCalculator<T>, elements: List<T>): AMatrix<T> {
            require(elements.size == row * column) {
                "Required $row * $column = ${row * column} elements, but ${elements.size} is given."
            }
            require(elements.isNotEmpty()) {
                "The matrix must not be empty!"
            }

            val data = elements.toTypedArray<Any?>()
            return AMatrix(mc, row, column, data)
        }

        fun <T> diag(elements: List<T>, mc: MathCalculator<T>): AMatrix<T> {
            require(elements.isNotEmpty())
            val n = elements.size
            val result = zero(n, n, mc)
            var l = 0
            for (d in elements) {
                result.data[l] = d
                l += n + 1
            }
            return result
        }

        fun <T> diag(d: T, n: Int, mc: MathCalculator<T>): AMatrix<T> {
            require(n > 0)
            val result = zero(n, n, mc)
            var l = 0
            for (i in 0 until n) {
                result.data[l] = d
                l += n + 1
            }
            return result
        }

        fun <T> identity(n: Int, mc: MathCalculator<T>): AMatrix<T> {
            return diag(mc.one, n, mc)
        }

        fun <T> fromVectors(vectors: List<AbstractVector<T>>, asColumn: Boolean): AMatrix<T> {
            val mc = vectors[0].mathCalculator
            if (asColumn) {
                val row = vectors[0].size
                val column = vectors.size
                val result = zero(row, column, mc)
                for (j in 0 until column) {
                    val v = vectors[j]
                    for (i in 0 until row) {
                        result.setChecked(i, j, v[i])
                    }
                }
                return result
            } else {
                val row = vectors.size
                val column = vectors[0].size
                val result = zero(row, column, mc)
                for (i in result.rowIndices) {
                    val v = vectors[i]
                    for (j in result.colIndices) {
                        result.setChecked(i, j, v[j])
                    }
                }
                return result
            }
        }
    }
}

internal object MatrixImpl {

    fun <T> copyOf(matrix: GenMatrix<T>, mc: MathCalculator<T>): MutableMatrix<T> {
        if (matrix is MutableMatrix) {
            return matrix.copy()
        }
        return AMatrix.copyOf(matrix, mc)
    }

    fun <T> copyOf(matrix: AbstractMatrix<T>): MutableMatrix<T> {
        return copyOf(matrix, matrix.mathCalculator)
    }

    internal fun <T> det(m: AbstractMatrix<T>): T {
        val mc = m.mathCalculator
        if (m.row == 1) {
            return m[0, 0]
        }
        if (m.row == 2) {
            return mc.eval {
                m[0, 0] * m[1, 1] - m[0, 1] * m[1, 0]
            }
        }
        if (m.row == 3) {
            return mc.eval {
                m[0, 0] * m[1, 1] * m[2, 2] +
                        m[0, 1] * m[1, 2] * m[2, 0] +
                        m[0, 2] * m[1, 0] * m[2, 1] -
                        m[0, 0] * m[1, 2] * m[2, 1] -
                        m[0, 1] * m[1, 0] * m[2, 2] -
                        m[0, 2] * m[1, 1] * m[2, 0]
            }
        }
        return detGaussBareiss(copyOf(m))
    }

    //    /**
    //     * Return det(this), this method computes the determinant of this
    //     * matrix by the definition.
    //     *
    //     * @return det(this)
    //     * @throws ArithmeticException if this Matrix is not a square matrix.
    //     */
    //    public T calDetDefault() {
    //        //just calculate the value by recursion definition.
    //
    //    }
    internal fun <T> detGaussBareiss(mat: MutableMatrix<T>): T {
        //Created by lyc at 2020-03-05 19:18
        /*
        Refer to 'A Course in Computational Algebraic Number Theory' Algorithm 2.2.6

        Explanation of the algorithm:
        We still use the primary transformation to eliminate elements in the matrix, but here we store the potential
        denominator and divide them only when necessary.
        For each loop, we eliminate the size of the matrix by one, but we still use the same array and the top-left
        element of remaining matrix is at the position (k,k).

        Recall the vanilla elimination process, assuming the element at (k,k) is non-zero, we multiply a factor to
        the first row and subtract it from i-th row. The factor is equal to m[i,k] / m[k,k]. This row transformation
        will affect the i-th row, changing it element m[i,j] to m[i,j] - m[k,j] * m[i,k] / m[k,k]. However, since
        we don't want to do division, we extract the denominator m[k,k] and so the resulting element is
            m[i,j] * m[k,k] - m[k,j] * m[i,k]
        After a loop, all element below m[k,k] are effectively zero, and the determinant of the original matrix is
        equal to the determinant of the remaining matrix.

         */
        val mc = mat.mathCalculator
        val division: (T, T) -> T = if (mc is UFDCalculator<*>) {
            @Suppress("UNCHECKED_CAST")
            val ufd: EUDCalculator<T> = mc as EUDCalculator<T>
            ufd::divideToInteger
            // avoid numeric imprecision
            // for example, in Polynomial<Double>
        } else {
            mc::divide
        }
        val n: Int = mat.row
        var d = mc.one // the denominator that we store
        var positive = true
        for (k in 0 until n) {
            //locate the top-left element used for elimination first, it must be non-zero
            if (mc.isZero(mat[k, k])) {
                var allZero = true
                var i = k
                while (i < n) {
                    if (mc.isZero(mat[i, k])) {
                        i++
                        continue
                    }
                    allZero = false
                    break
                }
                if (allZero) {
                    return mc.zero
                }
                // row swap
                mat.swapRow(i, k, k)
                positive = !positive
            }
            val p: T = mat[k, k]
            for (i in k + 1 until n) {
                for (j in k + 1 until n) {
                    val t = mc.eval {
                        p * mat[i, j] - mat[i, k] * mat[k, j]
                    }
                    mat[i, j] = division(t, d) //
                }
            }
            d = p
        }
        return if (positive) {
            mat[n - 1, n - 1]
        } else {
            mc.negate(mat[n - 1, n - 1])
        }
    }

    internal fun <T> multiply(x: Matrix<T>, y: Matrix<T>): AMatrix<T> {
        require(x.column == y.row) {
            "Shape mismatch in multiplication: (${x.row},${x.column}) (${y.row},${y.column})"
        }
        val mc = x.mathCalculator
        val result = AMatrix.zero(x.row, y.column, mc)
        for (i in x.rowIndices) {
            for (j in y.colIndices) {
                var t = mc.zero
                for (k in x.colIndices) {
                    t = mc.eval { t + x[i, k] * y[k, j] }
                }
                result.setChecked(i, j, t)
            }
        }
        return result
    }

    private inline fun <T> apply2(x: Matrix<T>, y: Matrix<T>, f: (T, T) -> T): AMatrix<T> {
        require(x.isSameShape(y))
        val mc = x.mathCalculator
        val result = AMatrix.zero(x.row, x.column, mc)
        for (i in x.rowIndices) {
            for (j in x.colIndices) {
                result.setChecked(i, j, f(x[i, j], y[i, j]))
            }
        }
        return result
    }

    internal inline fun <T, N> apply1(x: AbstractMatrix<T>, nc: MathCalculator<N>, f: (T) -> N)
            : AMatrix<N> {
        val result = AMatrix.zero(x.row, x.column, nc)
        for (i in x.rowIndices) {
            for (j in x.colIndices) {
                result.setChecked(i, j, f(x[i, j]))
            }
        }
        return result
    }


    internal fun <T> add(x: Matrix<T>, y: Matrix<T>): AMatrix<T> {
        return apply2(x, y, x.mathCalculator::add)
    }

    internal fun <T> subtract(x: Matrix<T>, y: Matrix<T>): AMatrix<T> {
        return apply2(x, y, x.mathCalculator::subtract)
    }

    internal fun <T> negate(x: Matrix<T>): AMatrix<T> {
        return apply1(x, x.mathCalculator, x.mathCalculator::negate)
    }


    internal fun <T> multiply(x: Matrix<T>, k: T): AMatrix<T> {
        val mc = x.mathCalculator
        return apply1(x, mc) {
            mc.multiply(k, it)
        }
    }

    internal fun <T> divide(x: Matrix<T>, k: T): AMatrix<T> {
        val mc = x.mathCalculator
        return apply1(x, mc) {
            mc.divide(k, it)
        }
    }


    /**
     *
     * @return a list of strictly increasing pivots of the column. The size of it is equal to the rank of the matrix.
     */
    internal fun <T> toUpperTriangle(M: MutableMatrix<T>,
                                     operations: MutableList<MatrixOperation<T>>? = null,
                                     column: Int = M.column): List<Int> {
        val mc = M.mathCalculator
        val row = M.row
        var i = 0
        val pivots = ArrayList<Int>(min(M.row, column))
        /*
        j = pivots[i] then M[i,j] is the first non-zero element in that row
         */
        for (j in 0 until column) {
            if (i >= row) {
                break
            }
            var f: T? = null
            for (i2 in i until row) {
                if (mc.isZero(M[i2, j])) {
                    continue
                }
                f = M[i2, j]
                if (i2 != i) {
                    M.swapRow(i2, i)
                    operations?.add(MatrixOperation.exchangeRow(i2, i))
                }
                break
            }
            if (f == null) {
                //not found
                continue
            }
            for (i2 in (i + 1) until row) {
                if (mc.isZero(M[i2, j])) {
                    continue
                }
                val k = mc.eval { -M[i2, j] / f }
                M[i2, j] = mc.zero
                M.multiplyAddRow(i, i2, k, j + 1)
                operations?.add(MatrixOperation.multiplyAddRow(i, i2, k))
            }
            pivots += j
            i++
        }
        return pivots
    }


    fun <T> rank(matrix: AbstractMatrix<T>): Int {
        val copy = copyOf(matrix)
        return toUpperTriangle(copy).size
    }


    internal fun <T> toEchelon(M: MutableMatrix<T>,
                               column: Int = M.column,
                               operations: MutableList<MatrixOperation<T>>? = null): List<Int> {
        val pivots = toUpperTriangle(M, operations, column)
        val mc = M.mathCalculator
        for (i in pivots.lastIndex downTo 0) {
            val j = pivots[i]
            if (!mc.isEqual(M[i, j], mc.one)) {
                M.divideRow(i, M[i, j], j + 1)
                M[i, j] = mc.one
                operations?.add(MatrixOperation.multiplyRow(i, mc.reciprocal(M[i, j])))
            }
            for (k in (i - 1) downTo 0) {
                val q = mc.eval { -M[k, j] }
                M.multiplyAddRow(i, k, q, j + 1)
                M[k, j] = mc.zero
                operations?.add(MatrixOperation.multiplyAddRow(i, k, q))
            }
        }
        return pivots
    }

    internal fun <T> nullSpaceOf(expanded: MutableMatrix<T>, column: Int, pivots: List<Int>): VectorBasis<T> {
        val r = pivots.size
        val dim = column
        val k = dim - r
        if (k == 0) {
            return VectorBasis.zeroBase(dim, expanded.mathCalculator)
        }
        val mc = expanded.mathCalculator
        val vectors = ArrayList<Vector<T>>(k)
        val negativeOne = mc.negate(mc.one)
        fun makeVector(j: Int) {
            val v = Vector.zero(dim, mc)
            v[j] = negativeOne
            for (i in pivots.indices) {
                v[pivots[i]] = expanded[i, j]
            }
            vectors += v
        }

        var l = 0
        for (j in 0 until pivots.last()) {
            if (j < pivots[l]) {
                makeVector(j)
            } else {
                l++
            }
        }
        for (j in (pivots.last() + 1) until column) {
            makeVector(j)
        }
        return VectorBasis.createBaseWithoutCheck(vectors)
    }

    fun <T> specialSolutionOf(expanded: MutableMatrix<T>, column: Int, pivots: List<Int>): Matrix<T> {
        val mc = expanded.mathCalculator
        val special = Matrix.zero(column, expanded.column - column, mc)
        for (k in pivots.indices) {
            val pk = pivots[k]
            for (j in special.colIndices) {
                special[pk, j] = expanded[k, j + column]
            }
        }
        return special
    }

    fun <T> solveLinear(expanded: MutableMatrix<T>, colSep: Int): Triple<Matrix<T>, VectorBasis<T>, Boolean> {
        val pivots = toEchelon(expanded, colSep, null)
        val r = pivots.size
        val mc = expanded.mathCalculator
        val special = specialSolutionOf(expanded, colSep, pivots)
        val basis = nullSpaceOf(expanded, colSep, pivots)
        val solvable = (r until expanded.row).all { i ->
            (colSep until expanded.column).all { j -> mc.isZero(expanded[i, j]) }
        }
        return Triple(special, basis, solvable)
    }

    fun <T> solveLinear(m: AbstractMatrix<T>, b: AbstractMatrix<T>): Triple<Matrix<T>, VectorBasis<T>, Boolean> {
        require(m.row == b.row)
        val expanded = Matrix.concatColumn(m, b)
        return solveLinear(expanded, m.column)
    }

    fun <T> solveLinear(m: AbstractMatrix<T>, b: AbstractVector<T>): Triple<Vector<T>, VectorBasis<T>, Boolean> {
        require(m.row == b.size)
        val expanded = AMatrix.zero(m.row, m.column + 1, m.mathCalculator)
        val col = m.column
        expanded.setAll(0, 0, m)
        for (i in b.indices) {
            expanded[i, col] = b[i]
        }
        val (special, basis, sol) = solveLinear(expanded, col)
        val v = special.getColumn(0)
        return Triple(v, basis, sol)
    }

    fun <T> solveHomo(m: AbstractMatrix<T>): VectorBasis<T> {
        val expanded = Matrix.copyOf(m)
        val pivots = toEchelon(expanded)
        return nullSpaceOf(expanded, m.column, pivots)
    }

    fun <T> inverse(m: AbstractMatrix<T>): Matrix<T> {
        require(m.isSquare())
        val n = m.row
        val mc = m.mathCalculator
        val expanded = AMatrix.zero(n, 2 * n, m.mathCalculator)
        expanded.setAll(0, 0, m)
        for (i in 0 until n) {
            expanded[i, i + n] = mc.one
        }
        val pivots = toEchelon(expanded, column = n)
        if (pivots.size != n) {
            ExceptionUtil.notInvertible()
        }
        return expanded.subMatrix(0, n, n, 2 * n)
    }

    fun <T> charMatrix(m: AbstractMatrix<T>, pc: MathCalculator<Polynomial<T>>): Matrix<Polynomial<T>> {
        val mc = m.mathCalculator
        m.requireSquare()
        val n = m.row
        val result = Matrix.zero(n, n, pc)
        for (i in 0 until n) {
            for (j in 0 until n) {
                result[i, j] = if (i == j) {
                    Polynomial.ofRoot(mc, m[i, j])
                } else {
                    Polynomial.constant(mc, mc.negate(m[i, j]))
                }
            }
        }
        return result
    }

    fun <T> decompQR(m: AbstractMatrix<T>): Pair<Matrix<T>, Matrix<T>> {
        m.requireSquare()
        val vs = m.columnVectors()
        val mc = m.mathCalculator
        val R = Matrix.zero(m.row, m.column, mc)
        val ws = ArrayList<MutableVector<T>>(m.row)
//        ws += Vector.copyOf(vs[0])
        for (i in 0 until m.row) {
            val u = Vector.copyOf(vs[i])
            for (j in 0 until i) {
                val k = u.inner(ws[j])
                u.addMultiplyAssign(mc.negate(k), ws[j])
                R[j, i] = k
            }
            if (!u.isZero()) {
                val length = u.norm()
                R[i, i] = length
                u.divAssign(length)
            }
            ws[i] = u
        }
        val Q = Matrix.fromVectors(ws)
        return Q to R
    }

    private fun <T> checkSymmetric(A: AbstractMatrix<T>) {
        A.requireSquare()
        val mc = A.mathCalculator
        for (i in 0 until A.row) {
            for (j in 0 until i) {
                require(mc.isEqual(A[i, j], A[j, i])) {
                    "Not symmetric!"
                }
            }
        }
    }

    fun <T> toCongDiagonalForm(A: AbstractMatrix<T>): Pair<Matrix<T>, Matrix<T>> {
        checkSymmetric(A)
        val n = A.row
        val mc = A.mathCalculator
        val x = AMatrix.zero(2 * n, n, A.mathCalculator)
        x.setAll(0, 0, A)
        val one = mc.one
        for (i in 0 until n) {
            x[i + n, i] = one
        }
        var pos = 0
        while (pos < n) {
            if (mc.isZero(x[pos, pos])) {
                var pi = -1
                var pj = -1
                SEARCH@ for (i in pos until n) {
                    for (j in pos..i) {
                        if (!mc.isZero(x[j, j])) {
                            pi = i
                            pj = j
                            break@SEARCH
                        }
                    }
                }
                if (pj < 0) {
                    break
                } else {
                    if (pj != pos) {
                        x.multiplyAddRow(pj, pos, one)
                        x.multiplyAddCol(pj, pos, one)
                    }
                    x.multiplyAddRow(pi, pos, one)
                    x.multiplyAddCol(pi, pos, one)
                }
            }
            for (i in pos + 1 until n) {
                if (mc.isZero(x[pos, i])) {
                    continue
                }
                val k = mc.negate(mc.divide(x[pos, i], x[pos, pos]))
                x.multiplyAddRow(pos, i, k)
                x.multiplyAddCol(pos, i, k)
            }
            pos++
        }
        val m1 = x.subMatrix(0, 0, n, n)
        val m2 = x.subMatrix(n, 0, 2 * n, n)
        return m1 to m2
    }


    fun <T> columnSpace(A: AbstractMatrix<T>): VectorBasis<T> {
        val matrix = A.toMutable()
        val pivots = toUpperTriangle(matrix)
        return VectorBasis.createBaseWithoutCheck(pivots.map { A.getColumn(it) })
    }
}

/**
 * Returns a mutable copy of this matrix.
 */
fun <T> AbstractMatrix<T>.toMutable(): MutableMatrix<T> = MatrixImpl.copyOf(this, mathCalculator)

open class MatrixCal<T>(val mc: MathCalculator<T>, val r: Int, val c: Int) : MathCalculatorAdapter<Matrix<T>>() {
    override val numberClass: Class<Matrix<T>>
        @Suppress("UNCHECKED_CAST")
        get() = Matrix::class.java as Class<Matrix<T>>
    override val zero: Matrix<T> = Matrix.zero(r, c, mc)


    override val characteristic: Long
        get() = throw UnsupportedCalculationException()

    override fun isZero(x: Matrix<T>): Boolean {
        return x.isZero()
    }

    override fun isEqual(x: Matrix<T>, y: Matrix<T>): Boolean {
        return x.valueEquals(y)
    }

    override fun add(x: Matrix<T>, y: Matrix<T>): Matrix<T> {
        return x.add(y)
    }

    override fun negate(x: Matrix<T>): Matrix<T> {
        return x.negate()
    }

    override fun subtract(x: Matrix<T>, y: Matrix<T>): Matrix<T> {
        return x.subtract(y)
    }

    override fun multiply(x: Matrix<T>, y: Matrix<T>): Matrix<T> {
        return x.multiply(y)
    }

    override fun divide(x: Matrix<T>, y: Matrix<T>): Matrix<T> {
        return x.multiply(y.inverse())
    }

    override fun multiplyLong(x: Matrix<T>, n: Long): Matrix<T> {
        return x.multiply(n)
    }

    override fun divideLong(x: Matrix<T>, n: Long): Matrix<T> {
        return x.applyAll { mc.divideLong(it, n) }
    }

    override fun reciprocal(x: Matrix<T>): Matrix<T> {
        return x.inverse()
    }
}

class SquareMatrixCal<T>(mc: MathCalculator<T>, n: Int) : MatrixCal<T>(mc, n, n) {
    override val one: Matrix<T> = Matrix.identity(r, mc)
}


fun main() {
    val m = AMatrix.of(3, 3, Fraction.calculator) { i, j ->
//        Fraction.of(i+j+0L)
        if (i == j && (i == 0 || i == 2)) {
            Fraction.ONE
        } else {
            Fraction.ZERO
        }

    }
    println(m)
    val (u, ops) = m.toUpperTriangleWay()
    println(u)
    println(ops)
}

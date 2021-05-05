package cn.ancono.math.geometry.analytic.space.transform

import cn.ancono.math.MathCalculator
import cn.ancono.math.algebra.abs.calculator.EqualPredicate
import cn.ancono.math.algebra.abs.calculator.FieldCalculator
import cn.ancono.math.algebra.abs.calculator.eval
import cn.ancono.math.algebra.linear.AbstractMatrix
import cn.ancono.math.algebra.linear.Matrix
import cn.ancono.math.function.Bijection
import cn.ancono.math.function.MathFunction
import cn.ancono.math.geometry.analytic.space.SPoint
import cn.ancono.math.geometry.analytic.space.SVector
import cn.ancono.math.numberModels.api.AlgebraModel
import cn.ancono.math.property.Composable
import cn.ancono.utilities.ArraySup
import org.jetbrains.annotations.NotNull
import java.util.function.Function

typealias SPointTrans<T> = MathFunction<SPoint<T>, SPoint<T>>


@Suppress("UNCHECKED_CAST")
class SRotateMatrix<T> internal constructor(override val calculator: FieldCalculator<T>, val mat: Array<Array<Any?>>) :
        AbstractMatrix<T>(calculator, 3, 3),
        Composable<SRotateMatrix<T>>,
        AlgebraModel<T, SRotateMatrix<T>> {



    init {
        require(mat.size == 3)
        require(mat[0].size == 3)
    }

    override fun isZero(): Boolean {
        return mat.all { row -> row.all { calculator.isZero(it as T) } }
    }

    override fun getChecked(i: Int, j: Int): T {
        return mat[i][j] as T
    }

    private inline fun mapTo0(f: (T) -> T): SRotateMatrix<T> {
        val nmat = Array(3) { i ->
            Array<Any?>(3) { j ->
                f(mat[i][j] as T)
            }
        }
        return SRotateMatrix(calculator, nmat)
    }

    private inline fun mapTo1(another: SRotateMatrix<T>, f: (T, T) -> T): SRotateMatrix<T> {
        val nmat = Array(3) { i ->
            Array<Any?>(3) { j ->
                f(mat[i][j] as T, another.mat[i][j] as T)
            }
        }
        return SRotateMatrix(calculator, nmat)
    }

    override fun get(i: Int, j: Int): T {
        return mat[i][j] as T
    }

    override fun getValues(): Array<Array<Any?>> {
        return ArraySup.deepCopy(mat)
    }

    override fun multiply(n: Long): SRotateMatrix<T> = mapTo0 { x ->
        calculator.multiplyLong(x, n)
    }

    override fun multiply(k: T): SRotateMatrix<T> = mapTo0 { x ->
        calculator.multiply(x, k)
    }

    override fun divide(k: T): SRotateMatrix<T> = mapTo0 { x ->
        calculator.divide(x, k)
    }

    override fun add(y: SRotateMatrix<T>): SRotateMatrix<T> = mapTo1(y) { a, b ->
        calculator.add(a, b)
    }

    override fun negate(): SRotateMatrix<T> = mapTo0 { calculator.negate(it) }

    override fun multiply(y: SRotateMatrix<T>): SRotateMatrix<T> {
        val mat1 = this.mat
        val mat2 = y.mat
        val mc = calculator
        val nmat = Array(3) { row ->
            Array<Any?>(3) { column ->
                //x * y

                var result: T = mc.zero
                for (i in 0..2) {
                    mc.eval { result += (mat1[row][i] as T) * (mat2[i][column] as T) }
                }
                result
            }
        }
        return SRotateMatrix(mc, nmat)
    }

    override fun transpose(): SRotateMatrix<T> {
        val nmat = Array(3) { i ->
            Array<Any?>(3) { j ->
                mat[j][i]
            }
        }
        return SRotateMatrix(calculator, nmat)
    }


    override fun <N> mapTo(newCalculator: EqualPredicate<N>, mapper: Function<T, N>): SRotateMatrix<N> {
        val nmat = Array(3) { i ->
            Array<Any?>(3) { j ->
                mapper.apply(mat[i][j] as T)
            }
        }
        return SRotateMatrix(newCalculator as FieldCalculator<N>, nmat)
    }

    override fun applyAll(f: (T) -> T): SRotateMatrix<T> = mapTo0 { f(it) }

    fun transform(p: SPoint<T>): SPoint<T> {
        val mat: Array<Array<T>> = this.mat as Array<Array<T>>
        return calculator.eval {
            val x = mat[0][0] * p.x + mat[0][1] * p.y + mat[0][2] * p.z
            val y = mat[1][0] * p.x + mat[1][1] * p.y + mat[1][2] * p.z
            val z = mat[2][0] * p.x + mat[2][1] * p.y + mat[2][2] * p.z
            SPoint.valueOf(x, y, z, this as MathCalculator<T>)  //TODO
        }
    }

    fun transform(v: SVector<T>): SVector<T> {
        val mc = calculator as MathCalculator<T> //TODO
        val arr = Array<Any?>(3) { i ->
            var re = mc.zero
            for (j in 0..2) {
                mc.eval {
                    re += (mat[i][j] as T) * v[j]
                }
            }
            re
        } as Array<T>
        return SVector.valueOf(arr[0], arr[1], arr[2], mc)
    }

    override fun compose(before: SRotateMatrix<T>): SRotateMatrix<T> {
        return this.multiply(before)
    }

    override fun andThen(after: SRotateMatrix<T>): SRotateMatrix<T> {
        return after.multiply(this)
    }

    private var inversed: SRotateMatrix<T>? = null

    override fun inverse(): SRotateMatrix<T> {
        if (inversed == null) {
            inversed = SRotateMatrix(calculator, super.inverse().getValues())
            inversed?.inversed = this
        }
        return inversed!!
    }

    fun toFunction(): SPointTrans<T> = SPointTrans(this::transform)

    fun toBijection(): Bijection<SPoint<T>, SPoint<T>> {
        val inversed = this.inverse()
        return object : Bijection<SPoint<T>, SPoint<T>> {
            override fun apply(x: SPoint<T>): SPoint<T> {
                return this@SRotateMatrix.transform(x)
            }

            override fun deply(y: @NotNull SPoint<T>): SPoint<T> {
                return inversed.transform(y)
            }

        }
    }


    companion object {

        fun <T> valueOf(mat: Matrix<T>): SRotateMatrix<T> {
            require(mat.row == 3)
            require(mat.column == 3)
            return SRotateMatrix(mat.calculator as FieldCalculator<T>, mat.getValues())
        }

        fun <T> multiplyBy(kx: T, ky: T, kz: T, mc: MathCalculator<T>): SRotateMatrix<T> {
            val zero = mc.zero
            val mat = arrayOf(
                    arrayOf<Any?>(kx, zero, zero),
                    arrayOf<Any?>(zero, ky, zero),
                    arrayOf<Any?>(zero, zero, kz))
            return SRotateMatrix(mc, mat)
        }

        fun <T> multiplyBy(k: T, mc: MathCalculator<T>): SRotateMatrix<T> {
            return multiplyBy(k, k, k, mc)
        }

        fun <T> identity(mc: MathCalculator<T>): SRotateMatrix<T> {
            return multiplyBy(mc.one, mc)
        }

        fun <T> valueOf(mat: List<List<T>>, mc: MathCalculator<T>): SRotateMatrix<T> {
            val nMat = Array(3) { i ->
                Array<Any?>(3) { j ->
                    mat[i][j]
                }
            }
            return SRotateMatrix(mc, nMat)
        }

    }


}
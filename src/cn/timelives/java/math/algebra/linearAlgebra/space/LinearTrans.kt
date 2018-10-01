package cn.timelives.java.math.algebra.linearAlgebra.space

import cn.timelives.java.math.MathCalculator
import cn.timelives.java.math.MathObject
import cn.timelives.java.math.MathObjectExtend
import cn.timelives.java.math.algebra.linearAlgebra.Matrix
import cn.timelives.java.math.algebra.linearAlgebra.Vector
import cn.timelives.java.math.numberModels.api.FlexibleNumberFormatter
import cn.timelives.java.math.plus
import java.util.function.Function

/**
 * Describes linear transformation on n-dimension Euclid space. The transformation
 * applies [transMatrix] first and then applies [translationVector].
 */
interface ILinearTrans<T : Any> {
    /**
     * The dimension of this linear transformation, that is, the size of
     * vector that [transform] accepts.
     */
    val dimension: Int

    /**
     * Performs the transformation on the given vector.
     */
    fun transform(v: Vector<T>): Vector<T>

    /**
     * The transformation matrix of this linear transformation, an invertible matrix
     * whose size is [dimension]
     */
    val transMatrix: Matrix<T>

    /**
     * The translation vector of this linear transformation, a vector whose size
     * is [dimension]
     */
    val translationVector: Vector<T>

    val expandedMatrix: Matrix<T>
        get() {
            val tm = transMatrix
            val mc = tm.mathCalculator
            return Matrix.getBuilder(dimension + 1, dimension + 1, tm.mathCalculator)
                    .fillArea(0, 0, tm)
                    .fillColumnVector(dimension, 0, translationVector)
                    .fillRow(mc.zero, dimension)
                    .set(mc.one, dimension, dimension)
                    .build()

        }

    val det: T
        get() = transMatrix.calDet()
}

abstract class LinearTrans<T : Any>(mc: MathCalculator<T>, override val dimension: Int) : MathObjectExtend<T>(mc), ILinearTrans<T> {

    override fun transform(v: Vector<T>): Vector<T> {
        return Vector.multiplyToVector(transMatrix, v) + translationVector
    }

    override fun toString(nf: FlexibleNumberFormatter<T, MathCalculator<T>>): String = buildString {
        appendln("Linear transformation, expanded matrix = ")
        appendln(expandedMatrix.contentToString(nf))
    }

    override fun valueEquals(obj: MathObject<T>): Boolean {
        if (obj !is LinearTrans) {
            return false
        }
        return dimension == obj.dimension && transMatrix.valueEquals(obj.transMatrix)
                && translationVector.valueEquals(obj.translationVector)
    }

    override fun <N : Any> valueEquals(obj: MathObject<N>, mapper: Function<N, T>): Boolean {
        if (obj !is LinearTrans) {
            return false
        }
        return dimension == obj.dimension && transMatrix.valueEquals(obj.transMatrix, mapper)
                && translationVector.valueEquals(obj.translationVector, mapper)
    }

    override fun <N : Any> mapTo(mapper: Function<T, N>, newCalculator: MathCalculator<N>): LinearTrans<N> {
        return DLinearTrans(newCalculator, dimension,
                transMatrix.mapTo(mapper, newCalculator),
                translationVector.mapTo(mapper, newCalculator))
    }

}


internal class DLinearTrans<T : Any>(mc: MathCalculator<T>, dimension: Int,
                                     override val transMatrix: Matrix<T>, override val translationVector: Vector<T>) : LinearTrans<T>(mc, dimension) {


    init {
        require(dimension == transMatrix.rowCount && dimension == transMatrix.columnCount)
        require(dimension == translationVector.size)
    }

    override val expandedMatrix : Matrix<T> by lazy { super.expandedMatrix }

}

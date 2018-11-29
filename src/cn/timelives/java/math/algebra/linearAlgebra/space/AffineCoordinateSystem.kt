package cn.timelives.java.math.algebra.linearAlgebra.space

import cn.timelives.java.math.MathCalculator
import cn.timelives.java.math.algebra.linearAlgebra.FullVectorBase
import cn.timelives.java.math.algebra.linearAlgebra.IFullVectorBase
import cn.timelives.java.math.algebra.linearAlgebra.Vector
import cn.timelives.java.math.algebra.linearAlgebra.VectorBase
import cn.timelives.java.math.plus
import cn.timelives.java.math.property.Composable
import java.util.function.Function

/**
 * Describes an affine coordinate system in n-dimension Euclid space.
 */
interface IAffineCoordinateSystem<T : Any> : ILinearSpace<T> {
    /**
     * The [standardDimension] is equal to [dimension].
     */
    override val standardDimension: Int
        get() = dimension
    override val vectorBase: IFullVectorBase<T>
    /**
     * Any vector whose size is equal to [dimension] is contained.
     */
    override fun contains(v: Vector<T>): Boolean = v.size == dimension

}

abstract class AffineCoordinateSystem<T : Any>(mc: MathCalculator<T>, final override val dimension: Int,
                                               originVector: Vector<T>,
                                               vectorBase: FullVectorBase<T>)
    : LinearSpace<T>(mc, originVector, vectorBase), IAffineCoordinateSystem<T>, Composable<AffineCoordinateSystem<T>> {

    init {
        require(dimension == originVector.size)
        require(dimension == vectorBase.rank)
    }

    @Suppress("CanBePrimaryConstructorProperty")
    override val vectorBase: FullVectorBase<T> = vectorBase


    override val standardDimension: Int
        get() = dimension


    override fun contains(v: Vector<T>): Boolean {
        return super<IAffineCoordinateSystem>.contains(v)
    }

    /**
     * Equal to `before.andThen(this)`.
     * @see andThen
     */
    override fun compose(before: AffineCoordinateSystem<T>): AffineCoordinateSystem<T> {
        return before.andThen(this)
    }

    /**
     * Assume [after] is a local coordinate system based on `this`, returns a new coordinate system
     * representing [after] based on the standard coordinate system.
     */
    override fun andThen(after: AffineCoordinateSystem<T>): AffineCoordinateSystem<T> {
        val nOriginVector = originVector + toStandardCord(after.originVector)
        val nVectorBase = vectorBase.andThenFull(after.vectorBase)
        return DAffineCoordinateSystem(mc, dimension, nOriginVector, nVectorBase)
    }

    //TODO
//    /**
//     * Returns the linear transformation that transforms this to another affine coordinate system,
//     * it is required that both coordinate systems have the same dimension.
//     */
//    fun transformationTo(affineCoordinateSystem: AffineCoordinateSystem<T>): AffineTrans<T> {
//        require(dimension == affineCoordinateSystem.dimension)
//        val translation = affineCoordinateSystem.originVector - originVector
//        val transMatrix = vectorBase.transMatrix(affineCoordinateSystem.vectorBase)
//        return DLinearTrans(mc, dimension, transMatrix, translation)
//    }

    companion object {
        /**
         * Creates a new linear space from the given origin vector and vector base.
         */
        fun <T : Any> valueOf(originVector: Vector<T>, vectorBase: FullVectorBase<T>): AffineCoordinateSystem<T> {
            require(originVector.size == vectorBase.vectorDimension)
            val dimension = vectorBase.vectorDimension
            val mc = originVector.mathCalculator
            return DAffineCoordinateSystem(mc, dimension, originVector, vectorBase)
        }

        /**
         * Creates a new linear space from the given origin vector and base vectors.
         */
        fun <T : Any> valueOf(originVector: Vector<T>, vararg baseVectors: Vector<T>): AffineCoordinateSystem<T> = valueOf(originVector, VectorBase.createFullBase(*baseVectors))
    }
}

///**
// * Returns the linear transformation that transforms the standard coordinate system to
// * this affine coordinate system.
// */
//fun <T:Any> IAffineCoordinateSystem<T>.toLinearTrans(mathCalculator: MathCalculator<T>): LinearTrans<T> {
//    return DLinearTrans(mathCalculator, dimension, vectorBase.getVectorsAsMatrix(), originVector)
//}

///**
// * Returns the linear transformation that transforms the standard coordinate system to
// * this affine coordinate system.
// */
//fun <T:Any> AffineCoordinateSystem<T>.toLinearTrans(): LinearTrans<T> {
//    return DLinearTrans(mathCalculator, dimension, vectorBase.getVectorsAsMatrix(), originVector)
//}

internal class DAffineCoordinateSystem<T : Any>(mc: MathCalculator<T>, dimension: Int, originVector: Vector<T>, vectorBase: FullVectorBase<T>) :
        AffineCoordinateSystem<T>(mc, dimension, originVector, vectorBase) {
    override fun <N : Any> mapTo(mapper: Function<T, N>, newCalculator: MathCalculator<N>): LinearSpace<N> {
        return DAffineCoordinateSystem(newCalculator, dimension, originVector.mapTo(mapper, newCalculator), vectorBase.mapTo(mapper, newCalculator))
    }

}

/**
 * Describes the standard coordinate system.
 */
class StandardCoordinateSystem<T : Any>(mc: MathCalculator<T>, dimension: Int) : AffineCoordinateSystem<T>(mc, dimension,
        Vector.zeroVector(dimension, mc), VectorBase.standardBase(dimension, mc)) {
    init {
        require(dimension > 0)
    }

    override fun <N : Any> mapTo(mapper: Function<T, N>, newCalculator: MathCalculator<N>): StandardCoordinateSystem<N> {
        return StandardCoordinateSystem(newCalculator, dimension)
    }

    override fun andThen(after: AffineCoordinateSystem<T>): AffineCoordinateSystem<T> {
        require(after.dimension == this.dimension)
        return after
    }

    override fun compose(before: AffineCoordinateSystem<T>): AffineCoordinateSystem<T> = before

    override fun fromStandardCord(v: Vector<T>): Vector<T> = v

    override fun toStandardCord(v: Vector<T>): Vector<T> = v

//    override fun transformationTo(affineCoordinateSystem: IAffineCoordinateSystem<T>): LinearTrans<T> {
//        return affineCoordinateSystem.toLinearTrans(mathCalculator)
//    }

}
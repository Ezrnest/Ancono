package cn.ancono.math.algebra.linear.space

import cn.ancono.math.MathCalculator
import cn.ancono.math.algebra.linear.FullVectorBasis
import cn.ancono.math.algebra.linear.IFullVectorBasis
import cn.ancono.math.algebra.linear.Vector
import cn.ancono.math.algebra.linear.VectorBasis
import cn.ancono.math.numberModels.api.plus
import cn.ancono.math.property.Composable
import java.util.function.Function

/**
 * Describes an affine coordinate system in n-dimension Euclid space.
 */
interface IAffineCoordinateSystem<T> : ILinearSpace<T> {
    /**
     * The [standardDimension] is equal to [dimension].
     */
    override val standardDimension: Int
        get() = dimension
    override val vectorBasis: IFullVectorBasis<T>

    /**
     * Any vector whose size is equal to [dimension] is contained.
     */
    override fun contains(v: Vector<T>): Boolean = v.size == dimension

}

abstract class AffineCoordinateSystem<T>(mc: MathCalculator<T>, final override val dimension: Int,
                                         originVector: Vector<T>,
                                         vectorBase: FullVectorBasis<T>)
    : AffineSpace<T>(mc, originVector, vectorBase), IAffineCoordinateSystem<T>, Composable<AffineCoordinateSystem<T>> {

    init {
        require(dimension == originVector.size)
        require(dimension == vectorBase.rank)
    }

    @Suppress("CanBePrimaryConstructorProperty")
    override val vectorBasis: FullVectorBasis<T> = vectorBase


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
        val nVectorBase = vectorBasis.andThenFull(after.vectorBasis)
        return DAffineCoordinateSystem(mc, dimension, nOriginVector, nVectorBase)
    }

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
        fun <T> valueOf(originVector: Vector<T>, vectorBase: FullVectorBasis<T>): AffineCoordinateSystem<T> {
            require(originVector.size == vectorBase.vectorLength)
            val dimension = vectorBase.vectorLength
            val mc = originVector.mathCalculator
            return DAffineCoordinateSystem(mc, dimension, originVector, vectorBase)
        }

        /**
         * Creates a new linear space from the given origin vector and base vectors.
         */
        fun <T> valueOf(originVector: Vector<T>, vararg baseVectors: Vector<T>): AffineCoordinateSystem<T> = valueOf(originVector, VectorBasis.createFullBase(*baseVectors))
    }
}

///**
// * Returns the linear transformation that transforms the standard coordinate system to
// * this affine coordinate system.
// */
//fun <T> IAffineCoordinateSystem<T>.toLinearTrans(mathCalculator: MathCalculator<T>): LinearTrans<T> {
//    return DLinearTrans(mathCalculator, dimension, vectorBase.getVectorsAsMatrix(), originVector)
//}

///**
// * Returns the linear transformation that transforms the standard coordinate system to
// * this affine coordinate system.
// */
//fun <T> AffineCoordinateSystem<T>.toLinearTrans(): LinearTrans<T> {
//    return DLinearTrans(mathCalculator, dimension, vectorBase.getVectorsAsMatrix(), originVector)
//}

internal class DAffineCoordinateSystem<T>(mc: MathCalculator<T>, dimension: Int, originVector: Vector<T>, vectorBase: FullVectorBasis<T>) :
        AffineCoordinateSystem<T>(mc, dimension, originVector, vectorBase) {
    override fun <N> mapTo(newCalculator: MathCalculator<N>, mapper: Function<T, N>): AffineSpace<N> {
        return DAffineCoordinateSystem(
                newCalculator, dimension, originVector.mapTo(newCalculator, mapper), vectorBasis.mapTo(
                newCalculator,
                mapper
        )
        )
    }

}

/**
 * Describes the standard coordinate system.
 */
class StandardCoordinateSystem<T>(mc: MathCalculator<T>, dimension: Int) : AffineCoordinateSystem<T>(mc, dimension,
        Vector.zero(dimension, mc), VectorBasis.standardBase(dimension, mc)) {
    init {
        require(dimension > 0)
    }

    override fun <N> mapTo(
            newCalculator: MathCalculator<N>,
            mapper: Function<T, N>
    ): StandardCoordinateSystem<N> {
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
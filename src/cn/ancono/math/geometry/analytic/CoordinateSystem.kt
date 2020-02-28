package cn.ancono.math.geometry.analytic

import cn.ancono.math.MathCalculator
import cn.ancono.math.MathObjectExtend
import cn.ancono.math.algebra.linearAlgebra.Vector
import cn.ancono.math.set.MathSet

/**
 * Describes a local coordinate system based in n-dimension Euclid space, where n is equal to [standardDimension].
 * The [dimension] of the coordinate system
 * is the size of valid coordinate vectors. Any coordinate system must be able to
 * convert a local coordinate vector to a general coordinate vector in the standard coordinate system,
 * which is a linear orthogonal unit coordinate system. The coordinate system also serves as
 * a [MathSet] containing vectors.
 */
interface CoordinateSystem<T : Any> : MathSet<Vector<T>> {
    /**
     * The dimension of this coordinate system, which is the size of coordinate vector required.
     */
    val dimension: Int

    /**
     * The dimension of the standard coordinate system that this coordinate is based on, which
     * is the size of vectors returned by [toStandardCord].
     */
    val standardDimension: Int

    /**
     * Determines whether the vector represents a valid coordinate in this coordinate system.
     */
    fun isValidCord(v: Vector<T>): Boolean

    /**
     * Determines whether the vector representing a coordinate in the standard coordinate system
     * is contained in this coordinate system.
     */
    override fun contains(v: Vector<T>): Boolean

    /**
     * Converts a vector [v] representing a coordinate in this coordinate system to
     * the standard coordinate system.
     * @param v a vector whose size is equal to [dimension]
     * @throws ArithmeticException if [v] is not a valid coordinate vector
     */
    fun toStandardCord(v: Vector<T>): Vector<T>

    /**
     * Converts a vector [v] representing a coordinate in the standard coordinate system to
     * coordinate in this coordinate system.
     * @param v a vector whose size is equal to [standardDimension]
     * @throws ArithmeticException if [v] is not contained in this coordinate system.
     */
    fun fromStandardCord(v: Vector<T>): Vector<T>

    fun toAnotherCord(v: Vector<T>, another: CoordinateSystem<T>): Vector<T> {
        val standard = toStandardCord(v)
        return another.fromStandardCord(standard)
    }

    fun fromAnotherCord(v: Vector<T>, another: CoordinateSystem<T>): Vector<T> = another.toAnotherCord(v, this)

}

@Suppress("RedundantOverride")
abstract class AbstractCoordinateSystem<T : Any>(mc: MathCalculator<T>) : MathObjectExtend<T>(mc), CoordinateSystem<T> {
    override fun toAnotherCord(v: Vector<T>, another: CoordinateSystem<T>): Vector<T> {
        return super.toAnotherCord(v, another)
    }
}
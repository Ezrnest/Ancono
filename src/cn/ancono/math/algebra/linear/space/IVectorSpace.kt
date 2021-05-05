package cn.ancono.math.algebra.linear.space

import cn.ancono.math.algebra.abs.calculator.FieldCalculator
import cn.ancono.math.algebra.abs.calculator.LinearSpaceCalculator
import cn.ancono.math.algebra.abs.structure.FiniteLinearSpace
import cn.ancono.math.algebra.linear.Vector


/*
 * Created by liyicheng at 2020-03-06 15:17
 */
/**
 * Describe a linear space over vector. The vectors in this vector space are all column vectors.
 *
 * @author liyicheng
 */
interface IVectorSpace<T> : FiniteLinearSpace<T, Vector<T>> {

//    override fun getSet(): MathSet<Vector<T>> {
//        return basis
//    }

    /**
     * Returns the length of the vector in this vector space.
     */
    val vectorLength: Int

    override fun getAbelCal(): VectorSpaceCalculator<T>
}


interface VectorSpaceCalculator<T> : LinearSpaceCalculator<T, Vector<T>> {

    /**
     * Returns the length of the vector in this vector space.
     */
    val vectorLength: Int

    @JvmDefault
    override fun isLinearDependent(u: Vector<T>, v: Vector<T>): Boolean {
        return u.isParallel(v)
    }

    @JvmDefault
    override fun isLinearDependent(vs: List<Vector<T>>): Boolean {
        return Vector.isLinearDependent(vs)
    }

    @JvmDefault
    override fun scalarMultiply(k: T, v: Vector<T>): Vector<T> {
        return v.multiply(k)
    }

    @JvmDefault
    override fun add(x: Vector<T>, y: Vector<T>): Vector<T> {
        return x + y
    }

    @JvmDefault
    override fun negate(x: Vector<T>): Vector<T> {
        return -x
    }

    @JvmDefault
    override fun isEqual(x: Vector<T>, y: Vector<T>): Boolean {
        return x.valueEquals(y)
    }


}

class VectorSpaceCalculatorImpl<T>(override val vectorLength: Int, private val mc: FieldCalculator<T>) : VectorSpaceCalculator<T> {
    override val scalarCalculator: FieldCalculator<T>
        get() = mc
    override val zero: Vector<T>
        get() = Vector.zero(vectorLength, mc)
}
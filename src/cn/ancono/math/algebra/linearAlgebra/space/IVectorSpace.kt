package cn.ancono.math.algebra.linearAlgebra.space

import cn.ancono.math.MathCalculator
import cn.ancono.math.algebra.abstractAlgebra.calculator.FieldCalculator
import cn.ancono.math.algebra.abstractAlgebra.calculator.LinearSpaceCalculator
import cn.ancono.math.algebra.abstractAlgebra.structure.FiniteLinearSpace
import cn.ancono.math.algebra.linearAlgebra.Vector


/*
 * Created by liyicheng at 2020-03-06 15:17
 */
/**
 * Describe a linear space over vector. The vectors in this vector space are all column vectors.
 * @author liyicheng
 */
interface IVectorSpace<T : Any> : FiniteLinearSpace<T, Vector<T>> {

//    override fun getSet(): MathSet<Vector<T>> {
//        return basis
//    }

    /**
     * Returns the length of the vector in this vector space.
     */
    val vectorLength: Int

    override fun getCalculator(): VectorSpaceCalculator<T>
}



interface VectorSpaceCalculator<T : Any> : LinearSpaceCalculator<T, Vector<T>> {

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
        return Vector.isLinearRelevant(vs)
    }

    @JvmDefault
    override fun scalarMultiply(k: T, v: Vector<T>): Vector<T> {
        return v.multiplyNumber(k)
    }

    @JvmDefault
    override fun apply(x: Vector<T>, y: Vector<T>): Vector<T> {
        return x + y
    }

    @JvmDefault
    override fun inverse(x: Vector<T>): Vector<T> {
        return -x
    }

    @JvmDefault
    override fun isEqual(x: Vector<T>, y: Vector<T>): Boolean {
        return x.valueEquals(y)
    }


}

class VectorSpaceCalculatorImpl<T : Any>(override val vectorLength: Int, private val mc: MathCalculator<T>) : VectorSpaceCalculator<T> {
    override val scalarCalculator: FieldCalculator<T>
        get() = mc
    override val identity: Vector<T>
        get() = Vector.zeroVector(vectorLength, mc)
}
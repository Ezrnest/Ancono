package cn.ancono.math.algebra.linearAlgebra.space

import cn.ancono.math.algebra.abstractAlgebra.calculator.LinearSpaceCalculator
import cn.ancono.math.algebra.abstractAlgebra.structure.LinearSpace
import cn.ancono.math.algebra.linearAlgebra.Vector
import cn.ancono.math.algebra.linearAlgebra.VectorBase
import cn.ancono.math.set.MathSet


/*
 * Created by liyicheng at 2020-03-06 15:17
 */
/**
 * Describe a linear space over vector.
 * @author liyicheng
 */
interface IVectorSpace<T : Any> : LinearSpace<T, Vector<T>> {

    override fun getSet(): MathSet<Vector<T>> {
        return MathSet { v ->
            val list = basis + v
            Vector.isLinearRelevant(list)
        }
    }

    override fun getCalculator(): VectorSpaceCalculator<T>
}

interface VectorSpaceCalculator<T : Any> : LinearSpaceCalculator<T, Vector<T>> {
    @JvmDefault
    override fun isLinearRelevant(u: Vector<T>, v: Vector<T>): Boolean {
        return u.isParallel(v)
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
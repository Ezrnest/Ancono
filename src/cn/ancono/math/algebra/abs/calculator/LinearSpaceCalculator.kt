package cn.ancono.math.algebra.abs.calculator

/*
 * Created by liyicheng at 2020-03-07 10:42
 */
/**
 *
 *
 * @author  liyicheng Created at 2018/11/29 16:57
 * @see cn.ancono.math.algebra.abs.structure.LinearSpace
 */
interface LinearSpaceCalculator<K, V> : ModuleCalculator<K, V> {
    override val scalarCalculator: FieldCalculator<K>

    /**
     * Determines whether the two vectors are linear dependent.
     */
    fun isLinearDependent(u: V, v: V): Boolean {
        throw UnsupportedOperationException()
    }


    /**
     * Determines whether the given vectors are linear dependent.
     */
    fun isLinearDependent(vs: List<V>): Boolean {
        throw UnsupportedOperationException()
    }


    fun scalarDivide(x: V, k: K): V {
        return scalarMultiply(scalarCalculator.reciprocal(k), x)
    }

}
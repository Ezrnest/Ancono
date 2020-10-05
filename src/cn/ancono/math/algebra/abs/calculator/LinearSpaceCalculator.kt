package cn.ancono.math.algebra.abs.calculator

/*
 * Created by liyicheng at 2020-03-07 10:42
 */ /**
 * Created at 2018/11/29 16:57
 * @author  liyicheng
 * @see cn.ancono.math.algebra.abs.structure.LinearSpace
 */
interface LinearSpaceCalculator<K : Any, V : Any> : ModuleCalculator<K, V> {
    override val scalarCalculator: FieldCalculator<K>

    /**
     * Determines whether the two vectors are linear dependent.
     */
    @JvmDefault
    fun isLinearDependent(u: V, v: V): Boolean {
        throw UnsupportedOperationException()
    }


    /**
     * Determines whether the given vectors are linear dependent.
     */
    @JvmDefault
    fun isLinearDependent(vs: List<V>): Boolean {
        throw UnsupportedOperationException()
    }
}
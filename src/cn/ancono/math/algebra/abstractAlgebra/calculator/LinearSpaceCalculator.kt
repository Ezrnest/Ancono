package cn.ancono.math.algebra.abstractAlgebra.calculator

/*
 * Created by liyicheng at 2020-03-07 10:42
 */ /**
 * Created at 2018/11/29 16:57
 * @author  liyicheng
 * @see cn.ancono.math.algebra.abstractAlgebra.structure.LinearSpace
 */
interface LinearSpaceCalculator<K : Any, V : Any> : ModuleCalculator<K, V> {
    override val scalarCalculator: FieldCalculator<K>

    /**
     * Determines whether the two vectors are linear relevant.
     */
    @JvmDefault
    fun isLinearRelevant(u: V, v: V): Boolean {
        throw UnsupportedOperationException()
    }
}
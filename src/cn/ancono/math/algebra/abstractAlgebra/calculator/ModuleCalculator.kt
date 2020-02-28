package cn.ancono.math.algebra.abstractAlgebra.calculator


/**
 * Describes the calculator for a module.
 * Created at 2018/9/20 19:31
 * @author  liyicheng
 * @see Module(cn.ancono.math.algebra.abstractAlgebra.structure.Module)
 */
interface ModuleCalculator<R : Any, V : Any> : GroupCalculator<V> {

    /**
     * Performs the multiplication operation( [R]*[V] -> [V])
     */
    fun scalarMultiply(k: R, v: V): V

    /**
     * The ring calculator for the scalar.
     */
    val scalarCalculator: RingCalculator<R>

    /**
     * Performs the addiction operation on [V]
     */
    override fun apply(x: V, y: V): V

    fun rAdd(r1: R, r2: R): R = scalarCalculator.add(r1, r2)

    fun rSubtract(r1: R, r2: R): R = scalarCalculator.subtract(r1, r2)

    fun rNegate(r: R): R = scalarCalculator.negate(r)

    fun rMultiply(r1: R, r2: R): R = scalarCalculator.multiply(r1, r2)

    val rZero: R
        get() = scalarCalculator.zero

}
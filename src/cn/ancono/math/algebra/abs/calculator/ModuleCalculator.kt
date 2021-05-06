package cn.ancono.math.algebra.abs.calculator

/*
 * Created by liyicheng at 2020-03-07 10:35
 */
/**
 * Describes the calculator for a module.
 * Created at 2018/9/20 19:31
 * @author  liyicheng
 * @see cn.ancono.math.algebra.abs.structure.Module
 */
interface ModuleCalculator<R, V> : AbelGroupCal<V> {
    /**
     * Performs the multiplication operation( [R]*[V] -> [V])
     */
    fun scalarMultiply(k: R, v: V): V

    /**
     * The ring calculator for the scalar.
     */
    val scalarCalculator: RingCalculator<R>

//    /**
//     * Performs the addiction operation on [V]
//     */
//    @JvmDefault
//    override fun apply(x: V, y: V): V

    /**
     * Performs the addition operation on [V]
     */
    override fun add(x: V, y: V): V

    fun rAdd(r1: R, r2: R): R {
        return scalarCalculator.add(r1, r2)
    }

    fun rSubtract(r1: R, r2: R): R {
        return scalarCalculator.subtract(r1, r2)
    }

    fun rNegate(r: R): R {
        return scalarCalculator.negate(r)
    }

    fun rMultiply(r1: R, r2: R): R {
        return scalarCalculator.multiply(r1, r2)
    }

    val rZero: R
        get() = scalarCalculator.zero
}
package cn.ancono.math.algebra.linear

import cn.ancono.math.CalculatorHolder
import cn.ancono.math.algebra.abs.calculator.FieldCalculator
import cn.ancono.math.algebra.abs.calculator.LinearSpaceCalculator
import cn.ancono.math.algebra.abs.calculator.eval
import cn.ancono.math.algebra.abs.structure.LinearSpace
import cn.ancono.math.function.MathFunction
import cn.ancono.math.function.SVFunction
import cn.ancono.math.function.invoke
import cn.ancono.math.numberModels.api.VectorModel


/**
 * An interface describing linear mapping on field(of type) **K**. Assuming there are two vector spaces(of type)
 * **V**, **U** over **K**, a linear mapping is a mapping between **V** and **U** which satisfies the following rules:
 * 1. For any elements `u`, `v` in **V** we have
 * >f(u+v) = f(u) + f(v)
 * 2. For any `c` in `K` and any `v` in **V** we have
 * >f(cv) = cf(v)
 *
 *
 *
 * Note:
 *
 * The mathematical rules for the linear mapping cannot be verified simply through this interface and
 * [K] is actually a marker type parameter for the mapping.
 */
interface ILinearMapping<K : Any, V : Any, U : Any> : MathFunction<V, U> {
    /**
     * Applies the linear mapping to [x], a vector in the vector space **V**, and returns a vector
     * in the vector space **U**.
     */
    override fun apply(x: V): U
}

//fun <K : Any,W:Any, V : Any, U : Any> ILinearMapping<K,V,U>.compose(mapping: ILinearMapping<K,W,V>):ILinearMapping<K,W,U>{
//
//}

/**
 * Describes the linear transformation on the linear space **V** over field **K**.
 */
interface ILinearTrans<K : Any, V : Any> : ILinearMapping<K, V, V>, SVFunction<V>


/**
 * Describes a linear mapping. Assuming there are two vector spaces **V**, **U** over **K**,
 * a linear mapping is a mapping between **V** and **U** which satisfies the following rules:
 * 1. For any elements `u`, `v` in **V** we have
 * >f(u+v) = f(u) + f(v)
 * 2. For any `c` in `K` and any `v` in **V** we have
 * >f(cv) = cf(v)
 *
 *
 *
 * Created at 2018/10/8 17:41
 * @author  liyicheng
 */
abstract class ALinearMapping<K : Any, V : Any, U : Any>
    : ILinearMapping<K, V, U>, CalculatorHolder<K, FieldCalculator<K>>,
        VectorModel<K, ALinearMapping<K, V, U>> {
    /**
     * Returns the math calculator for field `K`
     */
    override val mathCalculator: FieldCalculator<K>
        get() = vectorSpace.scalars.calculator

    val linearCalculator: LinearSpaceCalculator<K, U>
        get() = vectorSpace.calculator

    /**
     * The vector space of [U].
     */
    abstract val vectorSpace: LinearSpace<K, U>


    /**
     * Applies the mapping.
     */
    abstract override fun apply(x: V): U

    protected fun mappingOf(m: (V) -> U) = DALinearMapping(vectorSpace, m)

    override fun add(y: ALinearMapping<K, V, U>): ALinearMapping<K, V, U> {
        val f = this
        @Suppress("UnnecessaryVariable")//To make the following code more clear
        val g = y
        return mappingOf { x ->
            linearCalculator.eval {
                f(x) + g(x)
            }

        }
    }

    override fun negate(): ALinearMapping<K, V, U> {
        val f = this
        return mappingOf { x ->
            linearCalculator.eval {
                -f(x)
            }
        }
    }

    override fun multiply(k: K): ALinearMapping<K, V, U> {
        val f = this
        return mappingOf { x ->
            linearCalculator.scalarMultiply(k, f(x))
        }
    }


    open fun <W : Any> composeLinear(g: ALinearMapping<K, U, W>): ALinearMapping<K, V, W> {
        val f = this
        return DALinearMapping(g.vectorSpace) { x ->
            g(f(x))
        }
    }


}

abstract class KALinearTrans<K : Any, V : Any>(override val vectorSpace: LinearSpace<K, V>) : ALinearMapping<K, V, V>(), SVFunction<V> {

}

//
//class IdentityLinearMapping<K:Any,V:Any,U:Any>(override val vectorSpace: VectorSpace<K, U>) : LinearMapping<K,,V,U>(){
//    override fun apply(x: V): U = x
//
//    override fun composeLinear(g: LinearMapping<K, V,U>): LinearMapping<K, V,U> {
//        return g
//    }
//}

class ZeroALinearMapping<K : Any, V : Any, U : Any>(override val vectorSpace: LinearSpace<K, U>) : ALinearMapping<K, V, U>() {
    override fun apply(x: V): U = vectorSpace.identity()
}

class DALinearMapping<K : Any, V : Any, U : Any>(override val vectorSpace: LinearSpace<K, U>, private val f: (V) -> U)
    : ALinearMapping<K, V, U>() {
    override fun apply(x: V): U {
        return f(x)
    }
}

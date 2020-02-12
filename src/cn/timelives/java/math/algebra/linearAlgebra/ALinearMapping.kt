package cn.timelives.java.math.algebra.linearAlgebra

import cn.timelives.java.math.CalculatorHolder
import cn.timelives.java.math.algebra.abstractAlgebra.calculator.FieldCalculator
import cn.timelives.java.math.algebra.abstractAlgebra.calculator.VectorSpaceCalculator
import cn.timelives.java.math.algebra.abstractAlgebra.calculator.eval
import cn.timelives.java.math.algebra.linearAlgebra.space.VectorSpace
import cn.timelives.java.math.function.MathFunction
import cn.timelives.java.math.function.SVFunction
import cn.timelives.java.math.function.invoke
import cn.timelives.java.math.numberModels.api.VectorModel


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
        get() = vectorSpace.basis.calculator

    val vectorCalculator: VectorSpaceCalculator<K,U>
        get() = vectorSpace.calculator

    /**
     * The vector space of [U].
     */
    abstract val vectorSpace: VectorSpace<K, U>


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
            vectorCalculator.eval {
                f(x) + g(x)
            }

        }
    }

    override fun negate(): ALinearMapping<K, V, U> {
        val f = this
        return mappingOf { x ->
            vectorCalculator.eval {
                -f(x)
            }
        }
    }

    override fun multiply(k: K): ALinearMapping<K, V, U> {
        val f = this
        return mappingOf { x ->
            vectorCalculator.scalarMultiply(k,f(x))
        }
    }


    open fun <W : Any> composeLinear(g: ALinearMapping<K, U, W>): ALinearMapping<K, V, W> {
        val f = this
        return DALinearMapping(g.vectorSpace) { x ->
            g(f(x))
        }
    }


}

abstract class KALinearTrans<K : Any, V : Any>(override val vectorSpace: VectorSpace<K, V>) : ALinearMapping<K, V, V>(), SVFunction<V> {

}

//
//class IdentityLinearMapping<K:Any,V:Any,U:Any>(override val vectorSpace: VectorSpace<K, U>) : LinearMapping<K,,V,U>(){
//    override fun apply(x: V): U = x
//
//    override fun composeLinear(g: LinearMapping<K, V,U>): LinearMapping<K, V,U> {
//        return g
//    }
//}

class ZeroALinearMapping<K : Any, V : Any, U : Any>(override val vectorSpace: VectorSpace<K, U>) : ALinearMapping<K, V, U>() {
    override fun apply(x: V): U = vectorSpace.identity()
}

class DALinearMapping<K : Any, V : Any, U : Any>(override val vectorSpace: VectorSpace<K, U>, private val f: (V) -> U)
    : ALinearMapping<K, V, U>() {
    override fun apply(x: V): U {
        return f(x)
    }
}

package cn.timelives.java.math.algebra.linearAlgebra

import cn.timelives.java.math.CalculatorHolder
import cn.timelives.java.math.algebra.abstractAlgebra.calculator.FieldCalculator
import cn.timelives.java.math.algebra.abstractAlgebra.calculator.GroupCalculator
import cn.timelives.java.math.algebra.abstractAlgebra.calculator.eval
import cn.timelives.java.math.algebra.linearAlgebra.space.VectorSpace
import cn.timelives.java.math.function.SVFunction
import cn.timelives.java.math.function.invoke
import cn.timelives.java.math.numberModels.api.GroupNumberModel


/**
 * A linear mapping over a field `K`. For a linear mapping `F`, it is
 * required that
 * 1. For any elements `u`, `v` in `V` we have
 * >F(u+v) = F(u) + F(v)
 * 2. For all `c` in `K` and
 * 3.
 *
 *
 * Created at 2018/10/8 17:41
 * @author  liyicheng
 */
abstract class LinearMapping<K : Any,V : Any> : SVFunction<V>,CalculatorHolder<K,FieldCalculator<K>>,
        GroupNumberModel<LinearMapping<K,V>> {
    /**
     * Returns the math calculator for field `K`
     */
    override val mathCalculator: FieldCalculator<K>
        get() = vectorSpace.basis.getCalculator()

    val vectorCalculator : GroupCalculator<V>
        get() = vectorSpace.calculator

    /**
     * The vector space of [V].
     */
    abstract val vectorSpace : VectorSpace<K,V>


    /**
     * Applies the mapping.
     */
    abstract override fun apply(x: V): V

    protected fun mappingOf(m : (V)->V) = DLinearMapping(vectorSpace,m)

    override fun add(y: LinearMapping<K, V>): LinearMapping<K, V> {
        val f = this
        @Suppress("UnnecessaryVariable")//To make the following code more clear
        val g = y
        return mappingOf {x ->
            vectorCalculator.eval {
                f(x) + g(x)
            }

        }
    }

    override fun negate(): LinearMapping<K, V> {
        val f = this
        return mappingOf { x ->
            vectorCalculator.eval {
                -f(x)
            }
        }
    }

    open fun composeLinear(g : LinearMapping<K,V>) : LinearMapping<K,V>{
        val f = this
        return mappingOf {x ->
            g(f(x))
        }
    }
}

class IdentityLinearMapping<K:Any,V:Any>(override val vectorSpace: VectorSpace<K, V>) : LinearMapping<K,V>(){
    override fun apply(x: V): V = x

    override fun composeLinear(g: LinearMapping<K, V>): LinearMapping<K, V> {
        return g
    }
}

class ZeroLinearMapping<K:Any,V:Any>(override val vectorSpace: VectorSpace<K, V>) : LinearMapping<K,V>(){
    override fun apply(x: V): V = vectorSpace.identity()
}

class DLinearMapping<K:Any, V :Any>(override val vectorSpace: VectorSpace<K, V>, private val f : (V)->V)
    : LinearMapping<K,V>(){
    override fun apply(x: V): V {
        return f(x)
    }
}

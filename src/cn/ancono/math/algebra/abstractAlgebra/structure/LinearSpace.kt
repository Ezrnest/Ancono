package cn.ancono.math.algebra.abstractAlgebra.structure

import cn.ancono.math.algebra.abstractAlgebra.calculator.LinearSpaceCalculator
import cn.ancono.math.algebra.abstractAlgebra.field.FieldFromCal
import cn.ancono.math.set.MathSets


/*
 * Created at 2018/10/8 21:53
 * @author  liyicheng
 */
interface LinearSpace<T : Any, V : Any> : Module<T, V> {

    override val scalars: Field<T>
        get() = FieldFromCal(calculator.scalarCalculator, MathSets.universe<T>())

    override fun getCalculator(): LinearSpaceCalculator<T, V>


    /**
     * Returns a basis of the vector space.
     */
    val basis: List<V>
}


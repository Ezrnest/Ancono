package cn.ancono.math.algebra.linearAlgebra.space

import cn.ancono.math.algebra.abstractAlgebra.calculator.VectorSpaceCalculator
import cn.ancono.math.algebra.abstractAlgebra.structure.Field
import cn.ancono.math.algebra.abstractAlgebra.structure.Module


/*
 * Created at 2018/10/8 21:53
 * @author  liyicheng
 */
interface VectorSpace<T : Any, V : Any> : Module<T, V> {

    override val basis: Field<T>

    override fun getCalculator(): VectorSpaceCalculator<T, V>
}
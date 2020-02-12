package cn.timelives.java.math.algebra.linearAlgebra.space

import cn.timelives.java.math.algebra.abstractAlgebra.calculator.ModuleCalculator
import cn.timelives.java.math.algebra.abstractAlgebra.calculator.VectorSpaceCalculator
import cn.timelives.java.math.algebra.abstractAlgebra.structure.Field
import cn.timelives.java.math.algebra.abstractAlgebra.structure.Module
import cn.timelives.java.math.algebra.abstractAlgebra.structure.Ring


/*
 * Created at 2018/10/8 21:53
 * @author  liyicheng
 */
interface VectorSpace<T:Any,V : Any> : Module<T,V> {

    override val basis: Field<T>

    override fun getCalculator(): VectorSpaceCalculator<T, V>
}
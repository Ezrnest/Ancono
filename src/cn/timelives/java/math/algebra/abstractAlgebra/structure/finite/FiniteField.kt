package cn.timelives.java.math.algebra.abstractAlgebra.structure.finite

import cn.timelives.java.math.algebra.abstractAlgebra.structure.Field
import cn.timelives.java.math.set.FiniteSet


/**
 * Describes a field with finite elements.
 * Created at 2018/11/10 13:58
 * @author  liyicheng
 */
interface FiniteField<T:Any> : Field<T>, FiniteGroup<T>{

    override fun getSet(): FiniteSet<T>

}
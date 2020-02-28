package cn.ancono.math.algebra.abstractAlgebra.structure

import cn.ancono.math.algebra.abstractAlgebra.calculator.AlgebraCalculator
import cn.ancono.math.algebra.linearAlgebra.space.VectorSpace


/*
 * Created at 2018/11/29 18:42
 * @author  liyicheng
 */
interface Algebra<K : Any, V : Any> : VectorSpace<K, V> {
    override fun getCalculator(): AlgebraCalculator<K, V>
}
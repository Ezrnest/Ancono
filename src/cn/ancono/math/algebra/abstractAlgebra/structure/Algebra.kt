package cn.ancono.math.algebra.abstractAlgebra.structure

import cn.ancono.math.algebra.abstractAlgebra.calculator.AlgebraCalculator


/*
 * Created at 2018/11/29 18:42
 * @author  liyicheng
 */
interface Algebra<K : Any, V : Any> : LinearSpace<K, V> {
    override fun getCalculator(): AlgebraCalculator<K, V>
}
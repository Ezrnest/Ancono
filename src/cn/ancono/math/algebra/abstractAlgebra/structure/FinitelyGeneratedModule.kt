package cn.ancono.math.algebra.abstractAlgebra.structure

import cn.ancono.math.property.FiniteGenerator


/*
 * Created by liyicheng at 2020-03-06 12:01
 */
/**
 * @author liyicheng
 */
interface FinitelyGeneratedModule<T : Any, V : Any> : Module<T, V> {

    /**
     * Returns the generators, which are finite.
     */
    val generators: FiniteGenerator<V, T>
}
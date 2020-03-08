package cn.ancono.math.algebra.abstractAlgebra.structure

import cn.ancono.math.algebra.abstractAlgebra.calculator.LinearSpaceCalculator
import cn.ancono.math.algebra.abstractAlgebra.field.FieldFromCal
import cn.ancono.math.property.FiniteGenerator
import cn.ancono.math.set.MathSets


/*
 * Created at 2018/10/8 21:53
 * @author  liyicheng
 */
interface LinearSpace<K : Any, V : Any> : Module<K, V> {

    override val scalars: Field<K>
        get() = FieldFromCal(calculator.scalarCalculator, MathSets.universe())

    override fun getCalculator(): LinearSpaceCalculator<K, V>

}

/**
 * Describes a linear space of finite dimension.
 */
interface FiniteLinearSpace<K : Any, V : Any> : LinearSpace<K, V> {
//Created by lyc at 2020-03-07 17:50
    /**
     * Gets the dimension of this linear space.
     */
    @JvmDefault
    val dim: Int
        get() = basis.rank

    /**
     * Gets a basis of this linear space.
     */
    val basis: FiniteLinearBasis<K, V>

    /**
     * Determines whether the given elements can be a basis of this linear space.
     */
    fun isBasis(vs: List<V>): Boolean
}

/**
 * Describes a basis of a finite linear space.
 */
interface FiniteLinearBasis<T : Any, V : Any> : FiniteGenerator<T, V> {
    override fun getElements(): List<V>

    /**
     * Gets the rank of this linear basis, which is equal to the size of the elements.
     */
    @JvmDefault
    val rank: Int
        get() = elements.size
}


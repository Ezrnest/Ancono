package cn.ancono.math.algebra.abs.structure

import cn.ancono.math.algebra.abs.calculator.LinearSpaceCalculator
import cn.ancono.math.algebra.abs.field.FieldFromCal
import cn.ancono.math.property.FiniteGenerator
import cn.ancono.math.set.MathSets


/*
 * Created at 2018/10/8 21:53
 * @author  liyicheng
 */
interface LinearSpace<K, V> : Module<K, V> {

    override val scalars: Field<K>
        get() = FieldFromCal(abelCal.scalarCalculator, MathSets.universe())

    override fun getAbelCal(): LinearSpaceCalculator<K, V>
}

/**
 * Describes a linear space of finite dimension.
 */
interface FiniteLinearSpace<K, V> : LinearSpace<K, V> {
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
interface FiniteLinearBasis<T, V> : FiniteGenerator<T, V> {
    override fun getElements(): List<V>

    /**
     * Gets the rank of this linear basis, which is equal to the size of the elements.
     */
    @JvmDefault
    val rank: Int
        get() = elements.size
}


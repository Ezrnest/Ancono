package cn.ancono.math.algebra.abstractAlgebra.field

import cn.ancono.math.algebra.abstractAlgebra.calculator.FieldCalculator
import cn.ancono.math.algebra.abstractAlgebra.calculator.GroupCalculator
import cn.ancono.math.algebra.abstractAlgebra.group.finite.AbstractFiniteGroup
import cn.ancono.math.algebra.abstractAlgebra.structure.Group
import cn.ancono.math.algebra.abstractAlgebra.structure.finite.FiniteField
import cn.ancono.math.algebra.abstractAlgebra.structure.finite.FiniteGroup
import cn.ancono.math.set.FiniteSet


/*
 * Created at 2018/11/10 13:57
 * @author  liyicheng
 */
abstract class AbstractFiniteField<T : Any> : AbstractFiniteGroup<T>(), FiniteField<T> {


    override fun getNormalSubgroups(): FiniteSet<out AbstractFiniteGroup<T>> {
        return subgroups
    }

    override fun isNormalSubgroup(g: Group<T>): Boolean {
        return true
    }

    override fun isConjugate(h1: Group<T>, h2: Group<T>): Boolean {
        return h1 == h2
    }

    override fun isConjugate(g1: T, g2: T): Boolean {
        return g1 == g2
    }

    override fun conjugateSubgroup(h: Group<T>, x: T): FiniteGroup<T> {
        require(h is FiniteGroup)
        return h
    }

    override fun normalizer(h: Group<T>): FiniteGroup<T> {
        return this
    }

    override fun centralizer(a: T): FiniteGroup<T> {
        return this
    }

    override fun centralizer(h: Group<T>): FiniteGroup<T> {
        return this
    }
}
package cn.ancono.math.algebra.abs.structure.finite

import cn.ancono.math.algebra.abs.structure.Field
import cn.ancono.math.algebra.abs.structure.Group
import cn.ancono.math.set.FiniteSet


/**
 * Describes a field with finite elements.
 * Created at 2018/11/10 13:58
 * @author  liyicheng
 */
interface FiniteField<T> : Field<T>, FiniteGroup<T> {

    override fun getSet(): FiniteSet<T>

    override fun getNormalSubgroups(): FiniteSet<out FiniteGroup<T>> {
        return subgroups
    }

    override fun centralizer(a: T): FiniteGroup<T> {
        return this
    }

    override fun centralizer(h: Group<T>): FiniteGroup<T> {
        return this
    }

    override fun normalizer(h: Group<T>): FiniteGroup<T> {
        return this
    }

    override fun conjugateSubgroup(h: Group<T>, x: T): FiniteGroup<T> {
        require(h is FiniteGroup)
        return h
    }


}
package cn.ancono.math.algebra.abstractAlgebra.structure.finite

import cn.ancono.math.algebra.abstractAlgebra.structure.Field
import cn.ancono.math.algebra.abstractAlgebra.structure.Group
import cn.ancono.math.set.FiniteSet
import cn.ancono.math.set.MathSet


/**
 * Describes a field with finite elements.
 * Created at 2018/11/10 13:58
 * @author  liyicheng
 */
interface FiniteField<T : Any> : Field<T>, FiniteGroup<T> {

    override fun getSet(): FiniteSet<T>

    @JvmDefault
    override fun getNormalSubgroups(): FiniteSet<out FiniteGroup<T>> {
        return subgroups
    }

    @JvmDefault
    override fun centralizer(a: T): FiniteGroup<T> {
        return this
    }

    @JvmDefault
    override fun centralizer(h: Group<T>): FiniteGroup<T> {
        return this
    }

    @JvmDefault
    override fun normalizer(h: Group<T>): FiniteGroup<T> {
        return this
    }

    @JvmDefault
    override fun conjugateSubgroup(h: Group<T>, x: T): FiniteGroup<T> {
        require(h is FiniteGroup)
        return h
    }


}
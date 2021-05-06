package cn.ancono.math.algebra.abs.module

import cn.ancono.math.MathObject
import cn.ancono.math.algebra.abs.EqualRelation
import cn.ancono.math.algebra.abs.calculator.EqualPredicate
import cn.ancono.math.algebra.abs.calculator.GroupCalculator
import cn.ancono.math.algebra.abs.calculator.ModuleCalculator
import cn.ancono.math.algebra.abs.calculator.RingCalculator
import cn.ancono.math.algebra.abs.group.Homomorphism
import cn.ancono.math.algebra.abs.structure.*
import cn.ancono.math.numberModels.api.IntCalculator
import cn.ancono.math.numberModels.api.NumberFormatter
import cn.ancono.math.set.MathSet
import java.util.function.Function


/*
 * Created by liyicheng at 2020-03-09 17:36
 */
interface ZModule<Z, V> : Module<Z, V>

object ZModules {
    fun <Z, V> fromAbelianGroup(cal: IntCalculator<Z>, group: AbelianGroup<V>): ZModule<Z, V> {
        return ZModuleFromAGroup(cal, group)
    }
}

class ZModuleCalFromAGroup<Z, V>(val ic: IntCalculator<Z>, val gc: GroupCalculator<V>) : ModuleCalculator<Z, V> {
    override fun scalarMultiply(k: Z, v: V): V {
        return gc.gpow(v, ic.asLong(k))
    }

    override val scalarCalculator: RingCalculator<Z>
        get() = ic

    override fun add(x: V, y: V): V {
        return gc.apply(x, y)
    }

    override fun negate(x: V): V {
        return gc.inverse(x)
    }

    override val zero: V
        get() = gc.identity


    override fun isEqual(x: V, y: V): Boolean {
        return gc.isEqual(x, y)
    }

    override val numberClass: Class<V>
        get() = gc.numberClass
}

/**
 * Defines a Z-module from an Abelian group.
 */
class ZModuleFromAGroup<Z, V>(val cal: IntCalculator<Z>,
                              private val group: AbelianGroup<V>)
//Created by lyc at 2020-03-11 18:8
    : MathObject<Z, IntCalculator<Z>>,
        ZModule<Z, V> {
    override val calculator: IntCalculator<Z>
        get() = cal

    private val moduleCal: ModuleCalculator<Z, V> = ZModuleCalFromAGroup(cal, group.calculator)

    override fun getAbelCal(): ModuleCalculator<Z, V> {
        return moduleCal
    }


    override val scalars: Ring<Z>
        get() = super.scalars

    override fun isConjugate(g1: V, g2: V): Boolean {
        return group.isConjugate(g1, g2)
    }

    override fun getNormalSubgroups(): MathSet<out Group<V>> {
        return group.normalSubgroups
    }

    override fun isNormalSubgroup(g: Group<V>): Boolean {
        return group.isNormalSubgroup(g)
    }

    override fun isConjugate(h1: Group<V>, h2: Group<V>): Boolean {
        return group.isConjugate(h1, h2)
    }

    override fun normalizer(h: Group<V>): Group<V> {
        return group.normalizer(h)
    }

    override fun centralizer(a: V): Group<V> {
        return group.centralizer(a)
    }

    override fun centralizer(h: Group<V>): Group<V> {
        return group.centralizer(h)
    }

    override fun identity(): V {
        return group.identity()
    }

    override fun index(): Long {
        return group.index()
    }

    override fun getSubgroups(): MathSet<out Group<V>> {
        return group.subgroups
    }

    override fun isSubgroup(g: Group<V>): Boolean {
        return group.isSubgroup(g)
    }

    override fun getCoset(x: V, isLeft: Boolean): Coset<V, out Group<V>> {
        return group.getCoset(x, isLeft)
    }

    override fun getCoset(x: V, subGroup: Group<V>, isLeft: Boolean): Coset<V, out Group<V>> {
        return group.getCoset(x, subGroup, isLeft)
    }

    override fun getCosets(h: Group<V>, isLeft: Boolean): MathSet<out Coset<V, out Group<V>>> {
        return group.getCosets(h, isLeft)
    }

    override fun indexOf(sub: Group<V>): Long {
        return group.indexOf(sub)
    }

    override fun conjugateClass(): EqualRelation<Group<V>> {
        return group.conjugateClass()
    }

    override fun conjugateElementClass(): EqualRelation<V> {
        return group.conjugateElementClass()
    }

    override fun quotientGroup(h: Group<V>): Group<out Coset<V, out Group<V>>> {
        return group.quotientGroup(h)
    }

    override fun quotientGroupAndHomo(h: Group<V>): Homomorphism<V, out Coset<V, out Group<V>>, out Group<V>, out Group<out Coset<V, out Group<V>>>, out Group<V>> {
        return group.quotientGroupAndHomo(h)
    }

    override fun conjugateSubgroup(h: Group<V>, x: V): Group<V> {
        return group.conjugateSubgroup(h, x)
    }


    override fun toString(nf: NumberFormatter<Z>): String {
        return "Z-module from $group"
    }


    override fun getSet(): MathSet<V> {
        return group.set
    }

    override fun toString(): String {
        return toString(NumberFormatter.defaultFormatter())
    }

    override fun valueEquals(obj: MathObject<Z, IntCalculator<Z>>): Boolean {
        TODO("Not yet implemented")
    }

    override fun <N> mapTo(newCalculator: EqualPredicate<N>, mapper: Function<Z, N>): MathObject<N, *> {
        TODO("Not yet implemented")
    }
}
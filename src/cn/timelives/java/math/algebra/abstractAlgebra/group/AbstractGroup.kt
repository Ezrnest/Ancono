package cn.timelives.java.math.algebra.abstractAlgebra.group

import cn.timelives.java.math.algebra.abstractAlgebra.calculator.GroupCalculator
import cn.timelives.java.math.algebra.abstractAlgebra.structure.Coset
import cn.timelives.java.math.algebra.abstractAlgebra.structure.Group
import cn.timelives.java.math.set.MathSet


/*
 * Created at 2018/10/9 12:46
 * @author  liyicheng
 */
abstract class AbstractGroup<T:Any>(open val gc : GroupCalculator<T>, protected val elements: MathSet<T>) : Group<T>{
    override fun getCalculator(): GroupCalculator<T> {
        return gc
    }

    override fun getSet(): MathSet<T> {
        return elements
    }

    override fun identity(): T {
        return gc.identity
    }

    override fun index(): Long {
        throw UnsupportedOperationException()
    }

    override fun getSubgroups(): MathSet<out Group<T>> {
        throw UnsupportedOperationException()
    }

    override fun isSubgroup(g: Group<T>): Boolean {
        throw UnsupportedOperationException()
    }

    override fun getNormalSubgroups(): MathSet<out Group<T>> {
        throw UnsupportedOperationException()
    }

    override fun isNormalSubgroup(g: Group<T>): Boolean {
        throw UnsupportedOperationException()
    }

    override fun getCoset(x: T, subGroup: Group<T>, isLeft: Boolean): Coset<T, out Group<T>> {
        throw UnsupportedOperationException()
    }

    override fun getCosets(h: Group<T>, isLeft: Boolean): MathSet<out Coset<T, out Group<T>>> {
        throw UnsupportedOperationException()
    }

    override fun indexOf(sub: Group<T>): Long {
        throw UnsupportedOperationException()
    }

    override fun isConjugate(h1: Group<T>, h2: Group<T>): Boolean {
        throw UnsupportedOperationException()
    }

    override fun isConjugate(g1: T, g2: T): Boolean {
        throw UnsupportedOperationException()
    }

    override fun normalizer(h: Group<T>): Group<T> {
        throw UnsupportedOperationException()
    }

    override fun centralizer(a: T): Group<T> {
        throw UnsupportedOperationException()
    }

    override fun centralizer(h: Group<T>): Group<T> {
        throw UnsupportedOperationException()
    }

    override fun conjugateSubgroup(h: Group<T>, x: T): Group<T> {
        throw UnsupportedOperationException()
    }

    override fun quotientGroup(h: Group<T>): Group<out Coset<T, out Group<T>>> {
        throw UnsupportedOperationException()
    }

    override fun quotientGroupAndHomo(h: Group<T>): Homomorphism<T, out Coset<T, out Group<T>>, out Group<T>, out Group<out Coset<T, out Group<T>>>, out Group<T>> {
        throw UnsupportedOperationException()
    }
}
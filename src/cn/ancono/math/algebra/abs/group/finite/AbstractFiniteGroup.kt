/**
 * 2018-03-01
 */
package cn.ancono.math.algebra.abs.group.finite

import cn.ancono.math.algebra.abs.FiniteGroups
import cn.ancono.math.algebra.abs.HomomorphismMapping
import cn.ancono.math.algebra.abs.calculator.EqualPredicate
import cn.ancono.math.algebra.abs.calculator.GroupCalculator
import cn.ancono.math.algebra.abs.calculator.conjugateBy
import cn.ancono.math.algebra.abs.calculator.eval
import cn.ancono.math.algebra.abs.group.NotARequiredSubgroupException
import cn.ancono.math.algebra.abs.group.NotASubgroupException
import cn.ancono.math.algebra.abs.structure.Group
import cn.ancono.math.algebra.abs.structure.finite.FiniteGroup
import cn.ancono.math.discrete.combination.Permutation
import cn.ancono.math.discrete.combination.Permutations
import cn.ancono.math.numberModels.api.RealCalculator
import cn.ancono.math.set.FiniteSet
import cn.ancono.math.set.MathSets
import cn.ancono.utilities.ArraySup
import cn.ancono.utilities.CollectionSup
import java.util.function.Function
import java.util.function.Predicate
import java.util.function.ToIntFunction

/**
 * The abstract superclass for all finite groups. Basic methods are implemented.
 * @author liyicheng
 * 2018-03-01 19:13
 */
abstract class AbstractFiniteGroup<T>
    : FiniteGroup<T> {
    private var mc: RealCalculator<T>? = null

//    protected val wrappedCalculator: MathCalculator<T>
//        get() {
//            if (mc == null) {
//                mc = GroupCalculators.toMathCalculatorAdd(calculator)
//            }
//            return mc!!
//        }

    @Transient
    private var groupTable: Array<IntArray>? = null


    /*
	 * @see cn.ancono.math.algebra.abstractAlgebra.structure.Monoid#identity()
	 */
    override fun identity(): T {
        return calculator.identity
    }

    override fun indexOf(sub: Group<T>): Long {
        return super<FiniteGroup>.indexOf(sub)
    }

    override fun groupTable(): Array<IntArray> {
        if (groupTable != null) {
            return groupTable!!
        }
        //mapping first
        val set = set
        val size = Math.toIntExact(set.size())

        @Suppress("UNCHECKED_CAST")
        val arr = CollectionSup.iteratorToArray(set.iterator(), size) as Array<T>
        //		IntFunction<T> to = x -> arr[x];
        val from = ToIntFunction<T> { x: T -> ArraySup.firstIndexOf(arr) { y -> calculator.isEqual(x, y) } }
        val result = Array(size) { IntArray(size) }
        for (i in 0 until size) {
            for (j in 0 until size) {
                val a = arr[i]
                val b = arr[j]
                val c = calculator.apply(a, b)
                result[i][j] = from.applyAsInt(c)
                val t = result[i][j]
                if (t == -1) {
                    //                    T c = gc.apply(a,b);
                    //                    print(a);
                    //                    print(b);
                    //                    print(c);
                    throw AssertionError()
                }
            }
        }
        groupTable = result
        return result
    }

    /*
	 * @see cn.ancono.math.algebra.abstractAlgebra.structure.Group#isSubgroup(cn.ancono.math.algebra.abstractAlgebra.structure.Group)
	 */
    override fun isSubgroup(g: Group<T>): Boolean {
        if (g !is FiniteGroup<*>) {
            return false
        }
        val group = g as FiniteGroup<T>
        return if (group.calculator != calculator) {
            false
        } else MathSets.containsAll(set, group.set)
    }

    protected fun asSubgroup(h: Group<T>): FiniteGroup<T> {
        if (!isSubgroup(h)) {
            throw IllegalArgumentException()
        }
        return h as FiniteGroup<T>
    }


    /*
	 * @see cn.ancono.math.algebra.abstractAlgebra.structure.Group#getNormalSubgroups()
	 */
    override fun getNormalSubgroups(): FiniteSet<out AbstractFiniteGroup<T>> {
        throw UnsupportedOperationException()
    }

    /*
	 * @see cn.ancono.math.algebra.abstractAlgebra.structure.Group#isNormalSubgroup(cn.ancono.math.algebra.abstractAlgebra.structure.Group)
	 */
    override fun isNormalSubgroup(g: Group<T>): Boolean {
        if (!isSubgroup(g)) {
            return false
        }
        val sub = g as FiniteGroup<T>
        if (index() / sub.index() == 2L) {
            return true
        }
        val subSet = sub.set
        for (x in set) {
            for (h in subSet) {
                val t = calculator.conjugateBy(h, x)
                if (!subSet.contains(t)) {
                    return false
                }
            }
        }
        return true
    }

    /*
	 * @see cn.ancono.math.algebra.abstractAlgebra.structure.Group#getCoset(java.lang.Object)
	 */
    override fun getCoset(x: T, isLeft: Boolean): FiniteCoset<T> {
        return getCoset(x, this, isLeft)
    }

    /*
	 * @see cn.ancono.math.algebra.abstractAlgebra.structure.Group#getCoset(java.lang.Object, cn.ancono.math.algebra.abstractAlgebra.structure.Group)
	 */
    override fun getCoset(x: T, subGroup: Group<T>, isLeft: Boolean): FiniteCoset<T> {
        if (subGroup !is AbstractFiniteGroup<*>) {
            throw NotASubgroupException()
        }
        val set = set
        val list = ArrayList<T>()
        for (t in set) {
            val y = if (isLeft) calculator.apply(x, t) else calculator.apply(t, x)
            if (!CollectionSup.contains(list) { z -> calculator.isEqual(z, y) }) {
                list.add(y)
            }
        }
        val coset = MathSets.fromCollection(list, calculator)
        return FiniteCoset(coset, this, subGroup as AbstractFiniteGroup<T>)
    }

    override fun getCosets(h: Group<T>, isLeft: Boolean): FiniteSet<FiniteCoset<T>> {
        val sub = asSubgroup(h)
        val list = arrayListOf<FiniteCoset<T>>()

        for (g in set) {
            if (list.any { coset -> coset.contains(g) }) {
                continue
            }
            val coset = getCoset(g, sub, isLeft)
            list += coset
        }

        return MathSets.fromCollection(list, object : EqualPredicate<FiniteCoset<T>> {
            override fun isEqual(x: FiniteCoset<T>, y: FiniteCoset<T>): Boolean {
                return x.group === y.group && x.subGroup == y.subGroup
                        && x.set.contains(y.set.get(0))
            }

            @Suppress("UNCHECKED_CAST")
            override val numberClass: Class<FiniteCoset<T>>
                get() {
                    return FiniteCoset::class.java as Class<FiniteCoset<T>>
                }
        })
    }

    /**
     * Returns a regular representation of this finite group.
     */
    override fun regularRepresent(isRight: Boolean): PermutationGroup {
        val size = Math.toIntExact(index())
        val ps = ArrayList<Permutation>(size)

        @Suppress("UNCHECKED_CAST")
        val eleArr = arrayOfNulls<Any>(size) as Array<T>
        val eleSet = set
        var i = 0
        for (a in eleSet) {
            eleArr[i++] = a
        }

        i = 0
        while (i < size) {
            val a = eleArr[i]
            val permutation = IntArray(size)
            for (j in 0 until size) {
                val b = eleArr[j]
                val re: T = if (isRight) {
                    //a * a_i
                    calculator.apply(b, a)
                } else {
                    //a_i^-1 * a
                    calculator.apply(calculator.inverse(a), b)
                }
                val index = ArraySup.firstIndexOf(eleArr) { x -> calculator.isEqual(x, re) }
                permutation[j] = index
            }
            ps.add(Permutations.valueOf(*permutation))
            i++
        }

        return PermutationGroup.groupOfChecked(
                MathSets.fromCollection(ps, Permutations.getCalculator(size))
        )
    }


    /*
	 * @see cn.ancono.math.algebra.abstractAlgebra.structure.finite.FiniteGroup#getSubgroups()
	 */
    override fun getSubgroups(): FiniteSet<out AbstractFiniteGroup<T>> {
        //TODO: find methods to find all the subgroups of this finit group
        TODO()
//        return MathSets.asSet(EqualPredicate.naturalEqual(), this, FiniteGroups.identityGroup(calculator))
    }

    /*
	 * @see cn.ancono.math.algebra.abstractAlgebra.structure.Group#index()
	 */
    override fun index(): Long {
        return set.size()
    }


    override fun isConjugate(h1: Group<T>, h2: Group<T>): Boolean {
        val sub1 = asSubgroup(h1)
        val sub2 = asSubgroup(h2)
        if (h1 === h2) {
            return true
        }
        if (sub1.index() != sub2.index()) {
            return false
        }
        for (g in set) {
            var equals = true
            for (h in sub1.set) {
                val t = calculator.conjugateBy(h, g)
                if (!calculator.isEqual(t, h)) {
                    equals = false
                    break
                }
            }
            if (equals) {
                return true
            }
        }
        return false
    }

    override fun isConjugate(g1: T, g2: T): Boolean {
        for (x in set) {
            val t = calculator.conjugateBy(g1, x)
            if (calculator.isEqual(t, g2)) {
                return true
            }
        }

        return false
    }

    override fun conjugateSubgroup(h: Group<T>, x: T): FiniteGroup<T> {
        val sub = asSubgroup(h)
        val eles = MathSets.map(sub.set, calculator, Function { calculator.conjugateBy(it, x) })
        return FiniteGroups.createGroupWithoutCheck(calculator, eles)
    }


    override fun normalizer(h: Group<T>): FiniteGroup<T> {
        val sub = asSubgroup(h)
        val elements = sub.set
        val re = MathSets.filter(set, calculator, Predicate { x ->
            var isNormalizer = true
            for (t in elements) {
                val re = calculator.conjugateBy(t, x)
                if (!elements.contains(re)) {
                    isNormalizer = false
                    break
                }
            }
            isNormalizer
        })
        return FiniteGroups.createGroupWithoutCheck(calculator, re)
    }


    override fun centralizer(a: T): FiniteGroup<T> {
        val re = MathSets.filter(set, calculator, Predicate { x ->
            calculator.isEqual(a, calculator.conjugateBy(a, x))
        })
        return FiniteGroups.createGroupWithoutCheck(calculator, re)
    }


    override fun centralizer(h: Group<T>): FiniteGroup<T> {
        val sub = asSubgroup(h)
        val list = set.filter { x ->
            sub.set.all { h ->
                calculator.eval {
                    isEqual(apply(x, h), apply(h, x))
                }
            }
        }
        return FiniteGroups.createGroupWithoutCheck(calculator, list)
    }

    override fun quotientGroup(h: Group<T>): FiniteGroup<FiniteCoset<T>> {
        if (!isNormalSubgroup(h)) {
            throw NotARequiredSubgroupException("H must be normal subgroup.")
        }
        val elements = getCosets(h, true)

        fun cosetOf(x: T): FiniteCoset<T> {
            return elements.first { it.contains(x) }
        }

        val cosetGC = object : GroupCalculator<FiniteCoset<T>> {
            override fun inverse(x: FiniteCoset<T>): FiniteCoset<T> {
                return cosetOf(calculator.inverse(x.representative))
            }

            override val identity: FiniteCoset<T> = cosetOf(calculator.identity)

            override fun apply(x: FiniteCoset<T>, y: FiniteCoset<T>): FiniteCoset<T> {
                return cosetOf(calculator.apply(x.representative, y.representative))
            }

            override fun isEqual(x: FiniteCoset<T>, y: FiniteCoset<T>): Boolean {
                return x.contains(y.representative)
            }
        }
        return FiniteGroups.createGroupWithoutCheck(cosetGC, elements)
    }

    override fun quotientGroupAndHomo(h: Group<T>)
            : FiniteHomomorphism<T,
            FiniteCoset<T>, out FiniteGroup<T>,
            out FiniteGroup<FiniteCoset<T>>,
            out FiniteGroup<T>> {
        val qg = quotientGroup(h)
        val elements = qg.set
        val mapping: HomomorphismMapping<T, FiniteCoset<T>> = object : HomomorphismMapping<T, FiniteCoset<T>> {
            override fun apply(x: T): FiniteCoset<T> {
                return elements.first { it.contains(x) }
            }
        }
        return FiniteHomomorphism(mapping, this, qg, asSubgroup(h))
    }


}

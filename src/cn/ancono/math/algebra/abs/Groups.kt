/**
 * 2018-02-28
 */
package cn.ancono.math.algebra.abs

import cn.ancono.math.algebra.abs.calculator.GroupCalculator
import cn.ancono.math.algebra.abs.group.*
import cn.ancono.math.algebra.abs.structure.Coset
import cn.ancono.math.algebra.abs.structure.Group
import cn.ancono.math.function.Bijection
import cn.ancono.math.function.invoke
import cn.ancono.math.set.MathSet

/**
 * @author liyicheng
 * 2018-02-28 19:26
 */
object Groups {


    fun <T, S> isomorphism(g: Group<T>, f: Bijection<T, S>): Group<S> {
        return IsomorphismedGroup(g, f)
    }


    /**
     * Creates a group that only supports `Group#getCalculator()` and `Group#getSet()` without
     * checking whether the operation defined by [gc] is closed upon [set].
     */
    @JvmStatic
    fun <T> createGroupWithoutCheck(gc: GroupCalculator<T>, set: MathSet<T>): Group<T> {
        return GroupFromCal(gc, set)
    }

}

class IsomorphismedCoset<T, S>(val coset: Coset<T, *>, val f: Bijection<T, S>,
                               val group: IsomorphismedGroup<T, S>, val subGroup: IsomorphismedGroup<T, S>) :
        AbstractCoset<S, IsomorphismedGroup<T, S>>(group, subGroup) {

    override fun contains(t: S): Boolean {
        return coset.contains(f.deply(t))
    }

    override fun getRepresentatives(): MathSet<S> {
        return coset.getRepresentatives().mapTo(f)
    }

    override fun index(): Long {
        return coset.index()
    }
}

class IsomorphismedGroup<T, S>(val group: Group<T>, val f: Bijection<T, S>) : Group<S> {


    private val isomSet: MathSet<S> = group.set.mapTo(f)
    private val isomCal = GroupCalculators.isomorphism(group.calculator, f)
    override fun getSet(): MathSet<S> {
        return isomSet
    }

    override fun getCalculator(): GroupCalculator<S> {
        return isomCal
    }

    override fun index(): Long {
        return group.index()
    }

    private val subGroupMapper: Bijection<Group<T>, IsomorphismedGroup<T, S>>
        get() = Bijection.of({ subGroup -> IsomorphismedGroup(subGroup, f) }, { isomed -> isomed.group })

    @Suppress("UNCHECKED_CAST")
    private val cosetMapper: Bijection<Coset<T, Group<T>>, IsomorphismedCoset<T, S>>
        get() = Bijection.of({ c -> IsomorphismedCoset(c, f, this, IsomorphismedGroup(c.subGroup, f)) },
                { isomed -> isomed.coset as Coset<T, Group<T>> })


    override fun getSubgroups(): MathSet<IsomorphismedGroup<T, S>> {
        @Suppress("UNCHECKED_CAST")
        val sub = group.subgroups as MathSet<Group<T>>
        return sub.mapTo(subGroupMapper)
    }

    override fun isSubgroup(g: Group<S>): Boolean {
        return group.isSubgroup(originOf(g) ?: return false)
    }

    override fun getNormalSubgroups(): MathSet<IsomorphismedGroup<T, S>> {
        @Suppress("UNCHECKED_CAST")
        return (group.normalSubgroups as MathSet<Group<T>>).mapTo(subGroupMapper)
    }

    override fun isNormalSubgroup(g: Group<S>): Boolean {
        return group.isNormalSubgroup(originOf(g) ?: return false)
    }

    @Suppress("UNCHECKED_CAST")
    private fun originOf(g: Group<S>): Group<T>? {
        if (g !is IsomorphismedGroup<*, *>) {
            return null
        }
        val gOrigin = g.group
        if (gOrigin.calculator.numberClass != group.calculator.numberClass) {
            return null
        }
        return gOrigin as Group<T>
    }

    override fun identity(): S {
        return f(group.identity())
    }

    override fun getCoset(x: S, subGroup: Group<S>, isLeft: Boolean): Coset<S, out Group<S>> {
        val coset = group.getCoset(f.deply(x),
                originOf(subGroup) ?: throw NotASubgroupException(), isLeft)
        @Suppress("UNCHECKED_CAST")
        return IsomorphismedCoset(coset, f, this, subGroup as IsomorphismedGroup<T, S>)
    }

    override fun indexOf(sub: Group<S>): Long {
        return group.indexOf(originOf(sub)!!)
    }

    override fun isConjugate(h1: Group<S>, h2: Group<S>): Boolean {
        return group.isConjugate(originOf(h1)!!, originOf(h2)!!)
    }

    override fun normalizer(h: Group<S>): Group<S> {
        return IsomorphismedGroup(group.normalizer(originOf(h)), f)
    }


    override fun getCosets(h: Group<S>, isLeft: Boolean): MathSet<out Coset<S, out Group<S>>> {
        @Suppress("UNCHECKED_CAST")
        val t = group.getCosets(originOf(h)!!, isLeft) as MathSet<Coset<T, Group<T>>>
        return t.mapTo(cosetMapper)
    }

    override fun conjugateSubgroup(h: Group<S>, x: S): Group<S> {
        return IsomorphismedGroup(group.conjugateSubgroup(originOf(h)!!, f.deply(x)), f)
    }

    override fun isConjugate(g1: S, g2: S): Boolean {
        return group.isConjugate(f.deply(g1), f.deply(g2))
    }

    override fun centralizer(a: S): Group<S> {
        return IsomorphismedGroup(group.centralizer(f.deply(a)), f)
    }

    override fun centralizer(h: Group<S>): Group<S> {
        return IsomorphismedGroup(group.centralizer(originOf(h)), f)
    }

    override fun quotientGroup(h: Group<S>): Group<IsomorphismedCoset<T, S>> {
        //Probably type incompatibility
        @Suppress("UNCHECKED_CAST")
        val re = group.quotientGroup(originOf(h)) as Group<Coset<T, Group<T>>>
        return IsomorphismedGroup(re, cosetMapper)
    }

    override fun quotientGroupAndHomo(h: Group<S>):
            Homomorphism<S,
                    IsomorphismedCoset<T, S>,
                    IsomorphismedGroup<T, S>,
                    IsomorphismedGroup<Coset<T, Group<T>>, IsomorphismedCoset<T, S>>,
                    IsomorphismedGroup<T, S>> {
        @Suppress("UNCHECKED_CAST")
        val re = group.quotientGroupAndHomo(originOf(h))
                as Homomorphism<T, Coset<T, Group<T>>, Group<T>, Group<Coset<T, Group<T>>>, Group<T>>
        val mapping = re.mapping
        val subCosetMapper = cosetMapper
        val nMapping =
                HomomorphismMapping.fromFunction(f.inverse().andThen(mapping).andThen(subCosetMapper))
        val nQuotient: IsomorphismedGroup<Coset<T, Group<T>>, IsomorphismedCoset<T, S>> =
                IsomorphismedGroup(re.dest, subCosetMapper)
        val nKernel = IsomorphismedGroup(re.kernel, f)

        return HomomorphismImpl(nMapping, this, nQuotient, nKernel)

    }
}

//internal class GroupImpl<T>(gc: GroupCalculator<T>, set: MathSet<T>) : AbstractGroup<T>(gc, set) {
//    override fun getCalculator(): GroupCalculator<T> {
//        return cal
//    }
//
//    override fun index(): Long {
//        return -1
//    }
//}
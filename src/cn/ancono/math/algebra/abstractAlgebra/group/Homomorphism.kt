package cn.ancono.math.algebra.abstractAlgebra.group

import cn.ancono.math.algebra.abstractAlgebra.FiniteGroups
import cn.ancono.math.algebra.abstractAlgebra.HomomorphismMapping
import cn.ancono.math.algebra.abstractAlgebra.IsomorphismMapping
import cn.ancono.math.algebra.abstractAlgebra.structure.Group
import cn.ancono.math.algebra.abstractAlgebra.structure.finite.FiniteGroup


interface Homomorphism<T : Any, S : Any, G : Group<T>, H : Group<S>, K : Group<T>> {
    val mapping: HomomorphismMapping<T, S>
    val origin: G
    val dest: H
    val kernel: K
}

interface Isomorphism<T : Any, S : Any, G : Group<T>, H : Group<S>, K : Group<T>> : Homomorphism<T, S, G, H, K> {
    override val mapping: IsomorphismMapping<T, S>
}

typealias SelfHomomorphism<T, G, K> = Homomorphism<T, T, G, G, K>

typealias SelfIsomorphism<T, G> = Isomorphism<T, T, G, G, FiniteGroup<T>>

/**
 * Describes a homomorphism from a group of type [T] to [S].
 * Created at 2018/10/10 18:31
 * @author  liyicheng
 */
open class HomomorphismImpl<T : Any, S : Any, G : Group<T>, H : Group<S>, K : Group<T>>(
        override val mapping: HomomorphismMapping<T, S>,
        override val origin: G,
        override val dest: H,
        override val kernel: K) : Homomorphism<T, S, G, H, K> {

//    constructor(mapping: HomomorphismMapping<T, S>, origin: G, dest: H)
//            : this(mapping, origin, dest, computeKernel(mapping, origin, dest))

//    companion object {
//        private fun <T : Any, S : Any, G : Group<T>, H : Group<S>> computeKernel(mapping: HomomorphismMapping<T, S>, origin: G, dest: H){
//
//        }
//                MathSet { x: T -> dest.calculator.run { isEqual(mapping(x), identity) } }
//    }
//}
}

/**
 * Describes a isomorphism from a group of type [T] to [S].
 * Created at 2018/10/10 18:40
 * @author  liyicheng
 */
class IsomorphismImpl<T : Any, S : Any, G : Group<T>, H : Group<S>>
(override val mapping: IsomorphismMapping<T, S>, origin: G, dest: H)
    : HomomorphismImpl<T, S, G, H, FiniteGroup<T>>(mapping, origin, dest, FiniteGroups.identityGroup(origin.calculator)),
        Isomorphism<T, S, G, H, FiniteGroup<T>> {


    /**
     * The kernel of a isomorphism is the identity group of [origin].
     */
    override val kernel: FiniteGroup<T>
        get() = super.kernel
//        get() = MathSets.singleton(origin.identity(), origin.calculator)
}

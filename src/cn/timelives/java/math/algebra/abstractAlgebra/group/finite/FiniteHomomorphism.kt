package cn.timelives.java.math.algebra.abstractAlgebra.group.finite

import cn.timelives.java.math.algebra.abstractAlgebra.FiniteGroups
import cn.timelives.java.math.algebra.abstractAlgebra.HomomorphismMapping
import cn.timelives.java.math.algebra.abstractAlgebra.group.Homomorphism
import cn.timelives.java.math.algebra.abstractAlgebra.group.HomomorphismImpl
import cn.timelives.java.math.algebra.abstractAlgebra.structure.Group
import cn.timelives.java.math.algebra.abstractAlgebra.structure.finite.FiniteGroup
import cn.timelives.java.math.function.invoke
import cn.timelives.java.math.set.FiniteSet
import cn.timelives.java.math.set.MathSet
import cn.timelives.java.math.set.MathSets


/*
 * Created at 2018/10/13 19:35
 * @author  liyicheng
 */
open class FiniteHomomorphism<T : Any, S : Any, G : FiniteGroup<T>, H : FiniteGroup<S>, K : FiniteGroup<T>>(
        mapping: HomomorphismMapping<T, S>,
        origin: G,
        dest: H,
        override val kernel: K) : HomomorphismImpl<T, S, G, H,K>(mapping, origin, dest, kernel){

//    constructor(mapping: HomomorphismMapping<T, S>,origin: G,dest: H)
//            : this(mapping,origin,dest,computeKernel(mapping, origin, dest))

    companion object {

        public fun <T : Any, S : Any, G : FiniteGroup<T>, H : FiniteGroup<S>> of(mapping: HomomorphismMapping<T, S>,
                                                                                 origin: G,
                                                                                 dest: H) : FiniteHomomorphism<T,S,G,H,FiniteGroup<T>>{
            val kernel = computeKernel(mapping,origin, dest)
            return FiniteHomomorphism(mapping,origin, dest, kernel)
        }


        private fun <T : Any, S : Any, G : FiniteGroup<T>, H : FiniteGroup<S>>
                computeKernel(mapping: HomomorphismMapping<T, S>,origin: G,dest: H) : FiniteGroup<T>{
            val destGC = dest.calculator
            val list = origin.set.filter { x -> destGC.isEqual(mapping.invoke(x),destGC.identity) }
            return FiniteGroups.createGroup(origin.calculator,list)
        }
    }
}

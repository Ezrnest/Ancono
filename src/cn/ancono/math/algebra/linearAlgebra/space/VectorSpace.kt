package cn.ancono.math.algebra.linearAlgebra.space

import cn.ancono.math.MathCalculator
import cn.ancono.math.MathObject
import cn.ancono.math.MathObjectExtend
import cn.ancono.math.algebra.abstractAlgebra.calculator.LinearSpaceCalculator
import cn.ancono.math.algebra.linearAlgebra.Vector
import cn.ancono.math.algebra.linearAlgebra.VectorBase
import cn.ancono.math.numberModels.api.FlexibleNumberFormatter
import cn.ancono.math.set.MathSet
import java.util.function.Function


/*
 * Created by liyicheng at 2020-03-06 11:48
 */
/**
 * Describes a vector space on of vectors of type [T].
 * @author liyicheng
 */
class VectorSpace<T : Any>(val vectorBase: VectorBase<T>) : MathObjectExtend<T>(vectorBase.mathCalculator),
        IVectorSpace<T> {

    operator fun contains(v: Vector<T>): Boolean {
        return vectorBase.canReduce(v)
    }

    override fun getCalculator(): VectorSpaceCalculator<T> {
        TODO()
    }

    override fun getSet(): MathSet<Vector<T>> {
        return MathSet { v ->
            vectorBase.canReduce(v)
        }
    }

    override val basis: List<Vector<T>>
        get() = vectorBase.vectors

//    override fun getSet(): MathSet<Vector<T>> {
//
//    }


    override fun <N : Any> mapTo(mapper: Function<T, N>, newCalculator: MathCalculator<N>): MathObject<N> {
        return VectorSpace(vectorBase.mapTo(mapper, newCalculator))
    }

    override fun valueEquals(obj: MathObject<T>): Boolean {
        if (obj !is VectorSpace) {
            return false
        }
        val b2 = obj.vectorBase
        return vectorBase.equivalentTo(b2)
    }

    override fun toString(nf: FlexibleNumberFormatter<T, MathCalculator<T>>): String {
        return vectorBase.toString(nf)
    }
}
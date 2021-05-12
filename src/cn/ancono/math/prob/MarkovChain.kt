package cn.ancono.math.prob

import cn.ancono.math.AbstractMathObject
import cn.ancono.math.IMathObject
import cn.ancono.math.algebra.abs.calculator.AbelGroupCal
import cn.ancono.math.algebra.abs.calculator.EqualPredicate
import cn.ancono.math.algebra.linear.Matrix
import cn.ancono.math.algebra.linear.Vector
import cn.ancono.math.numberModels.api.NumberFormatter
import cn.ancono.math.numberModels.api.indices
import java.util.function.Function


/*
 * Created by liyicheng at 2021-05-12 20:32
 */

typealias Time = Int

interface MarkovSpace<R> : ProbSpace<Realization<R>> {

    override fun randomPoint(): Realization<R> {
        return CachedIntFunction(randomPointInitial(), this::randomPointFromPrev)
    }

    val randomPointSeq: Sequence<R>
        get() = generateSequence(this::randomPointInitial, this::randomPointFromPrev)


    fun randomPointInitial(): R

    fun randomPointFromPrev(x: R): R

    fun randomPointAt(t: Time): R {
        return randomPointSeq.elementAt(t)
    }

}

internal class CachedIntFunction<R>(x0: R, val nextFunction: (R) -> R) : Function<Int, R> {
    val data = arrayListOf<R>()

    init {
        data += x0
    }

    override fun apply(t: Int): R {
        if (t >= data.size) {
            var x = data.last()
            repeat(t - data.size + 1) {
                x = nextFunction(x)
                data += x
            }
        }
        return data[t]
    }
}

typealias State = Int

class MarkovSpaceWithTransition(val p: Matrix<Double>, val initial: Vector<Double>)
    : AbstractProbSpace<Function<Time, State>>(), MarkovSpace<State> {
    //TODO cdf
    val n = p.row

    init {
        require(p.isSquare())
        require(n == initial.size)
    }


    protected fun categorical(v: Vector<Double>): Int {
        var r = rd.nextDouble()
        for (i in v.indices) {
            r -= v[i]
            if (r <= 0) {
                return i
            }
        }
        return n - 1
    }

    override fun randomPointInitial(): Int {
        return categorical(initial)
    }

    override fun randomPointFromPrev(x: Int): Int {
        return categorical(p.getRow(x))
    }

}


abstract class MarkovChain<R>(override val space: MarkovSpace<R>) : SimpleRV<Realization<R>, Realization<R>>, RandomProcess<R> {
    override fun fromPoint(e: Realization<R>): Realization<R> {
        return e
    }

    override fun sampleAt(t: Int): R {
        return space.randomPointAt(t)
    }

    override fun randomVariableAt(t: Int): RandomVariable<R> {
        return map { r -> r.apply(t) }
    }

    override fun sampleUntil(t: Int): List<R> {
        return space.randomPointSeq.take(t).toList()
    }

    override fun randomVariableUntil(t: Int): RandomVariable<List<R>> {
        return map { r ->
            (0 until t).map { r.apply(it) }
        }
    }

    override fun sampleAsSeq(): Sequence<R> {
        return space.randomPointSeq
    }

    override fun randomVariableSeq(): RandomVariable<Sequence<R>> {
        val process = this
        return object : SimpleRV<Realization<R>, Sequence<R>> {
            override val space: ProbSpace<Realization<R>>
                get() = process.space

            override fun fromPoint(e: Realization<R>): Sequence<R> {
                return (0..Int.MAX_VALUE).asSequence().map { e.apply(it) }
            }

            override fun sample(): Sequence<R> {
                return process.sampleAsSeq()
            }
        }
    }


}


class RandomWalkSpace<R>(mc: AbelGroupCal<R>, val s0: R, val sampler: () -> R)
    : AbstractMathObject<R, AbelGroupCal<R>>(mc), MarkovSpace<R> {

    override fun randomPointInitial(): R {
        return s0
    }

    override fun randomPointFromPrev(x: R): R {
        val z = sampler()
        return calculator.add(x, z)
    }

    override fun toString(nf: NumberFormatter<R>): String {
        return "Random Walk Space"
    }

    override fun valueEquals(obj: IMathObject<R>): Boolean {
        return this === obj
    }

    override fun <N> mapTo(newCalculator: EqualPredicate<N>, mapper: Function<R, N>): RandomWalkSpace<N> {
        return RandomWalkSpace(newCalculator as AbelGroupCal<N>, mapper.apply(s0)) {
            mapper.apply(sampler())
        }
    }
}

class RandomWalk<R>(val s0: R, val X: RandomVariable<R>, val mc: AbelGroupCal<R>)
    : MarkovChain<R>(RandomWalkSpace(mc, s0, X::sample)) {

}
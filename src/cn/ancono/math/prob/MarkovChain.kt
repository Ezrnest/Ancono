package cn.ancono.math.prob

import cn.ancono.math.AbstractMathObject
import cn.ancono.math.IMathObject
import cn.ancono.math.algebra.abs.calculator.AbelGroupCal
import cn.ancono.math.algebra.abs.calculator.EqualPredicate
import cn.ancono.math.algebra.linear.Matrix
import cn.ancono.math.algebra.linear.Vector
import cn.ancono.math.numberModels.api.NumberFormatter
import cn.ancono.utilities.ArraySup
import java.util.function.Function
import java.util.function.IntToDoubleFunction


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

    fun startFrom(r: R): MarkovSpace<R> {
        return MarkovSpaceStartFrom(r, this)
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

/**
 *
 */
class MarkovSpaceWithTransition
private constructor(val initialCDF: DoubleArray, val transCDF: List<DoubleArray>)
    : AbstractProbSpace<Function<Time, State>>(), MarkovSpace<State> {
    val n: Int
        get() = initialCDF.size


    protected fun categorical(cdf: DoubleArray): Int {
        val r = rd.nextDouble()
        val idx = ArraySup.binarySearchCeiling(cdf, 0, cdf.size, r)
        return idx.coerceAtMost(cdf.lastIndex)
    }

    override fun randomPointInitial(): Int {
        return categorical(initialCDF)
    }

    override fun randomPointFromPrev(x: Int): Int {
        return categorical(transCDF[x])
    }

    override fun startFrom(r: State): MarkovSpace<State> {
        val dirac = DoubleArray(n)
        dirac[r] = 1.0
        return MarkovSpaceWithTransition(dirac, transCDF)
    }

    companion object {

        fun of(initial: Vector<Double>, p: Matrix<Double>): MarkovSpaceWithTransition {
            val initialCDF = makeCDF(initial)
            val transCDF = p.rowVectors().map { makeCDF(it) }
            return MarkovSpaceWithTransition(initialCDF, transCDF)
        }

        fun makeCDF(v: Vector<Double>): DoubleArray {
            val result = DoubleArray(v.size + 1)
            result[0] = v[0]
            for (i in 1 until v.size) {
                result[i] = v[i] + result[i - 1]
            }
            return result
        }
    }

}

class MarkovSpaceStartFrom<R>(val initial: R, val space: MarkovSpace<R>) : MarkovSpace<R> {
    override fun randomPointInitial(): R {
        return initial
    }

    override fun randomPointFromPrev(x: R): R {
        return space.randomPointFromPrev(x)
    }
}

/**
 * A markov chain is a stochastic process that has the markov property:
 * > E(X_{n+1} | F_n) = E(X_{n+1} | X_n)
 */
open class MarkovChain<R>(override val space: MarkovSpace<R>) : StochasticProcess<R> {
    override fun fromPoint(e: Realization<R>): Realization<R> {
        return e
    }

    override fun sampleAt(t: Int): R {
        return space.randomPointAt(t)
    }

    override fun rvAt(t: Int): RandomVariable<R> {
        return map { r -> r.apply(t) }
    }

    override fun sampleTo(t: Int): List<R> {
        return space.randomPointSeq.take(t + 1).toList()
    }

    override fun randomVariableTo(t: Int): RandomVariable<List<R>> {
        return map { r ->
            (0..t).map { r.apply(it) }
        }
    }

    override fun sampleAsSeq(): Sequence<R> {
        return space.randomPointSeq
    }


    /**
     * Returns a markov chain with the same transition probability that starts from [r].
     */
    fun startFrom(r: R): MarkovChain<R> {
        return MarkovChain(space.startFrom(r))
    }
}


class RandomWalkSpace<R>(mc: AbelGroupCal<R>, val s0: R, val sampler: () -> R)
    : AbstractMathObject<R, AbelGroupCal<R>>(mc), MarkovSpace<R> {

    override fun startFrom(r: R): MarkovSpace<R> {
        return RandomWalkSpace(calculator, r, sampler)
    }

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

class BranchingSpace(val sampler: () -> Int)
    : MarkovSpace<State> {
    override fun randomPointInitial(): Int {
        return 1
    }

    override fun randomPointFromPrev(x: Int): Int {
        return (0 until x).sumOf { sampler() }
    }
}

class BirthDeathSpace(val p: IntToDoubleFunction, val q: IntToDoubleFunction)
    : AbstractProbSpace<Function<Time, State>>(), MarkovSpace<State> {
    override fun randomPointInitial(): State {
        return 1
    }

    override fun randomPointFromPrev(x: State): State {
        var d = rd.nextDouble()
        val px = p.applyAsDouble(x)
        if (d < px) {
            return x + 1
        }
        if (d < px + q.applyAsDouble(x)) {
            return x - 1
        }
        return x
    }

    companion object {

        fun of(p: Double, q: Double): BirthDeathSpace {
            val pf = IntToDoubleFunction { p }
            val qf = IntToDoubleFunction { x ->
                if (x <= 0) {
                    0.0
                } else {
                    q
                }
            }
            return BirthDeathSpace(pf, qf)
        }


    }

}
//class RandomWalk<R>(val s0: R, val X: RandomVariable<R>, val mc: AbelGroupCal<R>)
//    : MarkovChain<R>(RandomWalkSpace(mc, s0, X::sample)) {
//}
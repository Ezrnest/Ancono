package cn.ancono.math.prob

import cn.ancono.math.numeric.NumericSup
import java.util.*
import java.util.function.DoubleUnaryOperator


typealias DoubleRV = RandomVariable<Double>
typealias IntRV = RandomVariable<Int>

/*
 * Created by liyicheng at 2020-06-02 17:14
 */
operator fun DoubleRV.plus(y: DoubleRV): DoubleRV {
    return MappedRV.binary(this, y, Double::plus)
}

operator fun DoubleRV.minus(y: DoubleRV): DoubleRV {
    return MappedRV.binary(this, y, Double::minus)
}

operator fun DoubleRV.times(y: DoubleRV): DoubleRV {
    return MappedRV.binary(this, y, Double::times)
}

operator fun DoubleRV.div(y: DoubleRV): DoubleRV {
    return MappedRV.binary(this, y, Double::div)
}

fun <T, R> RandomVariable<T>.map(f: (T) -> R): RandomVariable<R> {
    return UnaryMappedRV(this, f)
}

operator fun DoubleRV.unaryMinus(): DoubleRV {
    return this.map(Double::unaryMinus)
}

object RandomVariables {

    /**
     * Returns a random variable of uniform distribution.
     */
    fun uniform(lower: Double = 0.0, upper: Double = 1.0): DoubleRV {
        return UniformDist(IntervalSpace(lower, upper))
    }

    /**
     * Returns a random variable of Bernoulli distribution:
     *
     *     P{ X = 0 } = p
     *     P{ X = 1 } = 1-p
     */
    fun bernoulli(p: Double = 0.5): RandomVariable<Int> {
        return BernoulliDist(BernoulliSpace(p))
    }

    /**
     * Returns a random variable of geometry distribution:
     *
     *     P{ X = n } = q^(n-1) * p    ( n > 0 )
     */
    fun geometry(p: Double = 0.5): RandomVariable<Int> {
        return GeometryDist(PascalSpace(p))
    }

    /**
     * Returns a random variable of normal distribution.
     */
    fun normal(a: Double = 0.0, sigma: Double = 1.0): DoubleRV {
        return NormalDist(a, sigma, StandardNormalDistSpace())
    }


    /**
     * Returns a list of independent identically distributed random variables.
     */
    fun <T, E : Any> iid(x: SimpleRV<E, T>, n: Int): List<SimpleRV<List<E>, T>> {
        val space = ProductSpace(Collections.nCopies(n, x.space))

        return (0 until n).map { i ->
            object : SimpleRV<List<E>, T>() {
                override val space: ProbSpace<List<E>> = space

                override fun fromPoint(e: List<E>): T {
                    return x.fromPoint(e[i])
                }
            }
        }
    }

    /**
     * Returns a new simple random variable that has the same distribution as the given random variable, and the
     * new one is based on a independent probability space.
     */
    fun <T> copyToSimple(x: RandomVariable<T>): SimpleRV<Event, T> {
        val space = BundledSpace(x.spaces)
        return object : SimpleRV<Event, T>() {
            override val space: ProbSpace<Event> = space

            override fun fromPoint(e: Event): T {
                return x.fromEvent(e)
            }
        }
    }

    /**
     * Returns a sequence(not limited) of independent identically distributed random variables.
     */
    fun <T> iid(x: RandomVariable<T>): Sequence<RandomVariable<T>> = generateSequence {
        copyToSimple(x)
    }

    /**
     * Returns a list of independent identically distributed random variables.
     */
    fun <T> iid(x: RandomVariable<T>, n: Int): List<RandomVariable<T>> = (0 until n).map {
        copyToSimple(x)
    }

    /**
     * Maps the given random variables to a new random variable.
     */
    fun <T, R> map(rvs: List<RandomVariable<T>>, f: (List<T>) -> R): RandomVariable<R> {
        return MappedRV(rvs, f)
    }

    fun sum(rvs: List<DoubleRV>): DoubleRV {
        return map(rvs) { it.sum() }
    }


    fun average(rvs: List<DoubleRV>): DoubleRV {
        return map(rvs) { it.average() }
    }

    fun estimateExpectation(x: DoubleRV, times: Int = 10000): Double {
        return x.getAsSequence().take(times).average()
    }

    fun estimateDensity(x: DoubleRV,
                        from: Double, to: Double, blockCount: Int = 100,
                        times: Int = 1000000): DoubleUnaryOperator {
        require(to > from)
        val interval = from..to
        val counting = IntArray(blockCount)
        val d = (to - from) / blockCount

        repeat(times) {
            val a = x.get()
            if (a in interval) {
                val i = ((a - from) / d).toInt()
                counting[i]++
            }
        }
        val data = DoubleArray(counting.size) { i -> counting[i].toDouble() / times / d }

        return NumericSup.linearInterpolate(from, to, data, d)
    }

}
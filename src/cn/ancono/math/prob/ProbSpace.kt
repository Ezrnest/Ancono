package cn.ancono.math.prob

import java.util.*
import kotlin.collections.HashMap
import kotlin.math.exp
import kotlin.math.ln


typealias Event = Map<ProbSpace<*>, Any>

/*
 * Created by liyicheng at 2020-06-02 15:07
 */
interface ProbSpace<out E : Any> {
    fun randomPoint(): E

    fun randomPoints(): Sequence<E> {
        return generateSequence { this.randomPoint() }
    }
}


class ProductSpace<E : Any, S : ProbSpace<E>>(val spaces: List<S>) : ProbSpace<List<E>> {
    override fun randomPoint(): List<E> {
        return spaces.map { it.randomPoint() }
    }
}

//class ComposedSpace(val spaces: Set<AtomicSpace<*>>) : ProbSpace<ComposedPoint>() {
//    override fun randomPoint(): ComposedPoint {
//        val events = HashMap<ProbSpace<*>, Any>(spaces.size)
//        for (space in spaces) {
//            events[space] = space.randomPoint()
//        }
//        return events
//    }
//
//    companion object {
//        fun composeOf(spaces: Set<ProbSpace<*>>): ComposedSpace {
//            val s = spaces.flatMapTo(hashSetOf()) {
//                when (it) {
//                    is AtomicSpace<*> -> listOf(it)
//                    is ComposedSpace -> it.spaces
//                }
//            }
//            return ComposedSpace(s)
//        }
//    }
//}

/**
 * A bundled space that is independent to the original spaces.
 */
class BundledSpace(val spaces: Set<ProbSpace<*>>) : ProbSpace<Event> {
    override fun randomPoint(): Event {
        val result = HashMap<ProbSpace<*>, Any>(spaces.size)
        for (space in spaces) {
            result[space] = space.randomPoint()
        }
        return result
    }
}

abstract class AbstractProbSpace<out E : Any> : ProbSpace<E> {
    val rd = Random()
}

class IntervalSpace(val lower: Double, val upper: Double) : AbstractProbSpace<Double>() {
    init {
        require(lower < upper)
    }

    val k = upper - lower
    override fun randomPoint(): Double {
        return k * rd.nextDouble() + lower
    }
}

class StandardNormalDistSpace() : AbstractProbSpace<Double>() {
    override fun randomPoint(): Double {
        return rd.nextGaussian()
    }
}

object TrivialSpace : ProbSpace<Unit> {
    override fun randomPoint() {
    }
}

/**
 * A probability space of only `0` and `1`. The probability of `1` is equal to [p].
 */
class BernoulliSpace(val p: Double) : AbstractProbSpace<Int>() {

    init {
        require(p in 0.0..1.0)
    }

    override fun randomPoint(): Int {
        return if (rd.nextDouble() <= p) {
            1
        } else {
            0
        }
    }
}

class BinomialSpace(val p: Double, val n: Int) : AbstractProbSpace<Int>() {
    init {
        require(p in 0.0..1.0)
        require(n > 0)
    }

    override fun randomPoint(): Int {
        var i = 0
        repeat(n) {
            if (rd.nextDouble() <= p) {
                i++
            }
        }
        return i
    }
}

/**
 * A probability space of positive integers. The possibility of an integer `n` is equal to `pq^(n-1)`,
 * where `q = 1-p`.
 */
class GeomSpace(val p: Double) : AbstractProbSpace<Int>() {

    init {
        require(p in 0.0..1.0)
    }

    override fun randomPoint(): Int {
        var i = 1
        while (rd.nextDouble() > p) {
            i++
        }
        return i
    }
}

/**
 * A probability space of positive integers. The possibility of an integer `n` is equal to `pq^(n-1)`,
 * where `q = 1-p`.
 */
class PascalSpace(val p: Double, val n: Int) : AbstractProbSpace<Int>() {

    init {
        require(p in 0.0..1.0)
        require(n > 0)
    }

    override fun randomPoint(): Int {
        var i = 0
        repeat(n) {
            do {
                i++
            } while (rd.nextDouble() > p)
        }
        return i
    }
}

/**
 * A probability space of non-negative integers.
 *
 *     P(n) = k^n / n! * e^(-k)     (n>=0)
 */
class PoissonSpace(val k: Double) : AbstractProbSpace<Int>() {
    init {
        require(k > 0)
    }

    private val base = exp(-k)

    override fun randomPoint(): Int {
        val d = rd.nextDouble()
        var total = base
        var current = base
        var i = 0
        while (total < d) {
            i++
            current = current * k / i
            total += current
        }
        return i
    }
}

/**
 * A probability space of positive real numbers, density is
 *
 *     p(x) = e^(-x)
 */
class StandardExpSpace() : AbstractProbSpace<Double>() {

    override fun randomPoint(): Double {
        val d = rd.nextDouble()
        return -ln(d)
    }
}

//interface AtomicProbSpace<E : Any> : ProbSpace<E> {
//
//}
//
//interface ComposedProbSpace : ProbSpace<List<Any>> {
//
//}


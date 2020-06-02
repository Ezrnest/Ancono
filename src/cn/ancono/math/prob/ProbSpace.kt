package cn.ancono.math.prob

import java.util.*
import kotlin.collections.HashMap


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


class ProductSpace<S : Any>(val spaces: List<ProbSpace<S>>) : ProbSpace<List<S>> {
    override fun randomPoint(): List<S> {
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
 * A probability space of only `true` and `false`. The probability of `true` is equal to [p].
 */
class BernoulliSpace(val p: Double) : AbstractProbSpace<Boolean>() {

    init {
        require(p in 0.0..1.0)
    }

    override fun randomPoint(): Boolean {
        return rd.nextDouble() <= p
    }
}

/**
 * A probability space of positive integers. The possibility of an integer `n` is equal to `pq^(n-1)`,
 * where `q = 1-p`.
 */
class PascalSpace(val p: Double) : AbstractProbSpace<Int>() {

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


//interface AtomicProbSpace<E : Any> : ProbSpace<E> {
//
//}
//
//interface ComposedProbSpace : ProbSpace<List<Any>> {
//
//}


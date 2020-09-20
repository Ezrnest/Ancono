package cn.ancono.math.prob


/**
 * Describes a random variable that is based on a probability space.
 *
 *
 *
 * Created by liyicheng at 2020-06-01 10:54
 *
 * @see ProbSpace
 * @see RandomVariables
 */
sealed class RandomVariable<out T> {

    /**
     * A set of all the probability spaces that this random variable depends on.
     */
    abstract val spaces: Set<ProbSpace<*>>

    /**
     * Returns the value that corresponds to the random event, that is, a
     * map containing required probability spaces and random points.
     *
     * This method should return the same for the same input.
     */
    abstract fun fromEvent(event: Event): T

    /**
     * Randomly gets a value of this random variable according to the
     * probability.
     */
    abstract fun get(): T
}

fun <T : Any> RandomVariable<T>.getAsSequence(): Sequence<T> = generateSequence { this.get() }

abstract class SimpleRV<E : Any, out T>() : RandomVariable<T>() {
    abstract val space: ProbSpace<E>

    override val spaces: Set<ProbSpace<*>>
        get() = setOf(space)

    override fun get(): T {
        return fromPoint(space.randomPoint())
    }

    override fun fromEvent(event: Event): T {
        @Suppress("UNCHECKED_CAST")
        val e = event[space] as E
        return fromPoint(e)
    }

    abstract fun fromPoint(e: E): T

}

abstract class ComposedRV<out T>() : RandomVariable<T>() {

    abstract val rvs: List<RandomVariable<*>>


    override fun get(): T {
        val result = HashMap<ProbSpace<*>, Any>(spaces.size)
        for (space in spaces) {
            result[space] = space.randomPoint()
        }
        return fromEvent(result)
    }
}


class UnaryMappedRV<T, out R>(val rv: RandomVariable<T>, val f: (T) -> R) : ComposedRV<R>() {
    override val spaces: Set<ProbSpace<*>>
        get() = rv.spaces

    override val rvs: List<RandomVariable<*>> = listOf(rv)


    override fun fromEvent(event: Event): R {
        return f(rv.fromEvent(event))
    }

    override fun get(): R {
        return f(rv.get())
    }
}

class MappedRV<out T, S>(override val rvs: List<RandomVariable<S>>, val mapping: (List<S>) -> T) : ComposedRV<T>() {
    override val spaces: Set<ProbSpace<*>> = rvs.flatMapTo(hashSetOf()) { rv ->
        rv.spaces
    }

    override fun fromEvent(event: Event): T {
        val result = rvs.map { it.fromEvent(event) }
        return mapping(result)
    }

    companion object {
        fun <T, S, R> binary(x: RandomVariable<T>, y: RandomVariable<S>, f: (T, S) -> R): RandomVariable<R> {
            return MappedRV(listOf(x, y)) { list ->
                @Suppress("UNCHECKED_CAST")
                f(list[0] as T, list[1] as S)
            }
        }


    }
}


class ContanceDist<out T>(val c: T) : SimpleRV<Unit, T>() {
    override fun fromPoint(e: Unit): T {
        return c
    }

    override val space: ProbSpace<Unit>
        get() = TrivialSpace
}

/**
 * A random variable that simply returns the event.
 */
class IdentityVariable<E : Any, S : ProbSpace<E>>(override val space: S) : SimpleRV<E, E>() {
    override fun fromPoint(e: E): E {
        return e
    }
}
//
//class UniformDist(override val space: IntervalSpace) : SimpleRV<Double, Double>() {
//    val lower: Double
//        get() = space.lower
//
//    val upper: Double
//        get() = space.upper
//
//    override fun fromPoint(e: Double): Double {
//        return e
//    }
//}

class NormalDist(val a: Double, val sigma: Double, override val space: StandardNormalDistSpace) : SimpleRV<Double, Double>() {
    init {
        require(sigma > 0)
    }

    override fun fromPoint(e: Double): Double {
        return sigma * e + a
    }
}

//class BernoulliDist(override val space: BernoulliSpace) : SimpleRV<Boolean, Int>() {
//    override fun fromPoint(e: Boolean): Int {
//        return if (e) {
//            1
//        } else {
//            0
//        }
//    }
//}
//
//class GeometryDist(override val space: GeomSpace) : SimpleRV<Int, Int>() {
//    override fun fromPoint(e: Int): Int {
//        return e
//    }
//}
//
//class BinomialDist(override val space: BinomialSpace) : SimpleRV<Int, Int>() {
//    override fun fromPoint(e: Int): Int {
//        return e
//    }
//}
//
//class PascalDist(override val space: PascalSpace) : SimpleRV<Int, Int>() {
//    override fun fromPoint(e: Int): Int {
//        return e
//    }
//}
//
//class PoissonDist(override val space: PoissonSpace) : SimpleRV<Int,Int>(){
//    override fun fromPoint(e: Int): Int {
//        return e
//    }
//}


class ExpDist(val k: Double, override val space: StandardExpSpace) : SimpleRV<Double, Double>() {
    override fun fromPoint(e: Double): Double {
        return e / k
    }
}


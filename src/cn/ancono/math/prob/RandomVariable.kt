package cn.ancono.math.prob


/*
 * Created by liyicheng at 2020-06-01 10:54
 */
sealed class RandomVariable<out T> {
    abstract val spaces: Set<ProbSpace<*>>

    abstract fun fromEvent(event: Event): T

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


class ContanceDist<out T>(val c: T) : SimpleRV<Unit, T>() {
    override fun fromPoint(e: Unit): T {
        return c
    }

    override val space: ProbSpace<Unit>
        get() = TrivialSpace
}


class UniformDist(override val space: IntervalSpace) : SimpleRV<Double, Double>() {
    val lower: Double
        get() = space.lower

    val upper: Double
        get() = space.upper

    override fun fromPoint(e: Double): Double {
        return e
    }
}

class NormalDist(val a: Double, val sigma: Double, override val space: StandardNormalDistSpace) : SimpleRV<Double, Double>() {
    init {
        require(sigma > 0)
    }

    override fun fromPoint(e: Double): Double {
        return sigma * e + a
    }
}

class BernoulliDist(override val space: BernoulliSpace) : SimpleRV<Boolean, Int>() {
    override fun fromPoint(e: Boolean): Int {
        return if (e) {
            1
        } else {
            0
        }
    }
}

class GeometryDist(override val space: PascalSpace) : SimpleRV<Int, Int>() {
    override fun fromPoint(e: Int): Int {
        return e
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
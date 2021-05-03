package cn.ancono.math.prob

import cn.ancono.math.MathCalculator
import cn.ancono.math.exceptions.UnsupportedCalculationException
import cn.ancono.math.numberModels.MathCalculatorAdapter


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

    companion object{
        /**
         * Returns a calculator of random variables.
         *
         * Note that the calculator can not support methods `isEqual` and `compare`.
         */
        fun <T> getCalculator(mc: MathCalculator<T>): RVCalculator<T> {
            return RVCalculator(mc)
        }
    }
}

fun <T> RandomVariable<T>.getAsSequence(): Sequence<T> = generateSequence { this.get() }

/**
 * A simple random variable is only involved with single probability space.
 */
abstract class SimpleRV<E, out T> : RandomVariable<T>() {
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

abstract class ComposedRV<out T> : RandomVariable<T>() {

    abstract val rvs: List<RandomVariable<*>>


    override fun get(): T {
        val result = HashMap<ProbSpace<*>, Any?>(spaces.size)
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


class ConstantRV<out T>(val c: T) : SimpleRV<Unit, T>() {
    override fun fromPoint(e: Unit): T {
        return c
    }

    override val space: ProbSpace<Unit>
        get() = TrivialSpace
}

/**
 * A random variable that simply returns the event.
 */
class IdentityRV<E, S : ProbSpace<E>>(override val space: S) : SimpleRV<E, E>() {
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

class NormalRV(val a: Double, val sigma: Double, override val space: StandardNormalDistSpace) : SimpleRV<Double, Double>() {
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


class ExpRV(val k: Double, override val space: StandardExpSpace) : SimpleRV<Double, Double>() {
    override fun fromPoint(e: Double): Double {
        return e / k
    }
}

/**
 * A math calculator for random variable.
 *
 * Note that the calculator can not support methods `isEqual` and `compare`.
 */
class RVCalculator<T>(val mc: MathCalculator<T>) : MathCalculatorAdapter<RandomVariable<T>>() {
    override val one: RandomVariable<T> = ConstantRV(mc.one)
    override val zero: RandomVariable<T> = ConstantRV(mc.zero)

    override fun isZero(x: RandomVariable<T>): Boolean {
        if (x == zero) {
            return true
        }
        throw UnsupportedCalculationException()
    }

    override fun isEqual(x: RandomVariable<T>, y: RandomVariable<T>): Boolean {
        if (x == y) {
            return true
        }
        throw UnsupportedCalculationException()
    }

    override fun compare(x: RandomVariable<T>, y: RandomVariable<T>): Int {
        throw UnsupportedCalculationException()
    }

    override val isComparable: Boolean
        get() = false

    override fun add(x: RandomVariable<T>, y: RandomVariable<T>): RandomVariable<T> {
        return RandomVariables.map2(x, y, mc::add)
    }

    override fun negate(x: RandomVariable<T>): RandomVariable<T> {
        return x.map(mc::negate)
    }

    override fun abs(x: RandomVariable<T>): RandomVariable<T> {
        return x.map(mc::abs)
    }

    override fun subtract(x: RandomVariable<T>, y: RandomVariable<T>): RandomVariable<T> {
        return RandomVariables.map2(x, y, mc::subtract)
    }

    override fun multiply(x: RandomVariable<T>, y: RandomVariable<T>): RandomVariable<T> {
        return RandomVariables.map2(x, y, mc::multiply)
    }

    override fun divide(x: RandomVariable<T>, y: RandomVariable<T>): RandomVariable<T> {
        return RandomVariables.map2(x, y, mc::divide)
    }

    override fun multiplyLong(x: RandomVariable<T>, n: Long): RandomVariable<T> {
        return x.map { mc.multiplyLong(it, n) }
    }

    override fun divideLong(x: RandomVariable<T>, n: Long): RandomVariable<T> {
        return x.map { mc.divideLong(it, n) }
    }

    override fun reciprocal(x: RandomVariable<T>): RandomVariable<T> {
        return x.map(mc::reciprocal)
    }

    override fun squareRoot(x: RandomVariable<T>): RandomVariable<T> {
        return x.map(mc::squareRoot)
    }

    override fun pow(x: RandomVariable<T>, n: Long): RandomVariable<T> {
        return x.map { mc.pow(it, n) }
    }

    override fun exp(a: RandomVariable<T>, b: RandomVariable<T>): RandomVariable<T> {
        return RandomVariables.map2(a, b, mc::exp)
    }

    override fun log(a: RandomVariable<T>, b: RandomVariable<T>): RandomVariable<T> {
        return RandomVariables.map2(a, b, mc::log)
    }

    override fun cos(x: RandomVariable<T>): RandomVariable<T> {
        return x.map(mc::cos)
    }

    override fun tan(x: RandomVariable<T>): RandomVariable<T> {
        return x.map(mc::tan)
    }

    override fun arccos(x: RandomVariable<T>): RandomVariable<T> {
        return x.map(mc::arccos)
    }

    override fun arctan(x: RandomVariable<T>): RandomVariable<T> {
        return x.map(mc::arctan)
    }

    override fun nroot(x: RandomVariable<T>, n: Long): RandomVariable<T> {
        return x.map {
            mc.nroot(it, n)
        }
    }

    override fun constantValue(name: String): RandomVariable<T> {
        return ConstantRV(mc.constantValue(name)!!)
    }

    override fun exp(x: RandomVariable<T>): RandomVariable<T> {
        return x.map(mc::exp)
    }

    override fun ln(x: RandomVariable<T>): RandomVariable<T> {
        return x.map(mc::ln)
    }

    override fun sin(x: RandomVariable<T>): RandomVariable<T> {
        return x.map(mc::sin)
    }

    override fun arcsin(x: RandomVariable<T>): RandomVariable<T> {
        return x.map(mc::arcsin)
    }


    override val numberClass: Class<RandomVariable<T>>
        @Suppress("UNCHECKED_CAST")
        get() = RandomVariable::class.java as Class<RandomVariable<T>>

    override fun RandomVariable<T>.div(y: RandomVariable<T>): RandomVariable<T> {
        return divide(this, y)
    }

    override fun RandomVariable<T>.div(y: Long): RandomVariable<T> {
        return divideLong(this, y)
    }

    override fun RandomVariable<T>.times(y: RandomVariable<T>): RandomVariable<T> {
        return multiply(this, y)
    }

    override fun Long.times(x: RandomVariable<T>): RandomVariable<T> {
        return multiplyLong(x, this)
    }

    override fun RandomVariable<T>.times(n: Long): RandomVariable<T> {
        return multiplyLong(this, n)
    }

    override fun RandomVariable<T>.unaryMinus(): RandomVariable<T> {
        return negate(this)
    }

    override fun RandomVariable<T>.minus(y: RandomVariable<T>): RandomVariable<T> {
        return subtract(this, y)
    }

    override fun RandomVariable<T>.plus(y: RandomVariable<T>): RandomVariable<T> {
        return add(this, y)
    }

    override fun RandomVariable<T>.compareTo(y: RandomVariable<T>): Int {
        return compare(this, y)
    }
}
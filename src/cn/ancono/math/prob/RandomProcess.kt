package cn.ancono.math.prob

import cn.ancono.math.algebra.abs.calculator.AbelGroupCal
import cn.ancono.math.numberModels.Calculators
import java.util.function.Function
import java.util.function.Predicate


/*
 * Created by liyicheng at 2021-05-12 14:45
 */

interface GeneralRandomProcess<T, R> : RandomVariable<Function<T, R>> {

    fun sampleAt(t: T): R

    fun randomVariableAt(t: T): RandomVariable<R>
}

interface DiscreteTimeRandomProcess<R> : GeneralRandomProcess<Int, R> {

    fun sampleUntil(t: Int): List<R>

    fun randomVariableUntil(t: Int): RandomVariable<List<R>>

}

typealias Realization<R> = Function<Time, R>


interface RandomProcess<R> : DiscreteTimeRandomProcess<R> {


    fun sampleAsSeq(): Sequence<R>

    //
    fun randomVariableSeq(): RandomVariable<Sequence<R>>


    fun randomVariableSeqCollected(): RandomVariable<Sequence<List<R>>> {
        return map { f ->
            sequence {
                val list = arrayListOf<R>()
                for (i in 0..Int.MAX_VALUE) {
                    list.add(f.apply(i))
                    yield(list)
                }
            }
        }
    }

    fun stoppingTime(predicate: Predicate<List<R>>): StoppingTime<R> {
        return StoppingTime(this, predicate)
    }

    fun hittingTime(predicate: Predicate<R>): StoppingTime<R> {
        return HittingTime(predicate, this)
    }

    fun hittingTimeOf(r: R): HittingTime<R> {
        return HittingTime(Predicate.isEqual(r), this)
    }

}

open class StoppingTime<R>(
        val process: RandomProcess<R>,
        val predicate: Predicate<List<R>>
) : RandomVariable<Time> {
    override val spaces: Set<ProbSpace<*>>
        get() = process.spaces

    protected open fun fromRealization(r: Realization<R>): Time {
        val list = arrayListOf<R>()
        for (i in 0..Int.MAX_VALUE) {
            list.add(r.apply(i))
            if (predicate.test(list)) {
                return i
            }
        }
        throw ArithmeticException("Stopping time overflow!")
    }

    override fun fromEvent(event: Event): Time {
        val r = process.fromEvent(event)
        return fromRealization(r)
    }

    override fun sample(): Time {
        return fromRealization(process.sample())
    }

    open infix fun min(y: StoppingTime<R>): StoppingTime<R> {
        require(this.process == y.process)
        return StoppingTime(process, predicate.or(y.predicate))
    }

}


class HittingTime<R>(val isHit: Predicate<R>, process: RandomProcess<R>)
    : StoppingTime<R>(process, Predicate { isHit.test(it.last()) }) {
    override fun fromRealization(r: Realization<R>): Time {
        for (i in 0..Int.MAX_VALUE) {
            if (isHit.test(r.apply(i))) {
                return i
            }
        }
        throw ArithmeticException("Stopping time overflow!")
    }

    override fun sample(): Time {
        return process.sampleAsSeq().indexOfFirst { isHit.test(it) }
    }

    override fun min(y: StoppingTime<R>): StoppingTime<R> {
        require(process == y.process)
        if (y is HittingTime) {
            return HittingTime(isHit.or(y.isHit), this.process)
        }
        return super.min(y)
    }
}

interface Martingale<R> : RandomProcess<R>


object RandomProcesses {

    fun <T> randomWalk(x0: T, X: RandomVariable<T>, mc: AbelGroupCal<T>): RandomWalk<T> {
        return RandomWalk(x0, X, mc)
    }
}


fun main() {
    val X = RandomVariables.bernoulli(18.0 / 38).map { it * 2 - 1 }
    val rw = RandomProcesses.randomWalk(20, X, Calculators.integer())
    val T0 = rw.hittingTimeOf(0)
    val T1 = rw.hittingTimeOf(40)
    val T = T0 min T1
    val e = RandomVariables.estimateExpectation(T.map { it.toDouble() })
    println(e)
}
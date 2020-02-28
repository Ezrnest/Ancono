package cn.ancono.math.set

import cn.ancono.math.MathCalculator
import cn.ancono.math.MathObject
import cn.ancono.math.algebra.abstractAlgebra.calculator.eval
import cn.ancono.math.numberModels.api.FlexibleNumberFormatter
import cn.ancono.math.numberTheory.NTCalculator
import java.math.BigInteger
import java.util.function.Function


/*
 * Created at 2018/10/10 10:10
 * @author  liyicheng
 */
class FiniteInterval<T : Any>(override val mathCalculator: NTCalculator<T>, val downer: T, val upper: T)
    : Interval<T>(mathCalculator), FiniteSet<T> {
    override fun contains(n: T): Boolean {
        return mathCalculator.compare(downer, n) <= 0 && mathCalculator.compare(n, upper) <= 0
    }

    override fun upperBound(): T {
        return upper
    }

    override fun isUpperBoundInclusive(): Boolean {
        return true
    }

    override fun downerBound(): T {
        return downer
    }

    override fun isDownerBoundInclusive(): Boolean {
        return true
    }

    override fun lengthOf(): T {
        return mc.subtract(upper, downer)
    }

    override fun downerPart(n: T): Interval<T> {
        require(mathCalculator.eval { n > downer && n < upper })
        return FiniteInterval(mathCalculator, downer, n)
    }

    override fun downerPart(n: T, include: Boolean): Interval<T> {
        require(include)
        return downerPart(n)
    }

    override fun upperPart(n: T): Interval<T> {
        require(mathCalculator.eval { n > downer && n < upper })
        return FiniteInterval(mathCalculator, n, upper)
    }

    override fun upperPart(n: T, include: Boolean): Interval<T> {
        require(include)
        return upperPart(n)
    }

    override fun expandUpperBound(n: T): Interval<T> {
        require(mathCalculator.eval { n > upper })
        return FiniteInterval(mathCalculator, downer, n)
    }

    override fun expandUpperBound(n: T, include: Boolean): Interval<T> {
        require(include)
        return expandUpperBound(n)
    }

    override fun expandDownerBound(n: T): Interval<T> {
        require(mathCalculator.eval { n < downer })
        return FiniteInterval(mathCalculator, n, upper)
    }

    override fun expandDownerBound(n: T, include: Boolean): Interval<T> {
        require(include)
        return expandDownerBound(n)
    }

    override fun sameTypeInterval(downerBound: T, upperBound: T): Interval<T> {
        return FiniteInterval(mathCalculator, downer, upper)
    }

    override fun contains(iv: Interval<T>): Boolean {
        return mathCalculator.eval {
            upper >= iv.upperBound() &&
                    downer <= iv.downerBound()
        }
    }

    override fun intersect(iv: Interval<T>): FiniteInterval<T>? {
        val iL = iv.downerBound()!!
        val iR = iv.upperBound()!!
        val right = upper
        val left = downer
        return if ((mc.compare(right, iL) >= 0) && (mc.compare(iR, left) >= 0)) {
            if (mc.compare(left, iL) < 0) {
                FiniteInterval(mathCalculator, iL, right)
            } else {
                FiniteInterval(mathCalculator, left, iR)
            }
        } else null
    }

    override fun <N : Any> mapTo(mapper: Function<T, N>, newCalculator: MathCalculator<N>): Interval<N> {
        return closedInterval(mapper.apply(downer), mapper.apply(upper), newCalculator)
    }

    override fun toString(): String {
        return "[$downer,$upper]"
    }

    override fun toString(nf: FlexibleNumberFormatter<T, MathCalculator<T>>): String {
        return "[${nf.format(downer, mc)},${nf.format(upper, mc)}]"
    }

    override fun valueEquals(obj: MathObject<T>): Boolean {
        if (obj !is FiniteInterval) {
            return false
        }
        return mc.isEqual(downer, obj.downer) && mc.isEqual(upper, obj.upper)
    }

    override fun listIterator(): MutableListIterator<T> {
        return StepIterator()
    }

    /**
     * An iterator over a progression of values of type `T`.
     * @property step the number by which the value is incremented on each step.
     */
    internal inner class StepIterator : MutableListIterator<T> {
        override fun remove() {
            throw UnsupportedOperationException()
        }

        override fun set(element: T) {
            throw UnsupportedOperationException()
        }

        override fun add(element: T) {
            throw UnsupportedOperationException()
        }

        private var cur = downer


        override fun hasNext(): Boolean {
            return mathCalculator.eval { cur <= upper }
        }

        override fun hasPrevious(): Boolean {
            return mathCalculator.eval { cur > downer }
        }

        override fun next(): T {
            if (hasNext()) {
                val t = cur
                cur = mathCalculator.increase(cur)
                return t
            }
            throw NoSuchElementException()
        }

        override fun nextIndex(): Int {
            if (!hasNext()) {
                throw NoSuchElementException()
            }
            val re = mathCalculator.eval { (cur - downer) / one }
            return mathCalculator.asLong(re).toInt()
        }

        override fun previous(): T {
            if (hasPrevious()) {
                cur = mathCalculator.decrease(cur)
                return cur
            }
            throw NoSuchElementException()
        }

        override fun previousIndex(): Int {
            if (!hasPrevious()) {
                throw NoSuchElementException()
            }
            val re = mathCalculator.eval { (cur - downer) / one }
            return mathCalculator.asLong(re).toInt() - 1
        }

    }

    override fun iterator(): MutableIterator<T> {
        return listIterator()
    }

    override fun size(): Long {
        val re = mathCalculator.eval { (upper - downer) / one }
        return mathCalculator.asLong(re) + 1
    }

    override fun sizeAsBigInteger(): BigInteger {
        val re = mathCalculator.eval { (upper - downer) / one }
        return mathCalculator.asBigInteger(re).inc()
    }

    override fun get(index: Long): T {
        return mathCalculator.eval { downer + index * one }
    }

    override fun get(index: BigInteger): T {
        return mathCalculator.eval { downer + index.longValueExact() * one }
    }
}
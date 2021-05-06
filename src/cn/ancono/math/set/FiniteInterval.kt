package cn.ancono.math.set

import cn.ancono.math.MathObject
import cn.ancono.math.algebra.abs.calculator.EqualPredicate
import cn.ancono.math.algebra.abs.calculator.TotalOrderPredicate
import cn.ancono.math.algebra.abs.calculator.eval
import cn.ancono.math.numberModels.api.IntCalculator
import cn.ancono.math.numberModels.api.NumberFormatter
import java.math.BigInteger
import java.util.function.Function


/*
 * Created at 2018/10/10 10:10
 * @author  liyicheng
 */
class FiniteInterval<T>(override val calculator: IntCalculator<T>, val downer: T, val upper: T)
    : Interval<T>(calculator), FiniteSet<T> {


    override fun contains(n: T): Boolean {
        return calculator.compare(downer, n) <= 0 && calculator.compare(n, upper) <= 0
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
        return calculator.subtract(upper, downer)
    }

    override fun downerPart(n: T): Interval<T> {
        require(calculator.eval { n > downer && n < upper })
        return FiniteInterval(calculator, downer, n)
    }

    override fun downerPart(n: T, include: Boolean): Interval<T> {
        require(include)
        return downerPart(n)
    }

    override fun upperPart(n: T): Interval<T> {
        require(calculator.eval { n > downer && n < upper })
        return FiniteInterval(calculator, n, upper)
    }

    override fun upperPart(n: T, include: Boolean): Interval<T> {
        require(include)
        return upperPart(n)
    }

    override fun expandUpperBound(n: T): Interval<T> {
        require(calculator.eval { n > upper })
        return FiniteInterval(calculator, downer, n)
    }

    override fun expandUpperBound(n: T, include: Boolean): Interval<T> {
        require(include)
        return expandUpperBound(n)
    }

    override fun expandDownerBound(n: T): Interval<T> {
        require(calculator.eval { n < downer })
        return FiniteInterval(calculator, n, upper)
    }

    override fun expandDownerBound(n: T, include: Boolean): Interval<T> {
        require(include)
        return expandDownerBound(n)
    }

    override fun sameTypeInterval(downerBound: T, upperBound: T): Interval<T> {
        return FiniteInterval(calculator, downer, upper)
    }

    override fun contains(iv: Interval<T>): Boolean {
        return calculator.eval {
            upper >= iv.upperBound() &&
                    downer <= iv.downerBound()
        }
    }

    override fun intersect(iv: Interval<T>): FiniteInterval<T>? {
        val iL = iv.downerBound()!!
        val iR = iv.upperBound()!!
        val right = upper
        val left = downer
        val mc = calculator
        return if ((mc.compare(right, iL) >= 0) && (mc.compare(iR, left) >= 0)) {
            if (mc.compare(left, iL) < 0) {
                FiniteInterval(calculator, iL, right)
            } else {
                FiniteInterval(calculator, left, iR)
            }
        } else null
    }

    override fun <N> mapTo(newCalculator: EqualPredicate<N>, mapper: Function<T, N>): Interval<N> {
        return closedInterval(mapper.apply(downer), mapper.apply(upper), newCalculator as IntCalculator)
    }

    override fun toString(): String {
        return "[$downer,$upper]"
    }

    override fun toString(nf: NumberFormatter<T>): String {
        return "[${nf.format(downer)},${nf.format(upper)}]"
    }

    override fun valueEquals(obj: MathObject<T, TotalOrderPredicate<T>>): Boolean {
        if (obj !is FiniteInterval) {
            return false
        }
        val mc = calculator
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
            return calculator.eval { cur <= upper }
        }

        override fun hasPrevious(): Boolean {
            return calculator.eval { cur > downer }
        }

        override fun next(): T {
            if (hasNext()) {
                val t = cur
                cur = calculator.increase(cur)
                return t
            }
            throw NoSuchElementException()
        }

        override fun nextIndex(): Int {
            if (!hasNext()) {
                throw NoSuchElementException()
            }
            val re = calculator.eval { exactDivide(cur - downer, one) }
            return calculator.asLong(re).toInt()
        }

        override fun previous(): T {
            if (hasPrevious()) {
                cur = calculator.decrease(cur)
                return cur
            }
            throw NoSuchElementException()
        }

        override fun previousIndex(): Int {
            if (!hasPrevious()) {
                throw NoSuchElementException()
            }
            val re = calculator.eval { exactDivide(cur - downer, one) }
            return calculator.asLong(re).toInt() - 1
        }

    }

    override fun iterator(): MutableIterator<T> {
        return listIterator()
    }

    override fun size(): Long {
        val re = calculator.eval { exactDivide(upper - downer, one) }
        return calculator.asLong(re) + 1
    }

    override fun sizeAsBigInteger(): BigInteger {
        val re = calculator.eval { exactDivide(upper - downer, one) }
        return calculator.asBigInteger(re).inc()
    }

    override fun get(index: Long): T {
        return calculator.eval { downer + index * one }
    }

    override fun get(index: BigInteger): T {
        return calculator.eval { downer + index.longValueExact() * one }
    }
}
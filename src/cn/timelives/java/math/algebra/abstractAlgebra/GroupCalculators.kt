/**
 * 2018-03-05
 */
package cn.timelives.java.math.algebra.abstractAlgebra

import cn.timelives.java.math.algebra.abstractAlgebra.calculator.*
import cn.timelives.java.math.property.Composable
import cn.timelives.java.math.property.Invertible
import cn.timelives.java.math.function.Bijection
import cn.timelives.java.math.MathCalculator
import cn.timelives.java.math.numberModels.MathCalculatorAdapter

/**
 * Contains method to construct group calculators.
 * @author liyicheng
 * 2018-03-05 17:52
 */
object GroupCalculators {

    /**
     * Returns a group calculator from a composable and invertible type.
     * @param id the identity element of type T
     * @param equalPredicate
     * @return
     */
    fun <T> createComposing(id: T, equalPredicate: EqualPredicate<T>): GroupCalculator<T> where T : Any, T : Composable<T>, T : Invertible<T> {
        return object : GroupCalculator<T> {
            override val identity: T
                get() = id

            override fun apply(x: T, y: T): T {
                return x.compose(y)
            }

            override fun isEqual(x: T, y: T): Boolean {
                return equalPredicate.isEqual(x, y)
            }

            override fun inverse(x: T): T {
                return x.inverse()
            }
        }
    }

    /**
     * Returns a group calculator from a composable and invertible type. The equal relation in this group is defined by [Object.equals].
     * @param id
     * @return
     */
    fun <T> createComposing(id: T): GroupCalculator<T> where T : Any, T : Composable<T>, T : Invertible<T> {
        return object : GroupCalculator<T> {
            override val identity: T
                get() = id

            override fun apply(x: T, y: T): T {
                return x.compose(y)
            }

            override fun isEqual(x: T, y: T): Boolean {
                return x == y
            }

            override fun inverse(x: T): T {
                return x.inverse()
            }
        }
    }

    /**
     * Returns a semigroup calculator from a composable type. The equal relation in this group is defined by [Object.equals].
     * @return
     */
    fun <T : Composable<T>> createComposingSemi(): SemigroupCalculator<T> {
        return object : SemigroupCalculator<T> {

            override fun apply(x: T, y: T): T {
                return x.compose(y)
            }

            override fun isEqual(x: T, y: T): Boolean {
                return x == y
            }
        }
    }

    /**
     * Returns a semigroup calculator from a composable type.
     * @return
     */
    fun <T : Composable<T>> createComposingSemi(equalPredicate: EqualPredicate<T>): SemigroupCalculator<T> {
        return object : SemigroupCalculator<T> {

            override fun apply(x: T, y: T): T {
                return x.compose(y)
            }

            override fun isEqual(x: T, y: T): Boolean {
                return equalPredicate.isEqual(x, y)
            }
        }
    }

    /**
     * Returns a [MathCalculator] from the GroupCalculator, mapping the group's operation to "add" in MathCalculator
     * @param gc
     * @return
     */
    fun <T : Any> toMathCalculatorAdd(gc: GroupCalculator<T>): MathCalculator<T> {
        return object : MathCalculatorAdapter<T>() {

            /*
			 * @see cn.timelives.java.math.numberModels.MathCalculatorAdapter#getZero()
			 */
            override val zero: T
                get() = gc.identity

            /*
			 * @see cn.timelives.java.math.numberModels.MathCalculatorAdapter#isEqual(java.lang.Object, java.lang.Object)
			 */
            override fun isEqual(para1: T, para2: T): Boolean {
                return gc.isEqual(para1, para2)
            }

            /*
			 * @see cn.timelives.java.math.numberModels.MathCalculatorAdapter#add(java.lang.Object, java.lang.Object)
			 */
            override fun add(para1: T, para2: T): T {
                return gc.apply(para1, para2)
            }

            /*
			 * @see cn.timelives.java.math.numberModels.MathCalculatorAdapter#negate(java.lang.Object)
			 */
            override fun negate(para: T): T {
                return gc.inverse(para)
            }

            /*
			 * @see cn.timelives.java.math.numberModels.MathCalculatorAdapter#multiplyLong(java.lang.Object, long)
			 */
            override fun multiplyLong(p: T, l: Long): T {
                return gc.gpow(p, l)
            }
        }
    }

    /**
     * Returns a [MathCalculator] from the EqualPredicate, which only supports `isEqual(Object,Object)` method.
     * @param gc
     * @return
     */
    fun <T : Any> toMathCalculatorEqual(gc: EqualPredicate<T>): MathCalculator<T> {
        return if (gc is MathCalculator<*>) {
            gc as MathCalculator<T>
        } else object : MathCalculatorAdapter<T>() {
            /*
			 * @see cn.timelives.java.math.numberModels.MathCalculatorAdapter#isEqual(java.lang.Object, java.lang.Object)
			 */
            override fun isEqual(para1: T, para2: T): Boolean {
                return gc.isEqual(para1, para2)
            }
        }
    }

    /**
     * Returns a [MathCalculator] from the RingCalculator, mapping add, subtract, multiply.
     * @param rc
     * @param <T>
     * @return
    </T> */
    fun <T : Any> toMathCalculatorRing(rc: RingCalculator<T>): MathCalculator<T> {
        return object : MathCalculatorAdapter<T>() {

            override val zero: T
                get() = rc.zero

            override fun isEqual(para1: T, para2: T): Boolean {
                return rc.isEqual(para1, para2)
            }

            override fun add(para1: T, para2: T): T {
                return rc.add(para1, para2)
            }

            override fun negate(para: T): T {
                return rc.negate(para)
            }

            override fun subtract(para1: T, para2: T): T {
                return rc.subtract(para1, para2)
            }

            override fun multiply(para1: T, para2: T): T {
                return rc.multiply(para1, para2)
            }

            override fun multiplyLong(p: T, l: Long): T {
                return rc.multiplyLong(p, l)
            }

            override fun pow(p: T, exp: Long): T {
                return rc.pow(p, exp)
            }
        }
    }

    /**
     * Returns a [MathCalculator] from the RingCalculator, mapping add, subtract, multiply.
     * @param fc
     * @param <T>
     * @return
    </T> */
    fun <T : Any> toMathCalculatorDR(fc: DivisionRingCalculator<T>): MathCalculator<T> {
        return object : MathCalculatorAdapter<T>() {

            override val one: T
                get() = fc.one

            override val zero: T
                get() = fc.zero

            override fun isEqual(para1: T, para2: T): Boolean {
                return fc.isEqual(para1, para2)
            }

            override fun add(para1: T, para2: T): T {
                return fc.add(para1, para2)
            }

            override fun negate(para: T): T {
                return fc.negate(para)
            }

            override fun subtract(para1: T, para2: T): T {
                return fc.subtract(para1, para2)
            }

            override fun multiply(para1: T, para2: T): T {
                return fc.multiply(para1, para2)
            }

            override fun divide(para1: T, para2: T): T {
                return fc.divide(para1, para2)
            }

            override fun divideLong(p: T, n: Long): T {
                return fc.divideLong(p, n)
            }

            override fun multiplyLong(p: T, l: Long): T {
                return fc.multiplyLong(p, l)
            }

            override fun reciprocal(p: T): T {
                return fc.reciprocal(p)
            }

            override fun pow(p: T, exp: Long): T {
                return fc.pow(p, exp)
            }
        }
    }

    /**
     * Returns a isomorphism calculator of the original calculator through bijection `f`.
     * @param gc
     * @param f
     * @return
     */
    fun <T : Any, S : Any> isomorphism(gc: GroupCalculator<T>, f: Bijection<T, S>): GroupCalculator<S> {
        return IsoGC(gc, f)
    }


    internal class IsoGC<T : Any, S : Any>
    /**
     *
     */
    (val origin: GroupCalculator<T>, val f: Bijection<T, S>) : GroupCalculator<S> {
        /*
		 * @see cn.timelives.java.math.algebra.abstractAlgebra.calculator.MonoidCalculator#getIdentity()
		 */
        override val identity: S

        init {
            identity = f.apply(origin.identity)
        }

        /*
		 * @see cn.timelives.java.math.algebra.abstractAlgebra.calculator.SemigroupCalculator#apply(java.lang.Object, java.lang.Object)
		 */
        override fun apply(x: S, y: S): S {
            return f.apply(origin.apply(f.deply(x), f.deply(y)))
        }

        /*
		 * @see cn.timelives.java.math.algebra.abstractAlgebra.calculator.SemigroupCalculator#isEqual(java.lang.Object, java.lang.Object)
		 */
        override fun isEqual(x: S, y: S): Boolean {
            return origin.isEqual(f.deply(x), f.deply(y))
        }

        /*
		 * @see cn.timelives.java.math.algebra.abstractAlgebra.calculator.GroupCalculator#inverse(java.lang.Object)
		 */
        override fun inverse(x: S): S {
            return f.apply(origin.inverse(f.deply(x)))
        }

        /*
		 * @see cn.timelives.java.math.algebra.abstractAlgebra.calculator.GroupCalculator#gpow(java.lang.Object, long)
		 */
        override fun gpow(x: S, n: Long): S {
            val y = f.deply(x)
            val re = origin.gpow(y, n)
            return f.apply(re)
        }
    }

}
/**
 *
 */

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
    @JvmStatic
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
    @JvmStatic
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
    @JvmStatic
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
    @JvmStatic
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
    @JvmStatic
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
            override fun isEqual(x: T, y: T): Boolean {
                return gc.isEqual(x, y)
            }

            /*
			 * @see cn.timelives.java.math.numberModels.MathCalculatorAdapter#add(java.lang.Object, java.lang.Object)
			 */
            override fun add(x: T, y: T): T {
                return gc.apply(x, y)
            }

            /*
			 * @see cn.timelives.java.math.numberModels.MathCalculatorAdapter#negate(java.lang.Object)
			 */
            override fun negate(x: T): T {
                return gc.inverse(x)
            }

            /*
			 * @see cn.timelives.java.math.numberModels.MathCalculatorAdapter#multiplyLong(java.lang.Object, long)
			 */
            override fun multiplyLong(x: T, n: Long): T {
                return gc.gpow(x, n)
            }
        }
    }

    /**
     * Returns a [MathCalculator] from the EqualPredicate, which only supports `isEqual(Object,Object)` method.
     * @param gc
     * @return
     */
    @JvmStatic
    fun <T : Any> toMathCalculatorEqual(gc: EqualPredicate<T>): MathCalculator<T> {
        return if (gc is MathCalculator<*>) {
            gc as MathCalculator<T>
        } else object : MathCalculatorAdapter<T>() {
            /*
			 * @see cn.timelives.java.math.numberModels.MathCalculatorAdapter#isEqual(java.lang.Object, java.lang.Object)
			 */
            override fun isEqual(x: T, y: T): Boolean {
                return gc.isEqual(x, y)
            }
        }
    }

    /**
     * Returns a [MathCalculator] from the RingCalculator, mapping add, subtract, multiply.
     * @return
     */
    @JvmStatic
    fun <T : Any> toMathCalculatorRing(rc: RingCalculator<T>): MathCalculator<T> {
        return object : MathCalculatorAdapter<T>() {

            override val zero: T
                get() = rc.zero

            override fun isEqual(x: T, y: T): Boolean {
                return rc.isEqual(x, y)
            }

            override fun add(x: T, y: T): T {
                return rc.add(x, y)
            }

            override fun negate(x: T): T {
                return rc.negate(x)
            }

            override fun subtract(x: T, y: T): T {
                return rc.subtract(x, y)
            }

            override fun multiply(x: T, y: T): T {
                return rc.multiply(x, y)
            }

            override fun multiplyLong(x: T, n: Long): T {
                return rc.multiplyLong(x, n)
            }

            override fun pow(x: T, n: Long): T {
                return rc.pow(x, n)
            }
        }
    }

    /**
     * Returns a [MathCalculator] from the RingCalculator, mapping add, subtract, multiply.
     * @param fc
     * @param <T>
     * @return
     */
    @JvmStatic
    fun <T : Any> toMathCalculatorDR(fc: DivisionRingCalculator<T>): MathCalculator<T> {
        return object : MathCalculatorAdapter<T>() {

            override val one: T
                get() = fc.one

            override val zero: T
                get() = fc.zero

            override fun isEqual(x: T, y: T): Boolean {
                return fc.isEqual(x, y)
            }

            override fun add(x: T, y: T): T {
                return fc.add(x, y)
            }

            override fun negate(x: T): T {
                return fc.negate(x)
            }

            override fun subtract(x: T, y: T): T {
                return fc.subtract(x, y)
            }

            override fun multiply(x: T, y: T): T {
                return fc.multiply(x, y)
            }

            override fun divide(x: T, y: T): T {
                return fc.divide(x, y)
            }

            override fun divideLong(x: T, n: Long): T {
                return fc.divideLong(x, n)
            }

            override fun multiplyLong(x: T, n: Long): T {
                return fc.multiplyLong(x, n)
            }

            override fun reciprocal(x: T): T {
                return fc.reciprocal(x)
            }

            override fun pow(x: T, n: Long): T {
                return fc.pow(x, n)
            }
        }
    }
    @JvmStatic
    fun <T:Any> asSemigroupCalculator(rc : RingCalculator<T>) : SemigroupCalculator<T>{
        return object : SemigroupCalculator<T>{
            override fun apply(x: T, y: T): T {
                return rc.multiply(x,y)
            }

            override fun isEqual(x: T, y: T): Boolean {
                return rc.isEqual(x,y)
            }
        }
    }

    fun <T:Any> asMonoidCalculator(uc : UnitRingCalculator<T>) : MonoidCalculator<T>{
        return object : MonoidCalculator<T>{
            override val identity: T
                get() = uc.one

            override fun apply(x: T, y: T): T {
                return uc.multiply(x,y)
            }

            override fun isEqual(x: T, y: T): Boolean {
                return uc.isEqual(x,y)
            }
        }
    }

    fun <T:Any> asGroupCalculator(dc : DivisionRingCalculator<T>) : GroupCalculator<T>{
        return object : GroupCalculator<T>{
            override fun inverse(x: T): T {
                return dc.reciprocal(x)
            }

            override val identity: T
                get() = dc.one

            override fun apply(x: T, y: T): T {
                return dc.multiply(x,y)
            }

            override fun isEqual(x: T, y: T): Boolean {
                return dc.isEqual(x,y)
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


    internal class IsoGC<T : Any, S : Any>(val origin: GroupCalculator<T>, val f: Bijection<T, S>)
        : GroupCalculator<S> {

        override val isCommutative: Boolean
            get() = origin.isCommutative

        /*
		 * @see cn.timelives.java.math.algebra.abstractAlgebra.calculator.MonoidCalculator#getIdentity()
		 */
        override val identity: S = f.apply(origin.identity)

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

package cn.ancono.math.function

import cn.ancono.math.algebra.abs.calculator.eval
import cn.ancono.math.calculus.Derivable
import cn.ancono.math.numberModels.Fraction
import cn.ancono.math.numberModels.api.RealCalculator
import cn.ancono.math.set.MathSet
import cn.ancono.math.set.MathSets

/**
 * A derivable function supports [derive] operation, which also returns a [DerivableFunction]. It is acceptable that
 * the function [derive] throws an [ArithmeticException], for it is not
 * guaranteed that the returned function is also derivable in math.
 */
interface DerivableFunction<T, R> : MathFunction<T, R>, Derivable<T, R, DerivableFunction<T, R>> {
    /**
     * Returns the derivative of this function. Throws an [ArithmeticException] if
     * this function is not actually derivable.
     */
    val derivative: DerivableFunction<T, R>


    override fun derive(): DerivableFunction<T, R> {
        return derivative
    }

    companion object {
        /**
         * Returns a derivable function that applies the three given functions first and then merges the
         * result to type [S]. The merging operation will not be derived.
         */
        fun <T, R1, R2, R3, S> mergeOf3(f1: DerivableFunction<T, R1>,
                                        f2: DerivableFunction<T, R2>,
                                        f3: DerivableFunction<T, R3>,
                                        merger: (R1, R2, R3) -> S)
                : DerivableFunction<T, S> = DerivableMergeOf3(f1, f2, f3, merger)

        /**
         * Returns a derivable function that applies the two given functions first and then merges the
         * result to type [S]. The merging operation will not be derived.
         */
        fun <T, R1, R2, S> mergeOf2(f1: DerivableFunction<T, R1>,
                                    f2: DerivableFunction<T, R2>,
                                    merger: (R1, R2) -> S)
                : DerivableFunction<T, S> = DerivableMergeOf2(f1, f2, merger)

        /**
         * Returns a derivable function that is equal to the sum of the two functions.
         */
        fun <T, R1, R2, S> add(f: DerivableFunction<T, R1>,
                               g: DerivableFunction<T, R2>,
                               formalAdd: (R1, R2) -> S)
                : DerivableFunction<T, S> = DerivableAdd(f, g, formalAdd)

        /**
         * Returns a derivable function that is equal to the difference of the two functions.
         */
        fun <T, R1, R2, S> subtract(f: DerivableFunction<T, R1>,
                                    g: DerivableFunction<T, R2>,
                                    formalSubtract: (R1, R2) -> S)
                : DerivableFunction<T, S> = DerivableAdd(f, g, formalSubtract)


        /**
         * Returns a derivable function that is equal to the sum of the two functions. This function returns
         * a [SVFunction<T>].
         */
        fun <T> addSV(f: DerivableSVFunction<T>,
                      g: DerivableSVFunction<T>,
                      formalAdd: (T, T) -> T)
                : DerivableSVFunction<T> = DerivableAddSV(f, g, formalAdd)

        /**
         * Returns a derivable function that is equal to the difference of the two functions. This function returns
         * a [SVFunction<T>].
         */
        fun <T> subtractSV(f: DerivableSVFunction<T>,
                           g: DerivableSVFunction<T>,
                           formalSubtract: (T, T) -> T)
                : DerivableSVFunction<T> = DerivableAddSV(f, g, formalSubtract)

        /**
         * Returns a derivable function that is equal to the product of the two functions.
         * @param formalAdd performs add operation to type [S]
         * @param formalMultiply performs multiplication operation
         */
        fun <T, R1, R2, S> multiply(f: DerivableFunction<T, R1>,
                                    g: DerivableFunction<T, R2>,
                                    formalAdd: (S, S) -> S,
                                    formalMultiply: (R1, R2) -> S)
                : DerivableFunction<T, S> = DerivableMultiply(f, g, formalMultiply, formalAdd)

        /**
         * Returns a derivable function that is equal to the product of the two functions. This function returns
         * a [SVFunction<T>].
         * @param formalAdd performs add operation to type [T]
         * @param formalMultiply performs multiplication operation
         */
        fun <T, S> multiplySV(f: DerivableFunction<T, out S>,
                              g: DerivableFunction<T, out S>,
                              formalAdd: (T, T) -> T,
                              formalMultiply: (S, S) -> T)
                : DerivableSVFunction<T> = DerivableMultiplySV(f, g, formalMultiply = formalMultiply, formalAdd = formalAdd)

        /**
         * Returns a derivable function that is equal to the product of the two functions. This function returns
         * a [SVFunction<T>].
         */
        fun <T> multiplySV(f: DerivableFunction<T, T>,
                           g: DerivableFunction<T, T>,
                           mc: RealCalculator<T>)
                : DerivableSVFunction<T> = multiplySV(f, g, mc::add, mc::multiply)

        /**
         * Returns a derivable function that applies [f] first and then applies [g].
         */
        fun <T, R, S> compose(f: DerivableFunction<T, R>,
                              g: DerivableFunction<R, S>,
                              formalAdd: (S, S) -> S,
                              formalMultiply: (R, S) -> S)
                : DerivableFunction<T, S> = DerivableCompose(f, g, formalMultiply, formalAdd)

        /**
         * Returns a derivable function that applies [f] first and then applies [g]. This function returns
         * a [SVFunction<T>].
         */
        fun <T> composeSV(f: DerivableSVFunction<T>,
                          g: DerivableSVFunction<T>,
                          mc: RealCalculator<T>)
                : DerivableSVFunction<T> = DerivableComposeSV(f, g, mc::multiply, mc::add)


        /**
         * Returns a derivable function of `f/g`.
         */
        fun <T> divideSV(f: DerivableSVFunction<T>, g: DerivableSVFunction<T>, mc: RealCalculator<T>)
                : DerivableSVFunction<T> {
            return DerivableDivideSV(f, g, mc)
        }


        /**
         * Returns a derivable function of `f/g`.
         */
        fun <T, S> divide(f: DerivableFunction<T, S>, g: DerivableFunction<T, T>,
                          mc: RealCalculator<T>,
                          formalAdd: (S, S) -> S,
                          formalSubtract: (S, S) -> S,
                          formalMultiply: (T, S) -> S)
                : DerivableFunction<T, S> {
            return DerivableDivide(f, g, mc, formalMultiply, formalAdd, formalSubtract)
        }

        /**
         * Returns a derivable function that is equal to the product of all functions in `fs`.
         */
        fun <T, S> multiplyAllSV(fs: List<DerivableFunction<T, S>>,
                                 formalMultiplyAll: (List<S>) -> T,
                                 formalAddAll: (List<T>) -> T)
                : DerivableSVFunction<T> = DerivableMultiplyNSV(fs, formalMultiplyAll, formalAddAll)
    }
}

/**
 * Represents a derivable single variable function.
 */
interface DerivableSVFunction<T> : DerivableFunction<T, T>, SVFunction<T> {
    override val derivative: DerivableSVFunction<T>

    override fun derive(): DerivableSVFunction<T> {
        return derivative
    }

    override fun <S> mapTo(mapper: Bijection<T, S>): DerivableSVFunction<S> {
        return MappedSVDerivableFunction(this, mapper)
    }
}

/**
 * Returns a derivable function that applies this first, and then applies the [mapper], which will
 * not be derived when calculating the derivative of the function.
 */
fun <T, R, S> DerivableFunction<T, R>.andThenMap(mapper: (R) -> S): DerivableFunction<T, S> =
        AndThenHomoDerivableFunction(this, mapper)

/**
 * Converts a [DerivableFunction<T,T>] to [DerivableSVFunction<T>]
 */
fun <T> DerivableFunction<T, T>.asSVFunction(): DerivableSVFunction<T> {
    return if (this is SVFunction<*>) {
        this as DerivableSVFunction<T>
    } else {
        DerivableSVFunctionWrapper(this)
    }
}

/**
 * Composes this function to another DerivableFunction with given [formalAdd] and [formalMultiply].
 */
fun <T, R, S> DerivableFunction<T, R>.composeDerivable(g: DerivableFunction<R, S>,
                                                       formalAdd: (S, S) -> S,
                                                       formalMultiply: (R, S) -> S)
        : DerivableFunction<T, S> = DerivableFunction.compose(this, g, formalAdd, formalMultiply)


internal class DerivableSVFunctionWrapper<T>(private val ori: DerivableFunction<T, T>) : DerivableSVFunction<T> {
    override val derivative: DerivableSVFunction<T> by lazy {
        DerivableSVFunctionWrapper(ori.derive())
    }


    override fun apply(x: T): T {
        return ori.apply(x)
    }

    override fun domain(): MathSet<T> = ori.domain()
}

/**
 * The mapper should not be derived.
 */
internal class AndThenHomoDerivableFunction<T, R, S>(val f: DerivableFunction<T, R>, val homo: (R) -> S) : DerivableFunction<T, S> {
    override fun apply(x: T): S {
        return homo(f(x))
    }

    override fun domain(): MathSet<T> {
        return f.domain()
    }

    override val derivative: DerivableFunction<T, S> by lazy {
        AndThenHomoDerivableFunction(f.derive(), homo)
    }
}

/**
 * The mapper should not be derived.
 */
internal class MappedSVDerivableFunction<T, S>(val f: DerivableSVFunction<T>, private val homo: Bijection<T, S>) : DerivableSVFunction<S> {
    private val newDomain = f.domain().mapTo(homo)


    override fun apply(x: S): S {
        return homo(f(homo.deply(x)))
    }

    override fun domain(): MathSet<S> {
        return newDomain
    }

    override val derivative: DerivableSVFunction<S> by lazy {
        MappedSVDerivableFunction(f.derive(), homo)
    }
}

/**
 * The mapper should not be derived.
 */
internal open class DerivableMergeOf2<T, R1, R2, R>(
        fx: DerivableFunction<T, out R1>,
        gx: DerivableFunction<T, out R2>,
        merger: (R1, R2) -> R
) : MergeOf2<T, R1, R2, R>(fx, gx, merger), DerivableFunction<T, R> {

    override val f: DerivableFunction<T, out R1> = fx

    override val g: DerivableFunction<T, out R2> = gx

    override fun apply(x: T): R {
        return merger(f(x), g(x))
    }

    override val derivative: DerivableFunction<T, R> by lazy {
        DerivableMergeOf2(f.derive(), g.derive(), merger)
    }


}

/**
 * The mapper should not be derived.
 */
internal class DerivableMergeOf3<T, R1, R2, R3, S>(val f1: DerivableFunction<T, R1>,
                                                   val f2: DerivableFunction<T, R2>,
                                                   val f3: DerivableFunction<T, R3>,
                                                   val merger: (R1, R2, R3) -> S)
    : DerivableFunction<T, S> {

    val nDomain: MathSet<T> = MathSets.intersectOf(f1.domain(), f2.domain(), f3.domain())

    override val derivative: DerivableFunction<T, S> by lazy {
        DerivableMergeOf3(f1.derive(), f2.derive(), f3.derive(), merger)
    }


    override fun apply(x: T): S {
        return merger(f1(x), f2(x), f3(x))
    }

    override fun domain(): MathSet<T> = nDomain
}


internal open class DerivableAdd<T, R1, R2, R>(f: DerivableFunction<T, R1>,
                                               g: DerivableFunction<T, R2>,
                                               formalAdd: (R1, R2) -> R)
    : DerivableMergeOf2<T, R1, R2, R>(f, g, formalAdd) {

    override val derivative: DerivableFunction<T, R> by lazy {
        DerivableAdd(f.derive(), g.derive(), merger)
    }

}

internal class DerivableAddSV<T>(override val f: DerivableSVFunction<T>,
                                 override val g: DerivableSVFunction<T>,
                                 formalAdd: (T, T) -> T) : DerivableAdd<T, T, T, T>(f, g, formalAdd), DerivableSVFunction<T> {
    override val derivative: DerivableSVFunction<T> by lazy {
        DerivableAddSV(f.derive(), g.derive(), merger)
    }
}

internal open class DerivableMultiply<T, R1, R2, R>(f: DerivableFunction<T, out R1>,
                                                    g: DerivableFunction<T, out R2>,
                                                    formalMultiply: (R1, R2) -> R,
                                                    open val formalAdd: (R, R) -> R)
    : DerivableMergeOf2<T, R1, R2, R>(f, g, formalMultiply) {

    override val derivative: DerivableFunction<T, R> by lazy {
        //f(x) * g(x)
        //f'(x) * g(x) + f(x) * g'(x)
        val partA = DerivableMultiply(f.derive(), g, merger, formalAdd)
        val partB = DerivableMultiply(f, g.derive(), merger, formalAdd)
        DerivableAdd(partA, partB, formalAdd)
    }
}

internal class DerivableMultiplySV<T, S>(override val f: DerivableFunction<T, out S>,
                                         override val g: DerivableFunction<T, out S>,
                                         formalMultiply: (S, S) -> T,
                                         override val formalAdd: (T, T) -> T)
    : DerivableMultiply<T, S, S, T>(f, g, formalMultiply, formalAdd), DerivableSVFunction<T> {

    override val derivative: DerivableSVFunction<T> by lazy {
        //f(x) * g(x)
        //f'(x) * g(x) + f(x) * g'(x)
        val partA = DerivableMultiplySV(f.derive(), g, merger, formalAdd)
        val partB = DerivableMultiplySV(f, g.derive(), merger, formalAdd)
        DerivableAddSV(partA, partB, formalAdd)
    }
}

internal class DerivableAddNSV<T>(val fs: List<DerivableSVFunction<T>>,
                                  val formalAddAll: (List<T>) -> T) : DerivableSVFunction<T> {
    override val derivative: DerivableSVFunction<T> by lazy {
        DerivableAddNSV(fs.map { it.derivative }, formalAddAll)
    }

    override fun apply(x: T): T {
        return formalAddAll(fs.map { it(x) })
    }
}

internal class DerivableMultiplyNSV<T, S>(val fs: List<DerivableFunction<T, S>>,
                                          private val formalMultiplyAll: (List<S>) -> T,
                                          private val formalAddAll: (List<T>) -> T) : DerivableSVFunction<T> {
    override val derivative: DerivableSVFunction<T> by lazy {
        val parts = ArrayList<DerivableSVFunction<T>>(fs.size)
        for (i in fs.indices) {
            val t = ArrayList(fs)
            t[i] = fs[i].derivative
            parts += DerivableMultiplyNSV(t, formalMultiplyAll, formalAddAll)
        }
        DerivableAddNSV(parts, formalAddAll)
    }

    override fun apply(x: T): T {
        return formalMultiplyAll(fs.map { it(x) })
    }
}


internal open class DerivableCompose<T, R, S>(private val f: DerivableFunction<T, R>,
                                              private val g: DerivableFunction<R, S>,
                                              private val formalMultiply: (R, S) -> S,
                                              private val formalAdd: (S, S) -> S) : DerivableFunction<T, S> {

    override val derivative: DerivableFunction<T, S> by lazy {
        //g(f(x))
        //f'(x) * g'(f(x))
        val left = f.derive()
        val right = DerivableCompose(f, g.derive(), formalMultiply, formalAdd)
        DerivableMultiply(left, right, formalMultiply, formalAdd)
    }


    override fun apply(x: T): S {
        return g(f(x))
    }
}


internal open class DerivableComposeSV<T>(private val f: DerivableSVFunction<T>,
                                          private val g: DerivableSVFunction<T>,
                                          private val formalMultiply: (T, T) -> T,
                                          private val formalAdd: (T, T) -> T) : DerivableSVFunction<T> {
    override val derivative: DerivableSVFunction<T> by lazy {
        //g(f(x))
        //f'(x) * g'(f(x))
        val left = f.derive()
        val right = DerivableComposeSV(f, g.derive(), formalMultiply, formalAdd)
        DerivableMultiplySV(left, right, formalMultiply, formalAdd)
    }

    override fun apply(x: T): T {
        return g(f(x))
    }
}


internal class DerivableDivide<T, S>(private val f: DerivableFunction<T, S>,
                                     private val g: DerivableFunction<T, T>,
                                     private val mc: RealCalculator<T>,
                                     private val formalMultiply: (T, S) -> S,
                                     private val formalAdd: (S, S) -> S,
                                     private val formalSubtract: (S, S) -> S
) : DerivableFunction<T, S> {
    override val derivative: DerivableFunction<T, S> by lazy {
        val f_ = f.derivative
        val g_ = g.derivative
        val f_g = DerivableFunction.multiply(g, f_, formalAdd, formalMultiply)
        val fg_ = DerivableFunction.multiply(g_, f, formalAdd, formalMultiply)
        val nume = DerivableFunction.subtract(f_g, fg_, formalSubtract)
        val deno = DerivableFunction.compose(g, AbstractSVFunction.pow(Fraction.TWO, mc), mc::add, mc::multiply)

        DerivableDivide(nume, deno, mc, formalMultiply, formalAdd, formalSubtract)
    }

    override fun apply(x: T): S {
        return formalMultiply(mc.reciprocal(g(x)), f(x))
    }
}


internal class DerivableDivideSV<T>(private val f: DerivableSVFunction<T>,
                                    private val g: DerivableSVFunction<T>,
                                    private val mc: RealCalculator<T>) : DerivableSVFunction<T> {
    override val derivative: DerivableSVFunction<T> by lazy {
        val f_ = f.derivative
        val g_ = g.derivative
        val f_g = DerivableFunction.multiplySV(f_, g, mc)
        val fg_ = DerivableFunction.multiplySV(f, g_, mc)
        val nume = DerivableFunction.subtractSV(f_g, fg_, mc::subtract)
        val deno = DerivableFunction.composeSV(g, AbstractSVFunction.pow(Fraction.TWO, mc), mc)

        DerivableDivideSV(nume, deno, mc)
    }

    override fun apply(x: T): T {
        return mc.eval { f(x) / g(x) }
    }
}

//class DerivableSVFunctionCalculator<T>(val mc: MathCalculator<T>) : MathCalculatorAdapter<DerivableSVFunction<T>>() {
//    override val one: DerivableSVFunction<T> = AbstractSVFunction.constant(mc.one,mc)
//    override val zero: DerivableSVFunction<T> = AbstractSVFunction.constant(mc.zero,mc)
//
//    override fun add(x: DerivableSVFunction<T>, y: DerivableSVFunction<T>): DerivableSVFunction<T> {
//        return DerivableFunction.addSV(x,y,mc::add)
//    }
//
//    override fun negate(x: DerivableSVFunction<T>): DerivableSVFunction<T> {
//        return x.andThenMap(mc::negate)
//    }
//
//    override fun subtract(x: DerivableSVFunction<T>, y: DerivableSVFunction<T>): DerivableSVFunction<T> {
//        return super.subtract(x, y)
//    }
//
//    override fun multiply(x: DerivableSVFunction<T>, y: DerivableSVFunction<T>): DerivableSVFunction<T> {
//        return super.multiply(x, y)
//    }
//
//    override fun divide(x: DerivableSVFunction<T>, y: DerivableSVFunction<T>): DerivableSVFunction<T> {
//        return super.divide(x, y)
//    }
//
//    override fun reciprocal(x: DerivableSVFunction<T>): DerivableSVFunction<T> {
//        return super.reciprocal(x)
//    }
//
//    override fun squareRoot(x: DerivableSVFunction<T>): DerivableSVFunction<T> {
//        return super.squareRoot(x)
//    }
//
//    override fun pow(x: DerivableSVFunction<T>, n: Long): DerivableSVFunction<T> {
//        return super.pow(x, n)
//    }
//
//    override fun exp(a: DerivableSVFunction<T>, b: DerivableSVFunction<T>): DerivableSVFunction<T> {
//        return super.exp(a, b)
//    }
//
//    override fun ln(x: DerivableSVFunction<T>): DerivableSVFunction<T> {
//        return super.ln(x)
//    }
//}


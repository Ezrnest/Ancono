package cn.ancono.math.function

import cn.ancono.math.MathCalculator
import cn.ancono.math.numberModels.Fraction
import cn.ancono.math.set.MathSet
import cn.ancono.math.set.MathSets


/**
 * Describes a partially derivable function from `T` to `R`.
 *
 *
 * @see DerivableFunction
 */
interface NDerivableFunction<T, R> : NMathFunction<T, R> {


    /**
     * Returns the partial derivative of this function to the `i`-th argument.
     * Note that `i` should starts with `0`.
     */
    fun partial(i: Int): NDerivableFunction<T, R>

    companion object {
        /**
         * Returns a derivable function that applies the three given functions first and then merges the
         * result to type [S]. The merging operation will not be derived.
         */
        fun <T, R1, R2, R3, S> mergeOf3(f1: NDerivableFunction<T, R1>,
                                        f2: NDerivableFunction<T, R2>,
                                        f3: NDerivableFunction<T, R3>,
                                        merger: (R1, R2, R3) -> S)
                : NDerivableFunction<T, S> = NDerivableMergeOf3(f1, f2, f3, merger)

        /**
         * Returns a derivable function that applies the two given functions first and then merges the
         * result to type [S]. The merging operation will not be derived.
         */
        fun <T, R1, R2, S> mergeOf2(f1: NDerivableFunction<T, R1>,
                                    f2: NDerivableFunction<T, R2>,
                                    merger: (R1, R2) -> S)
                : NDerivableFunction<T, S> = NDerivableMergeOf2(f1, f2, merger)

        /**
         * Returns a derivable function that is equal to the sum of the two functions.
         */
        fun <T, R1, R2, S> add(f: NDerivableFunction<T, R1>,
                               g: NDerivableFunction<T, R2>,
                               formalAdd: (R1, R2) -> S)
                : NDerivableFunction<T, S> = NDerivableAdd(f, g, formalAdd)

        /**
         * Returns a derivable function that is equal to the difference of the two functions.
         */
        fun <T, R1, R2, S> subtract(f: NDerivableFunction<T, R1>,
                                    g: NDerivableFunction<T, R2>,
                                    formalSubtract: (R1, R2) -> S)
                : NDerivableFunction<T, S> = NDerivableAdd(f, g, formalSubtract)


        /**
         * Returns a derivable function that is equal to the product of the two functions.
         * @param formalAdd performs add operation to type [S]
         * @param formalMultiply performs multiplication operation
         */
        fun <T, R1, R2, S> multiply(f: NDerivableFunction<T, R1>,
                                    g: NDerivableFunction<T, R2>,
                                    formalAdd: (S, S) -> S,
                                    formalMultiply: (R1, R2) -> S)
                : NDerivableFunction<T, S> = NDerivableMultiply(f, g, formalMultiply, formalAdd)


        /**
         * Returns a derivable function that applies [f] first and then applies [g].
         */
        fun <T, R, S> compose(f: NDerivableFunction<T, R>,
                              g: DerivableFunction<R, S>,
                              formalAdd: (S, S) -> S,
                              formalMultiply: (R, S) -> S)
                : NDerivableFunction<T, S> = NDerivableCompose(f, g, formalMultiply, formalAdd)

        /**
         * Returns a derivable function of `f/g`.
         */
        fun <T, S> divide(f: NDerivableFunction<T, S>, g: NDerivableFunction<T, T>,
                          mc: MathCalculator<T>,
                          formalAdd: (S, S) -> S,
                          formalSubtract: (S, S) -> S,
                          formalMultiply: (T, S) -> S)
                : NDerivableFunction<T, S> {
            return NDerivableDivide(f, g, mc, formalMultiply, formalAdd, formalSubtract)
        }

    }
}


//typealias PDerivableFunction<T,R> = IPDerivableFunction<T,R,Int>

val <T, R> NDerivableFunction<T, R>.partial1: NDerivableFunction<T, R>
    get() = this.partial(0)

val <T, R> NDerivableFunction<T, R>.partial2: NDerivableFunction<T, R>
    get() = this.partial(1)

val <T, R> NDerivableFunction<T, R>.partial3: NDerivableFunction<T, R>
    get() = this.partial(2)


/**
 *  Describes a derivable two-variable function `f(x,y)`
 */
interface BiDerivableFunction<T, R> : BiMathFunction<T, T, R> {
    /**
     * Returns the partial derivative to the first argument, namely `∂/∂x f`
     */
    val partial1: BiDerivableFunction<T, R>

    /**
     * Returns the partial derivative to the second argument, namely `∂/∂y f`
     */
    val partial2: BiDerivableFunction<T, R>
}

/**
 *  Transforms this derivable function of multiple arguments to `BiDerivableFunction`.
 *  It is required that `this.n == 2`.
 */
fun <T, R> NDerivableFunction<T, R>.asBiDerivable(): BiDerivableFunction<T, R> {
    require(this.paramLength == 2) {
        "This function must accept exactly 2 arguments. Actual: ${this.paramLength}"
    }
    return object : BiDerivableFunction<T, R> {
        override val partial1: BiDerivableFunction<T, R> by lazy {
            this@asBiDerivable.partial(0).asBiDerivable()
        }
        override val partial2: BiDerivableFunction<T, R> by lazy {
            this@asBiDerivable.partial(1).asBiDerivable()
        }

        override fun apply(x: T, y: T): R {
            return this@asBiDerivable.apply(listOf(x, y))
        }
    }
}

fun <T, R> BiDerivableFunction<T, R>.asPDerivable(): NDerivableFunction<T, R> {
    val f = this
    return object : NDerivableFunction<T, R> {
        val partials by lazy {
            listOf(f.partial1.asPDerivable(), f.partial2.asPDerivable())
        }

        override fun partial(i: Int): NDerivableFunction<T, R> {
            return partials[i]
        }

        override val paramLength: Int
            get() = 2

        override fun apply(x: List<T>): R {
            val (u, v) = x
            return f.apply(u, v)
        }
    }
}


abstract class CachedPartialNDFunction<T, R>(final override val paramLength: Int) : NDerivableFunction<T, R> {
    private val partials: Array<NDerivableFunction<T, R>?> = arrayOfNulls(paramLength)

    /**
     * Override this method.
     */
    protected abstract fun computePartial(i: Int): NDerivableFunction<T, R>


    final override fun partial(i: Int): NDerivableFunction<T, R> {
        if (partials[i] == null) {
            partials[i] = computePartial(i)
        }
        return partials[i]!!
    }
}

/**
 * The mapper should not be derived.
 */
internal open class NDerivableMergeOf2<T, R1, R2, R>(
        val f: NDerivableFunction<T, out R1>,
        val g: NDerivableFunction<T, out R2>,
        val merger: (R1, R2) -> R
) : CachedPartialNDFunction<T, R>(f.paramLength) {
    init {
        require(f.paramLength == g.paramLength)
    }

    val nDomain: MathSet<List<T>> = MathSets.intersectOf(f.domain(), g.domain())

    override fun apply(x: List<T>): R {
        return merger(f(x), g(x))
    }

    override fun computePartial(i: Int): NDerivableFunction<T, R> {
        return NDerivableMergeOf2(f.partial(i), g.partial(i), merger)
    }

    override fun domain(): MathSet<List<T>> = nDomain
}

/**
 * The mapper should not be derived.
 */
internal class NDerivableMergeOf3
<T, R1, R2, R3, S>(
        val f1: NDerivableFunction<T, out R1>,
        val f2: NDerivableFunction<T, out R2>,
        val f3: NDerivableFunction<T, out R3>,
        val merger: (R1, R2, R3) -> S
) : CachedPartialNDFunction<T, S>(f1.paramLength) {
    init {
        require(f1.paramLength == f2.paramLength && f2.paramLength == f3.paramLength)
    }

    private val intersectDomain = MathSets.intersectOf(f1.domain(), f2.domain(), f3.domain())!!

    override fun apply(x: List<T>): S {
        return merger(f1(x), f2(x), f3(x))
    }

    override fun computePartial(i: Int): NDerivableFunction<T, S> {
        return NDerivableMergeOf3(f1.partial(i), f2.partial(i), f3.partial(i), merger)
    }

    override fun domain(): MathSet<List<T>> = intersectDomain
}


internal open class NDerivableAdd<T, R1, R2, R>(
        f: NDerivableFunction<T, R1>,
        g: NDerivableFunction<T, R2>,
        formalAdd: (R1, R2) -> R
) : NDerivableMergeOf2<T, R1, R2, R>(f, g, formalAdd) {

    override fun computePartial(i: Int): NDerivableFunction<T, R> {
        return NDerivableAdd(f.partial(i), g.partial(i), merger)
    }

}

internal open class NDerivableMultiply<T, R1, R2, R>(
        f: NDerivableFunction<T, out R1>,
        g: NDerivableFunction<T, out R2>,
        formalMultiply: (R1, R2) -> R,
        open val formalAdd: (R, R) -> R
) : NDerivableMergeOf2<T, R1, R2, R>(f, g, formalMultiply) {

    override fun computePartial(i: Int): NDerivableFunction<T, R> {
        //f(x) * g(x)
        //f'(x) * g(x) + f(x) * g'(x)
        val partA = NDerivableMultiply(f.partial(i), g, merger, formalAdd)
        val partB = NDerivableMultiply(f, g.partial(i), merger, formalAdd)
        return NDerivableAdd(partA, partB, formalAdd)
    }
}


internal open class NDerivableCompose<T, R, S>(
        private val f: NDerivableFunction<T, R>,
        private val g: DerivableFunction<R, S>,
        private val formalMultiply: (R, S) -> S,
        private val formalAdd: (S, S) -> S
) : NDerivableFunction<T, S> {

    override val paramLength: Int
        get() = f.paramLength

    override fun apply(x: List<T>): S {
        return g(f(x))
    }

    override fun partial(i: Int): NDerivableFunction<T, S> {
        val left = f.partial(i)
        val right = NDerivableCompose(f, g.derive(), formalMultiply, formalAdd)
        return NDerivableMultiply(left, right, formalMultiply, formalAdd)
    }
}


internal class NDerivableDivide<T, S>(private val f: NDerivableFunction<T, S>,
                                      private val g: NDerivableFunction<T, T>,
                                      private val mc: MathCalculator<T>,
                                      private val formalMultiply: (T, S) -> S,
                                      private val formalAdd: (S, S) -> S,
                                      private val formalSubtract: (S, S) -> S
) : NDerivableFunction<T, S> {

    init {
        require(f.paramLength == g.paramLength)
    }

    override val paramLength: Int
        get() = f.paramLength

    override fun partial(i: Int): NDerivableFunction<T, S> {
        val f_ = f.partial(i)
        val g_ = g.partial(i)
        val f_g = NDerivableMultiply(g, f_, formalMultiply, formalAdd)
        val fg_ = NDerivableMultiply(g_, f, formalMultiply, formalAdd)
        val nume = NDerivableAdd(f_g, fg_, formalSubtract)
        val deno = NDerivableCompose(g, AbstractSVFunction.pow(Fraction.TWO, mc), mc::add, mc::multiply)

        return NDerivableDivide(nume, deno, mc, formalMultiply, formalAdd, formalSubtract)
    }

    override fun apply(x: List<T>): S {
        return formalMultiply(mc.reciprocal(g(x)), f(x))
    }

}


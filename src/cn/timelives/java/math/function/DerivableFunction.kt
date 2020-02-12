package cn.timelives.java.math.function

import cn.timelives.java.math.calculus.Derivable
import cn.timelives.java.math.set.MathSet
import cn.timelives.java.math.set.MathSets

/**
 * A derivable function supports [derive] operation, which also returns a [DerivableFunction]. It is acceptable that
 * the function [derive] throws an [ArithmeticException], for it is not
 * guaranteed that the returned function is also derivable in math.
 */
interface DerivableFunction<T : Any, R : Any> : MathFunction<T, R>, Derivable<T, R, DerivableFunction<T, R>> {

    /**
     * Returns the derivative of this function. Throws an [ArithmeticException] if
     * this function is not derivable.
     */
    override fun derive(): DerivableFunction<T, R>

    companion object {
        /**
         * Returns a derivable function that applies the three given functions first and then merges the
         * result to type [S]. The merging operation will not be derived.
         */
        fun <T : Any, R1 : Any, R2 : Any, R3 : Any, S : Any> mergeOf3(f1: DerivableFunction<T, R1>,
                                                                      f2: DerivableFunction<T, R2>,
                                                                      f3: DerivableFunction<T, R3>,
                                                                      merger: (R1, R2, R3) -> S)
                : DerivableFunction<T, S> = DerivableMergeOf3(f1, f2, f3, merger)

        /**
         * Returns a derivable function that applies the two given functions first and then merges the
         * result to type [S]. The merging operation will not be derived.
         */
        fun <T : Any, R1 : Any, R2 : Any, S : Any> mergeOf2(f1: DerivableFunction<T, R1>,
                                                            f2: DerivableFunction<T, R2>,
                                                            merger: (R1, R2) -> S)
                : DerivableFunction<T, S> = DerivableMergeOf2(f1, f2, merger)

        /**
         * Returns a derivable function that is equal to the sum of the two functions.
         */
        fun <T : Any, R1 : Any, R2 : Any, S : Any> add(f: DerivableFunction<T, R1>,
                                                       g: DerivableFunction<T, R2>,
                                                       formalAdd: (R1, R2) -> S)
                : DerivableFunction<T, S> = DerivableAdd(f, g, formalAdd)

        /**
         * Returns a derivable function that is equal to the difference of the two functions.
         */
        fun <T : Any, R1 : Any, R2 : Any, S : Any> subtract(f: DerivableFunction<T, R1>,
                                                            g: DerivableFunction<T, R2>,
                                                            formalSubtract: (R1, R2) -> S)
                : DerivableFunction<T, S> = DerivableAdd(f, g, formalSubtract)

        /**
         * Returns a derivable function that is equal to the sum of the two functions. This function returns
         * a [SVFunction<T>].
         */
        fun <T : Any> addSV(f: DerivableSVFunction<T>,
                            g: DerivableSVFunction<T>,
                            formalAdd: (T, T) -> T)
                : DerivableSVFunction<T> = DerivableAddSV(f, g, formalAdd)

        /**
         * Returns a derivable function that is equal to the difference of the two functions. This function returns
         * a [SVFunction<T>].
         */
        fun <T : Any> subtractSV(f: DerivableSVFunction<T>,
                                 g: DerivableSVFunction<T>,
                                 formalSubtract: (T, T) -> T)
                : DerivableSVFunction<T> = DerivableAddSV(f, g, formalSubtract)

        /**
         * Returns a derivable function that is equal to the product of the two functions.
         * @param formalAdd performs add operation to type [S]
         * @param formalMultiply performs multiplication operation
         */
        fun <T : Any, R1 : Any, R2 : Any, S : Any> multiply(f: DerivableFunction<T, R1>,
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
        fun <T : Any> multiplySV(f: DerivableSVFunction<T>,
                                 g: DerivableSVFunction<T>,
                                 formalAdd: (T, T) -> T,
                                 formalMultiply: (T, T) -> T)
                : DerivableSVFunction<T> = DerivableMultiplySV(f, g, formalMultiply = formalMultiply, formalAdd = formalAdd)


        /**
         * Returns a derivable function that applies [f] first and then applies [g].
         */
        fun <T : Any, R : Any, S : Any> compose(f: DerivableFunction<T, R>,
                                                g: DerivableFunction<R, S>,
                                                formalAdd: (S, S) -> S,
                                                formalMultiply: (R, S) -> S)
                : DerivableFunction<T, S> = DerivableCompose(f, g, formalMultiply, formalAdd)

        /**
         * Returns a derivable function that applies [f] first and then applies [g]. This function returns
         * a [SVFunction<T>].
         */
        fun <T : Any> composeSV(f: DerivableSVFunction<T>,
                                g: DerivableSVFunction<T>,
                                formalAdd: (T, T) -> T,
                                formalMultiply: (T, T) -> T)
                : DerivableSVFunction<T> = DerivableComposeSV(f, g, formalMultiply, formalAdd)

    }
}

/**
 * Represents a derivable function which is also a [SVFunction].
 */
interface DerivableSVFunction<T : Any> : DerivableFunction<T, T>, SVFunction<T> {
    override fun derive(): DerivableSVFunction<T>

    override fun <S : Any> mapTo(mapper: Bijection<T, S>): DerivableSVFunction<S> {
        return MappedSVDerivableFunction(this, mapper)
    }
}

/**
 * Returns a derivable function that applies this first, and then applies the [mapper], which will
 * not be derived when calculating the derivative of the function.
 */
fun <T : Any, R : Any, S : Any> DerivableFunction<T, R>.andThenMap(mapper: (R) -> S): DerivableFunction<T, S> =
        AndThenHomoDerivableFunction(this, mapper)

/**
 * Converts a [DerivableFunction<T,T>] to [DerivableSVFunction<T>]
 */
fun <T : Any> DerivableFunction<T, T>.asSVFunction(): DerivableSVFunction<T> {
    return if (this is SVFunction<*>) {
        this as DerivableSVFunction<T>
    } else {
        DerivableSVFunctionWrapper(this)
    }
}

/**
 * Composes this function to another DerivableFunction with given [formalAdd] and [formalMultiply].
 */
fun <T : Any, R : Any, S : Any> DerivableFunction<T, R>.composeDerivable(g: DerivableFunction<R, S>,
                                                                         formalAdd: (S, S) -> S,
                                                                         formalMultiply: (R, S) -> S)
        : DerivableFunction<T, S> = DerivableFunction.compose(this, g, formalAdd, formalMultiply)

internal class DerivableSVFunctionWrapper<T : Any>(private val ori: DerivableFunction<T, T>) : DerivableSVFunction<T> {
    override fun derive(): DerivableSVFunction<T> {
        return DerivableSVFunctionWrapper(ori.derive())
    }

    override fun apply(x: T): T {
        return ori.apply(x)
    }

    override fun domain(): MathSet<T> = ori.domain()
}

/**
 * The mapper should not be derived.
 */
internal class AndThenHomoDerivableFunction<T : Any, R : Any, S : Any>(val f: DerivableFunction<T, R>, val homo: (R) -> S) : DerivableFunction<T, S> {
    override fun apply(x: T): S {
        return homo(f(x))
    }

    override fun domain(): MathSet<T> {
        return f.domain()
    }

    override fun derive(): DerivableFunction<T, S> {
        return AndThenHomoDerivableFunction(f.derive(), homo)
    }
}

/**
 * The mapper should not be derived.
 */
internal class MappedSVDerivableFunction<T : Any, S : Any>(val f: DerivableSVFunction<T>, private val homo: Bijection<T, S>) : DerivableSVFunction<S> {
    private val newDomain = f.domain().mapTo(homo)

    override fun derive(): DerivableSVFunction<S> {
        return MappedSVDerivableFunction(f.derive(), homo)
    }

    override fun apply(x: S): S {
        return homo(f(homo.deply(x)))
    }

    override fun domain(): MathSet<S> {
        return newDomain
    }

}

/**
 * The mapper should not be derived.
 */
internal open class DerivableMergeOf2<T : Any, R1 : Any, R2 : Any, R : Any>(fx: DerivableFunction<T, R1>,
                                                                            gx: DerivableFunction<T, R2>,
                                                                            merger: (R1, R2) -> R) : MergeOf2<T, R1, R2, R>(fx, gx, merger), DerivableFunction<T, R> {

    override val f: DerivableFunction<T, R1> = fx

    override val g: DerivableFunction<T, R2> = gx

    override fun derive(): DerivableFunction<T, R> {
        return DerivableMergeOf2(f.derive(), g.derive(), merger)
    }

    override fun apply(x: T): R {
        return merger(f(x), g(x))
    }
}

/**
 * The mapper should not be derived.
 */
internal class DerivableMergeOf3<T : Any, R1 : Any, R2 : Any, R3 : Any, S : Any>(val f1: DerivableFunction<T, R1>,
                                                                                 val f2: DerivableFunction<T, R2>,
                                                                                 val f3: DerivableFunction<T, R3>,
                                                                                 val merger: (R1, R2, R3) -> S)
    : DerivableFunction<T, S> {
    private val intersectDomain = MathSets.unionOf(f1.domain(), f2.domain(), f3.domain())!!

    override fun derive(): DerivableFunction<T, S> {
        return DerivableMergeOf3(f1.derive(), f2.derive(), f3.derive(), merger)
    }

    override fun apply(x: T): S {
        return merger(f1(x), f2(x), f3(x))
    }

    override fun domain(): MathSet<T> = intersectDomain
}


internal open class DerivableAdd<T : Any, R1 : Any, R2 : Any, R : Any>(f: DerivableFunction<T, R1>,
                                                                       g: DerivableFunction<T, R2>,
                                                                       formalAdd: (R1, R2) -> R)
    : DerivableMergeOf2<T, R1, R2, R>(f, g, formalAdd) {

    override fun derive(): DerivableFunction<T, R> {
        return DerivableAdd(f.derive(), g.derive(), merger)
    }
}

internal class DerivableAddSV<T : Any>(override val f: DerivableSVFunction<T>,
                                       override val g: DerivableSVFunction<T>,
                                       formalAdd: (T, T) -> T) : DerivableAdd<T, T, T, T>(f, g, formalAdd), DerivableSVFunction<T> {
    override fun derive(): DerivableSVFunction<T> {
        return DerivableAddSV(f.derive(), g.derive(), merger)
    }
}

internal open class DerivableMultiply<T : Any, R1 : Any, R2 : Any, R : Any>(f: DerivableFunction<T, R1>,
                                                                            g: DerivableFunction<T, R2>,
                                                                            formalMultiply: (R1, R2) -> R,
                                                                            open val formalAdd: (R, R) -> R)
    : DerivableMergeOf2<T, R1, R2, R>(f, g, formalMultiply) {

    override fun derive(): DerivableFunction<T, R> {
        //f(x) * g(x)
        //f'(x) * g(x) + f(x) * g'(x)
        val partA = DerivableMultiply(f.derive(), g, merger, formalAdd)
        val partB = DerivableMultiply(f, g.derive(), merger, formalAdd)
        return DerivableAdd(partA, partB, formalAdd)
    }
}

internal class DerivableMultiplySV<T : Any>(override val f: DerivableSVFunction<T>,
                                            override val g: DerivableSVFunction<T>,
                                            formalMultiply: (T, T) -> T,
                                            override val formalAdd: (T, T) -> T)
    : DerivableMultiply<T, T, T, T>(f, g, formalMultiply, formalAdd), DerivableSVFunction<T> {
    override fun derive(): DerivableSVFunction<T> {
        //f(x) * g(x)
        //f'(x) * g(x) + f(x) * g'(x)
        val partA = DerivableMultiplySV(f.derive(), g, merger, formalAdd)
        val partB = DerivableMultiplySV(f, g.derive(), merger, formalAdd)
        return DerivableAddSV(partA, partB, formalAdd)
    }
}

internal open class DerivableCompose<T : Any, R : Any, S : Any>(private val f: DerivableFunction<T, R>,
                                                                private val g: DerivableFunction<R, S>,
                                                                private val formalMultiply: (R, S) -> S,
                                                                private val formalAdd: (S, S) -> S) : DerivableFunction<T, S> {
    override fun derive(): DerivableFunction<T, S> {
        //g(f(x))
        //f'(x) * g'(f(x))
        val left = f.derive()
        val right = DerivableCompose(f, g.derive(), formalMultiply, formalAdd)
        return DerivableMultiply(left, right, formalMultiply, formalAdd)
    }

    override fun apply(x: T): S {
        return g(f(x))
    }
}


internal open class DerivableComposeSV<T : Any>(private val f: DerivableSVFunction<T>,
                                                private val g: DerivableSVFunction<T>,
                                                private val formalMultiply: (T, T) -> T,
                                                private val formalAdd: (T, T) -> T) : DerivableSVFunction<T> {
    override fun derive(): DerivableSVFunction<T> {
        //g(f(x))
        //f'(x) * g'(f(x))
        val left = f.derive()
        val right = DerivableComposeSV(f, g.derive(), formalMultiply, formalAdd)
        return DerivableMultiplySV(left, right, formalMultiply, formalAdd)
    }

    override fun apply(x: T): T {
        return g(f(x))
    }
}
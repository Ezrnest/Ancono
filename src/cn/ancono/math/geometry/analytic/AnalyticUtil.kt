package cn.ancono.math.geometry.analytic

import cn.ancono.math.MathCalculator
import cn.ancono.math.algebra.linear.Matrix
import cn.ancono.math.algebra.linear.Vector
import cn.ancono.math.function.AbstractSVFunction
import cn.ancono.math.function.DerivableFunction
import cn.ancono.math.function.DerivableSVFunction
import cn.ancono.math.geometry.analytic.space.SVector
import cn.ancono.math.geometry.differential.DVFunction


/**
 * Contains some utility methods for analytic geometry.
 */
object AnalyticUtil {

    /**
     * Returns the matrix product of two derivable matrix function as a derivable function.
     */
    fun <T : Any> matrixMultiply(f: DerivableFunction<T, out Matrix<T>>, g: DerivableFunction<T, out Matrix<T>>): DerivableFunction<T, Matrix<T>> {
        return DerivableFunction.multiply(f, g, { a, b -> Matrix.add(a, b) }, { a, b -> Matrix.multiply(a, b) })
    }

    /**
     * Returns the inner product of two derivable vector function as a derivable function.
     */
    fun <T : Any> innerProduct(mc: MathCalculator<T>, f: DerivableFunction<T, out Vector<T>>, g: DerivableFunction<T, out Vector<T>>
    ): DerivableSVFunction<T> {
        return DerivableFunction.multiplySV(f, g, mc::add, Vector<T>::innerProduct)
    }

    /**
     * Returns the outer product of two derivable vector function as a derivable function.
     */
    fun <T : Any> outerProduct(f: DVFunction<T>, g: DVFunction<T>
    ): DVFunction<T> {
        return DerivableFunction.multiply(f, g, SVector<T>::add, SVector<T>::outerProduct)
    }

    /**
     * Returns the outer product of two derivable vector function as a derivable function.
     */
    fun <T : Any> mixedProduct(f: DVFunction<T>, g: DVFunction<T>, h: DVFunction<T>, mc: MathCalculator<T>
    ): DerivableSVFunction<T> {
        return DerivableFunction.multiplyAllSV(listOf(f, g, h), { ls -> SVector.mixedProduct(ls[0], ls[1], ls[2]) }, { ls ->
            ls.reduce(mc::add)
        })
    }

    /**
     * Returns the length of a derivable vector function as a derivable function.
     */
    fun <T : Any> length(mc: MathCalculator<T>, f: DerivableFunction<T, out Vector<T>>): DerivableSVFunction<T> {
        val l2 = innerProduct(mc, f, f)
        val sqrt = AbstractSVFunction.sqrt(mc)
        return DerivableFunction.composeSV(l2, sqrt, mc)
    }

    /**
     * Returns a derivable function of `f(t)/|f(t)|`
     */
    fun <T : Any> unitVector(mc: MathCalculator<T>, f: DerivableFunction<T, Vector<T>>): DerivableFunction<T, Vector<T>> {
        val len = length(mc, f)
        @Suppress("UNCHECKED_CAST")
        return DerivableFunction.divide(f, len, mc,
                { v1, v2 -> Vector.subtract(v1, v2) },
                { k, v -> v.multiplyNumber(k) })
    }

    /**
     * Returns a derivable function of `f(t)/|f(t)|`
     */
    fun <T : Any> unitVectorSpace(mc: MathCalculator<T>, f: DerivableFunction<T, SVector<T>>): DerivableFunction<T, SVector<T>> {
        val len = length(mc, f)
        @Suppress("UNCHECKED_CAST")
        return DerivableFunction.divide(f, len, mc,
                { v1, v2 -> v1.subtract(v2) },
                { k, v -> v.multiplyNumber(k) })
    }


}
package cn.ancono.math.geometry.spaces

import cn.ancono.math.MathCalculator
import cn.ancono.math.algebra.linearAlgebra.Vector


/*
 * Created by liyicheng at 2020-09-26 12:22
 */

/**
 * An (Euclidean or Hermite) inner product is a mapping `(·,·)`: **V** × **V** -> **F**  satisfying that:
 * 1. `(x,x) >= 0`
 * 2. `(kx,y) = k(x,y)`
 * 3. `(x+y,z) = (x,z) + (y,z)`
 * 4. `(x,y) = (y,x)` or `(x,y) = conj(y,x)`, the latter is for Hermite inner product, `conj` is the complex conjugate.
 */
@FunctionalInterface
fun interface InnerProduct<in T : Any, out F : Any> {
    fun apply(x: T, y: T): F

    companion object {

        /**
         * Returns the canonical inner product of vectors.
         */
        fun <T : Any> canonical(): InnerProduct<Vector<T>, T> {
            return InnerProduct { x, y ->
                x.innerProduct(y)
            }
        }
    }
}


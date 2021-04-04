package cn.ancono.math.numberModels

import cn.ancono.math.MathCalculator
import cn.ancono.math.MathObjectExtend
import cn.ancono.math.numberModels.api.AlgebraModel
import cn.ancono.utilities.IterUtils
import java.util.function.Function

typealias Index = IntArray
/**
 * Created at 2019/9/12 11:11
 *
 * Specified by lyc at 2021-03-31 22:26
 * @author  lyc
 */

//fun test(){
//    (1..65).asSequence().scanReduce()
////    CollectionSup.cartesianProductM()
//}


//interface ITensor<T>{
//
//
//
//
//}

/**
 * ## Mathematical Description
 *
 * A tensor is an element in the tensor product space of several (finite dimensional) linear spaces `V_1,...,V_m` on
 * a field `T`. Denote `V = V_1⊗...⊗V_m` the tensor product space, then `V` is also a linear space on `T`.
 *
 * Assume `dim V_i = n_i`, and `v(i,1),...,v(i,n_i)` are the basis of `V_i`.
 * We have `dim V = n_1*...*n_m`, and the basis of `V` are `v(1,r_1)⊗v(2,r_2)⊗...⊗v(m,r_m)`,
 * and each element in `V` can be written as their linear combination.
 *
 * We call the dimensions of the component linear spaces `(n_1,...,n_m)` the shape of the tensor,
 * and `m` the dimension of the tensor.
 *
 * ## Programming Description
 *
 * A tensor can be viewed as a multi-dimensional array of type `T`. Its shape is the lengths of arrays in the
 * corresponding dimensions. We call each of the dimension an 'axis' and also use 'dimension' to indicate
 * the number of dimensions.
 *
 *
 *
 */
abstract class Tensor<T : Any>(
        mc: MathCalculator<T>,
        /**
         * The shape of the tensor, it should not be modified
         */
        protected val sh: IntArray) : MathObjectExtend<T>(mc), AlgebraModel<T, Tensor<T>> {

    //Created by lyc at 2021-03-31 20:39

    /**
     * Gets the shape of this tensor. The returned array is a copy
     */
    val shape: IntArray
        get() = sh.clone()

    /**
     * The dimension of this tensor, which is equal to the length of [shape].
     */
    val dim: Int
        get() = sh.size

    /**
     * Returns the indices of this tensor. The indices iterates the last axis first.
     */
    val indices: Sequence<Index>
        get() = IterUtils.prodIdx(sh)

    /**
     * Gets an element in this tensor according to the index.
     *
     * @param idx the index, it is required that `0 <= idx < shape`
     */
    abstract operator fun get(idx: Index): T


    /**
     * Determines whether this tensor is all-zero.
     */
    abstract override fun isZero(): Boolean

    /**
     * Returns the element-wise sum of this tensor and `y`.
     *
     * The sum of two tensor `x,y` has the
     * shape of `max(x.shape, y.shape)`, here `max` means element-wise maximum of two arrays.
     */
    abstract override fun add(y: Tensor<T>): Tensor<T>

    /**
     * Returns the negate of this tensor.
     *
     */
    abstract override fun negate(): Tensor<T>

    /**
     * Returns the sum of this tensor and `y`.
     *
     * The sum of two tensor `x,y` has the
     * shape of `max(x.shape, y.shape)`, here `max` means element-wise maximum of two arrays.
     */
    abstract override fun subtract(y: Tensor<T>): Tensor<T>

    /**
     * Returns the result of multiplying this tensor with a scalar.
     */
    abstract override fun multiply(k: T): Tensor<T>

    /**
     * Returns the **element-wise** product of this tensor and `y`.
     *
     */
    abstract override fun multiply(y: Tensor<T>): Tensor<T>

    /**
     * Returns the **element-wise** division of this tensor and `y`.
     *
     * @throws ArithmeticException if zero-division happens
     */
    abstract fun divide(y: Tensor<T>): Tensor<T>


    /**
     * Returns the wedge product of this tensor and `y`.
     *
     * The tensor product of two tensor `z = x⊗y` has the
     * shape of `x.shape + y.shape`, here `+` means concatenation of two arrays.
     *
     * The `[i,j]` element of `z` is equal to the scalar product of `x[i]` and `y[j]`, that is,
     *
     *     z[i,j] = x[i] * y[j]
     *
     */
    abstract fun wedge(y: Tensor<T>): Tensor<T>


    abstract override fun <N : Any> mapTo(newCalculator: MathCalculator<N>, mapper: Function<T, N>): Tensor<N>

    /*
    Array-like operations below:
     */

    /**
     * Returns a view of this tensor according to the slicing ranges or indices.
     * It is required that the elements are either `Int` or `IntProgression`.
     */
    abstract fun slice(ranges: List<Any>): Tensor<T>
    // we need union type here


    /**
     * The operator-overloading version of the method [slice].
     */
    abstract operator fun get(vararg ranges: Any): Tensor<T>

    /**
     * Returns a transposed view of this tensor.
     * The `i`-th axis in the resulting tensor corresponds to the `newAxis[i]`-th axis in this tensor.
     */
    abstract fun transpose(newAxis: IntArray): Tensor<T>

}


//class ATensor()

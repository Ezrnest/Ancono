package cn.ancono.math.property


/**
 * A math tuple is a ordered pair of a specific size.
 * Created at 2018/9/21 11:39
 * @author  liyicheng
 */
interface MathTuple<T> {
    operator fun get(i: Int): T

    val size: Int
}
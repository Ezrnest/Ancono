package cn.ancono.math.algebra.abs.calculator


/*
 * Created at 2018/11/29 18:46
 * @author  liyicheng
 */
interface AlgebraCalculator<K, V> : LinearSpaceCalculator<K, V>, RingCalculator<V> {

    @JvmDefault
    @Deprecated("use {@link #add(Object, Object)} instead for more clarity.", ReplaceWith("add(x, y)"))
    override fun apply(x: V, y: V): V = add(x, y)

    @JvmDefault
    @Deprecated("use {@link #negate(Object)} instead", ReplaceWith("negate(x)"))
    override fun inverse(x: V): V {
        return negate(x)
    }

    @JvmDefault
    @get:Deprecated("use {@link #getZero()} instead", ReplaceWith("zero"))
    override val identity: V
        get() = zero

    @JvmDefault
    @Deprecated("use {@link #multiplyLong(Object, long)} instead.", ReplaceWith("multiplyLong(x,n)"))
    override fun gpow(x: V, n: Long): V {
        return multiplyLong(x, n)
    }
}
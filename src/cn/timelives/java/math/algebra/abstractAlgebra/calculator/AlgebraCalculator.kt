package cn.timelives.java.math.algebra.abstractAlgebra.calculator


/*
 * Created at 2018/11/29 18:46
 * @author  liyicheng
 */
interface AlgebraCalculator<K:Any,V:Any> : VectorSpaceCalculator<K,V>, RingCalculator<V>{

    @Deprecated("use {@link #add(Object, Object)} instead for more clarity.", ReplaceWith("add(x, y)"))
    override fun apply(x: V, y: V): V = add(x,y)

}
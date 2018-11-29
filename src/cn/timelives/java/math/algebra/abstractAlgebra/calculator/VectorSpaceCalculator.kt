package cn.timelives.java.math.algebra.abstractAlgebra.calculator


/**
 * Created at 2018/11/29 16:57
 * @author  liyicheng
 * @see [cn.timelives.java.math.algebra.linearAlgebra.space.VectorSpace]
 */
interface VectorSpaceCalculator<K:Any, V :Any> : ModuleCalculator<K,V>{
    override val scalarCalculator: FieldCalculator<K>
}
package cn.timelives.java.math.algebra.abstractAlgebra.field.finite

import cn.timelives.java.math.algebra.abstractAlgebra.calculator.FieldCalculator
import cn.timelives.java.math.algebra.abstractAlgebra.structure.finite.FiniteField


/*
 * Created at 2018/11/10 13:57
 * @author  liyicheng
 */
abstract class AbstrctFiniteField<T : Any>(val fc : FieldCalculator<T>) : FiniteField<T>{

}
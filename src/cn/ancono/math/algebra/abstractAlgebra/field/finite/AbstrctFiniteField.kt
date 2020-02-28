package cn.ancono.math.algebra.abstractAlgebra.field.finite

import cn.ancono.math.algebra.abstractAlgebra.calculator.FieldCalculator
import cn.ancono.math.algebra.abstractAlgebra.structure.finite.FiniteField


/*
 * Created at 2018/11/10 13:57
 * @author  liyicheng
 */
abstract class AbstrctFiniteField<T : Any>(val fc: FieldCalculator<T>) : FiniteField<T> {

}
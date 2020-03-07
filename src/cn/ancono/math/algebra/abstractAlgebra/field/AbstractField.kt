package cn.ancono.math.algebra.abstractAlgebra.field

import cn.ancono.math.algebra.abstractAlgebra.calculator.FieldCalculator
import cn.ancono.math.algebra.abstractAlgebra.calculator.RingCalculator
import cn.ancono.math.algebra.abstractAlgebra.structure.Field
import cn.ancono.math.set.MathSet


/*
 * Created by liyicheng at 2020-03-06 15:19
 */
//abstract class AbstractField<T:Any> : AbstractRing<T>(), Field<T> {
//
//}

class FieldFromCal<T : Any>(private val fc: FieldCalculator<T>, private val mathSet: MathSet<T>) : Field<T> {
    override fun getCalculator(): FieldCalculator<T> {
        return fc
    }

    override fun getSet(): MathSet<T> {
        return mathSet
    }

    override fun unit(): T {
        return calculator.one
    }

}
package cn.ancono.math.algebra.abs.field

import cn.ancono.math.algebra.abs.calculator.FieldCalculator
import cn.ancono.math.algebra.abs.structure.Field
import cn.ancono.math.set.MathSet


/*
 * Created by liyicheng at 2020-03-06 15:19
 */
//abstract class AbstractField<T> : AbstractRing<T>(), Field<T> {
//
//}

class FieldFromCal<T>(private val fc: FieldCalculator<T>, private val mathSet: MathSet<T>) : Field<T> {
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
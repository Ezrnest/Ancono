package cn.ancono.math.algebra.abs.ring

import cn.ancono.math.algebra.abs.calculator.RingCalculator
import cn.ancono.math.algebra.abs.structure.Ring
import cn.ancono.math.set.MathSet


class RingFromCal<T>(private val rc: RingCalculator<T>, private val mathSet: MathSet<T>) : Ring<T> {
    override fun getCalculator(): RingCalculator<T> {
        return rc
    }

    override fun getSet(): MathSet<T> {
        return mathSet
    }

}

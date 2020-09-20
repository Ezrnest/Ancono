package cn.ancono.math.algebra.abstractAlgebra.ring

import cn.ancono.math.algebra.abstractAlgebra.calculator.RingCalculator
import cn.ancono.math.algebra.abstractAlgebra.structure.Ring
import cn.ancono.math.set.MathSet


class RingFromCal<T : Any>(private val rc: RingCalculator<T>, private val mathSet: MathSet<T>) : Ring<T> {
    override fun getCalculator(): RingCalculator<T> {
        return rc
    }

    override fun getSet(): MathSet<T> {
        return mathSet
    }

}

package cn.ancono.math.algebra.abs.group

import cn.ancono.math.algebra.abs.calculator.GroupCalculator
import cn.ancono.math.algebra.abs.structure.AbelianGroup
import cn.ancono.math.algebra.abs.structure.Group
import cn.ancono.math.set.MathSet


/*
 * Created by liyicheng at 2020-03-06 12:53
 */
/**
 * @author liyicheng
 */
class GroupFromCal<T>(private val gc: GroupCalculator<T>, private val elements: MathSet<T>) : Group<T> {
    override fun getCalculator(): GroupCalculator<T> {
        return gc
    }

    override fun getSet(): MathSet<T> {
        return elements
    }

}

class AbelGroupFromCal<T>(private val gc: GroupCalculator<T>, private val elements: MathSet<T>) : AbelianGroup<T> {
    override fun getCalculator(): GroupCalculator<T> {
        return gc
    }

    override fun getSet(): MathSet<T> {
        return elements
    }
}
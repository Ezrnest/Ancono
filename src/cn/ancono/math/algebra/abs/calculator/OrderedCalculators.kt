package cn.ancono.math.algebra.abs.calculator


/*
 * Created by liyicheng at 2021-05-06 19:32
 */


interface OrderedAbelGroupCal<T> : AbelGroupCal<T>, TotalOrderPredicate<T> {

    fun abs(x: T): T {
        if (compare(x, zero) < 0) {
            return -x
        }
        return x
    }
}

interface OrderedRingCal<T> : RingCalculator<T>, OrderedAbelGroupCal<T>

interface OrderedFieldCal<T> : FieldCalculator<T>, OrderedRingCal<T>


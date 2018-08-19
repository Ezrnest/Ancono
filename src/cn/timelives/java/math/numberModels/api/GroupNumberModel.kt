package cn.timelives.java.math.numberModels.api

interface GroupNumberModel<T : GroupNumberModel<T>> {
    fun add(y: T): T

    fun negate(): T

    fun subtract(y: T): T = add(y.negate())



}

operator fun <T : GroupNumberModel<T>> GroupNumberModel<T>.plus(y: T): T = add(y)

operator fun <T : GroupNumberModel<T>> GroupNumberModel<T>.unaryMinus(): T = negate()

interface RingNumberModel<T : RingNumberModel<T>> : GroupNumberModel<T> {
    fun multiply(y: T): T
}

operator fun <T : RingNumberModel<T>> RingNumberModel<T>.times(y: T): T = multiply(y)

interface DivisionRingNumberModel<T : DivisionRingNumberModel<T>> : RingNumberModel<T> {
    fun reciprocal(): T
    fun divide(y: T): T = multiply(y.reciprocal())


}

operator fun <T : DivisionRingNumberModel<T>> DivisionRingNumberModel<T>.div(y: T): T = divide(y)
package cn.timelives.java.math.numberModels.api

interface GroupNumberModel<T : GroupNumberModel<T>> {
    fun add(y: T): T

    fun negate(): T

    fun subtract(y: T): T = add(y.negate())

    operator fun plus(y: T): T = add(y)

    operator fun unaryMinus(): T = negate()

}

interface RingNumberModel<T : RingNumberModel<T>> : GroupNumberModel<T> {
    fun multiply(y: T): T

    operator fun times(y: T): T = multiply(y)

}

interface FeildNumberModel<T : FeildNumberModel<T>> : RingNumberModel<T> {
    fun reciprocal(): T

    fun divide(y: T): T = multiply(y.reciprocal())

    operator fun div(y: T): T = divide(y)

}
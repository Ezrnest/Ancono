package cn.ancono.math.algebra.abs.calculator

interface TotalOrderPredicate<T> : EqualPredicate<T>, Comparator<T> {


    @JvmDefault
    operator fun T.compareTo(y: T): Int = compare(this, y)
}
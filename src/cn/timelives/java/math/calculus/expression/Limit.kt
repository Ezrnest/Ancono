package cn.timelives.java.math.calculus.expression

import cn.timelives.java.math.numberModels.expression.Expression


enum class LimitDirection{
    /**
     * Process : from left
     * Result : from left to positive infinite
     */
    LEFT,
    /**
     * Process : from right
     * Result : from right to negative infinite
     */
    RIGHT,
    /**
     * Process : both
     * Result : just infinite
     */
    BOTH;
    fun signum() : Int{
        return when(this){
            LimitDirection.LEFT -> -1
            LimitDirection.RIGHT -> 1
            LimitDirection.BOTH -> 0
        }
    }

    companion object {
        fun fromSignum(s : Int):LimitDirection{
            return when{
                s > 0 -> RIGHT
                s == 0 -> BOTH
                else -> LEFT
            }
        }
    }
}


sealed class LimitValue<T>{
    abstract val isFinite : Boolean
    abstract val value : T
    companion object {
        fun <T> infiniteValue() : LimitValue<T>{
            @Suppress("UNCHECKED_CAST")
            return InfiniteValue as LimitValue<T>
        }

        fun <T> valueOf(x : T): LimitValue<T>{
            return FiniteValue(x)
        }
    }
}

data class FiniteValue<T>(override val value : T) : LimitValue<T>(){
    override val isFinite: Boolean
        get() = true
}



object InfiniteValue : LimitValue<Any>(){
    override val isFinite: Boolean
        get() = false
    override val value: Expression
        get() = throw UnsupportedOperationException()
}


/*
 * Created at 2018/10/20 12:05
 * @author  liyicheng
 */
data class LimitProcess<T>(val variableName : String,val value : LimitValue<T>, val direction : LimitDirection)

data class LimitResult<T>(val value: LimitValue<T>, val direction: LimitDirection){
    companion object {
        val POSITIVE_INF : LimitResult<Any> = LimitResult(LimitValue.infiniteValue(),LimitDirection.LEFT)
        val NEGATIVE_INF : LimitResult<Any> = LimitResult(LimitValue.infiniteValue(),LimitDirection.RIGHT)
        val INFINITE : LimitResult<Any> = LimitResult(LimitValue.infiniteValue(),LimitDirection.BOTH)
        fun <T> finiteValueOf(v : T) : LimitResult<T>{
            return LimitResult(FiniteValue(v),LimitDirection.BOTH)
        }

        fun <T> finiteValueOf(v : T, s : Int) : LimitResult<T>{
            return LimitResult(FiniteValue(v), LimitDirection.fromSignum(s))
        }

        @Suppress("UNCHECKED_CAST")
        fun <T> positiveInf() : LimitResult<T>{
            return POSITIVE_INF as LimitResult<T>
        }

        @Suppress("UNCHECKED_CAST")
        fun <T> negativeInf() : LimitResult<T>{
            return NEGATIVE_INF as LimitResult<T>
        }

        /**
         * Note: [s] is opposite from LimitDirection's signum.
         */
        @Suppress("UNCHECKED_CAST")
        fun <T> infiniteFromSignum(s : Int) : LimitResult<T>{
            return when{
                s > 0 -> POSITIVE_INF
                s == 0 -> INFINITE
                else -> NEGATIVE_INF
            } as LimitResult<T>
        }
    }
}


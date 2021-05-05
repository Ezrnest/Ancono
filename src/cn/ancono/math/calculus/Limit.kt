package cn.ancono.math.calculus

import cn.ancono.math.MathUtils
import cn.ancono.math.calculus.expression.FunctionHelper
import cn.ancono.math.calculus.expression.LimitProcessE
import cn.ancono.math.calculus.expression.LimitResultE
import cn.ancono.math.exceptions.ExceptionUtil
import cn.ancono.math.numberModels.Fraction
import cn.ancono.math.numberModels.Multinomial
import cn.ancono.math.numberModels.Term
import cn.ancono.math.numberModels.api.Computable
import cn.ancono.math.numberModels.api.RealCalculator
import cn.ancono.math.numberModels.expression.ExprCalculator
import cn.ancono.math.numberModels.expression.Expression

private fun constNotSupportInf(): Nothing {
    throw ArithmeticException("CONST doesn't apply for infinity!")
}

/**
 * Provides basic utility functions for calculating limits.
 */
object Limit {

    /**
     * Determines the monotonicity of [f], returns zero if it cannot be determined
     */
    private fun monoType(f: Multinomial, variable: String, value: Expression): Int {
        val derivated = Calculus.derivation(f, variable)
        val signum: Int
        signum = try {
            val actualValue = value.computeDouble(Computable.DEFAULT_OR_EXCEPTION)
            MathUtils.signum(derivated.computeDouble(Computable.withDefault {
                when (it) {
                    variable -> actualValue
                    else -> Double.NaN
                }
            }))
        } catch (e: Exception) {
            0
        }
        return signum
    }

    /**
     * Returns the limit of a term by the process.
     */
    @JvmStatic
    fun limitOf(x: Term, process: LimitProcessE, mc: ExprCalculator = ExprCalculator.instance): LimitResultE {
        val variable = process.variableName
        if (!x.containsChar(variable)) {
            return LimitResult.constantOf(Expression.fromTerm(x))
        }
        val pow = x.getCharacterPower(variable)
        val reWithoutCoe = Limit.power(process, pow, mc)
        val coe = Expression.fromTerm(x.removeChar(variable))
        return multiplyConst(coe, reWithoutCoe, mc)
    }

    /**
     * Computes the limit of a multinomial.
     */
    @JvmStatic
    fun limitOf(expr: Multinomial, process: LimitProcessE, mc: ExprCalculator = ExprCalculator.instance)
            : LimitResultE {
        if (process.direction == LimitDirection.CONST) {
            return LimitResult.constantOf(
                    mc.substitute(Expression.fromMultinomial(expr), process.variableName, process.value.value))
        }
        val variable = process.variableName
        if (!expr.containsChar(variable)) {
            return LimitResult.constantOf(Expression.fromMultinomial(expr))
        }
        val (f, constant) = expr.terms.partition { it.containsChar(variable) }

        if (!process.value.isFinite) {
            val top = f.maxByOrNull { t: Term -> t.getCharacterPower(variable) }!! // x^666
            val lim = limitOf(top, process)
            return Limit.addConst(lim, mc) { Expression.fromMultinomial(Multinomial.fromTerms(constant)) }
        }
        val value = process.value.value
        if (!mc.isZero(value)) {
            val signum0 = process.direction.signum()
            val signum: Int = monoType(Multinomial.fromTerms(f), variable, value)
            val re = mc.substitute(
                    Expression.fromMultinomial(expr), variable, value)
            return LimitResult.finiteValueOf(re, signum * signum0)
        }
        val bot = f.minByOrNull { it: Term -> it.getCharacterPower(variable) }!!// x ^ (-666)
        val lim = limitOf(bot, process, mc)
        return Limit.addConst(lim, mc) { Expression.fromMultinomial(Multinomial.fromTerms(constant)) }
    }

    /**
     * Returns the limit of a fraction of multinomial.
     */
    @JvmStatic
    fun fractionPoly(nume: Multinomial, deno: Multinomial, process: LimitProcessE,
                     mc: ExprCalculator = ExprCalculator.instance): LimitResultE {
        if (deno.isZero()) {
            ExceptionUtil.dividedByZero()
        }
        val reNume = limitOf(nume, process, mc)
        val reDeno = limitOf(deno, process, mc)
        divide(reNume, reDeno, mc)?.apply { return this }
        //must be 0/0 or inf/inf
        val variableName = process.variableName
        // 0/0
        return if (process.value.isFinite) {
            val v = process.value.value
            if (mc.isZero(v)) {
                fractionPoly0(nume, deno, process, mc)
            } else {
                fractionPoly(
                        Calculus.derivation(nume, variableName),
                        Calculus.derivation(deno, variableName),
                        process, mc)
            }
        } else {
            fractionPoly0(nume, deno, process, mc)
        }

    }

    /**
     * Returns the limit of an expression.
     */
    @JvmStatic
    fun limitOf(expr: Expression, process: LimitProcessE, mc: ExprCalculator = ExprCalculator.instance): LimitResultE? {
        return FunctionHelper.limitNode(expr.root, process, mc)
    }

    private fun fractionPoly0(nume: Multinomial, deno: Multinomial, process: LimitProcessE, mc: ExprCalculator)
            : LimitResultE {
        // 0 / 0 type or Inf / Inf type
        val variable = process.variableName
        val f1 = nume.terms
        val f2 = deno.terms
        if (process.value.isFinite) {
            // x -> 0, find the lowest term
            val low1 = f1.minByOrNull { it: Term -> it.getCharacterPower(variable) }!!
            val low2 = f2.minByOrNull { it: Term -> it.getCharacterPower(variable) }!!
            val p1 = low1.getCharacterPower(variable)
            val p2 = low2.getCharacterPower(variable)
            val signum = p1.signum * p2.signum * process.direction.signum()
            return when {
                p1 > p2 -> {//zero
                    LimitResult.finiteValueOf(mc.zero, signum)
                }
                p1 == p2 -> {//TODO determine the direction
                    LimitResult.finiteValueOf(Expression.fromTerm(low1.divide(low2)))
                }
                else -> {
                    LimitResult.infinityFromSignum(-signum)
                }
            }
        } else {
            //x -> Inf, find the highest term
            val top1 = f1.maxByOrNull { it: Term -> it.getCharacterPower(variable) }!!
            val top2 = f2.maxByOrNull { it: Term -> it.getCharacterPower(variable) }!!
            val p1 = top1.getCharacterPower(variable)
            val p2 = top2.getCharacterPower(variable)
            val signum = p1.signum * p2.signum * process.direction.signum()
            return when {
                p1 > p2 -> {//Inf
                    LimitResult.infinityFromSignum(-signum)
                }
                p1 == p2 -> {//TODO determine the direction
                    LimitResult.finiteValueOf(Expression.fromTerm(top1.divide(top2)))
                }
                else -> {
                    LimitResult.finiteValueOf(mc.zero, signum)
                }
            }
        }
    }


    /**
     * Returns the sum of two limit result if possible, otherwise returns `null`.
     */
    @JvmStatic
    fun <T> add(x: LimitResult<T>, y: LimitResult<T>, mc: RealCalculator<T>): LimitResult<T>? {
        if (x.isFinite) {
            return if (y.isFinite) {
                val re = mc.add(x.value.value, y.value.value)
                val direction = x.direction + y.direction
                LimitResult.finiteValueOf(re, direction)
            } else {
                y
            }
        } else {
            return when {
                y.isFinite -> x
                x.direction == y.direction -> LimitResult.infinityOf(x.direction)
                else -> //cannot determine
                    null
            }
        }
    }

    /**
     * Returns the sum of a limit result and a constant.
     */
    @JvmStatic
    inline fun <T> addConst(x: LimitResult<T>, mc: RealCalculator<T>, y: () -> T): LimitResult<T> {
        return if (x.isFinite) {
            LimitResult.finiteValueOf(mc.add(x.value.value, y()), x.direction)
        } else {
            x
        }
    }

    @JvmStatic
    fun <T> negate(x: LimitResult<T>, mc: RealCalculator<T>): LimitResult<T> {
        val direction = -x.direction
        return if (x.isFinite) {
            val re = mc.negate(x.value.value)
            LimitResult.finiteValueOf(re, direction)
        } else {
            LimitResult.infinityOf(direction)
        }
    }

    @JvmStatic
    fun <T> subtract(x: LimitResult<T>, y: LimitResult<T>, mc: RealCalculator<T>): LimitResult<T>? {
        return add(x, negate(y, mc), mc)
    }

    @JvmStatic
    fun <T> multiplySignum(x: LimitResult<T>, mc: RealCalculator<T>): LimitResult<T> {
        return if (x.isFinite) {
            val re = mc.negate(x.value.value)
            LimitResult.finiteValueOf(re, -x.direction)
        } else {
            LimitResult.infinityOf(-x.direction)
        }
    }

    @JvmStatic
    fun <T> multiplyConst(const: T, x: LimitResult<T>, mc: RealCalculator<T>): LimitResult<T> {
        if (x.direction == LimitDirection.CONST) {
            return LimitResult.constantOf(mc.multiply(const, x.value.value))
        }
        if (mc.isZero(const)) {
            return LimitResult.constantOf(mc.zero)
        }

        if (x.isFinite) {
            val re = mc.multiply(const, x.value.value)
            if (x.direction == LimitDirection.BOTH || !mc.isComparable) {
                return LimitResult.finiteValueOf(re)
            }
            val s = signumOf(const, mc)
            return LimitResult.finiteValueOf(re, s * x.direction.signum())
        } else {
            if (x.direction == LimitDirection.BOTH) {
                return x
            }
            val s = signumOf(const, mc)
            return LimitResult.infinityOf(x.direction.multiplySignum(s))
        }
    }

    /**
     * Returns the limit of `x^p`.
     */
    fun power(x: LimitResultE, pow: Fraction, mc: ExprCalculator): LimitResultE {
        val even = (pow.numerator % 2 == 0L)
        if (!x.value.isFinite) {
            if (pow.isPositive) {
                //determine the direction
                val directionWithoutCoe = if (even) {
                    LimitDirection.LEFT
                } else {
                    x.direction
                }
                return LimitResult.infinityOf(directionWithoutCoe)
            } else {
                //negative pow
                val directionWithoutCoe = if (even) {
                    LimitDirection.RIGHT
                } else {
                    -x.direction
                }
                return LimitResult.finiteValueOf(mc.zero, directionWithoutCoe)
            }
        }
        val v = x.value.value
        if (mc.isZero(v)) {
            // lim(x -> +0, x^p) = lim(x -> +Inf, x^(-p))
            // lim(x -> -0, x^p) = lim(x -> -Inf, x^(-p))
            val reciprocal = Limit.reciprocal(x, mc)
            return power(reciprocal, pow.negate(), mc)
        }
        val re = mc.exp(v, Expression.fromTerm(Term.valueOf(pow)))
        val monotonicity = Monotonicity.power(x, pow, mc)
        return LimitResult.finiteValueOf(re, x.direction.multiplySignum(monotonicity))

    }


    /**
     * Returns the signum of a value of limit
     */
    private fun <T> signumOf(t: T, mc: RealCalculator<T>): Int {
        return try {
            val s = mc.compare(t, mc.zero)
            if (s >= 0) {
                1
            } else {
                -1
            }
        } catch (e: Exception) {
            0
        }
    }

    @JvmStatic
    fun <T> multiply(x: LimitResult<T>, y: LimitResult<T>, mc: RealCalculator<T>): LimitResult<T>? {
        fun infResultOf(t: T, dInf: LimitDirection): LimitResult<T>? {
            val comp = mc.compare(t, mc.zero)
            if (comp == 0) {
                return null
            }
            val s = comp * dInf.signum()
            return LimitResult.infinityFromSignum(s)
        }


        if (x.direction == LimitDirection.CONST) {
            return multiplyConst(x.value.value, y, mc)
        } else if (y.direction == LimitDirection.CONST) {
            return multiplyConst(y.value.value, x, mc)
        }

        if (x.isFinite) {
            if (y.isFinite) {

                val re = mc.multiply(x.value.value, y.value.value)

                val d1 = x.direction
                val d2 = y.direction
                if (d1 == LimitDirection.BOTH || d2 == LimitDirection.BOTH || !mc.isComparable) {
                    return LimitResult.finiteValueOf(re)
                }

                val vs1 = signumOf(x.value.value, mc)
                val vs2 = signumOf(y.value.value, mc)
                val s1 = d1.signum() * vs1
                val s2 = d2.signum() * vs2
                if (s1 != s2) {
                    return LimitResult.finiteValueOf(re)
                }
                val s = vs1 * vs2 * s1
                return LimitResult.finiteValueOf(re, s)
            } else {
                return infResultOf(x.value.value, y.direction)
            }
        } else {
            if (y.isFinite) {
                return infResultOf(y.value.value, x.direction)
            }
            return LimitResult.infinityFromSignum(-x.direction.signum() * y.direction.signum())
        }
    }

    @JvmStatic
    fun <T> reciprocal(x: LimitResult<T>, mc: RealCalculator<T>): LimitResult<T> {
        if (x.direction == LimitDirection.CONST) {
            return LimitResult.constantOf(mc.reciprocal(x.value.value))
        }
        val direction = -x.direction
        if (x.isFinite) {
            if (mc.isZero(x.value.value)) {
                return LimitResult.infinityOf(direction)
            }
            val re = mc.reciprocal(x.value.value)
            return LimitResult.finiteValueOf(re, direction)
        } else {
            val re = mc.zero
            return LimitResult.finiteValueOf(re, direction)
        }
    }

    @JvmStatic
    fun <T> divide(x: LimitResult<T>, y: LimitResult<T>, mc: RealCalculator<T>): LimitResult<T>? {
        return multiply(x, reciprocal(y, mc), mc)
    }


    @JvmStatic
    fun <T> composeUnidentified(x: LimitResult<T>, f: (T) -> T,
                                positiveInfLimit: () -> LimitResult<T>? = LimitResult.Companion::infinity,
                                negativeInfLimit: () -> LimitResult<T>? = LimitResult.Companion::infinity,
                                infLimit: () -> LimitResult<T>? = LimitResult.Companion::infinity): LimitResult<T>? {
        return if (x.isFinite) {
            LimitResult.finiteValueOf(f(x.value.value))
        } else {
            when (x.direction) {
                LimitDirection.CONST -> constNotSupportInf()
                LimitDirection.LEFT -> positiveInfLimit()
                LimitDirection.RIGHT -> negativeInfLimit()
                LimitDirection.BOTH -> infLimit()
            }
        }
    }

    @JvmStatic
    fun <T> composeMonoIncrease(x: LimitResult<T>, f: (T) -> T,
                                positiveInfLimit: () -> LimitValue<T> = LimitValue.Companion::infinity,
                                negativeInfLimit: () -> LimitValue<T> = LimitValue.Companion::infinity): LimitResult<T>? {
        return if (x.isFinite) {
            LimitResult.finiteValueOf(f(x.value.value), x.direction)
        } else {
            when (x.direction) {
                LimitDirection.CONST -> constNotSupportInf()
                LimitDirection.LEFT -> LimitResult(positiveInfLimit(), LimitDirection.LEFT)
                LimitDirection.RIGHT -> LimitResult(negativeInfLimit(), LimitDirection.RIGHT)
                LimitDirection.BOTH -> {
                    return if (!positiveInfLimit().isFinite && !negativeInfLimit().isFinite) {
                        LimitResult.infinityOf(LimitDirection.BOTH)
                    } else {
                        null
                    }
                }
            }
        }
    }

    @JvmStatic
    fun <T> composeMonoDecrease(x: LimitResult<T>, f: (T) -> T,
                                positiveInfLimit: () -> LimitValue<T> = LimitValue.Companion::infinity,
                                negativeInfLimit: () -> LimitValue<T> = LimitValue.Companion::infinity): LimitResult<T>? {
        return if (x.isFinite) {
            LimitResult.finiteValueOf(f(x.value.value), -x.direction)
        } else {
            when (x.direction) {
                LimitDirection.CONST -> constNotSupportInf()
                LimitDirection.LEFT -> LimitResult(positiveInfLimit(), LimitDirection.RIGHT)
                LimitDirection.RIGHT -> LimitResult(negativeInfLimit(), LimitDirection.LEFT)
                LimitDirection.BOTH -> {
                    return if (!positiveInfLimit().isFinite && !negativeInfLimit().isFinite) {
                        LimitResult.infinityOf(LimitDirection.BOTH)
                    } else {
                        null
                    }
                }
            }
        }
    }


}

fun ExprCalculator.limit(expr: Expression, process: LimitProcessE): LimitResultE? =
        Limit.limitOf(expr, process, this)

/**
 * Describes the direction of a limit.
 */
enum class LimitDirection {
    /**
     * Process : from left
     *
     * Result : from left to positive infinite
     */
    LEFT,
    /**
     * Process : from right
     *
     * Result : from right to negative infinite
     */
    RIGHT,
    /**
     * Process : both
     *
     * Result : just infinite
     */
    BOTH,

    /**
     *  Process : just substitute
     *
     *  Result : constant
     */
    CONST;

    fun signum(): Int {
        return when (this) {
            LEFT -> -1
            RIGHT -> 1
            BOTH -> 0
            CONST -> 1 // special case, treat it as identity
        }
    }


    operator fun plus(y: LimitDirection): LimitDirection {
        if (this == CONST) {
            return y
        }
        if (y == CONST) {
            return this
        }
        if (this == y) {
            return this
        }
        return BOTH
    }

    operator fun unaryMinus(): LimitDirection {
        return when (this) {
            LEFT -> RIGHT
            RIGHT -> LEFT
            BOTH -> BOTH
            CONST -> CONST
        }
    }

    /**
     * Multiply by [s]: positive -> change nothing, zero -> both, negative -> -this
     */
    fun multiplySignum(s: Int): LimitDirection {
        return when {
            s > 0 -> this
            s == 0 -> BOTH
            else -> -this
        }
    }

    /**
     * Return this if [s], else -this
     */
    fun sameOrInverse(s: Boolean): LimitDirection {
        return if (s) {
            this
        } else {
            -this
        }
    }

    companion object {
        /**
         * Returns a limit direction from signum: [RIGHT] for positive, [BOTH] for zero and [LEFT] for negative.
         */
        fun fromSignum(s: Int): LimitDirection {
            return when {
                s > 0 -> RIGHT
                s == 0 -> BOTH
                else -> LEFT
            }
        }
    }
}


sealed class LimitValue<T> {
    abstract val isFinite: Boolean
    abstract val value: T

    abstract fun <R> map(f: (T) -> R): LimitValue<R>

    companion object {
        fun <T> infinity(): LimitValue<T> {
            @Suppress("UNCHECKED_CAST")
            return InfiniteValue as LimitValue<T>
        }

        fun <T> valueOf(x: T): LimitValue<T> {
            return FiniteValue(x)
        }
    }
}

data class FiniteValue<T>(override val value: T) : LimitValue<T>() {
    override val isFinite: Boolean
        get() = true

    override fun <R> map(f: (T) -> R): LimitValue<R> {
        return FiniteValue(f(value))
    }
}


object InfiniteValue : LimitValue<Any>() {
    override val isFinite: Boolean
        get() = false
    override val value: Expression
        get() = throw UnsupportedOperationException()

    @Suppress("UNCHECKED_CAST")
    override fun <R> map(f: (Any) -> R): LimitValue<R> {
        return this as LimitValue<R>
    }
}


/*
 * Created at 2018/10/20 12:05
 * @author  liyicheng
 */
class LimitProcess<T>(val variableName: String, value: LimitValue<T>, direction: LimitDirection)
    : LimitResult<T>(value, direction) {

    override fun toString(): String {
        return buildString {
            append(variableName)
            if (direction == LimitDirection.CONST) {
                append("=")
            } else {
                append("→")
            }
            if (value.isFinite) {
                if (direction == LimitDirection.LEFT) {
                    append('-')
                } else if (direction == LimitDirection.RIGHT) {
                    append("+")
                }
                append(value.value)
            } else {
                append(when (direction) {
                    LimitDirection.LEFT -> "+∞"
                    LimitDirection.RIGHT -> "-∞"
                    LimitDirection.BOTH -> "∞"
                    LimitDirection.CONST -> constNotSupportInf()
                })
            }
        }

    }

    @Suppress("UNCHECKED_CAST")
    companion object {
        private val POSITIVE_INF: LimitProcess<Any> =
                LimitProcess("x", LimitValue.infinity(), LimitDirection.LEFT)
        private val NEGATIVE_INF: LimitProcess<Any> =
                LimitProcess("x", LimitValue.infinity(), LimitDirection.RIGHT)

        fun <T> toPositiveInf(): LimitProcess<T> = POSITIVE_INF as LimitProcess<T>
        fun <T> toNegativeInf(): LimitProcess<T> = NEGATIVE_INF as LimitProcess<T>
        fun <T> toPositiveZero(mc: RealCalculator<T>) = LimitProcess("x", LimitValue.valueOf(mc.zero), LimitDirection.RIGHT)

        fun <T> toNegativeZero(mc: RealCalculator<T>) = LimitProcess("x", LimitValue.valueOf(mc.zero), LimitDirection.LEFT)

        fun <T> toZero(mc: RealCalculator<T>) = LimitProcess("x", LimitValue.valueOf(mc.zero), LimitDirection.BOTH)

    }
}

open class LimitResult<T>
internal constructor(val value: LimitValue<T>, val direction: LimitDirection) {
    val isFinite: Boolean
        get() = value.isFinite

    val isPositiveInf: Boolean
        get() = !isFinite && direction == LimitDirection.LEFT

    val isNegativeInf: Boolean
        get() = !isFinite && direction == LimitDirection.RIGHT

    fun reverseDirection(): LimitResult<T> =
            LimitResult(value, -direction)

    override fun toString(): String {
        if (value.isFinite) {
            return buildString {
                if (direction == LimitDirection.LEFT) {
                    append("→ ")
                }
                append(value.value)
                if (direction == LimitDirection.RIGHT) {
                    append(" ←")
                }
            }
        } else {
            return when (direction) {
                LimitDirection.LEFT -> "+∞"
                LimitDirection.RIGHT -> "-∞"
                LimitDirection.BOTH -> "∞"
                LimitDirection.CONST -> constNotSupportInf()
            }
        }
    }

    fun <R> map(f: (T) -> R): LimitResult<R> {
        return LimitResult(value.map(f), direction)
    }

    companion object {
        private val POSITIVE_INF: LimitResult<Any> = LimitResult(LimitValue.infinity(), LimitDirection.LEFT)
        private val NEGATIVE_INF: LimitResult<Any> = LimitResult(LimitValue.infinity(), LimitDirection.RIGHT)
        private val INFINITY: LimitResult<Any> = LimitResult(LimitValue.infinity(), LimitDirection.BOTH)
        @JvmStatic
        fun <T> constantOf(v: T): LimitResult<T> {
            return LimitResult(FiniteValue(v), LimitDirection.CONST)
        }

        @JvmStatic
        fun <T> finiteValueOf(v: T): LimitResult<T> {
            return LimitResult(FiniteValue(v), LimitDirection.BOTH)
        }

        @JvmStatic
        fun <T> finiteValueOf(v: T, s: Int): LimitResult<T> {
            return LimitResult(FiniteValue(v), LimitDirection.fromSignum(s))
        }

        @JvmStatic
        fun <T> finiteValueOf(v: T, d: LimitDirection): LimitResult<T> {
            return LimitResult(FiniteValue(v), d)
        }

        @JvmStatic
        @Suppress("UNCHECKED_CAST")
        fun <T> positiveInf(): LimitResult<T> {
            return POSITIVE_INF as LimitResult<T>
        }

        @JvmStatic
        @Suppress("UNCHECKED_CAST")
        fun <T> negativeInf(): LimitResult<T> {
            return NEGATIVE_INF as LimitResult<T>
        }

        @JvmStatic
        @Suppress("UNCHECKED_CAST")
        fun <T> infinity(): LimitResult<T> {
            return INFINITY as LimitResult<T>
        }

        @JvmStatic
        fun <T> positiveZero(mc: RealCalculator<T>): LimitResult<T> {
            return finiteValueOf(mc.zero, LimitDirection.RIGHT)
        }

        @JvmStatic
        fun <T> negativeZero(mc: RealCalculator<T>): LimitResult<T> {
            return finiteValueOf(mc.zero, LimitDirection.LEFT)
        }

        /**
         * Note: [s] is opposite from LimitDirection's signum.
         */
        @JvmStatic
        @Suppress("UNCHECKED_CAST")
        fun <T> infinityFromSignum(s: Int): LimitResult<T> {
            return when {
                s > 0 -> POSITIVE_INF
                s == 0 -> INFINITY
                else -> NEGATIVE_INF
            } as LimitResult<T>
        }

        @JvmStatic
        @Suppress("UNCHECKED_CAST")
        fun <T> infinityOf(d: LimitDirection): LimitResult<T> {
            return when (d) {
                LimitDirection.LEFT -> POSITIVE_INF
                LimitDirection.RIGHT -> NEGATIVE_INF
                LimitDirection.BOTH -> INFINITY
                LimitDirection.CONST -> constNotSupportInf()
            } as LimitResult<T>
        }
    }
}

fun <T> LimitResult<T>.signum(mc: RealCalculator<T>): Int {
    if (this.isFinite) {
        val v = value.value
        if (mc.isZero(v)) {
            return when (direction) {
                LimitDirection.RIGHT -> 1
                LimitDirection.LEFT -> -1
                else -> 0
            }
        }
        return try {
            mc.compare(v, mc.zero)
        } catch (e: Exception) {
            0
        }
    } else {
        return if (isPositiveInf) {
            1
        } else {
            -1
        }
    }
}

///**
// * Describes equivalent infinitesimal
// */
//data class PolyEqualInf<T>(val pow : Fraction, val coe : T){
//    fun add(y : PolyEqualInf<T>, mc : MathCalculator<T>){
//
//    }
//}

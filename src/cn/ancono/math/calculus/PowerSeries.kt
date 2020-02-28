package cn.ancono.math.calculus

import cn.ancono.math.MathCalculator
import cn.ancono.math.MathObject
import cn.ancono.math.MathObjectExtend
import cn.ancono.math.numberModels.api.FlexibleNumberFormatter
import cn.ancono.math.numberModels.api.RingNumberModel
import cn.ancono.math.numberModels.structure.Polynomial
import java.util.function.Function
import java.util.function.IntFunction


typealias Coefficient<T> = IntFunction<T>

operator fun <T> IntFunction<T>.invoke(i: Int): T = this.apply(i)
/**
 * The
 * Created at 2019/11/25 19:54
 * @author  lyc
 */
class PowerSeries<T : Any>(mc: MathCalculator<T>, val coefficient: Coefficient<T>)
    : MathObjectExtend<T>(mc), RingNumberModel<PowerSeries<T>> {

    override fun <N : Any> mapTo(mapper: Function<T, N>, newCalculator: MathCalculator<N>): MathObject<N> {
        return PowerSeries(newCalculator, IntFunction { mapper.apply(coefficient.apply(it)) })
    }

    override fun valueEquals(obj: MathObject<T>): Boolean {
        if (obj !is PowerSeries) {
            return false
        }
        return coefficient == obj.coefficient
    }

    override fun toString(nf: FlexibleNumberFormatter<T, MathCalculator<T>>): String {
        return (0..3).asSequence().map { i ->
            when (i) {
                0 -> {
                    nf.format(coefficient(i), mc)
                }
                1 -> {
                    "(${nf.format(coefficient(i), mc)})x"
                }
                else -> {
                    "(${nf.format(coefficient(i), mc)})x^$i"
                }
            }
        }.joinToString(" + ")
    }

    override fun add(y: PowerSeries<T>): PowerSeries<T> {
        return PowerSeries(mc, IntFunction { i -> mc.add(coefficient(i), y.coefficient(i)) })
    }

    override fun negate(): PowerSeries<T> {
        return PowerSeries(mc, IntFunction { i -> mc.negate(coefficient(i)) })
    }

    override fun multiply(y: PowerSeries<T>): PowerSeries<T> {
        return PowerSeries(mc, IntFunction { r ->
            var sum = mc.zero
            for (i in 0..r) {
                val j = r - i
                sum = mc.add(sum, mc.multiply(coefficient(i), y.coefficient(j)))
            }
            sum
        })
    }

    fun toPolynomial(n: Int): Polynomial<T> {
        return Polynomial.valueOf(mc, (0..n).map { coefficient(it) })
    }

//    /**
//     * Returns the power series of this compose [x], which is equal to the result of substituting
//     * the variable x by the power series. It is required that the constant term of [x] is zero.
//     */
//    fun compose(x : FormalPowerSeries<T>) : FormalPowerSeries<T>{
//
//    }


    override fun isZero(): Boolean {
        TODO("not implemented")
    }

    companion object {

        /**
         * Returns the power series of `1/(1+x) = 1 - x + x^2 - x^3 ... `
         *
         */
        fun <T : Any> series1P(mc: MathCalculator<T>): PowerSeries<T> {
            val negateOne = mc.negate(mc.one)
            return PowerSeries(mc, IntFunction { i ->
                if (i % 2 == 0) {
                    mc.one
                } else {
                    negateOne
                }
            })
        }
    }
}

//fun main(args: Array<String>) {
//    val mc = Multinomial.getCalculator()
//    val A = PowerSeries(mc, IntFunction { i -> if(i == 0){
//        mc.zero
//    }else{
//        Multinomial.monomial(Term.singleChar("a_{$i}").divide(CombUtils.factorialX(i)))
//    }
//    })
//    val sp = PowerSeries.series1P(mc)
//    val n = 6
//    val An = A.toPolynomial(n)
//    val re = sp.toPolynomial(n).substitute(An)
//    val B = An.add(Polynomial.one(mc))
//    val result = re.dropHigherTerms(n).mapIndexed { i, m -> m.multiply(Term.valueOf(CombUtils.factorialX(i))) }
//    println(result.joinToString("$$ $$",prefix = "$$",postfix = "$$").replace('*',' '))
//    println(B.times(re).dropHigherTerms(n))
//}
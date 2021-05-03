package cn.ancono.math.algebra.abs.field

import cn.ancono.math.MathCalculator
import cn.ancono.math.algebra.abs.calculator.FieldCalculator
import cn.ancono.math.numberModels.Calculators
import cn.ancono.math.numberTheory.Primes
import cn.ancono.math.set.FiniteSet
import cn.ancono.math.set.MathSets


/**
 * Describe the finite field `Z/pZ` and provides some useful methods.
 *
 *
 * Created at 2018/11/10 14:18
 * @author  liyicheng
 */
class PField(val p: Int) : AbstractFiniteField<Int>() {
    //Created by lyc at 2020-03-03 17:22
    init {
        require(Primes.getInstance().isPrime(p.toLong())) {
            "p =$p must be prime"
        }
    }

    private val mc: MathCalculator<Int> = Calculators.intModP(p)


    private val elements: FiniteSet<Int> = MathSets.asSet(mc, *Array(p) { it })

    override fun getCalculator(): FieldCalculator<Int> {
        return mc
    }

    override fun getSet(): FiniteSet<Int> {
        return elements
    }

    override fun unit(): Int {
        return 1
    }
}
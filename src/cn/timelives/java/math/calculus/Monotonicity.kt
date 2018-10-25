package cn.timelives.java.math.calculus

import cn.timelives.java.math.MathCalculator
import cn.timelives.java.math.numberModels.Fraction
import cn.timelives.java.math.numberModels.api.unaryMinus


enum class MonoType{

}

/*
 * Created at 2018/10/23 18:11
 * @author  liyicheng
 */
object Monotonicity{

    /**
     * Determines the monotonicity of a power function, `x^pow` in a infinitesimal neighbourhood
     * represented by the limit result. Return `1` for increasing, `0` for stable and
     * `-1` for decreasing.
     */
    fun <T:Any> power( x: LimitResult<T>,pow : Fraction,mc : MathCalculator<T>) : Int{
        if(pow.isZero){
            return 0
        }
        if(pow.isNegative){
            return -power(x, -pow, mc)
        }
        val even = (pow.numerator % 2 == 0L)
        return if(even){
            x.signum(mc)
        }else{
            1
        }
    }


}
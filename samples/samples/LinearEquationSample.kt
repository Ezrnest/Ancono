package samples

import cn.ancono.math.algebra.linearAlgebra.Matrix
import cn.ancono.math.algebra.linearAlgebra.Vector
import cn.ancono.math.calculus.Calculus
import cn.ancono.math.numberModels.BigFraction
import cn.ancono.math.numberModels.Calculators
import cn.ancono.math.times
import java.math.BigInteger


/*
 * Created by liyicheng at 2020-09-26 17:55
 */


object LinearEquationSample {
    fun solveHilbertMatrixEquation(){
        val n = 12
        val mc = BigFraction.calculator
        val H = Matrix.of(n,n,mc){ i,j ->
            BigFraction.valueOf(BigInteger.ONE, BigInteger.valueOf(i+j+1L))
        }
        val x = Vector.valueOf(n,mc){
            mc.one
        }
        val b = H * x
        println(b)
    }
}

fun main() {

    LinearEquationSample.solveHilbertMatrixEquation()
}
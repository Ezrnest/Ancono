package samples


import cn.ancono.math.prob.*
import cn.ancono.math.prob.RandomVariables.bernoulli
import cn.ancono.math.prob.RandomVariables.binomial
import cn.ancono.math.prob.RandomVariables.estimateDist
import cn.ancono.math.prob.RandomVariables.iid
import cn.ancono.math.prob.RandomVariables.sumInt
import kotlin.math.pow


/*
 * Created by liyicheng at 2020-06-02 19:57
 */
object ProbSample {

//    fun plotNormal() {
//        val X = RandomVariables.uniform()
//        val Y = RandomVariables.exponent()
//        val f = RandomVariables.estimateDensity(X, -2.0, 4.0, times = 100000)
//        val g = RandomVariables.estimateDensity(Y, -2.0, 4.0, times = 100000)
//
//        val plot = Plotting()
//        plot.setArea(-1.0, 3.0, -1.0, 3.0)
//        plot.plotFunction(f)
//        plot.plotFunction(g, Color.BLUE)
//        plot.show()
//    }

    fun construct() {
        /*
        assume X_i iid ~ bernoulli(p)
        then X_1 + ... + X_n ~ binomial(n,p)

         */
        val n = 5
        val p = 0.7
        val Y = binomial(n, p)
        val X = bernoulli(p)
        val Z = sumInt(iid(X, n))
        val c1 = estimateDist(Y, 0, n + 1)
        val c2 = estimateDist(Z, 0, n + 1)
        println(c1.contentToString())
        println(c2.contentToString())
    }

    fun rvAlgebra() {
        val X = RandomVariables.normal(0.0, 1.0)
        val Y = RandomVariables.constant(1.0)
        println(X.samples().take(5).toList()) // random numbers from normal dist.
        val Z = Y * X - X // random variable algebra
        println(Z.samples().take(5).toList()) // all zeros
    }

    fun randomWalk() {
        /*
        This example estimates the hitting times of simple symmetric random walk
         */
        val ssrw = StochasticProcesses.simpleRandomWalk()
        val T0 = ssrw.hittingTimeOf(-4)
        val T1 = ssrw.hittingTimeOf(6)
        val T = T0 min T1 // T = min(T0, T1), also stopping time
        val X_T = ssrw.rvAt(T)
        println("E(X_T) = " + X_T.estimateExpectation()) // ~ 0 by optional stopping theorem
        val p1 = (T0 lessThan T1).estimateExpectation() // T0 lessThan T1: creates a indicator r.v.
        val p2 = (T1 lessThan T0).estimateExpectation()
        println(p1) // ~ 0.6 = b / (b-a)
        println(p2) // ~ 0.4 = a / (b-a)
        println(T.estimateExpectation()) // ~ 4 * 6 = |ab| =  24
    }

    fun randomWalk2() {
        val p = 18.0 / 38
        val q = 1 - p
        val start = 20

        val rw = StochasticProcesses.simpleRandomWalk(p, start)
        val T0 = rw.hittingTimeOf(0)
        val T40 = rw.hittingTimeOf(40)
        val T = T0 min T40

        val pWin = (T40 lessThan T0).estimateExpectation()
        println("Estimation:")
        println("P_20 {T40 < T0} = $pWin")
        println("ET = " + T.estimateExpectation())
        println("Theoretic: ")
        fun phi(n: Int): Double {
            val r = q / p
            return (r.pow(n) - 1) / (r - 1)
        }

        val pExact = (phi(20) - phi(0)) / (phi(40) - phi(0))
        println("P_20 {T40 < T0} = $pExact")
        println("ET = " + (20 - 40 * pExact) / (q - p))

    }


}

fun main() {
    ProbSample.randomWalk2()
}
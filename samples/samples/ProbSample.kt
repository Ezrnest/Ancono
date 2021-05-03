package samples

//import cn.ancono.math.geometry.visual.visual2D.Plotting
import cn.ancono.math.prob.RandomVariables
import cn.ancono.math.prob.RandomVariables.bernoulli
import cn.ancono.math.prob.RandomVariables.binomial
import cn.ancono.math.prob.RandomVariables.estimateDist
import cn.ancono.math.prob.RandomVariables.iid
import cn.ancono.math.prob.RandomVariables.sumInt
import cn.ancono.math.prob.getAsSequence
import cn.ancono.math.prob.minus
import cn.ancono.math.prob.times


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
//        val c = DoubleArray(c1.size){ i ->
//            c1[i] - c2[i]
//        }
        println(c1.contentToString())
        println(c2.contentToString())
    }

    fun rvAlgebra() {
        val X = RandomVariables.normal(0.0, 1.0)
        val Y = RandomVariables.constant(1.0)
        println(X.getAsSequence().take(5).toList()) // random numbers from normal dist.
        val Z = Y * X - X // random variable algebra
        println(Z.getAsSequence().take(5).toList()) // all zeros
    }


}

fun main() {
    ProbSample.rvAlgebra()
}
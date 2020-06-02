package samples

import cn.ancono.math.geometry.visual.visual2D.Plotting
import cn.ancono.math.prob.RandomVariables


/*
 * Created by liyicheng at 2020-06-02 19:57
 */
object ProbSample {

    fun plotNormal() {
        val X = RandomVariables.uniform()
        val f = RandomVariables.estimateDensity(X, -2.0, 4.0, times = 10000)

        val plot = Plotting()
        plot.setArea(-1.0, 3.0, -1.0, 3.0)
        plot.plotFunction(f)
        plot.show()
    }
}

fun main() {
    ProbSample.plotNormal()
}
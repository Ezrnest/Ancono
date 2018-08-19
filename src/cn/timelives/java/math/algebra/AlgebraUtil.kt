package cn.timelives.java.math.algebra

import cn.timelives.java.math.MathCalculator
import cn.timelives.java.math.numberModels.structure.PolynomialX


/**
 * Returns a polynomial that is equal to the result of the
 * product of (x-root[0])(x-root[1])...(x-root[n])
 */
fun <T : Any> expandOfRoots(roots: List<T>, mc: MathCalculator<T>): PolynomialX<T> {
    return when (roots.size) {
        0 -> PolynomialX.zero(mc)
        1 -> PolynomialX.valueOf(mc, roots.first(), mc.one)
        2 -> {
            val x1 = roots[0]
            val x2 = roots[1]
            val c = mc.multiply(x1, x2)
            val b = mc.negate(mc.add(x1, x2))
            PolynomialX.valueOf(mc, c, b, mc.one)
        }
        else -> {
            val mid = roots.size / 2
            val left = expandOfRoots(roots.subList(0, mid), mc)
            val right = expandOfRoots(roots.subList(mid, roots.size), mc)
            return left.multiply(right)
        }
    }
}
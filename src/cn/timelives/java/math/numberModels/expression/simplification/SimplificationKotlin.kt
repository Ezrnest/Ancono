package cn.timelives.java.math.numberModels.expression.simplification

import cn.timelives.java.math.MathUtils
import cn.timelives.java.math.div
import cn.timelives.java.math.numberModels.Multinomial
import cn.timelives.java.math.numberModels.Term
import cn.timelives.java.math.numberModels.expression.*
import cn.timelives.java.math.numberModels.expression.SimplificationStrategies.*
import kotlin.reflect.KClass
import kotlin.reflect.full.functions

/**
 * Provides simplifications of trigonometric functions.
 */


internal object SimplificationKotlin {
    fun addDefault(list: MutableList<SimpleStrategy>) {
        SimplificationTri.addAll(list)
        SimplificationExp.addAll(list)
//        println(list)
    }
}

private fun addAllMethods(list: MutableList<SimpleStrategy>, clazz: KClass<*>, prefix: String = "sim") {
    clazz.functions
            .filter { it ->
                it.name.startsWith(prefix)
            }
            .forEach { list.add(it.call(clazz.objectInstance) as SimpleStrategy) }
}

@Suppress("unused") // through reflection
internal object SimplificationTri {
    fun addAll(list: MutableList<SimpleStrategy>) {
        addAllMethods(list, SimplificationTri::class)
//        println()
    }

    internal fun simTri1(): SimpleStrategy {
        //sin(x)^2+cos(x)^2 = 1
        val matcher = exp(sin(x), 2.m) + exp(cos(x), 2.m)
        val builder: ReplacementBuilder = { _, _ -> "1".p }
        val replacer = wrapAMReplacer(matcher, builder)
        return replacer.asStrategy(TAG_TRIGONOMETRIC_SET, description = "sin(x)^2+cos(x)^2 = 1")
    }

    /**
     * sin(x)cos(x) = sin(2x)/2
     */
    internal fun simTri2(): SimpleStrategy {
        val matcher = sin(x) * cos(x)
        val builder: ReplacementBuilder = { map, _ ->
            sin(2.p * map["x"]!!.invoke()) / 2.p
        }

        val replacer = wrapAMReplacer(matcher, builder)
        return replacer.asStrategy(TAG_TRIGONOMETRIC_SET, description = "sin(x)cos(x) = sin(2x)/2")
    }

    /**
     * tan2(x) + 1 = 1/cos(x)^2
     */
    internal fun simTri3(): SimpleStrategy {
        val matcher = square(tan(x)) + 1.m
        val builder: ReplacementBuilder = { map, _ -> 1.p / square(cos(map["x"]!!.invoke())) }
        val replacer = wrapAMReplacer(matcher, builder)
        return replacer.asStrategy(TAG_TRIGONOMETRIC_SET, description = "tan2(x) + 1 = 1/cos(x)^2")
    }

    /**
     * k(tan2(x) + 1) = k/cos(x)^2
     */
    internal fun simTri4(): SimpleStrategy {

        val matcher = square(tan(x)) * "k".ref + "k".ref
        val builder: ReplacementBuilder = { map, _ -> map["k"]!!.invoke() / square(cos(map["x"]!!.invoke())) }
        val replacer = wrapAMReplacer(matcher, builder)
        return replacer.asStrategy(TAG_TRIGONOMETRIC_SET, description = "k*tan2(x) + k = k/cos(x)^2")
    }

    /**
     * cot2(x) + 1 = 1/sin(x)^2
     */
    internal fun simTri5(): SimpleStrategy {
        val matcher = square(cot(x)) + 1.m
        val builder: ReplacementBuilder = { map, _ -> 1.p / square(sin(map["x"]!!.invoke())) }
        val replacer = wrapAMReplacer(matcher, builder)
        return replacer.asStrategy(TAG_TRIGONOMETRIC_SET, description = "cot2(x) + 1 = 1/sin(x)^2")
    }

    /**
     * k(cot2(x) + 1) = k/sin(x)^2
     */
    internal fun simTri6(): SimpleStrategy {
        val matcher = square(cot(x)) * "k".ref + "k".ref
        val builder: ReplacementBuilder = { map, _ -> map["k"]!!.invoke() / square(sin(map["x"]!!.invoke())) }
        val replacer = wrapAMReplacer(matcher, builder)
        return replacer.asStrategy(TAG_TRIGONOMETRIC_SET)
    }

    /**
     * (Previously: cos(x)^2 -> 1 - sin(x)^2)
     * 1 - 2*sin(x)^2 =  cos(2x)
     */
    internal fun simTri7(): SimpleStrategy {
        val matcher = (-2).m * square(sin(x)) + 1.m
        val builder: ReplacementBuilder = { map, _ -> cos(2.p * map["x"]!!.invoke()) }
        val replacer = wrapAMReplacer(matcher, builder)
        return replacer.asStrategy(TAG_TRIGONOMETRIC_SET)
    }

    /**
     * (Previously: cos(x)^2 -> 1 - sin(x)^2)
     * k - 2k*sin(x)^2 =  kcos(2x)
     */
    internal fun simTri8(): SimpleStrategy {
        val matcher = "k".mMul(Multinomial.TWO.negate()) * square(sin(x)) + "k".ref
        val builder: ReplacementBuilder = { map, _ -> map["k"]!!.invoke() * cos(2.p * map["x"]!!.invoke()) }
        val replacer = wrapAMReplacer(matcher, builder)
        return replacer.asStrategy(TAG_TRIGONOMETRIC_SET)
    }

    /**
     * tan(x) * cos(x) = sin(x)
     */
    internal fun simTri9(): SimpleStrategy {
        val mat = tan(x) * cos(x)
        val bud: ReplacementBuilder = { map, _ -> sin(map["x"]!!.invoke()) }
        val rep = wrapAMReplacer(mat, bud)
        return rep.asStrategy(TAG_TRIGONOMETRIC_SET, description = "tan(x) * cos(x) = sin(x)")
    }

    /**
     * cot(x) * sin(x) = cos(x)
     */
    internal fun simTri10(): SimpleStrategy {
        val mat = cot(x) * sin(x)
        val bud: ReplacementBuilder = { map, _ -> cos(map["x"]!!.invoke()) }
        val rep = wrapAMReplacer(mat, bud)
        return rep.asStrategy(TAG_TRIGONOMETRIC_SET, description = "cot(x) * sin(x) = cos(x)")
    }

//    internal fun simSigma() : SimpleStrategy{
//
//    }


}


@Suppress("unused") // through reflection
internal object SimplificationExp {
    fun addAll(list: MutableList<SimpleStrategy>) {
        addAllMethods(list, SimplificationExp::class)
//        println()
    }

    /**
     * > exp(a*(node),2) -> a^2 * exp(node,2)
     */
    internal fun simExp0(): SimpleStrategy {
        val mat = exp(poly.named("m"), rational.named("exp"))
        val bud: ReplacementBuilder = { map, ec ->
            val poly = Node.getPolynomialPart(map["m"]!!.invoke(), ec)
            val exp = Node.getPolynomialPart(map["exp"]!!.invoke(), ec)
            val pow = exp.first.toFraction()
            val gcd = Term.gcd(*poly.terms.toTypedArray())
            if (gcd == Term.ONE) {
                null
            } else {
                val nume = gcd.numerator().longValueExact()
                val deno = gcd.denominator().longValueExact()
                val rad = gcd.radical().longValueExact()
                val (n, reN) = MathUtils.integerExpFloor(nume, pow)
                val (d, reD) = MathUtils.integerExpCeil(deno, pow)
                val (r, reR) = MathUtils.integerExpFloor(rad, pow / 2)
                val chN = gcd.character.mapValues { en -> pow * en.value }
                val outerTerm = Term.newInstance(gcd.signum(), reN.toBigInteger(), reD.toBigInteger(), reR.toBigInteger(), chN)
                val toDivide = gcd.sameChar(gcd.signum(), n.toBigInteger(), d.toBigInteger(), r.toBigInteger())
                val nPoly = poly / toDivide
                Multinomial.monomial(outerTerm).p * exp(nPoly.p, map["exp"]!!.invoke())
            }
        }
        return MatcherReplacer(mat, bud).asStrategy(TAG_PRIMARY_SET, description = "exp(a*(node),2) -> a^2 * exp(node,2)")
    }

    /**
     * > k1 * exp(x,p1) / (k2 * exp(x,p2)) -> k1 * exp(x,p1-p2) / k2
     */
    internal fun simExp1(): SimpleStrategy {
        val mat = "k1".mulR(exp(x, "exp1".ref)) / "k2".mulR(exp(x, "exp2".ref))
        val bud: ReplacementBuilder = { map, _ ->

            (map["k1"]!!.invoke() * exp(map["x"]!!.invoke(), map["exp1"]!!.invoke() - map["exp2"]!!.invoke())) / map["k2"]!!.invoke()
        }
        return MatcherReplacer(mat, bud).asStrategy(TAG_PRIMARY_SET, description = "k1 * exp(x,p1) / (k2 * exp(x,p2)) -> k1 * exp(x,p1-p2) / k2")
    }
}

//fun main(args: Array<String>) {
//    setEnableSpi(true)
//    val ec = ExprCalculator.newInstance
////    val matcher = "k".mulR(exp(x,"exp1".ref))
////    println(matcher.matches(ec.simplify(Expression.valueOf("exp(x,a)")).root, emptyMap(),ec))
//
//    val expr = Expression.valueOf("a*exp(a^2+b^2,1/2)/exp(a^2+b^2,3/2) ")
//    println(ec.simplify(expr))
//}

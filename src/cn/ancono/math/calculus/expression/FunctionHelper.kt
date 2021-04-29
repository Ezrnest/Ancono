package cn.ancono.math.calculus.expression

import cn.ancono.math.calculus.*
import cn.ancono.math.exceptions.ExceptionUtil
import cn.ancono.math.exceptions.UnsupportedCalculationException
import cn.ancono.math.numberModels.Multinomial
import cn.ancono.math.numberModels.api.Computable
import cn.ancono.math.numberModels.expression.*
import cn.ancono.math.numberModels.expression.anno.DisallowModify
import cn.ancono.math.numberModels.expression.simplification.buildNode

typealias LimitProcessE = LimitProcess<Expression>
typealias LimitResultE = LimitResult<Expression>

/*
 * Created at 2018/10/20 12:04
 * @author  liyicheng
 */
object FunctionHelper {
    var hopitalThrehold = 3
        set(value) {
            field = if (value < 0) {
                0
            } else {
                value
            }
        }

    fun limitNode(root: Node, process: LimitProcess<Expression>, mc: ExprCalculator): LimitResultE? {
        val handler = LimitHandler(process, mc)
        return handler.limitNode(root)
    }


    internal val DISPATCHER = FunctionLimitDispatcher()

    init {
        addPrimaryFunctions()
    }

    private fun addPrimaryFunctions() {
        DISPATCHER.addProcessor(ReciprocalProcessor)
        DISPATCHER.addProcessor(ExpProcessor1)
        DISPATCHER.addProcessor(ExpProcessor2)
        DISPATCHER.addProcessor(LnProcessor)
        DISPATCHER.addProcessor(LogProcessor)
        DISPATCHER.addProcessor(SinProcessor)
        DISPATCHER.addProcessor(CosProcessor)
        DISPATCHER.addProcessor(TanProcessor)
    }


    private fun unsupportedFunctionLimit(functionName: String, parameterLength: Int): Nothing {
        throw UnsupportedCalculationException("Cannot compute limit of " + functionName +
                " with " + parameterLength + " parameter(s)")
    }


    internal class FunctionLimitDispatcher : ContinuousFunctionProcessor {
        private val sFunction = HashMap<String, ContinuousFunctionProcessor>()
        private val dFunction = HashMap<String, ContinuousFunctionProcessor>()
        private val mFunction = HashMap<String, ContinuousFunctionProcessor>()
        private val wildcards = ArrayList<ContinuousFunctionProcessor>()

        override fun accept(functionName: String, parameterLength: Int): Boolean {
            return true
        }

        override fun limit(node: Node, handler: LimitHandler): LimitResultE? {
            val type = node.type
            var (result, found) = when (type) {
                Node.Type.S_FUNCTION -> {
                    dSFunction(node as Node.SFunction, handler)
                }
                Node.Type.D_FUNCTION -> {
                    dDFunction(node as Node.DFunction, handler)
                }
                Node.Type.M_FUNCTION -> {
                    dMFunction(node as Node.MFunction, handler)
                }
                else -> {
                    (throw IllegalArgumentException())
                }
            }
            if (result != null) {
                return result
            }
            val fnode = node as Node.FunctionNode
            val fName = fnode.functionName
            val pLen = fnode.parameterLength

            for (fd in wildcards) {
                if (fd.accept(fName, pLen)) {
                    found = true
                    result = fd.limit(node, handler)
                    if (result != null) {
                        return result
                    }
                }
            }
//            unsupportedFunctionLimit(fName, pLen)
            if (found) {
                return null
            } else {
                unsupportedFunctionLimit(fName, pLen)
            }
        }


        private fun dSFunction(node: Node.SFunction, handler: LimitHandler): Pair<LimitResultE?, Boolean> {
            val name = node.functionName
            val derivator = sFunction[name] ?: return null to false
//            if(derivator == null){
//                println("?")
//                return null
//            }
            return derivator.limit(node, handler) to true
        }

        private fun dDFunction(node: Node.DFunction, handler: LimitHandler): Pair<LimitResultE?, Boolean> {
            val name = node.functionName
            val derivator = dFunction[name] ?: return null to false
            return derivator.limit(node, handler) to true
        }

        private fun dMFunction(node: Node.MFunction, handler: LimitHandler): Pair<LimitResultE?, Boolean> {
            val name = node.functionName
            val derivator = mFunction[name] ?: return null to false
            return derivator.limit(node, handler) to true
        }

        fun addProcessor(processor: CFPAdapter) {
            when (processor.parameterLength) {
                1 -> sFunction[processor.functionName] = processor
                2 -> dFunction[processor.functionName] = processor
                else -> mFunction[processor.functionName] = processor
            }
        }
    }
}

/**
 * A handler for computing limit of a function.
 */
class LimitHandler
internal constructor(val process: LimitProcessE, val mc: ExprCalculator, hopitalThrehold: Int = FunctionHelper.hopitalThrehold) {
    private var hopital: Int = hopitalThrehold


    fun limitNode(@DisallowModify node: Node):
            LimitResultE? {
        //require the term is a number or a single variable x.
        when (node.type) {
            Node.Type.POLYNOMIAL -> {
                val poly = node as Node.Poly
                return Limit.limitOf(poly.polynomial, process, mc)
            }
            Node.Type.ADD -> {
                return limitNodeAdd(node as Node.Add)
            }
            Node.Type.MULTIPLY -> {
                return limitNodeMultiply(node as Node.Multiply)
            }
            Node.Type.FRACTION -> {
                return limitNodeFraction(node as Node.NodeFrac)
            }
            Node.Type.S_FUNCTION, Node.Type.D_FUNCTION, Node.Type.M_FUNCTION ->
                return FunctionHelper.DISPATCHER.limit(node, this)
            else -> throw AssertionError()
        }
    }


    private fun limitNodeAdd(@DisallowModify node: Node.Add): LimitResultE? {
        val pLimit = node.polynomial?.let { Limit.limitOf(it, process, mc) }
        val nLimit = node.childrenList.map { limitNode(it) }
        var re = nLimit.reduce { x, y ->
            if (x == null || y == null) {
                null
            } else {
                Limit.add(x, y, mc)
            }
        }
        if (re != null && pLimit != null) {
            re = Limit.add(re, pLimit, mc)
        }
        if (re != null) {
            return re
        }

        // only deal with the situation
        // when there are both positive infinite and negative infinite
        // separates the expression to
        // f(x) + g(x), where f(x) -> positive infinite
        // and f(x)(1+g(x)/f(x)) = f(x) + g(x)
        // computes the limit separately as multiply
//        if(node.polynomial !=null){
//
//        }
        val positiveInfP: Multinomial?
        val remainsP: Multinomial?
        if (pLimit != null && pLimit.isPositiveInf) {
            positiveInfP = node.polynomial
            remainsP = null
        } else {
            positiveInfP = null
            remainsP = node.polynomial
        }
        val positiveInf = arrayListOf<Node>()
        val remains = arrayListOf<Node>()
        for (i in nLimit.indices) {
            val lim = nLimit[i]
            if (lim != null && lim.isPositiveInf) {
                positiveInf += node.getChildren(i)
            } else {
                remains += node.getChildren(i)
            }
        }
        if (positiveInf.isEmpty()) {
            if (positiveInfP == null) {
                //cannot compute limit
                return null
            }
        }

        var f = Node.wrapCloneNodeAM(true, positiveInf, positiveInfP)
        f = mc.simplify(f)
        val frac = Node.wrapNodeFraction(Node.wrapCloneNodeAM(true, remains, remainsP), f.cloneNode())
        val g = Node.wrapNodeAdd(mc.simplify(frac), Multinomial.ONE)
        return limitNodeMultiplyTwo(f, LimitResult.positiveInf(), g, null)
    }

    private fun limitNodeFraction(@DisallowModify node: Node.NodeFrac)
            : LimitResultE? {
        val nume = node.c1
        val deno = node.c2
        return limitNodeFractionTwo(nume, null, deno, null)
    }

    private fun limitNodeMultiply(@DisallowModify node: Node.Multiply)
            : LimitResultE? {
        val pLimit = node.polynomial?.let { Limit.limitOf(it, process, mc) }
        val nLimit = node.childrenList.map { limitNode(it) }
        var re = nLimit.reduce { x, y ->
            if (x == null || y == null) {
                null
            } else {
                Limit.multiply(x, y, mc)
            }
        }
        if (re != null && pLimit != null) {
            re = Limit.multiply(re, pLimit, mc)
        }
        if (re != null) {
            return re
        }
        // only when there are both infinity and zero.
        // separates the expression to
        // f(x) * g(x), where f(x) -> infinity and g(x) -> zero
        // and f(x)/ (1/g(x)) = f(x) * g(x)
        // computes the limit separately as Fraction
        val infinityP: Multinomial?
        val denoP: Multinomial?
        if (pLimit != null && !pLimit.isFinite) {
            infinityP = node.polynomial
            denoP = null
        } else {
            infinityP = null
            denoP = node.polynomial
        }
        val infinity = arrayListOf<Node>()
        val deno = arrayListOf<Node>()
        for (i in nLimit.indices) {
            val lim = nLimit[i]
            if (lim != null && lim.isFinite && mc.isZero(lim.value.value)) {
                deno += node.getChildren(i)
            } else {
                infinity += node.getChildren(i)
            }
        }
        if (infinity.isEmpty()) {
            if (infinityP == null) {
                return null
            }
            val g = Node.wrapCloneNodeAM(false, deno, denoP).let { mc.simplify(it) }
            val poly = Node.wrapCloneNodeFraction(Node.newPolyNode(Multinomial.ONE), Node.newPolyNode(infinityP))
                    .let { mc.simplify(it) }
            return limitNodeFractionTwo(g, null, poly, null)
        }
        if (deno.isEmpty() && denoP == null) {
            return null//TODO
        }
        val f = Node.wrapCloneNodeAM(false, infinity, infinityP).let { mc.simplify(it) }
        val g = Node.wrapCloneNodeAM(false, deno, denoP)
        val denominator = mc.simplify(Node.wrapNodeFraction(Node.newPolyNode(Multinomial.ONE), g))
        return limitNodeFractionTwo(f, null, denominator, null)
    }

    private fun limitNodeFractionTwo(@DisallowModify nume: Node, lim1: LimitResultE?,
                                     @DisallowModify deno: Node, lim2: LimitResultE?)
            : LimitResultE? {
        val limit1 = lim1 ?: (limitNode(nume) ?: return null)
        val limit2 = lim2 ?: (limitNode(deno) ?: return null)
        val re = Limit.divide(limit1, limit2, mc)
        if (re != null) {
            return re
        }
        //must be 0/0 or inf/inf
        return handleFracInf(nume, deno)
    }

    private fun limitNodeMultiplyTwo(@DisallowModify n1: Node, lim1: LimitResultE?,
                                     @DisallowModify n2: Node, lim2: LimitResultE?)
            : LimitResultE? {
        val limit1 = lim1 ?: (limitNode(n1) ?: return null)
        val limit2 = lim2 ?: (limitNode(n2) ?: return null)
        val re = Limit.multiply(limit1, limit2, mc)
        if (re != null) {
            return re
        }
        // inf * 0
        return if (limit1.isFinite) {
            handleInfMulZero(n2, n1)
        } else {
            handleInfMulZero(n1, n2)
        }
    }

    private fun handleInfMulZero(inf: Node, zero: Node)
            : LimitResultE? {
        //convert to fraction
        // 1/zero = inf
        var deno: Node = Node.wrapCloneNodeFraction(Node.newPolyNode(Multinomial.ONE), zero)
        deno = mc.simplify(deno)
        return handleFracInf(inf, deno)
    }

    /**
     * [nume] and [deno] must both have limit of zero or infinity.
     */
    private fun handleFracInf(nume: Node, deno: Node): LimitResultE? {
        if (hopital <= 0) {
            return null
        }
        hopital--
        // use l'hospital rule
        val n = DerivativeHelper.derivativeNode(nume, process.variableName)
        val d = DerivativeHelper.derivativeNode(deno, process.variableName)
//        try{
//            mc.checkValidTree(n)
//            mc.checkValidTree(d)
//        }catch (e : Exception){
//            print("?")
//        }
        val frac = mc.simplify(buildNode { n / d })
        val re = limitNode(frac)
        hopital++
        return re
    }
}

private object ExpProcessor1 : CFPAdapter(ExprFunction.FUNCTION_NAME_EXP, 1) {
    override fun limit(node: Node, handler: LimitHandler): LimitResultE? {
        // e^(node)
        val exponent = (node as Node.SFunction).child
        val lim = handler.limitNode(exponent) ?: return null
        return Limit.composeMonoIncrease(lim, handler.mc::exp, negativeInfLimit = { LimitValue.valueOf(Expression.ZERO) })
    }
}

private object ExpProcessor2 : CFPAdapter(ExprFunction.FUNCTION_NAME_EXP, 2) {
    override fun limit(node: Node, handler: LimitHandler): LimitResultE? {
        val mc = handler.mc
        val base = (node as Node.DFunction).c1
        val exponent = node.c2
        val lim1 = handler.limitNode(base) ?: return null
        val lim2 = handler.limitNode(exponent) ?: return null
        if (lim1.direction == LimitDirection.CONST) {
            return baseConst(lim1.value.value, lim2, mc)
        } else if (lim2.direction == LimitDirection.CONST) {
            return expConst(lim1, lim2.value.value, mc)
        }
        if (lim1.isFinite) {
            val baseVal = lim1.value.value
            if (lim2.isFinite) {
                val expVal = lim2.value.value
                if (mc.isZero(baseVal)) {
                    if (mc.isZero(expVal)) {
                        // 0^0
                        return toLnAndLimit(base, exponent, handler)
                    }
                } else {
                    return LimitResult.finiteValueOf(mc.zero, LimitDirection.RIGHT)
                }
                //TODO determine the direction
//                if(mc.isEqual(baseVal,mc.one)){
//                    return LimitResult.finiteValueOf(mc.one)
//                }
                val re = mc.exp(baseVal, expVal)
                return LimitResult.finiteValueOf(re)
            }
            //infinity case
            if (mc.isEqual(baseVal, mc.one)) {
                // 1 ^ Inf
                return toLnAndLimit(base, exponent, handler)
            }
            val comp = try {
                mc.compare(baseVal, mc.one)
            } catch (e: Exception) {
                1
            } * (-lim2.direction.signum())
            return when {
                comp == 0 -> null
                comp > 0 -> LimitResult.positiveZero(mc)
                else -> LimitResult.positiveInf()
            }
        }

        //lim1 infinity
        if (!lim2.isFinite) {
            return when {
                lim2.isPositiveInf -> LimitResult.positiveInf()
                lim2.isNegativeInf -> LimitResult.positiveZero(mc)
                else -> null
            }
        }
        val expVal = lim2.value.value
        if (mc.isEqual(expVal, mc.one)) {
            // Inf^0
            return toLnAndLimit(base, exponent, handler)
        }
        return expConst(lim1, expVal, mc)
    }

    private fun bothConst(base: Expression, exp: Expression, mc: ExprCalculator): LimitResultE {
        return LimitResult.constantOf(mc.exp(base, exp))
    }

    private fun baseConst(base: Expression, exp: LimitResultE, mc: ExprCalculator): LimitResultE? {
        if (mc.isZero(base)) {
            return LimitResult.constantOf(mc.zero)
        }
        if (mc.isEqual(base, mc.one)) {
            return LimitResult.constantOf(mc.one)
        }
        if (exp.direction == LimitDirection.CONST) {
            return bothConst(base, exp.value.value, mc)
        }
        val comp = try {
            mc.compare(base, mc.one)
        } catch (e: Exception) {
            1
        }
        return if (comp > 0) {
            Limit.composeMonoIncrease(exp, { y -> mc.exp(base, y) }, negativeInfLimit = { LimitValue.valueOf(mc.zero) })
        } else {
            Limit.composeMonoDecrease(exp, { y -> mc.exp(base, y) }, positiveInfLimit = { LimitValue.valueOf(mc.zero) })
        }
    }

    private fun expConst(base: LimitResultE, exp: Expression, mc: ExprCalculator): LimitResultE {
        if (base.direction == LimitDirection.CONST) {
            return bothConst(base.value.value, exp, mc)
        }
        if (mc.isZero(exp)) {
            // x ^ 0 = 1
            return LimitResult.constantOf(mc.one)
        }
        if (Node.isPolynomial(exp.root)) {
            val m = Node.getPolynomialPart(exp.root, mc)
            tryPowQuotient(base, m, mc)?.let { return it }
        }
        val comp = try {
            mc.compare(exp, mc.zero)
        } catch (e: Exception) {
            0
        }
        val f: (Expression) -> Expression = { x -> mc.exp(x, exp) }
        return when {
            comp > 0 -> Limit.composeMonoIncrease(base, f)!!
            comp == 0 -> Limit.composeUnidentified(base, f)!!
            else -> Limit.composeMonoDecrease(base, f)!!
        }
    }

    private fun tryPowQuotient(x: LimitResultE, exp: Multinomial, mc: ExprCalculator): LimitResultE? {
        if (!exp.isMonomial || !exp.first.isRational) {
            return null
        }
        val pow = exp.first.numberPartToFraction()
        return Limit.power(x, pow, mc)
    }

    private fun toLnAndLimit(base: Node, exponent: Node, handler: LimitHandler): LimitResultE? {
        // exp(base,exponent)
        // = exp(exponent * ln(base))
        val mc = handler.mc
        return buildNode {
            val multiply = exponent.cloneNode() * ln(base.cloneNode())
            val exp = exp(mc.simplify(multiply))
            ExpProcessor1.limit(exp, handler)
        }
    }

}

private object LnProcessor : CFPAdapter(ExprFunction.FUNCTION_NAME_LN, 1) {
    override fun limit(node: Node, handler: LimitHandler): LimitResultE? {
        //ln(x)
        val mc = handler.mc
        val sub = (node as Node.SFunction).child
        val limit = handler.limitNode(sub) ?: return null
        if (limit.signum(mc) < 0) {
            ExceptionUtil.negativeLog()
        }
        if (limit.isFinite) {
            val v = limit.value.value
            if (mc.isEqual(v, mc.zero)) {
                return LimitResult.negativeInf()
            }
            return Limit.composeMonoIncrease(limit, { x -> mc.ln(x) })
        } else {
            return LimitResult.positiveInf()
        }
    }

}

private object ReciprocalProcessor : CFPAdapter(ExprFunction.FUNCTION_NAME_RECIPROCAL, 1) {
    override fun limit(node: Node, handler: LimitHandler): LimitResultE? {
        val sub = (node as Node.SFunction).child
        val lim = handler.limitNode(sub) ?: return null
//        if(lim == null){
//            println("?")
//            return null
//        }
        return Limit.reciprocal(lim, handler.mc)
    }

}

private object SinProcessor : CFPAdapter(ExprFunction.FUNCTION_NAME_SIN, 1) {
    override fun limit(node: Node, handler: LimitHandler): LimitResultE? {
        val sub = (node as Node.SFunction).child
        val limit = handler.limitNode(sub) ?: return null
        if (!limit.isFinite) {
            return null
        }
        val v = limit.value.value
        try {
            val limitD = limit.map { it.computeDouble(Computable.DEFAULT_OR_EXCEPTION) }
            val mono = Monotonicity.sin(limitD)
            val f: (Expression) -> Expression = { x -> handler.mc.sin(x) }
            if (mono > 0) {
                return Limit.composeMonoIncrease(limit, f)
            } else if (mono < 0) {
                return Limit.composeMonoDecrease(limit, f)
            }
        } catch (e: Exception) {
        }
        return LimitResult.finiteValueOf(handler.mc.sin(v))
    }
}

private object CosProcessor : CFPAdapter(ExprFunction.FUNCTION_NAME_COS, 1) {
    override fun limit(node: Node, handler: LimitHandler): LimitResultE? {
        val sub = (node as Node.SFunction).child
        val limit = handler.limitNode(sub) ?: return null
        if (!limit.isFinite) {
            return null
        }
        val v = limit.value.value
        try {
            val limitD = limit.map { it.computeDouble(Computable.DEFAULT_OR_EXCEPTION) }
            val mono = Monotonicity.cos(limitD)
            val f: (Expression) -> Expression = { x -> handler.mc.cos(x) }
            if (mono > 0) {
                return Limit.composeMonoIncrease(limit, f)
            } else if (mono < 0) {
                return Limit.composeMonoDecrease(limit, f)
            }
        } catch (e: Exception) {
        }
        return LimitResult.finiteValueOf(handler.mc.cos(v))
    }
}

private object TanProcessor : CFPAdapter(ExprFunction.FUNCTION_NAME_TAN, 1) {
    override fun limit(node: Node, handler: LimitHandler): LimitResultE? {
        val sub = (node as Node.SFunction).child
        val lim = handler.limitNode(sub) ?: return null
        if (lim.isFinite) {
            return Limit.composeMonoIncrease(lim, handler.mc::tan)
        }
        return null
    }
}

private object LogProcessor : CFPAdapter(ExprFunction.FUNCTION_NAME_LOG, 2) {
    override fun limit(node: Node, handler: LimitHandler): LimitResultE? {
        val c1 = (node as Node.DFunction).c1
        val c2 = node.c2
        return buildNode {
            val nume = ln(c1)
            val deno = ln(c2)
            handler.limitNode(nume / deno)
        }
    }
}


//fun main(args: Array<String>) {
//    val mc = ExprCalculator.newInstance
//    val expr = mc.parseExpr("(sin(x)-x)/(x*(cos(x)-1))")
//    val process = LimitProcess.toPositiveZero(mc)
//    val limit = LimitHelper.limitNode(expr.root,process, mc)
//    println("The limit of $expr when $process is $limit")
//}
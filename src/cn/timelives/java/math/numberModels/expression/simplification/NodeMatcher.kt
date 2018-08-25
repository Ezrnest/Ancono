package cn.timelives.java.math.numberModels.expression.simplification

import cn.timelives.java.math.numberModels.Multinomial
import cn.timelives.java.math.numberModels.expression.*
import cn.timelives.java.math.numberModels.expression.anno.DisallowModify
import java.util.*

interface NodeMatchResult {
    val refMapping: Map<String, Node>
    val origin: Node
    val matcher: NodeMatcher
}

data class NodeMatchResultImpl(override val refMapping: Map<String, Node>,
                               override val origin: Node,
                               override val matcher: NodeMatcher) : NodeMatchResult

/**
 * Note: This matcher is experimental.
 *
 */
interface NodeMatcher {
    fun matches(@DisallowModify n: Node, refMapping: Map<String, Node>, ec: ExprCalculator): NodeMatchResult?

    val refNames: Set<String>
}

val String.m: PolyMatcher
    get() = PolyMatcher(Multinomial.valueOf(this))

val String.ref: SingleRefMatcher
    get() = SingleRefMatcher(this)

/**
 * Gets a matcher that matches the Int.
 */
val Int.m : PolyMatcher
    get() = PolyMatcher(Multinomial.valueOf(this.toLong()))

operator fun NodeMatcher.plus(y: NodeMatcher): SimpleAMMatcher {
    val x = this
    if (x is SimpleAMMatcher) {
        if (x.isAdd) {
            return mergeToAM(x, y, true)
        }
    }
    if (y is SimpleAMMatcher) {
        if (y.isAdd) {
            return mergeToAM(y, x, true)
        }
    }
    return SimpleAMMatcher(true, null, listOf(x, y), NoneMatcher)
}


operator fun NodeMatcher.times(y: NodeMatcher): SimpleAMMatcher {
    val x = this
    if (x is SimpleAMMatcher) {
        if (!x.isAdd) {
            return mergeToAM(x, y, false)
        }
    }
    if (y is SimpleAMMatcher) {
        if (!y.isAdd) {
            return mergeToAM(y, x, false)
        }
    }
    return SimpleAMMatcher(false, null, listOf(x, y), NoneMatcher)
}

fun addPartly(remainingMatcher: NodeMatcher, vararg matchers: NodeMatcher): SimpleAMMatcher {
    return SimpleAMMatcher(true, null, matchers.toList(), remainingMatcher)
}

fun mulPartly(remainingMatcher: NodeMatcher, vararg matchers: NodeMatcher): SimpleAMMatcher {
    return SimpleAMMatcher(false, null, matchers.toList(), remainingMatcher)
}

private fun mergeToAM(simpleAMMatcher: SimpleAMMatcher, y: NodeMatcher, isAdd: Boolean): SimpleAMMatcher {
    return SimpleAMMatcher(isAdd, simpleAMMatcher.polyMatcher, simpleAMMatcher.matchers + y, simpleAMMatcher.remainingMatcher)
}

fun abs(x: NodeMatcher): SFunctionMatcher = SFunctionMatcher(ExprFunction.FUNCTION_NAME_ABS, x)
fun arccos(x: NodeMatcher): SFunctionMatcher = SFunctionMatcher(ExprFunction.FUNCTION_NAME_ARCCOS, x)
fun arcsin(x: NodeMatcher): SFunctionMatcher = SFunctionMatcher(ExprFunction.FUNCTION_NAME_ARCSIN, x)
fun arctan(x: NodeMatcher): SFunctionMatcher = SFunctionMatcher(ExprFunction.FUNCTION_NAME_ARCTAN, x)
fun cos(x: NodeMatcher): SFunctionMatcher = SFunctionMatcher(ExprFunction.FUNCTION_NAME_COS, x)
fun cot(x: NodeMatcher): SFunctionMatcher = SFunctionMatcher(ExprFunction.FUNCTION_NAME_COT, x)
fun negate(x: NodeMatcher): SFunctionMatcher = SFunctionMatcher(ExprFunction.FUNCTION_NAME_NEGATE, x)
fun reciprocal(x: NodeMatcher): SFunctionMatcher = SFunctionMatcher(ExprFunction.FUNCTION_NAME_RECIPROCAL, x)
fun sin(x: NodeMatcher): SFunctionMatcher = SFunctionMatcher(ExprFunction.FUNCTION_NAME_SIN, x)
fun sqr(x: NodeMatcher): SFunctionMatcher = SFunctionMatcher(ExprFunction.FUNCTION_NAME_SQR, x)
fun tan(x: NodeMatcher): SFunctionMatcher = SFunctionMatcher(ExprFunction.FUNCTION_NAME_TAN, x)
fun exp(x: NodeMatcher): SFunctionMatcher = SFunctionMatcher(ExprFunction.FUNCTION_NAME_EXP, x)
fun ln(x: NodeMatcher): SFunctionMatcher = SFunctionMatcher(ExprFunction.FUNCTION_NAME_LN, x)
fun exp(a: NodeMatcher, b: NodeMatcher): DFunctionMatcher = DFunctionMatcher(ExprFunction.FUNCTION_NAME_EXP, a, b)
fun log(a: NodeMatcher, b: NodeMatcher): DFunctionMatcher = DFunctionMatcher(ExprFunction.FUNCTION_NAME_LOG, a, b)


object NoneMatcher : NodeMatcher {
    override fun matches(n: Node, refMapping: Map<String, Node>, ec: ExprCalculator): NodeMatchResult? = null
    override val refNames: Set<String>
        get() = emptySet()

}

class PolyMatcher(val m: Multinomial) : NodeMatcher {
    override fun matches(n: Node, refMapping: Map<String, Node>, ec: ExprCalculator): NodeMatchResult? {
        if (n.type != Node.Type.POLYNOMIAL) {
            return null
        }
        val poly = (n as Node.Poly).polynomial
        if (poly != m) {
            return null
        }
        return NodeMatchResultImpl(refMapping, n, this)
    }

    fun matchesPoly(poly: Multinomial, refMapping: Map<String, Node>, ec: ExprCalculator): Boolean {
        return m == poly
    }

    override val refNames: Set<String>
        get() = emptySet()

}

class SingleRefMatcher(val name: String) : NodeMatcher {
    override fun matches(n: Node, refMapping: Map<String, Node>, ec: ExprCalculator): NodeMatchResult? {
        if (refMapping.containsKey(name)) {
            return if (n.equalNode(refMapping[name], ec.multinomialCalculator)) {
                NodeMatchResultImpl(refMapping, n, this)
            } else {
                null
            }
        }
        val nMapping = mutableMapOf(name to n)
        nMapping.putAll(refMapping)
        return NodeMatchResultImpl(nMapping, n, this)
    }

    override val refNames: Set<String> = setOf(name)

}

class SFunctionMatcher(val fname: String, val xMatcher: NodeMatcher) : NodeMatcher {
    override fun matches(n: Node, refMapping: Map<String, Node>, ec: ExprCalculator): NodeMatchResult? {
        if (!Node.isFunctionNode(n, fname, 1)) {
            return null
        }
        val fnode = n as Node.NodeWithChildren
        val subResult = xMatcher.matches(fnode.getChildren(0), refMapping, ec) ?: return null
        val nMapping = subResult.refMapping
        return NodeMatchResultImpl(nMapping, n, this)
    }

    override val refNames: Set<String>
        get() = xMatcher.refNames
}

class DFunctionMatcher(val fname: String, val aMatcher: NodeMatcher, val bMatcher: NodeMatcher) : NodeMatcher {
    override fun matches(n: Node, refMapping: Map<String, Node>, ec: ExprCalculator): NodeMatchResult? {
        if (!Node.isFunctionNode(n, fname, 2)) {
            return null
        }
        val fnode = n as Node.NodeWithChildren
        val subResult1 = aMatcher.matches(fnode.getChildren(0), refMapping, ec) ?: return null
        val subResult2 = bMatcher.matches(fnode.getChildren(1), subResult1.refMapping, ec) ?: return null
        return NodeMatchResultImpl(subResult2.refMapping, n, this)
    }

    override val refNames: Set<String>
        get() = aMatcher.refNames + bMatcher.refNames
}

class MFunctionMatcher(val fname: String, val matchers: List<NodeMatcher>) : NodeMatcher {
    override fun matches(n: Node, refMapping: Map<String, Node>, ec: ExprCalculator): NodeMatchResult? {
        if (!Node.isFunctionNode(n, fname, matchers.size)) {
            return null
        }
        val fnode = n as Node.NodeWithChildren
        var nMapping = refMapping
        for (en in matchers.withIndex()) {
            val matcher = en.value
            val idx = en.index
            val subResult = matcher.matches(fnode.getChildren(idx), nMapping, ec) ?: return null
            nMapping = subResult.refMapping
        }
        return NodeMatchResultImpl(nMapping, n, this)
    }

    override val refNames: Set<String>
        get() {
            val set = mutableSetOf<String>()
            for (n in matchers) {
                set.addAll(n.refNames)
            }
            return set
        }
}

/**
 * Only matches the nodes one by one.
 * @param matchers one to one
 * @param remainingMatcher is used only if there is remaining nodes to match
 */
class SimpleAMMatcher(val isAdd: Boolean,
                      val polyMatcher: PolyMatcher?,
                      val matchers: List<NodeMatcher>,
                      val remainingMatcher: NodeMatcher) : NodeMatcher {
    override fun matches(n: Node, refMapping: Map<String, Node>, ec: ExprCalculator): NodeMatchResult? {
        if (isAdd) {
            if (n.type != Node.Type.ADD) {
                return null
            }
        } else {
            if (n.type != Node.Type.MULTIPLY) {
                return null
            }
        }
        val comb = n as Node.CombinedNode
        var nMapping = refMapping
        //match polynomial part
        var polyMapped = false
        if (polyMatcher != null) {
            if (matchers.size > comb.numberOfChildren) {
                return null
            }
            val poly = comb.polynomial ?: if (isAdd) {
                Multinomial.ZERO
            } else {
                Multinomial.ONE
            }
            if (!polyMatcher.matchesPoly(poly, nMapping, ec)) {
                return null
            }
            polyMapped = true
        } else {
            if (matchers.size > comb.numberOfChildren + 1) {
                return null
            }
        }
        val list: MutableList<Node> = comb.childrenListCopy
        if (!polyMapped) {
            if(comb.polynomial!==null){
                list.add(Node.newPolyNode(comb.polynomial))
            }
        }

        //match multiplication nodes
        val re = match0(refMapping, matchers, 0, list, ec) ?: return null
        nMapping = re.first
        val remaining = re.second

        if (!remaining.isEmpty()) {
            val reAsMul = Node.wrapNodeAM(isAdd, remaining)
            val result = remainingMatcher.matches(reAsMul, nMapping, ec) ?: return null
            nMapping = result.refMapping
        }

        return NodeMatchResultImpl(nMapping, n, this)
    }

    fun match0(refMapping: Map<String, Node>, matchers: List<NodeMatcher>, matIdx: Int, remainingNodes: List<Node>, ec: ExprCalculator)
            : Pair<Map<String, Node>, List<Node>>? {
        if (matIdx >= matchers.size) {
            return refMapping to remainingNodes
        }
        val matcher = matchers[matIdx]

        for (n in remainingNodes) {
            val result = matcher.matches(n, refMapping, ec) ?: continue
            val nRemaining = remainingNodes.filterNot { x -> x === n }
//            if(matIdx == matchers.size - 1){
//                return result.refMapping to nRemaining
//            }
            val refMap = match0(result.refMapping, matchers, matIdx + 1, nRemaining, ec)
            if (refMap != null) {
                return refMap
            }
            //failed
        }
        return null
    }

    override val refNames: Set<String>
        get() {
            val set = mutableSetOf<String>()
            for (m in matchers) {
                set.addAll(m.refNames)
            }
            set.addAll(remainingMatcher.refNames)
            return set
        }

}

class FractionMatcher(val numeMatcher: NodeMatcher, val denoMatcher: NodeMatcher) : NodeMatcher {
    override fun matches(n: Node, refMapping: Map<String, Node>, ec: ExprCalculator): NodeMatchResult? {
        if (n.type != Node.Type.FRACTION) {
            return null
        }
        val fraction = n as Node.Fraction
        val subResult1 = numeMatcher.matches(fraction.c1, refMapping, ec) ?: return null
        val subResult2 = denoMatcher.matches(fraction.c2, subResult1.refMapping, ec) ?: return null
        return NodeMatchResultImpl(subResult2.refMapping, n, this)
    }

    override val refNames: Set<String>
        get() = numeMatcher.refNames + denoMatcher.refNames

}




fun main(args: Array<String>) {
    val exp = exp("a".ref,"b".ref)
//    val sin = addPartly(SingleRefMatcher("$123"),exp(sin("x".ref),"2".m),exp(cos("x".ref),"2".m))
    val sin = exp(sin("refX".ref),2.m) + exp(cos("refX".ref),2.m)
    val expr = Expression.valueOf("exp(m,n)")
    val expr2 = Expression.valueOf("exp(sin(x),2)+exp(cos(x),2)")
    val ec = ExprCalculator.newInstance
    ec.tagRemove(SimplificationStrategies.TRIGONOMETRIC_FUNCTION)
    println(exp.matches(ec.simplify(expr).root, emptyMap(),ec)?.refMapping?.mapValues { n -> Expression(n.value) })
    println(sin.matches(ec.simplify(expr2).root, emptyMap(),ec)?.refMapping?.mapValues { n -> Expression(n.value) })
}
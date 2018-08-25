package cn.timelives.java.math.numberModels.expression.simplification

import cn.timelives.java.math.numberModels.expression.ExprCalculator
import cn.timelives.java.math.numberModels.expression.Node
import cn.timelives.java.math.numberModels.expression.SimpleStrategy
import cn.timelives.java.math.numberModels.expression.SimplificationStrategies
import java.util.*

/**
 * The node provided in the map must not be modified.
 */
typealias ReplacementBuilder = (Map<String, Node>, ExprCalculator) -> Node

interface NodeReplacer {
    /**
     * Returns the result of the replacement, or `null` if the operation can not be done.
     */
    fun replace(n: Node, ec: ExprCalculator): Node?
}

class MatcherReplacer(val matcher: NodeMatcher, val replacementBuilder: (Map<String, Node>, ExprCalculator) -> Node) : NodeReplacer {
    override fun replace(n: Node, ec: ExprCalculator): Node? {
        val matchResult = matcher.matches(n, emptyMap(), ec) ?: return null
        return replacementBuilder(matchResult.refMapping, ec)
    }
}

/**
 * Wraps the add/multiply matcher, transform the strict matcher to partly matcher of add/multiply. It is
 * required that the mather's remaining matcher is [NoneMatcher].
 */
fun wrapAM(matcher: SimpleAMMatcher, replacementBuilder: (Map<String, Node>, ExprCalculator) -> Node)
        : Pair<SimpleAMMatcher, (Map<String, Node>, ExprCalculator) -> Node> {
    if (matcher.remainingMatcher != NoneMatcher) {
        throw IllegalArgumentException()
    }
    val generatedRefName = "#${Random().nextInt()}"
    val nMatcher = SimpleAMMatcher(matcher.isAdd, matcher.polyMatcher, matcher.matchers, SingleRefMatcher(generatedRefName))
    val nBuilder: (Map<String, Node>, ExprCalculator) -> Node = { refMap, ec ->
        replacementBuilder(refMap, ec) + refMap[generatedRefName]!!.cloneNode(null)
    }
    return nMatcher to nBuilder
}

fun wrapAMReplacer(matcher: SimpleAMMatcher, replacementBuilder: (Map<String, Node>, ExprCalculator) -> Node): MatcherReplacer {
    val (m, b) = wrapAM(matcher, replacementBuilder)
    return MatcherReplacer(m, b)
}

fun MatcherReplacer.asStrategy(tags: Set<String>,
                               types: Set<Node.Type>? = null,
                               fname: String? = null,
                               description: String? = null): MatchReplaceStrategy {
    val matcher = this.matcher
    val typeSet: Set<Node.Type> = types ?: when (matcher) {
        is SimpleAMMatcher ->
            if (matcher.isAdd) {
                EnumSet.of(Node.Type.ADD)
            } else {
                EnumSet.of(Node.Type.MULTIPLY)
            }
        is SFunctionMatcher -> SimplificationStrategies.TYPES_FUNCTION
        is DFunctionMatcher -> SimplificationStrategies.TYPES_FUNCTION
        is MFunctionMatcher -> SimplificationStrategies.TYPES_FUNCTION
        is FractionMatcher -> EnumSet.of(Node.Type.FRACTION)
        is PolyMatcher -> EnumSet.of(Node.Type.POLYNOMIAL)
        else -> SimplificationStrategies.TYPES_UNIVERSE
    }
    return MatchReplaceStrategy(this, tags, typeSet, fname, description)

}

class MatchReplaceStrategy(val nr: NodeReplacer,
                           tags: Set<String>,
                           types: Set<Node.Type>,
                           fname: String?, description: String? = null) : SimpleStrategy(tags, types, fname, description) {

    override fun simplifyNode(node: Node, mc: ExprCalculator): Node? {
        return mc.simplify(nr.replace(node, mc)?:return null,Integer.MAX_VALUE)
    }
}


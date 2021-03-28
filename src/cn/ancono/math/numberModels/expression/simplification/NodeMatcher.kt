package cn.ancono.math.numberModels.expression.simplification

import cn.ancono.math.minus
import cn.ancono.math.numberModels.Multinomial
import cn.ancono.math.numberModels.Term
import cn.ancono.math.numberModels.expression.*
import cn.ancono.math.numberModels.expression.anno.DisallowModify
import java.util.function.Predicate

/**
 * Defines the matching result of node matcher.
 */
interface NodeMatchResult {
    val refMapping: Map<String, Node>
    val origin: Node
    val matcher: NodeMatcher
}

/**
 * An default implement of node match result.
 */
data class NodeMatchResultImpl(override val refMapping: Map<String, Node>,
                               override val origin: Node,
                               override val matcher: NodeMatcher) : NodeMatchResult

/**
 * Note: This matcher is experimental.
 * Performs matching operation on node, which supports reference. A named reference has a name of type String and
 * the referred Node.
 */
interface NodeMatcher {
    fun matches(@DisallowModify n: Node, @DisallowModify refMapping: Map<String, Node>, ec: ExprCalculator): NodeMatchResult?

    /**
     * Returns a sample of required node of this node matcher, returns `null` if the
     * required node cannot be determined.
     */
    fun requiredNode(@DisallowModify refMapping: Map<String, Node>, ec: ExprCalculator): Node?

    /**
     * Determines whether this node matcher requires a specific node.
     */
    fun requireSpecific(@DisallowModify refMapping: Map<String, Node>, ec: ExprCalculator): Boolean

    val refNames: Set<String>
}

val String.m: PolyMatcher
    get() = PolyMatcher(Multinomial.valueOf(this))

val String.ref: SingleRefMatcher
    get() = SingleRefMatcher(this)

/**
 * Returns a matcher that matches `m + this.ref`, where `m` is a multinomial.
 */
fun String.mAdd(m: Multinomial): PolyAMNameGroupMatcher {
    return PolyAMNameGroupMatcher(m, this, true)
}

/**
 * Returns a matcher that matches `m * this.ref`, where `m` is a multinomial.
 */
fun String.mMul(m: Multinomial): PolyAMNameGroupMatcher {
    return PolyAMNameGroupMatcher(m, this, false)
}


val poly: TypedMatcher = TypedMatcher(Node.Type.POLYNOMIAL)
val x = "x".ref
val integer = polyOf(Predicate { m ->
    m.isMonomial && m.first.isInteger
})

val rational = polyOf { m ->
    m.isMonomial && m.first.let { it.isRational && it.hasNoChar() }
}

/**
 * A real number without any character except `e` and `pi`
 */
private val epiSet = setOf(Term.E_STR, Term.PI_STR)
val real = polyOf { m ->
    m.terms.all { t -> t.hasNoChar() || epiSet.containsAll(t.characterName) }
}

val monomial = polyOf { it.isMonomial }

/**
 * Gets a matcher that matches the Int.
 */
val Int.m: PolyMatcher
    get() = PolyMatcher(Multinomial.valueOf(this.toLong()))

val Multinomial.m: PolyMatcher
    get() = PolyMatcher(this)

fun polyOf(matcher: Predicate<Multinomial>) = NoneRefMatcherWrapper { n ->
    if (!Node.isPolynomial(n)) {
        false
    } else {
        matcher.test((n as Node.Poly).polynomial)
    }
}

fun polyOf(matcher: (Multinomial) -> Boolean) = NoneRefMatcherWrapper { n ->
    if (!Node.isPolynomial(n)) {
        false
    } else {
        matcher.invoke((n as Node.Poly).polynomial)
    }
}

fun NodeMatcher.named(name: String): NamedGroupMatcher {
    return NamedGroupMatcher(name, this)
}


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

operator fun NodeMatcher.unaryMinus(): SimpleAMMatcher {
    val negativeOne = Multinomial.NEGATIVE_ONE.m
    return negativeOne * this
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

operator fun NodeMatcher.div(y: NodeMatcher): FractionMatcher {
    return FractionMatcher(this, y)
}

fun addPartly(remainingMatcher: NodeMatcher, vararg matchers: NodeMatcher): SimpleAMMatcher {
    return SimpleAMMatcher(true, null, matchers.toList(), remainingMatcher)
}

fun mulPartly(remainingMatcher: NodeMatcher, vararg matchers: NodeMatcher): SimpleAMMatcher {
    return SimpleAMMatcher(false, null, matchers.toList(), remainingMatcher)
}

/**
 * Returns a matcher of addition that takes the [matcher] as fixed matchers and a reference matcher
 * named as `this`. This method is the same as `addPartly(this.ref,*matcher)`.
 */
fun String.addR(vararg matcher: NodeMatcher): SimpleAMMatcher {
    return addPartly(this.ref, *matcher)
}

/**
 * Returns a matcher of multiplication that takes the [matcher] as fixed matchers and a reference matcher
 * named as `this`. This method is the same as `mulPartly(this.ref,*matcher)`.
 */
fun String.mulR(vararg matcher: NodeMatcher): SimpleAMMatcher {
    return mulPartly(this.ref, *matcher)
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
//fun negate(x: NodeMatcher): SFunctionMatcher = SFunctionMatcher(ExprFunction.FUNCTION_NAME_NEGATE, x)
//fun reciprocal(x: NodeMatcher): SFunctionMatcher = SFunctionMatcher(ExprFunction.FUNCTION_NAME_RECIPROCAL, x)
fun sin(x: NodeMatcher): SFunctionMatcher = SFunctionMatcher(ExprFunction.FUNCTION_NAME_SIN, x)

fun sqr(x: NodeMatcher): SFunctionMatcher = SFunctionMatcher(ExprFunction.FUNCTION_NAME_SQR, x)
fun tan(x: NodeMatcher): SFunctionMatcher = SFunctionMatcher(ExprFunction.FUNCTION_NAME_TAN, x)
fun exp(x: NodeMatcher): SFunctionMatcher = SFunctionMatcher(ExprFunction.FUNCTION_NAME_EXP, x)
fun ln(x: NodeMatcher): SFunctionMatcher = SFunctionMatcher(ExprFunction.FUNCTION_NAME_LN, x)
fun exp(a: NodeMatcher, b: NodeMatcher): DFunctionMatcher = DFunctionMatcher(ExprFunction.FUNCTION_NAME_EXP, a, b)
fun log(a: NodeMatcher, b: NodeMatcher): DFunctionMatcher = DFunctionMatcher(ExprFunction.FUNCTION_NAME_LOG, a, b)

fun square(x: NodeMatcher): DFunctionMatcher = exp(x, 2.m)

object NoneMatcher : NodeMatcher {
    override fun matches(n: Node, refMapping: Map<String, Node>, ec: ExprCalculator): NodeMatchResult? = null
    override val refNames: Set<String>
        get() = emptySet()

    override fun requireSpecific(refMapping: Map<String, Node>, ec: ExprCalculator): Boolean {
        return false
    }

    override fun requiredNode(refMapping: Map<String, Node>, ec: ExprCalculator): Node? {
        return null
    }
}

object AnyMatcher : NodeMatcher {
    override fun matches(n: Node, refMapping: Map<String, Node>, ec: ExprCalculator): NodeMatchResult? =
            NodeMatchResultImpl(refMapping, n, this)

    override val refNames: Set<String>
        get() = emptySet()

    override fun requireSpecific(refMapping: Map<String, Node>, ec: ExprCalculator): Boolean {
        return false
    }

    override fun requiredNode(refMapping: Map<String, Node>, ec: ExprCalculator): Node? {
        return null
    }
}

class NoneRefMatcherWrapper(val predicate: (Node) -> Boolean) : NodeMatcher {
    override fun matches(n: Node, refMapping: Map<String, Node>, ec: ExprCalculator): NodeMatchResult? {
        return if (predicate(n)) {
            NodeMatchResultImpl(refMapping, n, this)
        } else {
            null
        }
    }

    override val refNames: Set<String>
        get() = emptySet()

    override fun requireSpecific(refMapping: Map<String, Node>, ec: ExprCalculator): Boolean {
        return false
    }

    override fun requiredNode(refMapping: Map<String, Node>, ec: ExprCalculator): Node? {
        return null
    }
}

class TypedMatcher(val type: Node.Type) : NodeMatcher {
    override fun matches(n: Node, refMapping: Map<String, Node>, ec: ExprCalculator): NodeMatchResult? {
        if (n.type != type) {
            return null
        }
        return NodeMatchResultImpl(refMapping, n, this)
    }

    override val refNames: Set<String>
        get() = emptySet()


    override fun requireSpecific(refMapping: Map<String, Node>, ec: ExprCalculator): Boolean {
        return false
    }

    override fun requiredNode(refMapping: Map<String, Node>, ec: ExprCalculator): Node? {
        return null
    }
}

/**
 * Matches a specific multinomial
 */
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

    @Suppress("UNUSED_PARAMETER")
    fun matchesPoly(poly: Multinomial, refMapping: Map<String, Node>, ec: ExprCalculator): Boolean {
        return m == poly
    }

    override val refNames: Set<String>
        get() = emptySet()


    override fun requireSpecific(refMapping: Map<String, Node>, ec: ExprCalculator): Boolean {
        return true
    }

    override fun requiredNode(refMapping: Map<String, Node>, ec: ExprCalculator): Node? {
        return m.p
    }
}

/**
 * `NodeToMatch = m +* ref`
 */
class PolyAMNameGroupMatcher(val m: Multinomial, val name: String, val isAdd: Boolean) : NodeMatcher {

    init {
        require(name.isNotEmpty())
    }

    override fun matches(n: Node, refMapping: Map<String, Node>, ec: ExprCalculator): NodeMatchResult? {
        if (n.type != Node.Type.POLYNOMIAL) {
            return null
        }
        val poly = (n as Node.Poly).polynomial
        if (refMapping.containsKey(name)) {
            val node = refMapping[name]!!
            if (node.type != Node.Type.POLYNOMIAL) {
                return null
            }
            val p = (node as Node.Poly).polynomial
            if (!ec.multinomialCalculator.isEqual(
                            poly, if (isAdd) {
                        m.add(p)
                    } else {
                        m.multiply(p)
                    })) {
                return null
            }
            return NodeMatchResultImpl(refMapping, n, this)
        } else {
            val ref = if (isAdd) {
                ec.simplify((poly - m).p)
            } else {
                ec.simplify(poly.p / m.p)
            }
            val nMapping = refMapping + (name to ref)
            return NodeMatchResultImpl(nMapping, n, this)
        }

    }

    override val refNames: Set<String>
        get() = setOf(name)

    override fun requireSpecific(refMapping: Map<String, Node>, ec: ExprCalculator): Boolean {
        return false
    }

    override fun requiredNode(refMapping: Map<String, Node>, ec: ExprCalculator): Node? {
        return null
    }
}

class NamedGroupMatcher(val name: String, val matcher: NodeMatcher) : NodeMatcher {


    init {
        require(!matcher.refNames.contains(name)) {
            "Duplicate name: $name"
        }
        require(name.isNotEmpty())
    }

    override val refNames: Set<String>
        get() = matcher.refNames + name

    override fun matches(n: Node, refMapping: Map<String, Node>, ec: ExprCalculator): NodeMatchResult? {
        if (refMapping.containsKey(name)) {
            return if (n.equalNode(refMapping[name], ec.multinomialCalculator)) {
                NodeMatchResultImpl(refMapping, n, this)
            } else {
                null
            }
        }
        val result = matcher.matches(n, refMapping, ec) ?: return null
        val nMapping = result.refMapping + (name to n)
        return NodeMatchResultImpl(nMapping, n, this)
    }

    override fun requiredNode(refMapping: Map<String, Node>, ec: ExprCalculator): Node? {
        return matcher.requiredNode(refMapping, ec)
    }

    override fun requireSpecific(refMapping: Map<String, Node>, ec: ExprCalculator): Boolean {
        return matcher.requireSpecific(refMapping, ec)
    }
}


class SingleRefMatcher(val name: String) : NodeMatcher {
    init {
        require(name.isNotEmpty())
    }

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
    override fun requiredNode(refMapping: Map<String, Node>, ec: ExprCalculator): Node? {
        return null
    }

    override fun requireSpecific(refMapping: Map<String, Node>, ec: ExprCalculator): Boolean {
        return false
    }
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

    override fun requiredNode(refMapping: Map<String, Node>, ec: ExprCalculator): Node? {
        val x = xMatcher.requiredNode(refMapping, ec) ?: return null
        return sfun(fname, x)
    }

    override fun requireSpecific(refMapping: Map<String, Node>, ec: ExprCalculator): Boolean {
        return xMatcher.requireSpecific(refMapping, ec)
    }
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

    override fun requiredNode(refMapping: Map<String, Node>, ec: ExprCalculator): Node? {
        val a = aMatcher.requiredNode(refMapping, ec) ?: return null
        val b = bMatcher.requiredNode(refMapping, ec) ?: return null
        return dfun(fname, a, b)
    }

    override fun requireSpecific(refMapping: Map<String, Node>, ec: ExprCalculator): Boolean {
        return aMatcher.requireSpecific(refMapping, ec) && bMatcher.requireSpecific(refMapping, ec)
    }
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

    override fun requireSpecific(refMapping: Map<String, Node>, ec: ExprCalculator): Boolean {
        return matchers.all { it.requireSpecific(refMapping, ec) }
    }

    override fun requiredNode(refMapping: Map<String, Node>, ec: ExprCalculator): Node? {
        val list = ArrayList<Node>(matchers.size)
        for (m in matchers) {
            list.add(m.requiredNode(refMapping, ec) ?: return null)
        }
        return func(fname, false, *list.toTypedArray())
    }
}

/**
 * Creates a simple matcher for add or multiply. The [matchers] are fixed, which require
 * one to one matching.
 * @param polyMatcher to match multinomial, optional
 * @param matchers one to one, fixed matchers
 * @param remainingMatcher matches the remaining nodes. If there is no remaining nodes for it,
 * `0` or `1` (according to [isAdd]) will be passed to it to match.
 */
class SimpleAMMatcher(val isAdd: Boolean,
                      val polyMatcher: PolyMatcher?,
                      val matchers: List<NodeMatcher>,
                      val remainingMatcher: NodeMatcher) : NodeMatcher {
    override fun matches(n: Node, refMapping: Map<String, Node>, ec: ExprCalculator): NodeMatchResult? {
        if (isAdd) {
            if (n.type != Node.Type.ADD) {
                return kTimesSituation(n, refMapping, ec)
            }
        } else {
            if (n.type != Node.Type.MULTIPLY) {
                return kTimesSituation(n, refMapping, ec)
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
            if (comb.polynomial !== null) {
                list.add(Node.newPolyNode(comb.polynomial))
            }
        }

        //match multiplication nodes
        val re = match0(refMapping, matchers, 0, list, ec) ?: return null
        nMapping = re.first
        val remaining = re.second

        nMapping = if (!remaining.isEmpty()) {
            val reAsAM = Node.wrapNodeAM(isAdd, remaining)
            val result = remainingMatcher.matches(reAsAM, nMapping, ec) ?: return null
            result.refMapping
        } else {
            val rem = identityOfAM(isAdd)
            remainingMatcher.matches(rem, nMapping, ec)?.refMapping ?: return null
        }

        return NodeMatchResultImpl(nMapping, n, this)
    }

    private fun kTimesSituation(n: Node, refMapping: Map<String, Node>, ec: ExprCalculator): NodeMatchResult? {
        if (matchers.size != 1) {
            return null
        }
        val matcher = matchers[0]
        val re1 = matcher.matches(n, refMapping, ec) ?: return null
        val nMap = re1.refMapping
        val re2 = remainingMatcher.matches(identityOfAM(isAdd), nMap, ec) ?: return null
        return NodeMatchResultImpl(re2.refMapping, n, this)
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


    override fun requireSpecific(refMapping: Map<String, Node>, ec: ExprCalculator): Boolean {
        return false
    }

    override fun requiredNode(refMapping: Map<String, Node>, ec: ExprCalculator): Node? {
        return null
    }
}

class FractionMatcher(val numeMatcher: NodeMatcher, val denoMatcher: NodeMatcher) : NodeMatcher {
    override fun matches(n: Node, refMapping: Map<String, Node>, ec: ExprCalculator): NodeMatchResult? {
        if (n.type != Node.Type.FRACTION) {
            return null
        }
        val fraction = n as Node.NodeFrac
        val subResult1 = numeMatcher.matches(fraction.c1, refMapping, ec) ?: return null
        val subResult2 = denoMatcher.matches(fraction.c2, subResult1.refMapping, ec) ?: return null
        return NodeMatchResultImpl(subResult2.refMapping, n, this)
    }

    override val refNames: Set<String>
        get() = numeMatcher.refNames + denoMatcher.refNames

    override fun requireSpecific(refMapping: Map<String, Node>, ec: ExprCalculator): Boolean {
        return numeMatcher.requireSpecific(refMapping, ec) && denoMatcher.requireSpecific(refMapping, ec)
    }

    override fun requiredNode(refMapping: Map<String, Node>, ec: ExprCalculator): Node? {
        val nume = numeMatcher.requiredNode(refMapping, ec) ?: return null
        val deno = denoMatcher.requiredNode(refMapping, ec) ?: return null
        return nume / deno
    }
}


fun main() {
    val exp = exp("a".ref, "b".ref)
//    val sin = addPartly(SingleRefMatcher("$123"),exp(sin("x".ref),"2".m),exp(cos("x".ref),"2".m))
    val sin = exp(sin("refX".ref), 2.m) + exp(cos("refX".ref), 2.m)
    val expr = Expression.valueOf("exp(m,n)")
    val expr2 = Expression.valueOf("exp(sin(x),2)+exp(cos(x),2)")
    val ec = ExprCalculator.instance
    ec.tagRemove(SimplificationStrategies.TRIGONOMETRIC_FUNCTION)
    println(exp.matches(ec.simplify(expr).root, emptyMap(), ec)?.refMapping?.mapValues { n -> Expression(n.value) })
    println(sin.matches(ec.simplify(expr2).root, emptyMap(), ec)?.refMapping?.mapValues { n -> Expression(n.value) })
}
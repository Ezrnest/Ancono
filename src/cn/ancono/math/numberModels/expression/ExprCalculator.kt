/**
 * 2017-11-24
 */
package cn.ancono.math.numberModels.expression

import cn.ancono.math.MathCalculator
import cn.ancono.math.exceptions.UnsupportedCalculationException
import cn.ancono.math.numberModels.Fraction
import cn.ancono.math.numberModels.Multinomial
import cn.ancono.math.numberModels.MultinomialCalculator
import cn.ancono.math.numberModels.Term
import cn.ancono.math.numberModels.api.FunctionCalculator
import cn.ancono.math.numberModels.api.Simplifier
import cn.ancono.math.numberModels.expression.Node.*
import cn.ancono.math.numberModels.expression.anno.AllowModify
import cn.ancono.math.numberModels.expression.simplification.SimStraHolder
import cn.ancono.math.numberModels.expression.simplification.p
import cn.ancono.utilities.Printer.print
import java.util.*
import java.util.function.ToDoubleFunction

@Suppress("NAME_SHADOWING")
/**
 * Expression Calculator deals with the calculation of the Expression. Unlike
 * most types of [MathCalculator] which have few things to configure,
 * expression calculator provides a wide variety of configurations and plug-ins
 * that enable the calculator to handle customized calculation.
 * <h3>Functions</h3> In addition to basic math operations like add, multiply
 * and so on, the Expression also allows functions. A function is identified
 * with it own name and the number of parameters. The expression calculator
 * allows users to set the functions that the calculator should recognize by
 * assigning an instance of [ExprFunctionHolder] when creating a
 * calculator. Then the expression calculator can handle the functions and
 * compute them. A more detailed instruction of expression function can be found
 * in [ExprFunction].
 *
 * **Simplification** Expressions can be mathematically equal but are of
 * different Expression, and one of the possible forms can be simpler than
 * others and is more efficient. Therefore, proper simplification is essential
 * for expression calculator. Generally, there are two types of simplification.
 *
 *
 * One of them is polynomial simplification, which is already defined in the
 * calculator. The calculator doing polynomial simplification will try to add,
 * subtract, multiply, divide and calculate the functions available as long as
 * the result can be expressed with polynomials. It is normally the basic and
 * default simplification strategy.
 *
 *
 * (To be written...)
 *
 *
 *
 *
 *
 * Each ExprCalculator has a level of simplification, which determines
 * how far the calculator should perform the simplification. Generally, a higher
 * level of simplification means that the calculator will try to simplify the
 * expression by using more high-leveled [SimplificationStrategy], thus
 * making the simplification more thorough. However, it is not necessarily
 * better to set the level of simplification as high as possible, because a
 * higher level of simplification can also consume lots of time when the
 * expression cannot be simplified. Therefore, a suitable level should be set
 * according to the task.
 *
 *
 *
 *
 *
 *
 * @author liyicheng 2017-11-24 18:27
 */
class ExprCalculator
/**
 *
 */
@JvmOverloads constructor(
        /**
         * The polynomial calculator of this calculator
         */
        /**
         * Gets the pc.
         *
         * @return the pc
         */
        val multinomialCalculator: MultinomialCalculator = DEFAULT_CALCULATOR,
        /**
         * Gets the nc.
         *
         * @return the nc
         */
        val nodeComparator: Comparator<Node> = NodeComparator.DEFAULT,
        /**
         * Gets the ps.
         *
         * @return the ps
         */
        val multinomialSimplifier: Simplifier<Multinomial> = Multinomial.getSimplifier(),
        /**
         * Gets the fs.
         *
         * @return the fs
         */
        val functionHolder: ExprFunctionHolder = DEFAULT_FUNCTIONS,
        /**
         * Gets the ss.
         *
         * @return the ss
         */
        val simStraHolder: SimStraHolder = SimStraHolder.getDefault()) : FunctionCalculator<Expression> {
    internal val enabledTags: MutableSet<String> = SimplificationStrategies.getDefaultTags()
    internal val properties: MutableMap<String, String>

    // some constants here
    @get:JvmName("getPOne")
    internal val pOne: Multinomial

    @get:JvmName("getPZero")
    internal val pZero: Multinomial

    @get:JvmName("getPMinusOne")
    internal val pMinusOne: Multinomial

    /*
	 * @see cn.ancono.math.MathCalculator#getZero()
	 */
    override val zero: Expression
        get() = Expression.ZERO

    /*
	 * @see cn.ancono.math.MathCalculator#getOne()
	 */
    override val one: Expression
        get() = Expression.ONE

    private var simplificationIdentifier: Int = 0

    override val isComparable: Boolean
        get() = true


    /*
     * @see cn.ancono.math.MathCalculator#getNumberClass()
     */
    override val numberClass: Class<*>
        get() = Expression::class.java


    init {
        properties = HashMap()
        pOne = multinomialCalculator.one
        pZero = multinomialCalculator.zero
        pMinusOne = multinomialCalculator.negate(pOne)
        updateSimplificationIdentifier()
    }

    private fun updateSimplificationIdentifier() {
        var si = multinomialCalculator.hashCode()
        si = si * 31 + multinomialSimplifier.hashCode()
        si = si * 31 + functionHolder.hashCode()
        si = si * 31 + simStraHolder.hashCode()
        si = si * 31 + properties.hashCode()
        si = si * 31 + enabledTags.hashCode()
        if (si == 0) {
            si = 1
        }
        simplificationIdentifier = si
    }

    /**
     * Gets a property from this calculator.
     * @param key
     * @return
     */
    fun getProperty(key: String): String? {
        return properties[key]
    }

    /**
     * Sets a property for this calculator. This method is used to change the behavior of the
     * calculator.
     * @param key
     * @return
     */
    fun setProperty(key: String, value: String) {
        properties[key] = value
        updateSimplificationIdentifier()
    }

    /**
     * Gets the enabledTags.
     *
     * @return the enabledTags
     */
    fun getEnabledTags(): Set<String> {
        return HashSet(enabledTags)
    }


    /**
     * @param o
     * @return
     * @see Set.contains
     */
    fun tagContains(o: Any): Boolean {
        return enabledTags.contains(o)
    }

    /**
     * Adds a tag to the calculator, enabling some types of simplification.
     * @param e
     * @return
     */
    fun tagAdd(e: String): Boolean {
        val b = enabledTags.add(e)
        updateSimplificationIdentifier()
        return b
    }

    /**
     * @param o
     * @return
     * @see Set.remove
     */
    fun tagRemove(o: Any): Boolean {
        val b = enabledTags.remove(o)
        updateSimplificationIdentifier()
        return b
    }

    /**
     * @param c
     * @return
     */
    fun tagAddAll(c: Collection<String>): Boolean {
        val b = enabledTags.addAll(c)
        updateSimplificationIdentifier()
        return b
    }

    /**
     *
     */
    fun tagClear() {
        enabledTags.clear()
        updateSimplificationIdentifier()
    }

    fun setTags(set: Set<String>) {
        enabledTags.clear()
        enabledTags.addAll(set)
        updateSimplificationIdentifier()
    }

    override fun of(x: Long): Expression {
        return Expression.valueOf(x)
    }

    override fun of(x: Fraction): Expression {
        return Expression.valueOf(x)
    }

    /*
             * @see
             * cn.ancono.math.MathCalculator#isEqual(java.lang.Object,
             * java.lang.Object)
             */
    override fun isEqual(x: Expression, y: Expression): Boolean {
        return x.root.equalNode(y.root, multinomialCalculator)
    }


    private val compareCompute: ToDoubleFunction<String> = ToDoubleFunction { x ->
        when (x) {
            MathCalculator.STR_E -> Math.E
            MathCalculator.STR_PI -> Math.PI
            else -> throw UnsupportedOperationException("Cannot compare with unassigned character: $x")
        }
    }

    /*
	 * @see
	 * cn.ancono.math.MathCalculator#compare(java.lang.Object,
	 * java.lang.Object)
	 */
    override fun compare(x: Expression, y: Expression): Int {
        try {
            return x.computeDouble(compareCompute).compareTo(y.computeDouble(compareCompute))
        } catch (e: java.lang.Exception) {
            throw UnsupportedCalculationException()
        }
    }

    /*
	 * @see cn.ancono.math.MathCalculator#add(java.lang.Object,
	 * java.lang.Object)
	 */
    override fun add(x: Expression, y: Expression): Expression {
        // special case for both polynomial:
        if (isPolynomial(x.root) && isPolynomial(y.root)) {
            val p1 = toPolynomial(x.root).p
            val p2 = toPolynomial(y.root).p
            return Expression(newPolyNode(multinomialCalculator.add(p1, p2), null))
        }
        val list = ArrayList<Node>(2)
        val nroot = Add(null, null, list)
        val rt1 = x.root.cloneNode(nroot)
        val rt2 = y.root.cloneNode(nroot)
        list.add(rt1)
        list.add(rt2)
        val root = simplify(nroot)
        return Expression(root)
    }


    /*
	 * @see
	 * cn.ancono.math.MathCalculator#negate(java.lang.Object)
	 */
    override fun negate(x: Expression): Expression {
        if (isPolynomial(x.root)) {
            return Expression(newPolyNode(multinomialCalculator.negate(toPolynomial(x.root).p), null))
        }
        var nroot: Node = wrapCloneNodeMultiply(x.root, pMinusOne)
        nroot = simplify(nroot)
        return Expression(nroot)
    }

    /*
	 * @see cn.ancono.math.MathCalculator#abs(java.lang.Object)
	 */
    override fun abs(para: Expression): Expression {
        var rt: Node = wrapCloneNodeSF("abs", para.root)
        rt = simplify(rt)
        return Expression(rt)
    }

    /*
	 * @see
	 * cn.ancono.math.MathCalculator#subtract(java.lang.Object,
	 * java.lang.Object)
	 */
    override fun subtract(x: Expression, y: Expression): Expression {
        if (isPolynomial(x.root) && isPolynomial(y.root)) {
            val p1 = toPolynomial(x.root).p
            val p2 = toPolynomial(y.root).p
            return Expression(newPolyNode(multinomialCalculator.subtract(p1, p2), null))
        }
        // para1 + (-1)*para2
        val p1 = x.root.cloneNode(null)
        val p2 = wrapCloneNodeMultiply(y.root, pMinusOne)
        var root: Node = wrapNodeAM(true, p1, p2)
        root = simplify(root)
        return Expression(root)
    }

    /*
	 * @see cn.ancono.math.MathCalculator#isZero(java.lang.Object)
	 */
    override fun isZero(x: Expression): Boolean {
        return isEqual(zero, x)
    }

    /*
	 * @see
	 * cn.ancono.math.MathCalculator#multiply(java.lang.Object,
	 * java.lang.Object)
	 */
    override fun multiply(x: Expression, y: Expression): Expression {
        // special case for both polynomial:
        if (isPolynomial(x.root) && isPolynomial(y.root)) {
            val p1 = toPolynomial(x.root).p
            val p2 = toPolynomial(y.root).p
            return Expression(newPolyNode(multinomialCalculator.multiply(p1, p2), null))
        }
        var root: Node = wrapCloneNodeAM(false, x.root, y.root)
        root = simplify(root)
        return Expression(root)
    }

    /*
	 * @see
	 * cn.ancono.math.MathCalculator#divide(java.lang.Object,
	 * java.lang.Object)
	 */
    override fun divide(x: Expression, y: Expression): Expression {
        var root: Node = wrapCloneNodeFraction(x.root, y.root)
        root = simplify(root)
        return Expression(root)
    }

    /*
	 * @see cn.ancono.math.MathCalculator#reciprocal(java.lang.
	 * Object)
	 */
    override fun reciprocal(x: Expression): Expression {
        val root = wrapNodeFraction(newPolyNode(pOne, null), x.root.cloneNode(null))
        val r = simplify(root)
        return Expression(r)
    }

    /*
	 * @see
	 * cn.ancono.math.MathCalculator#multiplyLong(java.lang.
	 * Object, long)
	 */
    override fun multiplyLong(x: Expression, n: Long): Expression {
        // special case for both polynomial:
        if (isPolynomial(x.root)) {
            val p1 = toPolynomial(x.root).p
            return Expression(newPolyNode(multinomialCalculator.multiplyLong(p1, n), null))
        }
        var root: Node = wrapCloneNodeMultiply(x.root, Multinomial.of(n))
        root = simplify(root)
        return Expression(root)
    }

    /*
	 * @see cn.ancono.math.MathCalculator#divideLong(java.lang.
	 * Object, long)
	 */
    override fun divideLong(x: Expression, n: Long): Expression {
        if (isPolynomial(x.root)) {
            val p1 = toPolynomial(x.root).p
            return Expression(newPolyNode(multinomialCalculator.divideLong(p1, n), null))
        }
        var root: Node = wrapCloneNodeMultiply(x.root, Multinomial.monomial(Term.valueOfRecip(n)))
        root = simplify(root)
        return Expression(root)
    }

    /*
	 * @see cn.ancono.math.MathCalculator#squareRoot(java.lang.
	 * Object)
	 */
    override fun squareRoot(x: Expression): Expression {
        return sfunction("sqr", x)
    }

    /*
	 * @see
	 * cn.ancono.math.MathCalculator#nroot(java.lang.Object,
	 * long)
	 */
    override fun nroot(x: Expression, n: Long): Expression {
        var root: Node = wrapNodeDF("exp", x.root.cloneNode(null), newPolyNode(Multinomial.monomial(Term.valueOfRecip(n)), null))
        root = simplify(root)
        return Expression(root)
    }

    /*
	 * @see cn.ancono.math.MathCalculator#pow(java.lang.Object,
	 * long)
	 */
    override fun pow(x: Expression, n: Long): Expression {
        var root: Node = wrapNodeDF("exp", x.root.cloneNode(null), newPolyNode(Multinomial.of(n), null))
        root = simplify(root)
        return Expression(root)
    }

    /*
	 * @see
	 * cn.ancono.math.MathCalculator#constantValue(java.lang.
	 * String)
	 */
    override fun constantValue(name: String): Expression {
        try {
            val p = multinomialCalculator.constantValue(name)
            return Expression.fromMultinomial(p)
        } catch (uce: UnsupportedCalculationException) {
            throw uce
        }

        //		return null;
    }

    private fun sfunction(name: String, p: Expression): Expression {
        var root: Node = wrapCloneNodeSF(name, p.root)
        root = simplify(root)
        return Expression(root)
    }

    private fun dFunction(name: String, p1: Expression, p2: Expression): Expression {
        var root: Node = wrapCloneNodeDF(name, p1.root, p2.root)
        root = simplify(root)
        return Expression(root)
    }

    private fun mFunction(name: String, vararg p: Expression): Expression {
        var root: Node = wrapCloneNodeMF(name, p.toList().map { it.root }, false)
        root = simplify(root)
        return Expression(root)
    }

    /*
	 * @see cn.ancono.math.MathCalculator#exp(java.lang.Object)
	 */
    override fun exp(x: Expression): Expression {
        return sfunction("exp", x)
    }

    /*
	 * @see cn.ancono.math.MathCalculator#ln(java.lang.Object)
	 */
    override fun ln(x: Expression): Expression {
        return sfunction("ln", x)
    }

    /*
	 * @see cn.ancono.math.MathCalculator#sin(java.lang.Object)
	 */
    override fun sin(x: Expression): Expression {
        return sfunction("sin", x)
    }

    /*
	 * @see
	 * cn.ancono.math.MathCalculator#arcsin(java.lang.Object)
	 */
    override fun arcsin(x: Expression): Expression {
        return sfunction("arcsin", x)
    }

    /*
	 * @see
	 * cn.ancono.math.MathCalculator#arccos(java.lang.Object)
	 */
    override fun arccos(x: Expression): Expression {
        return sfunction("arccos", x)
    }

    /*
	 * @see
	 * cn.ancono.math.MathCalculator#arctan(java.lang.Object)
	 */
    override fun arctan(x: Expression): Expression {
        return sfunction("arctan", x)
    }

    /*
	 * @see cn.ancono.math.MathCalculator#cos(java.lang.Object)
	 */
    override fun cos(x: Expression): Expression {
        return sfunction("cos", x)
    }

    /*
	 * @see cn.ancono.math.MathCalculator#exp(java.lang.Object,
	 * java.lang.Object)
	 */
    override fun exp(a: Expression, b: Expression): Expression {
        return dFunction("exp", a, b)
    }

    /*
	 * @see cn.ancono.math.MathCalculator#log(java.lang.Object,
	 * java.lang.Object)
	 */
    override fun log(a: Expression, b: Expression): Expression {
        return dFunction("log", a, b)
    }

    /*
	 * @see cn.ancono.math.MathCalculator#tan(java.lang.Object)
	 */
    override fun tan(x: Expression): Expression {
        return sfunction("tan", x)
    }

    /**
     * Returns an expression representing the result of substituting [ch] in [expr] with all
     * values between [start] and [end] (inclusive).
     */
    fun sigma(expr: Expression, start: Expression, end: Expression, ch: String = "x"): Expression {
        return mFunction(ExprFunction.FUNCTION_NAME_SIGMA, expr, Expression(ch.p), start, end)
    }

    fun simplify(x: Expression): Expression {
        var root = x.root.cloneNode(null)
        root = simplify(root)
        checkValidTree(root)
        return Expression(root)
    }

    /**
     * Simplifies the node with the given depth. Assigning depth = 0 means only
     * simplify the node. The given node will be simplified regardless of its simplification identifier.
     * @param root
     * @param depth
     * @return
     */
    @JvmName("simplify")
    @JvmOverloads
    internal fun simplify(@AllowModify root: Node, depth: Int = Integer.MAX_VALUE): Node {
        checkValidTree(root)
        root.resetSimIdentifier()
        return if (depth == 0) {
            //special case: avoid recursion.
            simplifyNode(root)
        } else {
            root.recurApply({ x ->
                if (simplificationIdentifier == x.simIdentifier) {
                    //simplified
                    return@recurApply x
                }
                simplifyNode(x)
            }, depth)
        }
    }

    /**
     * Simplify the single node, this method will only simplify only node and
     * there will be no recursion.
     * @param node
     * @return
     */
    private fun simplifyNode(@AllowModify node: Node): Node {
        var node = node
        if (debugEnabled) {
            count++
            if (count % 100 == 0L)
                print("Simplify: " + node.type + " : " + node.hashCode())//TODO
        }
//        checkValidTree(node)
        node = simplifyPolynomial(node, 0)
        node = doSort(node, 0)//very important
        val re: Node
        if (showSimSteps) {
            val snapshot = node.cloneNode()
            re = simplifyWithStrategyNoRecur(node)
            try {
                println("$snapshot -> $re")
            } catch (e: Exception) {
                println("?? -> $re")
            }
        } else {
            re = simplifyWithStrategyNoRecur(node)
        }
//        re = simplifyPolynomial(re,0)
//        re = doSort(re,0)
        re.simIdentifier = simplificationIdentifier
//        checkValidTree(re)
        return re
    }

    /**
     * Try to merge polynomials in the expression as well as possible. For example,
     *
     * @param node
     * @param depth
     * @return
     */
    @JvmName("simplifyPolynomial")
    internal fun simplifyPolynomial(@AllowModify node: Node, depth: Int): Node {
        var node = node
        if (node.simIdentifier == simplificationIdentifier) {
            return node
        }
        when (node.type!!) {
            Type.POLYNOMIAL -> {
                return node
            }
            Type.ADD -> {
                node = polySimplifyAdd(node as Add, depth)
            }
            Type.FRACTION -> {
                node = polySimplifyFraction(node as NodeFrac, depth)
            }
            Type.MULTIPLY -> {
                node = polySimplifyMultiply(node as Multiply, depth)
            }

            Type.S_FUNCTION -> {
                node = polySimplifySFunction(node as SFunction, depth)
            }
            Type.D_FUNCTION -> {
                node = polySimplifyDFunction(node as DFunction, depth)
            }
            Type.M_FUNCTION -> {
                node = polySimplifyMFunction(node as MFunction, depth)
            }
        }
        return node
    }

    internal fun setParentAndReturn(@AllowModify original: Node, returned: Node): Node {
        returned.parent = original.parent
        return returned
    }

    internal fun polySimplifyAdd(@AllowModify node: Add, depth: Int): Node {
        var p: Multinomial? = node.p
        if (p == null) {
            p = multinomialCalculator.zero
        }
        val children = node.children
        val lit = node.children.listIterator(children.size)
        while (lit.hasPrevious()) {
            val t = lit.previous()
            val nt = if (depth > 0) simplifyPolynomial(t, depth - 1) else t
            if (nt.type == Type.POLYNOMIAL) {
                // add this one
                val pn = nt as Poly
                p = multinomialCalculator.add(p!!, pn.p)
                lit.remove()
            } else if (nt !== t) {
                lit.set(nt)
            }

        }
        if (children.isEmpty()) {
            return newPolyNode(p, node.parent)
        }
        if (multinomialCalculator.isZero(p!!)) {
            if (children.size == 1) {
                return setParentAndReturn(node, children[0])
            }
            node.p = null
        } else {
            node.p = p
        }
        return node
    }

    internal fun polySimplifyMultiply(@AllowModify node: Multiply, depth: Int): Node {
        var p: Multinomial? = node.p
        if (p == null) {
            p = multinomialCalculator.one
        }
        val children = node.children
        val lit = node.children.listIterator(children.size)
        while (lit.hasPrevious()) {
            val t = lit.previous()
            val nt = if (depth > 0) simplifyPolynomial(t, depth - 1) else t
            if (nt.type == Type.POLYNOMIAL) {
                // add this one
                val pn = nt as Poly
                p = multinomialCalculator.multiply(p!!, pn.p)
                if (multinomialCalculator.isZero(p)) {
                    // *0
                    break
                }
                lit.remove()
            } else if (nt !== t) {
                lit.set(nt)
            }
        }

        if (node.children.isEmpty() || multinomialCalculator.isZero(p!!)) {
            return newPolyNode(p, node.parent)
        }
        if (multinomialCalculator.isEqual(p, multinomialCalculator.one)) {
            if (children.size == 1) {
                return setParentAndReturn(node, children[0])
            }
            node.p = null
        } else {
            node.p = p
        }
        return node
    }

    internal fun polySimplifyFraction(@AllowModify node: NodeFrac, depth: Int): Node {
        var nume = if (depth > 0) simplifyPolynomial(node.c1, depth - 1) else node.c1
        var deno = if (depth > 0) simplifyPolynomial(node.c2, depth - 1) else node.c2
        if (nume.type == Type.POLYNOMIAL) {
            val pnume = nume as Poly
            if (multinomialCalculator.isZero(pnume.p)) {
                return newPolyNode(multinomialCalculator.zero, node.parent)
            }
            if (deno.type == Type.POLYNOMIAL) {
                val pdeno = deno as Poly
//                try {
//                    val quotient = multinomialCalculator.divide(pnume.p, pdeno.p)
//                    return newPolyNode(quotient, node.parent)
//                } catch (ex: UnsupportedCalculationException) {
//                    // cannot compute
//                }

                val pair = Multinomial.simplifyFraction(pnume.p, pdeno.p)
                if (pair.second.isOne) {
                    return newPolyNode(pair.first, node.parent)
                }
                nume = newPolyNode(pair.first, node)
                deno = newPolyNode(pair.second, node)
            }
        } else if (deno.type == Type.POLYNOMIAL) {
            val pdeno = deno as Poly
            try {
                val _p = multinomialCalculator.reciprocal(pdeno.p)
                nume.parent = null
                if (multinomialCalculator.isEqual(pOne, _p)) {
                    nume.parent = node.parent
                    return nume
                }
                val n = wrapNodeMultiply(nume, _p)
                n.parent = node.parent
                return n
            } catch (ex: UnsupportedCalculationException) {
            }

        }
        node.c1 = nume
//        if (deno.parent == null) {
//            deno.javaClass
//        }
        node.c2 = deno
        return node
    }

    internal fun polySimplifySFunction(@AllowModify node: SFunction, depth: Int): Node {
        val c = if (depth > 0) simplifyPolynomial(node.child, depth - 1) else node.child
        if (c.type == Type.POLYNOMIAL) {
            val p = c as Poly
            val result = functionHolder.computeSingle(node.functionName, p.p)
            if (result != null) {
                return newPolyNode(result, node.parent)
            }
        }
        node.child = c
        return node
    }

    internal fun polySimplifyDFunction(@AllowModify node: DFunction, depth: Int): Node {
        val c1 = if (depth > 0) simplifyPolynomial(node.c1, depth - 1) else node.c1
        val c2 = if (depth > 0) simplifyPolynomial(node.c2, depth - 1) else node.c2
        if (c1.type == Type.POLYNOMIAL && c2.type == Type.POLYNOMIAL) {
            val p1 = c1 as Poly
            val p2 = c2 as Poly
            val result = functionHolder.computeDouble(node.functionName, p1.p, p2.p)
            if (result != null) {
                return newPolyNode(result, node.parent)
            }
        }
        node.c1 = c1
        node.c2 = c2
        return node
    }

    internal fun polySimplifyMFunction(@AllowModify node: MFunction, depth: Int): Node {
        var allPoly = true
        val children = node.children
        val lit = children.listIterator()
        while (lit.hasNext()) {
            val t = lit.next()
            val nt = if (depth > 0) simplifyPolynomial(t, depth - 1) else t
            if (nt.type != Type.POLYNOMIAL) {
                allPoly = false
            }
            if (nt !== t) {
                lit.set(nt)
            }
        }
        if (allPoly) {
            val ps = arrayOfNulls<Multinomial>(children.size)
            var i = 0
            for (n in children) {
                ps[i++] = (n as Poly).p
            }
            val result = functionHolder.computeMultiple(node.functionName, ps)
            if (result != null) {
                return newPolyNode(result, node.parent)
            }
        }
        return node
    }

    @JvmName("simplifyWithStrategy")
    internal fun simplifyWithStrategy(@AllowModify node: Node, depth: Int): Node {
        return if (depth == 0) {
            simStraHolder.performSimplification(node, enabledTags, this)
        } else node.recurApply({ x -> simStraHolder.performSimplification(x, enabledTags, this) }, depth)
    }

    internal fun simplifyWithStrategyNoRecur(@AllowModify node: Node): Node {
        return simStraHolder.performSimplification(node, enabledTags, this)
    }

    @JvmName("doSort")
    internal fun doSort(@AllowModify node: Node, depth: Int): Node {
        return node.recurApply({ x ->
            if (x is NodeWithChildren) {
                x.doSort(nodeComparator)
            }
            x
        }, depth)
    }

    internal fun simplifyPolyAndSort(@AllowModify n: Node): Node {
        var n = n
        n = simplifyPolynomial(n, Integer.MAX_VALUE)
        n = doSort(n, Integer.MAX_VALUE)
        return n
    }

    fun checkValidTree(n: Node) {
        n.recurApplyConsumer({ x ->
            if (x !== n) {
                if (x.parent == null) {
                    throw AssertionError("Null parent: $x")
                }
            }
        }, Integer.MAX_VALUE)
    }

    fun checkValidTreeStrict(n: Node) {
        val set = hashSetOf<Node>()
        n.recurApplyConsumer({ x ->
            if (x !== n) {
                if (x.parent == null) {
                    throw AssertionError("Null parent: $x")
                }
                if (!x.parent.contains(x)) {
                    throw AssertionError("Fake child: $x")
                }
            }
            if (set.contains(x) && x.type != Type.POLYNOMIAL) {
                throw AssertionError("Duplicated node: $x")
            }
            set.add(x)
        }, Integer.MAX_VALUE)
    }


    /**
     * Substitutes the multinomial for the given character to the expression. This method only supports
     * integral exponent for the character.
     * @param expr
     * @param ch
     * @param val
     * @return
     */
    fun substitute(expr: Expression, ch: String, `val`: Multinomial): Expression {
        var root = expr.root.cloneNode(null)
        root = root.recurApply({ x ->
            x.resetSimIdentifier()
            var p = getPolynomialPart(x, this)
            if (p != null) {
                p = p.replace(ch, `val`)
                //				if(n.parent == null) {
                //					n.getClass();
                //				}
                return@recurApply setPolynomialPart(x, p)
            }
            x
        }, Integer.MAX_VALUE)
        root = simplify(root)
        return Expression(root)
    }

    /**
     * Substitutes the give expression `sub` for the character to the expression.
     * @param expr
     * @param ch
     * @param sub
     * @return
     */
    @Suppress("DuplicatedCode")
    fun substitute(expr: Expression, ch: String, sub: Expression): Expression {
        var root = expr.root.cloneNode(null)
        root = root.recurApply({ x ->
            x.resetSimIdentifier()
            val p = getPolynomialPart(x, this) ?: return@recurApply x
            val afterSub = replaceMultinomial(p, ch, sub)
                    ?: //not changed
                    return@recurApply x
            if (x.type == Type.POLYNOMIAL) {
                //replace the whole
                afterSub.parent = x.parent
                return@recurApply afterSub
            }
            val comb = x as CombinedNode
            comb.addChild(afterSub)
            comb.polynomial = pOne
            comb
        }, Integer.MAX_VALUE)
        //	    root.listNode(0);
        root = simplify(root)
        return Expression(root)
    }

    /**
     * Substitutes the give expression `sub` for the character to the expression.
     * @return
     */
    @Suppress("DuplicatedCode")
    fun substituteAll(expr: Expression, subMap: Map<String, Expression>): Expression {
        var root = expr.root.cloneNode(null)
        root = root.recurApply({ x ->
            x.resetSimIdentifier()
            val p = getPolynomialPart(x, this) ?: return@recurApply x
            val afterSub = replaceMultinomialAll(p, subMap)
                    ?: //not changed
                    return@recurApply x
            if (x.type == Type.POLYNOMIAL) {
                //replace the whole
                afterSub.parent = x.parent
                return@recurApply afterSub
            }
            val comb = x as CombinedNode
            comb.addChild(afterSub)
            comb.polynomial = pOne
            comb
        }, Integer.MAX_VALUE)
        //	    root.listNode(0);
        root = simplify(root)
        return Expression(root)
    }


    /**
     * Parses an expression from a String and simplifies it.
     */
    fun parse(expr: String): Expression {
        val expression = Expression.valueOf(expr)
        return simplify(expression)
    }


    private fun differential0(expr: Expression, variableName: String): Expression {
        val nroot = DerivativeHelper.derivativeNode(expr.root, variableName)
        return Expression(simplify(nroot))
    }


    /**
     * Returns the differential of an expression as a function of the given variable.
     */
    override fun differential(f: Expression, variable: String, order: Int): Expression {
        var re = f
        repeat(order) {
            re = differential0(re, variable)
        }
        return re
    }

    fun differential(f: Expression, variable: String = "x"): Expression {
        return differential0(f, variable)
    }

    companion object {

        private val DEFAULT_CALCULATOR = Multinomial.getCalculator()

        private val DEFAULT_FUNCTIONS = ExprFunctionHolder
                .getDefaultKit(Multinomial.getCalculator())
        private var count: Long = 0

        internal fun replaceMultinomial(mul: Multinomial, ch: String, sub: Expression): Node? {
            val count = mul.containsCharCount(ch)
            if (count == 0) {
                return null
            }
            val nodes = ArrayList<Node>(count)
            val remains = mul.removeAll { x -> x.containsChar(ch) }
            for (t in mul.terms) {
                if (!t.containsChar(ch)) {
                    continue
                }
                val pow = t.getCharacterPower(ch)
                val re = t.removeChar(ch)
                val nodeExp = wrapNodeDF(ExprFunction.FUNCTION_NAME_EXP,
                        sub.root.cloneNode(null),
                        newPolyNode(Multinomial.monomial(Term.valueOf(pow)), null))
                val nodeMul = wrapNodeMultiply(nodeExp, Multinomial.monomial(re))
                nodes.add(nodeMul)
            }
            return wrapNodeAM(true, nodes, remains)
        }

        internal fun replaceMultinomialAll(mul: Multinomial, replaceMap: Map<String, Expression>): Node? {
            val chs = replaceMap.keys
            val charCount = mul.terms.count { t ->
                t.character.keys.any {
                    it in chs
                }
            }
            if (charCount == 0) {
                return null
            }
            val nodes = ArrayList<Node>(charCount)
            val remains = mul.removeAll { t -> t.character.keys.any { it in chs } }
            for (t in mul.terms) {
                val c1 = t.character.keys.count {
                    it in chs
                }
                if (c1 == 0) {
                    continue
                }
                val mulNodes = ArrayList<Node>(c1)
                for ((ch, sub) in replaceMap) {
                    if (!t.containsChar(ch)) {
                        continue
                    }
                    val pow = t.getCharacterPower(ch)
                    val nodeExp = wrapNodeDF(ExprFunction.FUNCTION_NAME_EXP,
                            sub.root.cloneNode(null),
                            newPolyNode(Multinomial.monomial(Term.valueOf(pow)), null))
                    mulNodes += nodeExp
                }

                val num = t.numberPart()
                val remainChars = t.character.filter { en -> en.key !in chs }
                val re = num.sameNumber(remainChars)


                val nodeMul = wrapNodeAM(false, mulNodes, Multinomial.monomial(re))
                nodes.add(nodeMul)
            }
            return wrapNodeAM(true, nodes, remains)
        }

        /**
         * Gets a default instance of the ExprCalculator.
         * @return
         */
        @JvmStatic
        val instance: ExprCalculator = ExprCalculator()

        init {
            SimplificationStrategies.setCalRegularization(instance)
        }

        @JvmStatic
        fun getNewInstance(): ExprCalculator {
            val ec = ExprCalculator()
            SimplificationStrategies.setCalRegularization(ec)
            return ec
        }

        /**
         * For debugging, prints simplification per 100 steps.
         */
        var debugEnabled = false

        /**
         * Prints the steps of simplification.
         */
        var showSimSteps = false
    }
}

//fun main(args: Array<String>) {
//    val mc = ExprCalculator.newInstance
//    var f = Expression.valueOf("exp(2,-1)")
//    f = mc.simplify(f)
//    println(f)
//}
/**
 *
 */

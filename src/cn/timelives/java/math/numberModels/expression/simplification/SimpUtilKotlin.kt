package cn.timelives.java.math.numberModels.expression.simplification

import cn.timelives.java.math.numberModels.Multinomial
import cn.timelives.java.math.numberModels.expression.ExprFunction
import cn.timelives.java.math.numberModels.expression.Node

val Int.p: Node.Poly
    get() = Node.newPolyNode(Multinomial.valueOf(this.toLong()))

val String.p: Node.Poly
    get() = Node.newPolyNode(Multinomial.valueOf(this))

val Multinomial.p : Node.Poly
    get() = Node.newPolyNode(this)

fun identityOfAM(isAdd : Boolean) = if(isAdd){
    0.p
}else{
    1.p
}

operator fun Node.plus(n: Node): Node.Add = Node.wrapNodeAM(true, this, n) as Node.Add

operator fun Node.minus(n: Node): Node.Add = Node.wrapNodeAM(true, this,
        Node.wrapNodeMultiply(n, Multinomial.NEGATIVE_ONE)) as Node.Add

operator fun Node.times(n: Node): Node.Multiply = Node.wrapNodeAM(false, this, n) as Node.Multiply

operator fun Node.div(n: Node): Node.Fraction = Node.wrapNodeFraction(this, n)

fun addAll(vararg ns : Node) : Node = Node.wrapNodeAM(true,ns.toList())

fun multiplyAll(vararg ns : Node) : Node = Node.wrapNodeAM(false,ns.toList())

fun abs(x: Node): Node.SFunction = Node.wrapNodeSF(ExprFunction.FUNCTION_NAME_ABS, x)
fun arccos(x: Node): Node.SFunction = Node.wrapNodeSF(ExprFunction.FUNCTION_NAME_ARCCOS, x)
fun arcsin(x: Node): Node.SFunction = Node.wrapNodeSF(ExprFunction.FUNCTION_NAME_ARCSIN, x)
fun arctan(x: Node): Node.SFunction = Node.wrapNodeSF(ExprFunction.FUNCTION_NAME_ARCTAN, x)
fun cos(x: Node): Node.SFunction = Node.wrapNodeSF(ExprFunction.FUNCTION_NAME_COS, x)
fun cot(x: Node): Node.SFunction = Node.wrapNodeSF(ExprFunction.FUNCTION_NAME_COT, x)
//fun negate(x: Node): Node.SFunction = Node.wrapNodeSF(ExprFunction.FUNCTION_NAME_NEGATE, x)
//fun reciprocal(x: Node): Node.SFunction = Node.wrapNodeSF(ExprFunction.FUNCTION_NAME_RECIPROCAL, x)
fun sin(x: Node): Node.SFunction = Node.wrapNodeSF(ExprFunction.FUNCTION_NAME_SIN, x)
fun sqr(x: Node): Node.SFunction = Node.wrapNodeSF(ExprFunction.FUNCTION_NAME_SQR, x)
fun tan(x: Node): Node.SFunction = Node.wrapNodeSF(ExprFunction.FUNCTION_NAME_TAN, x)
fun exp(x: Node): Node.SFunction = Node.wrapNodeSF(ExprFunction.FUNCTION_NAME_EXP, x)
fun ln(x: Node): Node.SFunction = Node.wrapNodeSF(ExprFunction.FUNCTION_NAME_LN, x)
fun exp(a: Node, b: Node): Node.DFunction = Node.wrapNodeDF(ExprFunction.FUNCTION_NAME_EXP, a, b)
fun log(a: Node, b: Node): Node.DFunction = Node.wrapNodeDF(ExprFunction.FUNCTION_NAME_LOG, a, b)

fun square(x : Node) : Node.DFunction = exp(x,Node.newPolyNode(Multinomial.TWO))

fun sfun(fname: String, x : Node) : Node.SFunction = Node.wrapNodeSF(fname,x)

fun dfun(fname : String, a : Node, b : Node, sortable : Boolean = false) : Node.DFunction
        = Node.wrapNodeDF(fname,a,b,sortable)

fun func(fname : String,sortable: Boolean = false, vararg x : Node) : Node{
    return when(x.size){
        0 -> throw IllegalArgumentException("Function needs at least one parameter.")
        1 -> sfun(fname,x[0])
        2 -> dfun(fname,x[0],x[1],sortable)
        else -> Node.wrapNodeMF(fname,x.toList(),sortable)
    }
}
//fun main(args: Array<String>) {
//    for (f in ExprFunction.createBasicCalculatorFunctions(Multinomial.getCalculator())) {
//        when (f.paramNumber) {
//            1 -> {
//                println("fun ${f.name}(x : NodeMatcher) : SFunctionMatcher = SFunctionMatcher(ExprFunction.FUNCTION_NAME_${f.name.toUpperCase()},x)")
//            }
//            2 -> {
//                println("fun ${f.name}(a : NodeMatcher, b : NodeMatcher) : DFunctionMatcher = DFunctionMatcher(ExprFunction.FUNCTION_NAME_${f.name.toUpperCase()},a,b)")
//            }
//            else -> {
//
//            }
//        }
//    }
//}
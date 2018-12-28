package cn.timelives.java.math.calculus.expression

import cn.timelives.java.math.numberModels.expression.Node
import cn.timelives.java.math.numberModels.expression.anno.DisallowModify


/*
 * Created at 2018/11/4 13:34
 * @author  liyicheng
 */
interface ContinuousFunctionProcessor {
    /**
     * Determines whether this FunctionDerivator supports the function of
     * the functionName and parameterLength.
     *
     * @param functionName    the name of the function
     * @param parameterLength the length of the parameter
     * @return true if it supports the function
     */
    fun accept(functionName: String, parameterLength: Int): Boolean

    /**
     * Computes the limit of the node, the node is always an
     * instance of FunctionNode.
     */
    fun limit(@DisallowModify node: Node, handler : LimitHandler): LimitResultE?

//    /**
//     * Returns an equivalent infinitesimal of the function at the point `x=0`.
//     * The node is always an instance of FunctionNode.
//     */
//    fun equivalentInf(@DisallowModify node: Node, variableName : String, ) : Polynomial<Expression>


}

abstract class CFPAdapter(val functionName: String, val parameterLength: Int) : ContinuousFunctionProcessor {
    override fun accept(functionName: String, parameterLength: Int): Boolean {
        return functionName == this.functionName && parameterLength == this.parameterLength
    }
//    override fun equivalentInf(node: Node, handler: LimitHandler) {
//
//    }

}
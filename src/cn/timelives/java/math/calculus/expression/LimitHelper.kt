package cn.timelives.java.math.calculus.expression

import cn.timelives.java.math.calculus.Calculus
import cn.timelives.java.math.exceptions.UnsupportedCalculationException
import cn.timelives.java.math.numberModels.Term
import cn.timelives.java.math.numberModels.expression.ExprCalculator
import cn.timelives.java.math.numberModels.expression.ExprFunction
import cn.timelives.java.math.numberModels.expression.Expression
import cn.timelives.java.math.numberModels.expression.Node
import cn.timelives.java.math.numberModels.expression.anno.DisallowModify
import java.lang.IllegalArgumentException
import java.util.*
import java.util.function.BiFunction

typealias LimitProcessE = LimitProcess<Expression>
typealias LimitResultE = LimitResult<Expression>
/*
 * Created at 2018/10/20 12:04
 * @author  liyicheng
 */
object LimitHelper{
    fun limitNode(@DisallowModify node : Node, process : LimitProcess<Expression>, mc : ExprCalculator) :
            LimitResult<Expression>? {
        //require the term is a number or a single variable x.
        when (node.type) {
            Node.Type.POLYNOMIAL -> {
                val poly = node as Node.Poly
                return Calculus.limit(poly.polynomial, process,mc)
            }
            Node.Type.ADD -> {
                return dNodeAdd(node as Node.Add, variableName)
            }
            Node.Type.MULTIPLY -> {
                return dNodeMultiply(node as Node.Multiply, variableName)
            }
            Node.Type.FRACTION -> {
                return dNodeFraction(node as Node.Fraction, variableName)
            }
            Node.Type.S_FUNCTION, Node.Type.D_FUNCTION, Node.Type.M_FUNCTION ->
                return DISPATCHER.limit(node, process,mc)
            else -> throw AssertionError()
        }
    }

    private val DISPATCHER = FunctionDerivatorDispatcher()
    init {

    }
    private fun addPrimaryFunctions(){

    }




    private fun unsupportedFunctionLimit(functionName: String, parameterLength: Int): Nothing {
        throw UnsupportedCalculationException("Cannot compute limit of " + functionName +
                " with " + parameterLength + " parameter(s)")
    }

    private class FunctionDerivatorDispatcher : FunctionLimitProcessor {
        private val sFunction = HashMap<String, (Node.SFunction,LimitProcessE,ExprCalculator) -> LimitResultE?>()
        private val dFunction = HashMap<String, (Node.DFunction,LimitProcessE,ExprCalculator) -> LimitResultE?>()
        private val mFunction = HashMap<String, (Node.MFunction,LimitProcessE,ExprCalculator) -> LimitResultE?>()
        private val wildcards = ArrayList<FunctionLimitProcessor>()

        override fun accept(functionName: String, parameterLength: Int): Boolean {
            return true
        }

        override fun limit(node: Node, process: LimitProcessE ,mc:ExprCalculator): LimitResultE? {
            val type = node.type
            var result: LimitResultE? = when (type) {
                Node.Type.S_FUNCTION -> {
                    dSFunction(node as Node.SFunction, process,mc)
                }
                Node.Type.D_FUNCTION -> {
                    dDFunction(node as Node.DFunction, process,mc)
                }
                Node.Type.M_FUNCTION -> {
                    dMFunction(node as Node.MFunction, process,mc)
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
                    result = fd.limit(node, process, mc)
                    if (result != null) {
                        return result
                    }
                }
            }
            unsupportedFunctionLimit(fName, pLen)
        }

        private fun dSFunction(node: Node.SFunction, process: LimitProcessE ,mc:ExprCalculator): LimitResultE? {
            val name = node.functionName
            val derivator = sFunction[name] ?: return null
            return derivator.invoke(node, process,mc)
        }

        private fun dDFunction(node: Node.DFunction, process: LimitProcessE ,mc:ExprCalculator): LimitResultE? {
            val name = node.functionName
            val derivator = dFunction[name] ?: return null
            return derivator.invoke(node, process,mc)
        }

        private fun dMFunction(node: Node.MFunction, process: LimitProcessE ,mc:ExprCalculator): LimitResultE? {
            val name = node.functionName
            val derivator = mFunction[name] ?: return null
            return derivator.invoke(node, process,mc)
        }


        fun addSFunction(functionName: String,
                         derivator: (Node.SFunction,LimitProcessE,ExprCalculator) -> LimitResultE?) {
            sFunction[functionName] = derivator
        }

        fun addDFunction(functionName: String,
                         derivator: (Node.DFunction,LimitProcessE,ExprCalculator) -> LimitResultE?) {
            dFunction[functionName] = derivator
        }

        fun addMFunction(functionName: String,
                         derivator: (Node.MFunction,LimitProcessE,ExprCalculator) -> LimitResultE?) {
            mFunction[functionName] = derivator
        }

        fun addDerivator(fd: FunctionLimitProcessor) {
            wildcards.add(fd)
        }

    }


}

interface FunctionLimitProcessor {
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
     * Computes the derivation of the node, the node is always an
     * instance of FunctionNode
     */
    fun limit(@DisallowModify node: Node, process : LimitProcessE, mc : ExprCalculator): LimitResultE?
}
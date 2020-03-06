package samples

import cn.ancono.logic.propLogic.*

fun main(args: Array<String>) {
    LogicSample.predicateLogic()
}

/*
 * Created by liyicheng at 2020-03-03 12:34
 */
object LogicSample {

    fun predicateLogic() {
        val formula = (p implies q) and (q implies r) implies (p implies r)
        println(formula)
        println("Is tautology: ${formula.isTautology}")
        println("Main disjunctive norm: ${formula.toMainDisjunctiveNorm()}")
        println("Conjunctive norm: ${formula.toConjunctiveNorm()}")
        println("Is equivalent to T: ${formula valueEquals T}")
    }
}
package cn.timelives.java.logic.propLogic

import java.lang.NullPointerException
/*
 * Created at 2018/9/18
 * @author liyicheng
 */
data class SimplificationStep(val rule: Rule, val simplifiedPartBefore : Formula, val simplifiedPartAfter : Formula){
    override fun toString(): String {
        return "$simplifiedPartBefore  =>  $simplifiedPartAfter   Rule=$rule"
    }
}

fun simplifyByRules(f : Formula,rules : List<Rule>, steps : MutableList<SimplificationStep>? = null) : Formula{
    return f.recurApply { simplifySingle(it,rules,steps) }
}

fun simplifyByRulesWithSteps(f : Formula,rules : List<Rule>) : Pair<Formula,List<SimplificationStep>>{
    val list = ArrayList<SimplificationStep>()
    return f.recurApply { simplifySingle(it,rules,list) } to list
}

internal fun simplifySingle(f : Formula,rules: List<Rule>,steps : MutableList<SimplificationStep>? = null) : Formula{
    for(r in rules){
        try {
            val t = r.tryApply(f) ?: continue
            steps?.add(SimplificationStep(r, f, t))
            return simplifyByRules(t, rules, steps)
        }catch (e : NullPointerException){
            throw e
        }
    }
    return f
}
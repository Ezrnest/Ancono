package cn.timelives.java.logic.firstOrderLogic

import cn.timelives.java.math.isSorted
import cn.timelives.java.utilities.CollectionSup

enum class FormulaType{
    ATOMIC,
    NOT,
    AND,
    OR,
    IMPLIES,
    EQUAL,
    FOR_ANY,
    EXIST
}

/**
 * Describes a term in first-order logic.
 * Created at 2018/9/24 10:45
 * @author  liyicheng
 */
sealed class LogicFormula : Comparable<LogicFormula> {
    abstract val type : FormulaType


    /**
     * Defines the bracket level of the logic formula.
     */
    internal abstract val bracketLevel: Int

    /**
     * The names of individual terms concerned in this statement.
     */
    abstract val individualTerms: Set<IndividualTerm>

    /**
     * The set of free individual terms, which is not constrained by a qualifier.
     */
    abstract val freeIndividualTerms : Set<IndividualTerm>

    /**
     * Returns the propositions occurred in this formula.
     */
    abstract val predicates: Set<Predicate>


    /**
     * Recursively applies the function until the given depth.
     */
    abstract fun recurApply(depth: Int = Int.MAX_VALUE, f: (LogicFormula) -> LogicFormula): LogicFormula

    fun replaceTerm(term : IndividualTerm, replacement : IndividualTerm) : LogicFormula{
        return replaceTerms(mapOf(term to replacement))
    }

    /**
     * Returns a new logic formula with terms replaced,
     */
    fun replaceTerms(map : Map<IndividualTerm,IndividualTerm>) : LogicFormula{
        return recurApply { f->
            if(f is AtomicFormula){
                val nTerms = f.terms.map { t -> map[t]?:t }
                AtomicFormula(f.predicate,nTerms)
            }else{
                f
            }
        }
    }

    private companion object DefaultNameReplacer : (String,Set<String>) -> String{
        private val regex : Regex = "(\\w+)(\\d+)?".toRegex()
        override fun invoke(s: String, others : Set<String>): String {
            when(s){
                "x" -> if(!others.contains("y")){return "y"}
                "y" -> if(!others.contains("z")){return "z"}
                "z" -> if(!others.contains("w")){return "w"}
            }
            fun nextName(str : String) : String{
                val mr = regex.matchEntire(str) ?: return str+"_"
                val w = mr.groups[1]!!
                val d = mr.groups[2]
                if(d != null){
                    return try{
                        "${w.value}${d.value.toInt()+1}"
                    }catch (ignore : Exception){
                        "${w.value}${d.value}_"
                    }
                }
                return "${w.value}1"
            }
            var newName : String = s
            do{
                newName = nextName(newName)
            }while (others.contains(newName))
            return newName
        }

    }

    /**
     * Replace all the duplicated terms in this formula. For example,
     * formula `∀xF(x) and ∀xG(x)` will be replaced to `∀xF(x) and ∀yG(y)`
     */
    fun replaceTermName(nameReplacer : (String,Set<String>) -> String = DefaultNameReplacer) : LogicFormula = recurApply {f ->
        when (f) {
            is BiLogicFormula -> {
                val names1 = f.p.individualTerms.map { it.name }
                val terms2 = f.q.individualTerms
                val names2 = terms2.map { it.name }
                val duplicated = names1 intersect names2
                if(duplicated.isEmpty()){
                    return@recurApply f
                }
                val universe = names1.toMutableSet().also { it.addAll(names2) }
                val replacementMap = hashMapOf<IndividualTerm,IndividualTerm>()
                for(name in duplicated){
                    val nextName = nameReplacer(name,universe)
                    val ori = terms2.first { it.name == name }
                    replacementMap += ori to ori.copy(name = nextName)
                    universe += nextName
                }
                val nq = f.q.replaceTerms(replacementMap)
                when(f){
                    is ImplyLogicFormula -> f.p implies nq
                    is EquivalentLogicFormula -> f.p equalTo nq
                }

            }
            is AOLogicFormula -> {
                val allNames = hashSetOf<String>()
                val nfs = f.fs.map {sub ->
                    val terms = sub.individualTerms
                    val names = terms.map { it.name }
                    val toReplace = allNames intersect names
                    allNames.addAll(names)
                    if(toReplace.isEmpty()){
                        return@map sub
                    }
                    val repMap = hashMapOf<IndividualTerm,IndividualTerm>()
                    for(nameToReplace in toReplace){
                        val prevTerm = terms.first { it.name == nameToReplace }
                        val newName = nameReplacer(nameToReplace,allNames)
                        repMap += prevTerm to prevTerm.copy(newName)
                        allNames += newName
                    }
                    return@map sub.replaceTerms(repMap)
                }
                andOr(f.isAnd,nfs)
            }
            else -> f
        }
    }

    internal fun wrapBracket(f: LogicFormula): String = if (f.bracketLevel >= this.bracketLevel) {
        "($f)"
    } else {
        f.toString()
    }

    /**
     * Determines whether this formula contains no free individual term.
     */
    val isClosed : Boolean
        get() = freeIndividualTerms.isEmpty()

    abstract override fun toString(): String

    abstract override fun equals(other: Any?): Boolean

    abstract override fun hashCode(): Int
}

typealias IndividualTermAssignment = Map<IndividualTerm,Any>

/**
 * An atomic formula is a predicate with several individual terms.
 */
class AtomicFormula(val predicate: Predicate, val terms : List<Term>) : LogicFormula(){
    override val bracketLevel: Int
        get() = 0
    override val individualTerms: Set<IndividualTerm> by lazy { terms.flatMapTo(hashSetOf()){
        it.individualTerms
    } }

    override val freeIndividualTerms: Set<IndividualTerm>
        get() = individualTerms

    override val predicates: Set<Predicate>
        get() = setOf(predicate)

    override fun recurApply(depth: Int, f: (LogicFormula) -> LogicFormula): LogicFormula {
        return if(depth > 1) {
            f(this)
        } else {
            this
        }
    }

    override fun toString(): String = buildString {
        append(predicate.notation)
        if(terms.isNotEmpty()) {
            terms.joinTo(this, ",", "(",")")
        }
    }

    override fun equals(other: Any?): Boolean {
        if( other !is AtomicFormula){
            return false
        }
        return predicate == other.predicate && terms == other.terms
    }

    override fun hashCode(): Int {
        return terms.hashCode() * 31 + predicate.hashCode()
    }

    override fun compareTo(other: LogicFormula): Int {
        if(type != other.type){
            return type.compareTo(other.type)
        }
        val comp = predicate.compareTo((other as AtomicFormula).predicate)
        if (comp != 0) {
            return comp
        }

        return CollectionSup.compareCollection(terms,other.terms)
    }

    override val type: FormulaType
        get() = FormulaType.ATOMIC
    
    fun test(assignment: IndividualTermAssignment) : Boolean = predicate.test(terms.map { assignment[it]!! })
}

sealed class SingleLogicFormula(val formula: LogicFormula) : LogicFormula(){
    override val individualTerms: Set<IndividualTerm>
        get() = formula.individualTerms

    override val predicates: Set<Predicate>
        get() = formula.predicates

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SingleLogicFormula) return false
        if (type != other.type) return false
        if (formula != other.formula) return false
        return true
    }

    override fun hashCode(): Int {
        return formula.hashCode()
    }
    override fun compareTo(other: LogicFormula): Int {
        if (other.type != this.type) {
            return this.type.compareTo(other.type)
        }
        return formula.compareTo((other as SingleLogicFormula).formula)
    }
}


class NotLogicFormula(formula: LogicFormula) : SingleLogicFormula(formula) {
    override val type: FormulaType
        get() = FormulaType.NOT
    override val bracketLevel: Int
        get() = 5

    override val freeIndividualTerms : Set<IndividualTerm>
        get() = individualTerms





    override fun toString(): String = "¬${wrapBracket(formula)}"

    override fun recurApply(depth: Int, f: (LogicFormula) -> LogicFormula): LogicFormula {
        return f(if(depth < 1) {
            (this)
        } else {
            !formula.recurApply(depth-1,f)
        })
    }




}

sealed class BiLogicFormula(val p: LogicFormula, val q: LogicFormula) : LogicFormula() {


    override val individualTerms: Set<IndividualTerm>
        get() = p.individualTerms union q.individualTerms
    override val predicates: Set<Predicate>
        get() = p.predicates union q.predicates

    override val freeIndividualTerms : Set<IndividualTerm>
         get() = individualTerms

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EquivalentLogicFormula) return false

        if (p != other.p) return false
        if (q != other.q) return false

        return true
    }

    override fun hashCode(): Int {
        var result = p.hashCode()
        result = 31 * result + q.hashCode()
        return result
    }

    override fun compareTo(other: LogicFormula): Int {
        if (other.type != this.type) {
            return this.type.compareTo(other.type)
        }
        val another = other as BiLogicFormula
        val comp = p.compareTo(another.p)
        if (comp != 0) {
            return comp
        }
        return q.compareTo(another.q)
    }
}

/**
 * And and Or formula
 */
class AOLogicFormula(val isAnd: Boolean, fs: List<LogicFormula>) : LogicFormula() {
    override val type: FormulaType
        get() = if(isAnd){
            FormulaType.AND
        }else{
            FormulaType.OR
        }
    val fs: List<LogicFormula> = if (fs.isSorted()) {
        fs
    } else {
        fs.sorted()
    }
    init {
        require(fs.size > 1)
    }
    override val bracketLevel: Int
        get() = 15

    override val individualTerms: Set<IndividualTerm> by lazy { fs.flatMapTo(hashSetOf()) { it.individualTerms }.toSet() }

    override val freeIndividualTerms : Set<IndividualTerm>
        get() = individualTerms

    override val predicates: Set<Predicate> by lazy { fs.flatMapTo(hashSetOf()) { it.predicates } }


    override fun toString(): String {
        val separator = if (isAnd) {
            "∧"
        } else {
            "∨"
        }
        return fs.joinToString(separator, transform = { wrapBracket(it) })
    }

    //"${wrapBracket(p)}∧${wrapBracket(q)}"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AOLogicFormula) return false

        if (isAnd != other.isAnd) return false
        if (fs != other.fs) return false

        return true
    }

    override fun hashCode(): Int {
        var result = isAnd.hashCode()
        result = 31 * result + fs.hashCode()
        return result
    }

    override fun recurApply(depth: Int, f: (LogicFormula) -> LogicFormula): LogicFormula {
        return f(if (depth < 1) {
            this
        } else {
            val re = fs.mapTo(ArrayList(fs.size)){it.recurApply(depth-1,f)}
            re.sort()
            AOLogicFormula(isAnd, re)
        })
    }

    override fun compareTo(other: LogicFormula): Int {
        if (other.type != this.type) {
            return this.type.compareTo(other.type)
        }
        val another = other as AOLogicFormula
//        if (isAnd) {
//            if (!another.isAnd) {
//                return -1
//            }
//        } else {
//            if (another.isAnd) {
//                return 1
//            }
//        }
        val fs1 = fs
        val fs2 = another.fs
        return CollectionSup.compareCollection(fs1, fs2, Comparator.naturalOrder())
    }
}

class ImplyLogicFormula(p: LogicFormula, q: LogicFormula) : BiLogicFormula(p, q) {
    override val type: FormulaType
        get() = FormulaType.IMPLIES
    override val bracketLevel: Int
        get() = 15

    override fun toString(): String {
        return "${wrapBracket(p)}→${wrapBracket(q)}"
    }

    override fun recurApply(depth: Int, f: (LogicFormula) -> LogicFormula): LogicFormula {
        return f(if (depth < 1) {
            this
        } else {
            p.recurApply(depth-1,f) implies q.recurApply(depth-1,f)
        })
    }
}

class EquivalentLogicFormula private constructor(p: LogicFormula, q: LogicFormula) : BiLogicFormula(p, q) {
    override val type: FormulaType
        get() = FormulaType.EQUAL
    
    override val bracketLevel: Int
        get() = 15



    override fun toString(): String {
        return "${wrapBracket(p)}↔${wrapBracket(q)}"
    }

    override fun recurApply(depth: Int, f: (LogicFormula) -> LogicFormula): LogicFormula {
        return f(if (depth < 1) {
            this
        } else {
            f(p) equalTo f(q)
        })
    }

    companion object {
        fun of(p : LogicFormula, q : LogicFormula) : EquivalentLogicFormula = if(p > q){
            EquivalentLogicFormula(q,p)
        }else{
            EquivalentLogicFormula(p,q)
        }
    }
}

class QualifiedFormula(val isAny : Boolean, formula: LogicFormula, val term : IndividualTerm)
    : SingleLogicFormula(formula){
    override val type: FormulaType
        get() = if(isAny){
            FormulaType.FOR_ANY
        }else{
            FormulaType.EXIST
        }
    override val bracketLevel: Int
        get() = 3
    override val freeIndividualTerms: Set<IndividualTerm>
        get() = individualTerms - term

    override fun recurApply(depth: Int, f: (LogicFormula) -> LogicFormula): LogicFormula {
        return f(if(depth<1){
            this
        }else{
            formula.recurApply(depth-1,f)
        })
    }

    override fun toString(): String = buildString {
        append(if(isAny){
            "∀"
        }else{
            "∃"
        })
        append(term.name)
        append(wrapBracket(formula))
    }
}

















val True = AtomicFormula(TruePredicate, emptyList())
val False = AtomicFormula(FalsePredicate, emptyList())

val F = AtomicFormulaSupplier("F")
val G = AtomicFormulaSupplier("G")
val H = AtomicFormulaSupplier("H")

class AtomicFormulaSupplier(val name: String){
    operator fun invoke(vararg terms : IndividualTerm) : AtomicFormula{
        val ts = arrayListOf(*terms)
        return AtomicFormula(UniversalPredicate(name,terms.size),ts)
    }
}

operator fun LogicFormula.not() = NotLogicFormula(this)

infix fun LogicFormula.and(another: LogicFormula) = AOLogicFormula(true, listOf(this, another))
infix fun LogicFormula.or(another: LogicFormula) = AOLogicFormula(false, listOf(this, another))
infix fun LogicFormula.implies(another: LogicFormula) = ImplyLogicFormula(this, another)
infix fun LogicFormula.equalTo(another: LogicFormula) = EquivalentLogicFormula.of(this, another)
fun andAll(vararg fs: LogicFormula): LogicFormula = andAll(fs.toList())
fun andAll(fs: List<LogicFormula>): LogicFormula {
    return when {
        fs.isEmpty() -> True
        fs.size == 1 -> fs[0]
        else -> AOLogicFormula(true, fs.toList())
    }
}

fun orAll(vararg fs: LogicFormula): LogicFormula = orAll(fs.toList())
fun orAll(fs: List<LogicFormula>): LogicFormula {
    return when {
        fs.isEmpty() -> False
        fs.size == 1 -> fs[0]
        else -> AOLogicFormula(false, fs)
    }
}

fun andOr(isAnd: Boolean, fs: List<LogicFormula>): LogicFormula = if (isAnd) {
    andAll(fs)
} else {
    orAll(fs)
}

fun any(individualTerm: IndividualTerm, formula: LogicFormula) = QualifiedFormula(true,formula,individualTerm)
fun any(name: String, formula: LogicFormula) = QualifiedFormula(true,formula,name.t)
fun exist(individualTerm: IndividualTerm, formula: LogicFormula) = QualifiedFormula(false,formula,individualTerm)
fun exist(name: String, formula: LogicFormula) = QualifiedFormula(false,formula,name.t)

operator fun Predicate.invoke(vararg terms : Term) : AtomicFormula{
    require(this.parameters.size == terms.size)
    return AtomicFormula(this,terms.toList())
}


fun main(args: Array<String>) {
//    val ep = "ep".t
//    val zero = "0".t
//    val N = "N".t
//    val n = "n".t
//    val m = "m".t
//    val a = "a".t
//    val biggerThan = Predicate.of2("B")
//    val x = Term.functionOf("x")//progression
//    val diff = Term.functionOf("diff"){list -> "|${list[0]}-${list[1]}|"}
//    println(exist(a,
//       any(ep, biggerThan(ep,zero) implies
//          exist(N,
//             any(n,
//                biggerThan(n,N) implies
//                   biggerThan(ep,
//                          diff(a,x(n))
//                   )
//             )
//          )
//       )
//    )
//    )

    println((any(a,F(a)) or exist(a,G(a))).replaceTermName())
}
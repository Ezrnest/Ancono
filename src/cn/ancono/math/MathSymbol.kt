package cn.ancono.math


@Suppress("unused")
/**
 * A file containing useful math symbols.
 *
 * Created at 2018/10/25 21:49
 * @author  liyicheng
 */
object MathSymbol {
    //algebraic symbol


    /**
     * The sign for multiplication, cross product, out product.
     */
    const val MUL_CROSS = "×"

    /**
     * The sign for multiplication, dot product, inner product.
     */
    const val MUL_DOT = "∙"

    const val DIVIDE_SIGN = "÷"
    /**
     * The sign for square root.
     */
    const val SQUARE_ROOT = "√"
    /**
     * The sign for infinity.
     */
    const val INFINITY = "∞"

    const val EXIST = "∃"
    const val FOR_ALL = "∀"

    const val ELEMENT_OF = "∈"
    const val CONTAINS = "∋"
    const val SUBSET_LEFT = "⊂"
    const val SUBSET_RIGHT = "⊃"

    const val EMPTY_SET = "∅"

    const val UNION = "∪"
    const val INTERSECT = "∩"

    const val INTEGRAL = "∫"

    const val ADD_ALL = "∑"
    const val MULTIPLY_ALL = "∏"


    const val ANGLE = "∠"


    /** Greek character ALPHA. */
    const val GREEK_ALPHA = "α"
    /** Greek character BETA. */
    const val GREEK_BETA = "β"
    /** Greek character GAMMA. */
    const val GREEK_GAMMA = "γ"
    /** Greek character DELTA. */
    const val GREEK_DELTA = "δ"
    /** Greek character EPSILON. */
    const val GREEK_EPSILON = "ε"
    /** Greek character ZETA. */
    const val GREEK_ZETA = "ζ"
    /** Greek character ETA. */
    const val GREEK_ETA = "η"
    /** Greek character THETA. */
    const val GREEK_THETA = "θ"
    /** Greek character IOTA. */
    const val GREEK_IOTA = "ι"
    /** Greek character KAPPA. */
    const val GREEK_KAPPA = "κ"
    /** Greek character LAMBDA. */
    const val GREEK_LAMBDA = "λ"
    /** Greek character MU. */
    const val GREEK_MU = "μ"
    /** Greek character NU. */
    const val GREEK_NU = "ν"
    /** Greek character XI. */
    const val GREEK_XI = "ξ"
    /** Greek character OMICRON. */
    const val GREEK_OMICRON = "ο"
    /** Greek character PI. */
    const val GREEK_PI = "π"
    /** Greek character RHO. */
    const val GREEK_RHO = "ρ"
    /** Greek character SIGMA. */
    const val GREEK_SIGMA = "σ"
    /** Greek character SIGMA2. */
    const val GREEK_SIGMA2 = "ς"
    /** Greek character TAO. */
    const val GREEK_TAO = "τ"
    /** Greek character UPSILON. */
    const val GREEK_UPSILON = "υ"
    /** Greek character PHI. */
    const val GREEK_PHI = "φ"
    /** Greek character CHI. */
    const val GREEK_CHI = "χ"
    /** Greek character PSI. */
    const val GREEK_PSI = "ψ"
    /** Greek character OMEGA. */
    const val GREEK_OMEGA = "ω"


    /** Greek capitalized character ALPHA. */
    const val GREEK_ALPHA_CAP = "Α"

    /** Greek capitalized character BETA. */
    const val GREEK_BETA_CAP = "Β"

    /** Greek capitalized character GAMMA. */
    const val GREEK_GAMMA_CAP = "Γ"

    /** Greek capitalized character DELTA. */
    const val GREEK_DELTA_CAP = "Δ"

    /** Greek capitalized character EPSILON. */
    const val GREEK_EPSILON_CAP = "Ε"

    /** Greek capitalized character ZETA. */
    const val GREEK_ZETA_CAP = "Ζ"

    /** Greek capitalized character ETA. */
    const val GREEK_ETA_CAP = "Η"

    /** Greek capitalized character THETA. */
    const val GREEK_THETA_CAP = "Θ"

    /** Greek capitalized character IOTA. */
    const val GREEK_IOTA_CAP = "Ι"

    /** Greek capitalized character KAPPA. */
    const val GREEK_KAPPA_CAP = "Κ"

    /** Greek capitalized character LAMBDA. */
    const val GREEK_LAMBDA_CAP = "Λ"

    /** Greek capitalized character MU. */
    const val GREEK_MU_CAP = "Μ"

    /** Greek capitalized character NU. */
    const val GREEK_NU_CAP = "Ν"

    /** Greek capitalized character XI. */
    const val GREEK_XI_CAP = "Ξ"

    /** Greek capitalized character OMICRON. */
    const val GREEK_OMICRON_CAP = "Ο"

    /** Greek capitalized character PI. */
    const val GREEK_PI_CAP = "Π"

    /** Greek capitalized character RHO. */
    const val GREEK_RHO_CAP = "Ρ"

    /** Greek capitalized character SIGMA. */
    const val GREEK_SIGMA_CAP = "Σ"

    /** Greek capitalized character TAO. */
    const val GREEK_TAO_CAP = "Τ"

    /** Greek capitalized character UPSILON. */
    const val GREEK_UPSILON_CAP = "Υ"

    /** Greek capitalized character PHI. */
    const val GREEK_PHI_CAP = "Φ"

    /** Greek capitalized character CHI. */
    const val GREEK_CHI_CAP = "Χ"

    /** Greek capitalized character PSI. */
    const val GREEK_PSI_CAP = "Ψ"

    /** Greek capitalized character OMEGA. */
    const val GREEK_OMEGA_CAP = "Ω"

    const val LOGIC_AND = "∧"

    const val LOGIC_OR = "∨"

    val toLatexFunctionNameMap: MutableMap<String, String>

    init {
        toLatexFunctionNameMap = hashMapOf(
                "sin" to "\\sin ",
                "cos" to "\\cos ",
                "tan" to "\\tan ",
                "exp" to "\\exp ",
                "ln" to "\\ln ",
                "log" to "\\log ",
                "arcsin" to "\\arcsin ",
                "arccos" to "\\arccos ",
                "arctan" to "\\sin "
        )
    }

    @JvmStatic
    fun getLatexFunctionName(n: String): String = toLatexFunctionNameMap.getOrDefault(n, n)
}
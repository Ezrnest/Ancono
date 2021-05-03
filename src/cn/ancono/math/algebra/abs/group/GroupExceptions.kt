package cn.ancono.math.algebra.abs.group


/**
 * An exception to throw when an input parameter is required to be
 * a subgroup but it is actually not a subgroup.
 * Created at 2018/10/9 15:50
 * @author  liyicheng
 */
open class NotASubgroupException(s: String?) : ArithmeticException(s) {
    constructor() : this(null)
}

class NotARequiredSubgroupException(s: String?) : NotASubgroupException(s) {
    constructor() : this(null)
}
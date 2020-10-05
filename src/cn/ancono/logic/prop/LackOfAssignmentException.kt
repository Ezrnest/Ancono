package cn.ancono.logic.prop

/*
 * Created at 2018/9/18
 * @author liyicheng
 */
class LackOfAssignmentException(message: String? = null, val requiredAssignment: String? = null) : RuntimeException(message) {
    constructor(requiredAssignment: String?) : this(
            if (requiredAssignment == null) {
                null
            } else {
                "Lack of assignment named $requiredAssignment"
            }, requiredAssignment)
}
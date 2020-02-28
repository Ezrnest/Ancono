package cn.ancono.math.exceptions

import cn.ancono.math.set.MathSet

/**
 * Describes the situation that a parameter is out of the
 * required domain.
 */
class OutOfDomainException(detail: String = "", val domain: MathSet<*>? = null, val value: Any? = null) : ArithmeticException(detail)

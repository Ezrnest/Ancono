package cn.timelives.java.math.numberModels.expression.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Indicates the node parameter(s) must not be modified.
 */
@Target({ElementType.PARAMETER,ElementType.METHOD})
public @interface DisallowModify {
}

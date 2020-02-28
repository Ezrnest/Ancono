package cn.timelives.java.math.numberModels.expression.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Indicates the node parameter(s) can be modified, including all it sub-nodes. These nodes can be used for
 */
@Target({ElementType.PARAMETER,ElementType.METHOD})
public @interface AllowModify {
}

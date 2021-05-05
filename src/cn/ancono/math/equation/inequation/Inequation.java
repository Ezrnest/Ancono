/**
 * 2017-10-06
 */
package cn.ancono.math.equation.inequation;

import cn.ancono.math.equation.AbstractCompareStructure;
import cn.ancono.math.equation.Type;
import cn.ancono.math.numberModels.api.RealCalculator;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Describes inequation in math. An inequation is presented as
 * <pre> left <i>op</i> right</pre>,
 *
 * @author liyicheng
 * 2017-10-06 08:49
 */
public abstract class Inequation<T, S> extends AbstractCompareStructure<T, S> {

    private static final Set<Type> inoperation =
            Collections.unmodifiableSet(
                    EnumSet.of(Type.GREATER, Type.GREATER_OR_EQUAL, Type.LESS, Type.LESS_OR_EQUAL));

    /**
     * @param mc
     * @param op must be one of the
     */
    protected Inequation(RealCalculator<T> mc, Type op) {
        super(mc, check(op));
    }


    /**
     * Determines whether the {@code type} is one of the inequation type:
     * <ul>
     * <li>GREATER
     * <li>GREATER_OR_EQUAL
     * <li>LESS
     * <li>LESS_OR_EQUAL
     * </ul>
     *
     * @param type
     * @return
     */
    public static boolean isOperation(Type type) {
        return inoperation.contains(type);
    }

    /**
     * Gets all the supported operations: {@literal >,<,>=,<=}
     *
     * @return a set of operations.
     */
    public static Set<Type> getOperation() {
        return inoperation;
    }

    private static Type check(Type t) {
        if (!isOperation(t)) {
            throw new IllegalArgumentException("Must be one of the operations for inequation!");
        }
        return t;
    }

}

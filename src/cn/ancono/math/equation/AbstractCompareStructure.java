/**
 * 2017-10-08
 */
package cn.ancono.math.equation;

import cn.ancono.math.AbstractMathObjectReal;
import cn.ancono.math.numberModels.api.RealCalculator;

/**
 * @author liyicheng
 * 2017-10-08 11:34
 */
public abstract class AbstractCompareStructure<T, S> extends AbstractMathObjectReal<T>
        implements CompareStructure<T, S> {
    /**
     * The type of operation.
     */
    protected final Type op;

    /**
     * @param mc
     */
    protected AbstractCompareStructure(RealCalculator<T> mc, Type op) {
        super(mc);
        this.op = op;
    }

    /**
     * Returns {@code mc.compare(y, mc.getZero())}
     *
     * @param y a number
     * @return {@code mc.compare(y, mc.getZero())}
     */
    protected int compareZero(T y) {
        return getMc().compare(y, getMc().getZero());
    }


    /**
     * Determines whether the given list of variables is one of the solutions.
     * The size of the list should be equal to the number of the variables and the order is
     * considered.
     *
     * @param x a list of variable
     * @return {@code true} if {@code x} is solution.
     */
    public boolean isSolution(S x) {
        return op.matches(compareZero(asFunction().apply(x)));
    }

    /**
     * Returns the type of the operation.
     *
     * @return the type
     */
    public Type getOperationType() {
        return op;
    }

}

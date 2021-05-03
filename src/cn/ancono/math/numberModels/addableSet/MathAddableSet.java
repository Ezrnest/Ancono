package cn.ancono.math.numberModels.addableSet;

import java.util.Set;


/**
 *
 * @author lyc
 */
public abstract class MathAddableSet<E> implements Set<E> {
    /**
     * it should have a Adder when create
     */
    final MathAdder<E> ma;

    public MathAddableSet(MathAdder<E> ma) {
        this.ma = ma;
    }

    /**
     * Return true if one of the elements in this can be added to adder
     * <p>
     *
     * @param adder
     * @return true if one of the elements in this can be added to adder
     */
    abstract public boolean containsAdditive(E adder);


    /**
     * this method should add e to this set ,according to the method canAdd in MathAdder
     * <p> if there is an elements that can be added to e ,it should add e to that element and return true
     * <p> In this situation, the size of this set won't change
     * <p> if there isn't such an element, it should just add e to this set ,and the size of this set won't change
     */
    @Override
    abstract public boolean add(E e);

    public MathAdder<E> getAdder() {
        return ma;
    }

}

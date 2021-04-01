package cn.ancono.math.numberModels.addableSet;

/**
 *
 * @author lyc
 */
public interface MathAdder<E> {
    public boolean canAdd(E e1, E e2);

    public E addEle(E e1, E e2);
}

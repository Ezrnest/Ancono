package cn.ancono.math.numberModels.addableSet;

/**
 * MathAdder ��MathAddable �ıر����֣�
 * <p>������������һ�����㹤��,���Լ��������ɼӶ���ĵ���
 *
 * @author lyc
 * @see FormulaCalculator
 */
public interface MathAdder<E> {
    public boolean canAdd(E e1, E e2);

    public E addEle(E e1, E e2);
}

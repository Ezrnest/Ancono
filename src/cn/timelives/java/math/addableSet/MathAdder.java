package cn.timelives.java.math.addableSet;

import cn.timelives.java.math.numberModels.FormulaCalculator;

/**
 * MathAdder ��MathAddable �ıر����֣�
 * <p>������������һ�����㹤��,���Լ��������ɼӶ���ĵ���
 * 
 * 
 * 
 * 
 * 
 * @see FormulaCalculator
 * @author lyc
 *
 */
public interface MathAdder<E> {
	public boolean canAdd(E e1,E e2);
	
	public E addEle(E e1,E e2);
}

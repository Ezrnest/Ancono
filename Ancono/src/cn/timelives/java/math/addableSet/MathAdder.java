package cn.timelives.java.math.addableSet;

import cn.timelives.java.math.numberModels.FormulaCalculator;

/**
 * MathAdder 是MathAddable 的必备部分，
 * <p>该类描述的是一个计算工具,可以计算两个可加对象的叠加
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

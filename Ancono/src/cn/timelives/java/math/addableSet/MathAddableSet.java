package cn.timelives.java.math.addableSet;

import java.util.Set;


/**
 * 此接口是描述可以通过数学运算叠加的对象，
 * 此类对象都为某种集合，储存的是{@code <E>}的对象
 * 
 * 
 * 对于可加对象，还需要具有特定格式的MathAdder
 * <p>可叠加性:
 * 
 * 
 * @see 
 * @author lyc
 *
 */
public abstract class MathAddableSet<E> implements Set<E>
{
	/**
	 * it should have a Adder when create
	 */
	MathAdder<E> ma;
	public MathAddableSet(MathAdder<E> ma){
		this.ma=ma;
	}
	
	/**
	 * Return true if one of the elements in this can be added to adder
	 * <p>
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
	
	
}

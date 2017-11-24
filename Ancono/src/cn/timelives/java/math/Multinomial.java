/**
 * 2017-10-06
 */
package cn.timelives.java.math;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.function.BiPredicate;
import java.util.function.Function;

import cn.timelives.java.math.numberModels.MathCalculator;
import cn.timelives.java.math.numberModels.NumberFormatter;


/**
 * A multinomial is an math expression of a variable, usually called {@code x}, with 
 * defined operations <i>add</i> and <i>multiply</i>. Generally, a multinomial can be shown as 
 * <pre>a_n*x^n + ... + a_1*x + a0 , (a_n!=0,n>=0)</pre> The operator {@literal x^n} represents 
 * for multiply {@code x} for {@code n} times. {@code n} is called the power of {@code x} and 
 * {@code a_n} is called the coefficient.
 * @author liyicheng
 * 2017-10-06 16:51
 *
 */
public interface Multinomial<T> extends Iterable<T>{
	/**
	 * Gets the max power of {@code x}.
	 * @return the max power of {@code x}.
	 */
	int getMaxPower();
	/**
	 * Gets the number of the corresponding index, throws {@link IndexOutOfBoundsException}
	 * if {@code n<0 || n>getMaxPower()}. 
	 * @param n the power of the variable.
	 * @return
	 */
	T getCoefficient(int n);
	
	/** 
	 * Iterators the coefficient from the lowest one(a0).
	 */
	@Override
	public default Iterator<T> iterator() {
		return new It<>(this);
	}
	/**
	 * Determines whether the two multinomial are equal.
	 * @param m1 
	 * @param m2
	 * @param equal
	 * @return
	 */
	public static <T,S> boolean isEqual(Multinomial<T> m1,Multinomial<S> m2,BiPredicate<T, S> equal) {
		if(m1.getMaxPower() != m2.getMaxPower()) {
			return false;
		}
		int mp =m1.getMaxPower();
		for(int i=0;i<=mp;i++) {
			if(!equal.test(m1.getCoefficient(i), m2.getCoefficient(i))) {
				return false;
			}
		}
		return true;
	}
	/**
	 * Determines whether the two multinomial are equal, using the 
	 * equals() method in object.
	 * @param m1 
	 * @param m2
	 * @return
	 */
	public static boolean isEqual(Multinomial<?> m1,Multinomial<?> m2) {
		if(m1.getMaxPower() != m2.getMaxPower()) {
			return false;
		}
		int mp =m1.getMaxPower();
		for(int i=0;i<=mp;i++) {
			if(!m1.getCoefficient(i).equals(m2.getCoefficient(i))) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Returns the basically implemented hash code. 
	 * <code>int hash = 0;
	 *	int mp = m.getMaxPower();
	 *	for(int i=0;i<=mp;i++) {
	 *		hash = hash *31 + m.getCoefficient(i).hashCode();
	 *	}
	 *	return hash;</code>
	 * 
	 * @param m
	 *            a Multinomial
	 * @return
	 */
	public static int hashCodeOf(Multinomial<?> m) {
		int hash = 0;
		int mp =m.getMaxPower();
		for(int i=0;i<=mp;i++) {
			hash = hash *31 + m.getCoefficient(i).hashCode();
		}
		return hash;
	}
	
	public static <T> String stringOf(Multinomial<T> m,MathCalculator<T> mc,NumberFormatter<T> nf) {
		int maxPower = m.getMaxPower();
		if(maxPower== 0) {
			return nf.format(m.getCoefficient(0), mc);
		}
		StringBuilder sb = new StringBuilder();
		for(int i=maxPower;i>0;i--){
			if(mc.isZero(m.getCoefficient(i)))
				continue;
			T a = m.getCoefficient(i);
			if(mc.isEqual(mc.getOne(), a)) {
				if(i != 1) {
					sb.append("x^").append(i);
				}else {
					sb.append("x");
				}
			}else {
				sb.append(nf.format(m.getCoefficient(i), mc));
				if(i != 1) {
					sb.append("*x^").append(i);
				}else {
					sb.append("*x");
				}
			}
			sb.append(" + ");
			
		}
		if(mc.isZero(m.getCoefficient(0))==false){
			sb.append(nf.format(m.getCoefficient(0), mc));
		}else{
			sb.delete(sb.length()-3, sb.length());
		}
		return sb.toString();
	}
}
class It<T> implements ListIterator<T>{
	private final Multinomial<T> f;
	private final int max;
	private int n;
	/**
	 * 
	 */
	public It(Multinomial<T> f) {
		this.f = f;
		this.max = f.getMaxPower();
	}
	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return n<=max;
	}
	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	@Override
	public T next() {
		try {
			int index = n+1;
			T t =  f.getCoefficient(n);
			n = index;
			return t;
		}catch(IndexOutOfBoundsException ex) {
			throw new NoSuchElementException();
		}
	}
	/*
	 * @see java.util.ListIterator#add(java.lang.Object)
	 */
	@Override
	public void add(T arg0) {
		throw new UnsupportedOperationException();
	}
	/*
	 * @see java.util.ListIterator#hasPrevious()
	 */
	@Override
	public boolean hasPrevious() {
		return n>0;
	}
	/*
	 * @see java.util.ListIterator#nextIndex()
	 */
	@Override
	public int nextIndex() {
		return n;
	}
	/*
	 * @see java.util.ListIterator#previous()
	 */
	@Override
	public T previous() {
		try {
			int index = n-1;
			T t =  f.getCoefficient(n);
			n = index;
			return t;
		}catch(IndexOutOfBoundsException ex) {
			throw new NoSuchElementException();
		}
	}
	/*
	 * @see java.util.ListIterator#previousIndex()
	 */
	@Override
	public int previousIndex() {
		return n-1;
	}
	/*
	 * @see java.util.ListIterator#remove()
	 */
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
	/*
	 * @see java.util.ListIterator#set(java.lang.Object)
	 */
	@Override
	public void set(T arg0) {
		throw new UnsupportedOperationException();
	}
	
	
}

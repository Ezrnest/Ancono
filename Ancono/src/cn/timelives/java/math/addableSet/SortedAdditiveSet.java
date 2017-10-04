package cn.timelives.java.math.addableSet;

import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;
/**
 * SortedAdditiveSet 是一个有序的,储存特定对象的集合
 * <p>此集合可以储存任意对象，但一般来说，
 * 
 * @author lyc
 *
 * @param <E>
 */
public class SortedAdditiveSet<E> extends MathAddableSet<E>
{
	private TreeSet<E> eleData=new TreeSet<E>();
	
	public SortedAdditiveSet(MathAdder<E> ma) {
		super(ma);
	}

	@Override
	public int size() {
		return eleData.size();
	}

	@Override
	public boolean isEmpty() {
		return eleData.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return eleData.contains(o);
	}

	@Override
	public Iterator<E> iterator() {
		return eleData.iterator();
	}

	@Override
	public Object[] toArray() {
		return eleData.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return eleData.toArray(a);
	}

	@Override
	public boolean remove(Object o) {
		return eleData.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return eleData.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		for(E e:c){
			this.add(e);
		}
		return true;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return eleData.retainAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return eleData.removeAll(c);
	}

	@Override
	public void clear() {
		eleData.clear();
	}

	@Override
	public boolean containsAdditive(E adder) {
		for(E e:eleData){
			if(ma.canAdd(e,adder)){
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean add(E adder) {
		E e;
		for(Iterator<E> i=eleData.iterator();i.hasNext();){
			e=i.next();
			if(ma.canAdd(e, adder)){
				i.remove();
				i=null;
				this.add(ma.addEle(e,adder));//the new element should be checked again
				return true;
			}
		}
		eleData.add(adder);
		return false;
	}
	@Override
	public String toString(){
		return eleData.toString();
	}
	public MathAdder<E> getAdder(){
		return ma;
	}
	
	public boolean equals(SortedAdditiveSet<E> s){
		if(s.size()!=this.size())
			return false;
		Iterator<E> i1 = this.eleData.iterator();
		Iterator<E> i2 = s.eleData.iterator();
		boolean re = true;
		while(i1.hasNext()){
			re=i1.next().equals(i2.next());
			if(!re)
				return false;
		}
		return true;
	}
	

}

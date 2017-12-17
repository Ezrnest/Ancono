package cn.timelives.java.math.addableSet;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * 
 * 
 * 
 * @author lyc
 *
 */
public class AdditiveSet<E> extends MathAddableSet<E>
{
	private final HashSet<E> eleData;
	public AdditiveSet(MathAdder<E> ma) {
		super(ma);
		eleData = new HashSet<E>();
	}
	
	public AdditiveSet(MathAdder<E> ma,int initCap){
		super(ma);
		eleData = new HashSet<>(initCap);
	}
	
	@Override
	public boolean containsAdditive(E adder){
		for(E e:eleData){
			if(ma.canAdd(e,adder)){
				return true;
			}
		}
		return false;
	}
	
	public MathAdder<E> getAdder(){
		return ma;
	}
	
	
	
	@Override
	public int size(){
		return eleData.size();
	}
	@Override
	public boolean isEmpty(){
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
	
	@Override
	public boolean equals(Object obj){
		if(obj instanceof AdditiveSet){
			AdditiveSet<?> as = (AdditiveSet<?>) obj;
			return this.eleData.equals(as.eleData);
		}
		return false;
	}
	
	
}

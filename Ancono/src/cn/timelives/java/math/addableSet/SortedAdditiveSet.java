package cn.timelives.java.math.addableSet;

import static cn.timelives.java.utilities.Printer.print;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.regex.Pattern;

import cn.timelives.java.math.numberModels.Polynomial;
/**
 * SortedAdditiveSet ��һ�������,�����ض�����ļ���
 * <p>�˼��Ͽ��Դ���������󣬵�һ����˵��
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
	
	public E getFirst() {
		return eleData.first();
	}
	
	public E getLast() {
		return eleData.last();
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
//	public static void main(String[] args) {
//		String str = "-1/16*d[y1]d[y3]^2*p, +1/16*d[y1]^2*d[y2]p, -1/16*d[y1]^2*d[y3]p, -1/16*d[y1]^2*d[y2]p, "
//				+ "+1/8*d[y1]^2*d[y2]d[y3]^2*p^-1, -1/8*d[y1]^2*d[y2]d[y3]^2*p^-1, +1/16*d[y1]^3*d[y2]d[y3]^3*p^-3,"
//				+ " -1/16*d[y1]^3*d[y2]d[y3]^3*p^-3, +1/16*d[y1]^2*d[y2]^3*d[y3]^2*p^-3, +1/16*d[y1]d[y2]^3*d[y3]^3*p^-3, "
//				+ "-1/16*d[y1]d[y2]^3*d[y3]^3*p^-3, -1/16*d[y1]^2*d[y2]^3*d[y3]^2*p^-3, +1/16*d[y2]d[y3]^4*p^-1, +1/16*d[y1]d[y2]^2*p, "
//				+ "+1/16*d[y1]^2*d[y3]p, +1/16*d[y1]d[y3]^2*p, -1/16*d[y2]d[y3]^4*p^-1, -1/16*d[y1]d[y2]^2*p, +1/16*d[y2]^2*d[y3]p,"
//				+ " -1/16*d[y2]^2*d[y3]p, +1/16*d[y1]^3*d[y2]^2*p^-1, -1/16*d[y1]^3*d[y2]^2*p^-1";
//		str = str.replaceAll(Pattern.quote(","), "").replaceAll(" +", "");
//		print(str);
//		Polynomial p = Polynomial.valueOf(str);
//		print(p);//0
//	}

}

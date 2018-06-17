/**
 * 2017-09-08
 */
package cn.timelives.java.math.set;

import cn.timelives.java.math.MathObject;
import cn.timelives.java.math.MathCalculator;
import cn.timelives.java.math.numberModels.api.FlexibleNumberFormatter;
import cn.timelives.java.math.numberModels.api.NumberFormatter;
import cn.timelives.java.utilities.ArraySup;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Function;

/**
 * A collection set is a set that is created from a collection. 
 * @author liyicheng
 * 2017-09-08 17:01
 *
 */
public final class CollectionSet<T> extends AbstractLimitedSet<T> {
	private final List<T> list;
	/**
	 * Creates a collection set, the collection can be modified after this method.
	 * @param mc
	 */
	CollectionSet(MathCalculator<T> mc,Collection<T> coll) {
		super(mc);
		list = new ArrayList<>(coll);
	}
	/**
	 * Creates a collection set with a list which should not be modified.
	 * @param mc
	 * @param list
	 */
	CollectionSet(MathCalculator<T> mc,List<T> list){
		super(mc);
		this.list = list;
	}
	
	/**
	 * @see cn.timelives.java.math.set.FiniteSet#get(java.math.BigInteger)
	 */
	@Override
	public T get(BigInteger index) {
		int val = index.intValueExact();
		return list.get(val);
	}
	/**
	 * @see cn.timelives.java.math.set.CountableSet#iterator()
	 */
	@Override
	public ListIterator<T> listIterator() {
		return list.listIterator();
	}
	/**
	 * @see cn.timelives.java.math.set.CountableSet#size()
	 */
	@Override
	public long size() {
		return list.size();
	}
	/**
	 * @see cn.timelives.java.math.set.MathSet#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(T t) {
		for(T e : list){
			if(mc.isEqual(t, e)){
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * @see MathObject#mapTo(java.util.function.Function, MathCalculator)
	 */
	@Override
	public <N> CollectionSet<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator) {
		List<N> nlist = new ArrayList<>(list.size());
		for(T t : list){
			nlist.add(mapper.apply(t));
		}
		return new CollectionSet<>(newCalculator, nlist);
	}
	
	/**
	 * @see MathObject#valueEquals(MathObject)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean valueEquals(MathObject<T> obj) {
		if(obj == this){
			return true;
		}
		if(obj instanceof CollectionSet){
			CollectionSet<T> cs= (CollectionSet<T>) obj;
			if(cs.size()!=size()){
				return false;
			}
			return ArraySup.arrayEqualNoOrder(list.toArray(), cs.list.toArray(),(x,y)->mc.isEqual((T)x, (T)y));
		}
		
		return super.valueEquals(obj);
	}
	
	/**
	 * @see MathObject#valueEquals(MathObject, java.util.function.Function)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <N> boolean valueEquals(MathObject<N> obj, Function<N, T> mapper) {
		if(obj == this){
			return true;
		}
		if(obj instanceof CollectionSet){
			CollectionSet<N> cs= (CollectionSet<N>) obj;
			if(cs.size()!=size()){
				return false;
			}
			return ArraySup.arrayEqualNoOrder(list.toArray(), ArraySup.modifyAll(list.toArray(),(x)->
			mapper.apply((N)x)),(x,y)->mc.isEqual((T)x, (T)y));
		}
		
		return super.valueEquals(obj,mapper);
	}
	
	/**
	 * @see MathObject#toString(NumberFormatter)
	 */
	@Override
	public String toString(FlexibleNumberFormatter<T,MathCalculator<T>> nf) {
		StringBuilder sb = new StringBuilder();
		sb.append('{');
		for(T t : list){
			sb.append(nf.format(t, mc));
			sb.append(',');
		}
		sb.deleteCharAt(sb.length()-1);
		sb.append('}');
		return sb.toString();
	}
	
	

}

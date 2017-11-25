/**
 * 2017-09-10
 */
package cn.timelives.java.utilities;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;

import cn.timelives.java.math.MathUtils;


/**
 * @author liyicheng
 * 2017-09-10 14:24
 *
 */
public final class CollectionSup {

	/**
	 * 
	 */
	private CollectionSup() {
	}
	/**
	 * Applies {@code mapper} to the list and then add the result to a new list.
	 * @param list 
	 * @param mapper
	 * @return
	 */
	public static <T,S> List<S> mapList(List<T> list,Function<T,S> mapper){
		ArrayList<S> re = new ArrayList<>(list.size());
		for(T t : list){
			re.add(mapper.apply(t));
		}
		return re;
	}
	/**
	 * Applies {@code mapper} to the list and then add the result to {@code dest}. 
	 * @param list
	 * @param mapper
	 * @param dest
	 * @return
	 */
	public static <T,S> List<S> mapAdd(List<T> list,Function<T,S> mapper,List<S> dest){
		for(T t : list){
			dest.add(mapper.apply(t));
		}
		return dest;
	}
	/**
	 * Determines whether the two lists are equal by means of {@code isEqual}. The two 
	 * lists are equal only when they have the same size and the corresponding elements 
	 * are the same.
	 * @param list1 a list
	 * @param list2 another list
	 * @param isEqual a function to determine whether two elements are equal.
	 * @return {@code true} if the two lists are equal.
	 */
	public static <T,S> boolean listEqual(List<T> list1,List<S> list2,BiPredicate<T, S> isEqual) {
		if(list1.size()!=list2.size()) {
			return false;
		}
		Iterator<T> it1 = list1.iterator();
		Iterator<S> it2 = list2.iterator();
		while(it1.hasNext()) {
			T t = it1.next();
			S s = it2.next();
			if(!isEqual.test(t, s)) {
				return false;
			}
		}
		return true;
	}
	/**
	 * Applies the function to all the entries in the map.
	 * @param map a map
	 * @param f a function to compute the value
	 */
	public static <T,S> void modifyMap(Map<T,S> map,BiFunction<T,S,S> f) {
		for(Entry<T,S> en : map.entrySet()) {
			en.setValue(f.apply(en.getKey(), en.getValue()));
		}
	}
	
	public static <T> int compareList(List<T> list1,List<T> list2,Comparator<T> comp) {
		int com = list1.size() - list2.size();
		if(com != 0) {
			return com;
		}
		Iterator<T> it1 = list1.iterator(),
				it2 = list2.iterator();
		while(it1.hasNext()) {
			T a = it1.next();
			T b = it2.next();
			com = comp.compare(a, b);
			if(com!=0) {
				return com;
			}
		}
		return 0;
	}

}

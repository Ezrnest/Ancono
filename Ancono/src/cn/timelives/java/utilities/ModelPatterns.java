/**
 * 
 */
package cn.timelives.java.utilities;

import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.IntUnaryOperator;
import java.util.function.LongToIntFunction;
import java.util.function.ToIntFunction;

import cn.timelives.java.math.MathUtils;

/**
 * Some useful abstract models or patterns that is used in programming.
 * @author liyicheng
 *
 */
public final class ModelPatterns {

	/**
	 * 
	 */
	private ModelPatterns() {
	}
	
	/**
	 * Operates a binary search. This method is a long version of {@link #binarySearch(int, int, IntUnaryOperator)}
	 * @param fromIndex the lower bound, inclusive
	 * @param toIndex the upper bound, exclusive
	 * @param comparator a comparator
	 * @return the index of the key or (-(insertion point) - 1).
	 * @see #binarySearch(int, int, IntUnaryOperator)
	 */
	public static long binarySearchL(long fromIndex,long toIndex,LongToIntFunction comparator){
		if(fromIndex <0 || toIndex <0 || fromIndex > toIndex){
			throw new IllegalArgumentException();
		}
		// the code copied from Arrays.binarySearch
		long low = fromIndex;
        long high = toIndex - 1;

        while (low <= high) {
        	long mid = (low + high) >>> 1;
            int cmp = comparator.applyAsInt(mid);
            if (cmp < 0)
                low = mid + 1;
            else if (cmp > 0)
                high = mid - 1;
            else
                return mid; // key found
        }
        return -(low + 1);  // key not found.
		
	}
	/**
	 * Operates a binary search. For example, a binary search 
	 * for a sorted array can be done as follow:
	 * <pre>
	 * 	final int key = ... ;
	 * 	int index = binarySearch(0,arr.length, x-> arr[x] < key ? -1 : arr[x] == key ? 0 : -1 );
	 * 
	 * </pre>
	 * Note that this method doesn't supports negative values for {@code fromIndex} or {@code toIndex},
	 * and {@code fromIndex} should not be bigger than {@code toIndex}
	 * @param fromIndex the lower bound, inclusive
	 * @param toIndex the upper bound, exclusive
	 * @param comparator a comparator that determines whether the key is "in front of" the given index 
	 * (return -1), is at the index(return 0) or "behind" it (return 1), which is equal to 
	 * {@code arr[i].compareTo(key)}
	 * @return index of the search key, 
	 * if it is contained in the array within the specified range; 
	 * otherwise, (-(insertion point) - 1).
	 *  The insertion point is defined as the point at which the key would be inserted into the "array": 
	 *  the index of the first element in the range greater than the key, 
	 *  or toIndex if all elements in the range are less than the specified key. 
	 *  Note that this guarantees that the return value will be >= 0 if and only if the key is found.
	 */
	public static int binarySearch(int fromIndex,int toIndex,IntUnaryOperator comparator){
		if(fromIndex <0 || toIndex <0 || fromIndex > toIndex){
			throw new IllegalArgumentException();
		}
		// the code copied from Arrays.binarySearch
		int low = fromIndex;
        int high = toIndex - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            int cmp = comparator.applyAsInt(mid);
            if (cmp < 0)
                low = mid + 1;
            else if (cmp > 0)
                high = mid - 1;
            else
                return mid; // key found
        }
        return -(low + 1);  // key not found.
	}
	/**
	 * Solve a 'problem' with binary search method. For example, to find a function's zero point, 
	 * assuming the function is {@code f(x)} and the range to search is {@code [0,1]}, then
	 * {@code binarySolve(0d,1d,(a,b)->(a+b)/2,x->signum(f(x)),100)} will try to find the zero point and iterate for 100 times.
	 * @param low
	 * @param high
	 * @param middle
	 * @param comparator
	 * @param maxTime
	 * @return
	 */
	public static <T> T binarySolve(T low,T high,BinaryOperator<T> middle,ToIntFunction<T> comparator,int maxTime) {
		T mid = middle.apply(low, high);
		int cl = comparator.applyAsInt(low);
		int ch = comparator.applyAsInt(high);
		if(cl == 0) {
			return low;
		}
		if(ch == 0) {
			return high;
		}
		if(MathUtils.sameSignum(cl, ch)) {
			throw new IllegalArgumentException("Sign numbers are the same!");
		}
		
		boolean downerNegative = cl < 0;
		for(int i=0;i<maxTime;i++) {
			int t = comparator.applyAsInt(mid);
			if(t == 0) {
				return mid;
			}
			if(downerNegative^(t<0)) {
				high = mid;
			}else {
				low = mid;
			}
			mid = middle.apply(low, high);
		}
		return mid;
		
	}
	/**
	 * Solve a 'problem' with binary search method. For example, to find a function's zero point, 
	 * assuming the function is {@code f(x)} and the range to search is {@code [0,1]}, then
	 * {@code binarySolve(0d,1d,(a,b)->(a+b)/2,x->signum(f(x)),100)} will try to find the zero point and iterate for 100 times.
	 * @param low
	 * @param high
	 * @param middle
	 * @param comparator
	 * @param next
	 * @return
	 */
	public static <T> T binarySolve(T low,T high,BinaryOperator<T> middle,ToIntFunction<T> comparator,
			BiPredicate<T, T> next) {
		T mid = middle.apply(low, high);
		int cl = comparator.applyAsInt(low);
		int ch = comparator.applyAsInt(high);
		if(cl == 0) {
			return low;
		}
		if(ch == 0) {
			return high;
		}
		if(MathUtils.sameSignum(cl, ch)) {
			throw new IllegalArgumentException("Sign numbers are the same!");
		}
		
		boolean downerNegative = cl < 0;
		while(next.test(low, high)) {
			int t = comparator.applyAsInt(mid);
			if(t == 0) {
				return mid;
			}
			if(downerNegative^(t<0)) {
				high = mid;
			}else {
				low = mid;
			}
			mid = middle.apply(low, high);
		}
		return mid;
		
	}
	
	/**
	 * Performs an operation like computing {@code exp(x,p)}.
	 * @param p a non-negative number
	 * @param unit the unit value, such as 1.
	 * @param x
	 * @param square
	 * @param multiply
	 * @return
	 */
	public static <T> T binaryReduce(long p,T unit,T x,Function<T,T> square,BinaryOperator<T> multiply) {
		if(p<0){
			throw new IllegalArgumentException("p<0");
		}else if(p==0){
			return unit;
		}
		T re = unit;
		while(p>0){
			if((p&1)!=0){
				re = multiply.apply(x,re);
			}
			x = square.apply(x);
			p>>=1;
		}
		return re;
	}
	/**
	 * Performs an operation like computing {@code exp(x,p)}.
	 * @param p a non-negative number
	 * @return
	 */
	public static <T> T binaryReduce(long p,T unit,T x,BinaryOperator<T> multiply) {
		if(p<0){
			throw new IllegalArgumentException("p<0");
		}else if(p==0){
			return unit;
		}
		T re = unit;
		while(p>0){
			if((p&1)!=0){
				re = multiply.apply(x,re);
			}
			x = multiply.apply(x,x);
			p>>=1;
		}
		return re;
	}
	/**
	 * Performs an operation like computing {@code exp(x,p)}.
	 * @param p a positive number
	 * @param multiply
	 * @return
	 */
	public static <T> T binaryReduce(long p,T x,BinaryOperator<T> multiply) {
		if(p<=0){
			throw new IllegalArgumentException("p<=0");
		}
		return binaryReduce(p-1, x, x, multiply);
	}
	
//	public static void main(String[] args) {
//		DoubleUnaryOperator f = d -> d*d-d;
//		print(binarySolve(0.5d, 3d, (a,b)->(a+b)/2,x-> {
//			double t = f.applyAsDouble(x);
//			return t < 0 ? -1 : t == 0 ? 0 : 1;
//		}, 10));
//	}
}

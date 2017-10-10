/**
 * 
 */
package cn.timelives.java.utilities;

import java.util.function.IntUnaryOperator;
import java.util.function.LongToIntFunction;

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
	
	
	
}

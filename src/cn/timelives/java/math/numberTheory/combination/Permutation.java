/**
 * 2018-03-01
 */
package cn.timelives.java.math.numberTheory.combination;

import cn.timelives.java.math.property.Composable;
import cn.timelives.java.math.property.Invertible;
import cn.timelives.java.math.MathUtils;
import cn.timelives.java.utilities.ArraySup;

import java.util.Collections;
import java.util.List;

/**
 * A permutation describes a transformation on a finite set of elements.
 * Different from the mathematical permutation,
 *  the smallest index in this permutation should be <b>zero</b>.
 *  <p>
 *  See: <a href="https://en.wikipedia.org/wiki/Permutation">Permutation</a> 
 * @author liyicheng
 * 2018-03-01 19:26
 * @see Permutations
 */
public interface Permutation extends Composable<Permutation>,Invertible<Permutation>{
	
	/**
	 * Returns the size of this permutation, which is equal to the 
	 * size of the finite set.
	 * @return
	 */
	int size();
	
	/**
	 * Returns the index of the element of index {@code x} after the this permutation.<P> 
	 * For example, if the permutation is (1,0,2), then {@code apply(1)} returns 0.
	 * @param x
	 * @return
	 */
	int apply(int x);
	/**
	 * Returns the index before this permutation of the element of index {@code y} after this permutation. 
	 * It is ensured that {@code inverse(apply(n))==n}.
	 * <P> 
	 * For example, if the permutation is (1,0,2), then {@code inverse(1)} returns 0.
	 * @param y
	 * @return
	 */
	int inverse(int y);
	
	/**
	 * Returns the inverse of this permutation.
	 * @return
	 */
	Permutation inverse();
	
	/**
	 * Returns the index of this permutation. The index is 
	 * a number ranged in [0,size!-1]. It represents the index of this 
	 * permutation in all the permutations of the same size ordered by 
	 * the natural of their representative array. The identity permutation always 
	 * has the index of {@code 0} and the total flip permutation always has the 
	 * index of {@code size!-1}<P>
	 * For example, the index of {@code (1,0,2)} is {@code 2}, because all 
	 * 3-permutations are sorted as  
	 * {@code (0,1,2),(0,2,1),(1,0,2),(1,2,0),(2,0,1),(2,1,0)}. 
	 * @return
	 */
	default long index() {
		long sum = 0;
		int[] arr = getArray();
		for(int i=0;i<arr.length;i++) {
			sum += arr[i] * CFunctions.factorial(arr.length-i-1);
			for(int j=i+1;j<arr.length;j++) {
				if(arr[j]>arr[i]) {
					arr[j]--;
				}
			}
		}
		return sum;
	}
	
	/**
	 * Reduces this permutation to several elementary permutations. The last element in the 
	 * list is the first permutation that should be applied. Therefore, assume the elements 
	 * in the list in order are {@code p1,p2,p3...pn}, then {@code p1·p2·...·pn == this}, where 
	 * {@code ·} is the compose of permutations. 
	 * <P>
	 * For example, if {@code this=(4,0,3,1,2)}, then the returned list can 
	 * be equal to {@code (0,4)(0,1)(2,4)(3,4)}.
	 * @return
	 */
	List<Transposition> decomposeTransposition();
	/**
	 * Returns the count of inverted number in the array representing this permutation.
	 * @return
	 */
	default int inverseCount() {
		int[] arr = getArray();
		int count = 0;
		for(int i=0;i<arr.length;i++) {
			int t = arr[i];
			for(int j=i+1;j<arr.length;j++) {
				if(arr[j]<t) {
					count ++;
				}
			}
		}
		return count;
	}
	/**
	 * Determines whether this permutation is an even permutation.
	 * @return
	 */
	default boolean isEven() {
		return inverseCount() %2 == 0;
	}
	
	/**
	 * Decompose this permutation to several non-intersecting rotation permutations. The order is not 
	 * strictly restricted because the rotation permutations are commutative. The list may omit 
	 * rotations of length 1.
	 * <P>
	 * For example, if {@code this=(2,0,4,3,1,7,6,5)}, then the returned list can 
	 * be equal to {@code (1,3,5,2)(6,8)}, and 
	 * @return
	 */
	List<Cycle> decompose();
	/**
	 * Returns the rank of this permutation.<P>
	 * {@code this^rank=identity}
	 * @return
	 */
	default int rank() {
		List<Cycle> list = decompose();
		int rank = 1;
		for(Cycle p : list) {
			rank = MathUtils.lcm(rank, p.rank());
		}
		return rank;
	}
	
	/*
     * Returns a composed permutation that first applies the {@code before}
     * permutation to its input, and then applies this permutation to the result.
     * 
     */
    Permutation compose(Permutation before);

    /**
     * Returns a composed permutation that first applies this permutation to
     * its input, and then applies the {@code after} permutation to the result.
     * 
     */
    Permutation andThen(Permutation after);
	
	/**
	 * Gets a copy of array representing this permutation. For each index n in the range, 
	 * {@code arr[n]==apply(n)}
	 * @return
	 */
	default int[] getArray() {
		int length = size();
		int[] arr = new int[length];
		for(int i=0;i<length;i++) {
			arr[i] = apply(i);
		}
		return arr;
	}
	
	/**
	 * Applies this permutation to an array.
	 * @param array
	 * @return
	 */
	default <T> T[] apply(T[] array) {
		if(array.length<size()) {
			throw new IllegalArgumentException("array's length!="+size());
		}
		T[] copy = array.clone();
		for(int i=0;i<array.length;i++) {
			array[i] = copy[apply(i)];
		}
		return array;
	}
	/**
	 * Applies this permutation to an integer array.
	 * @param array
	 * @return
	 */
	default int[] apply(int[] array) {
		if(array.length<size()) {
			throw new IllegalArgumentException("array's length!="+size());
		}
		int[] copy = array.clone();
		for(int i=0;i<array.length;i++) {
			array[i] = copy[apply(i)];
		}
		return array;
	}
	
	/**
	 * Applies this permutation to an array.
	 * @param array
	 * @return
	 */
	default double[] apply(double[] array) {
		if(array.length<size()) {
			throw new IllegalArgumentException("array's length!="+size());
		}
		double[] copy = array.clone();
		for (int i = 0; i < array.length; i++) {
			array[i] = copy[apply(i)];
		}
		return array;
	}
	/**
	 * Applies this permutation to an array.
	 * @param array
	 * @return
	 */
	default boolean[] apply(boolean[] array) {
		if(array.length<size()) {
			throw new IllegalArgumentException("array's length!="+size());
		}
		boolean[] copy = array.clone();
		for (int i = 0; i < array.length; i++) {
			array[i] = copy[apply(i)];
		}
		return array;
	}
	
	/**
	 * Applies this permutation to an array.
	 * @param array
	 * @return
	 */
	default long[] apply(long[] array) {
		if(array.length<size()) {
			throw new IllegalArgumentException("array's length!="+size());
		}
		long[] copy = array.clone();
		for (int i = 0; i < array.length; i++) {
			array[i] = copy[apply(i)];
		}
		return array;
	}
	
	/**
	 * An transposition permutation is a permutation that only swap two elements.
	 * By convenience, it is not strictly required that the two elements aren't the same.
	 * @author liyicheng
	 * 2018-03-02 20:47
	 *
	 */
	interface Transposition extends Cycle{
		/**
		 * Gets the index of the first element of the swapping, which has 
		 * a smaller index. 
		 * @return
		 */
		int getFirst();
		/**
		 * Gets the index of the second element of the swapping, which has 
		 * a bigger index. 
		 * @return
		 */
		int getSecond();
		
		/*
		 */
		@Override
		default int[] getElements() {
			int a = getFirst();
			int b = getSecond();
			if(a==b) {
				return new int[] {a};
			}
			return new int[] {a,b};
		}
		
		/*
		 */
		@Override
		default int length() {
			return getFirst() == getSecond() ? 1 : 2;
		}
		
		/*
		 */
		@Override
		default boolean containsElement(int x) {
			return x==getFirst() || x == getSecond();
		}
		
		
		/*
		 */
		@Override
		default int apply(int x) {
			int f = getFirst();
			int s = getSecond();
			if (x == f) {
				return s;
			}
			if (x == s) {
				return f;
			}
			return x;
		}
		
		/*
		 */
		@Override
		default int inverse(int y) {
			//symmetry
			return apply(y);
		}
		
		/*
		 */
		@Override
		default Transposition inverse() {
			return this;
		}
		
		/*
		 * @see cn.timelives.java.math.numberTheory.combination.Permutation#reduce()
		 */
		@Override
		default List<Transposition> decomposeTransposition() {
			return Collections.singletonList(this);
		}
		
		/*
		 * @see cn.timelives.java.math.numberTheory.combination.Permutation#reduceRotate()
		 */
		@Override
		default List<Cycle> decompose() {
			return Collections.singletonList(this);
		}
		
		
		/*
		 * @see cn.timelives.java.math.numberTheory.combination.Permutation#apply(boolean[])
		 */
		@Override
		default boolean[] apply(boolean[] array) {
			ArraySup.swap(array, getFirst(), getSecond());
			return array;
		}
		
		/*
		 * @see cn.timelives.java.math.numberTheory.combination.Permutation#apply(double[])
		 */
		@Override
		default double[] apply(double[] array) {
			ArraySup.swap(array, getFirst(), getSecond());
			return array;
		}
		/*
		 * @see cn.timelives.java.math.numberTheory.combination.Permutation#apply(int[])
		 */
		@Override
		default int[] apply(int[] array) {
			ArraySup.swap(array, getFirst(), getSecond());
			return array;
		}
		/*
		 * @see cn.timelives.java.math.numberTheory.combination.Permutation#apply(long[])
		 */
		@Override
		default long[] apply(long[] array) {
			ArraySup.swap(array, getFirst(), getSecond());
			return array;
		}
		/*
		 * @see cn.timelives.java.math.numberTheory.combination.Permutation#apply(java.lang.Object[])
		 */
		@Override
		default <T> T[] apply(T[] array) {
			ArraySup.swap(array, getFirst(), getSecond());
			return array;
		}
		
		/*
		 * @see cn.timelives.java.math.numberTheory.combination.Permutation#getArray()
		 */
		@Override
		default int[] getArray() {
			int[] arr = ArraySup.indexArray(size());
			return apply(arr);
		}
	}

	/**
	 * A cycle permutation is a permutation that shifts some elements in this permutation by one.
	 * <P>For example, a rotation permutation whose element array is (0,2,4,1) should 
	 * have a permutation array of (2,0,4,3,1,5,6,7), which means the permutation map 0 to 2,
	 * 2 to 4,4 to 1 and 1 to 0.
	 * @author liyicheng
	 * 2018-03-03 15:44
	 *
	 */
	interface Cycle extends Permutation{
		
		/**
		 * Gets an array that contains all the elements that 
		 * should be rotated.
		 * @return
		 */
		int[] getElements();
		
		/**
		 * Determines whether the element should be rotated.
		 * @param x
		 * @return
		 */
		boolean containsElement(int x);
		/**
		 * Gets the number of the elements to rotate, the result should not be
		 * bigger than {@code size()}
		 * @return
		 */
		int length();
		
		/*
		 * @see cn.timelives.java.math.numberTheory.combination.Permutation#rank()
		 */
		@Override
		default int rank() {
			return length();
		}
		
		/*
		 * @see cn.timelives.java.math.numberTheory.combination.Permutation#reduceRotate()
		 */
		@Override
		default List<Cycle> decompose() {
			return Collections.singletonList(this);
		}
		
		/*
		 * @see cn.timelives.java.math.numberTheory.combination.Permutation#apply(int)
		 */
		@Override
		default int apply(int x) {
			if(!containsElement(x)) {
				return x;
			}
			if(length()==1) {
				return x;
			}
			int[] earr = getElements();
			int index = ArraySup.firstIndexOf(x, earr);
			index--;
			if(index<0) {
				index +=earr.length;
			}
			return earr[index];
		}
		
		/*
		 * @see cn.timelives.java.math.numberTheory.combination.Permutation#apply(int[])
		 */
		@Override
		default int[] apply(int[] array) {
			if(length()==1) {
				return array;
			}
			int[] earr = getElements();
			var t = array[earr[0]];
			for(int i=0;i<earr.length-1;i++) {
				array[earr[i]] = array[earr[i+1]];
			}
			array[earr[earr.length-1]] = t;
			return array;
		}
		
		/*
		 * @see cn.timelives.java.math.numberTheory.combination.Permutation#apply(boolean[])
		 */
		@Override
		default boolean[] apply(boolean[] array) {
			if(length()==1) {
				return array;
			}
			int[] earr = getElements();
			boolean t = array[earr[0]];
			for(int i=0;i<earr.length-1;i++) {
				array[earr[i]] = array[earr[i+1]];
			}
			array[earr[earr.length-1]] = t;
			return array;
		}
		
		/*
		 * @see cn.timelives.java.math.numberTheory.combination.Permutation#apply(double[])
		 */
		@Override
		default double[] apply(double[] array) {
			if(length()==1) {
				return array;
			}
			int[] earr = getElements();
			double t = array[earr[0]];
			for(int i=0;i<earr.length-1;i++) {
				array[earr[i]] = array[earr[i+1]];
			}
			array[earr[earr.length-1]] = t;
			return array;
		}
		/*
		 * @see cn.timelives.java.math.numberTheory.combination.Permutation#apply(long[])
		 */
		@Override
		default long[] apply(long[] array) {
			if(length()==1) {
				return array;
			}
			int[] earr = getElements();
			long t = array[earr[0]];
			for(int i=0;i<earr.length-1;i++) {
				array[earr[i]] = array[earr[i+1]];
			}
			array[earr[earr.length-1]] = t;
			return array;
		}
		/*
		 * @see cn.timelives.java.math.numberTheory.combination.Permutation#apply(java.lang.Object[])
		 */
		@Override
		default <T> T[] apply(T[] array) {
			if(length()==1) {
				return array;
			}
			int[] earr = getElements();
			T t = array[earr[0]];
			for(int i=0;i<earr.length-1;i++) {
				array[earr[i]] = array[earr[i+1]];
			}
			array[earr[earr.length-1]] = t;
			return array;
		}
	}
}

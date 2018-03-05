package cn.timelives.java.utilities;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
public class ArraySup {
	/**
     * The maximum size of array to allocate.
     * Some VMs reserve some header words in an array.
     * Attempts to allocate larger arrays may result in
     * OutOfMemoryError: Requested array size exceeds VM limit
     */
	public static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
	
	
	public static void fillArr(int[] arr , int num){
		for(int i=0;i<arr.length;i++){
			arr[i]=num;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T[] fillArr(int length,T t,Class<T> clazz){
		T[] array = (T[]) Array.newInstance(clazz, length);
		for(int i=0;i<array.length;i++){
			array[i] = t;
		}
		return array;
	}
	
	
	
	public static void fillArr(boolean[] arr , boolean b){
		for(int i=0;i<arr.length;i++){
			arr[i]=b;
		}
		
	}
	
	
	public static int[] fillArr(int length , int num){
		int[] arr = new int[length];
		for(int i=0;i<length;i++){
			arr[i]=num;
		}
		return arr;
		
	}
	public static char[] fillArr(int length , char num){
		char[] arr = new char[length];
		for(int i=0;i<length;i++){
			arr[i]=num;
		}
		return arr;
		
	}
	
	public static void ranFillArr(int[] arr){
		Random rd = new Random();
		for(int i=0;i<arr.length;i++){
			arr[i]= rd.nextInt();
		}
	}
	/**
	 * Randomly fill the array by given random
	 * @param arr
	 * @param rd
	 */
	public static void ranFillArr(int[] arr,Random rd){
		for(int i=0;i<arr.length;i++){
			arr[i]= rd.nextInt();
		}
	}
	public static void ranFillArr(int[] arr,int bound){
		Random rd = new Random();
		for(int i=0;i<arr.length;i++){
			arr[i]= rd.nextInt(bound);
		}
	}
	/**
	 * Randomly fill the array by given random
	 * @param arr
	 * @param rd
	 */
	public static void ranFillArr(int[] arr,int bound,Random rd){
		for(int i=0;i<arr.length;i++){
			arr[i]= rd.nextInt(bound);
		}
	}
	
	
	
	public static void ranFillArr(double[] arr){
		Random rd = new Random();
		for(int i=0;i<arr.length;i++){
			arr[i] = rd.nextDouble();
		}
	}
	public static void ranFillArr(double[] arr,double mutilplier){
		Random rd = new Random();
		for(int i=0;i<arr.length;i++){
			arr[i] = rd.nextDouble()*mutilplier;
		}
	}
	/**
	 * fill an array with random double 
	 * @param arr
	 * @param mutilplier
	 * @param negate : 
	 */
	public static void ranFillArrNe(double[] arr,double mutilplier){
		Random rd = new Random();
		for(int i=0;i<arr.length;i++){
			double temp =rd.nextDouble();
			temp = rd.nextBoolean() ? temp : -temp;
			arr[i] = temp*mutilplier;
		}
	}
	/**
	 * Create an random array which length is as given. 
	 * @param length
	 * @return
	 */
	public static int[] ranArr(int length){
		int[] arr = new int[length];
		ranFillArr(arr);
		return arr;
	}
	/**
	 * Create an array which length is as given.The random object will be 
	 * used to create values.
	 * @param length
	 * @return
	 */
	public static int[] ranArr(int length,Random rd){
		int[] arr = new int[length];
		ranFillArr(arr,rd);
		return arr;
	}
	/**
	 * Create an array which length is as given.The values in the array are in 
	 * [0,bound-1].
	 * 
	 * @param length
	 * @param bound
	 * @return
	 */
	public static int[] ranArr(int length,int bound){
//		return new Random().ints(length,0,bound).toArray();
		int[] arr = new int[length];
		ranFillArr(arr,bound);
		return arr;
	}
	/**
	 * Create an array which length is as given.The values in the array are in 
	 * [0,bound-1].The random object will be 
	 * used to create values.
	 * 
	 * @param length
	 * @param bound
	 * @return
	 */
	public static int[] ranArr(int length,int bound,Random rd){
		int[] arr = new int[length];
		ranFillArr(arr,bound,rd);
		return arr;
	}
	/**
	 * Create an array which length is as given.The values in the array are in 
	 * [0,bound-1].Each value are different,so if length > bound , exception will be 
	 * thrown. 
	 * 
	 * @param length
	 * @param bound
	 * @return an random array
	 * @throws IllegalArgumentException if length>bound
	 */
	public static int[] ranArrNoSame(int length,int bound){
		if(length>bound){
			throw new IllegalArgumentException("Length>bound");
		}
		int[] arr = new int[length];
		Random rd = new Random();
		for(int c=0;c<length;c++){
			cal:
			while(true){
				int t = rd.nextInt(bound);
				//check for the same
				for(int i=0;i<c;i++){
					if(arr[i]==t)
						continue cal;
				}
				arr[c] = t;
				break;
			}
			
		}
		return arr;
	}
	/**
	 * Create an array which length is as given.The values in the array are in 
	 * [0,bound-1].Each value are different,so if length > bound , exception will be 
	 * thrown. The random object will be 
	 * used to create values.
	 * 
	 * @param length
	 * @param bound
	 * @return an random array
	 * @throws IllegalArgumentException if length>bound
	 */
	public static int[] ranArrNoSame(int length,int bound,Random rd){
		if(length>bound){
			throw new IllegalArgumentException("Length>bound");
		}
		int[] arr = new int[length];
		for(int c=0;c<length;c++){
			cal:
			while(true){
				int t = rd.nextInt(bound);
				//check for the same
				for(int i=0;i<c;i++){
					if(arr[i]==t)
						continue cal;
				}
				arr[c] = t;
				break;
			}
			
		}
		return arr;
	}
	/**
	 * the ranDoubleArr is an array filled with random double number from [0,1)
	 * @param length
	 * @return
	 */
	public static double[] ranDoubleArr(int length){
		double[] arr = new double[length];
		ranFillArr(arr);
		return arr;
	}
	/**
	 * the ranDoubleArr is an array filled with random double number from [0,mutilplier)
	 * @param length
	 * @param mutilplier
	 * @return
	 */
	public static double[] ranDoubleArr(int length,double mutilplier){
		double[] arr = new double[length];
		ranFillArr(arr,mutilplier);
		return arr;
	}
	/**
	 * return a double array with negate value
	 * @param length
	 * @param mutilplier
	 * @return
	 */
	public static double[] ranDoubleArrNe(int length,double mutilplier){
		double[] arr = new double[length];
		ranFillArrNe(arr,mutilplier);
		return arr;
	}
	
	/**
	 * calculate the sum of the array from start (inclusive) to end (exclusive)
	 * @param arr
	 * @param start
	 * @param end
	 * @return
	 */
	public static int getSum(int[] arr , int start , int end){
		int sum = 0;
		for(int i=start ;i< end ;i++){
			sum+=arr[i];
		}
		return sum;
	}
	public static int getSum(int[] arr){
		return getSum(arr,0,arr.length);
	}
	public static double getSum(double[] arr , int start , int end){
		double sum = 0;
		for(int i=start ;i< end ;i++){
			sum+=arr[i];
		}
		return sum;
	}
	public static double getSum(double[] arr){
		return getSum(arr,0,arr.length);
	}
	
	public static int findMaxPos(int[] arr){
		int maxPos = 0;
		for(int i=1; i < arr.length;i++){
			maxPos = (arr[i]>arr[maxPos])? i : maxPos;
		}
		return maxPos;
	}
	public static int findMax(int[] arr){
		int max = arr[0];
		for(int i=1;i< arr.length ; ++i){
			max = Math.max(arr[i], max);
		}
		return max ;
	}
	
	
	
	/**
	 * make the array random
	 * @param arr
	 */
	public static void desort(int[] arr){
		Random rd = new Random();
		int len= arr.length;
		for(int i=0;i<len-1;i++){
			int npos = rd.nextInt(len-i) + i;
			int t = arr[npos];
			arr[npos] = arr[i];
			arr[i] = t;
		}
	}
	/**
	 * make the array random
	 * @param arr
	 */
	public static void desort(Object[] arr){
		Random rd = new Random();
		int len= arr.length;
		for(int i=0;i<len-1;i++){
			int npos = rd.nextInt(len-i) + i;
			Object t = arr[npos];
			arr[npos] = arr[i];
			arr[i] = t;
		}
		
	}
	
	/**
	 * An array whose element is its index.
	 * @param length
	 * @return
	 */
	public static int[] indexArray(int length){
		int[] a = new int[length];
		for(int i=0;i<length;i++){
			a[i] = i;
		}
		return a;
	}
	
	
	public static int[][] turnMatrix(int[][] mat){
		int width = -1;
		for(int[] arr: mat){
			width = Math.max(arr.length, width);
		}
		int[][] re = new int[width][mat.length];
		for(int i=0;i<mat.length;i++){
			for(int j=0;j<mat[i].length;j++){
				re[j][i]=mat[i][j];
			}
		}
		return re;
	}
	/**
	 * Flip an array.
	 * @param arr the array
	 * @return a flipped array
	 */
	public static <T> T[] filp(T[] arr){
		int len = arr.length;
		@SuppressWarnings("unchecked")
		T[] re = (T[]) Array.newInstance(arr.getClass().getComponentType(), len);
		for(int i=0;i<len;i++){
			re[len-i-1] = arr[i]; 
		}
		return re;
	}
	
	/**
	 * Reverse the array.For example,input {@code reverse("123456789",3)}
	 * returns a result {@code "456789123"}. This method will only use constant storage.
	 * @param arr
	 * @param len
	 * @return
	 */
	public static <T> void reverse(T[] arr,int len){
		if(len>arr.length){
			throw new IllegalArgumentException();
		}
		reverse0(arr,len);
	}
	
	/**
	 * Reverse the array. For example,input {@code reverse(123456789,3)}
	 * returns a result {@code 456789123}. This method will only use constant storage.
	 * @param arr
	 * @param len
	 */
	public static void reverse(int[] arr,int len) {
		if(len>arr.length){
			throw new IllegalArgumentException();
		}
		reverse0(arr,len);
	}
	
	/**
	 * Flip the array in the given range. For example, {@code flip(ABCDEFG,2,6)}
	 *  returns {@code ABFEDCG}.
	 * @param arr the array
	 * @param from index,inclusive
	 * @param to exclusive
	 */
	public static <T> void flip(T[] arr,int from,int to){
		if(to<=from){
			throw new IllegalArgumentException();
		}
		if(to==from+1){
			return;
		}
		T t;
		int top = from+to-1;
		int mid = (to+from) /2;
		for(int i=to;i<mid;i++){
			int j = top - i;
			t = arr[i];
			arr[i] = arr[j];
			arr[j] = t;
		}
	}
	/**
	 * Flip the array in the given range. For example, {@code flip(1234567,2,6)}
	 *  returns {@code 1265437}.
	 * @param arr the array
	 * @param from index,inclusive
	 * @param to exclusive
	 */
	public static int[] flip(int[] arr,int from,int to){
		if(to<=from){
			throw new IllegalArgumentException();
		}
		if(to==from+1){
			return arr;
		}
		int t;
		int top = from+to-1;
		int mid = (to+from) /2;
		for(int i=from;i<mid;i++){
			int j = top - i;
			t = arr[i];
			arr[i] = arr[j];
			arr[j] = t;
		}
		return arr;
	}
	private static void reverse0(Object[] arr,int flipLen){
		int length = arr.length;
		int start = 0;
		int exchangeSize ;
		boolean bigger;
		Object t;
		while(flipLen>0){
			int re = length - flipLen;
			int place ;
			
			if( flipLen<re){
				bigger = false;
				exchangeSize = flipLen;
				place = re;
			}else{
				bigger = true;
				exchangeSize = re;
				place = flipLen; 
			}
			for(int i=start;i<start+exchangeSize;i++){
				t = arr[place+i];
				arr[place+i] = arr[i];
				arr[i] = t;
			}
			length -= exchangeSize;
			if(bigger){
				start += exchangeSize;
				flipLen = place-exchangeSize;
			}else{
				flipLen = exchangeSize;
			}
			
		}
	}
	
	private static void reverse0(int[] arr,int flipLen){
		int length = arr.length;
		int start = 0;
		int exchangeSize ;
		boolean bigger;
		int t;
		while(flipLen>0){
			int re = length - flipLen;
			int place ;
			
			if( flipLen<re){
				bigger = false;
				exchangeSize = flipLen;
				place = re;
			}else{
				bigger = true;
				exchangeSize = re;
				place = flipLen; 
			}
			for(int i=start;i<start+exchangeSize;i++){
				t = arr[place+i];
				arr[place+i] = arr[i];
				arr[i] = t;
			}
			length -= exchangeSize;
			if(bigger){
				start += exchangeSize;
				flipLen = place-exchangeSize;
			}else{
				flipLen = exchangeSize;
			}
			
		}
	}
	
	/**
	 * Determines whether the two array is equal, this method will ignore the order of 
	 * specific element. The length of the two array should be the same.
	 * @param a1
	 * @param a2
	 * @param testEqual a function that test whether two given object is equal, return true if equal
	 * @return
	 */
	public static <T> boolean arrayEqualNoOrder(T[] a1,T[] a2,BiFunction<T, T, Boolean> testEqual){
		final int length = a1.length;
		if(a2.length != length){
			return false;
		}
		boolean[] mapped = new boolean[length];
		for(int i=0;i<length;i++){
			T t = a1[i];
			boolean suc = false;
			for(int j=0;j<length;j++){
				if(mapped[j])
					continue;
				if(testEqual.apply(t, a2[j])){
					mapped[j] = true;
					suc = true;
					break;
				}
			}
			if(!suc){
				return false;
			}
		}
		return true;
	}
	/**
	 * Determines whether the two array is equal, this method will ignore the order of 
	 * specific element. The length of the two array should be the same.(Two elements <tt>e1</tt> and
     * <tt>e2</tt> are <i>equal</i> if <tt>(e1==null ? e2==null :
     * e1.equals(e2))</tt>.)
	 * @param a1
	 * @param a2
	 * @return
	 */
	public static <T> boolean arrayEqualNoOrder(T[] a1,T[] a2){
		final int length = a1.length;
		if(a2.length != length){
			return false;
		}
		boolean[] mapped = new boolean[length];
		for(int i=0;i<length;i++){
			T t = a1[i];
			boolean suc = false;
			for(int j=0;j<length;j++){
				if(mapped[j])
					continue;
				T t2 = a2[j];
				if(t==null ? t2==null : t.equals(t2)){
					mapped[j] = true;
					suc = true;
					break;
				}
			}
			if(!suc){
				return false;
			}
		}
		return true;
	}
	
	
	/**
	 * Determines whether the array contains the specific object.
	 * @param arr
	 * @param element
	 * @param testEqual a function to determines whether the two objects are the same.
	 * @return 
	 */
	public static <T,S> boolean arrayContains(T[] arr,S element,BiFunction<T, S, Boolean> testEqual){
		for(int i=0;i<arr.length;i++){
			if(testEqual.apply(arr[i], element)){
				return true;
			}
		}
		return false;
	}
	/**
	 * Return an array of the mapped elements, the actual returned type is an array of object.
	 * @param arr
	 * @param mapper
	 * @return
	 */
	public static <N,T> N[] mapTo(T[] arr,Function<T,N> mapper){
		@SuppressWarnings("unchecked")
		N[] re = (N[]) new Object[arr.length];
		for(int i=0;i<arr.length;i++){
			re[i] = mapper.apply(arr[i]);
		}
		return re;
	}
	/**
	 * Return an array of the mapped elements, creates a new array.
	 * @param arr
	 * @param mapper
	 * @return
	 */
	public static <N,T> N[] mapTo(T[] arr,Function<T,N> mapper,Class<N> clazz){
		@SuppressWarnings("unchecked")
		N[] re = (N[]) Array.newInstance(clazz, arr.length);
		for(int i=0;i<arr.length;i++){
			re[i] = mapper.apply(arr[i]);
		}
		return re;
	}
	/**
	 * Return an array of the mapped elements, creates a new array.
	 * @param arr
	 * @param mapper
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <N,T> N[][] mapTo2(T[][] arr,Function<T,N> mapper,Class<N> clazz){
		Class<?> narrayType = Array.newInstance(clazz, 0).getClass();
		N[][] re = (N[][]) Array.newInstance(narrayType, arr.length);
		for(int i=0;i<arr.length;i++){
			re[i] =(N[])Array.newInstance(clazz,arr[i].length);
			for(int j=0;j<re[i].length;j++) {
				re[i][j] = mapper.apply(arr[i][j]);
			}
		}
		return re;
	}
	
	/**
	 * Creates a set from the array, uses HashSet by default.
	 * @param arr
	 * @return
	 */
	public static <T> Set<T> createSet(T[] arr){
		Set<T> set = new HashSet<>(arr.length);
		for(int i=0;i<arr.length;i++){
			set.add(arr[i]);
		}
		return set;
	}
	/**
	 * Creates a set from the array, uses the supplier to create a new set.
	 * @param arr
	 * @param sup
	 * @return
	 */
	public static <T> Set<T> createSet(T[] arr,Supplier<Set<T>> sup){
		Set<T> set = sup.get();
		for(int i=0;i<arr.length;i++){
			set.add(arr[i]);
		}
		return set;
	}
	/**
	 * Sort the {@code null} values to the back of the array, returns the number of non-null 
	 * objects in the array. The order of the original non-null objects will not be effected 
	 * but null values between them will be removed.
	 * @param objs an array to sort
	 * @return the number of non-null objects.
	 */
	@SuppressWarnings("unchecked")
	public static <T> int sortNull(T[] objs){
		Object[] temp = new Object[objs.length];
		int n = 0;
		for(T t : objs){
			if(t !=null){
				temp[n++] = t;
			}
		}
		for(int i=0;i<objs.length;i++){
			objs[i] = (T) temp[i];
		}
		return n;
	}
	
	
	/**
	 * Test that this array contains no {@code null} element.
	 * @param arr
	 * @return
	 */
	public static <T> T[] notEmpty(T[] arr){
		for(int i=0;i<arr.length;i++){
			if(arr[i] == null){
				throw new NullPointerException();
			}
		}
		return arr;
	}
	/**
	 * Copies the given array.
	 * @param arr
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] deepCopy(T[] arr){
		if(arr.length == 0){
			return arr.clone();
		}
		return (T[]) deepCopy0(arr);
	}

	static Object[] deepCopy0(Object[] arr){
		Object[] result = (Object[]) Array.newInstance(arr.getClass().getComponentType(), arr.length);
		for (int i = 0; i < arr.length; i++) {
			Object element = arr[i];
			if (element instanceof Object[])
				result[i] = deepCopy0((Object[]) arr[i]);
			else if (element instanceof byte[]) {
				byte[] t = (byte[]) arr[i];
				result[i] = t.clone();
			} else if (element instanceof short[]) {
				short[] t = (short[]) arr[i];
				result[i] = t.clone();
			} else if (element instanceof int[]) {
				int[] t = (int[]) arr[i];
				result[i] = t.clone();
			} else if (element instanceof long[]) {
				long[] t = (long[]) arr[i];
				result[i] = t.clone();
			} else if (element instanceof char[]) {
				char[] t = (char[]) arr[i];
				result[i] = t.clone();
			} else if (element instanceof float[]) {
				float[] t = (float[]) arr[i];
				result[i] = t.clone();
			} else if (element instanceof double[]) {
				double[] t = (double[]) arr[i];
				result[i] = t.clone();
			} else if (element instanceof boolean[]) {
				boolean[] t = (boolean[]) arr[i];
				result[i] = t.clone();
			} else {
				result[i] = arr[i];
			}
		}
		return result;
	}
	
	/**
	 * Modifies all the non-null elements in the array and replace the original elements.
	 * @param arr
	 * @return
	 */
	public static <T> T[] modifyAll(T[] arr,Function<? super T,? extends T> f){
		for(int i=0;i<arr.length;i++){
			T t = arr[i];
			if(t!=null){
				arr[i] = f.apply(t);
			}
		}
		return arr;
			
	}
	
	/**
	 * Set the given index to {@code x}, lengthen the array by 1.5x when needed.
	 * @param arr
	 * @param x
	 * @param index
	 * @return
	 */
	public static long[] ensureCapacityAndAdd(long[] arr,long x,int index) {
		if(arr.length<= index) {
			arr = Arrays.copyOf(arr, Math.max(arr.length*3/2, index+1));
		}
		arr[index] = x;
		return arr;
	}
	/**
	 * Set the given index to {@code x}, lengthen the array by 1.5x when needed.
	 * @param arr
	 * @param x
	 * @param index
	 * @return
	 */
	public static <T> T[] ensureCapacityAndAdd(T[] arr,T x,int index) {
		if(arr.length<= index) {
			arr = Arrays.copyOf(arr, Math.max(arr.length*3/2, index+1));
		}
		arr[index] = x;
		return arr;
	}
	/**
	 * Cast the number {@code n} to an integer as the length of an array, checking 
	 * whether it exceeds. Throws an exception if {@code n<0 || n> MAX_ARRAY_SIZE}
	 * @param n
	 * @return
	 */
	public static int castToArrayLength(long n) {
		if(n <0 || n>MAX_ARRAY_SIZE) {
			throw new IllegalArgumentException("Size exceeds: "+n);
		}
		return (int)n;
	}
	
	/**
	 * Applies the permutation to the array.
	 * @param arr
	 * @param parr
	 * @return
	 */
	public static <T> T[] applyPermutation(T[] arr,int[] parr) {
		T[] copy = Arrays.copyOf(arr, arr.length);
		for(int i=0;i<arr.length;i++) {
			arr[parr[i]] = copy[i]; 
		}
		return arr;
	}
	
	public static void swap(Object[] arr,int i,int j) {
		Object t = arr[i];
		arr[i] = arr[j];
		arr[j] = t;
	}
	public static void swap(int[] arr,int i,int j) {
		int t = arr[i];
		arr[i] = arr[j];
		arr[j] = t;
	}
	public static void swap(boolean[] arr,int i,int j) {
		boolean t = arr[i];
		arr[i] = arr[j];
		arr[j] = t;
	}
	public static void swap(long[] arr,int i,int j) {
		long t = arr[i];
		arr[i] = arr[j];
		arr[j] = t;
	}
	public static void swap(double[] arr,int i,int j) {
		double t = arr[i];
		arr[i] = arr[j];
		arr[j] = t;
	}
	public static void swap(float[] arr,int i,int j) {
		float t = arr[i];
		arr[i] = arr[j];
		arr[j] = t;
	}
	
	
	public static int firstIndexOf(int x,int[] arr) {
		for(int i=0;i<arr.length;i++) {
			if(arr[i]==x) {
				return i;
			}
		}
		return -1;
	}
	
//	public static void main(String[] args) {
//		Integer[] arr = new Integer[10]; 
//		Arrays.setAll(arr, i -> i);  
//		reverse(arr,0);
//		print(arr);
//		print(flip(indexArray(10),1,8));
//	}
}	

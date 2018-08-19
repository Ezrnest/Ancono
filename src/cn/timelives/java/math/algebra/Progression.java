package cn.timelives.java.math.algebra;

import cn.timelives.java.math.MathObject;
import cn.timelives.java.math.function.BiMathFunction;
import cn.timelives.java.math.MathCalculator;
import cn.timelives.java.math.numberModels.api.FlexibleNumberFormatter;
import cn.timelives.java.utilities.ArraySup;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;
import java.util.function.LongFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * progression is a series of number that is ordered.The progression is a mostly like the progression described in 
 * math,but unlike the progression in math, the starting element's index is zero, just as what we do in arrays.
 * <p>The progression can either be limited or unlimited.Because of such a feature, the {@code equals()} method can 
 * make sure  whether the two progression is actually equal, so sometimes the {@code equals()} method will not return 
 * {@code true} even if the two progression is actually the identity.But this method is required return {@code false} if the
 * progression if the two progression is not the identity.
 * @author lyc
 * @param <T> the type of number returned as the number in the progression
 */
public abstract class Progression<T> extends MathObject<T> implements Iterable<T>{
	/**
	 * The length of this progression, set it as UNLIMITED to indicate this progression is 
	 * unlimited.
	 * @see #UNLIMITED
	 */
	protected final long length;
	/**
	 * An long value returned by {@link #getLength()} indicating that the progression is unlimited.
	 */
	public static final long UNLIMITED = -1;
	
	
	protected Progression(MathCalculator<T> mc,long length) {
		super(mc);
		this.length = length;
	}
	/**
	 * Return the number in the {@code index} of this progression.  
	 * @param index the index of the number, starting from 0
	 * @return a number in this progression.
	 */
	public abstract T get(long index);
	/**
	 * Return {@code true} only if this progression is limited , which means this progression can be iterated completely.
	 * @return {@code true} if this progression is limited.
	 */
	public boolean isLimited(){
		return length != UNLIMITED;
	}
	/**
	 * Return the length of this progression.Return {@code UNLIMITED} if this progression is an unlimited progression. 
	 * @return the length of this progression , or {@code UNLIMITED} if this progression is an unlimited progression. 
	 */
	public long getLength(){
		return length;
	}
	
	/**
	 * Convert this progression to an array,if this progression is unlimited,then {@code null} should be returned.
     * The index of elements in the returning array should be the identity to the index in the progression.
	 * @return an array, or {@code null } if the progression is unlimited.
	 * @throws IllegalArgumentException if this progression's size is bigger than the max length of an array,
	 * 
	 */
	public Object[] toArray(){
		if(length == UNLIMITED){
			return null;
		}
		if(length > ArraySup.MAX_ARRAY_SIZE){
			throw new IllegalArgumentException("Progression size exceeds max array size");
		}
		int len = (int) length;
		
		Object[] arr = new Object[len];
		for(int i=0;i<len;i++){
			arr[i] = get(i);
		}
		return arr;
	}
	
	
	/**
	 * Return the number by order of the progression.
	 */
	@Override
	public Iterator<T> iterator(){
		return new IndexIterator<T>(0,this);
	}
	/**
	 * Return the numbers in this progression,ordered.The first number returned 
	 * will be the number in index {@code fromIndex}.If {@code fromIndex > getLength()},
	 * the the iterator will have no element to return.
	 * @param fromIndex index of the first number
	 * @return
	 */
	public Iterator<T> iteratorFrom(long fromIndex){
		return new IndexIterator<T>(fromIndex,this);
	}
	
	/**
	 * Return the numbers in this progression,ordered.The first number returned 
	 * will be the number in index {@code fromIndex}.This iterator requires 
	 * {@code fromIndex < endIndex <= length},if {@code endIndex <= fromIndex} then this 
	 * iterator will not return any element,and if {@code fromIndex} or {@code endIndex} is out  of bound,
	 * then the index will be adjust to bound.In these circumstances, no exception will be thrown.
	 * @param fromIndex index of the first number
	 * @param endIndex the bound , exclusive
	 * @return an iterator
	 */
	public Iterator<T> iteratorRange(long fromIndex,long endIndex){
		return new IndexIterator<T>(fromIndex,endIndex,this);
	}
	/**
	 * Get the sum of this progression from the index {@code start}(inclusive) to index {@code end}(exclusive).
	 * If {@code end <= start},then the number of index {@code start} will be returned.
	 * @param start inclusive
	 * @param end exclusive
	 * @return the sum of the progression
	 * @throws IllegalArgumentException if {@code start < 0} or {@code end} is bigger than the progression's length.
	 */
	public T sumOf(long start,long end){
		T sum = get(start);
		while(++start<end){
            sum = getMc().add(sum, get(start));
		}
		return sum;
	}
	
	
	protected boolean inRange(long index){
		return index > -1 &&(length==UNLIMITED || index < length);
	}
	
	
	
	
	
	static abstract class AbstractIndexIterator<T> implements Iterator<T>{
		
		protected long index;
		protected final long bound;
		
		AbstractIndexIterator(long proLength){
			index = 0;
			bound = proLength;
		}
		
		
		AbstractIndexIterator(long startFrom,long proLength){
			index = startFrom > -1 ? startFrom : 0;
			bound = proLength;
		}
		
		AbstractIndexIterator(long startFrom,long endWith,long proLength){
			index = startFrom > -1 ? startFrom : 0;
			bound = endWith < 0 ? 0 : 
				proLength == UNLIMITED ?  endWith : 
					endWith <= proLength ? endWith : proLength;
		}
		
		@Override
		public boolean hasNext() {
			return bound == UNLIMITED || index < bound ;
		}

//		@Override
//		public T next() {
//			if(hasNext()){
//				return pro.get(index++);
//			}
//			throw new NoSuchElementException();
//		}
		
	}
	
	
	static class IndexIterator<T> extends AbstractIndexIterator<T>{
		
		private final Progression<T> pro;
		
		IndexIterator(long startFrom, Progression<T> pro) {
			super(startFrom, pro.length);
			this.pro = pro;
		}
		
		IndexIterator(long startFrom, long endWith,Progression<T> pro) {
			super(startFrom, endWith,pro.length);
			this.pro = pro;
		}

		@Override
		public T next() {
			if(hasNext()){
				return pro.get(index++);
			}
			throw new NoSuchElementException();
		}
		
	}
	
	/**
	 * Return {@code true} only if {@code this} is actually equal to the progression( if the given 
	 * object is a progression).This method is allowed to return false even if {@code this} is actually 
	 * equal to {@code obj}, but must return {@code true} if {@code this==obj} and return {@code false} 
	 * if {@code this} is not actually equal to {@code obj}.<P>
	 */
	@Override
	public boolean equals(Object obj){
		if(obj instanceof Progression){
			Progression<?> pro = (Progression<?>) obj;
            if (pro.getMc() != this.getMc()) {
				return false;
			}
            // the identity progression type.
			long len = this.getLength();
			if (len > 0 && len == pro.getLength()) {
				for (long l = 0; l < len; l++) {
					if (!get(l).equals( pro.get(l))) {
						return false;
					}
				}
				return true;
			}
			
			return false;
		}
		return false;
	}
	/**
	 * We just choose the first two elements in this progression to compute the hash code.
	 */
	@Override
	public int hashCode() {
        int hash = getMc().hashCode();
		hash = hash*37 + get(0).hashCode();
		hash = hash*37 + get(1).hashCode();
		return hash;
	}
	
	
	/**
	 * Returns a progression that has the length of {@code limit}.
	 * @return a progression
	 * @throws IllegalArgumentException if the progression is limited and its length is smaller than {@code limit} 
	 */
	public Progression<T> limit(long limit){
		if(limit <= 0 ){
			throw new IllegalArgumentException("limit <= 0");
		}
		if(inRange(limit)){
			if(limit < ArraySup.MAX_ARRAY_SIZE){
				int size = (int)limit;
				//copy all the data to an array
				@SuppressWarnings("unchecked")
				T[] arr = (T[]) new Object[size];
				Iterator<T> it = this.iterator();
				int p = 0;
				while(p < size){
					arr[p++] = it.next();
				}
                return new ArrayProgression<>(getMc(), arr);
			}
		}
		throw new UnsupportedOperationException("Method failed!");
	}
	
	
	/**
	 * Return a stream of this progression.
	 * @return a stream
	 */
	public Stream<T> stream(){
		long size = getLength();
		if(size>0){
			//limited progression
			Spliterator<T> spl = Spliterators.spliterator(iterator(), size, Spliterator.IMMUTABLE);
	        return StreamSupport.stream(spl, false);
			
			
		}else{
			return Stream.generate(new Supplier<T>(){
				private long index = 0;
				@Override
				public T get() {
					return Progression.this.get(index++);
				}
			});
		}
	}
	
	
	@Override
	public <N> Progression<N> mapTo(
            @NotNull Function<T, N> mapper, @NotNull MathCalculator<N> newCalculator) {
		return new MappedProgression<>(newCalculator,mapper,this);
	}
	
	@Override
    public <N> boolean valueEquals(@NotNull MathObject<N> obj, @NotNull Function<N, T> mapper) {
		if(obj instanceof Progression){
			Progression<N> pro = (Progression<N>) obj;
			if(this.isLimited()&& this.getLength() == pro.getLength()){
				long i = this.getLength();
				Iterator<T> it1 = this.iterator();
				Iterator<N> it2 = pro.iterator();
				while(i-- > 0){
                    if (getMc().isEqual(it1.next(), mapper.apply(it2.next())) == false) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}
	@Override
    public boolean valueEquals(@NotNull MathObject<T> obj) {
		if(obj instanceof Progression){
			Progression<T> pro = (Progression<T>) obj;
			if(this.isLimited()&& length == pro.length){
				long i = length;
				Iterator<T> it1 = this.iterator();
				Iterator<T> it2 = pro.iterator();
				while(i-- > 0){
                    if (getMc().isEqual(it1.next(), it2.next()) == false) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.FlexibleMathObject#toString(cn.timelives.java.math.number_models.NumberFormatter)
	 */
	@Override
    public String toString(@NotNull FlexibleNumberFormatter<T, MathCalculator<T>> nf) {
		return "Progression:"+this.getClass().getName();
	}
	
	/**
	 * 
	 * @author lyc
	 *
	 * @param <T> original type
	 * @param <N> type mapped to
	 */
	static class MappedProgression<T,N> extends Progression<N>{
		
		private final Function<T,N> mapper;
		
		private final Progression<T> proSource;
		
		MappedProgression(MathCalculator<N> mc,Function<T,N> mapper,Progression<T> proSource) {
			super(mc,proSource.length);
			this.mapper = mapper;
			
			this.proSource = proSource;
		}
		
		MappedProgression(MathCalculator<N> mc,Function<T,N> mapper,Progression<T> proSource,long length) {
			super(mc,length);
			this.mapper = mapper;
			
			this.proSource = proSource;
		}
		
		<T1> MappedProgression(MathCalculator<N> mc,Function<T1,N> mapper,MappedProgression<T,T1> proSource) {
			super(mc,proSource.length);
			this.mapper = proSource.mapper.andThen(mapper);
			this.proSource = proSource.proSource;
		}

		@Override
		public N get(long index) {
			return mapper.apply(proSource.get(index));
		}


		@SuppressWarnings("unchecked")
		@Override
		public Object[] toArray() {
			Object[] arrS = proSource.toArray();
			Object[] arr =new Object[arrS.length];
			for(int i=0;i<arrS.length;i++){
				arr[i] = mapper.apply((T)arrS[i]);
			}
			return arr;
		}

		@Override
		public Iterator<N> iterator() {
			return new Iterator<N>() {
				private Iterator<T> itS = proSource.iterator();
				@Override
				public boolean hasNext() {
					return itS.hasNext();
				}
				@Override
				public N next() {
					return mapper.apply(itS.next());
				}
			};
		}

		@Override
        public <N1> Progression<N1> mapTo(@NotNull Function<N, N1> mapper, @NotNull MathCalculator<N1> newCalculator) {
			return new MappedProgression<T,N1>(newCalculator, this.mapper.andThen(mapper), proSource);
		}
		
		@Override
		public Progression<N> limit(long limit) {
			if(limit <= 0 || (length != UNLIMITED && limit>length)){
				throw new IllegalArgumentException();
			}
            return new MappedProgression<>(getMc(), mapper, proSource.limit(limit), limit);
		}
		
	}
	/**
	 * 
	 * @author lyc
	 *
	 * @param <T> the type of original progressions
	 * @param <R> the type of the new result
	 */
	static class CombinedProgression<T,R> extends Progression<R>{
		private final Function<T[],R> func;
		private final Progression<T>[] ps;
		
		private final int pNum ;
		/**
		 * The class of the generic type array.
		 */
		private final Class<?> clazz;
		
		/**
		 * No check will be done in this method
		 * @param mc
		 * @param func
		 * @param ps
		 */
		protected CombinedProgression(MathCalculator<R> mc,Function<T[],R> func,Progression<T>[] ps) {
			super(mc,ps[0].length);
			this.func = func;
			this.ps = ps;
			pNum = ps.length;
			clazz = ps[0].get(0).getClass();
		}
		/**
		 * No check will be done in this method
		 * @param mc
		 * @param func
		 * @param ps
		 */
		protected CombinedProgression(MathCalculator<R> mc,Function<T[],R> func,Progression<T>[] ps,long length) {
			super(mc,length);
			this.func = func;
			this.ps = ps;
			pNum = ps.length;
			clazz = ps[0].get(0).getClass();
		}

		@SuppressWarnings("unchecked")
		@Override
		public R get(long index) {
			if(inRange(index)){
				T[] ts = (T[]) Array.newInstance(clazz,pNum);
				for(int i=0;i<pNum;i++){
					ts[i] = ps[i].get(index);
				}
				return func.apply(ts);
			}
			throw new IndexOutOfBoundsException("for index:"+index);
			
		}

		@Override
		public Iterator<R> iterator() {
			return new IndexIterator<R>(0,this);
		}
		
		@Override
		public Progression<R> limit(long limit) {
			if(length != UNLIMITED && limit>length){
				throw new IllegalArgumentException();
			}
            return new CombinedProgression<>(getMc(), func, ps, limit);
		}
		
		@Override
        public <N> Progression<N> mapTo(@NotNull Function<R, N> mapper, @NotNull MathCalculator<N> newCalculator) {
			return new CombinedProgression<>(newCalculator,func.andThen(mapper),ps,length);
		}
		
	}
	
	
	
	
	
	
	
	static class ArrayProgression<T> extends Progression<T>{
		private final T[] arr;
		//the length of this kind of array is bigger than 0.
		protected ArrayProgression(MathCalculator<T> mc,T[] arr) {
			super(mc,arr.length);
			this.arr = arr;
		}

		@Override
		public T get(long index) {
			if(inRange(index)){
				return arr[(int)index];
			}
			throw new IndexOutOfBoundsException("for index:"+index);
		}
		
		@Override
		protected boolean inRange(long index) {
			return index > -1 && index < length;
		}

		@Override
		public boolean isLimited() {
			return true;
		}


		@Override
		public T[] toArray() {
			return arr.clone();
		}

		@Override
		public Iterator<T> iterator() {
			return new Iterator<T>() {
				private int index = 0;
				@Override
				public boolean hasNext() {
					return index < length;
				}

				@Override
				public T next() {
					if(index < length){
						return arr[index++];
					}
					throw new NoSuchElementException();
				}
			};
		}
		
		@Override
		public Progression<T> limit(long limit) {
			if(limit <= 0 || (length != UNLIMITED && limit>length)){
				throw new IllegalArgumentException();
			}
			T[] arrN = Arrays.copyOf(arr, (int)limit);
            return new ArrayProgression<>(getMc(), arrN);
			
		}
		
		@Override
		public T sumOf(long start, long end) {
			if(start < 0 || end > length){
				throw new IllegalArgumentException();
			}
			int s = (int) start;
			int e = (int) end;
			T sum = arr[s];
			while(++s < e){
                sum = getMc().add(sum, arr[s]);
			}
			return sum;
		}
		
		@Override
        public <N> Progression<N> mapTo(@NotNull Function<T, N> mapper, @NotNull MathCalculator<N> newCalculator) {
			@SuppressWarnings("unchecked")
			N[] newArr = (N[]) new Object[arr.length];
			for(int i=0;i<arr.length;i++){
				newArr[i] = mapper.apply(arr[i]);
			}
			return new ArrayProgression<>(newCalculator, newArr);
		}
		
		
		
	}
	
	static class CachedProgression<T> extends Progression<T>{
		/*
		 * startIndex : inclusive
		 * endIndex : exclusive
		 */
		private final long startIndex,endIndex;
		
		private final T[] cache;
		
		private final Progression<T> pro;
		
		private boolean allCached = false;
		/**
		 * 
		 * @param pro
		 * @param startIndex
		 * @param endIndex
		 */
		@SuppressWarnings("unchecked")
		protected CachedProgression(Progression<T> pro,long startIndex,long endIndex,long length) {
            super(pro.getMc(), length);
			if(pro instanceof ArrayProgression){
				//no need to allocate a new array 
				ArrayProgression<T> ap =(ArrayProgression<T>) pro;
				this.startIndex = 0L;
				this.endIndex = length;
				this.cache = ap.arr;
				this.pro = pro;
				allCached = true;
				return;
			}else if(pro instanceof CachedProgression){
				CachedProgression<T> cp = (CachedProgression<T>) pro;
				this.pro = cp.pro;
				if(cp.startIndex <= startIndex && cp.endIndex >= endIndex){
					this.startIndex = cp.startIndex;
					this.endIndex = cp.endIndex;
					this.cache = cp.cache;
					allCached = true;
					return;
				}else{
					//an array should be created.
					this.startIndex = startIndex;
					this.endIndex = endIndex;
					cache = (T[]) new Object[(int)(endIndex - startIndex)];
					if(cp.startIndex < endIndex){
						System.arraycopy(cp.cache, 0, 
								cache, (int)(cp.startIndex - startIndex), 
								(int)Math.min(endIndex - cp.startIndex, cp.endIndex - cp.startIndex));
					}
				
				}
				return;
			}else{
				this.pro = pro;
				this.startIndex = startIndex;
				this.endIndex = endIndex;
				cache = (T[]) new Object[(int)(endIndex - startIndex)];
			}
		}
		
		protected CachedProgression(Progression<T> pro,long startIndex,long endIndex) {
			this(pro,startIndex,endIndex,pro.length);
		}
		
		private CachedProgression(Progression<T> pro,T[] cache,long startIndex,long endIndex) {
            super(pro.getMc(), pro.length);
			this.pro = pro;
			this.cache = cache;
			this.startIndex = startIndex;
			this.endIndex = endIndex;
			allCached = true;
		}
		
		
		@Override
		public T get(long index) {
			if(index < endIndex && index >= startIndex){
				int x = (int)(index-startIndex);
				T t = cache[x];
				if(t==null){
					t = pro.get(index);
					cache[x]= t;
					return t;
				}else{
					return t;
				}
			}
			return pro.get(index);
		}
		
		
		
		@Override
		public Progression<T> limit(long limit) {
			if(length != Progression.UNLIMITED && limit > length){
				throw new IllegalArgumentException();
			}
			if(limit > startIndex){
				//return another cached progression
				return new CachedProgression<T>(this, startIndex, endIndex,limit);
			}
			return pro.limit(limit);
			
			
		}
		/**
		 * This method will make the progression to cache all the numbers. If some 
		 * numbers have been computed,they will be computed again,but if all the numbers 
		 * have been cache when constructing this progression,nothing will happen.
		 */
		public void cacheAll(){
			if(!allCached){
				Iterator<T> it = pro.iteratorFrom(startIndex);
				int len = (int)(endIndex - startIndex);
				for(int i=0;i<len;i++){
					cache[i] = it.next();
				}
				allCached = true;
			}
			
		}
		
		/**
		 * Cache all the new mapped elements.
		 */
		@Override
        public <N> Progression<N> mapTo(@NotNull Function<T, N> mapper, @NotNull MathCalculator<N> newCalculator) {
			@SuppressWarnings("unchecked")
			N[] newCache = (N[]) new Object[cache.length];
			for(int i=0;i<newCache.length;i++){
				newCache[i] = mapper.apply(cache[i]);
			}
			return new CachedProgression<>(pro.mapTo(mapper, newCalculator), newCache, startIndex, endIndex);
		}
		
		
	}
	
	
	static class GeneralFormulaProgression<T> extends Progression<T>{
		
		private final LongFunction<T> f;
		
		protected GeneralFormulaProgression(MathCalculator<T> mc, long length,LongFunction<T> generalFormula) {
			super(mc, length);
			this.f = generalFormula;
		}
		@Override
		public T get(long index) {
			if(inRange(index))
				return f.apply(index);
			throw new IndexOutOfBoundsException("For index:" + index);
		}
		
		@Override
        public <N> Progression<N> mapTo(@NotNull Function<T, N> mapper, @NotNull MathCalculator<N> newCalculator) {
			return new GeneralFormulaProgression<N>(newCalculator, length, l -> mapper.apply(f.apply(l)));
		}
	}
	static class FillingCachedProgression<T> extends Progression<T>{
		/**
		 * The recursive formula for this progression
		 */
		private final BiMathFunction<Progression<T>,Long,T> rf ;
		/**
		 * the index of the last element computed in this progression
		 */
		private int maxKnownIndex = 0;
		/**
		 * the storage for elements, increase by two times.
		 */
		T[] storage;
		/**
		 * The size of the array at first
		 */
		private static final int arraySize = 16;

		@SuppressWarnings("unchecked")
		protected FillingCachedProgression(MathCalculator<T> mc, long length,BiMathFunction<Progression<T>,Long,T> recursiveFormula,T[] firstSeveral) {
			super(mc, length);
			this.rf = recursiveFormula;
			storage = (T[]) new Object[Math.max(arraySize,firstSeveral.length)];
			for(int i=0;i<firstSeveral.length;i++){
				storage[i] = firstSeveral[i];
			}
			maxKnownIndex = firstSeveral.length-1;
		}
		@SuppressWarnings("unchecked")
		protected FillingCachedProgression(MathCalculator<T> mc, long length,BiMathFunction<Progression<T>,Long,T> recursiveFormula,T first) {
			super(mc, length);
			this.rf = recursiveFormula;
			storage = (T[]) new Object[arraySize];
			storage[0] = first;
			maxKnownIndex = 0;
		}
		private FillingCachedProgression(MathCalculator<T> mc, long length,BiMathFunction<Progression<T>,Long,T> recursiveFormula,T[] arr,int maxKnown){
			super(mc,length);
			rf = recursiveFormula;
			storage = arr;
			maxKnownIndex = maxKnown;
		}


		private T fillArrayTo(int pos){
			T t = storage[maxKnownIndex];
			for(int i=maxKnownIndex+1;i< pos+1;i++){
				storage[i] = (t = rf.apply(this,Long.valueOf(i)));
			}
			maxKnownIndex = pos;
			return t;
		}

		private void expandArray(int expectLength){
			int len = storage.length;
			int nlen = len + len << 1;
			if(nlen < 0 || nlen > ArraySup.MAX_ARRAY_SIZE || nlen < expectLength){
				//overflow
				nlen = expectLength < ArraySup.MAX_ARRAY_SIZE ? expectLength : ArraySup.MAX_ARRAY_SIZE;
			}
			storage = Arrays.copyOf(storage, nlen);
		}



		@Override
		public T[] toArray() {
			if(length == UNLIMITED || length >  ArraySup.MAX_ARRAY_SIZE){
				throw new UnsupportedOperationException("Unlimited progression.");
			}
			int len = (int)length;
			expandArray(len);
			fillArrayTo(len);
			return Arrays.copyOf(storage, len);
		}

		@Override
		public T get(long index) {
			if(! inRange(index)){
				throw new IndexOutOfBoundsException("for index:"+index);
			}
			if(index < ArraySup.MAX_ARRAY_SIZE){
				int pos = (int) index;
				if(pos<= maxKnownIndex){
					return storage[pos];
				}else if(pos < storage.length){
					return fillArrayTo(pos);
				}
				expandArray(pos+1);
				return fillArrayTo(pos);
			}
			expandArray(ArraySup.MAX_ARRAY_SIZE);
			fillArrayTo(ArraySup.MAX_ARRAY_SIZE-1);
			return get0(index);
		}

		private T get0(long index){
			if(index < maxKnownIndex){
				return storage[(int)index];
			}
			return rf.apply(this,index);
		}


		@Override
		public Progression<T> limit(long limit) {
			if(limit > 0 && (length == UNLIMITED || limit <= length)){
                return new FillingCachedProgression<>(getMc(), limit, rf, storage, maxKnownIndex);
			}
			throw new IndexOutOfBoundsException("for index:"+limit);
		}

		@Override
        public <N> Progression<N> mapTo(@NotNull Function<T, N> mapper, @NotNull MathCalculator<N> newCalculator) {
			return super.mapTo(mapper, newCalculator);
		}
	}
	static class CycleProgression<T> extends Progression<T>{
		
		private final T[] loop;
		private final int loopLength;
		
		protected CycleProgression(MathCalculator<T> mc, long length,T[] loop) {
			super(mc, length);
			this.loop = loop;
			this.loopLength = loop.length;
		}
		
		@Override
		public T get(long index) {
			return loop[(int) (index % loopLength)];
		}
		
		@Override
		public Iterator<T> iterator() {
			return new LoopIterator(0);
		}
		
		@Override
		public Iterator<T> iteratorFrom(long fromIndex) {
			return new LoopIterator(fromIndex);
		}
		
		@Override
		public Iterator<T> iteratorRange(long fromIndex, long endIndex) {
			return new LoopIterator(fromIndex, endIndex);
		}
		
		
		private class LoopIterator extends AbstractIndexIterator<T>{
			
			private int loopIndex ;
			
			public LoopIterator(long startFrom) {
				super(startFrom,length);
				loopIndex = (int)(startFrom % loopLength)-1;
			}
			
			LoopIterator(long startFrom, long endWith) {
				super(startFrom, endWith,length);
				loopIndex = (int)(startFrom % loopLength)-1;
			}

			@Override
			public T next() {
				loopIndex ++;
				index++;
				if(loopIndex >= loopLength){
					loopIndex = 0;
				}
				return loop[loopIndex];
			}
			
		}
		
		@Override
		public Progression<T> limit(long limit) {
			if(limit > 0 && (length == UNLIMITED || limit <= length)){
                return new CycleProgression<>(getMc(), limit, loop);
			}
			throw new IndexOutOfBoundsException("for index:"+limit);
		}
		
		
	}
	
	/**
	 * Create a new progression whose general formula is {@code formula}.The progression's first element has an 
	 * index of 0,which means the value will be {@code formula.apply(0)}.If the progression is unlimited,the parameter 
	 * {@code length} should be set as {@code -1}
	 * @param formula the general formula of this progression.
	 * @param length the length of this progression, or {@value #UNLIMITED} to indicate this progression is unlimited.
	 * @param mc a math calculator
	 * @return a newly created progression.
	 */
	public static <T> Progression<T> createProgression(LongFunction<T> formula,long length,MathCalculator<T> mc){
		return new GeneralFormulaProgression<>(mc, length < 0 ? UNLIMITED : length, formula);
	}
	/**
	 * Create a new progression whose recursive formula is {@code recursiveFormula}.The first element of this progression is 
	 * also needed.This progression fits that {@code a_n+1 = recursiveFormula.apply(a_n) | (n >= 0 )}.This kind of progression 
	 * will store the computed elements in temporary.
	 * @param recursiveFormula the recursive formula of this progression
	 * @param first the first element 
	 * @param length the length of this progression, or {@value #UNLIMITED} to indicate this progression is unlimited.
	 * @param mc a math calculator
	 * @return a newly created progression.
	 */
	public static <T> Progression<T> createProgressionRecur1(Function<T,T> recursiveFormula,T first,long length,MathCalculator<T> mc){
		return new FillingCachedProgression<>(mc, length, (progression,index)-> recursiveFormula.apply(progression.get(index-1)), first);
	}

	/**
	 * Create a new progression whose recursive formula is {@code recursiveFormula}.The first two elements of this progression are
	 * also needed. This progression fits that {@code a_n+1 = recursiveFormula.apply(a_(n-1),a_n) | (n >= 0 )}.This kind of progression
	 * will store the computed elements in temporary.
	 * @param recursiveFormula the recursive formula of this progression
	 * @param first the first element
	 * @param length the length of this progression, or {@value #UNLIMITED} to indicate this progression is unlimited.
	 * @param mc a math calculator
	 * @return a newly created progression.
	 */
	@SuppressWarnings("unchecked")
	public static <T> Progression<T> createProgressionRecur2(BiMathFunction<T,T,T> recursiveFormula,T first,T second,long length,MathCalculator<T> mc){
		return new FillingCachedProgression<>(mc, length,
				(progression,index)-> recursiveFormula.apply(progression.get(index-2),progression.get(index-1)),
				(T[])new Object[]{first,second});
	}
	/**
	 * Create a new progression whose recursive formula is {@code recur}. The recursive formula accepts the progression itself and
	 * the index of the required element.
	 * @param recur the recursive formula of this progression
	 * @param firstSeveral the first several elements
	 * @param length the length of this progression, or {@value #UNLIMITED} to indicate this progression is unlimited.
	 * @param mc a math calculator
	 * @return a newly created progression.
	 */
	public static <T> Progression<T> createProgressionRecur(BiMathFunction<Progression<T>,Long, T> recur, long length, MathCalculator<T> mc, T...firstSeveral){
		return new FillingCachedProgression<>(mc,length,recur,firstSeveral);
	}



	/**
	 * Create a new periodic that the first few elements are the elements in the array. 
	 * @param array an array containing elements.
	 * @param mc a math calculator
	 * @param length the length of this progression, or {@value #UNLIMITED} to indicate this progression is unlimited.
	 * @return a newly created progression.
	 * @throws NullPointerException if any element in the array is null.
	 */
	public static <T> Progression<T> createPeriodicProgression(T[] array,MathCalculator<T> mc,long length){
		for(T t : array){
			if(t == null){
				throw new NullPointerException();
			}
		}
		return new CycleProgression<>(mc, length, array);
	}
	
	
	/**
	 * Create a new progression containing the values in the given array.The order of elements in this progression
     * will be the identity to the order in the array.The length of this progression will be the identity to the length
	 * of the array.Notice that all of the elements in the array should not be null.
	 * @param array an array to copy data from.
	 * @param mc a math calculator
	 * @return a newly created progression.
	 */
	public static <T> Progression<T> fromArray(T[] array,MathCalculator<T> mc){
		@SuppressWarnings("unchecked")
		T[] arr = (T[])new Object[array.length];
		for(int i=0;i<arr.length;i++){
			arr[i] = Objects.requireNonNull(array[i]);
		}
		return new ArrayProgression<T>(mc, arr);
	}
	
	/**
	 * Create a new progression containing the elements in the given list.The order of the elements is 
     * the identity as the order in the list.
	 * @param list the list containing all the elements
	 * @param mc a math calculator
	 * @return a new progression
	 */
	@SuppressWarnings("unchecked")
	public static <T> Progression<T> fromList(List<T> list,MathCalculator<T> mc){
		return fromArray((T[])list.toArray(), mc);
	}
	
	/**
	 * Return a new progression that the combination of the given progressions,a function must be given.
	 * The {@code function} will be applied to arrays of numbers of type {@code T},the length of the array 
	 * will be the length of {@code pros},this method requires at least one progression.
	 * <P>
	 * The function itself should 
	 * be careful with the length of the number array for the number is corresponding to the progressions' order.
	 * <P> 
     * The length of these progressions should be the identity , or all of them are unlimited progressions.
	 * @param mc a new calculator for the progression
	 * @param function a function
	 * @param pros progressions to calculate
	 * @return a new progression
	 * @throws IllegalArgumentException if the parameters are not suitable.
	 * @throws NullPointerException if the given arguments are null.
	 */
	@SafeVarargs
	public static <R,T> Progression<R>
	combinedProgression(MathCalculator<R> mc,Function<T[],R> function,Progression<T>...pros){
		//length check
		long length = pros[0].length;
		for(int i=1;i<pros.length;i++){
			if(length != pros[i].getLength()){
				throw new IllegalArgumentException();
			}
		}
		return new CombinedProgression<>(mc, function, pros);
	}
	
	/**
	 * Assume the progression {@code pro} is {@code p(n)},and this method will return a new progression that is {@code f(p(n))}.
     * The new progression has the identity length as the original one.This method is like a one-parameter version of method
	 * {@link #combinedProgression(MathCalculator, Function, Progression...)},while it has better performance.
	 * @param mc a new calculator for the progression
	 * @param f a function
	 * @param pro progression to calculate
	 * @return a new mapped progression
	 * @throws NullPointerException if the given arguments are null.
	 */
	public static <R,T> Progression<R> computeProgression(MathCalculator<R> mc,Function<T,R> f,Progression<T> pro){
		return new MappedProgression<T,R>(mc, f, pro);
	}
	
	/**
	 * Cache this progression for faster {@code get()} methods.A specific start index and the cache size should be 
	 * assigned.Whether to compute all the numbers can be also determined.
	 * @param pro the progression to cache.
	 * @param startIndex the start index in the progression
	 * @param cacheSize the number of elements to cache from the start to end.
	 * @param cacheNow determines whether to compute all the number now.
	 * @return a cached progression.
	 */
	public static <T> Progression<T> cachedProgression(Progression<T> pro,long startIndex,int cacheSize,boolean cacheNow){
		if(startIndex < 0 || cacheSize <= 0){
			throw new IllegalArgumentException("bad index or size");
		}
		
		long length = pro.length;
		long endIndex = startIndex + cacheSize;
		if(length != UNLIMITED && endIndex > length){
			throw new IllegalArgumentException("Out of bound");
		}
		CachedProgression<T> cp =  new CachedProgression<>(pro, startIndex, endIndex,length);
		if(cacheNow){
			cp.cacheAll();
		}
		return cp;
	}

}


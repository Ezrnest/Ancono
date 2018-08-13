/**
 * 2017-09-08
 */
package cn.timelives.java.math.set;

import cn.timelives.java.math.MathCalculator;
import cn.timelives.java.math.algebra.abstractAlgebra.GroupCalculators;
import cn.timelives.java.math.algebra.abstractAlgebra.calculator.EqualPredicate;
import cn.timelives.java.math.numberModels.Calculators;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Stream;

/**
 * @author liyicheng
 * 2017-09-08 16:31
 *
 */
public final class MathSets {
	
	/**
	 * 
	 */
	private MathSets() {
		throw new AssertionError("No instance!");
	}
	
	
	public static <T> SingletonSet<T> singleton(T t,EqualPredicate<T> mc){
        return new SingletonSet<>(GroupCalculators.INSTANCE.toMathCalculatorEqual(mc), t);
	}

	@SafeVarargs
	public static <T> CollectionSet<T> asSet(EqualPredicate<T> mc, T...ts){
		List<T> list = new ArrayList<>(ts.length);
		for(T t : ts){
			for(T t0 : list) {
				if(mc.isEqual(t, t0)) {
					//equal
					continue;
				}
			}
			list.add(t);
		}
        return new CollectionSet<>(GroupCalculators.INSTANCE.toMathCalculatorEqual(mc), list);
	}


	/**
	 * Returns a CollectionSet created from the given Collection.
	 * A copy of {@code coll} will be created. 
	 * @param coll
	 * @param mc
	 * @return
	 */
	public static <T> CollectionSet<T> fromCollection(Collection<T> coll,EqualPredicate<T> mc){
		List<T> list = new ArrayList<>(coll.size());
		for(T t : coll){
			for(T t0 : list) {
				if(mc.isEqual(t, t0)) {
					//equal
					continue;
				}
			}
			list.add(t);
		}
        return new CollectionSet<>(GroupCalculators.INSTANCE.toMathCalculatorEqual(mc), coll);
	}
	/**
	 * Returns the symmetricGroups set in math, which contains all the elements. The set will
	 * returns "Ω" when toString() is called, which indicates is an
	 * symmetricGroups set in math.
	 * @return Ω
	 */
	@SuppressWarnings("unchecked")
	public static <T> MathSet<T> universe(){
		return (MathSet<T>)UNIVERSE;
	}
	/**
	 * Returns the empty set in math, which contains no element. The set will
	 * returns "∅" when toString() is called, which indicates is an
	 * empty set in math.
	 * @return ∅
	 */
	@SuppressWarnings("unchecked")
	public static <T> MathSet<T> empty(){
		return (MathSet<T>)EMPTY;
	}
	
	
	
	private static final Universe<?> UNIVERSE= new Universe<>();
	private static final Empty<?> EMPTY = new Empty<>();



    static final class Universe<T> implements MathSet<T>{
		private Universe() {
		}
		@Override
		public boolean contains(T t) {
			Objects.requireNonNull(t);
			return true;
		}
		

		/**
		 * Returns "Ω", which indicates this is an
		 * symmetricGroups set in math.
		 */
		@Override
		public String toString() {
			return "Ω";
		}
	}
	
	static final class Empty<T> implements FiniteSet<T>{

		/**
		 * @see cn.timelives.java.math.set.FiniteSet#get(long)
		 */
		@Override
		public T get(long index) {
			throw new IndexOutOfBoundsException("Empty");
		}

		/**
		 * @see cn.timelives.java.math.set.FiniteSet#get(java.math.BigInteger)
		 */
		@Override
		public T get(BigInteger index) {
			throw new IndexOutOfBoundsException("Empty");
		}
		
		/**
		 * @see cn.timelives.java.math.set.FiniteSet#listIterator()
		 */
		@Override
		public ListIterator<T> listIterator() {
			return Collections.emptyListIterator();
		}

		/**
		 * @see cn.timelives.java.math.set.CountableSet#iterator()
		 */
		@Override
		public Iterator<T> iterator() {
			return Collections.emptyListIterator();
		}

		/**
		 * @see cn.timelives.java.math.set.CountableSet#stream()
		 */
		@Override
		public Stream<T> stream() {
			return Stream.empty();
		}
		

		/**
		 * @see cn.timelives.java.math.set.CountableSet#size()
		 */
		@Override
		public long size() {
			return 0;
		}

		/**
		 * @see cn.timelives.java.math.set.CountableSet#sizeAsBigInteger()
		 */
		@Override
		public BigInteger sizeAsBigInteger() {
			return BigInteger.ZERO;
		}

		/**
		 * @see cn.timelives.java.math.set.MathSet#contains(java.lang.Object)
		 */
		@Override
		public boolean contains(T t) {
			return false;
		}
		/**
		 * @see cn.timelives.java.math.set.CountableSet#isFinite()
		 */
		@Override
		public boolean isFinite() {
			return true;
		}
		
		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "∅";
		}
		
	}


    /**
     * Determines whether the set contains all the elements in s2.
     * @param set
     * @param s2
     * @param <T>
     * @return
     */
	public static <T> boolean containsAll(MathSet<T> set, FiniteSet<T> s2) {
	    for(T t : s2){
	        if(!set.contains(t)){
	            return false;
            }
        }
        return true;
	}
}

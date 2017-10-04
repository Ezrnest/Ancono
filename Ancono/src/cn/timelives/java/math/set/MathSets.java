/**
 * 2017-09-08
 */
package cn.timelives.java.math.set;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import cn.timelives.java.math.FlexibleMathObject;
import cn.timelives.java.math.numberModels.MathCalculator;
import cn.timelives.java.math.numberModels.NumberFormatter;

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
	
	
	public static <T> SingletonSet<T> singleton(T t,MathCalculator<T> mc){
		return new SingletonSet<T>(mc, t);
	}
	@SafeVarargs
	public static <T> CollectionSet<T> asSet(MathCalculator<T> mc,T...ts){
		List<T> list = new ArrayList<>(ts.length);
		for(T t : ts){
			list.add(t);
		}
		return new CollectionSet<>(mc, list);
	}
	/**
	 * Returns a CollectionSet created from the given Collection.
	 * A copy of {@code coll} will be created. 
	 * @param coll
	 * @param mc
	 * @return
	 */
	public static <T> CollectionSet<T> fromCollection(Collection<T> coll,MathCalculator<T> mc){
		return new CollectionSet<>(mc, coll);
	}
	/**
	 * Returns the universe set in math, which contains all the elements. The set will
	 * returns "Ω" when toString() is called, which indicates is an
	 * universe set in math.
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
		/**
		 * @param mc
		 */
		private Universe() {
		}
		@Override
		public boolean contains(T t) {
			Objects.requireNonNull(t);
			return true;
		}
		

		/**
		 * Returns "Ω", which indicates this is an
		 * universe set in math.
		 */
		@Override
		public String toString() {
			return "Ω";
		}
	}
	
	static final class Empty<T> implements LimitedSet<T>{

		/**
		 * @see cn.timelives.java.math.set.LimitedSet#get(long)
		 */
		@Override
		public T get(long index) {
			throw new IndexOutOfBoundsException("Empty");
		}

		/**
		 * @see cn.timelives.java.math.set.LimitedSet#get(java.math.BigInteger)
		 */
		@Override
		public T get(BigInteger index) {
			throw new IndexOutOfBoundsException("Empty");
		}
		
		/**
		 * @see cn.timelives.java.math.set.LimitedSet#listIterator()
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
	
}

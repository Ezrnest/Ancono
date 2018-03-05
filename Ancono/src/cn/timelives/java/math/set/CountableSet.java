/**
 * 2017-09-10
 */
package cn.timelives.java.math.set;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * A countable set is a set that is iterable, but it contains either 
 * infinite or finite elements. For example, the set of all the integers or
 * {x|x=2k,k in Z} are both countable set with infinite elements.
 * @author liyicheng
 * @see FiniteSet
 *
 */
public interface CountableSet<T> extends MathSet<T> ,Iterable<T>{
	/**
	 * Returns an iterator that iterates this countable set, the 
	 * iterator may always return {@code true} when calling {@link Iterator#hasNext()}
	 * if this countable set contains infinite elements.
	 * The order of the elements can be any, but should 
	 * maintain the same. 
	 */
	@Override
	Iterator<T> iterator();
	
	/**
	 * Returns this countable set's elements as a stream.
	 * @return
	 */
	default Stream<T> stream(){
		if(isFinite()){
			long size = size();
			//limited
			Spliterator<T> spl = Spliterators.spliterator(iterator(), size, Spliterator.IMMUTABLE | Spliterator.SIZED);
	        return StreamSupport.stream(spl, false);
		}else{
			return Stream.generate(new Supplier<T>(){
				final Iterator<T> it = iterator();
				@Override
				public T get() {
					return it.next();
				}
			});
		}
	}
	
	/**
	 * Gets the size of this countable set, returns {@code -1} if 
	 * this set contains infinite elements, throws {@link UnsupportedOperationException}
	 * if the size exceeds long.
	 * @return the size of this set
	 * @throws UnsupportedOperationException if the size exceeds long.
	 */
	long size();
	
	/**
	 * Gets the size of this countable set, returns {@code -1} if 
	 * this set contains infinite elements.
	 * @return the size of this set
	 */
	BigInteger sizeAsBigInteger();
	
	/**
	 * Determines whether this CountableSet contains finite elements.
	 * @return
	 */
	default boolean isFinite(){
		return size()!=-1;
	}
	
}

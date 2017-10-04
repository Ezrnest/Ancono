/**
 * 
 */
package cn.timelives.java.math.set;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import cn.timelives.java.math.numberModels.MathCalculator;

/**
 *
 *
 */
public abstract class AbstractCountableSet<T> extends AbstractMathSet<T> implements CountableSet<T>{
	
	
	/**
	 * @param mc
	 */
	protected AbstractCountableSet(MathCalculator<T> mc) {
		super(mc);
	}
	
	
	
	/**
	 * @see cn.timelives.java.math.set.CountableSet#stream()
	 */
	@Override
	public Stream<T> stream() {
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
	
	
	@Override
	public BigInteger sizeAsBigInteger() {
		return BigInteger.valueOf(size());
	}
	
	@Override
	public abstract <N> AbstractCountableSet<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator);
}

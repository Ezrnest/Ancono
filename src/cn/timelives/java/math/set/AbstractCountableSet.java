/**
 * 
 */
package cn.timelives.java.math.set;

import cn.timelives.java.math.MathCalculator;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.function.Function;

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
	
	
	
	
	
	@Override
	public BigInteger sizeAsBigInteger() {
		return BigInteger.valueOf(size());
	}
	
	@NotNull
    @Override
    public abstract <N> AbstractCountableSet<N> mapTo(@NotNull Function<T, N> mapper, @NotNull MathCalculator<N> newCalculator);
}

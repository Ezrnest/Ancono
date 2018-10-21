/**
 * 2017-09-09
 */
package cn.timelives.java.math.set;

import cn.timelives.java.math.MathObject;
import cn.timelives.java.math.MathCalculator;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * An abstract class for math sets, which extends the super class {@link MathObject}.
 * @author liyicheng
 * 2017-09-09 20:26
 *
 */
public abstract class AbstractMathSet<T> extends MathObject<T> implements MathSet<T> {

	/**
	 * @param mc
	 */
	protected AbstractMathSet(MathCalculator<T> mc) {
		super(mc);
	}

	@NotNull
    @Override
    public abstract <N> AbstractMathSet<N> mapTo(@NotNull Function<T, N> mapper, @NotNull MathCalculator<N> newCalculator);
	

}

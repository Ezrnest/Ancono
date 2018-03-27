package cn.timelives.java.math.numberModels;

import cn.timelives.java.math.exceptions.UnsupportedCalculationException;



/**
 * An adapter for MathCalculator, all methods are implemented by throwing UnsupportedOperationException.
 * This class also provides some basic calculators for the frequently-used number classes. 
 * @author lyc
 *
 * @param <T> the type of number to deal with
 */
public abstract class MathCalculatorAdapter<T> implements MathCalculator<T> {
	
	private static void throwFor() throws UnsupportedCalculationException{
		throw new UnsupportedCalculationException("Adapter");
	}
	
	
	
	@Override
	public boolean isEqual(T para1, T para2) {
		throwFor();
		return false;
	}

	@Override
	public int compare(T para1, T para2) {
		throwFor();
		return 0;
	}

	@Override
	public T add(T para1, T para2) {
		throwFor();
		return null;
	}

	@Override
	public T negate(T para) {
		throwFor();
		return null;
	}

	@Override
	public T abs(T para) {
		throwFor();
		return null;
	}

	@Override
	public T subtract(T para1, T para2) {
		throwFor();
		return null;
	}

	@Override
	public T multiply(T para1, T para2) {
		throwFor();
		return null;
	}

	@Override
	public T divide(T para1, T para2) {
		throwFor();
		return null;
	}
	
	@Override
	public T divideLong(T p, long l) {
		throwFor();
		return null;
	}
	
	@Override
	public T multiplyLong(T p, long l) {
		throwFor();
		return null;
	}
	
	@Override
	public T getOne() {
		throwFor();
		return null;
	}
	
	@Override
	public T getZero() {
		throwFor();
		return null;
	}
	
	@Override
	public T reciprocal(T p) {
		throwFor();
		return null;
	}
	
	@Override
	public T squareRoot(T p) {
		throwFor();
		return null;
	}
	
	@Override
	public T pow(T p, long exp) {
		throwFor();
		return null;
	}
	
	/**
	 * @see cn.timelives.java.math.numberModels.MathCalculator#nroot(java.lang.Object, long)
	 */
	@Override
	public T nroot(T x, long n) {
		throwFor();
		return null;
	}
	
	
	
	@Override
	public T constantValue(String name) {
		throwFor();
		return null;
	}
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.number_models.MathCalculator#exp(java.lang.Object)
	 */
	@Override
	public T exp(T x) {
		throwFor();
		return null;
	}
	
	
	
	
	
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.number_models.MathCalculator#ln(java.lang.Object)
	 */
	@Override
	public T ln(T x) {
		throwFor();
		return null;
	}
	
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.number_models.MathCalculator#sin(java.lang.Object)
	 */
	@Override
	public T sin(T x) {
		throwFor();
		return null;
	}
	
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.number_models.MathCalculator#arcsin(java.lang.Object)
	 */
	@Override
	public T arcsin(T x) {
		throwFor();
		return null;
	}
	
	/* (non-Javadoc)
	 * @see cn.timelives.java.utilities.math.MathCalculator#getNumberClass()
	 */
	@Override
	public Class<?> getNumberClass() {
		return getZero().getClass();
	}
	
	
	
	
	
	
	
}

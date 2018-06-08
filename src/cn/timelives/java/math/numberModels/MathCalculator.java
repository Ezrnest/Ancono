package cn.timelives.java.math.numberModels;

import cn.timelives.java.math.abstractAlgebra.calculator.FieldCalculator;
import cn.timelives.java.math.exceptions.UnsupportedCalculationException;

import java.util.Comparator;

/**
 * Describe a calculator that can calculator the basic operations for
 * number, this interface is create to give some math-based objects full
 * flexibility to all kind of numbers. 
 * <P>
 * All methods in a math calculator should be consistent.No change should be
 * done to the number when any method is called.
 * <p>
 * All methods in this calculator may not be operational because of the limit of
 * number's format and so other reasons,so if necessary, an
 * {@linkplain UnsupportedCalculationException} can be thrown.For some special
 * operations, exceptional arithmetic condition may occur, so an
 * {@linkplain ArithmeticException} may be thrown.
 * <p>
 * It is highly recommended that you should only create one instance of the math
 * calculator and pass it all through the calculation. This can keep the
 * calculation result from being different and in some FlexibleMathObject,such
 * as Triangle,may contain other FlexibleMathObject,and some calculation is not
 * strongly made sure that only the calculator from the Triangle itself is
 * used(which means the calculator in Point may be used), so there may be
 * potential safety problems. Therefore, in a multiple-number-type task, you
 * should always be careful with the math calculator.
 * <p>
 * A MathCalculator naturally deals with numbers, so it is a subclass of {@link FieldCalculator}.
 * However, it is not strictly required all the operations(addition, multiplication..)
 * must return a number and throwing exceptions is acceptable.  
 * @author lyc
 *
 * @param <T>
 *            the type of number to deal with
 */
public interface MathCalculator<T> extends FieldCalculator<T>,Comparator<T> {

	/**
	 * Compare the two numbers and determines whether these two numbers are the
	 * same.
	 * <P>
	 * <b> For any calculator, this method should be implemented.</b>
	 * 
	 * @param para1
	 *            a number
	 * @param para2
	 *            another number
	 * @return {@code true} if {@code para1 == para2},otherwise {@code false}
	 * @throws UnsupportedCalculationException
	 *             if this operation can not be done.(optional)
	 */
	public boolean isEqual(T para1, T para2);

	/**
	 * Compare the two numbers, return -1 if {@code para1 < para2 }, 0 if
	 * {@code para1==para2} , or 1 if {@code para1 > para2}.This method is
	 * recommended to be literally the same to the method {@code compareTo()} if the
	 * object {@code T} is comparable.
	 * 
	 * @param para1
	 *            a number
	 * @param para2
	 *            another number
	 * @return -1 if {@code para1 < para2 }, 0 if {@code para1==para2} , or 1 if
	 *         {@code para1 > para2}
	 * @throws UnsupportedCalculationException
	 *             if this operation can not be done.(optional)
	 */
	int compare(T para1, T para2);

	/**
	 * Determines whether this calculator supports {@code compare()} method.
	 * @return {@code true} if compare method is available.
	 */
	boolean isComparable();

	/**
	 * Add two parameters, this method is required to be commutative, so is it
	 * required that {@code add(t1,t2)=add(t2,t1)}.
	 * 
	 * @param para1
	 *            a number
	 * @param para2
	 *            another number
	 * @return {@code para1 + para2}
	 * @throws UnsupportedCalculationException
	 *             if this operation can not be done.(optional)
	 * @throws ArithmeticException
	 *             if this operation causes an exceptional arithmetic condition.
	 */
	public T add(T para1, T para2);

	/**
	 * Add the parameters,this method is equal to:
	 * 
	 * <pre>
	 * T sum = getZero();
	 * for (Object t : ps) {
	 * 	sum = add(sum, (T) t);
	 * }
	 * return sum;
	 * </pre>
	 * 
	 * The Object-type input array is to fit genetic types.
	 * 
	 * @param ps
	 * @return the sum of {@code ps}
	 * @throws UnsupportedCalculationException
	 *             if this operation can not be done.(optional)
	 * @throws ArithmeticException
	 *             if this operation causes an exceptional arithmetic condition.
	 */
	@SuppressWarnings("unchecked")
	public default T addX(Object... ps) {
		T sum = getZero();
		for (Object t : ps) {
			sum = add(sum, (T) t);
		}
		return sum;
	}

	/**
	 * Returns the negate of this number.
	 * 
	 * @param para
	 *            a number
	 * @return {@code -para}
	 * @throws UnsupportedCalculationException
	 *             if this operation can not be done.(optional)
	 * @throws ArithmeticException
	 *             if this operation causes an exceptional arithmetic condition.
	 */
	public T negate(T para);

	/**
	 * Returns the absolute value of this number.
	 * 
	 * @param para
	 *            a number
	 * @return {@code |para|}
	 * @throws UnsupportedCalculationException
	 *             if this operation can not be done.(optional)
	 * @throws ArithmeticException
	 *             if this operation causes an exceptional arithmetic condition.
	 */
	public T abs(T para);

	/**
	 * Returns the result of {@code para1-para2},this method should return the same
	 * result with {@code add(para1,this.negate(para2))}.
	 * 
	 * @param para1
	 *            a number
	 * @param para2
	 *            another number
	 * @return {@code para1-para2}
	 * @throws UnsupportedCalculationException
	 *             if this operation can not be done.(optional)
	 * @throws ArithmeticException
	 *             if this operation causes an exceptional arithmetic condition.
	 */
	public T subtract(T para1, T para2);

	/**
	 * Return the value zero in this kind of number type.The returned number should
	 * be equal to {@code this.subtract(t,t)}.
	 * 
	 * @return 0
	 * @throws UnsupportedCalculationException
	 *             if this operation can not be done.(optional)
	 */
	public T getZero();

	/**
	 * Determines whether the given parameter is zero.This method is set because the
	 * high frequency of testing whether the number is zero in most math
	 * calculations.
	 * 
	 * @return {@code true} if {@code para==zero}
	 * @throws UnsupportedCalculationException
	 *             if this operation can not be done.(optional)
	 */
	public default boolean isZero(T para) {
		return isEqual(getZero(), para);
	}

	/**
	 * Returns the result of {@code para1 * para2}.
	 * 
	 * @param para1
	 *            a number
	 * @param para2
	 *            another number
	 * @return {@code para1 * para2}
	 * @throws UnsupportedCalculationException
	 *             if this operation can not be done.(optional)
	 * @throws ArithmeticException
	 *             if this operation causes an exceptional arithmetic condition.
	 */
	public T multiply(T para1, T para2);

	/**
	 * Multiply the parameters,this method is equal to:
	 * 
	 * <pre>
	 * T re = getOne();
	 * for (T t : ps) {
	 * 	re = multiply(re, t);
	 * }
	 * return re;
	 * </pre>
	 * 
	 * @param ps
	 * @return the result
	 * @throws UnsupportedCalculationException
	 *             if this operation can not be done.(optional)
	 * @throws ArithmeticException
	 *             if this operation causes an exceptional arithmetic condition.
	 */
	@SuppressWarnings("unchecked")
	public default T multiplyX(Object... ps) {
		T re = getOne();
		for (Object t : ps) {
			re = multiply(re, (T) t);
		}
		return re;
	}

	/**
	 * Returns the result of {@code para1 / para2}.
	 * 
	 * @param para1
	 *            a number as dividend
	 * @param para2
	 *            another number as divisor
	 * @return {@code para1 / para2}
	 * @throws UnsupportedCalculationException
	 *             if this operation can not be done.(optional)
	 * @throws ArithmeticException
	 *             if this operation causes an exceptional arithmetic condition.
	 */
	public T divide(T para1, T para2);

	/**
	 * Return the value one in this kind of number type. The returned number should
	 * be equal to {@code this.divide(t,t)}.
	 * 
	 * @return 1
	 * @throws UnsupportedCalculationException
	 *             if this operation can not be done.(optional)
	 */
	public T getOne();

	/**
	 * Return the value of {@code 1/p}. This method should be equal to
	 * {@code this.divide(this.getOne,p)}.
	 * 
	 * @param p
	 *            a number
	 * @return {@code 1/p}
	 * @throws UnsupportedCalculationException
	 *             if this operation can not be done.(optional)
	 * @throws ArithmeticException
	 *             if this operation causes an exceptional arithmetic condition.
	 */
	public T reciprocal(T p);

	/**
	 * Return the result of {@code l*p}, this method is provided because this is
	 * equals to {@code add(p,p)} for {@code l} times. This method expects a better
	 * performance.
	 * 
	 * @param p
	 *            a number
	 * @param l
	 *            another number of long
	 * @return {@code p*l}
	 * @throws UnsupportedCalculationException
	 *             if this operation can not be done.(optional)
	 * @throws ArithmeticException
	 *             if this operation causes an exceptional arithmetic condition.
	 */
	public default T multiplyLong(T p, long l) {
		return FieldCalculator.super.multiplyLong(p, l);
	}

	/**
	 * Return the result of {@code p / l} , throws exception if necessary.
	 * 
	 * @param p
	 *            a number as dividend
	 * @param l
	 *            another number of long as divisor
	 * @return {@code p / l}
	 * @throws UnsupportedCalculationException
	 *             if this operation can not be done.(optional)
	 * @throws ArithmeticException
	 *             if this operation causes an exceptional arithmetic condition.
	 */
	public T divideLong(T p, long l);

	/**
	 * Return the square root of {@code p}. This method should return the positive
	 * square of {@code p}.
	 * 
	 * @param p
	 *            a number
	 * @return {@code p ^ 0.5}
	 * @throws UnsupportedCalculationException
	 *             if this operation can not be done.(optional)
	 * @throws ArithmeticException
	 *             if this operation causes an exceptional arithmetic condition.
	 */
	public T squareRoot(T p);

	/**
	 * Return the n-th root of {@code x}. This method should return a positive
	 * number if {@code n} is even.
	 * 
	 * @param x
	 *            a number
	 * @return {@code x ^ (1/n)}
	 * @throws UnsupportedCalculationException
	 *             if this operation can not be done.(optional)
	 * @throws ArithmeticException
	 *             if this operation causes an exceptional arithmetic condition.
	 */
	public T nroot(T x, long n);

	/**
	 * Return {@code p ^ exp}.This method should be equal to calling
	 * {@code this.multiply(p,p)} for many times if {@code exp > 0} , or
	 * {@code this.divide(p,p)} if {@code exp < 0 }, or return {@code getOne()} if
	 * {@code exp == 0}.Notice that this calculator may not throw an
	 * ArithmeticException if {@code p == 0 && exp <= 0},whether to throw exception
	 * is determined by the implementation.
	 * 
	 * @param p
	 *            a number
	 * @param exp
	 *            the exponent
	 * @return {@code p ^ exp}.
	 * @throws UnsupportedCalculationException
	 *             if this operation can not be done.(optional)
	 * @throws ArithmeticException
	 *             if this operation causes an exceptional arithmetic condition.
	 */
	public T pow(T p, long exp);

	/**
	 * The string representation of pi.
	 */
	String STR_PI = "Pi";
	/**
	 * The string representation of e.
	 */
	String STR_E = "e";
	/**
	 * The string representation of i, the square root of -1.
	 * This constant value may not be available.
	 */
	String STR_I = "i";
	/**
	 * Gets a constant value from the calculator, the constant value is got by its
	 * name as a String. It is recommended that the string should be case
	 * insensitive in case of spelling mistakes. The name of the constant value should be
	 * specified wherever the value is needed. <br>
	 * Some common constants are list below:
	 * <ul>
	 * <li><tt>Pi</tt> :the ratio of the circumference of a circle to its
	 * diameter.See:{@link Math#PI}
	 * <li><tt>e</tt> :the base of the natural logarithms.See:{@link Math#E}
	 * <li><tt>i</tt> :the square root of {@code -1}.
	 * </ul>
	 * 
	 * @param name
	 *            the name of the constant value,case insensitive
	 * @return a number that represents the constant value.
	 * @throws UnsupportedCalculationException
	 *             if this operation can not be done.(optional)
	 */
	public T constantValue(String name);

	/**
	 * Returns the result of {@code a^b}. <br>
	 * This method provides a default implement by computing:
	 * {@code exp(multiply(ln(a), b))}.
	 * 
	 * @param a
	 *            a number
	 * @param b
	 *            the exponent
	 * @return {@code a^b}
	 * @throws UnsupportedCalculationException
	 *             if this operation can not be done.(optional)
	 * @throws ArithmeticException
	 *             if this operation causes an exceptional arithmetic condition.
	 */
	public default T exp(T a, T b) {
		return exp(multiply(ln(a), b));
	}

	/**
	 * Returns the result of {@code e^x}, where {@code e} is the base of the natural
	 * logarithm.
	 * 
	 * @param x
	 *            the exponent
	 * @return {@code e^x}
	 * @throws UnsupportedCalculationException
	 *             if this operation can not be done.(optional)
	 * @throws ArithmeticException
	 *             if this operation causes an exceptional arithmetic condition.
	 */
	public T exp(T x);

	/**
	 * Returns result of
	 * 
	 * <pre>
	 * log<sub>a</sub>b
	 * </pre>
	 * 
	 * <br>
	 * This method provides a default implement by computing:
	 * {@code divide(ln(b),ln(a))}.
	 * 
	 * @param a
	 *            a number
	 * @param b
	 *            another number
	 * @return
	 * 
	 *         <pre>
	 * log<sub>a</sub>b
	 *         </pre>
	 * 
	 * @throws UnsupportedCalculationException
	 *             if this operation can not be done.(optional)
	 * @throws ArithmeticException
	 *             if this operation causes an exceptional arithmetic condition.
	 */
	public default T log(T a, T b) {
		return divide(ln(b), ln(a));
	}

	/**
	 * Returns result of
	 * 
	 * <pre>
	 * ln(x)
	 * </pre>
	 * 
	 * or the natural logarithm (base e).
	 * 
	 * @param x
	 *            a number
	 * @return
	 * 
	 *         <pre>
	 *         ln(x)
	 *         </pre>
	 * 
	 * @throws UnsupportedCalculationException
	 *             if this operation can not be done.(optional)
	 * @throws ArithmeticException
	 *             if this operation causes an exceptional arithmetic condition.
	 */
	public T ln(T x);

	/**
	 * Returns the result of {@code sin(x)}
	 * 
	 * @param x
	 *            a number
	 * @return {@code sin(x)}
	 * @throws UnsupportedCalculationException
	 *             if this operation can not be done.(optional)
	 * @throws ArithmeticException
	 *             if this operation causes an exceptional arithmetic condition.
	 */
	public T sin(T x);

	/**
	 * Returns the result of {@code cos(x)}. <br>
	 * This method provides a default implement by computing:
	 * {@code squareRoot(subtract(getOne(), multiply(x, x)))}. If a better implement
	 * is available, subclasses should always override this method.
	 * 
	 * @param x
	 *            a number
	 * @return {@code cos(x)}
	 * @throws UnsupportedCalculationException
	 *             if this operation can not be done.(optional)
	 * @throws ArithmeticException
	 *             if this operation causes an exceptional arithmetic condition.
	 * 
	 */
	public default T cos(T x) {
		return squareRoot(subtract(getOne(), multiply(x, x)));
	}

	/**
	 * Returns the result of {@code tan(x)}. <br>
	 * This method provides a default implement by computing:
	 * {@code  divide(sin(x),cos(x))}. If a better implement is available,
	 * subclasses should always override this method.
	 * 
	 * @param x
	 *            a number
	 * @return {@code tan(x)}
	 * @throws UnsupportedCalculationException
	 *             if this operation can not be done.(optional)
	 * @throws ArithmeticException
	 *             if this operation causes an exceptional arithmetic condition.
	 */
	public default T tan(T x) {
		return divide(sin(x), cos(x));
	}

	/**
	 * Returns the result of {@code arcsin(x)}.
	 * 
	 * @param x
	 *            a number
	 * @return {@code arcsin(x)}
	 * @throws UnsupportedCalculationException
	 *             if this operation can not be done.(optional)
	 * @throws ArithmeticException
	 *             if this operation causes an exceptional arithmetic condition.
	 */
	public T arcsin(T x);

	/**
	 * Returns the result of {@code arccos(x)}. <br>
	 * This method provides a default implement by computing:
	 * {@code  subtract(divideLong(constantValue(STR_PI), 2l), arcsin(x))}. If a
	 * better implement is available, subclasses should always override this method.
	 * 
	 * @param x
	 *            a number
	 * @return {@code arccos(x)}
	 * @throws UnsupportedCalculationException
	 *             if this operation can not be done.(optional)
	 * @throws ArithmeticException
	 *             if this operation causes an exceptional arithmetic condition.
	 */
	public default T arccos(T x) {
		return subtract(divideLong(constantValue(STR_PI), 2l), arcsin(x));
	}

	/**
	 * Returns the result of {@code arctan(x)}. <br>
	 * This method provides a default implement by computing:
	 * {@code arcsin(divide(x,squareRoot(add(getOne(), multiply(x, x)))))}. If a
	 * better implement is available, subclasses should always override this method.
	 * 
	 * @param x
	 *            a number
	 * @return {@code arctan(x)}
	 * @throws UnsupportedCalculationException
	 *             if this operation can not be done.(optional)
	 * @throws ArithmeticException
	 *             if this operation causes an exceptional arithmetic condition.
	 */
	public default T arctan(T x) {
		return arcsin(divide(x, squareRoot(add(getOne(), multiply(x, x)))));
	}

	/**
	 * Returns the class object of the number type operated by this MathCalculator.
	 * 
	 * @return the class
	 */
	public Class<?> getNumberClass();

}

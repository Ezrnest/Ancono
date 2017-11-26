/**
 * 2017-11-24
 */
package cn.timelives.java.math.numberModels.expression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import cn.timelives.java.math.numberModels.MathCalculator;
import cn.timelives.java.math.numberModels.PolyCalculator;
import cn.timelives.java.math.numberModels.Polynomial;
import cn.timelives.java.utilities.ArraySup;

/**
 * An expression function describes a function that the calculator should
 * recognize and calculator when simplifying an expression. The ExprFunction is
 * a combined class of the function's details(name, number of parameters...) and
 * also the polynomial function in program(optional).
 * <p>
 * Polynomial functions in program is responsible for calculating the function
 * directly to a polynomial result. Generally, a function can be 'computed' via
 * directly applying the function or {@link SimplificationStrategy}.
 * Simplification strategy is more flexible and can apply to more situations
 * when the parameter is not a polynomial. However, it is vital to use polynomial function
 * to simplify the expression to a plain Polynomial, which is the most efficient form
 * of an expression and this calculation can be done in the first phase of simplification. 
 * So if a function can be calculated to a polynomial result for some parameters, it is recommended 
 * to implement a polynomial function.
 * <p>
 * The functions are divided to {@link PolyFunctionS}, {@link PolyFunctionB} and
 * {@link PolyFunctionM}. The reason why the functions are divided is to speed the calculation.
 * <p>
 * There is a basic bundle of expression functions which is already defined in a {@link MathCalculator}.
 * 
 * @author liyicheng 2017-11-24 20:04
 * 
 */
public class ExprFunction {
	private final String name;
	private final boolean paramOrdered;
	private final int paramNumber;
	private final String description;
	private final Object polyFunction;
	/**
	 * @param name
	 * @param paramOrdered
	 * @param paramNumber
	 * @param paramDetails an object(or null) to indicate the details of each parameter
	 */
	ExprFunction(String name, int paramNumber, boolean paramOrdered, String description,Object polyFunction) {
		super();
		if(paramNumber<=0 ) {
			throw new IllegalArgumentException();
		}
		this.paramNumber = Objects.requireNonNull(paramNumber);
		this.name = Objects.requireNonNull(name);
		this.paramOrdered = Objects.requireNonNull(paramOrdered);
		this.description = Objects.requireNonNull(description);
		this.polyFunction = polyFunction;
	}
	
	/**
	 * Gets the name.
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * Gets the paramOrdered.
	 * @return the paramOrdered
	 */
	public boolean isParamOrdered() {
		return paramOrdered;
	}
	/**
	 * Gets the paramNumber.
	 * @return the paramNumber
	 */
	public int getParamNumber() {
		return paramNumber;
	}
	/**
	 * Gets the paramDetails.
	 * @return the paramDetails
	 */
	public String getParamDetails() {
		return description;
	}
	
	public Object asPolyFunction() {
		return polyFunction;
	}
	
	/**
	 * Create a single-parameter function with its name.
	 * @param name
	 * @param function
	 * @param detail
	 * @return
	 */
	public static ExprFunction createSingle(String name,PolyFunctionS function,String description) {
		return new ExprFunction(name, 1, false, description, function);
	}
	
	public static ExprFunction createDouble(String name,boolean sortable,PolyFunctionB function,String description) {
		return new ExprFunction(name, 2, sortable, description, function);
	}
	
	public static ExprFunction createMultiple(String name,int paramNumber,
			boolean sortable,PolyFunctionM function,String description) {
		return new ExprFunction(name, paramNumber, sortable, description, function);
	}
	
	/**
	 * Returns the basic calculator functions.
	 * @param pc
	 * @return
	 */
	public static List<ExprFunction> createBasicCalculatorFunctions(PolyCalculator pc){
		final int number = 15;
		ExprFunction[] fs = new ExprFunction[number];
		fs[0] = createSingle("abs", pc::abs, "Returns the absolute value of the polynomial:|x|");
		fs[1] = createSingle("arccos", pc::arccos, "Returns the arccos value of the polynomial:arccos(x)");
		fs[2] = createSingle("arcsin", pc::arcsin, "Returns the arcsin value of the polynomial:arcsin(x)");
		fs[3] = createSingle("arctan", pc::arctan, "Returns the arctan value of the polynomial:arctan(x)");
		fs[4] = createSingle("cos", pc::cos, "Returns the cos value of the polynomial:cos(x)");
		fs[5] = createSingle("cot", pc::cot, "Returns the cot value of the polynomial:cot(x)");
		fs[6] = createSingle("negate", pc::negate, "Returns -x.");
		fs[7] = createSingle("reciprocal", pc::reciprocal, "Returns 1/x.");
		fs[8] = createSingle("sin", pc::sin, "Returns the sin value of the polynomial:sin(x)");
		fs[9] = createSingle("sqr", pc::squareRoot, "Returns the square root of the polynomial:sqr(x)");
		fs[10] = createSingle("tan", pc::squareRoot, "Returns the tan value of the polynomial:tan(x)");
		fs[11] = createSingle("exp", pc::exp, "Returns the exp value of the polynomial:exp(x)");
		fs[12] = createSingle("ln", pc::ln, "Returns ln(x).");
		fs[13] = createDouble("exp", false, pc::exp, "Returns exp(x,y) = x^y");
		fs[14] = createDouble("log", false, pc::log, "Returns log(x,y). (exp(x,log(x,y)) = y) ");
		return Arrays.asList(fs);
	}
	
	
}

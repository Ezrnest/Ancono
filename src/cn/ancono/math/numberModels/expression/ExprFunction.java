/**
 * 2017-11-24
 */
package cn.ancono.math.numberModels.expression;

import cn.ancono.math.numberModels.MultinomialCalculator;
import cn.ancono.math.numberModels.api.RealCalculator;
import cn.ancono.math.numberModels.expression.simplification.SimplificationStrategy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

;

/**
 * An expression function describes a function that the calculator should
 * recognize and calculate when simplifying an expression. The ExprFunction is
 * a combined class of the function's details(name, number of parameters...) and
 * also the polynomial function in program(optional).
 * <p>
 * Polynomial functions in program is responsible for calculating the function
 * directly to a polynomial result. Generally, a function can be 'computed' via
 * directly applying the function or {@link SimplificationStrategy}.
 * Simplification strategy is more flexible and can apply to more situations
 * when the parameter is not a polynomial. However, it is vital to use
 * polynomial function to simplify the expression to a plain polynomial, which
 * is the most efficient form of an expression and this calculation can be done
 * in the first phase of simplification. So if a function can be calculated to a
 * polynomial result for some parameters, it is recommended to implement a
 * polynomial function.
 * <p>
 * The functions are divided to {@link PolyFunctionS}, {@link PolyFunctionB} and
 * {@link PolyFunctionM}. The reason why the functions are divided is to speed
 * the calculation.
 * <p>
 * There is a basic bundle of expression functions which is already defined in a
 * {@link RealCalculator}.
 *
 * @author liyicheng 2017-11-24 20:04
 */
public class ExprFunction {
    private final String name;
    private final boolean paramOrdered;
    private final int paramNumber;
    private final String description;
    private final Object polyFunction;


    /**
     * @param name         the name of the function
     * @param paramNumber  the size of parameter
     * @param paramOrdered whether the parameters are ordered
     * @param description  a string(or null) to indicate the details of each parameter
     * @param polyFunction a function
     */
    private ExprFunction(String name, int paramNumber, boolean paramOrdered, String description, Object polyFunction) {
        super();
        if (paramNumber <= 0) {
            throw new IllegalArgumentException();
        }
        this.paramNumber = paramNumber;
        this.name = Objects.requireNonNull(name);
        this.paramOrdered = paramOrdered;
        this.description = Objects.requireNonNull(description);
        this.polyFunction = polyFunction;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the paramOrdered.
     *
     * @return the paramOrdered
     */
    public boolean isParamOrdered() {
        return paramOrdered;
    }

    /**
     * Gets the paramNumber.
     *
     * @return the paramNumber
     */
    public int getParamNumber() {
        return paramNumber;
    }

    /**
     * Gets the paramDetails.
     *
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
     *
     * @param name
     * @param function
     * @param description
     * @return
     */
    public static ExprFunction createSingle(String name, PolyFunctionS function, String description) {
        return new ExprFunction(name, 1, false, description, function);
    }

    public static ExprFunction createDouble(String name, boolean sortable, PolyFunctionB function, String description) {
        return new ExprFunction(name, 2, sortable, description, function);
    }

    public static ExprFunction createMultiple(String name, int paramNumber, boolean sortable, PolyFunctionM function,
                                              String description) {
        return new ExprFunction(name, paramNumber, sortable, description, function);
    }


    public static final String FUNCTION_NAME_ABS = "abs";
    public static final String FUNCTION_NAME_ARCCOS = "arccos";
    public static final String FUNCTION_NAME_ARCSIN = "arcsin";
    public static final String FUNCTION_NAME_ARCTAN = "arctan";
    public static final String FUNCTION_NAME_COS = "cos";
    public static final String FUNCTION_NAME_COT = "cot";
    public static final String FUNCTION_NAME_NEGATE = "negate";
    public static final String FUNCTION_NAME_RECIPROCAL = "reciprocal";
    public static final String FUNCTION_NAME_SIN = "sin";
    public static final String FUNCTION_NAME_SQR = "sqr";
    public static final String FUNCTION_NAME_TAN = "tan";
    public static final String FUNCTION_NAME_EXP = "exp";
    public static final String FUNCTION_NAME_LN = "ln";
    public static final String FUNCTION_NAME_LOG = "log";
    public static final String FUNCTION_NAME_SIGMA = "sigma";

    /**
     * Returns the basic calculator functions.
     *
     * @param pc
     * @return
     */
    public static List<ExprFunction> createBasicCalculatorFunctions(MultinomialCalculator pc) {
        final int number = 14;
        var fs = new ArrayList<ExprFunction>();
//        fs[0] = createSingle(FUNCTION_NAME_ABS, pc::abs, "Returns the absolute value of the polynomial:|x|");
        fs.add(createSingle(FUNCTION_NAME_ARCCOS, pc::arccos, "Returns the arccos value of the polynomial:arccos(x)"));
        fs.add(createSingle(FUNCTION_NAME_ARCSIN, pc::arcsin, "Returns the arcsin value of the polynomial:arcsin(x)"));
        fs.add(createSingle(FUNCTION_NAME_ARCTAN, pc::arctan, "Returns the arctan value of the polynomial:arctan(x)"));
        fs.add(createSingle(FUNCTION_NAME_COS, pc::cos, "Returns the cos value of the polynomial:cos(x)"));
        fs.add(createSingle(FUNCTION_NAME_COT, pc::cot, "Returns the cot value of the polynomial:cot(x)"));
        fs.add(createSingle(FUNCTION_NAME_NEGATE, pc::negate, "Returns -x."));
        fs.add(createSingle(FUNCTION_NAME_RECIPROCAL, pc::reciprocal, "Returns 1/x."));
        fs.add(createSingle(FUNCTION_NAME_SIN, pc::sin, "Returns the sin value of the polynomial:sin(x)"));
        fs.add(createSingle(FUNCTION_NAME_SQR, pc::squareRoot, "Returns the square origin of the polynomial:sqr(x)"));
        fs.add(createSingle(FUNCTION_NAME_TAN, pc::tan, "Returns the tan value of the polynomial:tan(x)"));
        fs.add(createSingle(FUNCTION_NAME_EXP, pc::exp, "Returns the exp value of the polynomial:exp(x)"));
        fs.add(createSingle(FUNCTION_NAME_LN, pc::ln, "Returns ln(x)."));
        fs.add(createDouble(FUNCTION_NAME_EXP, false, pc::exp, "Returns exp(x,y) = x^y"));
//        fs[14] = createDouble(FUNCTION_NAME_LOG, false, pc::log, "Returns log(x,y). (exp(x,log(x,y)) = y) ");


        return fs;
    }

    private static final Class<?>[][] TEMP = new Class[3][];

    static {
        TEMP[0] = new Class[0];
        TEMP[1] = new Class[]{Object.class};
        TEMP[2] = new Class[]{Object.class, Object.class};
    }

    @SuppressWarnings("unchecked")
    public static <T> T findFunctionAndApply(RealCalculator<T> mc, String name, T... args) {
        Class<?>[] argClass;
        if (args.length < TEMP.length) {
            argClass = TEMP[args.length];
        } else {
            argClass = new Class<?>[args.length];
            Arrays.fill(argClass, Object.class);
        }
        try {
            Method md = mc.getClass().getMethod(name, argClass);
            md.setAccessible(true);
            return (T) md.invoke(mc, (Object[]) args);
        } catch (Exception e) {
            throw new UnsupportedOperationException("Failed to invoke method", e);
        }
    }


//	public static void main(String[] args){
//		var list = createBasicCalculatorFunctions(Multinomial.getCalculator());
//		for(var f : list){
//			String name = f.getName();
//			print("public static final String FUNCTION_NAME_"+name.toUpperCase()+" = "+"\""+name+"\";");
//		}
//	}

}

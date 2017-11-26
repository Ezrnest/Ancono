/**
 * 2017-11-25
 */
package cn.timelives.java.math.numberModels.expression;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import cn.timelives.java.math.exceptions.UnsupportedCalculationException;
import cn.timelives.java.math.numberModels.PolyCalculator;
import cn.timelives.java.math.numberModels.Polynomial;
import cn.timelives.java.math.numberModels.expression.Node.SingleNode;

/**
 * @author liyicheng
 * 2017-11-25 19:30
 *
 */
public final class ExprFunctionHolder {
	
	private final Map<String,PolyFunctionS> singleFunctions;
	private final Map<String,PolyFunctionB> binaryFunctions;
	private final Map<String,PolyFunctionM> multiFunctions;
	private final Set<ExprFunction> functions;
	
	/**
	 * 
	 */
	public ExprFunctionHolder() {
		singleFunctions = new HashMap<>();
		binaryFunctions = new HashMap<>();
		multiFunctions = new HashMap<>();
		functions = new TreeSet<>((ExprFunction x,ExprFunction y)-> x.getName().compareTo(y.getName()));
	}
	
	public void addExprFunction(ExprFunction f) {
		functions.add(f);
		int n = f.getParamNumber();
		String name = f.getName();
		if(n<=0) {
			throw new IllegalArgumentException();
		}
		Object pf = f.asPolyFunction();
		if(pf != null) {
			if(n == 1) {
				singleFunctions.put(name, (PolyFunctionS)pf);
			}else if(n==2) {
				binaryFunctions.put(name, (PolyFunctionB)pf);
			}else {
				multiFunctions.put(name, (PolyFunctionM)pf);
			}
		}
		
	}
	
	/**
	 * Returns the result of applying the single function, returns {@code null} if there 
	 * is no such function or the function cannot perform calculation.
	 * @param fname
	 * @param x
	 * @return
	 */
	public Polynomial computeSingle(String fname,Polynomial x) {
		PolyFunctionS f = singleFunctions.get(fname);
		if(f == null) {
			return null;
		}
		try {
			return f.apply(x);
		}catch(UnsupportedCalculationException uce) {
			return null;
		}
	}
	
	/**
	 * Returns the result of applying the double function, returns {@code null} if there 
	 * is no such function or the function cannot perform calculation.
	 * @param fname
	 * @param x
	 * @return
	 */
	public Polynomial computeDouble(String fname,Polynomial x,Polynomial y) {
		PolyFunctionB f = binaryFunctions.get(fname);
		if(f == null) {
			return null;
		}
		try {
			return f.apply(x,y);
		}catch(UnsupportedCalculationException uce) {
			return null;
		}
	}
	/**
	 * Returns the result of applying the multiple function, returns {@code null} if there 
	 * is no such function or the function cannot perform calculation.
	 * @param fname
	 * @param x
	 * @return
	 */
	public Polynomial computeMultiple(String fname,Polynomial[] xs) {
		PolyFunctionM f = multiFunctions.get(fname);
		if(f == null) {
			return null;
		}
		try {
			return f.apply(xs);
		}catch(UnsupportedCalculationException uce) {
			return null;
		}
	}
	
	
	public static ExprFunctionHolder getDefaultKit(PolyCalculator pc) {
		ExprFunctionHolder holder = new ExprFunctionHolder();
		List<ExprFunction> fs = ExprFunction.createBasicCalculatorFunctions(pc);
		for(ExprFunction f : fs) {
			holder.addExprFunction(f);
		}
		return holder;
	}
	
}

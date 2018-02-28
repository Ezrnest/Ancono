/**
 * 2018-01-25
 */
package cn.timelives.java.math.equation;

import java.util.List;

/**
 * Equation solver provides a method to solve an equation and returns the roots of the equation 
 * as a list. Normally, an equation solver whose targets are single variable polynomial equation 
 * should return a list whose size is equal to the degree of the equation(which means all n-times roots are 
 * added for n times). 
 * @author liyicheng
 * 2018-01-25 19:38
 *
 */
public interface GeneralEquationSolver<T,R,S extends Equation<T,R>> {
	
	/**
	 * Solves the equation
	 * @param equation
	 * @return
	 */
	public List<R> solve(S equation);
	
}

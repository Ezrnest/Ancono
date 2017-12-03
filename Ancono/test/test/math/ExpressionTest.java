/**
 * 2017-11-29
 */
package test.math;

import static cn.timelives.java.utilities.Printer.print;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import java.util.List;

import cn.timelives.java.math.equation.SVPEquation;
import cn.timelives.java.math.equation.SVPEquation.QEquation;
import cn.timelives.java.math.numberModels.Polynomial;
import cn.timelives.java.math.numberModels.expression.ExprCalculator;
import cn.timelives.java.math.numberModels.expression.Expression;
import cn.timelives.java.math.numberModels.expression.SimplificationStrategies;

/**
 * @author liyicheng
 * 2017-11-29 21:27
 *
 */
public class ExpressionTest {
	ExprCalculator mc = new ExprCalculator();
	Expression cos = mc.cos(Expression.fromPolynomial(Polynomial.valueOf("x"))),
			sin = mc.sin(Expression.fromPolynomial(Polynomial.valueOf("x")));
	Expression a = Expression.fromPolynomial(Polynomial.valueOf("a")),
			b = Expression.fromPolynomial(Polynomial.valueOf("b")),
			c = Expression.fromPolynomial(Polynomial.valueOf("c")),
			x,y,z,w,
			sum = mc.add(a, b);
	/**
	 * 
	 */
	public ExpressionTest() {
	}
	
	public static void printAndList(Expression x) {
		x.listNode();
		print(x);
	}
//	@Test
	public void testFractionSimplify1() {
		Expression x = Expression.fromPolynomial(Polynomial.valueOf("a+b")),
				y = Expression.fromPolynomial(Polynomial.valueOf("a")),
				z = Expression.fromPolynomial(Polynomial.valueOf("b"));
		y = mc.multiply(sin, y);
		z = mc.multiply(sin, z);
		y = mc.divide(y, x);
		z = mc.divide(z, x);
		x = mc.add(y, z);
		assertEquals("((a)*sin(x))/(a+b)+((b)*sin(x))/(a+b) = sin(x)" , x.toString(), "sin(x)");
	}
//	@Test
	public void testFractionSimplify2() {
		Expression x = mc.divide(sin, cos),
				y = mc.divide(cos, sin);
		x = mc.multiply(x, y);
		assertEquals("sinx/cosx * cosx/sinx = 1" , x.toString(), "1");
	}
	
	public void testFractionSimplify3() {
		Expression x = mc.divide(a, sum),
				y = mc.divide(b, mc.add(sum, a));
		x = mc.add(x, y);
		printAndList(x);
		mc.setProperty(SimplificationStrategies.PROP_MERGE_FRACTION, "true");
		x = mc.simplify(x);
		printAndList(x);
		
	}
	public void testFractionSimplify4() {
		Expression x = mc.divide(a, sum),
				y = mc.divide(b, mc.add(sum, a));
//		x = mc.add(x, y);
		x = mc.add(x, a);
		printAndList(x);
		mc.setProperty(SimplificationStrategies.PROP_MERGE_FRACTION, "true");
		x = mc.simplify(x);
		printAndList(x);
		
	}
	@Test
	public void testSqr() {
		SimplificationStrategies.setCalRegularization(mc);
		QEquation<Expression> equation = SVPEquation.quadratic(a, b, c, mc);
		List<Expression> solution = equation.solve();
		x = solution.get(0);
		y = solution.get(1);
		z = mc.add(x, y);
		w = mc.multiply(x, y);
		print(z);
		print(w);
	}
//	@Test
	public void testExp1() {
		SimplificationStrategies.setCalRegularization(mc);
		x = mc.exp(mc.reciprocal(sum));
		y = mc.exp(x, sum);
		printAndList(y);
	}
}

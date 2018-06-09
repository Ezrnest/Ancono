/**
 * 2017-11-29
 */
package test.math;

import cn.timelives.java.math.MathUtils;
import cn.timelives.java.math.equation.SVPEquation;
import cn.timelives.java.math.equation.SVPEquation.QEquation;
import cn.timelives.java.math.numberModels.Multinomial;
import cn.timelives.java.math.numberModels.expression.ExprCalculator;
import cn.timelives.java.math.numberModels.expression.Expression;
import cn.timelives.java.math.numberModels.expression.SimplificationStrategies;
import org.junit.Test;

import java.util.List;

import static cn.timelives.java.utilities.Printer.print;
import static org.junit.Assert.assertEquals;

/**
 * @author liyicheng
 * 2017-11-29 21:27
 *
 */
public class ExpressionTest {
	ExprCalculator mc = new ExprCalculator();
	Expression cos = mc.cos(Expression.fromMultinomial(Multinomial.valueOf("x"))),
			sin = mc.sin(Expression.fromMultinomial(Multinomial.valueOf("x")));
	Expression a = Expression.fromMultinomial(Multinomial.valueOf("a")),
			b = Expression.fromMultinomial(Multinomial.valueOf("b")),
			c = Expression.fromMultinomial(Multinomial.valueOf("c")),
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
	@Test
	public void testFractionSimplify1() {
		Expression x = Expression.fromMultinomial(Multinomial.valueOf("a+b")),
				y = Expression.fromMultinomial(Multinomial.valueOf("a")),
				z = Expression.fromMultinomial(Multinomial.valueOf("b"));
		y = mc.multiply(sin, y);
		z = mc.multiply(sin, z);
		y = mc.divide(y, x);
		z = mc.divide(z, x);
		x = mc.add(y, z);
		assertEquals("((a)*sin(x))/(a+b)+((b)*sin(x))/(a+b) = sin(x)" , x.toString(), "sin(x)");
	}
	@Test
	public void testFractionSimplify2() {
		Expression x = mc.divide(sin, cos),
				y = mc.divide(cos, sin);
		x = mc.multiply(x, y);
		assertEquals("sinx/cosx * cosx/sinx = 1" , x.toString(), "1");
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
	@Test
	public void testExp1() {
		SimplificationStrategies.setCalRegularization(mc);
		x = mc.exp(mc.reciprocal(sum));
		y = mc.exp(x, sum);
		assertEquals(y.toString(),"e");
	}

	@Test
    public void testMerge(){
        SimplificationStrategies.setCalRegularization(mc);
        Expression x = mc.squareRoot(Expression.fromMultinomial(Multinomial.valueOf("a+b"))),
                y = Expression.fromMultinomial(Multinomial.valueOf("a")),
                z = Expression.fromMultinomial(Multinomial.valueOf("b"));
        printAndList(mc.add(mc.multiply(x,y),mc.multiply(x,z)));
    }

    @Test
    public void test1(){
        SimplificationStrategies.setCalRegularization(mc);
        print(MathUtils.solveEquation(a,b,c,mc));
    }

    @Test
    public void test2(){
        Expression x = mc.squareRoot(Expression.fromMultinomial(Multinomial.valueOf("a+b"))),
                y = Expression.fromMultinomial(Multinomial.valueOf("a"));
        print(mc.negate(mc.multiply(x,y)));
    }


}

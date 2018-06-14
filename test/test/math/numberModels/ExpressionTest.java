/**
 * 2017-11-29
 */
package test.math.numberModels;

import cn.timelives.java.math.MathUtils;
import cn.timelives.java.math.equation.SVPEquation;
import cn.timelives.java.math.equation.SVPEquation.QEquation;
import cn.timelives.java.math.numberModels.Calculators;
import cn.timelives.java.math.numberModels.Multinomial;
import cn.timelives.java.math.numberModels.expression.ExprCalculator;
import cn.timelives.java.math.numberModels.expression.Expression;
import cn.timelives.java.math.numberModels.expression.SimplificationStrategies;
import cn.timelives.java.math.geometry.analytic.planeAG.Line;
import cn.timelives.java.math.geometry.analytic.planeAG.Point;
import cn.timelives.java.math.geometry.analytic.planeAG.curve.EllipseV;
import org.junit.Test;

import java.util.List;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

import static cn.timelives.java.utilities.Printer.print;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static test.math.TestUtils.assertMathEquals;

/**
 * @author liyicheng
 * 2017-11-29 21:27
 *
 */
public class ExpressionTest {
	ExprCalculator mc = new ExprCalculator();
	Expression cos = mc.cos(valueOf("x")),
			sin = mc.sin(valueOf("x"));
	Expression a = valueOf("a"),
			b = valueOf("b"),
			c = valueOf("c"),
			x = valueOf("x"),y = valueOf("y")
			,z,w,
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
	Expression valueOf(String expr){
	    return mc.parseExpr(expr);
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
        var equation = QEquation.quadratic(a,b,c,mc);
        var list = equation.solve();
        assertTrue("x1+x2= -b/a",mc.isEqual(
        		mc.add(list.get(0),list.get(1)),
				equation.rootsSum()));
    }

    @Test
    public void test2(){
		SimplificationStrategies.setCalRegularization(mc);
		var ell = EllipseV.standardEquation(a,b,true,mc);
		var k = mc.parseExpr("k");
		var d = mc.parseExpr("d");
		var l1 = Line.slopeIntercept(k,d,mc);
		var list = ell.intersectPoints(l1);
		var A = list.get(0);
		var B = list.get(1);
		var M = A.middle(B);
		var l2 = Line.parallelY(M.x,mc);
		var P1 = Point.valueOf(x,ell.computeY(x),mc);
		var P2 = Point.valueOf(P1.x,mc.negate(P1.y),mc);
		var AP1 = Line.twoPoint(A,P1);
		var BP2 = Line.twoPoint(B,P2);
		var E = AP1.intersectPoint(l2);
		var F = BP2.intersectPoint(l2);
		var result = mc.multiply(mc.subtract(E.y,M.y),mc.subtract(F.y,M.y));
		var resultShouldBe = mc.parseExpr("(-a^2*b^2*k^2-b^4+b^2*d^2)/(a^2*k^2+b^2)");
		assertTrue("",mc.isEqual(result,resultShouldBe));
    }

    @Test
	public void testFractionSimplify3(){
		SimplificationStrategies.setCalRegularization(mc);
		a = valueOf("1+sin(x)");
		b = valueOf("2+2sin(x)");
		print(mc.divide(a,b));
	}

	@Test
	public void testSubstitute(){
		SimplificationStrategies.setCalRegularization(mc);
		Expression expr = mc.parseExpr("(a+b)/(a-b)+(a+2b)/(a-b)");
		assertMathEquals(expr,valueOf("(2a+3b)/(a-b)"),mc);
		assertMathEquals(
				mc.substitute(expr,"a",valueOf("exp(a,2)")),
				valueOf("(2a^2+3b)/(a^2-b)"),mc);
		Function<String,Expression> f = x ->{
			switch(x){
				case "a" : return valueOf("x");
				case "b" : return valueOf("y");
			}
			return valueOf("1");
		};
		ToDoubleFunction<String> f2 = x->{
			switch(x){
				case "a" : return 5d;
				case "b" : return 2d;
				case "x" : return Math.PI/2;
			}
			return 1d;
		};
        assertMathEquals(expr.compute(f,mc),valueOf("(2x+3y)/(x-y)"),mc);
        assertMathEquals(expr.computeDouble(f2),16d/3,Calculators.getCalculatorDoubleDev());
        assertMathEquals(mc.parseExpr("sin(x)").computeDouble(f2),1d,Calculators.getCalculatorDoubleDev());
	}


}

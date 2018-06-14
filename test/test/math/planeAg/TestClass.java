/**
 * 
 */
package test.math.planeAg;

import cn.timelives.java.math.numberModels.Calculators;
import cn.timelives.java.math.numberModels.MathCalculator;
import cn.timelives.java.math.geometry.analytic.planeAG.PAffineTrans;
import cn.timelives.java.math.geometry.analytic.planeAG.PVector;
import cn.timelives.java.math.geometry.analytic.planeAG.Point;
import cn.timelives.java.math.geometry.analytic.planeAG.curve.ConicSection;
import cn.timelives.java.math.geometry.analytic.planeAG.curve.GeneralConicSection;
import org.junit.Test;

import static cn.timelives.java.utilities.Printer.print;
import static org.junit.Assert.assertTrue;

/**
 * @author liyicheng
 *
 */
public class TestClass {
	/**
	 * 
	 */
	public TestClass() {
	}
	MathCalculator<Double> mcd = Calculators.getCalculatorDoubleDev();
	@Test
	public void testAffineTrans(){
		PAffineTrans<Double> pt = PAffineTrans.identity(mcd);
		ConicSection<Double> cs = GeneralConicSection.ellipse(1d, 2d, -3d, mcd);
		Point<Double> p = Point.valueOf(1d, 1d, mcd);
		print(cs);
		assertTrue("contains:",cs.contains(p));
		pt = pt.translate(PVector.valueOf(1d, 1d, mcd));
		cs = cs.transform(pt);
		print(pt);
		print(cs);
		print(pt.apply(p));
		print(cs.contains(pt.apply(p)));
		print("Test completed!");
	}
	
	
}

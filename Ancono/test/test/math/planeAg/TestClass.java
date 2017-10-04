/**
 * 
 */
package test.math.planeAg;

import static cn.timelives.java.utilities.Printer.print;
import static org.junit.Assert.*;

import org.junit.Test;

import cn.timelives.java.math.MathCalculatorHolder;
import cn.timelives.java.math.numberModels.MathCalculator;
import cn.timelives.java.math.numberModels.MathCalculatorAdapter;
import cn.timelives.java.math.planeAG.PAffineTrans;
import cn.timelives.java.math.planeAG.PVector;
import cn.timelives.java.math.planeAG.Point;
import cn.timelives.java.math.planeAG.curve.ConicSection;
import cn.timelives.java.math.planeAG.curve.GeneralConicSection;

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
	MathCalculator<Double> mcd = MathCalculatorAdapter.getCalculatorDoubleDev();
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

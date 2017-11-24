/**
 * 2017-10-05
 */
package test.math;

import static cn.timelives.java.utilities.Printer.print;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import java.util.Arrays;

import cn.timelives.java.math.numberModels.Calculators;
import cn.timelives.java.math.numberModels.MathCalculator;
import cn.timelives.java.math.numberModels.MathCalculatorAdapter;
import cn.timelives.java.math.set.IntersectableSet;
import cn.timelives.java.math.set.Interval;
import cn.timelives.java.math.set.IntervalUnion;
import cn.timelives.java.math.set.MathSet;

/**
 * @author liyicheng
 * 2017-10-05 13:35
 *
 */
public class TestSet {

	/**
	 * 
	 */
	public TestSet() {
	}
	private <T> void printContains(MathSet<T> set,T e) {
		print(set.toString()+" contains "+e+" = "+set.contains(e));
	}
	private <S extends IntersectableSet<?, S>> void printIntersect(S set1,S set2) {
		print(set1.toString()+" ∩ "+set2.toString()+" = "+set1.intersect(set2));
	}
	
	MathCalculator<Integer> mc = Calculators.getCalculatorInteger();
	
	
	public void testInterval() {
		Interval<Integer> v1,v2,v3;
		v1 = Interval.closedInterval(0, 2, mc);
		v2 = Interval.closedInterval(2, 4, mc);
		v3 = Interval.closedInterval(2, 2, mc);
//		v4 = Interval.closedInterval(0, 2, mc);
		printContains(v3,2);
		printContains(v3,3);
		printIntersect(v1,v2);
		v1 = Interval.openInterval(0, 2, mc);
		v2 = Interval.openInterval(2, 4, mc);
		v3 = Interval.openInterval(1, 3, mc);
		printIntersect(v1,v2);
		printIntersect(v1,v3);
		v2 = Interval.leftClosedRightOpen(2, 4, mc);
		v3 = Interval.leftClosedRightOpen(1, 4, mc);
		printIntersect(v1,v2);
		printIntersect(v1,v3);
//		assertTrue("[0,2] intersect [2,4] shoule be [2,2]", v3.valueEquals(v1.intersect(v2)));
	}
//	@Test
	public void testIntervalUnion() {
		Interval<Integer> v1,v2,v3;
		v1 = Interval.closedInterval(0, 2, mc);
		v2 = Interval.closedInterval(-2, 4, mc);
		v3 = Interval.closedInterval(5, 7, mc);
		printIntervalUnion(v1,v2,v3);
		v1 = Interval.leftOpenRightClosed(0, 2, mc);
		v2 = Interval.leftOpenRightClosed(2, 4, mc);
		printIntervalUnion(v1,v2,v3);
		v1 = Interval.openInterval(0, 2, mc);
		v2 = Interval.openInterval(2, 4, mc);
		v3 = Interval.closedInterval(2, 2, mc);
		printIntervalUnion(v1,v2);
		printIntervalUnion(v1,v2,v3);
		v1 = Interval.fromNegativeInf(1, false, mc);
		printIntervalUnion(v1,v2);
	}
	
	@Test
	public void testIntervalUnion2() {
		Interval<Integer> v1,v2,v3;
		v1 = Interval.closedInterval(0, 2, mc);
		v2 = Interval.closedInterval(-2, 4, mc);
		v3 = Interval.closedInterval(5, 7, mc);
		IntervalUnion<Integer> in1 = IntervalUnion.valueOf(v1, v2,v3);
		v1 = Interval.openInterval(1, 2, mc);
		v2 = Interval.openInterval(2, 6, mc);
		v3 = Interval.closedInterval(2, 2, mc);
		IntervalUnion<Integer> in2 = IntervalUnion.valueOf(v1, v2,v3);
		assertEquals("Interval intersect "+in1.toString()+" and "+in2.toString(), "(1,4]∪[5,6)", in1.intersect(in2).toString());
	}
	
	@Test
	public void testIntervalUnion3() {
		Interval<Integer> v1,v2,v3;
		v1 = Interval.closedInterval(0, 2, mc);
		v2 = Interval.closedInterval(-2, 4, mc);
		v3 = Interval.closedInterval(5, 7, mc);
		IntervalUnion<Integer> in1 = IntervalUnion.valueOf(v1, v2,v3);
		v1 = Interval.openInterval(1, 2, mc);
		v2 = Interval.openInterval(2, 6, mc);
		v3 = Interval.closedInterval(2, 2, mc);
		IntervalUnion<Integer> in2 = IntervalUnion.valueOf(v1, v2,v3);
		assertTrue("S union complement(S) is universe", IntervalUnion.universe(mc).valueEquals(in1.union(in1.complement())));
		assertTrue("S union complement(S) is universe", IntervalUnion.universe(mc).valueEquals(in2.union(in2.complement())));
		assertTrue("The complement of universe is empty", IntervalUnion.universe(mc).complement().valueEquals(IntervalUnion.empty(mc)));
//		print(IntervalUnion.empty(mc).complement());
		assertTrue("The complement of empty is universe", IntervalUnion.empty(mc).complement().valueEquals(IntervalUnion.universe(mc)));
	}
	
	@SafeVarargs
	private final static <T> void printIntervalUnion(Interval<T>...intervals) {
		IntervalUnion<T> in = IntervalUnion.valueOf(Arrays.asList(intervals));
		print("The union of: "+Arrays.toString(intervals));
		print("= "+in);
	}
}

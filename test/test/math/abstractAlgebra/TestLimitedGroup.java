/**
 * 2018-02-27
 */
package test.math.abstractAlgebra;

import cn.timelives.java.math.abstractAlgebra.FiniteGroups;
import cn.timelives.java.math.abstractAlgebra.calculator.GroupCalculator;
import cn.timelives.java.math.abstractAlgebra.group.finite.AbstractFiniteGroup;
import cn.timelives.java.math.numberModels.Calculators;
import cn.timelives.java.math.numberModels.MathCalculator;
import cn.timelives.java.math.planeAG.TransMatrix;
import cn.timelives.java.utilities.ArraySup;
import org.junit.Test;

import java.util.function.Function;

import static cn.timelives.java.utilities.Printer.print;
import static cn.timelives.java.utilities.Printer.printMatrix;

/**
 * @author liyicheng
 * 2018-02-27 19:16
 *
 */
public class TestLimitedGroup {

	/**
	 * 
	 */
	public TestLimitedGroup() {
	}
	
	
	@Test
	public void test1() {
		MathCalculator<Integer> mc = Calculators.getCalculatorInteger();
		GroupCalculator<TransMatrix<Integer>> matmc = new GroupCalculator<TransMatrix<Integer>>() {
			@Override
			public boolean isEqual(TransMatrix<Integer> x, TransMatrix<Integer> y) {
				return x.valueEquals(y);
			}
			
			@Override
			public TransMatrix<Integer> apply(TransMatrix<Integer> x, TransMatrix<Integer> y) {
				return y.andThen(x);
			}
			
			@Override
			public TransMatrix<Integer> getIdentity() {
				return TransMatrix.identityTrans(mc);
			}
			
			@Override
			public TransMatrix<Integer> inverse(TransMatrix<Integer> x) {
				return x.inverse();
			}
		}; 
		AbstractFiniteGroup<TransMatrix<Integer>> g = FiniteGroups.createGroup(matmc, 
				TransMatrix.flipX(mc),
				TransMatrix.flipY(mc),
				TransMatrix.centralSymmetry(mc),
				TransMatrix.flipXY(mc));
		TransMatrix<Integer>[][] table = FiniteGroups.generateGroupTable(g);
		String names = "eabcmpqn";
		Function<TransMatrix<Integer>,String> namer = x -> {
			for(int i=1;i<table.length;i++) {
				if(matmc.isEqual(table[0][i],x)) {
					return names.substring(i-1, i);
				}
			}
			return "-";
		};
		String[][] str = ArraySup.mapTo2(table, namer,String.class);
		printMatrix(str);
		for(int i=0;i<str.length-1;i++) {
			print(names.charAt(i)+":");
			table[0][i+1].printMatrix();
		}
	}
}

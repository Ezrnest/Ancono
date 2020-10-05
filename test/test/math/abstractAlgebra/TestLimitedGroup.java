/**
 * 2018-02-27
 */
package test.math.abstractAlgebra;

import cn.ancono.math.algebra.abs.FiniteGroups;
import cn.ancono.math.algebra.abs.calculator.GroupCalculator;
import cn.ancono.math.algebra.abs.group.finite.AbstractFiniteGroup;
import cn.ancono.math.numberModels.Calculators;
import cn.ancono.math.MathCalculator;
import cn.ancono.math.geometry.analytic.plane.TransMatrix;
import cn.ancono.utilities.ArraySup;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.function.Function;

import static cn.ancono.utilities.Printer.print;
import static cn.ancono.utilities.Printer.printMatrix;

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
        MathCalculator<Integer> mc = Calculators.getCalInteger();
        GroupCalculator<TransMatrix<Integer>> matmc = new GroupCalculator<>() {
            @Override
            public boolean isCommutative() {
                return false;
            }


            @Override
            public boolean isEqual(TransMatrix<Integer> x, @NotNull TransMatrix<Integer> y) {
                return x.valueEquals(y);
            }

            @Override
            public TransMatrix<Integer> apply(@NotNull TransMatrix<Integer> x, TransMatrix<Integer> y) {
                return y.andThen(x);
            }

            @Override
            public TransMatrix<Integer> getIdentity() {
                return TransMatrix.identityTrans(mc);
            }

            @NotNull
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
			for(int i=0;i<table.length;i++) {
				if(matmc.isEqual(table[0][i],x)) {
					return names.substring(i, i+1);
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

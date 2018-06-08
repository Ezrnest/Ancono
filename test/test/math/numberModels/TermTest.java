package test.math.numberModels;

import cn.timelives.java.math.MathUtils;
import cn.timelives.java.math.numberModels.Term;
import org.junit.Test;

import static cn.timelives.java.utilities.Printer.print;
import static cn.timelives.java.utilities.Printer.printnb;
import static org.junit.Assert.*;

public class TermTest {

    @Test
    public void compareTo() {
        Term[] ts = new Term[]{
                Term.valueOf("ax"),
                Term.valueOf("bx^2"),
                Term.valueOf("x^2"),
                Term.valueOf("-xy"),
                Term.valueOf("2xy"),
                Term.valueOf("-Sqr2"),
                Term.valueOf("-1"),
                Term.valueOf("Sqr3"),
                Term.valueOf("2"),
                Term.valueOf("Sqr7"),
                Term.valueOf("1/x"),
        };
        for(int i=0;i<ts.length;i++){
            for(int j=0;j<ts.length;j++){
                int comp = ts[i].compareTo(ts[j]);
                if(!MathUtils.sameSignum(i-j,comp)){
                    printnb("Compare:");
                    print(ts[i]+" and "+ts[j]);
                    printnb("Should be "+(i-j));
                    print(" ,but is "+comp);
                    throw new AssertionError();
                }
            }
        }
    }

    /**
     * Term t1 = Term.valueOf("2a/5*Sqr90"),
     *                 t2 = valueOf("2b^2/3*Sqr3"),
     *                 t3 = valueOf("a^2"),
     *                 t4 = valueOf("a").reciprocal();
     * 		print(t1);
     * 		print(t2);
     * 		print(t1.multiply(t2));
     * 		print(t1.divide(t2));
     */
    @Test
    public void compareChar() {
    }

    @Test
    public void compareNumber() {
    }
}
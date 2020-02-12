package test.math.numberModels;

import cn.timelives.java.math.MathUtils;
import cn.timelives.java.math.numberModels.Multinomial;
import org.junit.Test;

import static cn.timelives.java.math.numberModels.Multinomial.valueOf;
import static cn.timelives.java.utilities.Printer.print;
import static org.junit.Assert.*;
public class MultinomialTest {

    @Test
    public void negate() {
        assertEquals(valueOf("5a-3b").negate(),valueOf("-5a+3b"));
    }

    @Test
    public void add() {
        assertEquals(valueOf("4xy+5y").add(valueOf("-3xy+x^2")),valueOf("x^2+xy+5y"));
    }

    @Test
    public void subtract() {

    }

    @Test
    public void add1() {
    }

    @Test
    public void subtract1() {
    }

    @Test
    public void multiply() {
    }

    @Test
    public void pow() {
    }

    @Test
    public void primarySymmetry(){
        var re = Multinomial.primarySymmetry(3,"abcde".split(""));
        assertEquals("abc+abd+abe+acd+ace+ade+bcd+bce+bde+cde",re.toString());
        re = Multinomial.primarySymmetry(6,"abcdef".split(""));
        assertEquals("abcdef",re.toString());
    }

    @Test
    public void newton(){
        var re = Multinomial.newtonMultinomial(3,"a","b","c");
        assertEquals("a^3+b^3+c^3",re.toString());
    }

    @Test
    public void isSymmetry(){
        var m = Multinomial.valueOf("ab+bc+ca");
        assertTrue(m.isSymmetry());
        m = Multinomial.valueOf("ab+bc+cd");
        assertTrue(!m.isSymmetry());
    }

    @Test
    public void primarySymmetryReduce(){
        var m = Multinomial.valueOf("a^2b+a^2c+ab^2+ac^2+b^2c+bc^2");
        print(m);
        print(m.primarySymmetryReduce());
    }
}
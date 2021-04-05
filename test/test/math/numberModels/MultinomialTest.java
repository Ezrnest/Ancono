package test.math.numberModels;

import cn.ancono.math.numberModels.Multinomial;
import org.junit.Test;

import static cn.ancono.math.numberModels.Multinomial.of;
import static cn.ancono.utilities.Printer.print;
import static org.junit.Assert.*;

public class MultinomialTest {

    @Test
    public void negate() {
        assertEquals(Multinomial.parse("5a-3b").negate(), Multinomial.parse("-5a+3b"));
    }

    @Test
    public void add() {
        assertEquals(Multinomial.parse("4xy+5y").add(Multinomial.parse("-3xy+x^2")), Multinomial.parse("x^2+xy+5y"));
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
    public void primarySymmetry() {
        var re = Multinomial.primarySymmetry(3, "abcde".split(""));
        assertEquals("abc+abd+abe+acd+ace+ade+bcd+bce+bde+cde", re.toString());
        re = Multinomial.primarySymmetry(6, "abcdef".split(""));
        assertEquals("abcdef", re.toString());
    }

    @Test
    public void newton() {
        var re = Multinomial.newtonMultinomial(3, "a", "b", "c");
        assertEquals("a^3+b^3+c^3", re.toString());
    }

    @Test
    public void isSymmetry() {
        var m = Multinomial.parse("ab+bc+ca");
        assertTrue(m.isSymmetry());
        m = Multinomial.parse("ab+bc+cd");
        assertFalse(m.isSymmetry());
    }

    @Test
    public void primarySymmetryReduce() {
        var m = Multinomial.parse("a^2b+a^2c+ab^2+ac^2+b^2c+bc^2");
//        print(m);
//        print(m.primarySymmetryReduce());
        assertEquals("σ1σ2-3σ3", m.primarySymmetryReduce().toString());
    }

    @Test
    public void simplifyFraction() {
        var m1 = Multinomial.parse("xy+x");
        var m2 = Multinomial.parse("y+1");
        var f = Multinomial.simplifyFraction(m1, m2);
        assertEquals("(xy+x)/(y+1) = x/1", Multinomial.parse("x"), f.getFirst());
        assertEquals("(xy+x)/(y+1) = x/1", Multinomial.ONE, f.getSecond());
        f = Multinomial.simplifyFraction(Multinomial.ZERO, m1);
        assertEquals("0/(xy+x) = 0/1", Multinomial.ZERO, f.getFirst());
        assertEquals("0/(xy+x) = 0/1", Multinomial.ONE, f.getSecond());
        m1 = Multinomial.parse("-x");
        m2 = Multinomial.parse("-y");
        f = Multinomial.simplifyFraction(m1, m2);
        assertEquals("(-x)/(-y) = x/y", m1.negate(), f.getFirst());
        assertEquals("(-x)/(-y) = x/y", m2.negate(), f.getSecond());

    }

    @Test
    public void simplifyFraction2() {
        var m1 = Multinomial.parse("-a^2*d^-2*k^2-b^2*d^-2+1");
        var m2 = Multinomial.parse("a^2*b^-2*d^-2*k^2+d^-2");
        print(Multinomial.simplifyFraction(m1, m2));
    }
    //

    @Test
    public void reciprocalSqr() {
        var m = Multinomial.parse("1+Sqr2+Sqr3+Sqr6");
        var m1 = m.reciprocal();
        assertEquals("m*(1/m) = 1", m.multiply(m1), Multinomial.ONE);
    }

    @Test
    public void gcd1() {
        var m1 = Multinomial.parse("xy+x");
        var m2 = Multinomial.parse("y^2+2y+1");
        print(Multinomial.gcd(m1, m2));
    }

    @Test
    public void gcd2() {
        var m1 = Multinomial.parse("x^-1");
        var m2 = Multinomial.parse("xy^-2");
        print(Multinomial.gcd(m1, m2));
    }

}
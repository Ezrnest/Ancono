package test.math.numberModels;

import cn.timelives.java.math.MathUtils;
import cn.timelives.java.math.numberModels.Multinomial;
import org.junit.Test;

import static cn.timelives.java.math.numberModels.Multinomial.valueOf;
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
}
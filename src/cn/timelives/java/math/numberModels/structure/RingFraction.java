package cn.timelives.java.math.numberModels.structure;


import cn.timelives.java.math.FlexibleMathObject;
import cn.timelives.java.math.abstractAlgebra.calculator.RingCalculator;
import cn.timelives.java.math.numberModels.NumberFormatter;

/**
 * A ring fraction is created from a type T which can be
 * a ring. The fraction itself and the corresponding operations
 * consists a field called the fraction field.
 */
public class RingFraction<T> extends FlexibleMathObject<T,RingCalculator<T>> {

    RingFraction(RingCalculator<T> mc){
        super(mc);
    }

    @Override
    public String toString(NumberFormatter<T> nf) {
        return null;
    }
}

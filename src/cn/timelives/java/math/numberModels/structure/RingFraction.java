package cn.timelives.java.math.numberModels.structure;


import cn.timelives.java.math.FlexibleMathObject;
import cn.timelives.java.math.algebra.abstractAlgebra.calculator.RingCalculator;
import cn.timelives.java.math.numberModels.NumberFormatter;

/**
 * A ring fraction is created from a type T which can be
 * a ring. The fraction itself and the corresponding operations
 * consists a field called the fraction field.
 */
public class RingFraction<T> extends FlexibleMathObject<T,RingCalculator<T>> {

    final T nume,deno;


    RingFraction(T nume,T deno,RingCalculator<T> mc){
        super(mc);
        this.nume = nume;
        this.deno = deno;
    }

    @Override
    public String toString(NumberFormatter<T> nf) {

        return null;
    }


}

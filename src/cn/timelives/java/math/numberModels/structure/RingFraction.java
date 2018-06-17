package cn.timelives.java.math.numberModels.structure;


import cn.timelives.java.math.FlexibleMathObject;
import cn.timelives.java.math.algebra.abstractAlgebra.calculator.FieldCalculator;
import cn.timelives.java.math.algebra.abstractAlgebra.calculator.RingCalculator;
import cn.timelives.java.math.exceptions.ExceptionUtil;
import cn.timelives.java.math.numberModels.api.FlexibleNumberFormatter;
import cn.timelives.java.math.numberModels.api.Simplifier;

import java.util.Objects;
import java.util.function.Function;

/**
 * A ring fraction is created from a type T which can be
 * a ring. The fraction itself and the corresponding operations
 * consists a field called the fraction field.
 * <P>
 * A ring fraction consists of a numerator and a denominator, and can be
 * denoted as (n,d). The equivalent relation of two fraction (n1,d1) and (n2,d2)
 * is that {@code n1*d2 = n2*d1}.
 * <p>
 * <h3>Notice:</h3>
 * To operate the ring fraction, the corresponding ring fraction calculator must be used, which
 * supports the basic operations defined on a field calculator.
 * <a href="https://en.wikipedia.org/wiki/Field_of_fractions">Field of fractions</a>
 */
public class RingFraction<T> extends FlexibleMathObject<T,RingCalculator<T>>{

    final T nume,deno;

    RingFraction(T nume,T deno,RingCalculator<T> mc){
        super(mc);
        this.nume = Objects.requireNonNull(nume);
        this.deno = Objects.requireNonNull(deno);
    }

    /**
     * Gets the denominator of the fraction.
     * @return denominator
     */
    public T getDeno() {
        return deno;
    }

    /**
     * Gets the numerator of the fraction.
     * @return numerator
     */
    public T getNume() {
        return nume;
    }

    @Override
    public String toString(FlexibleNumberFormatter<T, RingCalculator<T>> nf) {
        return "(" + nf.format(nume, mc) +
                ")/(" + nf.format(deno, mc) +
                ')';
    }

    public <N> RingFraction<N> mapTo(Function<T, N> mapper,RingCalculator<N> ringCalculator) {
        return new RingFraction<>(mapper.apply(nume),mapper.apply(deno),ringCalculator);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this){
            return true;
        }
        if(! (obj instanceof RingFraction)){
            return false;
        }
        RingFraction<?> rf = (RingFraction)obj;
        return nume.equals(rf.nume) && deno.equals(rf.deno);
    }


    /**
     * Gets a calculator for fraction field.
     * @param ringCalculator
     * @param simplifier
     * @param noneZero
     * @param <T>
     * @return
     */
    public static <T> RFCalculator<T> getCalculator(RingCalculator<T> ringCalculator,Simplifier<T> simplifier,T noneZero){
        return new RFCalculator<>(ringCalculator,simplifier,noneZero);
    }

    public static <T> RingFraction<T> valueOf(T nume,T deno,RingCalculator<T> mc){
        if(mc.isEqual(deno,mc.getZero())){
            ExceptionUtil.divideByZero();
        }
        return new RingFraction<>(nume,deno,mc);
    }

    public static class RFCalculator<T> implements FieldCalculator<RingFraction<T>>{
        final RingCalculator<T> mc;
        final Simplifier<T> sim;
        final RingFraction<T> zero,one;

        RFCalculator(RingCalculator<T> mc, Simplifier<T> sim,T nonZero) {
            this.mc = Objects.requireNonNull(mc);
            this.sim = sim;
            zero = new RingFraction<>(mc.getZero(),nonZero,mc);
            one = new RingFraction<>(nonZero,nonZero,mc);
        }

        public RingFraction<T> valueOf(T nume,T deno){
            if(mc.isEqual(deno,mc.getZero())){
                ExceptionUtil.divideByZero();
            }
            if(sim != null){
                var pair = sim.simplify(nume,deno);
                nume = pair.getFirst();
                deno = pair.getSecond();
            }
            return new RingFraction<>(nume,deno,mc);
        }

        @Override
        public RingFraction<T> getZero() {
            return zero;
        }

        @Override
        public RingFraction<T> add(RingFraction<T> x, RingFraction<T> y) {
            T n = mc.add(mc.multiply(x.nume,y.deno),mc.multiply(x.deno,y.nume));
            T d = mc.multiply(x.deno,y.deno);
            if(sim != null){
                var pair = sim.simplify(n,d);
                n = pair.getFirst();
                d = pair.getSecond();
            }
            return new RingFraction<>(n,d,mc);
        }

        @Override
        public RingFraction<T> negate(RingFraction<T> x) {
            return new RingFraction<>(mc.negate(x.nume),x.deno,mc);
        }

        @Override
        public RingFraction<T> subtract(RingFraction<T> x, RingFraction<T> y) {
            T n = mc.subtract(mc.multiply(x.nume,y.deno),mc.multiply(x.deno,y.nume));
            T d = mc.multiply(x.deno,y.deno);
            if(sim != null){
                var pair = sim.simplify(n,d);
                n = pair.getFirst();
                d = pair.getSecond();
            }
            return new RingFraction<>(n,d,mc);
        }

        @Override
        public RingFraction<T> multiply(RingFraction<T> x, RingFraction<T> y) {
            T n = mc.multiply(x.nume,y.nume);
            T d = mc.multiply(x.deno,y.deno);
            if(sim != null){
                var pair = sim.simplify(n,d);
                n = pair.getFirst();
                d = pair.getSecond();
            }
            return new RingFraction<>(n,d,mc);
        }

        @Override
        public RingFraction<T> reciprocal(RingFraction<T> x) {
            if(mc.isEqual(x.nume,mc.getZero())){
                ExceptionUtil.divideByZero();
            }
            return new RingFraction<>(x.deno,x.nume,mc);
        }

        @Override
        public RingFraction<T> divide(RingFraction<T> x, RingFraction<T> y) {

            T n = mc.multiply(x.nume,y.deno);
            T d = mc.multiply(x.deno,y.nume);
            if(mc.isEqual(d,mc.getZero())){
                ExceptionUtil.divideByZero();
            }
            if(sim != null){
                var pair = sim.simplify(n,d);
                n = pair.getFirst();
                d = pair.getSecond();
            }
            return new RingFraction<>(n,d,mc);
        }

        @Override
        public RingFraction<T> getOne() {
            return one;
        }

        @Override
        public boolean isEqual(RingFraction<T> x, RingFraction<T> y) {
            return mc.isEqual(mc.multiply(x.nume,y.deno),mc.multiply(x.deno,y.nume));
        }
    }

}

package cn.timelives.java.math.algebra.abstractAlgebra.calculator.javaImpl;

import cn.timelives.java.math.algebra.abstractAlgebra.calculator.SemigroupCalculator;
import org.jetbrains.annotations.NotNull;

/*
 * Created at 2018/10/8 21:22
 * @author  liyicheng
 */
@SuppressWarnings("unchecked")
public interface JSemigroupCalculator<T> extends SemigroupCalculator<T> {
    @NotNull
    @Override
    default T plus(@NotNull T $receiver, @NotNull T y) {
        return (T) DefaultImpls.plus(this,$receiver,y);
    }

    @NotNull
    @Override
    default T gpow(@NotNull T x, long n) {
        return (T) DefaultImpls.gpow(this,x,n);
    }

    @NotNull
    @Override
    default T times(long $receiver, @NotNull T x) {
        return (T) DefaultImpls.times(this,$receiver,x);
    }

    @NotNull
    @Override
    default T times(@NotNull T $receiver, long n) {
        return (T) DefaultImpls.times(this,$receiver,n);
    }


}

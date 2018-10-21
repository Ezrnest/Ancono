package cn.timelives.java.math.algebra.abstractAlgebra.calculator.javaImpl;

import cn.timelives.java.math.algebra.abstractAlgebra.calculator.GroupCalculator;
import cn.timelives.java.math.algebra.abstractAlgebra.calculator.RingCalculator;
import org.jetbrains.annotations.NotNull;

/*
 * Created at 2018/10/8 21:28
 * @author  liyicheng
 */
@SuppressWarnings("unchecked")
public interface JRingCalculator<T> extends JGroupCalculator<T>, RingCalculator<T> {



    @Override
    default boolean isCommutative() {
        return true;
    }

    @NotNull
    @Override
    default T times(@NotNull T $receiver, @NotNull T y) {
        return (T) RingCalculator.DefaultImpls.times(this,$receiver,y);
    }

    @NotNull
    @Override
    default T times(@NotNull T $receiver, long n) {
        return (T) RingCalculator.DefaultImpls.times(this,$receiver,n);
    }

    @NotNull
    @Override
    default T times(long $receiver, @NotNull T x) {
        return (T) RingCalculator.DefaultImpls.times(this,$receiver,x);
    }

    @NotNull
    @Override
    default T multiplyLong(@NotNull T x, long n) {
        return (T) RingCalculator.DefaultImpls.multiplyLong(this,x,n);
    }

    @NotNull
    @Override
    default T pow(@NotNull T x, long n) {
        return (T) RingCalculator.DefaultImpls.pow(this,x,n);
    }

    @NotNull
    @Override
    @Deprecated
    default T gpow(@NotNull T x, long n) {
        return JGroupCalculator.super.gpow(x,n);
    }
}

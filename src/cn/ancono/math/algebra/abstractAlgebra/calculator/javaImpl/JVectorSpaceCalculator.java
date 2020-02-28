package cn.ancono.math.algebra.abstractAlgebra.calculator.javaImpl;

import cn.ancono.math.algebra.abstractAlgebra.calculator.GroupCalculator;
import cn.ancono.math.algebra.abstractAlgebra.calculator.ModuleCalculator;
import cn.ancono.math.algebra.abstractAlgebra.calculator.VectorSpaceCalculator;
import org.jetbrains.annotations.NotNull;

/*
 * Created at 2018/11/29 17:14
 * @author  liyicheng
 */
@SuppressWarnings("unchecked")
public interface JVectorSpaceCalculator<K, V> extends VectorSpaceCalculator<K, V> {
    @NotNull
    @Override
    default K rAdd(@NotNull K r1, @NotNull K r2) {
        return (K) DefaultImpls.rAdd(this, r1, r2);
    }

    @NotNull
    @Override
    default K rSubtract(@NotNull K r1, @NotNull K r2) {

        return (K) ModuleCalculator.DefaultImpls.rSubtract(this, r1, r2);
    }

    @NotNull
    @Override
    default K rNegate(@NotNull K k) {
        return (K) ModuleCalculator.DefaultImpls.rNegate(this, k);
    }

    @NotNull
    @Override
    default K rMultiply(@NotNull K r1, @NotNull K r2) {
        return (K) ModuleCalculator.DefaultImpls.rMultiply(this, r1, r2);
    }

    @NotNull
    @Override
    default K getRZero() {
        return getScalarCalculator().getZero();
    }


    @NotNull
    @Override
    default V subtract(@NotNull V x, @NotNull V y) {
        return (V) GroupCalculator.DefaultImpls.subtract(this, x, y);
    }

    @NotNull
    @Override
    default V minus(@NotNull V $receiver, @NotNull V y) {
        return (V) GroupCalculator.DefaultImpls.minus(this, $receiver, y);
    }

    @NotNull
    @Override
    default V unaryMinus(@NotNull V $receiver) {
        return (V) GroupCalculator.DefaultImpls.unaryMinus(this, $receiver);
    }

    @NotNull
    @Override
    default V gpow(@NotNull V x, long n) {
        return (V) GroupCalculator.DefaultImpls.gpow(this, x, n);
    }

    @Override
    default boolean isCommutative() {
        return false;
    }

    @NotNull
    @Override
    default Class<?> getNumberClass() {
        return DefaultImpls.getNumberClass(this);
    }

    @NotNull
    @Override
    default V plus(@NotNull V $receiver, @NotNull V y) {
        return (V) DefaultImpls.plus(this, $receiver, y);
    }

    @NotNull
    @Override
    default V times(long $receiver, @NotNull V x) {
        return (V) DefaultImpls.times(this, $receiver, x);
    }

    @NotNull
    @Override
    default V times(@NotNull V $receiver, long n) {
        return (V) DefaultImpls.times(this, $receiver, n);
    }

    @Override
    default boolean isLinearRelevant(@NotNull V u, @NotNull V v) {
        throw new UnsupportedOperationException();
    }
}

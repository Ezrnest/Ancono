package cn.ancono.math.algebra.abstractAlgebra.calculator.javaImpl;

import cn.ancono.math.algebra.abstractAlgebra.calculator.GroupCalculator;
import cn.ancono.math.algebra.abstractAlgebra.calculator.MonoidCalculator;
import org.jetbrains.annotations.NotNull;

/*
 * Created at 2018/10/8 21:26
 * @author  liyicheng
 */
@SuppressWarnings("unchecked")
public interface JGroupCalculator<T> extends JSemigroupCalculator<T>, GroupCalculator<T> {
    @NotNull
    @Override
    default T subtract(@NotNull T x, @NotNull T y) {
        return (T) GroupCalculator.DefaultImpls.subtract(this, x, y);
    }

    @NotNull
    @Override
    default T minus(@NotNull T $receiver, @NotNull T y) {
        return (T) GroupCalculator.DefaultImpls.minus(this, $receiver, y);
    }

    @NotNull
    @Override
    default T unaryMinus(@NotNull T $receiver) {
        return (T) GroupCalculator.DefaultImpls.unaryMinus(this, $receiver);
    }

    @NotNull
    @Override
    default T gpow(@NotNull T x, long n) {
        return (T) GroupCalculator.DefaultImpls.gpow(this, x, n);
    }

    @Override
    default boolean isCommutative() {
        return false;
    }

    @NotNull
    @Override
    default Class<?> getNumberClass() {
        return MonoidCalculator.DefaultImpls.getNumberClass(this);
    }
}

package cn.ancono.math.numberTheory;

import cn.ancono.math.algebra.abs.calculator.FieldCalculator;

/**
 * Describes a calculator for field <code>Z<sub>p</sub></code>, where <code>p</code> is a
 * prime number.
 *
 * @param <T>
 */
public interface ZModPCalculator<T> extends FieldCalculator<T> {


    long getP();

    @Override
    default long getCharacteristic() {
        return getP();
    }
}

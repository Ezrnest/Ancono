package cn.ancono.math.numberTheory;

import cn.ancono.math.MathCalculator;

/**
 * Describes a calculator for field <code>Z<sub>p</sub></code>, where <code>p</code> is a
 * prime number.
 *
 * @param <T>
 */
public interface ZModPCalculator<T> extends MathCalculator<T> {


    int getP();

    @Override
    default long getCharacteristic() {
        return getP();
    }
}

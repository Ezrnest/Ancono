package cn.ancono.math.numberTheory;

import cn.ancono.math.algebra.abs.calculator.OrderedFieldCal;

/**
 * Describes a calculator for field <code>Z<sub>p</sub></code>, where <code>p</code> is a
 * prime number.
 *
 * @param <T>
 */
public interface ZModPCalculator<T> extends OrderedFieldCal<T> {


    long getP();

    @Override
    default long getCharacteristic() {
        return getP();
    }
}

package cn.ancono.math.geometry.analytic.space.shape;

import cn.ancono.math.numberModels.api.RealCalculator;

public abstract class Parallelepiped<T> extends Prism<T> {

    protected Parallelepiped(RealCalculator<T> mc, long p) {
        super(mc, p);
    }

}

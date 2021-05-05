package cn.ancono.math.geometry.analytic.space.shape;

import cn.ancono.math.numberModels.api.RealCalculator;

public abstract class Cuboid<T> extends Parallelepiped<T> {

    protected Cuboid(RealCalculator<T> mc) {
        super(mc, 4);
    }

}

package cn.timelives.java.math.geometry.analytic.spaceAG.shape;

import cn.timelives.java.math.MathCalculator;

public abstract class Cuboid<T> extends Parallelepiped<T> {

	protected Cuboid(MathCalculator<T> mc) {
		super(mc, 4);
	}
	
}

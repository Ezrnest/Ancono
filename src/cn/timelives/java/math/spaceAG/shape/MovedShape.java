/**
 * 
 */
package cn.timelives.java.math.spaceAG.shape;

import cn.timelives.java.math.FieldMathObject;
import cn.timelives.java.math.numberModels.MathCalculator;
import cn.timelives.java.math.spaceAG.*;

import java.util.function.Function;

/**
 * Moved shape is a shape that is produced by a {@link SPlaneCurve} moving by a vector, which 
 * shapes a plane object. The 
 * @author liyicheng
 *
 */
public class MovedShape<T> extends SpaceObject<T> {
	final SpacePlaneObject<T> spo;
	final SVector<T> vec;
	final SPointTrans<T> spt;
	/**
	 * @param mc
	 * @param pl
	 */
	MovedShape(MathCalculator<T> mc,SpacePlaneObject<T> spo,SVector<T> vec) {
		super(mc);
		this.spo = spo;
		this.vec = vec;
		spt = spo.getPlane().projectionAsFunction(vec);
	}

	/* (non-Javadoc)
	 * @see cn.timelives.java.math.spaceAG.SpacePointSet#contains(cn.timelives.java.math.spaceAG.SPoint)
	 */
	@Override
	public boolean contains(SPoint<T> p) {
		SPoint<T> projection = spo.getPlane().projection(p, vec);
		if(!spo.contains(projection)){
			//the project must be in the shape
			return false;
		}
		SVector<T> sv = SVector.vector(p, projection);
		return MathTools.compareVector(vec, sv)>=0;
	}

	/* (non-Javadoc)
	 * @see cn.timelives.java.math.FlexibleMathObject#mapTo(java.util.function.Function, cn.timelives.java.math.MathCalculator)
	 */
	@Override
	public <N> MovedShape<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cn.timelives.java.math.FlexibleMathObject#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see cn.timelives.java.math.FlexibleMathObject#hashCode()
	 */
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see cn.timelives.java.math.FlexibleMathObject#valueEquals(cn.timelives.java.math.FlexibleMathObject)
	 */
	@Override
	public boolean valueEquals(FieldMathObject<T> obj) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see cn.timelives.java.math.FlexibleMathObject#valueEquals(cn.timelives.java.math.FlexibleMathObject, java.util.function.Function)
	 */
	@Override
	public <N> boolean valueEquals(FieldMathObject<N> obj, Function<N, T> mapper) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see cn.timelives.java.math.spaceAG.shape.SpaceObject#isInside(cn.timelives.java.math.spaceAG.SPoint)
	 */
	@Override
	public boolean isInside(SPoint<T> p) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see cn.timelives.java.math.spaceAG.shape.SpaceObject#isOnSurface(cn.timelives.java.math.spaceAG.SPoint)
	 */
	@Override
	public boolean isOnSurface(SPoint<T> p) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see cn.timelives.java.math.spaceAG.shape.SpaceObject#volume()
	 */
	@Override
	public T volume() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see cn.timelives.java.math.spaceAG.shape.SpaceObject#surfaceArea()
	 */
	@Override
	public T surfaceArea() {
		// TODO Auto-generated method stub
		return null;
	}
	
}

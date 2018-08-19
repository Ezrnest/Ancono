/**
 * 
 */
package cn.timelives.java.math.geometry.analytic.spaceAG;

import cn.timelives.java.math.MathObject;
import cn.timelives.java.math.MathCalculator;
import cn.timelives.java.math.geometry.analytic.planeAG.curve.AbstractPlaneCurve;
import cn.timelives.java.math.geometry.analytic.spaceAG.Plane.PlaneCoordinateConverter;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * This class bridges the spaceAG and planeAG.
 * @author liyicheng
 *
 */
public class SPlaneCurve<T> extends SpacePlaneObject<T> {
	final AbstractPlaneCurve<T> pc;
	final PlaneCoordinateConverter<T> pcc;
	/**
	 * @param mc
	 * @param pl
	 */
	protected SPlaneCurve(MathCalculator<T> mc, PlaneCoordinateConverter<T> pcc,AbstractPlaneCurve<T> pc) {
		super(mc, pcc.getPlane());
		this.pc = pc;
		this.pcc = pcc;
	}
	
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.spaceAG.SpacePointSet#contains(cn.timelives.java.math.spaceAG.SPoint)
	 */
	@Override
	public boolean contains(SPoint<T> p) {
		return pl.contains(p) && pc.contains(pcc.toPlanePoint0(p));
	}

	/* (non-Javadoc)
	 * @see cn.timelives.java.math.FlexibleMathObject#mapTo(java.util.function.Function, cn.timelives.java.math.MathCalculator)
	 */
    @NotNull
    @Override
    public <N> SPlaneCurve<N> mapTo(@NotNull Function<T, N> mapper, @NotNull MathCalculator<N> newCalculator) {
		return new SPlaneCurve<N>(newCalculator, pcc.mapTo(mapper, newCalculator),
				pc.mapTo(mapper, newCalculator));
	}

	/* (non-Javadoc)
	 * @see cn.timelives.java.math.FlexibleMathObject#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof SPlaneCurve){
			SPlaneCurve<?> spc = (SPlaneCurve<?>) obj;
			return pc.equals(spc.pc) && pcc.equals(spc.pcc);
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see cn.timelives.java.math.FlexibleMathObject#hashCode()
	 */
	@Override
	public int hashCode() {
		return pc.hashCode() * 37 + pcc.hashCode();
	}

	/* (non-Javadoc)
	 * @see cn.timelives.java.math.FlexibleMathObject#valueEquals(cn.timelives.java.math.FlexibleMathObject)
	 */
	@Override
    public boolean valueEquals(@NotNull MathObject<T> obj) {
		if(obj instanceof SPlaneCurve){
			SPlaneCurve<T> spc = (SPlaneCurve<T>) obj;
			return pc.valueEquals(spc.pc) && pcc.valueEquals(spc.pcc);
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see cn.timelives.java.math.FlexibleMathObject#valueEquals(cn.timelives.java.math.FlexibleMathObject, java.util.function.Function)
	 */
	@Override
    public <N> boolean valueEquals(@NotNull MathObject<N> obj, @NotNull Function<N, T> mapper) {
		if(obj instanceof SPlaneCurve){
			SPlaneCurve<N> spc = (SPlaneCurve<N>) obj;
			return pc.valueEquals(spc.pc,mapper) && pcc.valueEquals(spc.pcc,mapper);
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("SPlaneCurve:p={").append(pl).append("} curve=").append(pc.toString());
		return sb.toString();
	}

}

/**
 * 
 */
package cn.timelives.java.math.spaceAG;

import cn.timelives.java.math.FieldMathObject;
import cn.timelives.java.math.function.MathFunction;
import cn.timelives.java.math.numberModels.MathCalculator;
import cn.timelives.java.math.planeAG.Circle;
import cn.timelives.java.math.spaceAG.Plane.PlaneCoordinateConverter;

import java.util.function.Function;

/**
 * SCircle is a circle in space.  
 * @author liyicheng
 *
 */
public final class SCircle<T> extends SpacePlaneObject<T> {
	final SPoint<T> o;
	final T r2;
	T r;
	
	/**
	 * @param mc
	 */
	protected SCircle(MathCalculator<T> mc,SPoint<T> o,T r2,Plane<T> pl) {
		super(mc,pl);
		this.o = o;
		this.r2 = r2;
	}

	/**
	 * Determines whether the point is inside or on this circle.
	 */
	@Override
	public boolean contains(SPoint<T> p) {
		return pl.contains(p) && mc.compare(o.distanceSq(p), r2) <= 0;
	}
	/**
	 * Determines whether the point is on the edge of the circle.
	 * @param p
	 * @return
	 */
	public boolean isOnEgde(SPoint<T> p){
		return pl.contains(p) && mc.compare(o.distanceSq(p), r2) == 0;
	}
	
	/**
	 * A so usually called square calculation.<p>
	 * <b>Make sure the calculator has been initialized.</b>
	 * @param x
	 * @return square of x
	 */
	private T square(T x){
		return mc.multiply(x, x);
	}
	/**
	 * Get the center of this circle.
	 * @return the center of this circle.
	 */
	public SPoint<T> getCenter(){
		return o;
	}
	
	/**
	 * Gets the radius of this circle.This method usually requires the {@code squareRoot} method is 
	 * available in the math calculator.
	 * @return the radius of this circle.
	 */
	public T getRadius(){
		if(r==null){
			r = mc.squareRoot(r2);
		}
		return r;
	}
	/**
	 * Gets the diameter of this circle.This method usually requires the {@code squareRoot} method is 
	 * available in the math calculator.
	 * @return the diameter of this circle.
	 * @see Circle#radius()
	 */
	public T getDiameter(){
		return mc.multiplyLong(getRadius(), 2l);
	}
	/**
	 * Computes the corresponding arc length of the given angle.This method will not check whether 
	 * the angle is positive and smaller than {@code 2π}.
	 * @param angle the angle,radical
	 * @return the length of the arc
	 */
	public T getArcLength(T angle){
		return mc.multiply(angle, getRadius());
	}
	
	
	/**
	 * Gets the area of this circle,which is calculated by the formula {@literal S = πr²}.This method requires the 
	 * constant value of {@literal pi} is available in the calculator.
	 * @return the area of this circle
	 */
	public T getArea(){
		T pi = mc.constantValue(MathCalculator.STR_PI);
		return mc.multiply(pi, r2);
	}
	/**
	 * Get the perimeter of this circle,which is equal to {@literal C = 2πr}.This method requires the 
	 * constant value of {@literal PI} is available in the calculator.
	 * @return the perimeter of this circle.
	 */
	public T getPerimeter(){
		T pi = mc.constantValue(MathCalculator.STR_PI);
		return mc.multiply(mc.multiplyLong(pi, 2l), getRadius());
	}
	/**
	 * Assume {@code d} is the distance of the center of this circle to a chord,
	 * return the length of the chord.If the distance is larger than the radius,
	 * {@code null} will be returned.
	 * @param d distance
	 * @return the length of the chord,or {@code null}
	 */
	public T getChordLength(T d){
		T d2 = square(d);
		if(mc.compare(d2, r2)>0){
			return null;
		}
		return mc.multiplyLong(mc.squareRoot(mc.subtract(r2, d2)), 2l);
	}
	/**
	 * Assume {@code d2} is the square of the distance of the center of this circle to a chord,
	 * return the square of the length of the chord.
	 * @param d square of distance
	 * @return the square of the length of the chord
	 */
	public T getChordLengthSq(T d2){
		return mc.multiplyLong(mc.subtract(r2, d2), 4l);
	}
	/**
	 * Get the corresponding central angle of the chord whose length is {@code cl}.
	 * If {@code cl} is larger than the diameter of this circle,{@code null} will be 
	 * returned. 
	 * @param cl the length of chord
	 * @param arccos the math function arccos
	 * @return the angle of 
	 */
	public T getCentralAngle(T cl,MathFunction<T,T> arccos){
		T an = getCircumAngle(cl, arccos);
		if(an == null){
			return null;
		}
		return mc.multiplyLong(an, 2l);
	}
	/**
	 * Get the corresponding circumference angle of the chord whose length is {@code cl}.
	 * If {@code cl} is larger than the diameter of this circle,{@code null} will be 
	 * returned. 
	 * @param cl the length of chord
	 * @param arccos the math function arccos
	 * @return the angle of 
	 */
	public T getCircumAngle(T cl,MathFunction<T,T> arccos){
		T l_2 = mc.divideLong(cl, 2l);
		if(mc.compare(l_2, getRadius())>0){
			return null;
		}
		return arccos.apply(mc.divide(l_2, r));
	}
	
	/**
	 * Determines the relation of the given point and this circle.The relation may be 
	 * <i>inside</i>,<i>on</i>,<i>outside</i>.
	 * @param p a point on the same plane
	 * @return {@code -1} if {@code p} is inside this circle,
	 * {@code 0} if {@code p} is on this circle,
	 * or {@code 1} if {@code p} is outside this circle.
	 */
	public int relation(SPoint<T> p){
		if(!pl.contains(p)){
			throw new IllegalArgumentException("Not on this plane");
		}
		T dis = o.distanceSq(p);
		return mc.compare(dis, square(getRadius()));
	}
	
	public Circle<T> toPlaneCircle(PlaneCoordinateConverter<T> pcc){
		return pcc.toPlaneCircle(this);
	}
	
	<N> void fillFields(SCircle<N> sc,Function<T,N> mapper){
		if(r != null){
			sc.r = mapper.apply(r);
		}
	}
	
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.FlexibleMathObject#mapTo(java.util.function.Function, cn.timelives.java.math.MathCalculator)
	 */
	@Override
	public <N> SCircle<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator) {
		SCircle<N> sc = new SCircle<N>(newCalculator, 
				o.mapTo(mapper, newCalculator), mapper.apply(r2), pl.mapTo(mapper, newCalculator));
		fillFields(sc,mapper);
		return sc;
	}

	/* (non-Javadoc)
	 * @see cn.timelives.java.utilities.math.FlexibleMathObject#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof SCircle){
			SCircle<?> sp = (SCircle<?>) obj;
			return sp.pl.equals(pl) && sp.o.equals(o) && r2.equals(sp.r2);
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see cn.timelives.java.utilities.math.FlexibleMathObject#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = o.hashCode();
		hash = pl.hashCode() + hash*31;
		hash = hash*31 + r2.hashCode();
		return hash;
	}

	/* (non-Javadoc)
	 * @see cn.timelives.java.utilities.math.FlexibleMathObject#valueEquals(cn.timelives.java.utilities.math.FlexibleMathObject)
	 */
	@Override
	public boolean valueEquals(FieldMathObject<T> obj) {
		if(obj instanceof SCircle){
			SCircle<T> sp = (SCircle<T>) obj;
			return pl.valueEquals(sp.pl) && 
					o.valueEquals(sp.o) &&
					mc.isEqual(r2,sp.r2);
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see cn.timelives.java.utilities.math.FlexibleMathObject#valueEquals(cn.timelives.java.utilities.math.FlexibleMathObject, java.util.function.Function)
	 */
	@Override
	public <N> boolean valueEquals(FieldMathObject<N> obj, Function<N, T> mapper) {
		if(obj instanceof SCircle){
			SCircle<N> sp = (SCircle<N>) obj;
			return pl.valueEquals(sp.pl,mapper) && 
					o.valueEquals(sp.o,mapper) && 
					mc.isEqual(r2,mapper.apply(sp.r2));
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Circle").append(o.toString());
		if(r == null){
			sb.append(" R2=").append(r2);
		}else{
			sb.append(" R=").append(r);
		}
		sb.append(" p=").append(pl.toString());
		return sb.toString();
	}

}

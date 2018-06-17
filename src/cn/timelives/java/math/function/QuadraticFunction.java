/**
 * 
 */
package cn.timelives.java.math.function;


import cn.timelives.java.math.MathObject;
import cn.timelives.java.math.equation.SVPEquation;
import cn.timelives.java.math.equation.SVPEquation.QEquation;
import cn.timelives.java.math.numberModels.ComputeExpression;
import cn.timelives.java.math.MathCalculator;
import cn.timelives.java.math.numberModels.api.FlexibleNumberFormatter;
import cn.timelives.java.math.geometry.analytic.planeAG.Line;
import cn.timelives.java.math.geometry.analytic.planeAG.Point;
import cn.timelives.java.math.geometry.analytic.planeAG.curve.AbstractPlaneFunction;
import cn.timelives.java.math.geometry.analytic.planeAG.curve.ConicSection;
import cn.timelives.java.math.geometry.analytic.planeAG.curve.GeneralConicSection;

import java.util.function.Function;

/**
 * Describes function {@code f(x) = ax^2+bx+c (a!=0)}.
 * @author liyicheng
 *
 */
public final class QuadraticFunction<T> extends AbstractPlaneFunction<T> implements SVPFunction<T>{
	private final T a,b,c;
	/**
	 * @param mc
	 */
	QuadraticFunction(MathCalculator<T> mc,T a,T b,T c) {
		super(mc);
		if(mc.isZero(a)){
			throw new IllegalArgumentException("a==0");
		}
		this.a = a;
		this.b = b;
		this.c = c;
	}
	
	private static final ComputeExpression EXPR_APPLY = ComputeExpression.compile("($1$0+$2)$0+$3");
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.geometry.analytic.planeAG.curve.PlaneFunction#apply(java.lang.Object)
	 */
	@Override
	public T apply(T x) {
		//(ax+b)x+c
		return EXPR_APPLY.compute(mc, x,a,b,c);
	}
	
	private Point<T> climax;
	private static final ComputeExpression EXPR_CLIMAXY = ComputeExpression.compile("(4$0$2-$1*$1)/$0/4");
	public Point<T> climax(){
		if(climax == null){
			//-b/2a , (4ac-b^2)/4a
			climax = Point.valueOf(mc.divideLong(mc.divide(b, a), -2l), EXPR_CLIMAXY.compute(mc, a,b,c), mc);
		}
		return climax;
	}
	/**
	 * Determines whether the quadratic function has a minimum value or a maximum value.
	 * @return
	 */
	public boolean hasMinValue(){
		return mc.compare(a, mc.getZero()) > 0;
	}
	/**
	 * Gets the coefficient a.
	 * @return
	 */
	public T getA() {
		return a;
	}
	/**
	 * Gets the coefficient a.
	 * @return
	 */
	public T getB() {
		return b;
	}
	/**
	 * Gets the coefficient a.
	 * @return
	 */
	public T getC() {
		return c;
	}
	/**
	 * Returns the tangent line of passes the point (x,f(x))
	 * @param x x coordinate of the point
	 * @return
	 */
	public Line<T> tangentLine(T x){
		//k = 2ax0+b 
		T k = mc.add(mc.multiplyLong(mc.multiply(a, x), 2l), b);
		return Line.pointSlope(getPoint(x), k, mc);
	}
	
	
	
	/**
	 * Converts the quadratic function to an equation.
	 * @return
	 */
	public QEquation<T> toEquation(){
		return SVPEquation.quadratic(a, b, c, mc);
	}
	/**
	 * Returns a ConicSection representing this function.
	 * @return
	 */
	public ConicSection<T> toConicSection(){
		return GeneralConicSection.generalFormula(mc, a,null,null,b,mc.negate(mc.getOne()),c);
	}

	/* (non-Javadoc)
	 * @see cn.timelives.java.math.FlexibleMathObject#mapTo(java.util.function.Function, cn.timelives.java.math.number_models.MathCalculator)
	 */
	@Override
	public <N> QuadraticFunction<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator) {
		return new QuadraticFunction<N>(newCalculator, mapper.apply(a),mapper.apply(b),mapper.apply(c));
	}

	/* (non-Javadoc)
	 * @see cn.timelives.java.math.FlexibleMathObject#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(this == obj){
			return true;
		}

		if(obj instanceof QuadraticFunction){
			QuadraticFunction<?> q = (QuadraticFunction<?>) obj;
			return mc.equals(q.mc) && a.equals(q.a) && b.equals(q.b) && c.equals(q.c);
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see cn.timelives.java.math.FlexibleMathObject#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = mc.hashCode();
		hash = hash*31 + a.hashCode();
		hash = hash*31 + b.hashCode();
		hash = hash*31 + c.hashCode();
		return hash;
	}

	/* (non-Javadoc)
	 * @see cn.timelives.java.math.FlexibleMathObject#valueEquals(cn.timelives.java.math.FlexibleMathObject)
	 */
	
	@Override
	public boolean valueEquals(MathObject<T> obj) {
		if(this == obj){
			return true;
		}
		if(!(obj instanceof SVPFunction)){
			return false;
		}
		@SuppressWarnings("unchecked")
		SVPFunction<T> f = (SVPFunction<T>)obj;
		return SVPFunction.isEqual(this,f , mc::isEqual);
	}

	/* (non-Javadoc)
	 * @see cn.timelives.java.math.FlexibleMathObject#valueEquals(cn.timelives.java.math.FlexibleMathObject, java.util.function.Function)
	 */
	@Override
	public <N> boolean valueEquals(MathObject<N> obj, Function<N, T> mapper) {
		if(this == obj){
			return true;
		}
		if(!(obj instanceof SVPFunction)){
			return false;
		}
		@SuppressWarnings("unchecked")
		SVPFunction<N> f = (SVPFunction<N>)obj;
		return SVPFunction.isEqual(this,f , (x,y)->mc.isEqual(x, mapper.apply(y)));
	}
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.function.SVPFunction#getCoefficient(int)
	 */
	@Override
	public T getCoefficient(int n) {
		switch(n){
		case 0:return c;
		case 1:return b;
		case 2: return a;
		}
		throw new IndexOutOfBoundsException("For n="+n);
	}
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.function.SVPFunction#getMaxPower()
	 */
	@Override
	public int getDegree() {
		return 2;
	}
	/**
	 * Returns a new quadratic function.
	 * @param a
	 * @param b
	 * @param c
	 * @param mc
	 * @return
	 */
	public static <T> QuadraticFunction<T> generalFormula(T a,T b,T c,MathCalculator<T> mc){
		return new QuadraticFunction<T>(mc, a, b, c);
	}
	
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.FlexibleMathObject#toString(cn.timelives.java.math.number_models.NumberFormatter)
	 */
	@Override
	public String toString(FlexibleNumberFormatter<T,MathCalculator<T>> nf) {
		StringBuilder sb = new StringBuilder();
		for(int i=2;i>0;i--){
			if(mc.isZero(getCoefficient(i)))
				continue;
			sb.append(nf.format(getCoefficient(i), mc)).append("*x^").append(i).append(" + ");
		}
		if(mc.isZero(getCoefficient(0))==false){
			sb.append(nf.format(getCoefficient(0), mc));
		}else{
			sb.delete(sb.length()-3, sb.length());
		}
		return sb.toString();
	}
	


}

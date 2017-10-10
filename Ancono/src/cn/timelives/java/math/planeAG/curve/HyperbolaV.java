package cn.timelives.java.math.planeAG.curve;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import cn.timelives.java.math.FlexibleMathObject;
import cn.timelives.java.math.equation.SVPEquation.QEquation;
import cn.timelives.java.math.exceptions.UnsupportedCalculationException;
import cn.timelives.java.math.numberModels.MathCalculator;
import cn.timelives.java.math.planeAG.Line;
import cn.timelives.java.math.planeAG.Point;
/**
 * A hyperbola is a special kind of conic section,it is a set of point that 
 * <pre>{p|d(p,F1)-d(p,F2)=2a}</pre>,where {@code a} is a constant and is equal to a half of the length of major axis.
 * <p>  
 * In this class of hyperbola,the hyperbola's center is always at the point O(0,0),and its foci are 
 * on either coordinate axis X or Y. The reason why the hyperbola is set like this is to reduce 
 * the calculation.
 * <p>
 * In an hyperbola,there are two foci,we usually call them {@code F1,F2},
 * and the distance between the two foci is equal to {@code 2c}.
 * The value {@code 2a} is equal to the length of transverse axis,
 * value {@code 2b} that {@code a^2+b^2=c^2} is the length of the conjugate axis.
 * There is also a boolean value to indicate whether the hyperbola's foci are on X axis or Y axis.
 * <p>
 * The standard equation of this ellipse is 
 * <pre>x^2/a^2 - y^2/b^2 = 1</pre>
 * or 
 * <pre>x^2/b^2 - y^2/a^2 = -1</pre>
 * determined by whether foci are on X axis.
 * @author lyc
 * @param <T>
 */
public final class HyperbolaV<T> extends EHSection<T> {
	
	private List<Line<T>> asys ;
	
	//onX only indicates the hyperbola's foci,a for x and b for y does not change. 

	protected HyperbolaV(MathCalculator<T> mc, T A, T C, T a, T b, T c, T a2, T b2, T c2, boolean onX) {
		super(mc, A, C, a, b, c, a2, b2, c2, onX);
	}
	
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.planeAG.ConicSection#getType()
	 */
	@Override
	public cn.timelives.java.math.planeAG.curve.ConicSection.Type determineType() {
		return ConicSection.Type.HYPERBOLA;
	}
	
	@Override
	public List<Point<T>> vertices() {
		//points :
		List<Point<T>> list = new ArrayList<>();
		if(onX){
			list.add(Point.valueOf(mc.negate(a), mc.getZero(), mc));
			list.add(Point.valueOf(a, mc.getZero(), mc));
		}else{
			list.add(Point.valueOf( mc.getZero(),mc.negate(b), mc));
			list.add(Point.valueOf( mc.getZero(),b, mc));
		}
		return list;
	}
	
	@Override
	public T substitute(T x, T y) {
		//Ax^2 / a^2 + By^2 + F 
		T re = mc.multiply(A, square(x));
		re =  mc.add(re, mc.multiply(C, square(y)));
		return mc.add(re, F);
	}
	/**
	 * Returns the conjugate hyperbola of this.
	 * @return a hyperbola
	 */
	public HyperbolaV<T> conjugateHyperbola(){
		return new HyperbolaV<>(mc,mc.negate(A),mc.negate(C),a,b,c,a2,b2,c2,!onX);
	}
	/**
	 * Returns the two asymptotes of this hyperbola.
	 * @return a list of lines.
	 */
	public List<Line<T>> asymptote(){
		if(asys==null){
			T zero = mc.getZero();
			asys = new ArrayList<>(2);
			asys.add(Line.pointDirection(zero, zero, a, b, mc));
			asys.add(Line.pointDirection(zero, zero, a, mc.negate(b), mc));
		}
		return asys;
	}
	/**
	 * Determines whether the given line is parallel to one of the asymptotes,which means 
	 * this line has only one intersect point with {@code this}.
	 * @return {@code true} if the given line is parallel to one of the asymptotes.
	 */
	public boolean isParallelAsymptote(Line<T> line){
		for(Line<T> l : asymptote()){
			if(l.isParallel(line)){
				return true;
			}
		}
		return false;
	}
	
	private void checkOn(Point<T> p){
		if(!contains(p)){
			throw new IllegalArgumentException("point not on hyperbola");
		}
	}
	
	@Override
	public T focusDL(Point<T> p) {
		checkOn(p);
		if(onX){
			return mc.abs(mc.add(a, mc.multiply(getEccentricity(), p.x)));
		}else{
			return mc.abs(mc.add(b, mc.multiply(getEccentricity(), p.y)));
		}
	}

	@Override
	public T focuseDR(Point<T> p) {
		checkOn(p);
		if(onX){
			return mc.abs(mc.subtract(a, mc.multiply(getEccentricity(), p.x)));
		}else{
			return mc.abs(mc.subtract(b, mc.multiply(getEccentricity(), p.y)));
		}
	}
	
	@Override
	public T computeX(T y) {
		T t = onX ? b2 : mc.negate(b2);
		T re = mc.divide(a, b);
		return mc.multiply(re, mc.squareRoot(mc.add(t, square(y))));
	}
	
	@Override
	public T computeY(T x) {
		T t = onX ? mc.negate(b2) : b2;
		T re = mc.divide(b, a);
		return mc.multiply(re, mc.squareRoot(mc.add(t, square(x))));
	}
	
	/**
	 * Returns the relation of the line to this hyperbola.
	 * <li>Returns -1 if  {@code line} doesn't intersect with this hyperbola.
	 * <li>Returns 0 if {@code line} is parallel to one of the asymptotes(which means 
	 * it only intersect with {@code this} at one point.
	 * <li>Returns 1 if {@code line} is a tangent line.
	 * <li>Returns 2 if {@code line} intersects with this hyperbola at two points.   
	 * @param line a line
	 * @return an integer representing the relation.
	 */
	public int relation(Line<T> line){
		if(isParallelAsymptote(line)){
			return 0;
		}
		if(line.slope() == null ){
			QEquation<T> equ = (QEquation<T>)createEquationY(line);
			int solv = equ.getNumberOfRoots();
			if(solv == 0){
				return -1;
			}
			return solv;
		}
			
		QEquation<T> equa = (QEquation<T>)createEquationX(line);
		int ren = equa.getNumberOfRoots();
		if(ren == 0){
			return -1;
		}
		return ren;
	}
	
	@Override
	public T chordLength(Line<T> line) {
		if(isParallelAsymptote(line)){
			return null;
		}
		T k = line.slope();
		if(k == null){
			QEquation<T> equ = (QEquation<T>)createEquationY(line);
			int re = 2;
			try{
				re = equ.getNumberOfRoots();
			}catch(UnsupportedCalculationException ex){
				//ignore
			}
			if(re == 0){
				return null;
			}else if(re==1){
				return mc.getZero();
			}
			return equ.rootsSubtract();
		}
		QEquation<T> equ = (QEquation<T>)createEquationX(line); 
		int re = 2;
		try{
			re = equ.getNumberOfRoots();
		}catch(UnsupportedCalculationException ex){
			//ignore
		}
		if(re==0){
			return null;
		}else if(re == 1){
			return mc.getZero();
		}
		T len = mc.squareRoot(mc.add(mc.getOne(), square(k)));
		return mc.multiply(len, equ.rootsSubtract());
	}
	
	@Override
	public T chordLengthSq(Line<T> line) {
		if(isParallelAsymptote(line)){
			return null;
		}
		T k = line.slope();
		if(k == null){
			QEquation<T> equ = (QEquation<T>)createEquationY(line);
			int re = 2;
			try{
				re = equ.getNumberOfRoots();
			}catch(UnsupportedCalculationException ex){
				//ignore
			}
			if(re == 0){
				return null;
			}else if(re==1){
				return mc.getZero();
			}
			return equ.rootsSubtractSq();
		}
		QEquation<T> equ = (QEquation<T>)createEquationX(line); 
		int re = 2;
		try{
			re = equ.getNumberOfRoots();
		}catch(UnsupportedCalculationException ex){
			//ignore
		}
		if(re==0){
			return null;
		}else if(re == 1){
			return mc.getZero();
		}
		T len = mc.add(mc.getOne(), square(k));
		return mc.multiply(len, equ.rootsSubtractSq());
	}
	

	@Override
	public <N> boolean valueEquals(FlexibleMathObject<N> obj, Function<N, T> mapper) {
		if(obj instanceof HyperbolaV){
			HyperbolaV<N> ev = (HyperbolaV<N>) obj;
			if(ev.onX == onX){
				return mc.isEqual(mapper.apply(ev.a), a) && mc.isEqual(mapper.apply(ev.b), b);
			}
			return false;
		}
		return super.valueEquals(obj, mapper);
	}
	
	@Override
	public boolean valueEquals(FlexibleMathObject<T> obj) {
		if(obj instanceof HyperbolaV){
			HyperbolaV<T> ev = (HyperbolaV<T>) obj;
			if(ev.onX == onX){
				return mc.isEqual(ev.a, a) && mc.isEqual(ev.b, b);
			}
			return false;
		}
		return super.valueEquals(obj);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof HyperbolaV){
			HyperbolaV<?> ev = (HyperbolaV<?>) obj;
			return ev.onX == onX && a.equals(ev.a) && b.equals(ev.b);  
		}
		return false;
	}
	
	@Override
	public <N> HyperbolaV<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator) {
		HyperbolaV<N> nell = new HyperbolaV<N>(newCalculator, mapper.apply(A), mapper.apply(C)
				, mapper.apply(a), mapper.apply(b), mapper.apply(c)
				, mapper.apply(a2), mapper.apply(b2), mapper.apply(c2)
				,onX);
		
		
		nell.e  = e == null ? null : mapper.apply(e);
		nell.f1  = f1 == null ? null : f1.mapTo(mapper, newCalculator);
		nell.f2  = f2 == null ? null : f2.mapTo(mapper, newCalculator);
		if(asys != null){
			ArrayList<Line<N>> newList = new ArrayList<>(2);
			asys.forEach(l -> newList.add(l.mapTo(mapper, newCalculator)));
			nell.asys = newList;
		}
		return nell;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Hyperbola: x^2/(");
			sb.append(a2);
		sb.append(')').append(" - y^2/(");
		sb.append(b2);
		sb.append(") = ");
		if(!onX){
			sb.append('-');
		}
		sb.append("1");
		return sb.toString();
	}
	
	private static <T> HyperbolaV<T> create0(T a,T b,T c,T a2,T b2,T c2,boolean onX,MathCalculator<T> mc){
		T A,C;
		A = mc.reciprocal(a2);
		C = mc.reciprocal(b2);
		if(onX){
			C = mc.negate(C);
		}else{
			A = mc.negate(A);
		}
		return new HyperbolaV<>(mc,A,C,
				a,b,c,
				a2,b2,c2,
				onX);
	}
	
	/**
	 * Creates a hyperbolaV of 
	 * <pre>x^2/a^2 - y^2/b^2 = +-1</pre>,
	 * @param a coefficient a
	 * @param b coefficient b
	 * @param onX decides whether this Hyperbola should be on X . 
	 * @param mc a {@link MathCalculator}
	 * @return new HyperbolaV
	 * @throws IllegalArgumentException if {@code a==b} or {@code a <= 0 || b <= 0}
	 */
	public static <T> HyperbolaV<T> standardEquation(T a,T b,boolean onX,MathCalculator<T> mc){
		T a2 = mc.multiply(a, a);
		T b2 = mc.multiply(b, b);
		T c2 = mc.add(a2, b2);
		T c = mc.squareRoot(c2);
		return create0(a, b, c, a2, b2, c2, onX, mc);
	}


}

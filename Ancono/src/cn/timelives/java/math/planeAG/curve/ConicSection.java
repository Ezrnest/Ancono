package cn.timelives.java.math.planeAG.curve;

import static  java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import cn.timelives.java.math.FlexibleMathObject;
import cn.timelives.java.math.equation.SVPEquation;
import cn.timelives.java.math.equation.SVPEquation.LEquation;
import cn.timelives.java.math.equation.SVPEquation.QEquation;
import cn.timelives.java.math.exceptions.UnsupportedCalculationException;
import cn.timelives.java.math.numberModels.ComputeExpression;
import cn.timelives.java.math.numberModels.MathCalculator;
import cn.timelives.java.math.numberModels.NumberFormatter;
import cn.timelives.java.math.numberModels.Simplifiable;
import cn.timelives.java.math.numberModels.Simplifier;
import cn.timelives.java.math.planeAG.Circle;
import cn.timelives.java.math.planeAG.Line;
import cn.timelives.java.math.planeAG.PAffineTrans;
import cn.timelives.java.math.planeAG.PVector;
import cn.timelives.java.math.planeAG.Point;
import cn.timelives.java.math.planeAG.TransMatrix;
import cn.timelives.java.utilities.structure.Pair;
/**
 * Conic section is a set of curves that can be described with the equation 
 * <pre>
 * Ax^2 + Bxy + Cy^2 + Dx + Ey + F = 0
 * </pre>
 * For instance, {@link Circle} is a kind of conic section.
 * @see GeneralConicSection
 * @see Circle
 * @see EllipseV
 * @see HyperbolaV
 * @author lyc
 * 
 */
public abstract class 
ConicSection<T> 
extends AbstractPlaneCurve<T> 
implements Simplifiable<T,ConicSection<T>>,SubstituableCurve<T>{
	/**
	 * The coefficients
	 */
	protected final T A,B,C,D,E,F;
	/**
	 * Describes the basic types of the Conic Section. All the 
	 * conic can be classified into one of this types, and can 
	 * be transformed to the standard formula of a specific type
	 * through rotating and translating.
	 * @author liyicheng
	 *
	 */
	public enum Type{
		/**
		 * An ellipse, whose standard formula is {@literal x^2/a^2 + y^2/b^2 - 1 = 0}.
		 */
		ELLIPSE,
		/**
		 * An imaginary ellipse, whose standard formula is {@literal x^2/a^2 + y^2/b^2 + 1 = 0}.
		 */
		IMAGINARY_ELLIPSE,
		/**
		 * A point, whose standard formula is {@literal x^2/a^2 + y^2/b^2 = 0}.
		 */
		POINT,
		/**
		 * A hyperbola, whose standard formula is {@literal x^2/a^2 - y^2/b^2 - 1 = 0}, or {@literal -x^2/a^2 + y^2/b^2 - 1 = 0}.
		 */
		HYPERBOLA,
		/**
		 * A pair of intersect lines, whose standard formula is {@literal x^2/a^2 - y^2/b^2 = 0}
		 */
		INTERSECT_LINE,
		/**
		 * A parabola, whose standard formula is {@literal y^2 - 2px = 0} or {@literal x^2 - 2py = 0}.
		 */
		PARABOLA,
		/**
		 * A pair of parallel lines, whose standard formula is {@literal x^2 - a^2 = 0}, or {@literal x^2 + a^2 = 0}.
		 */
		PARALLEL_LINE,
		/**
		 * A pair of imaginary parallel lines, whose standard formula is {@literal x^2 + a^2 = 0}, or {@literal y^2 + a^2 = 0}.
		 */
		IMAGINARY_PARALLEL_LINE,
		/**
		 * A pair of coincide lines, whose standard formula is {@literal x^2 = 0}, or  {@literal y^2 = 0}.
		 */
		CONCIDE_LINE
	}
	/**
	 * To create a conic section,the following coefficient must be given.
	 * @param mc
	 * @param A
	 * @param B
	 * @param C
	 * @param D
	 * @param E
	 * @param F
	 */
	protected ConicSection(MathCalculator<T> mc,T A,T B,T C,T D,T E,T F) {
		super(mc);
		if(mc.isZero(A)&&mc.isZero(C)&&mc.isZero(B)){
			throw new IllegalArgumentException("A=B=C=0 for conic section");
		}
		this.A = requireNonNull(A);
		this.B = requireNonNull(B);
		this.C = requireNonNull(C);
		this.D = requireNonNull(D);
		this.E = requireNonNull(E);
		this.F = requireNonNull(F);
	}

	/**
	 * Gets the coefficient A.
	 * @return the a
	 */
	public T getA() {
		return A;
	}

	/**
	 * Gets the coefficient B.
	 * @return the b
	 */
	public T getB() {
		return B;
	}

	/**
	 * Gets the coefficient C.
	 * @return the c
	 */
	public T getC() {
		return C;
	}

	/**
	 * Gets the coefficient D.
	 * @return the d
	 */
	public T getD() {
		return D;
	}

	/**
	 * Gets the coefficient E.
	 * @return the e
	 */
	public T getE() {
		return E;
	}

	/**
	 * Gets the coefficient F.
	 * @return the f
	 */
	public T getF() {
		return F;
	}
	/**
	 * Returns a list that contains all the coefficients.The order of coefficient is A,B,C,D,E,F.
	 * The list is modifiable.
	 * @return a coefficient list.
	 */
	public List<T> getCoefficients(){
		List<T> list = new ArrayList<T>(6);
		list.add(A);
		list.add(B);
		list.add(C);
		list.add(D);
		list.add(E);
		list.add(F);
		return list;
	}
	
	@Override
	public T substitute(T x, T y) {
		T re = mc.multiply(A, mc.multiply(x, x));
		re = mc.add(re, mc.multiply(B, mc.multiply(x, y)));
		re = mc.add(re, mc.multiply(C, mc.multiply(y, y)));
		re = mc.add(re, mc.multiply(D, x));
		re = mc.add(re, mc.multiply(E, y));
		re = mc.add(re, F);
		return re;
	}
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.planeAG.PlaneCurve#contains(cn.timelives.java.math.planeAG.Point)
	 */
	@Override
	public boolean contains(Point<T> p) {
		return mc.isZero(substitute(p.x,p.y));
	}
	/**
	 * Returns the polar line corresponding to the given point. The point must not be the 
	 * <i>center</i> of this conic section, and if so {@code null} will be returned.
	 * <br>
	 * The polar can be described as :
	 * <ul>
	 * <li>If the point is on this conic section, then it will be the tangent line.
	 * <li>If two tangent lines that pass the point {@code p} exist, the the result will 
	 * be the line connecting the two tangency points.
	 * <li>Otherwise, for each secant line that passes {@code p}, 
	 * there are two intersect points with this conic section, 
	 * and the all intersect point of the two tangent lines from the two 
	 * intersect points will be on <b>the same line</b>, which is the result.
	 * </ul>
	 * @param p a point.
	 * @return the polar line, or {@code null}
	 */
	public Line<T> polarLine(Point<T> p){
		T a = mc.add(mc.multiply(mc.add(A, mc.divideLong(B, 2)), p.x), mc.divideLong(D,2));
		T b = mc.add(mc.multiply(mc.add(C, mc.divideLong(B, 2)), p.y), mc.divideLong(E,2));
		T c = mc.add(mc.divideLong(mc.multiply(D, p.x), 2), 
				mc.add(mc.divideLong(mc.multiply(E, p.y), 2), F));
		if(mc.isZero(a)&&mc.isZero(b)){
			return null;
		}
		return Line.generalFormula(a, b, c, mc);
	}
	
	
	/**
	 * Returns the tangent line of this conic section from point {@code p}.The point must be on this 
	 * conic section. 
	 * @param p
	 * @return the tangent line.
	 * @throws IllegalArgumentException if {@code p} is not on this conic section.
	 */
	public Line<T> tangentLine(Point<T> p){
		if(! contains(p)){
			throw new IllegalArgumentException("Not contain point:"+p);
		}
		return polarLine(p);
		
	}
	
	/**
	 * Returns the corresponding polar point. This method will check whether the line can actually 
	 * be a polar line, throw {@link IllegalArgumentException} if it is invalid.
	 * @param l a line
	 * @return the corresponding polar line.
	 * @see #polarLine(Point)
	 */
	public Point<T> polarPoint(Line<T> l){
		T x = mc.divide(mc.subtract(mc.multiplyLong(l.getA(), 2l), D), mc.subtract(mc.multiplyLong(A, 2l), B));
		T y = mc.divide(mc.subtract(mc.multiplyLong(l.getB(), 2l), E), mc.subtract(mc.multiplyLong(C, 2l), B));
		//check valid
		Point<T> p = Point.valueOf(x, y, mc);
		Line<T> l2 = polarLine(p);
		if(l2!=null && l2.valueEquals(l)){
			return p;
		}
		throw new IllegalArgumentException();
	}
	
	/**
	 * Create the equation of {@code x} from the line and this conic section.this method 
	 * requires that {@code line} is not parallel to Y axis.<p>
	 * This method will return either a {@link QEquation} or {@link LEquation},decided by 
	 * whether the coefficient of x^2 is equal to zero,use {@link SVPEquation#getDegree()} to 
	 * identify it.
	 * @param line a line.
	 * @return an equation of {@code x} indicating the two intersect points of the line 
	 * and this ellipse,or {@code null}.
	 * @throws IllegalArgumentException if k == null
	 */
	public SVPEquation<T> createEquationX(Line<T> line){
		T k = line.slope();
		if(k==null){
			throw new IllegalArgumentException("k==null");
		}
		T b = line.getInterceptY();
		//(A +Bk+Ck^2)
		T ca = mc.add(A, mc.multiply(k, mc.add(B, mc.multiply(C, k))));
		// Bb + 2kbc + Ek + D
		T cb = mc.add(D, mc.add(mc.multiply(b, mc.add(B, mc.multiply(k, C))), mc.multiply(k, mc.add(mc.multiply(b, C), E))));
		//cb^2 + Eb + F
		T cc = mc.add(F, mc.multiply(b, mc.add(mc.multiply(C, b), E)));
		if(mc.isZero(ca)){
//			throw new ArithmeticException("a = 0");
			return SVPEquation.linear(cb, cc, mc);
		}
		return SVPEquation.quadratic(ca, cb, cc, mc);
	}
	
	/**
	 * Create the equation of {@code y} from the line and this conic section.this method 
	 * requires that {@code line} is not parallel to X axis.<p>
	 * This method will return either a {@link QEquation} or {@link LEquation},decided by 
	 * whether the coefficient of y^2 is equal to zero,use {@link SVPEquation#getDegree()} to 
	 * identify it.
	 * @param line a line.
	 * @return an equation of {@code y} indicating the two intersect points of the line 
	 * and this ellipse,or {@code null}.
	 * @throws IllegalArgumentException if k == 0
	 */
	public SVPEquation<T> createEquationY(Line<T> line){
		T at = line.getA();
		if(mc.isZero(at)){
			throw new IllegalArgumentException("k==0");
		}
		T bt = line.getB();
		T k = mc.negate(mc.divide(bt, at));
		// solve an equation:
		T b = line.getInterceptX();
		//(C +Bk+Ak^2)
		T ca = mc.add(C, mc.multiply(k, mc.add(B, mc.multiply(A, k))));
		// Bb + 2kbc + Ek + D
		T cb = mc.add(E, mc.add(mc.multiply(b, mc.add(B, mc.multiply(k, A))), mc.multiply(k, mc.add(mc.multiply(b, A), D))));
		//cb^2 + Eb + F
		T cc = mc.add(F, mc.multiply(b, mc.add(mc.multiply(A, b), D)));
		if(mc.isZero(ca)){
//			throw new ArithmeticException("a = 0");
			return SVPEquation.linear(cb, cc, mc);
		}
		return SVPEquation.quadratic(ca, cb, cc, mc);
	}
	/**
	 * Computes the intersect points with the given line. 
	 * @param line
	 * @return
	 */
	public List<Point<T>> intersectPoints(Line<T> line) {
		SVPEquation<T> equa = null;
		try {
			equa = createEquationX(line);
		}catch(IllegalArgumentException ex) {
		}
		if(equa!= null) {
			if(equa.getDegree()==1){
				List<Point<T>> ps = new ArrayList<>(1);
				T x = ((LEquation<T>)equa).solution();
				T y = line.computeY(x);
				ps.add(new Point<>(mc,x,y));
				return ps;
			}else{
				QEquation<T> eq = (QEquation<T>) equa;
				int ren = 2;
				try{
					ren = eq.getNumberOfRoots();
				}catch(UnsupportedCalculationException ece) {
					
				}
				switch(ren){
				case 0:
					return Collections.emptyList();
				case 1:{
					List<Point<T>> ps = new ArrayList<>(1);
					T x = eq.solve().get(0);
					ps.add(new Point<>(mc,x,line.computeY(x)));
					return ps;
				}
				default:{
					List<Point<T>> ps = new ArrayList<>(2);
					for(T x : eq.solve()){
						ps.add(new Point<>(mc,x,line.computeY(x)));
					}
					return ps;
				}
				}
			}
		}else {
			equa = createEquationY(line);
			if(equa.getDegree() == 1){
				List<Point<T>> ps = new ArrayList<>(1);
				T y = ((LEquation<T>)equa).solution();
				T x = line.computeX(y);
				ps.add(new Point<>(mc,x,y));
				return ps;
			}else{
				QEquation<T> eq = (QEquation<T>) equa;
				int ren = eq.getNumberOfRoots();
				switch(ren){
				case 0:
					return Collections.emptyList();
				case 1:{
					List<Point<T>> ps = new ArrayList<>(1);
					T y = eq.solve().get(0);
					ps.add(new Point<>(mc,line.computeX(y),y));
					return ps;
				}
				default:{
					List<Point<T>> ps = new ArrayList<>(2);
					for(T y : eq.solve()){
						ps.add(new Point<>(mc,line.computeX(y),y));
					}
					return ps;
				}
				}
			}
		}
		
		
	}
	
	/**
	 * Returns the another intersect point of the line and this conic section. Returns null if 
	 * there the line only intersect with this conic section at one point.
	 * @param p
	 * @param line
	 * @return
	 */
	public Point<T> intersectPointAnother(Point<T> p,Line<T> line){
		if((!line.contains(p)) || (! contains(p))) {
			throw new IllegalArgumentException();
		}
		SVPEquation<T> equa = null;
		boolean equationOfX;
		//if line is Ax - C = 0, then the slope doesn't exist,
		//so create the equation with y.
		if(mc.isZero(line.getB())){
			equationOfX = false;
			equa= createEquationY(line);
		}else {
			equationOfX = true;
			equa= createEquationX(line);
		}
		if (equa.getDegree() == 1) {
			return null;
		}
		QEquation<T> eq = (QEquation<T>) equa;
		int degree = 2;
		try {
			degree = eq.getNumberOfRoots();
		} catch (UnsupportedCalculationException ece) {
		}
		if (degree < 2) {
			return null;
		}
		T x, y;
		if (equationOfX) {
			x = mc.subtract(eq.rootsSum(), p.x);
			y = line.computeY(x);
		} else {
			y = mc.subtract(eq.rootsSum(), p.y);
			x = line.computeX(y);
		}
		return Point.valueOf(x, y, mc);
		
	}
	
	/**
	 * Performs transform:
	 * <pre>
	 * x = vx.x*x' + vx.y * y'
	 * y = vy.x*x' + vy.y * y'
	 * </pre>
	 * @param vx
	 * @param vy
	 * @return
	 */
	public ConicSection<T> transform(PVector<T> vx,PVector<T> vy){
		T a = vx.getX(), b=vx.getY(),
			c = vy.getX(),d = vy.getY();
		return transform0(a, b, c, d);
	}
	
	GeneralConicSection<T> transform0(T a,T b,T c,T d){
		T _A = mc.add(mc.add(mc.multiply(A, mc.multiply(a, a)), mc.multiply(B, mc.multiply(a, c))), mc.multiply(C, mc.multiply(c, c)));
		T _C = mc.add(mc.add(mc.multiply(A, mc.multiply(b, b)), mc.multiply(B, mc.multiply(b, d))), mc.multiply(C, mc.multiply(d, d)));
		T _B = mc.add(mc.add(mc.multiply(mc.multiply(B, a), d), mc.multiply(B, mc.multiply(b, c))), 
				mc.add(mc.multiplyLong(mc.multiply(A, mc.multiply(a, b)), 2l),
						mc.multiplyLong(mc.multiply(C, mc.multiply(c, d)), 2l)));
		if(mc.isZero(_A)&&mc.isZero(_C)&&mc.isZero(_B)){
			throw new IllegalArgumentException("A=B=C=0");
		}
		T _D = mc.add(mc.multiply(E, c), mc.multiply(D, a));
		T _E = mc.add(mc.multiply(E, d), mc.multiply(D, b));
		return new GeneralConicSection<T>(mc, _A, _B, _C, _D, _E, F);
	}
	
	/**
	 * Returns the transformed formula of the conic section. This operation 
	 * is a transformation of coordinate:
	 * <pre>tmat * (x,y)<sup>T</sup> = (x',y')<sup>T</sup></pre>
	 * And therefore:
	 * <pre>tmat<sup>-1</sup>*(x',y')<sup>T</sup> = (x,y)<sup>T</sup> 
	 * </pre>
	 * 
	 * @param tmat
	 * @return
	 */
	public ConicSection<T> transform(TransMatrix<T> tmat){
		if(tmat.getRowCount()!=2 || tmat.getColumnCount()!=2){
			throw new IllegalArgumentException("Invalid matrix size!");
		}
		//compute inverse
		tmat = tmat.inverse();
		return transform0(tmat.getNumber(0, 0), tmat.getNumber(0, 1), tmat.getNumber(1, 0), tmat.getNumber(1, 1));
	}
	/**
	 * Performs a translation operation, (moves the conic section toward). 
	 * 
	 * @param d
	 * @param doX determines whether to move along x axis.
	 * @return
	 */
	public ConicSection<T> translate(T d,boolean doX){
		return translate(PVector.valueOf(doX ? d : mc.getZero(), doX ? mc.getZero() : d, mc));
	}
	/**
	 * Performs a translation operation,(moves the conic section toward).
	 * If point A(x,y) is on this conic section, after this operation, 
	 * point A'(x+v.x,y+v.y) will be on this conic section. 
	 * @param d
	 * @param doX
	 * @return
	 */
	public ConicSection<T> translate(PVector<T> v){
		//x: D-2*Aa-Bb
		//y: E-2*Cb-Ba
		//F: Aa^2+Bab+Cb^2-Da-Eb+F
		T x = v.getX(),y = v.getY();
		T _D = mc.subtract(D, mc.add(mc.multiplyLong(mc.multiply(A, x), 2l), mc.multiply(B, y)));
		T _E = mc.subtract(D, mc.add(mc.multiplyLong(mc.multiply(C, y), 2l), mc.multiply(B, x)));
		T _F = expr1.compute(mc, A,B,C,D,E,F,x,y);
		return GeneralConicSection.generalFormula(A, B, C, _D, _E, _F, mc);
	}
	private static final ComputeExpression expr1 = ComputeExpression.compile("$0$6^2+$1$6$7+$2$7^2-$3$6-$4$7+$5");
	
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.planeAG.curve.AbstractPlaneCurve#transform(cn.timelives.java.math.planeAG.PAffineTrans)
	 */
	@Override
	public ConicSection<T> transform(PAffineTrans<T> trans) {
		return transform(trans.getMatrix()).translate(trans.getVector());
	}
	
	/**
	 * Determines the type of this conic section. Returns {@code null} if the type cannot be determined.
	 * @see Type
	 * @return
	 */
	public abstract Type determineType();
	
	
	
	/**
	 * Performs a rotation to make the coefficient of {@literal xy} to zero, 
	 * returns the result conic section.
	 * @return
	 */
	public ConicSection<T> normalize(){
		return normalizeAndTrans().getSecond();
	}
	/**
	 * Performs a rotation to make the coefficient of {@literal xy} to zero, 
	 * returns the transformation matrix and the result conic section.
	 * @return
	 */
	public abstract Pair<TransMatrix<T>,ConicSection<T>> normalizeAndTrans();
	
	/**
	 * Transform this conic section to standard form:
	 * <pre>Ax^2+Cy^2+F=0</pre>
	 * @return
	 */
	public ConicSection<T> toStandardForm(){
		return toStandardFormAndTrans().getSecond();
	}
	/**
	 * Transform this conic section to standard form, and returns the transformation performed and the standard form.
	 * <pre>Ax^2+Cy^2+F=0</pre>
	 * @return
	 */
	public abstract Pair<PAffineTrans<T>,ConicSection<T>> toStandardFormAndTrans();
	
	
	@Override
	public boolean valueEquals(FlexibleMathObject<T> obj) {
		if(obj instanceof ConicSection){
			ConicSection<T> cs = (ConicSection<T>) obj;
			T p ;
			if(mc.isZero(A)){
				//use the 
				p = mc.divide(cs.C, C);
			}else{
				p = mc.divide(cs.A, A);
			}
			if(mc.isZero(p)){
				return false;
			}
			
			return mc.isEqual(mc.multiply(A,p), cs.A) && 
					mc.isEqual(mc.multiply(B,p), cs.B) && 
					mc.isEqual(mc.multiply(C,p), cs.C) && 
					mc.isEqual(mc.multiply(D,p), cs.D) && 
					mc.isEqual(mc.multiply(E,p), cs.E) && 
					mc.isEqual(mc.multiply(F,p), cs.F) ;
		}
		return false;
	}
	
	@Override
	public <N> boolean valueEquals(FlexibleMathObject<N> obj, Function<N, T> mapper) {
		if(obj instanceof ConicSection){
			ConicSection<N> cs = (ConicSection<N>) obj;
			T p ;
			if(mc.isZero(A)){
				//use the 
				p = mc.divide(mapper.apply(cs.C), C);
			}else{
				p = mc.divide(mapper.apply(cs.A), A);
			}
			if(mc.isZero(p)){
				return false;
			}
			
			return mc.isEqual(mc.multiply(A,p), mapper.apply(cs.A)) && 
					mc.isEqual(mc.multiply(B,p), mapper.apply(cs.B)) && 
					mc.isEqual(mc.multiply(C,p), mapper.apply(cs.C)) && 
					mc.isEqual(mc.multiply(D,p), mapper.apply(cs.D)) && 
					mc.isEqual(mc.multiply(E,p), mapper.apply(cs.E)) && 
					mc.isEqual(mc.multiply(F,p), mapper.apply(cs.F)) ;
		}
		return false;
	}

	/* 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + A.hashCode();
		result = prime * result + B.hashCode();
		result = prime * result + C.hashCode();
		result = prime * result + D.hashCode();
		result = prime * result + E.hashCode();
		result = prime * result + F.hashCode();
		return result;
	}

	/* 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if(obj instanceof ConicSection){
			ConicSection<?> cs = (ConicSection<?>)obj;
			return A.equals(cs.A)&&
					B.equals(cs.B)&&
					C.equals(cs.C)&&
					D.equals(cs.D)&&
					E.equals(cs.E)&&
					F.equals(cs.F);
		}
		return false;
	}
	
	@Override
	public ConicSection<T> simplify() {
		return this;
		
	}
	@Override
	public ConicSection<T> simplify(Simplifier<T> sim) {
		List<T> list =sim.simplify(getCoefficients());
		return GeneralConicSection.generalFormula(list, mc);
	}
	
	/**
	 * Returns the general formula.
	 */
	@Override
	public String toString(NumberFormatter<T> nf) {
		StringBuilder sb = new StringBuilder();
		final String str = " + ";
		sb.append("ConicSection : ");
		if(!mc.isZero(A)){
			sb.append('(').append(nf.format(A,mc)).append(")x^2").append(str);
		}
		if(!mc.isZero(B)){
			sb.append('(').append(nf.format(B,mc)).append(")xy").append(str);
		}
		if(!mc.isZero(C)){
			sb.append('(').append(nf.format(C,mc)).append(")y^2").append(str);
		}
		if(!mc.isZero(D)){
			sb.append('(').append(nf.format(D,mc)).append(")x").append(str);
		}
		if(!mc.isZero(E)){
			sb.append('(').append(nf.format(E,mc)).append(")y").append(str);
		}
		if(!mc.isZero(F)){
			sb.append('(').append(nf.format(F,mc)).append(")").append(str);
		}
		sb.delete(sb.length()-str.length()+1, sb.length());
		sb.append("= 0");
		return sb.toString();
	}
//	public static void main(String[] args) {
//		Polynomial p = new Polynomial(FormulaCalculator.getCalculator(),
//				"Ax^2+Bxy+Cy^2+Dx+Ey+F");
//		PolyCalculator pc = PolyCalculator.DEFALUT_CALCULATOR;
//		p = pc.replace("x", p, Polynomial.valueOf("x-a"));
//		p = pc.replace("y", p, Polynomial.valueOf("y-b"));
//		print(p);
//		//x^2
//		BigDecimal TWO = BigDecimal.valueOf(2l),
//				ONE = BigDecimal.ONE,ZERO = BigDecimal.ZERO;
//		printnb("x^2: ");
//		p.getFormulaList().stream().filter(f -> TWO.equals(f.getCharacterPower("x"))).forEach(x -> printnb(x.removeChar("x")));
//		print();
//		printnb("y^2: ");
//		p.getFormulaList().stream().filter(f -> TWO.equals(f.getCharacterPower("y"))).forEach(x -> printnb(x.removeChar("y")));
//		print();
//		printnb("xy: ");
//		p.getFormulaList().stream().filter(f -> ONE.equals(f.getCharacterPower("x")) && ONE.equals(f.getCharacterPower("y")))
//		.forEach(x -> printnb(x.removeChar("y").removeChar("x")));
//		print();
//		printnb("x: ");
//		p.getFormulaList().stream().filter(f -> ONE.equals(f.getCharacterPower("x"))&& f.getCharacterPower("y")==null).forEach(x -> printnb(x.removeChar("x")));
//		print();
//		printnb("y: ");
//		p.getFormulaList().stream().filter(f -> ONE.equals(f.getCharacterPower("y"))&& f.getCharacterPower("x")==null).forEach(x -> printnb(x.removeChar("y")));
//		print();
//		printnb("constant: ");
//		p.getFormulaList().stream().filter(f -> f.getCharacterPower("y") == null && f.getCharacterPower("x")==null).forEach(x -> printnb(x));
//		print();
//		print(p);
//	}
	
}

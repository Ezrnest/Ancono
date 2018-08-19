package cn.timelives.java.math.geometry.analytic.planeAG;

import cn.timelives.java.math.MathObject;
import cn.timelives.java.math.MathUtils;
import cn.timelives.java.math.function.MathFunction;
import cn.timelives.java.math.numberModels.Calculators;
import cn.timelives.java.math.MathCalculator;
import cn.timelives.java.math.numberModels.api.FlexibleNumberFormatter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;

/**
 * Triangle in a plane has three vertexes which are not on a line.The triangle provides 
 * a lot of useful methods to calculate related values or geometric relations.
 * <p>
 * <h1>Side and Vertex</h1>
 * In a triangle,there are three sides and three corresponding vertexes.The three sides are indicated by 
 * character <b>a</b>,<b>b</b>,<b>c</b>,while the three corresponding vertexes are indicated by upper-case 
 * character <i>A</i>,<i>B</i>,<i>C</i>.The following graph shows the relationship of the sides and vertexes.
 * <pre>
 *     <i>A</i>
 *     |\
 *     | \
 *  <b>b</b>  |  \ <b>c</b>
 *     |   \
 *     |    \
 *   <i>C</i> ------ <i>B</i>
 *       <b>a</b>
 * </pre>
 * <h2>Angle</h2>
 * In a triangle,we usually use the vertex to indicate a inner angle.For example, angle {@literal ��}<i>ABC</i> is 
 * called by {@literal ��}<i>B</i>. In this class, as a result of the lack of trigonometric functions in {@linkplain MathCalculator},
 * the related methods usually require a supplementary trigonometric function.If the 
 * @author lyc
 *
 * @param <T>
 */
public final class Triangle<T> extends MathObject<T> {
	
	/**
	 * The three vertexes of this triangle.
	 */
	private final Point<T> A,B,C;
	/**
	 * The three side's line,temperate storage.
	 */
	private Line<T> a,b,c;
	
	/**
	 * The three side's length.
	 */
	private T lenA,lenB,lenC;
	
	
	
	
	protected Triangle(MathCalculator<T> mc,Point<T> a,Point<T> b,Point<T> c) {
		super(mc);
		A = Objects.requireNonNull(a);
		B = Objects.requireNonNull(b);
		C = Objects.requireNonNull(c);
		
	}
	
	private Triangle(MathCalculator<T> mc,Point<T> A,Point<T> B,Point<T> C,
			Line<T> a,Line<T> b,Line<T> c){
		this(mc,A,B,C,a,b,c,null,null,null);
	}
	
	private Triangle(MathCalculator<T> mc,Point<T> A,Point<T> B,Point<T> C,
			Line<T> a,Line<T> b,Line<T> c,T lenA,T lenB,T lenC) {
		super(mc);
		this.a = a;
		this.b = b;
		this.c = c;
		this.A = A;
		this.B = B;
		this.C = C;
		this.lenA = lenA;
		this.lenB = lenB;
		this.lenC = lenC;
		
	}
	
	/**
	 * Get the side <b>a</b> of this triangle.Side <b>a</b> is the corresponding side of vertex <i>A</i>.
	 * @return side <b>a</b>
	 */
	public Line<T> sideA() {
		if(a == null){
            a = Line.twoPoint(B, C, getMc());
		}
		return a;
	}
	
	/**
	 * Get the side <b>b</b> of this triangle.Side <b>b</b> is the corresponding side of vertex <i>B</i>.
	 * @return side <b>b</b>
	 */
	public Line<T> sideB() {
		if(b == null){
            b = Line.twoPoint(A, C, getMc());
		}
		return b;
	}
	
	/**
	 * Get the side <b>c</b> of this triangle.Side <b>c</b> is the corresponding side of vertex <i>C</i>.
	 * @return side <b>c</b>
	 */
	public Line<T> sideC() {
		if(c == null){
            c = Line.twoPoint(A, B, getMc());
		}
		return c;
	}
	
	/**
	 * Get the point of vertex <i>A</i>.
	 * @return vertex <i>A</i>
	 */
	public Point<T> vertexA(){
		return A;
	}
	/**
	 * Get the point of vertex <i>B</i>.
	 * @return vertex <i>B</i>
	 */
	public Point<T> vertexB(){
		return B;
	}
	/**
	 * Get the point of vertex <i>C</i>.
	 * @return vertex <i>C</i>
	 */
	public Point<T> vertexC(){
		return C;
	}
	/**
	 * Returns the length of side <b>a</b>.
	 * @return the length of side <b>a</b>
	 */
	public T lengthA(){
		if(lenA==null){
            lenA = getMc().squareRoot(B.distanceSq(C));
		}
		return lenA;
	}
	/**
	 * Returns the length of side <b>b</b>.
	 * @return the length of side <b>b</b>
	 */
	public T lengthB(){
		if(lenB==null){
            lenB = getMc().squareRoot(A.distanceSq(C));
		}
		return lenB;
	}
	/**
	 * Returns the length of side <b>c</b>.
	 * @return the length of side <b>c</b>
	 */
	public T lengthC(){
		if(lenC==null){
            lenC = getMc().squareRoot(A.distanceSq(B));
		}
		return lenC;
	}
	
	public T angleCosA() {
		PVector<T> v1 = PVector.vector(A, B),
				v2 = PVector.vector(A, C);
		return v1.angleCos(v2);
	}
	
	/**
	 * Returns the area of this triangle,the area is defined by the determinant of this 
	 * triangle of <pre>
	 * |A.x A.y 1|
	 * |B.x B.y 1|
	 * |C.x C.y 1|
	 * </pre>
	 * This method may return a negative value , which indicate that the point order <i>ABC</i> is 
	 * clockwise. 
	 * @return the area of this triangle
	 */
	public T areaPN(){
        T t1 = getMc().multiply(getMc().subtract(A.x, B.x), getMc().subtract(A.y, C.y));
        T t2 = getMc().multiply(getMc().subtract(A.x, C.x), getMc().subtract(B.y, A.y));
        return getMc().add(t1, t2);
	}
	
	/**
	 * Returns the area of this triangle,in geometry meaning.
	 * @return the area of this triangle
	 */
	public T area(){
        return getMc().abs(areaPN());
	}
	
	/**
	 * Returns the gravity center of this triangle,which is the intersect point 
	 * of three central lines.
	 * @return the center of gravity of this triangle.
	 */
	public Point<T> centerG(){
        T x = getMc().divideLong(getMc().addX(A.x, B.x, C.x), 3);
        T y = getMc().divideLong(getMc().addX(A.y, B.y, C.y), 3);
        return new Point<>(getMc(), x, y);
		
	}
	/**
	 * Returns the orthocenter of this triangle,which is the intersect point of three
	 * altitudes.
	 * @return the orthocenter of this triangle
	 */
	public Point<T> centerH(){
        T y2_y1 = getMc().subtract(B.y, A.y);
        T y3_y2 = getMc().subtract(C.y, B.y);
        T y1_y3 = getMc().subtract(A.y, C.y);
        T x2_x1 = getMc().subtract(B.x, A.x);
        T x3_x2 = getMc().subtract(C.x, B.x);
        T x1_x3 = getMc().subtract(A.x, C.x);
        T s = getMc().negate(getMc().add(getMc().add(
                getMc().multiply(A.x, y3_y2),
                getMc().multiply(B.x, y1_y3)),
                getMc().multiply(C.x, y2_y1)));
        T d1 = getMc().add(getMc().multiply(getMc().multiply(A.x, B.x), y2_y1),
                getMc().multiply(getMc().multiply(B.x, C.x), y3_y2));
        d1 = getMc().add(d1, getMc().multiply(getMc().multiply(C.x, A.x), y1_y3));
        d1 = getMc().subtract(d1, getMc().multiply(getMc().multiply(y2_y1, y3_y2), y1_y3));
        T d2 = getMc().add(getMc().multiply(getMc().multiply(A.y, B.y), x2_x1),
                getMc().multiply(getMc().multiply(B.y, C.y), x3_x2));
        d2 = getMc().add(d2, getMc().multiply(getMc().multiply(C.y, A.y), x1_x3));
        d2 = getMc().subtract(d2, getMc().multiply(getMc().multiply(x2_x1, x3_x2), x1_x3));
        d2 = getMc().negate(d2);
        d1 = getMc().divide(d1, s);
        d2 = getMc().divide(d2, s);
        return new Point<T>(getMc(), d1, d2);
	}
	/**
	 * Returns the circumcenter of this triangle,which is the the center of the circumcircle,as well 
	 * as three perpendicular bisectors' intersect point.
	 * @return the circumcenter of this triangle
	 */
	public Point<T> centerO(){
        T y1_y2 = getMc().subtract(A.y, B.y);
        T y2_y3 = getMc().subtract(B.y, C.y);
        T y3_y1 = getMc().subtract(C.y, A.y);
        T x1_x2 = getMc().subtract(A.x, B.x);
        T x2_x3 = getMc().subtract(B.x, C.x);
        T x3_x1 = getMc().subtract(C.x, A.x);

        T s = getMc().multiplyLong(getMc().add(getMc().add(
                getMc().multiply(A.x, y2_y3),
                getMc().multiply(B.x, y3_y1)),
                getMc().multiply(C.x, y1_y2)), 2);
        T n1 = getMc().add(getMc().multiply(y2_y3, getMc().multiply(A.x, A.x)),
                getMc().multiply(y3_y1, getMc().multiply(B.x, B.x)));
        n1 = getMc().add(n1, getMc().multiply(y1_y2, getMc().multiply(C.x, C.x)));
        n1 = getMc().subtract(n1, getMc().multiply(getMc().multiply(y1_y2, y2_y3), y3_y1));
        T n2 = getMc().add(getMc().multiply(x2_x3, getMc().multiply(A.y, A.y)),
                getMc().multiply(x3_x1, getMc().multiply(B.y, B.y)));
        n2 = getMc().add(n2, getMc().multiply(x1_x2, getMc().multiply(C.y, C.y)));
        n2 = getMc().subtract(n2, getMc().multiply(getMc().multiply(x1_x2, x2_x3), x3_x1));
        n2 = getMc().negate(n2);
        n1 = getMc().divide(n1, s);
        n2 = getMc().divide(n2, s);
        return new Point<>(getMc(), n1, n2);
	}
	/**
	 * Returns the incenter of this triangle,which is the the center of the incircle,as well 
	 * as three angular bisectors' intersect point.
	 * @return the incenter of this triangle
	 */
	public Point<T> centerI(){
		lengthA();
		lengthB();
		lengthC();
		//calculate the length
        T nx = getMc().add(getMc().add(getMc().multiply(A.x, lenA), getMc().multiply(B.x, lenB)), getMc().multiply(C.x, lenC));
        T ny = getMc().add(getMc().add(getMc().multiply(A.y, lenA), getMc().multiply(B.y, lenB)), getMc().multiply(C.y, lenC));
        T deno = getMc().add(getMc().add(lenA, lenB), lenC);
        nx = getMc().divide(nx, deno);
        ny = getMc().divide(ny, deno);
        return new Point<>(getMc(), nx, ny);
		
	}
	
	/**
	 * Returns the altitude of side <b>a</b>,which is perpendicular to <b>a</b> and 
	 * passes through vertex <i>A</i>.
	 * @return the altitude of side <b>a</b>
	 */
	public Line<T> altitudeA(){
		Line<T> a = sideA();
		return a.perpendicular(A);
	}
	
	/**
	 * Returns the altitude of side <b>b</b>,which is perpendicular to <b>b</b> and 
	 * passes through vertex <i>B</i>.
	 * @return the altitude of side <b>b</b>
	 */
	public Line<T> altitudeB(){
		Line<T> a = sideB();
		return a.perpendicular(B);
	}
	
	/**
	 * Returns the altitude of side <b>c</b>,which is perpendicular to <b>c</b> and 
	 * passes through vertex <i>C</i>.
	 * @return the altitude of side <b>c</b>
	 */
	public Line<T> altitudeC(){
		Line<T> a = sideC();
		return a.perpendicular(C);
	}
	
	/**
	 * Returns the central line of side <b>a</b>,which is the connecting line 
	 * of the middle point of side <b>a</b> and vertex <i>A</i>.
	 * @return the central line of side <b>a</b>
	 */
	public Line<T> centralLineA(){
		Point<T> m = B.middle(C);
        return Line.twoPoint(m, A, getMc());
	}
	/**
	 * Returns the central line of side <b>b</b>,which is the connecting line 
	 * of the middle point of side <b>b</b> and vertex <i>B</i>.
	 * @return the central line of side <b>b</b>
	 */
	public Line<T> centralLineB(){
		Point<T> m = A.middle(C);
        return Line.twoPoint(m, B, getMc());
	}
	/**
	 * Returns the central line of side <b>c</b>,which is the connecting line 
	 * of the middle point of side <b>c</b> and vertex <i>C</i>.
	 * @return the central line of side <b>c</b>
	 */
	public Line<T> centralLineC(){
		Point<T> m = A.middle(B);
        return Line.twoPoint(m, C, getMc());
	}
	
	/**
	 * Returns the perpendicular bisector of side <b>a</b>,which is perpendicular to side <b>a</b> and 
	 * passes through the middle point of side <b>a</b>.
	 * @return the perpendicular bisector of side <b>a</b>
	 */
	public Line<T> perpendicularBisectorA(){
		Point<T> m = B.middle(C);
		return sideA().perpendicular(m);
	}
	
	/**
	 * Returns the perpendicular bisector of side <b>b</b>,which is perpendicular to side <b>b</b> and 
	 * passes through the middle point of side <b>b</b>.
	 * @return the perpendicular bisector of side <b>b</b>
	 */
	public Line<T> perpendicularBisectorB(){
		Point<T> m = A.middle(C);
		return sideB().perpendicular(m);
	}
	
	/**
	 * Returns the perpendicular bisector of side <b>c</b>,which is perpendicular to side <b>c</b> and 
	 * passes through the middle point of side <b>c</b>.
	 * @return the perpendicular bisector of side <b>c</b>
	 */
	public Line<T> perpendicularBisectorC(){
		Point<T> m = A.middle(B);
		return sideC().perpendicular(m);
	}
	
	/**
	 * Returns the angle <i>A</i>'s angular bisector.This operation requires square root operation in math 
	 * calculator,and may cause exception if the calculator does not support such operation.
	 * @return the angle <i>A</i>'s angular bisector
	 */
	public Line<T> angularBisectorA(){
		lengthB();
		lengthC();
        Point<T> p = B.proportionPoint(C, getMc().divide(lenC, lenB));
        return Line.twoPoint(p, A, getMc());
	}
	/**
	 * Returns the angle <i>B</i>'s angular bisector.This operation requires square root operation in math 
	 * calculator,and may cause exception if the calculator does not support such operation.
	 * @return the angle <i>B</i>'s angular bisector
	 */
	public Line<T> angularBisectorB(){
		lengthA();
		lengthC();
        Point<T> p = A.proportionPoint(C, getMc().divide(lenC, lenA));
        return Line.twoPoint(p, B, getMc());
	}
	/**
	 * Returns the angle <i>A</i>'s angular bisector.This operation requires square root operation in math 
	 * calculator,and may cause exception if the calculator does not support such operation.
	 * @return the angle <i>A</i>'s angular bisector
	 */
	public Line<T> angularBisectorC(){
		lengthA();
		lengthB();
        Point<T> p = A.proportionPoint(B, getMc().divide(lenB, lenA));
        return Line.twoPoint(p, C, getMc());
	}
	
	void assignLenA(T len){
		lenA = len;
	}
	void assignLenB(T len){
		lenB = len;
	}
	void assignLenC(T len){
		lenC = len;
	}


	/**
	 * Determines whether the given point is inside or on the edge of this triangle.
	 * @param p
	 * @return
	 */
	public boolean contains(Point<T> p){
		PVector<T> v = A.directVector(p);
		PVector<T> AB = A.directVector(B),
				AC = A.directVector(C);
		PVector<T> xy = v.reduce(AB,AC);
        T zero = getMc().getZero(), one = getMc().getOne();
        return MathUtils.oppositeSide(zero, one, xy.x, getMc()) && Calculators.between(zero, one, xy.y, getMc());
	}


	/**
	 * Transforms this triangle, the transformation must be invertible.
	 * @param trans
	 * @return
	 */
	public Triangle<T> transform(PAffineTrans<T> trans){
		if(!trans.isInvertible()){
			throw new IllegalArgumentException("Trans isn't invertible.");
		}
		Point<T> nA = trans.apply(A),
				nB = trans.apply(B),
				nC = trans.apply(C);
        return new Triangle<>(getMc(), nA, nB, nC);
	}

	@Override
    public <N> Triangle<N> mapTo(@NotNull Function<T, N> mapper, @NotNull MathCalculator<N> newCalculator) {
		return new Triangle<>(newCalculator, A.mapTo(mapper, newCalculator),
								B.mapTo(mapper, newCalculator),
								C.mapTo(mapper, newCalculator),
								a == null? null : a.mapTo(mapper, newCalculator),
								b == null? null : b.mapTo(mapper, newCalculator),
								c == null? null : c.mapTo(mapper, newCalculator),
								lenA == null ? null : mapper.apply(lenA),
								lenB == null ? null : mapper.apply(lenB),
								lenC == null ? null : mapper.apply(lenC));
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == this){
			return true;
		}
		if(obj instanceof Triangle){
			Triangle<?> tr = (Triangle<?>)obj;
			return A.equals(tr.A) && 
					B.equals(tr.B) && 
					C.equals(tr.C);
					
		}
		return false;
	}
	
	@Override
	public int hashCode() {
        int h = getMc().hashCode();
		h = h*31 + A.hashCode();
		h = h*31 + B.hashCode();
		h = h*31 + C.hashCode();
		return h;
	}
	
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.FlexibleMathObject#toString(cn.timelives.java.math.number_models.NumberFormatter)
	 */
	@Override
    public String toString(@NotNull FlexibleNumberFormatter<T, MathCalculator<T>> nf) {
		return "Triangle: A:"+A.toString(nf)+" B:"+B.toString(nf)+" C:"+C.toString(nf);
	}
	@Override
    public <N> boolean valueEquals(@NotNull MathObject<N> obj, @NotNull Function<N, T> mapper) {
		if(obj instanceof Triangle){
			Triangle<N> tri = (Triangle<N>)obj;
			T x = mapper.apply(tri.A.x);
			T y = mapper.apply(tri.A.y);
            if (getMc().isEqual(x, A.x) == false || getMc().isEqual(y, A.y) == false) {
				return false;
			}
			x = mapper.apply(tri.B.x);
			y = mapper.apply(tri.B.y);
            if (getMc().isEqual(x, B.x) == false || getMc().isEqual(y, B.y) == false) {
				return false;
			}
			x = mapper.apply(tri.C.x);
			y = mapper.apply(tri.C.y);
            if (getMc().isEqual(x, C.x) == false || getMc().isEqual(y, C.y) == false) {
				return false;
			}
			return true;
		}
		return false;
	}
	
	@Override
    public boolean valueEquals(@NotNull MathObject<T> obj) {
		if(obj == this){
			return true;
		}
		if(obj instanceof Triangle){
			Triangle<T> tri = (Triangle<T>)obj;
            return getMc().isEqual(A.x, tri.A.x) && getMc().isEqual(A.y, tri.A.y) &&
                    getMc().isEqual(B.x, tri.B.x) && getMc().isEqual(B.y, tri.B.y) &&
                    getMc().isEqual(C.x, tri.C.x) && getMc().isEqual(C.y, tri.C.y);
		}
		return false;
	}
	
	/**
	 * Returns a triangle using the calculator {@code mc} and its vertexes are in 
	 * the given coordinates.
	 * @param mc a math calculator
	 * @param ax X coordinate of vertex A
	 * @param ay Y coordinate of vertex A
	 * @param bx X coordinate of vertex B
	 * @param by Y coordinate of vertex B
	 * @param cx X coordinate of vertex C
	 * @param cy Y coordinate of vertex C
	 * @return a newly created triangle
	 */
	public static <T> Triangle<T> fromVertex(MathCalculator<T> mc,T ax,T ay,T bx,T by,T cx,T cy){
		Point<T> A = new Point<T>(mc,ax,ay);
		Point<T> B = new Point<T>(mc,bx,by);
		Point<T> C = new Point<T>(mc,cx,cy);
        //must check the three point is not on a identity line.
		
		
		Triangle<T> tri =  new Triangle<>(mc, A, B, C);
		if(mc.isZero(tri.areaPN())){
			throw new IllegalArgumentException("Three points a line.");
		}
		return tri;
	}
	/**
	 * Returns a triangle using the calculator {@code mc} and its vertexes are  
	 * the given points.The math calculator will be reset to the given calculator {@code mc}.
	 * @param mc
	 * @param A vertex <i>A</i>
	 * @param B vertex <i>B</i>
	 * @param C vertex <i>C</i>
	 * @return a newly created triangle
	 */
	public static <T> Triangle<T> fromVertex(MathCalculator<T> mc,Point<T> A,Point<T> B,Point<T> C){
		Function<T,T> mapper = MathFunction.identity();
		Triangle<T> tri = new Triangle<>(mc, A.mapTo(mapper, mc),
								B.mapTo(mapper, mc),
								C.mapTo(mapper, mc));
		if(mc.isZero(tri.areaPN())){
			throw new IllegalArgumentException("Three points a line.");
		}
		return tri;
	}
	/**
	 * Returns a triangle using the calculator {@code mc} and its vertexes are  
	 * the given points.The math calculator will be reset to the given calculator {@code mc}.
	 * @param A vertex <i>A</i>
	 * @param B vertex <i>B</i>
	 * @param C vertex <i>C</i>
	 * @return a newly created triangle
	 */
	public static <T> Triangle<T> fromVertex(Point<T> A,Point<T> B,Point<T> C){
		MathCalculator<T> mc = A.getMathCalculator();
		Triangle<T> tri = new Triangle<>(mc, A, B, C);
		if(mc.isZero(tri.areaPN())){
			throw new IllegalArgumentException("Three points a line.");
		}
		return tri;
	}
	/**
	 * Returns a triangle whose vertexes are the three intersect point of these three lines.If either 
     * there are two parallel line or three lines have the identity intersect point,exception will be
	 * thrown. 
	 * <p>
	 * The triangle will use the given math calculator instead of using one from one of the three lines.
	 * @param mc a math calculator
	 * @param a the line equation of side <b>a</b>
	 * @param b the line equation of side <b>b</b>
	 * @param c the line equation of side <b>c</b>
	 * @return a triangle
	 * @throws ArithmeticException if these three lines cannot intersect into a triangle
	 */
	public static <T> Triangle<T> fromSide(MathCalculator<T> mc,Line<T> a,Line<T> b,Line<T> c){
		//calculate the three intersect point
		Point<T> A = b.intersectPoint(c);
		Point<T> B = c.intersectPoint(a);
		Point<T> C = a.intersectPoint(b);
		if(A==null||B==null||C==null){
			throw new ArithmeticException("Parallel lines");
		}
		if(A.valueEquals(B)){
			throw new ArithmeticException("Same point");
		}
		return new Triangle<>(mc,A,B,C,a,b,c);
	}

	

}

package cn.ancono.math.geometry.analytic.space;

import cn.ancono.math.MathCalculator;
import cn.ancono.math.MathObject;
import cn.ancono.math.geometry.analytic.plane.Triangle;
import cn.ancono.utilities.ArraySup;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Describes a triangle on the space
 *
 * @param <T>
 * @author liyicheng
 */
public final class STriangle<T> extends SpacePlaneObject<T> {

    private final Segment<T> c, a, b;

    private final SPoint<T> A, B, C;
    // vector restrict :
    /* c = AB,a = BC. b = CA
     *
     */
    //often-used variable
    private T area;

    protected STriangle(MathCalculator<T> mc, Plane<T> p, SPoint<T> A, SPoint<T> B, SPoint<T> C,
                        Segment<T> a, Segment<T> b, Segment<T> c) {
        super(mc, p);
        this.c = c;
        this.b = b;
        this.a = a;
        this.A = A;
        this.B = B;
        this.C = C;
    }


    /**
     * Determines whether the point is in this triangle, include edges.
     */
    @Override
    public boolean contains(SPoint<T> point) {
        if (!pl.contains(point)) {
            return false;
        }
        var mc = getMc();
        SVector<T> vt = SVector.vector(A, point);
        SVector<T> v1 = c.getDirectVector(),
                v2 = b.getDirectVector();
        T D = mc.subtract(mc.multiply(v1.y, v2.x), mc.multiply(v1.x, v2.y));
        T Dx = mc.subtract(mc.multiply(vt.y, v2.x), mc.multiply(vt.x, v2.y));
        T Dy = mc.subtract(mc.multiply(v1.x, vt.y), mc.multiply(v1.y, vt.x));
        T x = mc.divide(Dx, D);
        T y = mc.divide(Dy, D);
        T zero = mc.getZero();
        if (mc.compare(x, zero) >= 0 && mc.compare(y, zero) >= 0 && mc.compare(mc.add(y, x), mc.getOne()) <= 0) {
            return true;
        }
        return false;
    }

    /**
     * Get the plane of this triangle
     *
     * @return a plane
     */
    @Override
    public Plane<T> getPlane() {
        return pl;
    }

    /**
     * Get the vertex A of this triangle
     *
     * @return a point
     */
    public SPoint<T> getA() {
        return A;
    }

    /**
     * Get the vertex Bof this triangle
     *
     * @return a point
     */
    public SPoint<T> getB() {
        return B;
    }

    /**
     * Get the vertex C of this triangle
     *
     * @return a point
     */
    public SPoint<T> getC() {
        return C;
    }

    /**
     * Gets the edge a of this triangle.
     *
     * @return a segment
     */
    public Segment<T> getEdgeA() {
        return a;
    }

    /**
     * Gets the edge b of this triangle.
     *
     * @return a segment
     */
    public Segment<T> getEdgeB() {
        return b;
    }

    /**
     * Gets the edge c of this triangle.
     *
     * @return a segment
     */
    public Segment<T> getEdgeC() {
        return c;
    }

    /**
     * Returns the length of side <b>a</b>.
     *
     * @return the length of side <b>a</b>
     */
    public T getLengthA() {
        return a.getLength();
    }

    /**
     * Returns the length of side <b>b</b>.
     *
     * @return the length of side <b>b</b>
     */
    public T getLengthB() {
        return b.getLength();
    }

    /**
     * Returns the length of side <b>c</b>.
     *
     * @return the length of side <b>c</b>
     */
    public T getLengthC() {
        return c.getLength();
    }


    /**
     * Returns the area of this triangle,in geometry meaning.
     *
     * @return the area of this triangle
     */
    public T area() {
        if (area == null) {
            area = getMc().divideLong(
                    a.getDirectVector()
                            .outerProduct(
                                    c.getDirectVector()).norm(), 2l);
        }
        return area;
    }

    public T areaSq() {
        return getMc().divideLong(a.getDirectVector().outerProduct(c.getDirectVector()).normSq(), 4l);
    }

    /**
     * Returns the gravity center of this triangle,which is the intersect point
     * of three central lines.
     *
     * @return the center of gravity of this triangle.
     */
    public SPoint<T> centerG() {
        var mc = getMc();
        T x = mc.divideLong(mc.addX(A.x, B.x, C.x), 3);
        T y = mc.divideLong(mc.addX(A.y, B.y, C.y), 3);
        T z = mc.divideLong(mc.addX(A.z, B.z, C.z), 3);
        return new SPoint<>(mc, x, y, z);
    }

//	/**
//	 * Returns the orthocenter of this triangle,which is the intersect point of three
//	 * altitudes.
//	 * @return the orthocenter of this triangle
//	 */
//	public Point<T> centerH(){
//		T y2_y1 = mc.subtract(B.y, A.y);
//		T y3_y2 = mc.subtract(C.y, B.y);
//		T y1_y3 = mc.subtract(A.y, C.y);
//		T x2_x1 = mc.subtract(B.x, A.x);
//		T x3_x2 = mc.subtract(C.x, B.x);
//		T x1_x3 = mc.subtract(A.x, C.x);
//		T s = mc.negate(mc.add(mc.add(
// 				mc.multiply(A.x, y3_y2),
// 				mc.multiply(B.x, y1_y3)),
// 				mc.multiply(C.x, y2_y1)));
//		T d1 = mc.add(mc.multiply(mc.multiply(A.x,B.x),y2_y1),
//					mc.multiply(mc.multiply(B.x,C.x),y3_y2));
//			d1 = mc.add(d1, mc.multiply(mc.multiply(C.x,A.x),y1_y3));
//			d1 = mc.subtract(d1,mc.multiply(mc.multiply(y2_y1, y3_y2),y1_y3));
//		T d2 = mc.add(mc.multiply(mc.multiply(A.y,B.y),x2_x1),
//				mc.multiply(mc.multiply(B.y,C.y),x3_x2));
//			d2 = mc.add(d2, mc.multiply(mc.multiply(C.y,A.y),x1_x3));
//			d2 = mc.subtract(d2,mc.multiply(mc.multiply(x2_x1, x3_x2),x1_x3));
//			d2 = mc.negate(d2);
//		d1 = mc.divide(d1, s);
//		d2 = mc.divide(d2, s);
//		return new Point<T>(mc,d1,d2);
//	}
//	
//	private T centerH0(T a,T b,T c){
//		
//	}

    /**
     * Returns the incenter of this triangle,which is the the center of the incircle,as well
     * as three angular bisectors' intersect point.
     *
     * @return the incenter of this triangle
     */
    public SPoint<T> centerI() {
        //calculate the length
        var mc = getMc();
        T nx = mc.add(mc.add(mc.multiply(A.x, a.getLength()), mc.multiply(B.x, b.getLength())),
                mc.multiply(C.x, c.getLength()));
        T ny = mc.add(mc.add(mc.multiply(A.y, a.getLength()), mc.multiply(B.y, b.getLength())),
                mc.multiply(C.y, c.getLength()));
        T nz = mc.add(mc.add(mc.multiply(A.z, a.getLength()), mc.multiply(B.z, b.getLength())),
                mc.multiply(C.z, c.getLength()));
        T deno = mc.add(mc.add(a.getLength(), b.getLength()), c.getLength());
        nx = mc.divide(nx, deno);
        ny = mc.divide(ny, deno);
        nz = mc.divide(nz, deno);
        return new SPoint<>(mc, nx, ny, nz);
    }

    /**
     * Returns the altitude of side <b>a</b>,which is perpendicular to <b>a</b> and
     * passes through vertex <i>A</i>.
     *
     * @return the altitude of side <b>a</b>
     */
    public Line<T> altitudeA() {
        Line<T> a = this.a.getLine();
        return a.perpendicular(A);
    }

    /**
     * Returns the altitude of side <b>b</b>,which is perpendicular to <b>b</b> and
     * passes through vertex <i>B</i>.
     *
     * @return the altitude of side <b>b</b>
     */
    public Line<T> altitudeB() {
        Line<T> a = this.b.getLine();
        return a.perpendicular(B);
    }

    /**
     * Returns the altitude of side <b>c</b>,which is perpendicular to <b>c</b> and
     * passes through vertex <i>C</i>.
     *
     * @return the altitude of side <b>c</b>
     */
    public Line<T> altitudeC() {
        Line<T> a = this.c.getLine();
        return a.perpendicular(C);
    }

    /**
     * Returns the central line of side <b>a</b>,which is the connecting line
     * of the middle point of side <b>a</b> and vertex <i>A</i>.
     *
     * @return the central line of side <b>a</b>
     */
    public Line<T> centralLineA() {
        SPoint<T> m = B.middle(C);
        return Line.twoPoints(m, A);
    }

    /**
     * Returns the central line of side <b>b</b>,which is the connecting line
     * of the middle point of side <b>b</b> and vertex <i>B</i>.
     *
     * @return the central line of side <b>b</b>
     */
    public Line<T> centralLineB() {
        SPoint<T> m = A.middle(C);
        return Line.twoPoints(m, B);
    }

    /**
     * Returns the central line of side <b>c</b>,which is the connecting line
     * of the middle point of side <b>c</b> and vertex <i>C</i>.
     *
     * @return the central line of side <b>c</b>
     */
    public Line<T> centralLineC() {
        SPoint<T> m = A.middle(B);
        return Line.twoPoints(m, C);
    }

    /**
     * Returns the perpendicular bisector of side <b>a</b>,which is perpendicular to side <b>a</b> and
     * passes through the middle point of side <b>a</b>.
     *
     * @return the perpendicular bisector of side <b>a</b>
     */
    public Line<T> perpendicularBisectorA() {
        SPoint<T> m = B.middle(C);
        return a.getLine().perpendicular(m);
    }

    /**
     * Returns the perpendicular bisector of side <b>b</b>,which is perpendicular to side <b>b</b> and
     * passes through the middle point of side <b>b</b>.
     *
     * @return the perpendicular bisector of side <b>b</b>
     */
    public Line<T> perpendicularBisectorB() {
        SPoint<T> m = A.middle(C);
        return b.getLine().perpendicular(m);
    }

    /**
     * Returns the perpendicular bisector of side <b>c</b>,which is perpendicular to side <b>c</b> and
     * passes through the middle point of side <b>c</b>.
     *
     * @return the perpendicular bisector of side <b>c</b>
     */
    public Line<T> perpendicularBisectorC() {
        SPoint<T> m = A.middle(B);
        return c.getLine().perpendicular(m);
    }

    /**
     * Returns the angle <i>A</i>'s angular bisector.This operation requires square root operation in math
     * calculator,and may cause exception if the calculator does not support such operation.
     *
     * @return the angle <i>A</i>'s angular bisector
     */
    public Line<T> angularBisectorA() {
        SPoint<T> p = B.proportionPoint(C, getMc().divide(c.getLength(), b.getLength()));
        return Line.twoPoints(p, A);
    }

    /**
     * Returns the angle <i>B</i>'s angular bisector.This operation requires square root operation in math
     * calculator,and may cause exception if the calculator does not support such operation.
     *
     * @return the angle <i>B</i>'s angular bisector
     */
    public Line<T> angularBisectorB() {
        SPoint<T> p = A.proportionPoint(C, getMc().divide(c.getLength(), a.getLength()));
        return Line.twoPoints(p, B);
    }

    /**
     * Returns the angle <i>A</i>'s angular bisector.This operation requires square root operation in math
     * calculator,and may cause exception if the calculator does not support such operation.
     *
     * @return the angle <i>A</i>'s angular bisector
     */
    public Line<T> angularBisectorC() {
        SPoint<T> p = A.proportionPoint(B, getMc().divide(b.getLength(), a.getLength()));
        return Line.twoPoints(p, C);
    }

    /**
     * Determines whether the two triangle is the identity, regardless of the order of
     * the edges.
     *
     * @param s
     * @return
     */
    @SuppressWarnings("unchecked")
    public boolean valueEqualNoOrder(STriangle<T> s) {
        SPoint<T>[] a1 = new SPoint[]{A, B, C};
        SPoint<T>[] a2 = new SPoint[]{s.A, s.B, s.C};
        return ArraySup.arrayEqualNoOrder(a1, a2, (x, y) -> x.valueEquals(y));
    }

    /**
     * Returns a new STriangle <tt>A1B1C1</tt>,  <pre>
     * A -> A1
     * B -> C1
     * C -> B1
     * </pre>
     *
     * @return
     */
    public STriangle<T> changeOrderReverse() {
        STriangle<T> s = new STriangle<>(getMc(), pl, A, C, B, c.reverse(), b.reverse(), a.reverse());
        fillField(s);
        return s;
    }

    /**
     * Returns a new STriangle <tt>A1B1C1</tt>, if forward<pre>
     * A -> B1
     * B -> C1
     * C -> A1
     * </pre>
     * otherwise backward<pre>
     * A -> C1
     * B -> A1
     * C -> B1
     * </pre>
     *
     * @return
     */
    public STriangle<T> changeOrderMove(boolean forward) {
        STriangle<T> s;
        if (forward) {
            s = new STriangle<>(getMc(), pl, C, A, B, c, a, b);
        } else {
            s = new STriangle<>(getMc(), pl, B, C, A, b, c, a);
        }
        fillField(s);
        return s;
    }

    private void fillField(STriangle<T> s) {
        s.area = area;
    }

    private <N> void fillField(STriangle<N> s, Function<T, N> mapper) {
        s.area = area == null ? null : mapper.apply(area);
    }

    @NotNull
    @Override
    public <N> STriangle<N> mapTo(@NotNull MathCalculator<N> newCalculator, @NotNull Function<T, N> mapper) {
        STriangle<N> s = new STriangle<>(newCalculator, pl.mapTo(newCalculator, mapper),
                A.mapTo(newCalculator, mapper), B.mapTo(newCalculator, mapper), C.mapTo(newCalculator, mapper),
                a.mapTo(newCalculator, mapper), b.mapTo(newCalculator, mapper), c.mapTo(newCalculator, mapper));
        fillField(s, mapper);
        return s;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof STriangle) {
            STriangle<?> s = (STriangle<?>) obj;
            return A.equals(s.A) && B.equals(s.B) && C.equals(s.C);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = A.hashCode();
        hash = hash * 31 + B.hashCode();
        hash = hash * 31 + C.hashCode();
        return hash;
    }

    @Override
    public boolean valueEquals(@NotNull MathObject<T> obj) {
        if (obj instanceof STriangle) {
            if (this == obj) {
                return true;
            }
            STriangle<T> s = (STriangle<T>) obj;
            return A.valueEquals(s.A) && B.valueEquals(s.B) && C.valueEquals(s.C);
        }
        return false;
    }

    @Override
    public <N> boolean valueEquals(@NotNull MathObject<N> obj, @NotNull Function<N, T> mapper) {
        if (obj instanceof STriangle) {
            STriangle<N> s = (STriangle<N>) obj;
            return A.valueEquals(s.A, mapper) && B.valueEquals(s.B, mapper) && C.valueEquals(s.C, mapper);
        }
        return false;
    }

    /**
     * Returns a triangle on this plane and
     *
     * @param pcc
     * @return
     */
    public Triangle<T> toPlaneTriangle(Plane.PlaneCoordinateConverter<T> pcc) {
        return pcc.toPlaneTriangle(this);
    }

    /* Returns
     * <pre>
     * Triangle: A(x,y,z) B(x,y,z) C(x,y,z)
     *  </pre>
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Triangle: A");
        sb.append(A.toString());
        sb.append(" B").append(B.toString()).append(" C").append(C.toString());
        return sb.toString();
    }

    /**
     * Creates a new STriangle.
     * <p>The {@link MathCalculator} will be taken from the first parameter of {@link MathObject}
     *
     * @param A
     * @param B
     * @param C
     * @return a new triangle in space
     */
    public static <T> STriangle<T> vertex(SPoint<T> A, SPoint<T> B, SPoint<T> C) {
        Segment<T> c = Segment.twoPoints(A, B),
                a = Segment.twoPoints(B, C),
                b = Segment.twoPoints(C, A);
        SVector<T> ab = c.getDirectVector(),
                bc = a.getDirectVector();
        Plane<T> p = Plane.vectorPoint(ab, bc, A);
        return new STriangle<>(A.getMathCalculator(), p, A, B, C, a, b, c);
    }

    /**
     * Creates a new STriangle with three sides a,b,c. The end point must be the identity.
     *
     * @param a
     * @param b
     * @param c
     * @return a new triangle in space
     */
    public static <T> STriangle<T> sides(Segment<T> a, Segment<T> b, Segment<T> c) {
        SPoint<T> A = a.getEndPointA(), B = b.getEndPointA(), C = c.getEndPointA();
        if (!A.valueEquals(c.getEndPointB()) || !B.valueEquals(a.getEndPointB()) || !C.valueEquals(b.getEndPointB())) {
            throw new IllegalArgumentException("End point not the identity");
        }
        Plane<T> p = Plane.vectorPoint(a.getDirectVector(), c.getDirectVector(), A);
        return new STriangle<>(A.getMathCalculator(), p, A, B, C, a, b, c);
    }

    private static <T> STriangle<T> sides0(Segment<T> a, Segment<T> b, Segment<T> c) {
        SPoint<T> A = a.getEndPointA(), B = b.getEndPointA(), C = c.getEndPointA();
        Plane<T> p = Plane.vectorPoint(a.getDirectVector(), c.getDirectVector(), A);
        return new STriangle<>(A.getMathCalculator(), p, A, B, C, a, b, c);
    }

    /**
     * @param p
     * @param points
     * @param mc
     * @return
     * @see #prismSurfaces(SPoint, List)
     */
    public static <T> List<STriangle<T>> prismSurfaces(SPoint<T> p, List<SPoint<T>> points, MathCalculator<T> mc) {
        if (points.size() < 2) {
            throw new IllegalArgumentException("Not enough point");
        }
        SPoint<T> last = null;
        SPoint<T> first = null;
        List<STriangle<T>> re = new ArrayList<>(points.size() + 1);
        Segment<T> lastS = null, firstS = null;
        for (SPoint<T> s : points) {
            Segment<T> cur = Segment.twoPoints(p, s);
            if (first == null) {
                first = s;
                firstS = cur;
            } else {
                Segment<T> t = Segment.twoPoints(s, last);
                re.add(sides0(lastS, cur, t));
            }
            last = s;
            lastS = cur.reverse();
        }
        re.add(sides0(lastS, firstS, Segment.twoPoints(first, last)));
        return re;
    }

    /**
     * Create a list of triangles, the number of the triangle will be equal to the length of {@code points} plus
     * one. <br>
     * The order of the triangles is specified, as well as the vertexes. Assume the points in {@code points} are
     * named <tt>P0,P1,P2,...</tt>, then the first triangle returned will be <tt>P0-P-P1</tt>, which the vertexA of this
     * triangle will be <tt>P0</tt>
     * <p>The {@link MathCalculator} will be taken from the first parameter of {@link MathObject}
     *
     * @param p
     * @param points
     * @param mc
     * @return
     */
    public static <T> List<STriangle<T>> prismSurfaces(SPoint<T> p, List<SPoint<T>> points) {
        return prismSurfaces(p, points, points.iterator().next().getMathCalculator());
    }

    /**
     * Create a list of triangles, the number of the triangle will be equal to the length of {@code points} plus
     * one. <br>
     * The order of the triangles is specified, as well as the vertexes. Assume the points in {@code points} are
     * named <tt>P0,P1,P2,...</tt>, then the first triangle returned will be <tt>P0-P-P1</tt>, which the vertexA of this
     * triangle will be <tt>P0</tt>
     *
     * @param p
     * @param points
     * @return
     */
    @SafeVarargs
    public static <T> List<STriangle<T>> prismSurfaces(SPoint<T> p, SPoint<T>... points) {
        if (points.length < 2) {
            throw new IllegalArgumentException("Not enough point");
        }
        List<STriangle<T>> re = new ArrayList<>(points.length + 1);
        Segment<T> lastS = Segment.twoPoints(points[0], p);
        Segment<T> first = lastS.reverse();
        for (int i = 1; i < points.length; i++) {
            Segment<T> cur = Segment.twoPoints(p, points[i]),
                    t = Segment.twoPoints(points[i], points[i - 1]);
            re.add(sides0(lastS, cur, t));
            lastS = cur.reverse();
        }
        re.add(sides0(lastS, first, Segment.twoPoints(points[0], points[points.length - 1])));
        return re;
    }


//	public static void main(String[] args) {
//		MathCalculator<Double> mc = Calculators.getCalculatorDouble();
//		STriangle<Double> t1 = 
//				STriangle.vertex(SPoint.valueOf(0d, 0d, 0d, mc), 
//						SPoint.valueOf(0d, 1d, 0d, mc), 
//						SPoint.valueOf(1d, 0d, 0d, mc)),
//				t2 = STriangle.vertex(SPoint.valueOf(0d, 0d, 0d, mc),SPoint.valueOf(1d, 0d, 0d, mc), 
//						SPoint.valueOf(0d, 1d, 0d, mc)
//						);
//		Printer.print(t1.contains(SPoint.valueOf(0.5d, 0.6d, 0d, mc)));
//		Printer.print(t1.equals(t2)+"..."+t1.valueEqualNoOrder(t2));
//		
//	}
}

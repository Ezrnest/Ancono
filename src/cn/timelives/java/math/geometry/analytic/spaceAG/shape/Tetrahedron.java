
package cn.timelives.java.math.geometry.analytic.spaceAG.shape;

import cn.timelives.java.math.FieldMathObject;
import cn.timelives.java.math.exceptions.UnsatisfiedCalculationResultException;
import cn.timelives.java.math.geometry.analytic.spaceAG.*;
import cn.timelives.java.math.numberModels.MathCalculator;
import cn.timelives.java.math.spaceAG.*;
import cn.timelives.java.utilities.ArraySup;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
/**
 * Tetrahedron is a kind of space object with four vertexes, six edges and four surfaces which are all triangles.<p>
 * The points are generally names <tt>P,A,B,C</tt>. The point <tt>P</tt> is the top and the triangle 
 * at the bottom consists of vertexes <tt>A B C</tt>.
 *  
 * 
 * @author liyicheng
 * @param <T>
 */
public final class Tetrahedron<T> extends Pyramid<T>{
	private static final int PLANE_NUM = 4,EGDE_NUM = 6,VERTEX_NUM = 4;
	private STriangle<T> bot,f1,f2,f3;
	@SuppressWarnings("unchecked")
	private SPoint<T>[] ps = (SPoint<T>[]) new SPoint<?>[VERTEX_NUM];
	@SuppressWarnings("unchecked")
	private Segment<T>[] es = (Segment<T>[]) new Segment<?>[EGDE_NUM];
	@SuppressWarnings("unchecked")
	private Plane<T>[] pls = (Plane<T>[]) new Plane<?>[PLANE_NUM];
	/**
	 * @param mc
	 * @param num
	 */
	protected Tetrahedron(MathCalculator<T> mc,
			STriangle<T> bot,STriangle<T> f1,STriangle<T> f2,STriangle<T> f3) {
		super(mc, 3);
		ps[0] = f1.getB();
		ps[1] = bot.getA();
		ps[2] = bot.getB();
		ps[3] = bot.getC();
		
		es[0] = bot.getEdgeA();
		es[1] = bot.getEdgeB();
		es[2] = bot.getEdgeC();
		es[3] = f1.getEdgeA();
		es[4] = f2.getEdgeA();
		es[5] = f3.getEdgeA();
		
		pls[0] = bot.getPlane();
		pls[1] = f1.getPlane();
		pls[2] = f2.getPlane();
		pls[3] = f3.getPlane();
		
		this.bot = bot;
		this.f1 = f1;
		this.f2 = f2;
		this.f3 = f3;
	}



	/* (non-Javadoc)
	 * @see cn.timelives.java.utilities.math.spaceAG.shape.Polyhedron#isVertex(cn.timelives.java.utilities.math.spaceAG.SPoint)
	 */
	@Override
	public boolean isVertex(SPoint<T> p) {
		return ArraySup.arrayContains(ps, p, (a,b)->a.valueEquals(b));
	}

	/**
	 * 
	 */
	@Override
	public boolean isEdge(Line<T> line) {
		return ArraySup.arrayContains(es, line, (Segment<T> s,Line<T> l)->s.getLine().valueEquals(l));
	}
	
	/* (non-Javadoc)
	 * @see cn.timelives.java.utilities.math.spaceAG.shape.Polyhedron#isSurface(cn.timelives.java.utilities.math.spaceAG.Plane)
	 */
	@Override
	public boolean isSurface(Plane<T> p) {
		return ArraySup.arrayContains(pls, p, (p1,p2)->p1.valueEquals(p2));
	}

	/**
	 * Returns a set of vertexes.
	 */
	@Override
	public Set<SPoint<T>> getVertexes() {
		return ArraySup.createSet(ps);
	}

	/* (non-Javadoc)
	 * @see cn.timelives.java.utilities.math.spaceAG.shape.Polyhedron#getEdges()
	 */
	@Override
	public Set<Segment<T>> getEdges() {
		Set<Segment<T>> set = new HashSet<>(EGDE_NUM);
		for(int i=0;i<EGDE_NUM;i++){
			set.add(es[i]);
		}
		return set;
	}

	/* (non-Javadoc)
	 * @see cn.timelives.java.utilities.math.spaceAG.shape.Polyhedron#getSurfaces()
	 */
	@Override
	public Set<Plane<T>> getSurfaces() {
		return ArraySup.createSet(pls);
	}
	/**
	 * Gets the vertex <tt>P</tt>.
	 * @return vertex <tt>P</tt>.
	 */ 
	public SPoint<T> getP(){
		return ps[0];
	}
	/**
	 * Gets the vertex <tt>A</tt>.
	 * @return vertex <tt>A</tt>.
	 */ 
	public SPoint<T> getA(){
		return ps[1];
	}
	/**
	 * Gets the vertex <tt>B</tt>.
	 * @return vertex <tt>B</tt>.
	 */ 
	public SPoint<T> getB(){
		return ps[2];
	}
	/**
	 * Gets the vertex <tt>C</tt>.
	 * @return vertex <tt>C</tt>.
	 */ 
	public SPoint<T> getC(){
		return ps[3];
	}
	/**
	 * Gets the bottom surface <tt>ABC</tt>, the order of the vertex is 
	 * specified.
	 * @return 
	 */
	public STriangle<T> getBottom(){
		return bot;
	}
	
	/**
	 * Gets the side surface <tt>APB</tt>, the order of the vertex is 
	 * specified.
	 * @return a 
	 */
	public STriangle<T> getSideF1(){
		return f1;
	}
	/**
	 * Gets the side surface <tt>BPC</tt>, the order of the vertex is 
	 * specified.
	 * @return a 
	 */
	public STriangle<T> getSideF2(){
		return f2;
	}
	
	/**
	 * Gets the side surface <tt>CPA</tt>, the order of the vertex is 
	 * specified.
	 * @return a 
	 */
	public STriangle<T> getSideF3(){
		return f3;
	}
	
	private T surfaceArea = null;
	
	/* (non-Javadoc)
	 * @see cn.timelives.java.utilities.math.spaceAG.shape.Polyhedron#surfaceArea()
	 */
	@Override
	public T surfaceArea() {
		if(surfaceArea==null){
			
			surfaceArea = mc.addX(bot.area(),f1.area(),f2.area(),f3.area());
		}
		return surfaceArea;
	}
	
	private T volume = null;
	
	/* (non-Javadoc)
	 * @see cn.timelives.java.utilities.math.spaceAG.shape.Polyhedron#volume()
	 */
	@Override
	public T volume() {
		return mc.abs(volumePN());
	}
	/**
	 * Returns the volume computed by the determination, may be negative. 
	 * @return
	 */
	public T volumePN(){
		if(volume == null){
			volume = mc.divideLong(SVector.mixedProduct(
					es[0].getDirectVector(),es[2].getDirectVector(), es[3].getDirectVector()), 6l);
		}
		return volume;
	}
	
	/**
	 * Returns the gravity center of this triangle,which is the intersect point 
	 * of three central lines.
	 * @return the center of gravity of this triangle.
	 */
	public SPoint<T> centerG(){
		return SPoint.average(ps);
	}
	private Sphere<T> outerS = null;
	/**
	 * Returns the radius of the in-sphere.
	 * @return
	 */
	public T radiusI(){
		return mc.multiplyLong(mc.divide(volume(), surfaceArea()), 3l);
	}
	/**
	 * Returns the radius of the out-sphere.
	 * @return
	 */
	public T radiusO(){
		return sphereO().getRadius();
	}
	
	/**
	 * Returns the out-sphere, which has the four vertexes on it surface.
	 * @return a sphere
	 */
	public Sphere<T> sphereO(){
		if(outerS == null){
			outerS = Sphere.fourPoints(ps[0], ps[1], ps[2], ps[3]);
		}
		return outerS;
	}
	/**
	 * Returns the in-sphere, which is tangent to the plane.
	 * @return  a sphere
	 */
	public Sphere<T> sphereI(){
		initPlaneDirect();
		surfaceArea();
		T r = radiusI();
		T x = mc.add(mc.add(mc.multiply(
				bot.area(), ps[0].getX()), mc.multiply(
				f2.area(), ps[1].getX())), mc.add(mc.multiply(
				f3.area(), ps[2].getX()), mc.multiply(
				f1.area(), ps[3].getX())));
		T y = mc.add(mc.add(mc.multiply(
				bot.area(), ps[0].getY()), mc.multiply(
				f2.area(), ps[1].getY())), mc.add(mc.multiply(
				f3.area(), ps[2].getY()), mc.multiply(
				f1.area(), ps[3].getY())));
		T z = mc.add(mc.add(mc.multiply(
				bot.area(), ps[0].getZ()), mc.multiply(
				f2.area(), ps[1].getZ())), mc.add(mc.multiply(
				f3.area(), ps[2].getZ()), mc.multiply(
				f1.area(), ps[3].getZ())));
		x = mc.divide(x, surfaceArea);
		y = mc.divide(y, surfaceArea);
		z = mc.divide(z, surfaceArea);
		SPoint<T> center = SPoint.valueOf(x, y, z, mc);
		return Sphere.centerRadius(center, r);
	}
	
	
	
	/**
	 * 1 For the normal vectors of the surfaces point inside.
	 * -1 point outside.
	 */
	private int planeDirect = 0;
	
	/* (non-Javadoc)
	 * @see cn.timelives.java.utilities.math.spaceAG.shape.SpaceObject#isInside(cn.timelives.java.utilities.math.spaceAG.SPoint)
	 */
	@Override
	public boolean isInside(SPoint<T> p) {
		if (planeDirect==0){
			initPlaneDirect();
		}
		T z = mc.getZero();
		int d1 = mc.compare(f1.getPlane().distanceDirected(p), z);
		int d2 = mc.compare(f2.getPlane().distanceDirected(p), z);
		int d3 = mc.compare(f3.getPlane().distanceDirected(p), z);
		if (planeDirect > 0) {
			return d1 > 0 && d2 > 0 && d3 > 0;
		} else {
			return d1 < 0 && d2 < 0 && d3 < 0;
		}
	}

	/* (non-Javadoc)
	 * @see cn.timelives.java.utilities.math.spaceAG.shape.SpaceObject#isOnSurface(cn.timelives.java.utilities.math.spaceAG.SPoint)
	 */
	@Override
	public boolean isOnSurface(SPoint<T> p) {
		return bot.contains(p) || f1.contains(p) || f2.contains(p) || f3.contains(p);
	}
	private void initPlaneDirect(){
		planeDirect = mc.compare(bot.getPlane().distanceDirected(centerG()), mc.getZero());
		if(planeDirect == 0){
			throw new UnsatisfiedCalculationResultException("Distance=0", mc);
		}
	}
	/* (non-Javadoc)
	 * @see cn.timelives.java.utilities.math.spaceAG.SpacePointSet#contains(cn.timelives.java.utilities.math.spaceAG.SPoint)
	 */
	@Override
	public boolean contains(SPoint<T> p) {
		if (planeDirect==0){
			initPlaneDirect();
		}
		T z = mc.getZero();
		int d1 = mc.compare(f1.getPlane().distanceDirected(p), z);
		int d2 = mc.compare(f2.getPlane().distanceDirected(p), z);
		int d3 = mc.compare(f3.getPlane().distanceDirected(p), z);
		if(d1 == 0 || d2 == 0 || d3==0){
			return true;
			//on surface
		}
//		print(d1+"  "+d2 +"  "+d3);
		if (planeDirect > 0) {
			return d1 > 0 && d2 > 0 && d3 > 0;
		} else {
			return d1 < 0 && d2 < 0 && d3 < 0;
		}
	}
	
	private <N> void fillField(Tetrahedron<N> tr,Function<T,N> mapper){
		if(surfaceArea!=null)
			tr.surfaceArea = mapper.apply(surfaceArea);
		if(volume!=null)
			tr.volume = mapper.apply(volume);
	}
	
	/* (non-Javadoc)
	 * @see cn.timelives.java.utilities.math.FlexibleMathObject#mapTo(java.util.function.Function, cn.timelives.java.utilities.math.MathCalculator)
	 */
	@Override
	public <N> Tetrahedron<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator) {
		Tetrahedron<N> te= new Tetrahedron<>(newCalculator, 
				bot.mapTo(mapper, newCalculator),
				f1.mapTo(mapper, newCalculator),
				f2.mapTo(mapper, newCalculator),
				f3.mapTo(mapper, newCalculator));
		fillField(te, mapper);
		return te;
	}

	/* (non-Javadoc)
	 * @see cn.timelives.java.utilities.math.FlexibleMathObject#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Tetrahedron){
			return Arrays.equals(ps, ((Tetrahedron<?>)obj).ps);
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see cn.timelives.java.utilities.math.FlexibleMathObject#hashCode()
	 */
	@Override
	public int hashCode() {
		return Arrays.hashCode(ps);
	}

	/* (non-Javadoc)
	 * @see cn.timelives.java.utilities.math.FlexibleMathObject#valueEquals(cn.timelives.java.utilities.math.FlexibleMathObject)
	 */
	@Override
	public boolean valueEquals(FieldMathObject<T> obj) {
		if(obj instanceof Tetrahedron){
			Tetrahedron<T> tr = (Tetrahedron<T>) obj;
			return ArraySup.arrayEqualNoOrder(ps, tr.ps, (p1,p2)->p1.valueEquals(p2));
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see cn.timelives.java.utilities.math.FlexibleMathObject#valueEquals(cn.timelives.java.utilities.math.FlexibleMathObject, java.util.function.Function)
	 */
	@Override
	public <N> boolean valueEquals(FieldMathObject<N> obj, Function<N, T> mapper) {
		if(obj instanceof Tetrahedron){
			Tetrahedron<N> tr = (Tetrahedron<N>) obj;
			SPoint<T>[] ar = ArraySup.mapTo(tr.ps, p -> p.mapTo(mapper, mc));
			return ArraySup.arrayEqualNoOrder(ps, ar, (p1,p2)->p1.valueEquals(p2));
		}
		return false;
	}
	
	/* 
	 * Returns <pre>
	 * Tetrahedron:(xp,yp,zp),(xa,ya,za),(xb,yb,zb),(xc,yc,zc)
	 * </pre>
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Tetrahedron:");
		for(int i=0;i<VERTEX_NUM;i++){
			sb.append(ps[i]).append(',');
		}
		sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}
	
	/**
	 * Create a tetrahedron by four points.
	 * <p>The {@link MathCalculator} will be taken from the first parameter of {@link FieldMathObject}
	 * @param p
	 * @param A
	 * @param B
	 * @param C
	 * @return
	 */
	public static <T> Tetrahedron<T> fourPoints(SPoint<T> p,SPoint<T> A,SPoint<T> B,SPoint<T> C){
		List<STriangle<T>> list = STriangle.prismSurfaces(p, A,B,C);
		STriangle<T> f1 = list.get(0),
				f2 = list.get(1),
				f3 = list.get(2);
		return new Tetrahedron<>(p.getMathCalculator(), STriangle.sides(
				f1.getEdgeC().reverse(), f2.getEdgeC().reverse(), f3.getEdgeC().reverse()), 
				f1, f2, f3);
	}
	
//	public 
	
	
//	public static void main(String[] args) {
//		SPointGenerator<Double> g= new SPointGenerator<>(Calculators.getCalculatorDouble());
//		Tetrahedron<Double> te = fourPoints(g.of(1d,1d,1d),g.of(0d, 0d, 0d),g.of(0d, 1d, 0d),g.of(1d,0d,0d));
//		print(te);
////		print(t1.f1);
////		print(t1.f2);
////		print(t1.f3);
////		print("bot:"+t1.bot);
////		print(t1.bot.getPlane().getNormalVector());
////		print(t1.f1.getPlane().getNormalVector());
////		print(t1.f2.getPlane().getNormalVector());
////		print(t1.f3.getPlane().getNormalVector());
//		print(te.ps);
//		Sphere<Double> sp = te.sphereO();
//		print(sp);
////		t1.getVertexes().forEach(v -> print(sp.contains(v)));
//		print(te.contains(te.centerG()));
//		Sphere<Double> spr = te.sphereI();
//		te.getSurfaces().forEach(c -> print(c.distance(spr.getCenter())));
//	}
}

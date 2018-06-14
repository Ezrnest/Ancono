package cn.timelives.java.math.geometry.analytic.spaceAG.shape;

import cn.timelives.java.math.FieldMathObject;
import cn.timelives.java.math.geometry.analytic.spaceAG.Plane;
import cn.timelives.java.math.geometry.analytic.spaceAG.SPoint;
import cn.timelives.java.math.geometry.analytic.spaceAG.Segment;
import cn.timelives.java.math.numberModels.MathCalculator;
import cn.timelives.java.math.geometry.analytic.spaceAG.Line;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
/**
 * A cube:<pre>
 * 
 *   H_______G
 *   /|     /|
 * E/______/F|
 *  | |____|_| 
 *  |D/    | /C
 *  |/_____|/
 *  A      B
 * </pre>
 * Here E,F,G,H can be also called A1,B1,C1,D1
 * 
 * @author liyicheng
 *
 * @param <T>
 */
public final class Cube<T> extends RightPrism<T> {
	
	private static final int EDGE_NUM = 12,VERTEX_NUM = 8 , PLANE_NUM = 6,Slantedge_NUM = 4;
	
	/**
	 * ABCD 0123,
	 * A1 B1 C1 D1 4 5 6 7 
	 */
	private final SPoint<T>[] vertex ;//= (Point<T>[]) new Point[8];
	/**
	 * ABCD 0123
	 * AA1 BB1 CC1 DD1 4 5 6 7
	 * A1 B1 C1 D1 8 9 10 11
	 */
	private final Segment<T>[] edge;
	/**
	 * Bottom 0
	 * front-right-behind-left 1-2-3-4
	 * Top 5
	 */
	private final Plane<T>[] surface;//= (Plane<T>[]) new Plane[6];
	
	private final T a;
	
	protected Cube(MathCalculator<T> mc,SPoint<T>[] vertex,Segment<T>[] edge,Plane<T>[] surface,T a) {
		super(mc, 4);
		this.vertex = vertex;
		this.edge = edge;
		this.surface = surface;
		this.a = a;
	}

	@Override
	public Plane<T> getBottomSurface() {
		return surface[0];
	}

	@Override
	public Plane<T> getTopSurface() {
		return surface[5];
	}

	@Override
	public Set<Plane<T>> getSides() {
		Set<Plane<T>> set = new HashSet<>(PLANE_NUM);
		for(Plane<T> p : surface){
			set.add(p);
		}
		return set;
	}

	@Override
	public boolean isSide(Plane<T> p) {
		return surface[1].valueEquals(p) 
				|| surface[2].valueEquals(p) 
				|| surface[3].valueEquals(p) 
				|| surface[4].valueEquals(p) ;
	}

	@Override
	public Set<Segment<T>> getSlantedge() {
		Set<Segment<T>> set = new HashSet<>(Slantedge_NUM);
		set.add(edge[4]);
		set.add(edge[5]);
		set.add(edge[6]);
		set.add(edge[7]);
		return set;
	}

	@Override
	public T getHeight() {
		return a;
	}


	@Override
	public boolean isVertex(SPoint<T> p) {
		for(SPoint<T> p0 : vertex){
			if(p0.valueEquals(p)){
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isEdge(Line<T> l) {
		for(Segment<T> p0 : edge){
			if(p0.getLine().valueEquals(l)){
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isSurface(Plane<T> p) {
		for(Plane<T> p0 : surface){
			if(p0.valueEquals(p)){
				return true;
			}
		}
		return false;
	}

	@Override
	public Set<SPoint<T>> getVertexes() {
		Set<SPoint<T>> set = new HashSet<>(VERTEX_NUM);
		for(SPoint<T> p0 : vertex){
			set.add(p0);
		}
		return set;
	}

	@Override
	public Set<Segment<T>> getEdges() {
		Set<Segment<T>> set = new HashSet<>(EDGE_NUM);
		for(Segment<T> p0 : edge){
			set.add(p0);
		}
		return set;
	}

	@Override
	public Set<Plane<T>> getSurfaces() {
		Set<Plane<T>> set = new HashSet<>(PLANE_NUM);
		for(Plane<T> p0 : surface){
			set.add(p0);
		}
		return set;
	}

	@Override
	public T surfaceArea() {
		return mc.multiplyLong(mc.multiply(a, a), 6l);
	}

	@Override
	public T volume() {
		return mc.pow(a, 3);
	}

	@Override
	public boolean isInside(SPoint<T> p) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isOnSurface(SPoint<T> p) {
		for(Plane<T> plane : surface){

		}
		return false;
	}

	@Override
	public boolean contains(SPoint<T> p) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <N> Cube<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Cube){
			Cube<?> c = (Cube<?>) obj;
			return Arrays.deepEquals(vertex, c.vertex);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(vertex);
	}

	@Override
	public boolean valueEquals(FieldMathObject<T> obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <N> boolean valueEquals(FieldMathObject<N> obj, Function<N, T> mapper) {
		// TODO Auto-generated method stub
		return false;
	}
	/**
	 * Gets a vertex.
	 * @param name
	 * @return
	 */
	public SPoint<T> getVertex(String name){
		switch(name){
		case "A":
			return vertex[0];
		case "B":
			return vertex[1];
		case "C":
			return vertex[2];
		case "D":
			return vertex[3];
		case "E":
			return vertex[4];
		case "A1":
			return vertex[4];
		case "F":
			return vertex[5];
		case "B1":
			return vertex[5];
		case "G":
			return vertex[6];
		case "C1":
			return vertex[6];
		case "H":
			return vertex[7];
		case "D1":
			return vertex[7];
		}
		return null;
	}
	
	public static <T> Cube<T> unitCube(MathCalculator<T> mc){
		return defaultPosition(mc.getOne(),mc);
	}
	
	/**
	 * Returns a cube of 
	 * (0,0,0),(a,0,0),(a,a,0),(0,a,0)<br/>
	 * (0,0,a),(a,0,a),(a,a,a),(0,a,a)
	 * @param a
	 * @param mc
	 * @return
	 */
	public static <T> Cube<T> defaultPosition(T a,MathCalculator<T> mc){
		@SuppressWarnings("unchecked")
		SPoint<T>[] vec = (SPoint<T>[]) new SPoint<?>[VERTEX_NUM];
		T z = mc.getZero();
		vec[0] = SPoint.valueOf(z, z, z, mc);
		vec[1] = SPoint.valueOf(a, z, z, mc);
		vec[2] = SPoint.valueOf(a, a, z, mc);
		vec[3] = SPoint.valueOf(z, a, z, mc);
		vec[4] = SPoint.valueOf(z, z, a, mc);
		vec[5] = SPoint.valueOf(a, z, a, mc);
		vec[6] = SPoint.valueOf(a, a, a, mc);
		vec[7] = SPoint.valueOf(z, a, a, mc);
		return eightPoints(a,vec, mc);
	}
	
	private static <T> Cube<T> eightPoints(T a,SPoint<T>[] vs,MathCalculator<T> mc){
		@SuppressWarnings("unchecked")
		Segment<T>[] vec = (Segment<T>[]) new Segment<?>[EDGE_NUM];
		for(int i=0;i<4;i++){
			vec[i] = Segment.twoPoints(vs[i], vs[(i+1)%4]);
		}
		for(int i=0;i<4;i++){
			vec[i+4] = Segment.twoPoints(vs[i], vs[i+4]);
		}
		for(int i=0;i<4;i++){
			vec[i+8] = Segment.twoPoints(vs[i+4], vs[(i+1)%4+4]);
		}
		@SuppressWarnings("unchecked")
		Plane<T>[] pl = (Plane<T>[]) new Plane<?>[PLANE_NUM];
		pl[0] = Plane.twoLines(vec[0].getLine(), vec[3].getLine());
		pl[1] = Plane.twoLines(vec[0].getLine(), vec[4].getLine());
		pl[2] = Plane.twoLines(vec[1].getLine(), vec[5].getLine());
		pl[3] = Plane.twoLines(vec[2].getLine(), vec[6].getLine());
		pl[4] = Plane.twoLines(vec[3].getLine(), vec[7].getLine());
		pl[4] = Plane.twoLines(vec[8].getLine(), vec[11].getLine());
		return new Cube<T>(mc, vs, vec, pl, a);
	}
//	public static void main(String[] args) {
//		Cube<Double> cb = unitCube(Calculators.getCalculatorDouble());
//		cb.getVertexes().forEach(p -> Printer.print(p));
//	}

	

}

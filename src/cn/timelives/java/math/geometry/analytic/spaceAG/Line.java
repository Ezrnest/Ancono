package cn.timelives.java.math.geometry.analytic.spaceAG;

import cn.timelives.java.math.MathObject;
import cn.timelives.java.math.function.MathFunction;
import cn.timelives.java.math.algebra.linearAlgebra.LinearEquationSolution;
import cn.timelives.java.math.algebra.linearAlgebra.Matrix;
import cn.timelives.java.math.algebra.linearAlgebra.MatrixSup;
import cn.timelives.java.math.algebra.linearAlgebra.Vector;
import cn.timelives.java.math.MathCalculator;
import cn.timelives.java.math.numberModels.api.Simplifiable;
import cn.timelives.java.math.numberModels.api.Simplifier;
import cn.timelives.java.utilities.ArraySup;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public final class Line<T>  extends SpacePointSet<T> implements Simplifiable<T, Line<T>>{
	
	public enum Relation{
		/**
		 * Skew lines, which means the two lines neither intersect, nor parallel(sure them are not 
		 * the same).
		 */
		SKEW,
		/**
		 * Parallel
		 */
		PARALLEL,
		/**
		 * Intersect
		 */
		INTERSECT,
		/**
		 * Coincide
		 */
		COINCIDE;
	}
	
	//we use a form of 
	//	x = x0 + k*vx
	//  y = y0 + k*vy
	//  z = z0 + k*vz
	final SVector<T> vec;
	final SPoint<T> p0;
	
	
	
	
	protected Line(MathCalculator<T> mc,SPoint<T> p0,SVector<T> vec) {
		super(mc);
		this.vec = vec;
		this.p0 = p0;
	}
	/**
	 * Gets one point in this line.
	 * @return a point
	 */
	public SPoint<T> getOnePoint(){
		return p0;
	}
	/**
	 * Gets the direct vector of this line.
	 * @return the direct vector 
	 */
	public SVector<T> getDirectVector(){
		return vec;
	}
	
	
	
	/**
	 * Returns the relation with another line.
	 * @param l
	 * @return the relation
	 * @see {@link Line.Relation}
	 */
	public Relation relationWith(Line<T> l){
		//check whether the two lines are parallel
		if(vec.isParallel(l.vec)){
			return l.contains(p0) ? Relation.COINCIDE :
				Relation.PARALLEL;
		}
		return hasIntersectPoint(l) ? Relation.INTERSECT : Relation.SKEW;
	}
	/**
	 * Returns the relation with the plane.Notice that returned 
	 * enumeration is {@linkplain Plane.Relation}. 
	 * @param p
	 * @return
	 */
	public Plane.Relation relationWith(Plane<T> p){
		return p.relationWith(this);
	}
	
	private Matrix<T> createEquation(Line<T> l){
		@SuppressWarnings("unchecked")
		T[][] mat = (T[][]) new Object[3][3];
		mat[0][0] = vec.x;
		mat[0][1] = l.vec.x;
		mat[0][2] = mc.subtract(l.p0.x, p0.x);
		mat[1][0] = vec.y;
		mat[1][1] = l.vec.y;
		mat[1][2] = mc.subtract(l.p0.y, p0.y);
		mat[2][0] = vec.z;
		mat[2][1] = l.vec.z;
		mat[2][2] = mc.subtract(l.p0.z, p0.z);
		return Matrix.valueOf(mat, mc);
	}
	
	/**
	 * Determines whether the two lines are parallel, in this method, if the 
	 * two lines are coincide, they will also be considered as parallel.
	 * @param l
	 * @return true if the two lines are parallel
	 */
	public boolean isParallel(Line<T> l){
		return vec.isParallel(l.vec);
	}
	/**
	 * Determines whether the two lines has intersect point, in other
	 * word, this means the two lines are coincide or intersect.
	 * @param l
	 * @return true if the two lines have intersect point.
	 */
	public boolean hasIntersectPoint(Line<T> l){
		Matrix<T> m = createEquation(l);
		LinearEquationSolution<T> sov = MatrixSup.solveLinearEquation(m);
		return sov.getSolutionSituation() != LinearEquationSolution.Situation.NO_SOLUTION;
	}
	
	/**
	 * Returns the intersect point. This method will throw an exception 
	 * if the two line is coincide, and returns {@code null} if the two lines 
	 * are parallel.
	 * @param l
	 * @return the intersect point or {@code null} if the two lines 
	 * are parallel.
	 */
	public SPoint<T> intersectPoint(Line<T> l){
		Matrix<T> m = createEquation(l);
		LinearEquationSolution<T> sov = MatrixSup.solveLinearEquation(m);
		if(sov.getSolutionSituation() == LinearEquationSolution.Situation.UNBOUNDED_SOLUTION){
			throw new ArithmeticException("Coincide!");
		}
		if(sov.getSolutionSituation() == LinearEquationSolution.Situation.NO_SOLUTION){
			return null;
		}
		
		//here shows the advantage of naming the vector in space as SVector 
		Vector<T> base = sov.getBase();
		T k = base.getNumber(0);
		
		return new SPoint<T>(mc,mc.add(p0.x, mc.multiply(k, vec.x)),
				mc.add(p0.y, mc.multiply(k, vec.y)),
						mc.add(p0.z, mc.multiply(k, vec.z)));
	}
	
	/**
	 * Returns the cos value of the angle of {@code this} and {@code l}.
	 * @param l another line
	 * @return the cos value of the angle of {@code this} and {@code l}.
	 */
	public T angleCos(Line<T> l){
		return mc.abs(vec.angleCos(l.vec));
	}
	/**
	 * Returns the angle of {@code this} and {@code l}.
	 * @param l another line
	 * @return the angle of {@code this} and {@code l}.
	 */
	public <R> R angle(Line<T> l,MathFunction<T, R> arccos){
		return arccos.apply(angleCos(l));
	}
	
	
	@Override
	public boolean contains(SPoint<T> p) {
		if(mc.isZero(vec.x)){
			if(!mc.isEqual(p0.x, p.x)){
				return false;
			}
			return mc.isEqual(mc.multiply(vec.z, mc.subtract(p.y, p0.y)), 
					mc.multiply(vec.y, mc.subtract(p.z, p0.z)));
		}
		if(mc.isZero(vec.y)){
			if(!mc.isEqual(p0.y, p.y)){
				return false;
			}
			return  mc.isEqual(mc.multiply(vec.z, mc.subtract(p.x, p0.x)), 
					mc.multiply(vec.x, mc.subtract(p.z, p0.z)));
		}
		if(mc.isZero(vec.z)){
			if(!mc.isEqual(p0.z, p.z)){
				return false;
			}
			return  mc.isEqual(mc.multiply(vec.y, mc.subtract(p.x, p0.x)), 
					mc.multiply(vec.x, mc.subtract(p.y, p0.y)));
		}
		return  mc.isEqual(mc.multiply(vec.z, mc.subtract(p.x, p0.x)), 
				mc.multiply(vec.x, mc.subtract(p.z, p0.z))) && 
				mc.isEqual(mc.multiply(vec.y, mc.subtract(p.x, p0.x)), 
						mc.multiply(vec.x, mc.subtract(p.y, p0.y)));
		/* or replace it with
		return SVector.vector(p0, p).isParallel(vec)
		 */
	}
	
	/**
	 * 
	 * @param p
	 * @return
	 */
	public SPoint<T> projection(SPoint<T> p){
		SVector<T> v = perpendicularVector(p);
		return p.moveToward(v);
	}
	/**
	 * Returns a line that intersect with this line and is 
	 * perpendicular to this line and contains the point,
	 * @param p a point 
	 * @return a line
	 */
	public Line<T> perpendicular(SPoint<T> p){
		SVector<T> v = perpendicularVector(p);
		if(v.isZeroVector()){
			throw new ArithmeticException("Line contains point");
		}
		return new Line<>(mc,p,v);
	}
	/**
	 * Returns a vector that is perpendicular to this line and 
	 * the point of {@code p+v} is on this line.
	 * <pre>
	 *  | <-- this line
	 *  |
	 *  |   The vector
	 *  |   v
	 *  |<------�� 
	 *  |       ^
	 *  |       The point
	 * 
	 * </pre>
	 * @param p
	 * @return
	 */
	public SVector<T> perpendicularVector(SPoint<T> p){
		SVector<T> dv = SVector.vector(p,p0);
		return vec.perpendicular(dv);
	}
	
	/**
	 * Returns a parallel line of this and contains the point.
	 * @param p a point
	 * @return a line
	 */
	public Line<T> parallel(SPoint<T> p){
		return new Line<>(mc,p,vec);
	}
	/**
	 * Returns  square of the distance of the two lines. Returns 0 if the two lines are 
	 * intersect.
	 * @param l a line.
	 * @return the square of the distance
	 */
	public T distanceSq(Line<T> l){
		SVector<T> s = vec.outerProduct(l.vec);
		if(s.isZeroVector()){
			return mc.getZero();
		}
		SVector<T> d = SVector.vector(p0, l.p0);
		T t = s.innerProduct(d);
		t = mc.multiply(t, t);
		return mc.divide(t, s.calLengthSq());
	}
	/**
	 * Returns the distance of the two lines. Returns 0 if the two lines are 
	 * intersect.
	 * @param l a line.
	 * @return the distance
	 */
	public T distance(Line<T> l){
		SVector<T> s = vec.outerProduct(l.vec);
		if(s.isZeroVector()){
			return mc.getZero();
		}
		SVector<T> d = SVector.vector(p0, l.p0);
		T t = mc.abs(s.innerProduct(d));
		return mc.divide(t, s.calLength());
	}
	/**
	 * Returns the square of the distance of this to the point.
	 * @param p
	 * @return the distance
	 */
	public T distanceSq(SPoint<T> p){
		return perpendicularVector(p).calLengthSq();
	}
	/**
	 * Returns the distance of this to the point.
	 * @param p
	 * @return the distance
	 */
	public T distance(SPoint<T> p){
		return perpendicularVector(p).calLength();
	}
	
	/**
	 * Moves the line for the given vector.
	 * @param v
	 * @return a new line
	 */
	public Line<T> moveToward(SVector<T> v){
		return  new Line<>(mc,p0.moveToward(v),vec);
	}

	@Override
	public <N> Line<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator) {
		return new Line<>(newCalculator,p0.mapTo(mapper, newCalculator),vec.mapTo(mapper, newCalculator));
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Line){
			Line<?> l = (Line<?>) obj;
			return vec.equals(l.vec) && p0.equals(l.p0);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return vec.hashCode() * 37 + p0.hashCode();
	}

	@Override
	public boolean valueEquals(MathObject<T> obj) {
		if(obj instanceof Line){
			Line<T> line = (Line<T>) obj;
			return isParallel(line) && line.contains(p0);
		}
		return false;
	}

	@Override
	public <N> boolean valueEquals(MathObject<N> obj, Function<N, T> mapper) {
		if(obj instanceof Line){
			return valueEquals(((Line<N>)obj).mapTo(mapper, mc));
		}
		return false;
	}
	/**
	 * Returns "Line:p=(x,y,z) v=(vx,vy,vz)" where x,y,z are 
	 * the point coordinate of a point on this line and vx,vy,vz are 
	 * the direct vector. 
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Line:p=");
		sb.append(p0.toString());
		sb.append(" v=");
		sb.append(vec.toString());
		return sb.toString();
	}
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.number_types.Simplifiable#simplify()
	 */
	@Override
	public Line<T> simplify() {
		return this;
	}
	/** 
	 * Simplify the line, the direct vector of this line is possible to be changed. 
	 * The given Simplifier should maintain that
	 *  positive(or negative) value should be positive(or negative) after simplifying. 
	 */
	@Override
	public Line<T> simplify(Simplifier<T> sim) {
		List<T> list = Arrays.asList(vec.toArray());
		list = sim.simplify(list);
		return new Line<>(mc,p0,SVector.fromList(list, mc));
	}
	/* (non-Javadoc)
	 * @see cn.timelives.java.utilities.math.spaceAG.SpacePointSet#intersect(cn.timelives.java.utilities.math.spaceAG.SpacePointSet)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public SpacePointSet<T> intersect(SpacePointSet<T> set) {
		if(set instanceof Plane){
			Plane<T> pl = (Plane<T>) set;
			if(pl.isParallel(this)){
				if(pl.contains(p0)){
					return this;
				}
				return getEmptySet(mc);
			}
			SPoint<T> p = pl.intersectPoint(this);
			return SpacePointSet.cenvertNull(p, mc);
		}else if(set instanceof Line){
			Line<T> l = (Line<T>) set;
			Relation r = this.relationWith(l);
			if(r == Relation.COINCIDE){
				return this;
			}else if(r == Relation.PARALLEL || r== Relation.SKEW){
				return SpacePointSet.getEmptySet(mc);
			}
			return intersectPoint(l);
		}else if(set instanceof SPoint){
			SPoint<T> p = (SPoint<T>) set;
			return this.contains(p) ? SpacePointSet.getEmptySet(mc) : p;
		}else if(set instanceof Segment){
			Segment<T> s = (Segment<T>) set;
			if(s.getLine().isParallel(this) || this.valueEquals(s.getLine())){
				return s;
			}
			SPoint<T> sp = s.intersectPoint(this);
			return SpacePointSet.cenvertNull(sp, mc);
		}else if(set instanceof STriangle){
			STriangle<T> tri = (STriangle<T>) set;
			Plane<T> pl = tri.getPlane();
			if(pl.isParallel(this)){
				if(!pl.contains(p0)){
					return getEmptySet(mc);
				}
				if(pl.valueEquals(tri.getEdgeA().getLine())){
					return tri.getEdgeA();
				}
				if(pl.valueEquals(tri.getEdgeB().getLine())){
					return tri.getEdgeB();
				}
				if(pl.valueEquals(tri.getEdgeC().getLine())){
					return tri.getEdgeC();
				}
				SPoint<T> p1 = tri.getEdgeA().intersectPoint(this);
				SPoint<T> p2 = tri.getEdgeB().intersectPoint(this);
				SPoint<T> p3 = tri.getEdgeC().intersectPoint(this);
				SPoint<T>[] ps = (SPoint<T>[]) new SPoint<?>[]{p1,p2,p3};
				int n = ArraySup.sortNull(ps);
				if(n==0){
					return getEmptySet(mc);
				}else if(n==1){
					return ps[0];
				}else if(n==2){
					p1 = ps[0];
					p2 = ps[1];
					return Segment.segmentOrPoint(p1, p2);
				}
				//n = 3 is impossible
			}else{
				SPoint<T> p = pl.intersectPoint(this);
				return tri.contains(p) ? p : getEmptySet(mc);
			}
		}
		return super.intersect(set);
	}
	
	/**
	 * Create a line that contains the point p and its direct vector is vec, the 
	 * direct vector cannot be zero vector.<p>
	 * <p>The {@link MathCalculator} will be taken from the first parameter of {@link MathObject}
	 * @param p a point
	 * @param vec the direct vector
	 * @return a new line
	 */
	public static <T> Line<T> pointDirect(SPoint<T> p,SVector<T> vec){
		return pointDirect(p, vec,p.getMathCalculator());
	}
	/**
	 * Create a line that contains the point p and its direct vector is vec, the 
	 * direct vector cannot be zero vector.<p>
	 * @param p a point
	 * @param vec the direct vector
	 * @param mc a {@link MathCalculator}
	 * @return a new line
	 */
	public static <T> Line<T> pointDirect(SPoint<T> p,SVector<T> vec,MathCalculator<T> mc){
		if(p == null){
			throw new NullPointerException();
		}
		if(vec.isZeroVector()){
			throw new IllegalArgumentException("ZERO vector!");
		}
		return new Line<>(mc,p,vec);
	}
	/**
	 * Create a line passing through the two points, throws an exception if the two 
	 * points are the same.
	 * <p>The {@link MathCalculator} will be taken from the first parameter of {@link MathObject}
	 * @param p1
	 * @param p2
	 * @return a new line
	 */
	public static <T> Line<T> twoPoints(SPoint<T> p1,SPoint<T> p2){
		if(p1.valueEquals(p2)){
			throw new IllegalArgumentException("p1==p2");
		}
		return new Line<>(p1.getMathCalculator(),p1,SVector.vector(p1, p2));
	}
	
//	public static void main(String[] args) {
//		MathCalculator<Double> mc = Calculators.getCalculatorDouble();
//		Line<Double> l1 = pointDirect(SPoint.valueOf(0d, 0d, 0d, mc),
//				SVector.valueOf(-1d, 1d, 0d, mc));
//		print(l1);
//		print(l1.perpendicular(SPoint.valueOf(1d, 0d,0d, mc)));
//		print(l1.projection(SPoint.valueOf(1d, 0d,0d, mc)));
//	}
	
}

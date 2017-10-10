package cn.timelives.java.math.spaceAG.shape;

import java.util.Set;
import java.util.function.Function;

import cn.timelives.java.math.numberModels.MathCalculator;
import cn.timelives.java.math.spaceAG.Line;
import cn.timelives.java.math.spaceAG.Plane;
import cn.timelives.java.math.spaceAG.SPoint;
import cn.timelives.java.math.spaceAG.Segment;
/**
 * Polyhedron is a space object whose surfaces are all polygons and 
 * has several edges and vertexes.
 * @author liyicheng
 *
 * @param <T>
 * @param <S>
 */
public abstract class Polyhedron<T> extends SpaceObject<T> {

	protected Polyhedron(MathCalculator<T> mc) {
		super(mc);
	}
	
	/**
	 * Gets the number of surfaces.
	 * @return the number of surfaces.
	 */
	public abstract long numSurface();
	/**
	 * Gets the number of edges.
	 * @return the number of edges.
	 */
	public abstract long numEdge();
	/**
	 * Gets the number of vertexes.
	 * @return the number of vertexes.
	 */
	public abstract long numVertex();
	/**
	 * Determines whether this polyhedron is a convex polyhedron.
	 * @return
	 */
	public abstract boolean isConvex();
	/**
	 * Determines whether the point is one of the vertexes.
	 * @param p
	 * @return
	 */
	public abstract boolean isVertex(SPoint<T> p);
	/**
	 * Determines whether the line is one of the edges,
	 * @param l
	 * @return 
	 */
	public abstract boolean isEdge(Line<T> l);
	/**
	 * Determines whether the point is one of the surfaces.
	 * @param p
	 * @return
	 */
	public abstract boolean isSurface(Plane<T> p);
	
	/**
	 * Returns a set of vertexes, the modification of the set 
	 * has no effect on this object.
	 * @return a set
	 */
	public abstract Set<SPoint<T>> getVertexes();
	
	/**
	 * Returns a set of edges, the modification of the set 
	 * has no effect on this object.
	 * @return a set
	 */
	public abstract Set<Segment<T>> getEdges();
	/**
	 * Returns a set of surfaces, the modification of the set 
	 * has no effect on this object.
	 * @return a set
	 */
	public abstract Set<Plane<T>> getSurfaces();
	
	/**
	 * Returns the surface area of this polyhedron.
	 * @return
	 */
	@Override
	public abstract T surfaceArea();
	/**
	 * Returns the volume of this polyhedron.
	 * @return
	 */
	@Override
	public abstract T volume();
	
	@Override
	public abstract <N> Polyhedron<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator);
}

/**
 * 
 */
package cn.timelives.java.math.planeAG;

/**
 * @author liyicheng
 *
 */
public final class Utilities {

	/**
	 * 
	 */
	private Utilities() {
	}
	/**
	 * Returns the area of triangle <i>ABC</i>, the 
	 * area may be negate.
	 * @param A
	 * @param B
	 * @param C
	 * @return
	 */
	public static <T> T area(Point<T> A,Point<T> B,Point<T> C){
		return Triangle.fromVertex(A.getMathCalculator(), A, B, C).areaPN();
	}
	
	public static <T> T angleCos(Point<T> A,Point<T> O,Point<T> B){
		return PVector.vector(O, A).angleCos(PVector.vector(O, B));
	}

}

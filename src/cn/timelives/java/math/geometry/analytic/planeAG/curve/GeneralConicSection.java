package cn.timelives.java.math.geometry.analytic.planeAG.curve;

import cn.timelives.java.math.equation.EquationSup;
import cn.timelives.java.math.algebra.linearAlgebra.Matrix;
import cn.timelives.java.math.algebra.linearAlgebra.MatrixSup;
import cn.timelives.java.math.algebra.linearAlgebra.Vector;
import cn.timelives.java.math.numberModels.ComputeExpression;
import cn.timelives.java.math.MathCalculator;
import cn.timelives.java.math.numberModels.api.Simplifier;
import cn.timelives.java.math.geometry.analytic.planeAG.PAffineTrans;
import cn.timelives.java.math.geometry.analytic.planeAG.PVector;
import cn.timelives.java.math.geometry.analytic.planeAG.TransMatrix;
import cn.timelives.java.utilities.structure.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
/**
 * A final subclass for conic section.
 * @author liyicheng
 *
 * @param <T>
 */
public final class GeneralConicSection<T> extends ConicSection<T>{
	
	
	
	protected GeneralConicSection(MathCalculator<T> mc, T A, T B, T C, T D, T E, T F) {
		super(mc, A, B, C, D, E, F);
	}
	
	
	@NotNull
    @Override
    public <N> GeneralConicSection<N> mapTo(@NotNull Function<T, N> mapper, @NotNull MathCalculator<N> newCalculator) {
		N a = mapper.apply(A);
		N b = mapper.apply(B);
		N c = mapper.apply(C);
		N d = mapper.apply(D);
		N e = mapper.apply(E);
		N f = mapper.apply(F);
		return new GeneralConicSection<N>(newCalculator, a,b,c,d,e,f);
	}
	
	@Override
	public GeneralConicSection<T> simplify(Simplifier<T> sim) {
        return generalFormula(sim.simplify(getCoefficients()), getMc());
	}
	
	/**
	 * Determines the type of this conic section. Returns {@code null} if the type cannot be determined.
	 * @see Type
	 * @return
	 */
	public Type determineType() {
		//computes the determinant first
		T inv0 = computeI3();
		T inv1 = computeI2();
		T s = computeI1();
        T z = getMc().getZero();
        int a = getMc().compare(inv0, z);
        int b = getMc().compare(inv1, z);
        int c = getMc().compare(s, z);
		if(b > 0){
			if(a==0){
				return Type.POINT;
			}
			if(c > 0){
				return Type.ELLIPSE;
			}else if(c<0){
				return Type.IMAGINARY_ELLIPSE;
			}else{
				throw new ArithmeticException("s==0");
			}
		}else if(b == 0){
			if(a!=0){
				return Type.PARABOLA;
			}
			//a == 0
			T d3 = computeK1();
            int d = getMc().compare(d3, z);
			if(d>0){
				return Type.PARALLEL_LINE;
			}else if(d==0){
				return Type.CONCIDE_LINE;
			}else{
				return Type.IMAGINARY_PARALLEL_LINE;
			}
		}else{
			if(a==0){
				return Type.INTERSECT_LINE;
			}else{
				return Type.PARABOLA;
			}
		}
	}	
	
	private T inv0,inv1,k1;
	/**
	 * Computes one of the invariants:
	 * <pre>
	 * |2A B D|
	 * |B 2C E|
	 * |D E 2F|
	 * </pre>
	 * @return
	 */
	protected T computeI3(){
		if(inv0==null){
            T A2 = getMc().multiplyLong(A, 2l),
                    C2 = getMc().multiplyLong(C, 2l),
                    F2 = getMc().multiplyLong(F, 2l);
			inv0 = MatrixSup.det3(new Object[][]{
				{A2,B,D},
				{B,C2,E},
				{D,E,F2}
            }, getMc());
		}
		return inv0;
	}
	
	/**
	 * Computes one of the invariants:
	 * <pre>
	 * 4AC-B^2
	 * </pre>
	 * @return
	 */
	protected T computeI2(){
		if(inv1==null){
            T A2 = getMc().multiplyLong(A, 2l),
                    C2 = getMc().multiplyLong(C, 2l);
            inv1 = getMc().subtract(getMc().multiply(A2, C2), getMc().multiply(B, B));
		}
		return inv1;
	}
	/**
	 * 
	 * Computes one of the invariants:
	 * <pre>
	 * A+C
	 * </pre>
	 * @return
	 */
	protected T computeI1(){
        return getMc().add(A, C);
	}
	/**
	 * Computes a half-invariant.
	 * @return
	 */
	protected T computeK1(){
		if(k1==null){
            k1 = expr_d3.compute(getMc(), A, C, D, E, F);
		}
		return k1;
	}
	private static final ComputeExpression expr_d3 = ComputeExpression.compile("$2^2+$3^2-($0+$1)*$4");
		

	@SuppressWarnings("unchecked")
	protected Matrix<T> createQuadraticFormMatrix(){
        T B_2 = getMc().divideLong(B, 2L);
		return Matrix.valueOf((T[][])new Object[][]{
			{A,B_2},
			{B_2,C},
        }, getMc());
	}
	
	/**
	 * @see cn.timelives.java.math.geometry.analytic.planeAG.curve.ConicSection#normalize()
	 */
	@Override
	public ConicSection<T> normalize() {
		return normalizeAndTrans().getSecond();
	}
	
	/**
	 * @see cn.timelives.java.math.geometry.analytic.planeAG.curve.ConicSection#normalizeAndTrans()
	 */
	@Override
	public Pair<TransMatrix<T>, ConicSection<T>> normalizeAndTrans() {
		Matrix<T> mat = createQuadraticFormMatrix();
		List<Pair<T,Vector<T>>> eigenvaluesAndVectors =  mat.eigenvaluesAndVectors(
				EquationSup::solveUsingFormula);
		Vector<T> v1 = eigenvaluesAndVectors.get(0).getSecond(),
				v2 = eigenvaluesAndVectors.get(1).getSecond();
		List<Vector<T>> list = Vector.orthogonalizeAndUnit(v1,v2);
		v1 = list.get(0);
		v2 = list.get(1);
		TransMatrix<T> matrix =
                TransMatrix.valueOf(v1.getNumber(0), v2.getNumber(0), v1.getNumber(1), v2.getNumber(1), getMc());
		ConicSection<T> cs = this.transform(matrix.inverse());
		return new Pair<>(matrix,cs);
	}
	
	/**
	 * @see cn.timelives.java.math.geometry.analytic.planeAG.curve.ConicSection#toStandardForm()
	 */
	@Override
	public ConicSection<T> toStandardForm() {
		return toStandardFormAndTrans().getSecond();
	}
	
	/**
	 * @see cn.timelives.java.math.geometry.analytic.planeAG.curve.ConicSection#toStandardFormAndTrans()
	 */
	@Override
	public Pair<PAffineTrans<T>, ConicSection<T>> toStandardFormAndTrans() {
		Pair<TransMatrix<T>, ConicSection<T>> p1 = normalizeAndTrans();
		TransMatrix<T> trans = p1.getFirst();
		ConicSection<T> cs = p1.getSecond();
        T vx = getMc().divideLong(getMc().divide(cs.D, cs.A), 2L),
                vy = getMc().divideLong(getMc().divide(cs.E, cs.C), 2L);
        PAffineTrans<T> atrans = PAffineTrans.ofTranslation(PVector.valueOf(vx, vy, getMc()));
		cs = cs.transform(atrans);
		atrans = PAffineTrans.valueOf(trans, atrans.getVector());
		return new Pair<>(atrans,cs);
	}
	
	
	
	
	
	
	
	
	
	/**
	 * Create a new conic section as the general formula,the coefficients are given through a list,
	 * and the order should be paid attention.
	 * This method requires that {@code A!=0 || B!=0 || C!=0}.
	 * @param list a list of coefficient
	 * @param mc a {@link MathCalculator}
	 * @return a new conic section
	 */
	public static <T> GeneralConicSection<T> generalFormula(List<T> list,MathCalculator<T> mc){
		Iterator<T> it = list.iterator();
		return new GeneralConicSection<T>(mc, it.next(), it.next(), it.next(), 
				it.next(), it.next(), it.next());
	}
	
	
	/**
	 * Creates a new conic section with the given coefficients. The order should be considered and  
	 * {@code null} values in {@code coefficients} and
	 * remaining unassigned coefficients(if any) will be considered as zero.
	 * <P>
	 * For example, {@code generalFormula(mc,1,null,1)} creates a circle: x^2+y^2=1.
	 * @param mc the {@link MathCalculator}
	 * @param coefficients the coefficients
	 * @return
	 */
	@SafeVarargs
	public static <T> GeneralConicSection<T> generalFormula(MathCalculator<T> mc,T...coefficients){
		if(coefficients.length==0){
			throw new IllegalArgumentException();
		}
		T[] coe = Arrays.copyOf(coefficients, 6);
		T z = mc.getZero();
		for(int i=0;i<coe.length;i++){
			if(coe[i]==null){
				coe[i]=z; 
			}
		}
		return generalFormula0(mc,coe);
	}
	/**
	 * Returns a new general conic section.
	 * @param A
	 * @param B
	 * @param C
	 * @param D
	 * @param E
	 * @param F
	 * @param mc
	 * @return
	 */
	public static <T> GeneralConicSection<T> generalFormula(T A,T B,T C,T D , T E,T F,MathCalculator<T> mc){
		return new GeneralConicSection<T>(mc, A, B, C, D, E, F);
	}
	
	static <T> GeneralConicSection<T> generalFormula0(MathCalculator<T> mc,T[] c){
		if(c.length!=6){
			throw new IllegalArgumentException();
		}
		return new GeneralConicSection<T>(mc, c[0], c[1],c[2], c[3], c[4], c[5]);
	}
	
	/**
	 * Converts the given conic section to a GeneralConicSection
	 * @param cs a conic section
	 * @return GeneralConicSection
	 */
	public static <T> GeneralConicSection<T> convert(ConicSection<T> cs){
		if(cs instanceof GeneralConicSection){
			return (GeneralConicSection<T>)cs;
		}
		return new GeneralConicSection<T>(cs.getMathCalculator(), cs.A, cs.B, cs.C, cs.D, cs.E, cs.F);
	}
	/**
	 * Creates an ellipse:
	 * <pre>Ax^2+Cy^2+F = 0</pre>
	 * @param A
	 * @param C
	 * @param F
	 * @return ellipse
	 */
	public static <T> GeneralConicSection<T> ellipse(T A,T C,T F,MathCalculator<T> mc){
		T z = mc.getZero();
		return new GeneralConicSection<T>(mc, A,z,C,z,z,F);
	}
	/**
	 * Creates a hyperbola:
	 * <pre>Ax^2-Cy^2+F = 0</pre>
	 * @param A
	 * @param C
	 * @param F
	 * @return hyperbola
	 */
	public static <T> GeneralConicSection<T> hyperbola(T A,T C,T F,MathCalculator<T> mc){
		T z = mc.getZero();
		return new GeneralConicSection<T>(mc, A,z,mc.negate(C),z,z,F);
	}
	/**
	 * Creates a parabola
	 * <pre>y^2 = 2px </pre>
	 * @param p coefficient
	 * @param mc a {@link MathCalculator}
	 * @return parabola
	 */
	public static <T> GeneralConicSection<T> parabola(T p,MathCalculator<T> mc){
		return parabola(p,true,mc);
	}
	/**
	 * Creates a parabola<br>
	 * y^2 = 2px,if {@code onX}<br>or<br> x^2 = 2py if {@code !onX}
	 * @param p coefficient, nonzero
	 * @param mc a {@link MathCalculator}
	 * @return parabola
	 */
	public static <T> GeneralConicSection<T> parabola(T p,boolean onX,MathCalculator<T> mc){
		if(mc.isZero(p)){
			throw new IllegalArgumentException("p==0");
		}
		T z = mc.getZero();
		if(onX){
			return new GeneralConicSection<T>(mc,z,z,mc.getOne(),mc.multiplyLong(p, -2l),z,z);
		}else{
			return new GeneralConicSection<T>(mc,mc.getOne(),z,z,z,mc.multiplyLong(p, -2l),z);
		}
	}

	/**
	 * Returns the conic section representing the quadratic function y = ax^2+bx+c
	 * @param a
	 * @param b
	 * @param c
	 * @param mc
	 * @param <T>
	 * @return
	 */
	public static <T> GeneralConicSection<T> quadraticFunction(T a, T b, T c, MathCalculator<T> mc){
		T o = mc.getZero();
		return new GeneralConicSection<>(mc,a,o,o,b,mc.negate(mc.getOne()),c);
	}


}

package cn.timelives.java.math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import cn.timelives.java.math.function.MathFunction;
import cn.timelives.java.math.numberModels.MathCalculator;
import cn.timelives.java.math.numberModels.NumberFormatter;


/**
 * SVPEquation stands for <i>single variable polynomial equation</i>,which means that this 
 * equation can be transformed to a polynomial function {@code f} that {@code f(x) = 0}.
 * Generally,the equation can be shown as 
 * <pre>an*x^n + ... + a1*x + a0 = 0 , (an!=0,n>0)</pre>
 * @author lyc
 * @param <T>
 */
public abstract class SVPEquation<T> extends SingleVEquation<T> {
	
	protected SVPEquation(MathCalculator<T> mc) {
		super(mc);
	}
	
	/**
	 * Returns the value of {@code f(x)} in this equation.
	 * @param x a value
	 * @return the result
	 */
	public abstract T compute(T x);
	
	/**
	 * Returns the corresponding function f(x) of this equation.
	 * @return a MathFunction
	 */
	public MathFunction<T,T> getFx(){
		return this::compute;
	}
	/**
	 * Gets the coefficient of {@code x^n},if {@code n==0} then the 
	 * coefficient {@code a0} will be returned.
	 * @param n the index,positive
	 * @return coefficient
	 */
	public abstract T getCoefficient(int n);
	
	@Override
	public boolean isSolution(T x){
		return mc.isZero(compute(x));
	}
	/**
	 * Returns the max power of x in this equation.
	 * @return an integer number indicates the max power.
	 * 
	 */
	public abstract int getMaxPower();
	
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.FlexibleMathObject#toString(cn.timelives.java.math.number_models.NumberFormatter)
	 */
	@Override
	public String toString(NumberFormatter<T> nf) {
		StringBuilder sb = new StringBuilder();
		int mp = getMaxPower();
		for(int i=mp;i>1;i--){
			T a = getCoefficient(i);
			if(!mc.isZero(a)){
				sb.append("(").append(nf.format(a, mc)).append(")x^").append(i).append("+");
			}
		}
		T t = getCoefficient(1);
		if(!mc.isZero(t)){
			sb.append("(")
			.append(nf.format(t, mc)).append(")x").append("+");
		}
		t = getCoefficient(0);
		if(!mc.isZero(t)){
			sb.append(nf.format(t, mc));
		}else{
			sb.deleteCharAt(sb.length()-1);
		}
		return sb.toString();
	}
	
	/**
	 * A default implements for the equation.
	 * @author lyc
	 *
	 * @param <T>
	 */
	public static class DSVPEquation<T> extends SVPEquation<T>{
		private final T[] coes;
		
		private final int mp;
		
		private transient int hash = 0;
		
		protected DSVPEquation(MathCalculator<T> mc,T[] coes) {
			super(mc);
			this.coes = coes;
			mp = coes.length-1;
		}
		
		@Override
		public T compute(T x){
			T re = coes[mp];
			for(int i=mp-1;i>-1;i--){
				re = mc.multiply(x, re);
				re = mc.add(coes[i], re);
			}
			return re;
		}
		
		@Override
		public boolean isSolution(T x) {
			return mc.isZero(compute(x));
		}
		
		@Override
		public int getMaxPower() {
			return mp;
		}
		
		/**
		 * This method will try to solve the equation using the solution-formulas.Because 
		 * formulas are only available when {@code n<5}, if {@code n>=5},an exception will 
		 * be thrown.
		 * @return a list of solutions,including imaginary roots.
		 * @throws ArithmeticException if {@code n>=5}
		 */
		public List<T> solveUsingFormula(){
			switch(mp){
			case 1:{
				return Arrays.asList(mc.negate(mc.divide(coes[0], coes[1])));
			}
			case 2:{
				T a = coes[2];
				T b = coes[1];
				T c = coes[0];
				T delta = mc.subtract(mc.multiply(b, b), mc.multiplyLong(mc.multiply(a, c), 4l));
				// x1 = (-b + sqr(delta)) / 2a
				// x2 = (-b - sqr(delta)) / 2a
				List<T> so = new ArrayList<>(2);
				delta = mc.squareRoot(delta);
				T a2 = mc.multiplyLong(a, 2);
				T re = mc.divide(mc.subtract(delta, b), a2);
				so.add(re);
				re = mc.negate(mc.divide(mc.add(b, delta), a2));
				so.add(re);
				return so;
			}
			default:{
				throw new ArithmeticException("No formula available.");
			}
			}
			
		}
		

		@Override
		public <N> DSVPEquation<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator) {
			@SuppressWarnings("unchecked")
			N[] newCoes = (N[]) new Object[coes.length];
			for(int i=0;i<newCoes.length;i++){
				newCoes[i] = mapper.apply(coes[i]);
			}
			return new DSVPEquation<>(newCalculator, newCoes);
		}

		@Override
		public boolean equals(Object obj) {
			if(obj instanceof SVPEquation){
				return Arrays.equals(coes, ((DSVPEquation<?>)obj).coes);
			}
			return false;
		}

		@Override
		public int hashCode() {
			if(hash==0){
				int h = mp;
				h = h * 31 + coes.hashCode();
				hash = h;
			}
			return hash;
		}

		@Override
		public boolean valueEquals(FlexibleMathObject<T> obj) {
			if(obj instanceof SVPEquation){
				DSVPEquation<T> sv = (DSVPEquation<T>) obj;
				if(sv.mp == this.mp){
					T[] svc = sv.coes;
					for(int i=0;i<coes.length;i++){
						if(!mc.isEqual(coes[i], svc[i])){
							return false;
						}
					}
					return true;
				}
				return false;
			}
			return false;
		}

		@Override
		public <N> boolean valueEquals(FlexibleMathObject<N> obj, Function<N, T> mapper) {
			if(obj instanceof SVPEquation){
				DSVPEquation<N> sv = (DSVPEquation<N>) obj;
				if(sv.mp == this.mp){
					N[] svc = sv.coes;
					for(int i=0;i<coes.length;i++){
						if(!mc.isEqual(coes[i], mapper.apply(svc[i]))){
							return false;
						}
					}
					return true;
				}
				return false;
			}
			return false;
		}

		@Override
		public T getCoefficient(int n) {
			return coes[n];
		}

		
	}
	
	
	
	/**
	 * Returns an equation that is equal to {@code (x-a)^p = 0},the roots are 
	 * already available({@code x=a}).
	 * @param a coefficient
	 * @param p power of the expression,should be larger than 0.
	 * @param mc a {@link MathCalculator}
	 * @return an equation
	 */
	public static <T> SVPEquation<T> binomialPower(T a,int p,MathCalculator<T> mc){
		if(p<=0){
			throw new IllegalArgumentException("p <= 0 ");
		}
		return null;
	}
	
	/**
	 * QEquation is quadratic equation with only one unknown.This class provides 
	 * simple method for solving the equation,calculate x1+x2,x1x2 and so on.
	 * @author lyc
	 *
	 * @param <T>
	 */
	public static final class QEquation<T> extends SVPEquation<T>{
		
		private final T a,b,c;
		
		protected QEquation(MathCalculator<T> mc,T a,T b,T c) {
			super(mc);
			this.a = Objects.requireNonNull(a);
			this.b = Objects.requireNonNull(b);
			this.c = Objects.requireNonNull(c);
		}
		
		private QEquation(MathCalculator<T> mc,T a,T b,T c,T x1,T x2,T delta,int d){
			super(mc);
			this.a = a;
			this.b = b;
			this.c = c;
			this.x1 = x1;
			this.x2 = x2;
			this.delta = delta;
			this.d = d;
		}
		
		/**
		 * Temporary storage for x1 and x2. 
		 */
		private transient T x1, x2;
		
		private transient T delta;
		
		private transient int d = UNKNOWN;
		
		private static final int TWO_IN_R = 2;
		private static final int ONE_IN_R = 1;
		private static final int NONE_IN_R = 0	;
		private static final int UNKNOWN = -1;
		
		@Override
		public T compute(T x) {
			T re = mc.multiply(a, x);
			re = mc.add(re, b);
			re = mc.multiply(x, re);
			return mc.add(re, c);
		}
		
		public T coeA(){
			return a;
		}
		public T coeB(){
			return a;
		}
		public T coeC(){
			return a;
		}
		@Override
		public int getMaxPower() {
			return 2;
		}
		/**
		 * Solve this equation,all solutions will be considered as well as duplicated root,so the 
		 * returning list will always contain two elements.
		 * <p>This method may return imaginary roots.
		 * @return a list of solutions.
		 */
		public List<T> solve(){
			if(x1==null){
				T delta = mc.squareRoot(delta());
				// x1 = (-b + sqr(delta)) / 2a
				// x2 = (-b - sqr(delta)) / 2a
				T a2 = mc.multiplyLong(a, 2);
				x1 = mc.divide(mc.subtract(delta, b), a2);
				x2 = mc.negate(mc.divide(mc.add(b, delta), a2));
			}
			List<T> so = new ArrayList<>(2);
			so.add(x1);
			so.add(x2);
			return so;
		}
		/**
		 * Computes the delta value of this equation,which is calculated by 
		 * <pre> b^2 - 4ac </pre>
		 * @return delta
		 * @see #solveR()
		 */
		public T delta(){
			if(delta==null)
				delta =  mc.subtract(mc.multiply(b, b), mc.multiplyLong(mc.multiply(a, c), 4l));
			return delta;
		}
		/**
		 * Returns the number of roots in the real number field according to 
		 * the delta vlaue.
		 * @return the number or roots.
		 */
		public int getNumberOfRoots(){
			delta();
			int comp = mc.compare(delta, mc.getZero());
			if(comp < 0){
				d = NONE_IN_R;
				return 0;
			}else if(comp==0){
				d = ONE_IN_R;
				return 1;
			}else{
				d = TWO_IN_R;
				return 2;
			}
		}

		/**
		 * Solve this equation in real number field,and take the duplicated root as one root,
		 * <p>This method will return a list of solutions,which will contain 
		 * no element if there is no real solution({@code жд<0}),
		 * one if there is only one solution(or two solutions of the same value)({@code жд=0})
		 * or two elements if there are two solutions(({@code жд>0}).
		 * @return a list of solution,regardless of order.
		 */
		public List<T> solveR(){
			if(d == UNKNOWN){
				getNumberOfRoots();
			}
			if(d == NONE_IN_R){
				return Collections.emptyList();
			}else if( d== ONE_IN_R){
				if(x1 == null){
					T t = mc.divide(b, mc.multiplyLong(a, -2l));
					x1 = t;
					x2 = t;
				}
				List<T> list = new ArrayList<>(1);
				list.add(x1);
				return list;
			}else{
				if(x1==null){
					//solve it.
					T delta = mc.squareRoot(delta());
					T a2 = mc.multiplyLong(a, 2);
					x1 = mc.divide(mc.subtract(delta, b), a2);
					x2 = mc.negate(mc.divide(mc.add(b, delta), a2));
				}
				List<T> so = new ArrayList<>(2);
				so.add(x1);
				so.add(x2);
				return so;
			}
		}
		/**
		 * Returns the sum of roots in this equation,which is calculated by
		 * <pre>-b/a</pre>
		 * @return x1+x2
		 */
		public T rootsSum(){
			return mc.negate(mc.divide(b, a));
		}
		/**
		 * Returns the multiply of roots in this equation,which is calculated by
		 * <pre>c/a</pre>
		 * @return x1*x2
		 */
		public T rootsMul(){
			return mc.divide(c, a);
		}
		/**
		 * Returns (x1-x2)^2,which is equal to delta/a^2.
		 * @return (x1-x2)^2
		 */
		public T rootsSubtractSq(){
			T d = delta();
			d = mc.divide(delta, mc.multiply(a, a));
			return d;
		}
		/**
		 * Returns |x1-x2|,which is equal to sqrt(delta)/|a|.
		 * @return |x1-x2|
		 */
		public T rootsSubtract(){
			if(x1!=null){
				return mc.abs(mc.subtract(x1, x2));
			}
			T d = delta();
			return mc.divide(mc.squareRoot(d), mc.abs(a));
		}
		

		@Override
		public <N> QEquation<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator) {
			return new QEquation<>(newCalculator,mapper.apply(a),mapper.apply(b),mapper.apply(c)
					,mapper.apply(x1),mapper.apply(x2),mapper.apply(delta),d);
		}

		@Override
		public boolean equals(Object obj) {
			if(obj instanceof QEquation){
				QEquation<?> qe = (QEquation<?>) obj;
				return a.equals(qe.a) && b.equals(qe.b) && c.equals(qe.c);
			}
			return false;
		}

		@Override
		public int hashCode() {
			int hash = a.hashCode();
			hash = 31*hash + b.hashCode();
			hash = 31*hash + c.hashCode();
			return hash;
		}

		@Override
		public boolean valueEquals(FlexibleMathObject<T> obj) {
			if(obj instanceof QEquation){
				QEquation<T> eq = (QEquation<T>) obj;
				return mc.isEqual(a, eq.a)&&mc.isEqual(b, eq.b)&&mc.isEqual(c, eq.c);
			}
			return false;
			
		}

		@Override
		public <N> boolean valueEquals(FlexibleMathObject<N> obj, Function<N, T> mapper) {
			if(obj instanceof QEquation){
				QEquation<N> eq = (QEquation<N>) obj;
				return mc.isEqual(a, mapper.apply(eq.a))
						&&mc.isEqual(b, mapper.apply(eq.b))
						&&mc.isEqual(c, mapper.apply(eq.c));
			}
			return false;
		}


		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append('(').append(a).append(")x^2 + (").append(b).append(")x + (").append(c).append(") = 0");
			return sb.toString();
		}
		
		/**
		 * Create an equation that 
		 * <pre>ax^2 + bx + c = 0</pre>
		 * @param a the coefficient of x^2.
		 * @param b the coefficient of x.
		 * @param c the constant coefficient
		 * @param mc a {@link MathCalculator}
		 * @return an equation
		 */
		public static <T> QEquation<T> equation2(T a,T b,T c,MathCalculator<T> mc){
			if(mc.isZero(a)){
				throw new IllegalArgumentException("a == 0");
			}
			return new QEquation<T>(mc, a, b, c);
		}
		/**
		 * Returns an equation that 
		 * <pre>(x-d)^2 = 0</pre>
		 * @param a an coefficient
		 * @param mc a {@link MathCalculator}
		 * @return an equation
		 */
		public static <T> QEquation<T> perfectSquare(T d,MathCalculator<T> mc){
			T a = mc.getOne();
			T b = mc.multiplyLong(d, -2l);
			T c = mc.multiply(d, d);
			T delta = mc.getZero();
			int t = ONE_IN_R;
			return new QEquation<T>(mc, a, b, c, d, d, delta, t);
		}

		@Override
		public T getCoefficient(int n) {
			switch(n){
			case 0:
				return c;
			case 1:
				return b;
			case 2:
				return a;
			default:
				throw new IllegalArgumentException();
			}
		}
		
		
	}
	/**
	 *  a*x +b = 0
	 * @author lyc
	 *
	 * @param <T>
	 */
	public static final class LEquation<T> extends SVPEquation<T>{
		
		private final T a,b;
		private final T sol;
		protected LEquation(MathCalculator<T> mc,T a,T b) {
			super(mc);
			if(mc.isZero(a)){
				throw new IllegalArgumentException("a=0");
			}
			this.a = a;
			this.b = Objects.requireNonNull(b);
			sol = mc.negate(mc.divide(b, a));
		}
		
		private LEquation(MathCalculator<T> mc,T a,T b,T sol){
			super(mc);
			this.a =  Objects.requireNonNull(a);
			this.b =  Objects.requireNonNull(b);
			this.sol =  Objects.requireNonNull(sol);
		}
		
		@Override
		public T compute(T x) {
			return mc.add(mc.multiply(a, x), b);
		}

		@Override
		public int getMaxPower() {
			return 1;
		}
		@Override
		public T getCoefficient(int n) {
			switch(n){
			case 0:
				return b;
			case 1:
				return a;
			default:
				throw new IllegalArgumentException();
			}
		}
		
		public T solution(){
			return sol;
		}

		@Override
		public <N> LEquation<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator) {
			return new LEquation<N>(newCalculator, mapper.apply(a), mapper.apply(b), mapper.apply(sol));
		}

		@Override
		public boolean equals(Object obj) {
			if(obj instanceof LEquation){
				LEquation<?> leq = (LEquation<?>) obj;
				return a.equals(leq.a) && b.equals(leq.b);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return a.hashCode() * 31 + b.hashCode();
		}

		@Override
		public boolean valueEquals(FlexibleMathObject<T> obj) {
			if(obj instanceof LEquation){
				LEquation<T> leq = (LEquation<T>) obj;
				return mc.isEqual(a, leq.a) && mc.isEqual(b, leq.b);
			}
			return false;
		}

		@Override
		public <N> boolean valueEquals(FlexibleMathObject<N> obj, Function<N, T> mapper) {
			if(obj instanceof LEquation){
				LEquation<N> leq = (LEquation<N>) obj;
				return mc.isEqual(a, mapper.apply(leq.a)) && mc.isEqual(b, mapper.apply(leq.b));
			}
			return false;
		}
		/**
		 * Creates an equation of 
		 * <pre>ax + b = 0</pre>
		 * @param a coefficient of x.
		 * @param b coefficient.
		 * @param mc a {@link MathCalculator}
		 * @return a new LEquation
		 */
		public static <T> LEquation<T> createEquation(T a,T b,MathCalculator<T> mc){
			return new LEquation<T>(mc, a, b);
		}
	}
}

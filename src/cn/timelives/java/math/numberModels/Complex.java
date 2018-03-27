package cn.timelives.java.math.numberModels;

import cn.timelives.java.math.FieldMathObject;
import cn.timelives.java.math.exceptions.UnsupportedCalculationException;
import cn.timelives.java.math.function.MathFunction;
import cn.timelives.java.math.planeAG.PVector;
import cn.timelives.java.math.planeAG.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
/**
 * Complex number ,a type of number that can be written as A+Bi,where A,B are 
 * both real number,and "i" is the square root of {@code -1}.<p>
 * In this type of number,all calculation will consider that both A and B are real number,
 * and followings are the basic rules.
 * 	<li>Add:<pre> (A+Bi)+(C+Di) = (A+B)+(C+Di)</pre>
 * 	<li>Negate:<pre> -(A+Bi) = -A + (-B)i</pre>
 * 	<li>Subtract:<pre>Z1 - Z2 = Z1 + (-Z2)</pre>
 * 	<li>Multiple:<pre>(A+Bi)*(C+Di)=(AC-BD)+(AD+BC)i</pre>
 * 	<li>DIvide:<pre>(A+Bi)/(C+Di)=1/(C^2+D^2)*((AC+BD)+(BC-AD)i)</pre>
 * Operations such as modulus and conjugate are also provided.
 * <p>
 * The complex itself requires a type of number to implement A and B,such as 
 * {@code Complex<Double>} and {@code Complex<Long>},and complex is a kind of number too.
 * This may cause some waste, to use complex number only as a kind of number type, you may use 
 * {@link ComplexI}
 * @author lyc
 *
 * @param <T>
 */
public final class Complex<T> extends FieldMathObject<T> {
	private final T a,b;
	
	protected Complex(MathCalculator<T> mc,T a,T b) {
		super(mc);
		this.a = Objects.requireNonNull(a);
		this.b = Objects.requireNonNull(b);
	}
	/**
	 * Returns the real part of this,which is 
	 * equal to {@code Re(this)}.
	 * @return {@code Re(this)}
	 */
	public T re(){
		return a;
	}
	/**
	 * Returns the imaginary part of this,which is 
	 * equal to {@code Im(this)}.
	 * @return {@code Im(this)}
	 */
	public T im(){
		return b;
	}
	private T m = null;
	
	/**
	 * Returns the modulus of this complex number,which is 
	 * equal to {@code |this|}.
	 * @return {@code |this|}
	 */
	public T modulus(){
		if(m == null){
			m = mc.squareRoot(mc.add(mc.multiply(a, a), mc.multiply(b, b)));
		}
		return m;
	}
	/**
	 * Returns the vector of this({@code <a,b>}).
	 * @return a vector
	 */
	public PVector<T> toVector(){
		return PVector.valueOf(a, b, mc);
	}
	
	/**
	 * Returns the point representation in complex plane of this number. 
	 * @return point(a,b)
	 */
	public Point<T> toPoint(){
		return Point.valueOf(a, b, mc);
	}
	
	/**
	 * Returns the trigonometrical form of this complex number.
	 * The first element is the argument and the second element is the modulus 
	 * of this complex number.
	 * @param argImpl the arctan function of number type T,the vector is equal to this complex number's 
	 * vector.
	 * @return
	 */
	public List<T> triForm(MathFunction<Complex<T>, T> argImpl){
		List<T> list = new ArrayList<T>(2);
		list.add(arg());
		list.add(modulus());
		return list;
	}
	private T arg = null;
	
	/**
	 * Returns the argument of this complex number,which is 
	 * equal to arg(this).The range of the angle will be in [0,2Pi).
	 * <p>Notice : arg(0) = 0.
	 * @return arg(this)
	 */
	public T arg(){
		if(arg==null){
			T pi = mc.constantValue(MathCalculator.STR_PI);
			int compa = mc.compare(a, mc.getZero());
			int compb = mc.compare(b, mc.getZero());
			if(compa == 0){
				if(compb==0){
					return mc.getZero();
				}
				if(compb>0){
					return mc.divideLong(pi, 2l);
				}else{
					return mc.add(mc.divideLong(pi, 2l), pi);
				}
			}
			if(compb == 0){
				if(compa>0){
					return mc.getZero();
				}else{
					return pi;
				}
			}
			T theta = mc.arctan(mc.abs(mc.divide(b, a)));
			if(compa>0){
				if(compb<0){
					theta = mc.subtract(mc.multiplyLong(pi, 2l), theta);
				}
			}else{
				if(compb>0){
					theta = mc.subtract(pi, theta);
				}else{
					theta = mc.add(theta, pi);
				}
			}
			arg = theta;
			
		}
		return arg;
	}
	//some argument function implements here:
	/**
	 * A default implement of argument function for Double.
	 */
	public static final MathFunction<Complex<Double>, Double> 
		DAF = new MathFunction<Complex<Double>, Double>() {
			@Override
			public Double apply(Complex<Double> t) {
				
				double arg = Math.atan2(t.b, t.a);
				//must be in [0,2pi),so add 2Pi if necessary.
				if(arg<0){
					arg += Math.PI*2;
				}
				return arg;
			}
	};
	/**
	 * Returns {@code this+z}.
	 * @param z
	 * @return {@code this+z}
	 */
	public Complex<T> add(Complex<T> z){
		return new Complex<T>(mc,mc.add(a, z.a),mc.add(b, z.b));
	}
	/**
	 * Returns {@code this-z}.
	 * @param z
	 * @return {@code this-z}
	 */
	public Complex<T> subtract(Complex<T> z){
		return new Complex<T>(mc,mc.subtract(a, z.a),mc.subtract(b, z.b));
	}
	/**
	 * Returns {@code -this}.
	 * @return {@code -this}
	 */
	public Complex<T> negate(){
		return new Complex<T>(mc,mc.negate(a),mc.negate(b));
	}
	/**
	 * Returns the conjugate complex number of {@code this}.
	 * @return
	 * <pre>____
	 *this
	 * </pre> 
	 * 
	 */
	public Complex<T> conjugate(){
		return new Complex<T>(mc,a,mc.negate(b));
	}
	/**
	 * Returns this*z.
	 * @param z 
	 * @return the result of {@code this*z}.
	 */
	public Complex<T> multiply(Complex<T> z){
		//(a+bi)*(c+di) = 
		// ac-bd + (ad+bc)i
		//but we can use a trick to reduce calculation:
		//1.(a+b)(c+d) = ac + bd + ad + bc
		//2.ac , 3. bd
		//and 1 - 2 - 3 = ad + bc
		//    2 - 3 = ac - bd
		T t1 = mc.multiply(mc.add(a, b), mc.add(z.a, z.b));
		T t2 = mc.multiply(a, z.a);
		T t3 = mc.multiply(b, z.b);
		T an = mc.subtract(t2, t3);
		T bn = mc.subtract(t1, mc.add(t2, t3));
		return new Complex<T>(mc, an, bn);
	}
	/**
	 * Returns this*r.
	 * @param r
	 * @return this*r
	 */
	public Complex<T> multiplyReal(T r){
		return new Complex<T>(mc,mc.multiply(a, r),mc.multiply(b, r));
	}
	
	/**
	 * Returns this/z,throw ArithmeticException if z = 0.
	 * @param z 
	 * @return the result of {@code this*z}.
	 */
	public Complex<T> divide(Complex<T> z){
		//                _
		//z1 / z2 = (z1 * z2) / |z2|^2
		T sq = mc.add(mc.multiply(z.a, z.a), mc.multiply(z.b, z.b));
		//copy code here
		T tb = mc.negate(z.b);
		T t1 = mc.multiply(mc.add(a, b), mc.add(z.a, tb));
		T t2 = mc.multiply(a, z.a);
		T t3 = mc.multiply(b, tb);
		T an = mc.subtract(t2, t3);
		T bn = mc.subtract(t1, mc.add(t2, t3));
		an = mc.divide(an, sq);
		bn = mc.divide(bn, sq);
		return new Complex<T>(mc, an, bn);
	}
	
	public Complex<T> reciprocal(){
		//         _
		// 1 / z = z / |z|^2
		T sq = mc.add(mc.multiply(a, a), mc.multiply(b, b));
		T an = mc.divide(a, sq);
		T bn = mc.divide(mc.negate(b), sq);
		return new Complex<T>(mc, an, bn);
	}
	/**
	 * Calculates the result of {@code this^p},the {@code p} should be a non-negative 
	 * number.<p>
	 * @param p the power 
	 * @return {@code this^p}
	 * @throws ArithmeticException if {@code p==0 && this==0}
	 */
	public Complex<T> pow(long p){
		if(p<0){
			throw new IllegalArgumentException("Cannot calculate:p<0");
		}
		
		//we use this way to reduce the calculation to log(p)
		Complex<T> re = Complex.real(mc.getOne(), mc);
		if(p == 0){
			if(mc.isZero(a)&&mc.isZero(b)){
				throw new ArithmeticException("0^0");
			}
			return re;
		}
		Complex<T> th = this;
		while(p!=0){
			//which means need to multiple this one
			if((p&1L)!=0){
				re = re.multiply(th);
			}
			th = th.multiply(th);
			p>>=1;
		}
		return re;
	}
	
//	public Complex<T> powf()
	
	@Override
	public <N> Complex<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator) {
		return new Complex<N>(newCalculator,mapper.apply(a),mapper.apply(b));
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Complex){
			Complex<?> com = (Complex<?>) obj;
			return a.equals(com.a) && b.equals(com.b);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return a.hashCode()+37*b.hashCode();
	}
	
	/**
	 * Return the form of (a)+(b)i
	 * @return
	 */
	@Override
	public String toString(NumberFormatter<T> nf) {
		if(mc.isZero(a)){
			if (mc.isZero(b)) {
				return "0";
			}
			return "("+nf.format(b, mc)+")i";
		}else{
			if(mc.isZero(b)){
				return "("+nf.format(a, mc)+")i";
			}
			StringBuilder sb = new StringBuilder();
			sb.append('(').append(nf.format(a, mc))
			.append(")")
			.append(nf.format(b, mc))
			.append(")i");
			return sb.toString();
		}
	}
	@Override
	public boolean valueEquals(FieldMathObject<T> obj) {
		if(obj instanceof Complex){
			Complex<T> com = (Complex<T>) obj;
			return mc.isEqual(a, com.a) && mc.isEqual(b, com.b);
		}
		return false;
	}

	@Override
	public <N> boolean valueEquals(FieldMathObject<N> obj, Function<N, T> mapper) {
		if(obj instanceof Complex){
			Complex<N> com = (Complex<N>) obj;
			return mc.isEqual(a, mapper.apply(com.a)) && mc.isEqual(b, mapper.apply(com.b));
		}
		return false;
	}
	/**
	 * Gets the value of 0.
	 * @param mc
	 * @return
	 */
	public static <T> Complex<T> zero(MathCalculator<T> mc){
		@SuppressWarnings("unchecked")
		Complex<T> c = (Complex<T>) zeros.get(mc);
		if(c ==null) {
			T z = mc.getZero();
			c =  new Complex<T>(mc,z,z);
			zeros.put(mc, c);
		}
		return c;
	}
	
	private static final Map<MathCalculator<?>,Complex<?>> zeros = new ConcurrentHashMap<>();
	
	/**
	 * Creates a new complex instance of 
	 * <pre>a + bi</pre>
	 * @param a real part of the complex
	 * @param b imaginary part of the complex
	 * @param mc a {@link MathCalculator}
	 * @return a new complex.
	 */
	public static <T> Complex<T> ins(T a,T b,MathCalculator<T> mc){
		return new Complex<T>(mc, a, b);
	}
	/**
	 * Create a imaginary number 
	 * <pre>a</pre>
	 * The imaginary part of this number will be 0.
	 * @param a the real part
	 * @param mc a {@link MathCalculator}
	 * @return a new complex.
	 */
	public static <T> Complex<T> real(T a,MathCalculator<T> mc){
		return new Complex<T>(mc,a,mc.getZero());
	}
	/**
	 * Create a real number 
	 * <pre>bi</pre>
	 * The real part of this number will be 0.
	 * @param a the imaginary part
	 * @param mc a {@link MathCalculator}
	 * @return a new complex.
	 */
	public static <T> Complex<T> imaginary(T b,MathCalculator<T> mc){
		return new Complex<T>(mc,mc.getZero(),b);
	}
	/**
	 * Returns the complex that is equal to:
	 * <pre>r*(cos(theta)+isin(theta))</pre>
	 * @param r
	 * @param theta
	 * @return
	 */
	public static <T> Complex<T> modArg(T r,T theta,MathCalculator<T> mc){
		return new Complex<T>(mc,mc.multiply(r, mc.cos(theta)),mc.multiply(r, mc.sin(theta)));
	}
	
	/**
	 * Gets a wrapped calculator for the number type complex.
	 * @param mc
	 * @return
	 */
	public static <T> MathCalculator<Complex<T>> getCalculator(MathCalculator<T> mc){
		return new ComplexCalculator<T>(mc);
	}
	
	
	public static class ComplexCalculator<T> extends MathCalculatorAdapter<Complex<T>>{
		private final MathCalculator<T> mc;
		public ComplexCalculator(MathCalculator<T> mc) {
			this.mc = mc;
		}
		
		@Override
		public boolean isEqual(Complex<T> para1, Complex<T> para2) {
			return para1.valueEquals(para2);
		}

		@Override
		public int compare(Complex<T> para1, Complex<T> para2) {
			throw new UnsupportedCalculationException("Complex Number");
		}

		@Override
		public Complex<T> add(Complex<T> para1, Complex<T> para2) {
			return para1.add(para2);
		}

		@Override
		public Complex<T> negate(Complex<T> para) {
			return para.negate();
		}
		/**
		 * This method overrides the normal {@code abs()} method and it 
		 * is equal to modulus of the complex.(|z|)
		 */
		@Override
		public Complex<T> abs(Complex<T> para) {
			return Complex.real(para.modulus(), para.mc);
		}

		@Override
		public Complex<T> subtract(Complex<T> para1, Complex<T> para2) {
			return para1.subtract(para2);
		}
		private Complex<T> zero,one;
		@Override
		public Complex<T> getZero() {
			return zero == null ? zero = Complex.zero(mc) : zero;
		}

		@Override
		public Complex<T> multiply(Complex<T> para1, Complex<T> para2) {
			return para1.multiply(para2);
		}

		@Override
		public Complex<T> divide(Complex<T> para1, Complex<T> para2) {
			return para1.divide(para2);
		}

		@Override
		public Complex<T> getOne() {
			return one == null ? one = Complex.real(mc.getOne(),mc) : one;
		}

		@Override
		public Complex<T> reciprocal(Complex<T> p) {
			return p.reciprocal();
		}

		@Override
		public Complex<T> multiplyLong(Complex<T> p, long l) {
			return new Complex<T>(mc,mc.multiplyLong(p.a, l),mc.multiplyLong(p.b, l));
		}

		@Override
		public Complex<T> divideLong(Complex<T> p, long l) {
			return new Complex<T>(mc,mc.divideLong(p.a, l),mc.divideLong(p.b, l));
		}

		@Override
		public Complex<T> squareRoot(Complex<T> p) {
			if(mc.isZero(p.im())){
				T re = p.re();
				if(nonNegative(re)){
					return new Complex<>(mc, mc.squareRoot(re), mc.getZero());
				}else{
					return new Complex<>(mc,mc.getZero(),mc.negate(re));
				}
			}
			T m = p.modulus();
			T r = mc.squareRoot(m);
			T cos = mc.divide(p.a, m);
			T sin = mc.divide(p.b, r);
			cos = mc.squareRoot(mc.divideLong(mc.add(cos, mc.getOne()), 2l));
			sin = mc.squareRoot(mc.divideLong(mc.subtract(mc.getOne(), sin), 2l));
			if(!nonNegative(p.b)){
				cos = mc.negate(cos);
			}
			cos = mc.multiply(r, cos);
			sin = mc.multiply(r, sin);
			return new Complex<T>(mc, cos, sin);
			
		}
		
		/**
		 * @see cn.timelives.java.math.numberModels.MathCalculator#root(java.lang.Object, long)
		 */
		@Override
		public Complex<T> nroot(Complex<T> x, long n) {
			T arg = x.arg();
			arg = mc.divideLong(arg, n);
			return Complex.modArg(mc.nroot(x.modulus(), n), arg, mc);
		}
		
		private boolean nonNegative(T t){
			return mc.compare(t, mc.getZero())>0;
		}

		@Override
		public Complex<T> pow(Complex<T> p, long exp) {
			return p.pow(exp);
		}
		
		/**
		 * The String representation of <i>i</i>, the square root of -1.
		 */
		public static final String STR_I = "i";
		
		@Override
		public Complex<T> constantValue(String name) {
			switch(name) {
			case STR_I:{
				return new Complex<>(mc,mc.getZero(),mc.getOne());
			}
			}
			T x = mc.constantValue(name);
			if(x == null) {
				return null;
			}
			return new Complex<>(mc,x,mc.getZero());
		}
		
		/**
		 * Returns exp(x). Assuming that x = a+bi, the function returns a 
		 * complex number whose modulus is equal to {@literal e^a} and 
		 * argument is equal to {@literal b}. 
		 */
		@Override
		public Complex<T> exp(Complex<T> x) {
			return Complex.modArg(mc.exp(x.a), x.b, mc);
		}
		/**
		 * Returns exp(a,b), which is equal to exp(ln(a)*b). 
		 */
		@Override
		public Complex<T> exp(Complex<T> a, Complex<T> b) {
			return exp(multiply(ln(a), b));
		}

		/**
		 * Returns the primary value of ln(x), which is equal to
		 * <pre>
		 *  ln(|z|) + arg(z)i
		 * </pre>
		 */
		@Override
		public Complex<T> ln(Complex<T> x) {
			T mod = x.modulus();
			if(mc.isZero(mod)) {
				throw new ArithmeticException("ln(0)");
			}
			T arg = x.arg();
			return new Complex<>(mc,mod,arg);
		}
		
		/* (non-Javadoc)
		 * @see cn.timelives.java.utilities.math.MathCalculator#getNumberClass()
		 */
		@SuppressWarnings("rawtypes")
		@Override
		public Class<Complex> getNumberClass() {
			return Complex.class;
		}
	}
	
//	public static void main(String[] args) {
//		//test here 
//		MathCalculator<Formula> mc = Formula.getCalculator();
//		List<Complex<Formula>> list = ProgressionSup
//				.createArithmeticProgression(Formula.valueOf("0.5"),Formula.ONE, mc)
//				.limit(10)
//				.mapTo(f -> Complex.real(f, mc), Complex.getCalculator(mc))
//				.stream()
//				.collect(Collectors.toList());
//		Complex<Formula> mul = Complex.ins(Formula.ONE, Formula.ONE.negate(), mc);
//		list.forEach(f -> Printer.print(f.multiply(mul)));
//	}
	
	
	
	
	
	
	
	
	
	
	
}

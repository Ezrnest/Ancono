/**
 * 
 */
package cn.timelives.java.math.function;

import cn.timelives.java.math.MathObject;
import cn.timelives.java.math.MathCalculatorHolder;
import cn.timelives.java.math.algebra.Polynomial;
import cn.timelives.java.math.numberModels.Utils;
import cn.timelives.java.math.algebra.calculus.Calculus;
import cn.timelives.java.math.algebra.calculus.Derivable;
import cn.timelives.java.math.algebra.calculus.Integrable;
import cn.timelives.java.math.MathCalculator;
import cn.timelives.java.math.numberModels.NumberFormatter;
import cn.timelives.java.utilities.ArraySup;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;

/**
 * A class providing single variable polynomial functions.
 * @author liyicheng
 * @see SVPFunction
 */
public abstract class AbstractSVPFunction<T> extends AbstractSVFunction<T>
implements SVPFunction<T>,Derivable<T,AbstractSVPFunction<T>>,Integrable<T>{
	
	/**
	 * @param mc
	 */
	protected AbstractSVPFunction(MathCalculator<T> mc,int maxp) {
		super(mc);
		mp = maxp;
	}
	
	
	final int mp;
	
	@Override
	public int getDegree() {
		return mp;
	}
	/**
	 * The default implement for computing the value f(x), it is calculated 
	 * through f(x) = a0+x(a1+x(a2+.....)
	 */
	@Override
	public T apply(T x) {
		T re = getCoefficient(mp);
		for(int i=mp-1;i>-1;i--){
			re = mc.multiply(x, re);
			re = mc.add(getCoefficient(i), re);
		}
		return re;
	}

	/**
	 * Returns the derivation of this function.
	 * @return
	 */
	public AbstractSVPFunction<T> derive(){
		return Calculus.derivation(this);
	}
	
	/*
	 * @see cn.timelives.java.math.algebra.calculus.Integrable#integrate()
	 */
	@Override
	public AbstractSVPFunction<T> integrate() {
		return Calculus.integration(this);
	}
	
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.FlexibleMathObject#mapTo(java.util.function.Function, cn.timelives.java.math.number_models.MathCalculator)
	 */
	@Override
	public abstract <N> AbstractSVPFunction<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator);
	
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.FlexibleMathObject#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof AbstractSVPFunction){
			@SuppressWarnings("unchecked")
			AbstractSVPFunction<T> as = (AbstractSVPFunction<T>) obj;
			if(getDegree()!=as.getDegree()){
				return false;
			}
			for(int i=0;i<mp;i++){
				if(!getCoefficient(i).equals(as.getCoefficient(i))){
					return false;
				}
			}
			return true;
			
		}
		return false;
	}

	/**
	 * Compares whether the another one is also a SVPFunction and determines whether 
	 * they are equal
	 */
	@Override
	public boolean valueEquals(MathObject<T> obj) {
		if(!(obj instanceof SVPFunction)){
			return false;
		}
		if(obj == this) {
			return true;
		}
		@SuppressWarnings("unchecked")
		SVPFunction<T> f = (SVPFunction<T>) obj;
		return SVPFunction.isEqual(this,f, mc::isEqual);
	}

	/* (non-Javadoc)
	 * @see cn.timelives.java.math.FlexibleMathObject#valueEquals(cn.timelives.java.math.FlexibleMathObject, java.util.function.Function)
	 */
	@Override
	public <N> boolean valueEquals(MathObject<N> obj, Function<N, T> mapper) {
		if(!(obj instanceof SVPFunction)){
			return false;
		}
		if(obj == this) {
			return true;
		}
		@SuppressWarnings("unchecked")
		SVPFunction<N> f = (SVPFunction<N>) obj;
		return Polynomial.isEqual(this,f, Utils.mappedIsEqual(mc, mapper));
	}
	
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.FlexibleMathObject#toString(cn.timelives.java.math.number_models.NumberFormatter)
	 */
	@Override
	public String toString(NumberFormatter<T> nf) {
		return Polynomial.stringOf(this, mc, nf);
	}
	
	static class SVPFunctionImpl1<T> extends AbstractSVPFunction<T>{
		final T[] coes;
		/**
		 * @param mc
		 * @param maxp
		 */
		SVPFunctionImpl1(MathCalculator<T> mc, int maxp,T[] coes) {
			super(mc, maxp);
			this.coes = coes;
		}
		/* (non-Javadoc)
		 * @see cn.timelives.java.math.function.SVFunction#apply(java.lang.Object)
		 */
		@Override
		public T apply(T x) {
			T re = coes[mp];
			for(int i=mp-1;i>-1;i--){
				re = mc.multiply(x, re);
				re = mc.add(coes[i], re);
			}
			return re;
		}
		/* (non-Javadoc)
		 * @see cn.timelives.java.math.function.AbstractSVPFunction#getCoefficient(int)
		 */
		@Override
		public T getCoefficient(int n) {
			return coes[n];
		}
		/* (non-Javadoc)
		 * @see cn.timelives.java.math.FlexibleMathObject#mapTo(java.util.function.Function, cn.timelives.java.math.number_models.MathCalculator)
		 */
		@Override
		public <N> SVPFunctionImpl1<N> mapTo(Function<T, N> mapper,
											 MathCalculator<N> newCalculator) {
			return new SVPFunctionImpl1<>(newCalculator, mp, ArraySup.mapTo(coes, mapper));
		}
		private int hash;
		@Override
		public int hashCode() {
			if(hash==0){
				int h = mp;
				h = h * 31 + coes.hashCode();
				hash = h;
			}
			return hash;
		}
		
	}
	
	/**
	 * An implement for {@link AbstractSVPFunction} which uses map to store data. This implement 
	 * is aimed to be used  
	 * 
	 * 
	 * @author liyicheng
	 *
	 * @param <T>
	 */
	static class SVPFunctionImpl2<T> extends AbstractSVPFunction<T>{
		final Map<Integer,T> map;
		/**
		 * 
		 */
		SVPFunctionImpl2(MathCalculator<T> mc,int mp,Map<Integer,T> map) {
			super(mc,mp);
			this.map = map;
		}
		
		/* (non-Javadoc)
		 * @see cn.timelives.java.math.function.AbstractSVPFunction#getCoefficient(int)
		 */
		@Override
		public T getCoefficient(int n) {
			return map.getOrDefault(n, mc.getZero());
		}

		/* (non-Javadoc)
		 * @see cn.timelives.java.math.FlexibleMathObject#mapTo(java.util.function.Function, cn.timelives.java.math.number_models.MathCalculator)
		 */
		@Override
		public <N> SVPFunctionImpl2<N> mapTo(Function<T, N> mapper,
											 MathCalculator<N> newCalculator) {
			HashMap<Integer,N> nmap = new HashMap<>(map.size());
			for(Entry<Integer,T> en : map.entrySet()){
				nmap.put(en.getKey(), mapper.apply(en.getValue()));
			}
			return new SVPFunctionImpl2<>(newCalculator, mp, nmap); 
		}

		/* (non-Javadoc)
		 * @see cn.timelives.java.math.FlexibleMathObject#hashCode()
		 */
		@Override
		public int hashCode() {
			return mc.hashCode()*31 + map.hashCode();
		}
	}
	
	/**
	 * Describe the linear function.
	 * @author liyicheng
	 * 2017-10-07 15:08
	 *
	 * @param <T>
	 */
	public static final class LinearFunction<T> extends AbstractSVPFunction<T>{
		private final T a,b;
		/**
		 * @param mc
		 * @param maxp
		 */
		LinearFunction(MathCalculator<T> mc, T a,T b) {
			super(mc, 1);
			this.a = Objects.requireNonNull(a);
			this.b = Objects.requireNonNull(b);
			if(mc.isZero(a)) {
				throw new IllegalArgumentException("a==0");
			}
		}
		/*
		 * @see cn.timelives.java.math.function.AbstractSVPFunction#getCoefficient(int)
		 */
		@Override
		public T getCoefficient(int n) {
			switch(n) {
			case 0:{
				return b;
			}
			case 1:{
				return a;
			}
			}
			throw new IndexOutOfBoundsException();
		}
		/*
		 * @see cn.timelives.java.math.function.AbstractSVPFunction#mapTo(java.util.function.Function, cn.timelives.java.math.MathCalculator)
		 */
		@Override
		public <N> LinearFunction<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator) {
			return new LinearFunction<N>(newCalculator, mapper.apply(a), mapper.apply(b));
		}
		
	}
	/**
	 * A constant function is a type of MathFunction that 
	 * always returns the same result.
	 * @author 
	 *
	 */
	public final static class ConstantFunction<T> extends AbstractSVPFunction<T> implements SVFunction<T>{
		private final T r;
		ConstantFunction(MathCalculator<T> mc,T r){
			super(mc,0);
			this.r = r;
		}
		
		
		/*
		 * @see cn.timelives.java.math.function.MathFunction#apply(java.lang.Object)
		 */
		@Override
		public T apply(T x) {
			return r;
		}
		/**
		 * Returns the result.
		 * @return
		 */
		public T getResult() {
			return r;
		}


		/*
		 * @see cn.timelives.java.math.FlexibleMathObject#mapTo(java.util.function.Function, cn.timelives.java.math.MathCalculator)
		 */
		@Override
		public <N> ConstantFunction<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator) {
			return new ConstantFunction<N>(newCalculator,mapper.apply(r));
		}


		/*
		 * @see cn.timelives.java.math.FlexibleMathObject#valueEquals(cn.timelives.java.math.FlexibleMathObject)
		 */
		@Override
		public boolean valueEquals(MathObject<T> obj) {
			if(!(obj instanceof ConstantFunction)) {
				return false;
			}
			return mc.isEqual(r, ((ConstantFunction<T>)obj).r);
		}

		/*
		 * @see cn.timelives.java.math.FlexibleMathObject#toString(cn.timelives.java.math.numberModels.NumberFormatter)
		 */
		@Override
		public String toString(NumberFormatter<T> nf) {
			return nf.format(r, mc);
		}


		/*
		 * @see cn.timelives.java.math.function.SVPFunction#getCoefficient(int)
		 */
		@Override
		public T getCoefficient(int n) {
			if(n!=0) {
				throw new IndexOutOfBoundsException("n!=0");
			}
			return r;
		}
	}

	
	/**
	 * Creates a function with it coefficients.
	 * @param mc a {@link MathCalculator}
	 * @param coes an array of coefficients, if an element is {@code null}, 
	 * then it will be considered as zero.
	 * @return a new single variable polynomial function
	 */
	@SafeVarargs
	public static <T> AbstractSVPFunction<T> valueOf(MathCalculator<T> mc,T...coes){
		for(int i=0;i<coes.length;i++){
			if(coes[i]==null){
				coes[i] = mc.getZero();
			}
		}
		int max = coes.length-1;
		while(mc.isZero(coes[max])){
			max--;
		}
		coes = Arrays.copyOf(coes, max+1);
		return new SVPFunctionImpl1<>(mc,max, coes);
	}
	/**
	 * Creates a function with it coefficients as a list.
	 * @param coes an list of coefficients, {@code null} values are unacceptable. 
	 * @param mc a {@link MathCalculator}
	 * @return a new single variable polynomial function
	 */
	public static <T> AbstractSVPFunction<T> valueOf(List<T> coes,MathCalculator<T> mc){
		@SuppressWarnings("unchecked")
		T[] arr = (T[]) coes.toArray();
		for(int i=0;i<arr.length;i++) {
			if(arr[i]==null) {
				throw new NullPointerException("null in list: index = "+i);
			}
		}
		return new SVPFunctionImpl1<>(mc, arr.length-1, arr);
	}
	/**
	 * Creates a function with it coefficients as a map.
	 * @param coes an map of coefficients, {@code null} values are unacceptable. 
	 * @param mc a {@link MathCalculator}
	 * @return a new single variable polynomial function
	 */
	public static <T> AbstractSVPFunction<T> valueOf(Map<Integer,T> coes,MathCalculator<T> mc){
		Map<Integer,T> map = new HashMap<>();
		int mp = 0;
		for(Entry<Integer,T> en : coes.entrySet()) {
			
			if(en.getKey() == null || en.getValue()==null) {
				throw new NullPointerException();
			}
			mp = Math.max(mp, en.getKey());
			map.put(en.getKey(), en.getValue());
		}
		return new SVPFunctionImpl2<>(mc, mp, map);
	}
	
	/**
	 * Returns a constant function:
	 * <pre>c</pre>
	 * @param c the constant
	 * @param mc a {@link MathCalculator}
	 * @return a new ConstantFunction
	 */
	public static <T> ConstantFunction<T> constant(T c,MathCalculator<T> mc){
		return new ConstantFunction<T>(mc, c);
	}
	
	/**
	 * Returns a linear function:
	 * <pre>ax+b</pre>
	 * It is required that {@code a!=0}.
	 * @param a the coefficient of {@code x}
	 * @param b the constant
	 * @param mc a {@link MathCalculator}
	 * @return a new LinearFunction
	 */
	public static <T> LinearFunction<T> linear(T a,T b,MathCalculator<T> mc){
		return new LinearFunction<T>(mc, a, b);
	}
	
	/**
	 * Returns a new quadratic function.
	 * <pre>ax^2+bx+c</pre>
	 * It is required that {@code a!=0}.
	 * @param a the coefficient of {@code x^2}
	 * @param b the coefficient of {@code x}
	 * @param c the constant
	 * @param mc a {@link MathCalculator}
	 * @return a new QuadraticFunction
	 */
	public static <T> QuadraticFunction<T> quadratic(T a,T b,T c,MathCalculator<T> mc){
		return new QuadraticFunction<T>(mc, a, b, c);
	}
	
	/**
	 * Returns a function from a multinomial.
	 * @param m a {@link Polynomial}
	 * @param mc a {@link MathCalculator}
	 * @return an {@link AbstractSVPFunction}
	 */
	public static <T> AbstractSVPFunction<T> fromMultinomial(Polynomial<T> m, MathCalculator<T> mc){
		if(m instanceof AbstractSVPFunction) {
			return (AbstractSVPFunction<T>)m;
		}
		final int size = m.getDegree()+1;
		@SuppressWarnings("unchecked")
		T[] list = (T[]) new Object[size];
		for(int i=0;i<size;i++) {
			list[i] = m.getCoefficient(i);
		}
		return new SVPFunctionImpl1<>(mc, size-1, list);
	}
	/**
	 * Returns a function from a multinomial which is also a {@link MathCalculatorHolder}.
	 * @param m a {@link Polynomial}
	 * @param mc a {@link MathCalculator}
	 * @return an {@link AbstractSVPFunction}
	 * @throws ClassCastException if {@code !(m instanceof MathCalculatorHolder)};
	 */
	public static <T> AbstractSVPFunction<T> fromMultinomial(Polynomial<T> m){
		@SuppressWarnings("unchecked")
		MathCalculatorHolder<T> holder = (MathCalculatorHolder<T>)m;
		return fromMultinomial(m, holder.getMathCalculator());
	}
	
	/**
	 * Adds two functions.
	 * @param p1
	 * @param p2
	 * @return
	 */
	public static <T> AbstractSVPFunction<T> add(SVPFunction<T> p1,SVPFunction<T> p2){
		int max = Math.max(p1.getDegree(), p2.getDegree());
		@SuppressWarnings("unchecked")
		T[] coes =  (T[]) new Object[max+1];
		MathCalculator<T> mc = p1.getMathCalculator();
		for(int i=0;i<=max;i++){
			coes[i] = mc.add(p1.getCoefficient(i), p2.getCoefficient(i));
		}
		return valueOf(mc, coes);
	}
	/**
	 * Subtracts two functions.
	 * @param p1
	 * @param p2
	 * @return
	 */
	public static <T> AbstractSVPFunction<T> subtract(SVPFunction<T> p1,SVPFunction<T> p2){
		int max = Math.max(p1.getDegree(), p2.getDegree());
		@SuppressWarnings("unchecked")
		T[] coes =  (T[]) new Object[max+1];
		MathCalculator<T> mc = p1.getMathCalculator();
		for(int i=0;i<=max;i++){
			coes[i] = mc.subtract(p1.getCoefficient(i), p2.getCoefficient(i));
		}
		return valueOf(mc, coes);
	}
	private static final int MAX_ARRAY_THREHOLD = 128;
	/**
	 * Multiplies the two SVPFunction, returns a new function as the result.
	 * @param p1
	 * @param p2
	 * @return
	 */
	public static <T> AbstractSVPFunction<T> multiply(SVPFunction<T> p1,SVPFunction<T> p2){
		int max = p1.getDegree()+ p2.getDegree();
		if(max < MAX_ARRAY_THREHOLD){
			return multiplyToArr(p1, p2, max);
		}else{
			return multiplyToMap(p1,p2,max);
		}
	}
	
	
	
	/**
	 * @param p1
	 * @param p2
	 * @param max
	 * @return
	 */
	private static <T> AbstractSVPFunction<T> multiplyToMap(SVPFunction<T> p1, SVPFunction<T> p2, int max) {
		MathCalculator<T> mc = p1.getMathCalculator();
		HashMap<Integer,T> map = new HashMap<>();
		for(int i=0,max1 = p1.getDegree();i<=max1;i++){
			for(int j=0,max2= p2.getDegree();j<=max2;j++){
				int t = i+j;
				T coe = mc.multiply(p1.getCoefficient(i), p2.getCoefficient(j));
				map.compute(t, (p,c)-> c== null ? coe : mc.add(c, coe));
			}
		}
		return new SVPFunctionImpl2<>(mc, max, map);
	}
	private static <T> AbstractSVPFunction<T> multiplyToArr(SVPFunction<T> p1,SVPFunction<T> p2,int max){
		MathCalculator<T> mc = p1.getMathCalculator();
		@SuppressWarnings("unchecked")
		T[] arr = (T[]) new Object[max+1];
		for(int i=0,max1 = p1.getDegree();i<=max1;i++){
			for(int j=0,max2= p2.getDegree();j<=max2;j++){
				int t = i+j;
				T coe = mc.multiply(p1.getCoefficient(i), p2.getCoefficient(j));
				if(arr[t] == null){
					arr[t] = coe;
				}else{
					arr[t] = mc.add(arr[t], coe);
				}
			}
		}
		for(int i=0;i<arr.length;i++){
			if(arr[i]==null){
				arr[i] = mc.getZero();
			}
		}
		return new SVPFunctionImpl1<>(mc,max,arr);
	}
	
	
}

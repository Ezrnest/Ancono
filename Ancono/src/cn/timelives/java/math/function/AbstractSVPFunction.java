/**
 * 
 */
package cn.timelives.java.math.function;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import cn.timelives.java.math.FlexibleMathObject;
import cn.timelives.java.math.calculus.Calculus;
import cn.timelives.java.math.numberModels.MathCalculator;
import cn.timelives.java.math.numberModels.NumberFormatter;
import cn.timelives.java.math.planeAG.Point;
import cn.timelives.java.math.planeAG.curve.PlaneFunction;
import cn.timelives.java.utilities.ArraySup;

/**
 * 
 * @author liyicheng
 *
 */
public abstract class AbstractSVPFunction<T> extends FlexibleMathObject<T> implements PlaneFunction<T>, SVPFunction<T> {
	
	/**
	 * @param mc
	 */
	protected AbstractSVPFunction(MathCalculator<T> mc,int maxp) {
		super(mc);
		mp = maxp;
	}
	
	
	final int mp;
	
	@Override
	public int getMaxPower() {
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

	/* (non-Javadoc)
	 * @see cn.timelives.java.math.planeAG.PlanePointSet#contains(cn.timelives.java.math.planeAG.Point)
	 */
	@Override
	public boolean contains(Point<T> p) {
		return mc.isEqual(apply(p.getX()), p.getY());
	}

	/* (non-Javadoc)
	 * @see cn.timelives.java.math.function.SVPFunction#getCoefficient(int)
	 */
	@Override
	public abstract T getCoefficient(int n);
	/**
	 * Returns the derivation of this function.
	 * @return
	 */
	public SVPFunction<T> derivation(){
		return Calculus.derivation(this);
	}
	
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.FlexibleMathObject#mapTo(java.util.function.Function, cn.timelives.java.math.number_models.MathCalculator)
	 */
	@Override
	public abstract  <N> AbstractSVPFunction<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator);
	
	
	
	public static class SVPFunctionImpl1<T> extends AbstractSVPFunction<T>{
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
	public static class SVPFunctionImpl2<T> extends AbstractSVPFunction<T>{
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
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.FlexibleMathObject#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof AbstractSVPFunction){
			@SuppressWarnings("unchecked")
			AbstractSVPFunction<T> as = (AbstractSVPFunction<T>) obj;
			if(getMaxPower()!=as.getMaxPower()){
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
	

	
	
	
	

	/* (non-Javadoc)
	 * @see cn.timelives.java.math.FlexibleMathObject#valueEquals(cn.timelives.java.math.FlexibleMathObject)
	 */
	@Override
	public boolean valueEquals(FlexibleMathObject<T> obj) {
		if(obj instanceof AbstractSVPFunction){
			AbstractSVPFunction<T> f = (AbstractSVPFunction<T>) obj;
			if(f.mp != mp){
				return false;
			}
			for(int i=0;i<=mp;i++){
				if(!mc.isEqual(getCoefficient(i), f.getCoefficient(i))){
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see cn.timelives.java.math.FlexibleMathObject#valueEquals(cn.timelives.java.math.FlexibleMathObject, java.util.function.Function)
	 */
	@Override
	public <N> boolean valueEquals(FlexibleMathObject<N> obj, Function<N, T> mapper) {
		if(obj instanceof AbstractSVPFunction){
			AbstractSVPFunction<N> f = (AbstractSVPFunction<N>) obj;
			if(f.mp != mp){
				return false;
			}
			for(int i=0;i<=mp;i++){
				if(!mc.isEqual(getCoefficient(i), mapper.apply(f.getCoefficient(i)))){
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return toString(NumberFormatter.getToStringFormatter());
	}
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.FlexibleMathObject#toString(cn.timelives.java.math.number_models.NumberFormatter)
	 */
	@Override
	public String toString(NumberFormatter<T> nf) {
		StringBuilder sb = new StringBuilder();
		for(int i=mp;i>0;i--){
			if(mc.isZero(getCoefficient(i)))
				continue;
			sb.append(nf.format(getCoefficient(i), mc)).append("*x^").append(i).append(" + ");
		}
		if(mc.isZero(getCoefficient(0))==false){
			sb.append(nf.format(getCoefficient(0), mc));
		}else{
			sb.delete(sb.length()-3, sb.length());
		}
		return sb.toString();
	}
	
	@SafeVarargs
	public static <T> AbstractSVPFunction<T> createFunction(MathCalculator<T> mc,T...coes){
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
	 * 
	 * @param p1
	 * @param p2
	 * @return
	 */
	public static <T> AbstractSVPFunction<T> add(SVPFunction<T> p1,SVPFunction<T> p2){
		int max = Math.max(p1.getMaxPower(), p2.getMaxPower());
		@SuppressWarnings("unchecked")
		T[] coes =  (T[]) new Object[max+1];
		MathCalculator<T> mc = p1.getMathCalculator();
		for(int i=0;i<=max;i++){
			coes[i] = mc.add(p1.getCoefficient(i), p2.getCoefficient(i));
		}
		return createFunction(mc, coes);
	}
	
	private static final int MAX_ARRAY_THREHOLD = 128;
	/**
	 * Multiplies the two SVPFunction, returns a new function as the result.
	 * @param p1
	 * @param p2
	 * @return
	 */
	public static <T> AbstractSVPFunction<T> multiply(SVPFunction<T> p1,SVPFunction<T> p2){
		int max = Math.max(p1.getMaxPower(), p2.getMaxPower());
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
		for(int i=0,max1 = p1.getMaxPower();i<max1;i++){
			for(int j=0,max2= p2.getMaxPower();j<max2;j++){
				int t = i+j;
				T coe = mc.multiply(p1.getCoefficient(i), p1.getCoefficient(j));
				map.compute(t, (p,c)-> c== null ? coe : mc.add(c, coe));
			}
		}
		return new SVPFunctionImpl2<>(mc, max, map);
	}
	private static <T> AbstractSVPFunction<T> multiplyToArr(SVPFunction<T> p1,SVPFunction<T> p2,int max){
		MathCalculator<T> mc = p1.getMathCalculator();
		@SuppressWarnings("unchecked")
		T[] arr = (T[]) new Object[max];
		for(int i=0,max1 = p1.getMaxPower();i<max1;i++){
			for(int j=0,max2= p2.getMaxPower();j<max2;j++){
				int t = i+j;
				T coe = mc.multiply(p1.getCoefficient(i), p1.getCoefficient(j));
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

/**
 * 2017-11-21
 */
package cn.timelives.java.math.numberModels;

import static cn.timelives.java.utilities.Printer.print;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Function;

import cn.timelives.java.math.FieldMathObject;
import cn.timelives.java.math.MathCalculatorHolder;
import cn.timelives.java.math.Multinomial;
import cn.timelives.java.math.exceptions.UnsupportedCalculationException;
import cn.timelives.java.utilities.CollectionSup;
import cn.timelives.java.utilities.ModelPatterns;
import cn.timelives.java.utilities.structure.Pair;
import cn.timelives.java.utilities.structure.WithInt;

/**
 * A multinomialX is an implement for multinomial.
 * @author liyicheng
 * @see Multinomial
 * 2017-11-21 17:10
 *
 */
public final class MultinomialX<T> extends FieldMathObject<T> implements Multinomial<T>,Comparable<MultinomialX<T>> {
	/**
	 * A map.
	 */
	private final NavigableMap<Integer,T> map;
	private final int degree;
	
	
	/**
	 * 
	 * @param calculator
	 * @param degree
	 */
	MultinomialX(MathCalculator<T> calculator,NavigableMap<Integer,T> map,int degree) {
		super(calculator);
		this.map = Objects.requireNonNull(map);
		this.degree = degree;
	}

	

	/*
	 * @see cn.timelives.java.math.Multinomial#getMaxPower()
	 */
	@Override
	public int getDegree() {
		return degree;
	}
	
	private T getCoefficient0(Integer n) {
		T a = map.get(n);
		if(a == null) {
			a = mc.getZero();
		}
		return a;
	}
	
	/*
	 * @see cn.timelives.java.math.Multinomial#getCoefficient(int)
	 */
	@Override
	public T getCoefficient(int n) {
		if(n < 0 || n > degree) {
			throw new IndexOutOfBoundsException("For n="+n);
		}
		return getCoefficient0(n);
	}
	
	/**
	 * Returns the value of this multinomial
	 * @param x
	 * @return
	 */
	public T compute(T x) {
		T re = getCoefficient(degree);
		for(int i=degree-1;i>-1;i--){
			re = mc.multiply(x, re);
			re = mc.add(getCoefficient(i), re);
		}
		return re;
	}
	
	/**
	 * Divides this multinomial by a number to get a new multinomial whose leading coefficient is one.
	 * @return
	 */
	public MultinomialX<T> unit(){
		if(mc.isEqual(mc.getOne(), getCoefficient(degree))) {
			return this;
		}
		T k = getCoefficient(degree);
		NavigableMap<Integer, T> nmap = getCoefficientMap();
		for(Entry<Integer,T> en : nmap.entrySet()) {
			en.setValue(mc.divide(en.getValue(),k));
		}
		return new MultinomialX<>(mc, nmap, degree);
	}

	/*
	 * @see cn.timelives.java.math.FlexibleMathObject#mapTo(java.util.function.Function, cn.timelives.java.math.numberModels.MathCalculator)
	 */
	@Override
	public <N> MultinomialX<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator) {
		TreeMap<Integer,N> nmap = new TreeMap<>();
		for(Entry<Integer,T> en : map.entrySet()) {
			nmap.put(en.getKey(),mapper.apply(en.getValue()));
		}
		return new MultinomialX<>(newCalculator, nmap, degree);
	}

	/*
	 * @see cn.timelives.java.math.FlexibleMathObject#valueEquals(cn.timelives.java.math.FlexibleMathObject)
	 */
	@Override
	public boolean valueEquals(FieldMathObject<T> obj) {
		if(!(obj instanceof MultinomialX)) {
			return false;
		}
		return Multinomial.isEqual(this, (MultinomialX<T>)obj, mc::isEqual);
	}
	
	
	/*
	 * @see cn.timelives.java.math.FlexibleMathObject#valueEquals(cn.timelives.java.math.FlexibleMathObject, java.util.function.Function)
	 */
	@Override
	public <N> boolean valueEquals(FieldMathObject<N> obj, Function<N, T> mapper) {
		if (!(obj instanceof MultinomialX)) {
			return false;
		}
		return Multinomial.isEqual(this, (MultinomialX<N>) obj, (x,y)->mc.isEqual(x, mapper.apply(y)));
	}
	/*
	 * @see cn.timelives.java.math.FlexibleMathObject#toString(cn.timelives.java.math.numberModels.NumberFormatter)
	 */
	@Override
	public String toString(NumberFormatter<T> nf) {
		 return Multinomial.stringOf(this, mc, nf);
	}
	
	/*
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(MultinomialX<T> o) {
		int mp = o.degree;
		if(mp > degree) {
			return -1;
		}else if( mp < degree) {
			return 1;
		}
		for(int i=mp;i>-1;i--) {
			Integer n = i;
			T a =getCoefficient0(n);
			T b = o.getCoefficient0(n);
			int t = mc.compare(a, b);
			if(t != 0) {
				return t;
			}
		}
		return 0;
	}
	
	private int hashCode;
	
	/*
	 * @see cn.timelives.java.math.FlexibleMathObject#hashCode()
	 */
	@Override
	public int hashCode() {
		if(hashCode == 0) {
			hashCode = map.hashCode() + 31*degree;
		}
		return hashCode;
	}
	
	public NavigableMap<Integer,T> getCoefficientMap(){
		if(map instanceof TreeMap) {
			@SuppressWarnings("unchecked")
			NavigableMap<Integer,T> nmap = (NavigableMap<Integer, T>)((TreeMap<Integer,T>)map).clone();
			return nmap;
		}else {
			return new TreeMap<>(map);
		}
	}
	
	private static final Map<MathCalculator<?>,MultinomialX<?>> zeros = new HashMap<>();
	
	
	/**
	 * Returns zero.
	 * @param mc
	 * @return
	 */
	public static <T> MultinomialX<T> zero(MathCalculator<T> mc){
		@SuppressWarnings("unchecked")
		MultinomialX<T> zero = (MultinomialX<T>) zeros.get(mc);
		if(zero == null) {
			zero = new MultinomialX<>(mc, Collections.emptyNavigableMap(), 0);
			synchronized (zeros) {
				zeros.put(mc, zero);
			}
		}
		return zero;
	}
	
	/**
	 * Returns the multinomial {@literal 1}。
	 * @param mc
	 * @return
	 */
	public static <T> MultinomialX<T> one(MathCalculator<T> mc){
		return constant(mc,mc.getOne());
	}
	
	/**
	 * Returns the multinomial {@literal x}.
	 * @param mc
	 * @return
	 */
	public static <T> MultinomialX<T> oneX(MathCalculator<T> mc){
		TreeMap<Integer,T> map = new TreeMap<>();
		map.put(Integer.valueOf(1), mc.getOne());
		return new MultinomialX<>(mc, map, 1);
	}
	
	public static <T> MultinomialX<T> constant(MathCalculator<T> mc,T c){
		NavigableMap<Integer,T> map = new TreeMap<>();
		if(mc.isZero(c)) {
			return zero(mc);
		}
		map.put(0,c);
		return new MultinomialX<>(mc, map, 0);
	}
	
	/**
	 * Returns a multinomial
	 * @param mc
	 * @param ts
	 * @return
	 */
	@SafeVarargs
	public static <T> MultinomialX<T> valueOf(MathCalculator<T> mc,T...coes){
		if(coes.length==0) {
			return zero(mc);
		}
		int max = coes.length-1;
		while(coes[max] == null || mc.isZero(coes[max])){
			max--;
		}
		if(max == -1) {
			return zero(mc);
		}
		TreeMap<Integer,T> map = new TreeMap<>();
		for(int i=max;i>-1;i--) {
			if(coes[i]!= null && !mc.isZero(coes[i])) {
				map.put(i, coes[i]);
			}
		}
		if(map.isEmpty()) {
			return zero(mc);
		}
		return new MultinomialX<>(mc, map, max);
	}
	public static MultinomialX<Formula> fromPolynomial(Polynomial p,String ch){
		return fromPolynomial(p, ch,Formula.getCalculator());
	}
	/**
	 * Converts the given polynomial to a multinomial of the given character {@code ch}.
	 * @param p
	 * @param ch
	 * @param calculator
	 * @return
	 * @throws ArithmeticException if the polynomial has a fraction power or a negative power for the character
	 * (such as x^0.5 or x^(-2)).
	 */
	public static MultinomialX<Formula> fromPolynomial(Polynomial p,String ch,MathCalculator<Formula> mc){
		TreeMap<Integer,Formula> map = new TreeMap<>();
		int max = 0;
		for(Formula f : p.getFormulas()) {
			BigDecimal bd = f.getCharacterPower(ch);
			int pow = bd.intValueExact();
			if(pow < 0) {
				throw new ArithmeticException("Negative exponent for:["+ch+"] in "+f.toString());
			}
			if(pow > max) {
				max = pow;
			}
			Formula coe = f.removeChar(ch);
			map.put(pow, coe);
		}
		return new MultinomialX<>(mc, map, max);
		
	}
	
	/**
	 * Gets a calculator of the specific type of MultinomialX
	 * @param mc
	 * @return
	 */
	public static <T> MultinomialCalculator<T> getCalculator(MathCalculator<T> mc){
		return new MultinomialCalculator<>(mc);
	}
	
	public static class MultinomialCalculator<T> extends MathCalculatorAdapter<MultinomialX<T>> 
	implements MathCalculatorHolder<T>,NTCalculator<MultinomialX<T>>{
		private final MathCalculator<T> mc;
		private final MultinomialX<T> zero,one;
		/**
		 * 
		 */
		MultinomialCalculator(MathCalculator<T> mc) {
			this.mc = mc;
			zero = zero(mc);
			one = one(mc);
		}
		/*
		 * @see cn.timelives.java.math.MathCalculatorHolder#getMathCalculator()
		 */
		@Override
		public MathCalculator<T> getMathCalculator() {
			return mc;
		}
		/*
		 * @see cn.timelives.java.math.numberModels.MathCalculator#isEqual(java.lang.Object, java.lang.Object)
		 */
		@Override
		public boolean isEqual(MultinomialX<T> para1, MultinomialX<T> para2) {
			return Multinomial.isEqual(para1,para2, mc::isEqual);
		}

		/*
		 * @see cn.timelives.java.math.numberModels.MathCalculator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(MultinomialX<T> para1, MultinomialX<T> para2) {
			return para1.compareTo(para2);
		}

		/*
		 * @see cn.timelives.java.math.numberModels.MathCalculator#add(java.lang.Object, java.lang.Object)
		 */
		@Override
		public MultinomialX<T> add(MultinomialX<T> para1, MultinomialX<T> para2) {
			TreeMap<Integer,T> map = new TreeMap<>();
			int mp1 = para1.getDegree();
			int mp2 = para2.getDegree();
			int mp0 = mp1;
			int mp = -1;
			if(mp1>mp2) {
				for(int i=mp1;i>mp2;i--) {
					Integer n = i;
					T a = para1.getCoefficient0(n);
					if(!mc.isZero(a))
						map.put(n,a);
				}
				mp0 = mp2;
				mp = mp1;
			}else if(mp2 > mp1) {
				for(int i=mp2;i>mp1;i--) {
					Integer n = i;
					T a = para2.getCoefficient0(n);
					if(!mc.isZero(a))
						map.put(n,a);
				}
				mp = mp2;
			}
			for(int i=mp0;i>-1;i--) {
				Integer n = i;
				T a = para1.getCoefficient0(n),
						b = para2.getCoefficient0(n);
				T sum = mc.add(a, b);
				if(mc.isZero(sum)==false) {
					if(mp==-1) {
						mp = i;
					}
					map.put(n, sum);
				}
			}
			if(mp == -1) {
				return zero;
			}
			return new MultinomialX<>(mc, map, mp);
		}

		/*
		 * @see cn.timelives.java.math.numberModels.MathCalculator#negate(java.lang.Object)
		 */
		@Override
		public MultinomialX<T> negate(MultinomialX<T> para) {
			if(para == zero) {
				return zero;
			}
			NavigableMap<Integer,T> nmap = para.getCoefficientMap();
			for(Entry<Integer,T> en : nmap.entrySet()) {
				en.setValue(mc.negate(en.getValue()));
			}
			return new MultinomialX<>(mc, nmap, para.degree);
		}

		

		/*
		 * @see cn.timelives.java.math.numberModels.MathCalculator#subtract(java.lang.Object, java.lang.Object)
		 */
		@Override
		public MultinomialX<T> subtract(MultinomialX<T> para1, MultinomialX<T> para2) {
			TreeMap<Integer,T> map = new TreeMap<>();
			int mp1 = para1.getDegree();
			int mp2 = para2.getDegree();
			int mp0 = mp1;
			int mp = -1;
			if(mp1>mp2) {
				for(int i=mp1;i>mp2;i--) {
					Integer n = i;
					T a = para1.getCoefficient0(n);
					if(!mc.isZero(a)) {
						map.put(n,a);
					}
				}
				mp0 = mp2;
				mp = mp1;
			}else if(mp2 > mp1) {
				for(int i=mp2;i>mp1;i--) {
					Integer n = i;
					T a = para2.getCoefficient0(n);
					if(!mc.isZero(a)) {
						a = mc.negate(a);
						map.put(n,a);
					}
				}
				mp = mp2;
			}
			for(int i=mp0;i>-1;i--) {
				Integer n = i;
				T a = para1.getCoefficient0(n),
						b = para2.getCoefficient0(n);
				T sum = mc.subtract(a, b);
				if(mc.isZero(sum)==false) {
					if(mp==-1) {
						mp = i;
					}
					map.put(n, sum);
				}
			}
			if(mp == -1) {
				return zero;
			}
			return new MultinomialX<>(mc, map, mp);
		}

		/*
		 * @see cn.timelives.java.math.numberModels.MathCalculator#getZero()
		 */
		@Override
		public MultinomialX<T> getZero() {
			return zero;
		}
		/*
		 * @see cn.timelives.java.math.numberModels.MathCalculator#isZero(java.lang.Object)
		 */
		@Override
		public boolean isZero(MultinomialX<T> para) {
			return isEqual(zero,para);
		}

		/*
		 * @see cn.timelives.java.math.numberModels.MathCalculator#multiply(java.lang.Object, java.lang.Object)
		 */
		@Override
		public MultinomialX<T> multiply(MultinomialX<T> para1, MultinomialX<T> para2) {
			NavigableMap<Integer,T> map = multiplyToMap(para1.map, para2.map);
			if(map.isEmpty()) {
				return zero;
			}
//			for(int i=0,max1 = para1.getMaxPower();i<=max1;i++){
//				for(int j=0,max2= para2.getMaxPower();j<=max2;j++){
//					int t = i+j;
//					T coe = mc.multiply(para1.getCoefficient(i), para2.getCoefficient(j));
//					map.compute(t, (p,c)-> c== null ? coe : mc.add(c, coe));
//				}
//			}
			return new MultinomialX<>(mc, map, para1.degree+para2.degree);
		}

		/*
		 * @see cn.timelives.java.math.numberModels.MathCalculator#divide(java.lang.Object, java.lang.Object)
		 */
		@Override
		public MultinomialX<T> divide(MultinomialX<T> para1, MultinomialX<T> para2) {
			Pair<MultinomialX<T>,MultinomialX<T>> p = divideAndReminder(para1, para2);
			if(!isZero(p.getSecond())) {
				throw new UnsupportedCalculationException("Reminder!= 0, see divideAndReminder");
			}
			return p.getFirst();
		}
		
		/**
		 * Returns a pair of quotient and the reminder of the division of two MultinomialX.
		 * <pre>p1 = k*p2 + r</pre> The degree of {@code r} is smaller than {@code p2}.
		 * @param p1
		 * @param p2
		 * @return a pair of the quotient and the reminder.
		 */
		public Pair<MultinomialX<T>,MultinomialX<T>> divideAndReminder(MultinomialX<T> p1,MultinomialX<T> p2){
			if(isZero(p2)) {
				throw new ArithmeticException("divide by zero!");
			}
			int mp1 = p1.degree,mp2 = p2.degree;
			if(mp2 > mp1) {
				return new Pair<>(zero,p1);
			}
			if(isZero(p1)) {
				return new Pair<>(zero,zero);
			}
			NavigableMap<Integer, T> remains = p1.getCoefficientMap();
			TreeMap<Integer,T> quotient = new TreeMap<>();
			T first = p2.getCoefficient(mp2);
			List<WithInt<T>> toSubtract = new ArrayList<>(p2.map.size()-1);
			for(Entry<Integer,T> en : p2.map.entrySet()) {
				int n = en.getKey();
				if(n != mp2) {
					toSubtract.add(new WithInt<>(n,en.getValue()));
				}
			}
			while(!remains.isEmpty()) {
				Entry<Integer,T> en = remains.pollLastEntry();
				int p = en.getKey() - mp2;
				if(p < 0) {
					remains.put(en.getKey(), en.getValue());
					break;
				}
				T k = mc.divide(en.getValue(), first);
				quotient.put(p, k);
				for(WithInt<T> w : toSubtract) {
					int n = p+w.getInt();
					T a = mc.multiply(k, w.getObj());
					remains.compute(n, (x,t)->{
						if(t == null) {
							return mc.negate(a);
						}
						t = mc.subtract(t, a);
						if(mc.isZero(t)) {
							return null;
						}
						return t;
					});
				}
			}
			MultinomialX<T> qm = new MultinomialX<>(mc, quotient, mp1-mp2);
			MultinomialX<T> rm = remains.isEmpty() ? zero : new MultinomialX<>(mc, remains, remains.lastKey());
			return new Pair<MultinomialX<T>, MultinomialX<T>>(qm, rm);
		}
		
		/**
		 * Returns the reminder of the two polynomials.
		 */
		@Override
		public MultinomialX<T> reminder(MultinomialX<T> a, MultinomialX<T> b) {
			return divideAndReminder(a, b).getSecond();
		}

		/*
		 * @see cn.timelives.java.math.numberModels.MathCalculator#getOne()
		 */
		@Override
		public MultinomialX<T> getOne() {
			return one;
		}


		/*
		 * @see cn.timelives.java.math.numberModels.MathCalculator#multiplyLong(java.lang.Object, long)
		 */
		@Override
		public MultinomialX<T> multiplyLong(MultinomialX<T> p, long l) {
			if(l==1) {
				return p;
			}
			if(l == 0) {
				return zero;
			}
			if(l == -1) {
				return negate(p);
			}
			NavigableMap<Integer, T> nmap = p.getCoefficientMap();
			CollectionSup.modifyMap(nmap, (x,y)->mc.multiplyLong(y, l));
			return new MultinomialX<>(mc, nmap, p.degree);
		}

		/*
		 * @see cn.timelives.java.math.numberModels.MathCalculator#divideLong(java.lang.Object, long)
		 */
		@Override
		public MultinomialX<T> divideLong(MultinomialX<T> p, long l) {
			if(l == 0) {
				throw new ArithmeticException("Divide by zero");
			}
			if(l==1) {
				return p;
			}
			if(l == -1) {
				return negate(p);
			}
			NavigableMap<Integer, T> nmap = p.getCoefficientMap();
			CollectionSup.modifyMap(nmap, (x,y)->mc.divideLong(y, l));
			return new MultinomialX<>(mc, nmap, p.degree);
		}

		/*
		 * @see cn.timelives.java.math.numberModels.MathCalculator#squareRoot(java.lang.Object)
		 */
		@Override
		public MultinomialX<T> squareRoot(MultinomialX<T> p) {
			throw new UnsupportedCalculationException();
		}

		/*
		 * @see cn.timelives.java.math.numberModels.MathCalculator#nroot(java.lang.Object, long)
		 */
		@Override
		public MultinomialX<T> nroot(MultinomialX<T> x, long n) {
			throw new UnsupportedCalculationException();
		}
		
		private NavigableMap<Integer, T> multiplyToMap(NavigableMap<Integer, T> p1,NavigableMap<Integer, T> p2) {
			TreeMap<Integer,T> map = new TreeMap<>();
			for(Entry<Integer,T> en1 : p1.entrySet()){
				int n1 = en1.getKey();
				for(Entry<Integer,T> en2 : p2.entrySet()){
					int t = n1+en2.getKey();
					T coe = mc.multiply(en1.getValue(), en2.getValue());
					map.compute(t, (p,c)-> c== null ? coe : mc.add(c, coe));
				}
			}
			return map;
		}
		
		/*
		 * @see cn.timelives.java.math.numberModels.MathCalculator#pow(java.lang.Object, long)
		 */
		@Override
		public MultinomialX<T> pow(MultinomialX<T> p, long exp) {
			if(exp == 1) {
				return p;
			}
			if(p.degree == 0) {
				//single 
				return constant(mc, mc.pow(p.getCoefficient(0), exp));
			}
			long mp = exp*p.degree;
			if(mp > Integer.MAX_VALUE || mp < 0) {
				throw new ArithmeticException("Too big for exp="+exp);
			}
			NavigableMap<Integer, T> map = ModelPatterns.binaryReduce(exp, one.map, p.map, this::multiplyToMap);
			return new MultinomialX<>(mc, map, (int)mp);
		}

		/*
		 * @see cn.timelives.java.math.numberModels.MathCalculator#constantValue(java.lang.String)
		 */
		@Override
		public MultinomialX<T> constantValue(String name) {
			return constant(mc, mc.constantValue(name));
		}

		/*
		 * @see cn.timelives.java.math.numberModels.MathCalculatorAdapter#abs(java.lang.Object)
		 */
		@Override
		public MultinomialX<T> abs(MultinomialX<T> para) {
			if(mc.compare(para.getCoefficient(para.degree), mc.getZero())<0) {
				return negate(para);
			}
			return para;
		}
		
		/**
		 * Returns a the greatest common divisor of {@code a} and {@code b}. A greatest common divisor of polynomial {@code p} and {@code q} 
		 * is a polynomial {@code d} that divides {@code p} and {@code q} such that every common divisor of {@code p} and {@code q} also divides {@code d}.
		 * 
		 * @return the  greatest common divisor of {@code a} and {@code b}, whose leading coefficient is one.
		 */
		@Override
		public MultinomialX<T> gcd(MultinomialX<T> a, MultinomialX<T> b) {
			if(a.degree < b.degree) {
				MultinomialX<T> t = a;
				a = b;
				b = t;
			}
			while(b.degree > 0) {
				MultinomialX<T> t = reminder(a, b);
				a = b;
				b = t;
			}
			return a.unit();
		}


		/*
		 * @see cn.timelives.java.math.numberModels.MathCalculator#getNumberClass()
		 */
		@Override
		public Class<?> getNumberClass() {
			return MultinomialX.class;
		}
		/*
		 * @see cn.timelives.java.math.numberModels.NTCalculator#isInteger(java.lang.Object)
		 */
		@Override
		public boolean isInteger(MultinomialX<T> x) {
			throw new UnsupportedCalculationException();
		}
		/*
		 * @see cn.timelives.java.math.numberModels.NTCalculator#isQuotient(java.lang.Object)
		 */
		@Override
		public boolean isQuotient(MultinomialX<T> x) {
			throw new UnsupportedCalculationException();
		}
		/*
		 * @see cn.timelives.java.math.numberModels.NTCalculator#mod(java.lang.Object, java.lang.Object)
		 */
		@Override
		public MultinomialX<T> mod(MultinomialX<T> a, MultinomialX<T> b) {
			return divideAndReminder(a, b).getSecond();
		}
		/*
		 * @see cn.timelives.java.math.numberModels.NTCalculator#divideToInteger(java.lang.Object, java.lang.Object)
		 */
		@Override
		public MultinomialX<T> divideToInteger(MultinomialX<T> a, MultinomialX<T> b) {
			return divideAndReminder(a, b).getFirst();
		}
		
		
		
	}
	
	public static void main(String[] args) {
		MathCalculator<Integer> mc = Calculators.getCalculatorInteger();
		MultinomialX<Integer> p1 = MultinomialX.valueOf(mc, 6,7,1);
		MultinomialX<Integer> p2 = MultinomialX.valueOf(mc, -6,-5,1);
		MultinomialCalculator<Integer> mmc = getCalculator(mc);
		MultinomialX<Integer> re = mmc.add(p1, p2);
		print(p1);
		print(p2);
//		print(re);
//		print(mmc.multiply(p1, p2));//x^3 + 3*x^2 + 3*x + 1
		print(mmc.divideAndReminder(p1, p2));
//		print(mmc.pow(p2, 5));
//		print(mmc.gcd(mmc.pow(p2, 5), mmc.pow(p2, 3)));
		print(mmc.gcd(p1, p2));
	}
}

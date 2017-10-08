/**
 * 2017-10-06
 */
package cn.timelives.java.math.equation;

import java.util.List;
import java.util.function.Function;

import cn.timelives.java.math.FlexibleMathObject;
import cn.timelives.java.math.Multinomial;
import cn.timelives.java.math.Utils;
import cn.timelives.java.math.function.AbstractSVPFunction;
import cn.timelives.java.math.function.AbstractSVPFunction.LinearFunction;
import cn.timelives.java.math.function.MathFunction;
import cn.timelives.java.math.numberModels.MathCalculator;
import cn.timelives.java.math.numberModels.NumberFormatter;

/**
 * SVPEquation stands for <i>single variable polynomial inequation</i>.
 * Generally, the inequation can be shown as 
 * <pre>an*x^n + ... + a1*x + a0 <i>op</i> 0 , (an!=0,n>0)</pre>
 * where <i>op</i> is one of the inequation operation(Inequation{@link #getInquationType()}).
 * @author liyicheng
 * 2017-10-06 09:33
 *
 */
public abstract class SVPInequation<T> extends SVInquation<T> implements Multinomial<T>{
	protected final int mp;
	
	/**
	 * @param mc
	 * @param op
	 */
	protected SVPInequation(MathCalculator<T> mc, Type op,int mp) {
		super(mc, op);
		this.mp = mp;
	}
	/*
	 * @see cn.timelives.java.math.Multinomial#getMaxPower()
	 */
	@Override
	public int getMaxPower() {
		return mp;
	}
	/**
	 * Determine whether the two inequations are equal, this method only 
	 * compare the corresponding coefficient. 
	 * <p>Therefore, for example, 
	 * {@literal 2x>0} and {@literal x>0} are considered to be not the same.
	 * This assures that if two equations are equal, then the functions returned
	 * by {@link #asFunction()} are equal. 
	 * 
	 */
	@Override
	public boolean valueEquals(FlexibleMathObject<T> obj) {
		if(obj == this) {
			return true;
		}
		if (!(obj instanceof SVPInequation)) {
			return false;
		}
		SVPInequation<T> sv = (SVPInequation<T>) obj;
		return Multinomial.isEqual(this,sv, mc::isEqual);
	}
	
	/*
	 * @see cn.timelives.java.math.FlexibleMathObject#valueEquals(cn.timelives.java.math.FlexibleMathObject, java.util.function.Function)
	 */
	@Override
	public <N> boolean valueEquals(FlexibleMathObject<N> obj, Function<N, T> mapper) {
		if(obj == this) {
			return true;
		}
		if (!(obj instanceof SVPInequation)) {
			return false;
		}
		SVPInequation<N> sv = (SVPInequation<N>) obj;
		return Multinomial.isEqual(this,sv, Utils.mappedIsEqual(mc, mapper));
	}
	
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
		sb.append(' ').append(op.toString());
		sb.append(" 0");
		return sb.toString();
	}
	
	
	/*
	 * @see cn.timelives.java.math.SingleVInquation#mapTo(java.util.function.Function, cn.timelives.java.math.numberModels.MathCalculator)
	 */
	@Override
	public abstract <N> SVPInequation<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator);
	
	
	
	static class FromFunction<T> extends SVPInequation<T>{
		final AbstractSVPFunction<T> f;
		/**
		 * @param mc
		 * @param op
		 */
		protected FromFunction(MathCalculator<T> mc, Type op,AbstractSVPFunction<T> f) {
			super(mc, op,f.getMaxPower());
			this.f = f;
		}
		/*
		 * @see cn.timelives.java.math.Multinomial#getCoefficient(int)
		 */
		@Override
		public T getCoefficient(int n) {
			return f.getCoefficient(n);
		}
		
		/*
		 * @see cn.timelives.java.math.equation.SVPInequation#mapTo(java.util.function.Function, cn.timelives.java.math.numberModels.MathCalculator)
		 */
		@Override
		public <N> SVPInequation<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator) {
			return new FromFunction<>(newCalculator, op, f.mapTo(mapper, newCalculator));
		}
		/*
		 * @see cn.timelives.java.math.equation.SVCompareStructure#compute(java.lang.Object)
		 */
		@Override
		public T compute(T x) {
			return f.apply(x);
		}
	}
	
	public static class LinearInequation<T> extends SVPInequation<T>{
		private final AbstractSVPFunction.LinearFunction<T> f;
		/**
		 * @param mc
		 * @param op
		 * @param mp
		 */
		protected LinearInequation(MathCalculator<T> mc, Type op,AbstractSVPFunction.LinearFunction<T> f) {
			super(mc, op, 1);
			this.f = f;
		}
		
		/*
		 * @see cn.timelives.java.math.Multinomial#getCoefficient(int)
		 */
		@Override
		public T getCoefficient(int n) {
			return f.getCoefficient(n);
		}
		
		/*
		 * @see cn.timelives.java.math.equation.SVCompareStructure#compute(java.lang.Object)
		 */
		@Override
		public T compute(T x) {
			return f.apply(x);
		}
		
		/*
		 * @see cn.timelives.java.math.equation.CompareStructure#asFunction()
		 */
		@Override
		public LinearFunction<T> asFunction() {
			return f;
		}

		/*
		 * @see cn.timelives.java.math.equation.SVPInequation#mapTo(java.util.function.Function, cn.timelives.java.math.numberModels.MathCalculator)
		 */
		@Override
		public <N> LinearInequation<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator) {
			return new LinearInequation<>(newCalculator, op, f.mapTo(mapper, newCalculator));
		}
	}
	
	/**
	 * Creates an SVPInequation from a list of coefficients. The index of the 
	 * coefficient is considered as the corresponding power of {@code x}. 
	 * For example, a list [1,2,3] represents for {@literal 3x^2+2x+1}.
	 * @param coes a list of coefficient
	 * @param op the inequation operation type
	 * @param mc a {@link MathCalculator}
	 * @return an inequation
	 */
	public static <T> SVPInequation<T> valueOf(List<T> coes,Type op,MathCalculator<T> mc){
		@SuppressWarnings("unchecked")
		T[] arr = (T[]) coes.toArray();
		for(int i=0;i<arr.length;i++) {
			if(arr[i]==null) {
				throw new NullPointerException("null in list: index = "+i);
			}
		}
		return new FromFunction<>(mc, op, AbstractSVPFunction.valueOf(coes, mc));
	}
}

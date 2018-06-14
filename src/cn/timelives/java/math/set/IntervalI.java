package cn.timelives.java.math.set;

import cn.timelives.java.math.MathObject;
import cn.timelives.java.math.MathCalculator;
import cn.timelives.java.math.numberModels.NumberFormatter;

import java.util.function.Function;
public final class IntervalI<T> extends Interval<T>{

	/**
	 * decide the type of this interval
	 */
	private final int type;
	
	private final T left, right;
	
	static final int LEFT_OPEN_MASK = 0x2,
								RIGHT_OPEN_MASK = 0x4,
								BOTH_OPEN_MASK = LEFT_OPEN_MASK | RIGHT_OPEN_MASK;
	
	/**
	 * Create a new Interval with the given arguments. 
	 * @param mc the math calculator,only compare methods will be used.
	 * @param downerBound the downer bound of this interval, or {@code null} to indicate unlimited.
	 * @param upperBound  the upper bound of this interval, or {@code null} to indicate unlimited.
	 * @param downerInclusive determines whether downer should be inclusive
	 * @param upperInclusive  determines whether upper should be inclusive
	 */
	public IntervalI(MathCalculator<T> mc,T downerBound,T upperBound,boolean downerInclusive,boolean upperInclusive) {
		super(mc);
		int type = 0;
		if(downerBound == null || ! downerInclusive){
			type |= LEFT_OPEN_MASK;
		}
		if(upperBound == null || ! upperInclusive){
			type |= RIGHT_OPEN_MASK;
		}
		if(downerBound != null && upperBound != null ){
			int t =mc.compare(downerBound, upperBound);
			if( t >0) {
				throw new IllegalArgumentException("downerBound > upperBound");
			}
			if(t == 0 && (!downerInclusive || !upperInclusive)) {
				throw new IllegalArgumentException("downerBound==upperBound but not a closed interval");
			}
		}
		this.left = downerBound;
		this.right = upperBound;
		this.type = type;
	}
	
	
	IntervalI(MathCalculator<T> mc,T left,T right,int type) {
		super(mc);
		this.left = left;
		this.right = right;
		this.type  =type;
	}
	
	
	@Override
	public boolean contains(T n) {
		if(left == null){
			if(right == null){
				return true;
			}
			int t = mc.compare(n, right);
			if((type & RIGHT_OPEN_MASK) == RIGHT_OPEN_MASK){
				// right open
				return t == -1;
			}else{
				return t == -1 || t == 0;
			}
		}else if(right == null){
			int t = mc.compare(left, n);
			if((type & LEFT_OPEN_MASK) == LEFT_OPEN_MASK){
				//left open
				return t == -1;
			}else{
				return t != 1;
			}
		}
		int rl = mc.compare(left, n);
		if((type & LEFT_OPEN_MASK) == LEFT_OPEN_MASK){
			if(rl != -1){
				return false;
			}
		}else{
			if(rl == 1){
				return false;
			}
		}
		int rr = mc.compare(n, right);
		if((type & RIGHT_OPEN_MASK) == RIGHT_OPEN_MASK){
			return rr == -1;
		}else{
			return rr != 1;
		}
		
	}


	@Override
	public T upperBound() {
		return right;
	}


	@Override
	public boolean isUpperBoundInclusive() {
		return (type & RIGHT_OPEN_MASK) == 0;
	}


	@Override
	public T downerBound() {
		return left;
	}


	@Override
	public boolean isDownerBoundInclusive() {
		return (type & LEFT_OPEN_MASK) == 0;
	}


	@Override
	public T lengthOf() {
		if(left == null || right == null)
			return null;
		return mc.subtract(right, left);
	}

	
	private boolean inRangeExclusive(T n){
		if(right == null){
			if(left == null){
				return true;
			}
			return mc.compare(left, n) == -1;
		}
		if(left == null){
			return mc.compare(n,right) == -1;
		}
		
		int t = mc.compare(left,n);
		if(t != -1){
			return false;
		}
		t = mc.compare(n, right);
		return t == -1;
	}
	
	
	private final void thr(T n) throws IllegalArgumentException{
		throw new IllegalArgumentException("n = "+n);
	}
	
	
	@Override
	public Interval<T> downerPart(T n) {
		if(inRangeExclusive(n)){
			return new IntervalI<T>(mc,left,n,type);
		}
		thr(n);
		return null;
	}


	@Override
	public Interval<T> downerPart(T n, boolean include) {
		if(inRangeExclusive(n)){
			return new IntervalI<T>(mc,left,n,(type & LEFT_OPEN_MASK) | (include ? 0 : RIGHT_OPEN_MASK));
		}
		thr(n);
		return null;
	}


	@Override
	public Interval<T> upperPart(T n) {
		if(inRangeExclusive(n)){
			return new IntervalI<T>(mc,n,right,type);
		}
		thr(n);
		return null;
	}


	@Override
	public Interval<T> upperPart(T n, boolean include) {
		if(inRangeExclusive(n)){
			return new IntervalI<T>(mc,n,right,(type & RIGHT_OPEN_MASK) | (include ? 0 : LEFT_OPEN_MASK));
		}
		thr(n);
		return null;
	}


	@Override
	public Interval<T> expandUpperBound(T n) {
		if(mc.compare(right, n) == -1){
			return new IntervalI<T>(mc,left,n,type);
		}
		thr(n);
		return null;
	}


	@Override
	public Interval<T> expandUpperBound(T n, boolean include) {
		if(mc.compare(right, n) == -1){
			return new IntervalI<T>(mc,left,n,(type & LEFT_OPEN_MASK) | (include ? 0 : RIGHT_OPEN_MASK));
		}
		thr(n);
		return null;
	}


	@Override
	public Interval<T> expandDownerBound(T n) {
		if(mc.compare(left, n) == -1){
			return new IntervalI<T>(mc,n,right,type);
		}
		thr(n);
		return null;
	}


	@Override
	public Interval<T> expandDownerBound(T n, boolean include) {
		if(mc.compare(left, n) == -1){
			return new IntervalI<T>(mc,n,right,(type & RIGHT_OPEN_MASK) | (include ? 0 : LEFT_OPEN_MASK));
		}
		thr(n);
		return null;
	}


	@Override
	public Interval<T> sameTypeInterval(T downerBound, T upperBound) {
		if(downerBound == null || upperBound == null){
			throw new NullPointerException();
		}
		return new IntervalI<>(mc,downerBound,upperBound,type);
	}


	@Override
	public boolean contains(Interval<T> iv) {
		T iL = iv.downerBound();
		T iR = iv.upperBound();
		//left side judge
		if(left != null){
			int t = mc.compare(left, iL);
			if(t == 1){
				return false;
			}
			if(t == 0 && iv.isDownerBoundInclusive() && isDownerBoundInclusive()== false){
				return false;
			}
		}
		if(right != null){
			int t = mc.compare(iR, right);
			if(t == 1){
				return false;
			}
			if(t == 0 && iv.isUpperBoundInclusive() && isUpperBoundInclusive()== false){
				return false;
			}
		}
		return true;
	}


	@Override
	public Interval<T> intersect(Interval<T> iv) {
		T iL = iv.downerBound();
		T iR = iv.upperBound();
		if((right == null || iL ==null || mc.compare(right, iL) >=0) &&
			(iR == null ||  left == null || mc.compare(iR, left) >=0)){
			if(mc.compare(left, iL) == -1){
				if(mc.isEqual(right, iL) && (!isUpperBoundInclusive()|| !iv.isDownerBoundInclusive())) {
					return null;
				}
				return new IntervalI<>
				(mc, iL, right, iv.isDownerBoundInclusive(), isUpperBoundInclusive());
			}else{
				if(mc.isEqual(left, iR) && (!isDownerBoundInclusive() || !iv.isUpperBoundInclusive()) ) {
					return null;
				}
				return new IntervalI<>
				(mc, left, iR , isDownerBoundInclusive(),iv.isUpperBoundInclusive());
			}
		}
		return null;
		
	}


	@Override
	public String toString() {
		return toString(NumberFormatter.getToStringFormatter());
	}
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.Interval#toString(cn.timelives.java.math.number_models.NumberFormatter)
	 */
	@Override
	public String toString(NumberFormatter<T> nf) {
		StringBuilder sb = new StringBuilder();
		if(isDownerBoundInclusive()){
			sb.append('[');
		}else{
			sb.append('(');
		}
		if(left==null){
			sb.append("-∞");
		}else{
			sb.append(nf.format(left, mc));
		}
		sb.append(',');
		if(right == null){
			sb.append("+∞");
		}else{
			sb.append(nf.format(right, mc));
		}
		if(isUpperBoundInclusive()){
			sb.append(']');
		}else{
			sb.append(')');
		}
		return sb.toString();
	}

	@Override
	public <N> Interval<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator) {
		return new IntervalI<>(newCalculator, mapper.apply(left), mapper.apply(right), type);
	}


	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Interval){
			Interval<?> iv = (Interval<?>) obj;
			if(isDownerBoundInclusive()== iv.isDownerBoundInclusive() 
					&& isUpperBoundInclusive() == iv.isUpperBoundInclusive()){
				return mc.equals(iv.getMathCalculator()) && (left == null ? iv.downerBound() == null : left.equals(iv.downerBound())) &&
						(right == null ? iv.upperBound() == null : right.equals(iv.upperBound()));
			}
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int hash = mc.hashCode();
		hash = hash + 31*type;
		hash = hash*37 + left.hashCode();
		hash = hash*37 + right.hashCode();
		return hash;
	}


	@Override
	public <N> boolean valueEquals(MathObject<N> obj, Function<N, T> mapper) {
		if(obj instanceof Interval){
			Interval<N> iv = (Interval<N>) obj;
			if(isDownerBoundInclusive()== iv.isDownerBoundInclusive() 
					&& isUpperBoundInclusive() == iv.isUpperBoundInclusive()){
				N iL = iv.downerBound();
				N iR = iv.upperBound();
				if(iL == null){
					if(left != null){
						return false;
					}
				}else if(left == null){
					return false;
				}else{
					T iLM = mapper.apply(iL);
					if(!mc.isEqual(iLM, left)){
						return false;
					}
				}
				if(iR == null){
					if(right != null){
						return false;
					}
				}else if(right == null){
					return false;
				}else{
					T iRM = mapper.apply(iR);
					if(!mc.isEqual(iRM, right)){
						return false;
					}
				}
				return true;
				
			}
		}
		return false;
	}

	@Override
	public boolean valueEquals(MathObject<T> obj) {
		if(obj instanceof Interval){
			Interval<T> iv = (Interval<T>) obj;
			if(isDownerBoundInclusive()== iv.isDownerBoundInclusive() 
					&& isUpperBoundInclusive() == iv.isUpperBoundInclusive()){
				T iL = iv.downerBound();
				T iR = iv.upperBound();
				if(iL == null){
					if(left != null){
						return false;
					}
				}else if(left == null){
					return false;
				}else{
					if(!mc.isEqual(iL, left)){
						return false;
					}
				}
				if(iR == null){
					if(right != null){
						return false;
					}
				}else if(right == null){
					return false;
				}else{
					if(!mc.isEqual(iR, right)){
						return false;
					}
				}
				return true;
				
			}
		}
		return false;
	}

	
	
}

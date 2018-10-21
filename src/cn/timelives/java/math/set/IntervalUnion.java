package cn.timelives.java.math.set;

import cn.timelives.java.math.MathCalculator;
import cn.timelives.java.math.MathObject;
import cn.timelives.java.math.numberModels.api.FlexibleNumberFormatter;
import cn.timelives.java.math.numberModels.api.NumberFormatter;
import cn.timelives.java.utilities.CollectionSup;
import cn.timelives.java.utilities.ModelPatterns;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;

/**
 * A union of intervals.
 * @author liyicheng
 * 2017-09-09 11:38
 *
 */
public class IntervalUnion<T> extends AbstractMathSet<T>{
	/**
	 * A sorted list. May be empty.
	 * No change to this list is permitted.
	 */
	private final List<Interval<T>> is;
	
	/**
	 * 
	 */
	public IntervalUnion(MathCalculator<T> mc) {
		super(mc);
		is = Collections.emptyList();
	}
	
	/**
	 * @param mc
	 * @param is a list of intervals, sorted by it bounds and no intersection.
	 */
	IntervalUnion(MathCalculator<T> mc,List<Interval<T>> is) {
		super(mc);
		this.is = is;
	}
	/**
	 * @param mc
	 * @param is a list of intervals, sorted by it bounds and no intersection.
	 */
	IntervalUnion(MathCalculator<T> mc,Interval<T> v) {
		super(mc);
		this.is = Collections.singletonList(v);
	}
	/**
	 * @see cn.timelives.java.math.set.MathSet#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(T t) {
		int pos=findSmallerDownerBound(t);
		if(pos >= 0 ){
			return is.get(pos).contains(t);
		}
		if(pos == -1){
			return false;
		}
		pos = - pos - 2;
		
		return is.get(pos).contains(t);
	}
	/**
	 * Determines whether this IntervalUnion contains the interval.
	 * @param v an Interval
	 * @return {@code true} if the Interval is contained.
	 */
	public boolean contains(Interval<T> v) {
		if(v.downerBound() == null) {
			return is.get(0).contains(v);
		}
		int pos = findSmallerDownerBound(v.downerBound());
		if(pos == -1) {
			return false;
		}
		return is.get(pos).contains(v);
	}
	
	
	
	/**
	 * Returns the interval that contains {@code t}, or returns 
	 * {@code null} if it is not contained in all intervals.
	 * @param t a number
	 * @return the interval or null
	 */
	public Interval<T> findInverval(T t){
		int pos=findSmallerDownerBound(t);
		if(pos == -1){
			return null;
		}
		Interval<T> candidate = is.get(pos);
		if(candidate.contains(t)) {
			return candidate;
		}else {
			return null;
		}
	}
	/**
	 * Gets the number of intervals in this interval union.
	 * @return the number of intervals
	 */
	public int getNumber() {
		return is.size();
	}
	
	/**
	 * Gets an interval from this interval union.
	 * @param n the index
	 * @return an Interval
	 * @throws IndexOutOfBoundsException if {@code n<0||n>=this.getNumber()}.
	 */
	public Interval<T> getInterval(int n){
		return is.get(n);
	}
	/**
	 * Determines whether this IntervalUnion is empty.
	 * @return {@code true} if this is empty.
	 */
	public boolean isEmpty() {
		return is.isEmpty();
	}
	
	/**
	 * Find the closet downer bound which is smaller to t.
	 * @param t non-null
	 * @return
	 */
	private int findSmallerDownerBound(T t) {
		if(is.isEmpty()) {
			return -1;
		}
		int n = ModelPatterns.binarySearch(0, is.size(), x -> {
			T downer = is.get(x).downerBound();
			if (downer == null) {
				return -1;
			}
            return getMc().compare(downer, t);
		});
		if(n<-1) {
			n = -n-2;
		}
		return n;
	}
	
	private boolean shouldUnionWithThePrevInterval(Interval<T> v,int downer) {
		if(v.isDownerBoundInclusive() && downer>0) {
			Interval<T> prev = is.get(downer-1);
            if (!prev.isUpperBoundInclusive() && getMc().isEqual(v.downerBound(), prev.upperBound())) {
				return true;
			}
		}
		return false;
	}
	
	private boolean shouldUnionWithTheNextInterval(Interval<T> v,Interval<T> in) {
		return v.isUpperBoundInclusive() || in.isDownerBoundInclusive();
	}
	
	/**
	 * Union this with the given Interval, returns a new IntervalUnion.
	 * @param v an Interval
	 */
	public IntervalUnion<T> unionWith(Interval<T> v){
		if(is.isEmpty()) {
            return new IntervalUnion<>(getMc(), v);
		}
		//deal with inf cases
		
		//find the closet intervals to the downer bound and the upper bound first.
		int downer, upper;
		if(v.downerBound() == null) {
			downer = -1;
		}else {
			downer = findSmallerDownerBound(v.downerBound());
		}
		if(v.upperBound() == null) {
			upper = is.size()-1;
		}else {
			upper = findSmallerDownerBound(v.upperBound());
		}
		
		if(upper == -1) {
			//there is no interval smaller than it, it must be the lower one.
			return insertInterval(0,v);
		}
//		if(downer==upper) {
//			//the two interval is the identity
//			Interval<T> in = is.get(downer);
//			int t = mc.compare(in.upperBound(), v.downerBound());
//			if(t>=0) {
//				if(in.isUpperBoundInclusive() || v.isDownerBoundInclusive() || t!=0) {
//					//the new interval is the union of the two
//					Interval<T> nin = in.expandUpperBound(v.upperBound(), v.isUpperBoundInclusive());
//					return replaceInterval(downer, nin);
//				}
//				// the case that (a,b) (b,c)
//			}
//			return insertInterval(downer+1, v);
//			
//		}
		T ndowner,nupper;
		boolean ndownerIn,nupperIn;
		int indexDowner,indexUpper;
		if(downer==-1) {
			ndownerIn = v.isDownerBoundInclusive();
			ndowner = v.downerBound();
			indexDowner = 0;
		}else {
			Interval<T> in = is.get(downer);
			final T vdb= v.downerBound();
            if (in.downerBound() != null && vdb != null && getMc().isEqual(in.downerBound(), vdb)) {
				if(shouldUnionWithThePrevInterval(v, downer)) {
					Interval<T> prev = is.get(downer-1);
					indexDowner = downer-1;
					ndownerIn = prev.isDownerBoundInclusive();
					ndowner = prev.downerBound();
				}else {
					ndownerIn = v.isDownerBoundInclusive() || in.isDownerBoundInclusive();
					ndowner = in.downerBound();
					indexDowner = downer;
				}
			}else {
                int t2 = vdb == null ? 1 : in.upperBound() == null ? 1 : getMc().compare(in.upperBound(), vdb);
				if(t2>=0 && (t2!=0 || in.isUpperBoundInclusive() || v.isDownerBoundInclusive() )) {
					indexDowner = downer;
					ndownerIn = in.isDownerBoundInclusive();
					ndowner = in.downerBound();
				}else {
					indexDowner = downer+1;
					ndownerIn = v.isDownerBoundInclusive();
					ndowner = v.downerBound();
				}
			}
		}
		if(v.upperBound() == null){
			indexUpper = upper;
			nupper = v.upperBound();
			nupperIn = v.isUpperBoundInclusive();
		}else{
			//deal with upper
			Interval<T> in = is.get(upper);
			final T vub= v.upperBound();
            if (vub != null && in.downerBound() != null && getMc().isEqual(in.downerBound(), vub)) {
				if(shouldUnionWithTheNextInterval(v,in)) {
					nupperIn = in.isUpperBoundInclusive();
					nupper = in.upperBound();
					indexUpper = upper;
				}else {
					nupperIn = false;
					nupper = vub;
					indexUpper = upper-1;
				}
			}else {
                int t2 = vub == null ? -1 : in.upperBound() == null ? 1 : getMc().compare(in.upperBound(), vub);
				indexUpper= upper;
				if(t2==0) {
					nupper = in.upperBound();
					nupperIn = in.isUpperBoundInclusive() || v.isUpperBoundInclusive();
				}else if(t2>0) {
					nupper = in.upperBound();
					nupperIn = in.isUpperBoundInclusive();
				}else {
					nupper = v.upperBound();
					nupperIn = v.isUpperBoundInclusive();
				}
			}
		}
        Interval<T> nin = new IntervalI<>(getMc(), ndowner, nupper, ndownerIn, nupperIn);
		indexUpper++;
		return replaceIntervalRange(indexDowner,indexUpper,nin);
	}
	/**
	 * Union {@code this} with another IntervalUnion
	 * @param in another IntervalUnion.
	 * @return a new IntervalUnion
	 */
	public IntervalUnion<T> union(IntervalUnion<T> in){
		List<Interval<T>> nivs = copyList();
		for(Interval<T> v : in.is) {
            unionWith0(nivs, v, getMc());
        }
        return new IntervalUnion<>(getMc(), nivs);
	}
	
	/**
	 * Insert the interval
	 * @param n the index of the interval {@code v} in the nex interval union.
	 * @param v
	 * @return
	 */
	private IntervalUnion<T> insertInterval(int n,Interval<T> v){
		final int size = is.size()+1;
		List<Interval<T>> nivs = new ArrayList<>(size);
		for(int i=0;i<n;i++) {
			nivs.add(is.get(i));
		}
		nivs.add(v);
		for(int i=n+1;i<size;i++) {
			nivs.add(is.get(i-1));
		}
        return new IntervalUnion<>(getMc(), nivs);
	}
	
	private IntervalUnion<T> replaceInterval(int n,Interval<T> v){
		List<Interval<T>> nivs = copyList();
		nivs.set(n, v);
        return new IntervalUnion<>(getMc(), nivs);
	}
	/**
	 * Replaces the intervals.
	 * @param downer inclusive
	 * @param upper exclusive
	 * @param v
	 * @return
	 */
	private IntervalUnion<T> replaceIntervalRange(int downer,int upper,Interval<T> v){
		if(downer==upper) {
			return insertInterval(downer, v);
		}else if(downer == upper-1) {
			return replaceInterval(downer,v);
		}
		final int size = is.size()+downer-upper+1;
		List<Interval<T>> nivs = new ArrayList<>(size);
		for(int i=0;i<downer;i++) {
			nivs.add(is.get(i));
		}
		nivs.add(v);
		for(int i=upper,isize = is.size();i<isize;i++) {
			nivs.add(is.get(i));
		}
        return new IntervalUnion<>(getMc(), nivs);
	}
	
	@SuppressWarnings("unchecked")
	private List<Interval<T>> copyList(){
		if(is instanceof ArrayList) {
			return (List<Interval<T>>) ((ArrayList<T>)is).clone();
		}else {
			return new ArrayList<>(is);
		}
	}
	/**
	 * Returns the intersect of {@code this} and the interval.
	 * @param v an Interval
	 * @return a new IntervalUnion
	 */
	public IntervalUnion<T> intersect(Interval<T> v){
		List<Interval<T>> nis = new ArrayList<>(2);
		intersect0(nis,v);
        return new IntervalUnion<>(getMc(), nis);
	}
	
	private void intersect0(List<Interval<T>> nis,Interval<T> v){
		int downer,upper;
		if(v.downerBound()==null) {
			downer = 0;
		}else {
			downer = findSmallerDownerBound(v.downerBound());
		}
		if(v.upperBound() == null) {
			upper = is.size()-1;
		}else {
			upper = findSmallerDownerBound(v.upperBound());
		}
		Iterator<Interval<T>> it = is.listIterator(downer);
		for(int i=downer;i<=upper;i++) {
			Interval<T> t = it.next().intersect(v);
			if(t != null) {
				nis.add(t);
			}
		}
	}
	
	/**
	 * Returns the intersect of {@code this} and another IntervalUnion. 
	 * @param in another IntervalUnion
	 * @return a new IntervalUnion
	 */
	public IntervalUnion<T> intersect(IntervalUnion<T> in){
		//special cases.
		if(is.isEmpty()) {
			return this;
		}
		if(in.isEmpty()) {
			return in;
		}
		List<Interval<T>> nis = new ArrayList<>(Math.min(is.size(),in.is.size()));
		for(Interval<T> v : in.is) {
			intersect0(nis,v);
		}
        return new IntervalUnion<>(getMc(), nis);
	}
	
	/**
	 * Returns the complement of this interval union.
	 * @return the complement
	 */
	public IntervalUnion<T> complement(){
		if(is.isEmpty()) {
            return universe(getMc());
		}
		List<Interval<T>> nis = new ArrayList<>(is.size()+1);
		T prev = null;
		boolean prevInclusive = false;
		for(Interval<T> v : is) {
			T downer = v.downerBound();
			if(downer!=null) {
				//not -inf
                Interval<T> in = Interval.valueOf(prev, downer, prevInclusive, !v.isDownerBoundInclusive(), getMc());
				nis.add(in);
			}
			prev = v.upperBound();
			prevInclusive = !v.isUpperBoundInclusive();
		}
		if(prev != null) {
            Interval<T> in = Interval.valueOf(prev, null, prevInclusive, false, getMc());
			nis.add(in);
		}
        return new IntervalUnion<>(getMc(), nis);
	}
	
	
	/**
	 * @see cn.timelives.java.math.set.MathSet#mapTo(java.util.function.Function, MathCalculator)
	 */
	@NotNull
    @Override
    public <N> IntervalUnion<N> mapTo(@NotNull Function<T, N> mapper, @NotNull MathCalculator<N> newCalculator) {
		return new IntervalUnion<>(newCalculator, CollectionSup.mapList(is, x -> x.mapTo(mapper, newCalculator)));
	}
	
	/**
	 * @see MathObject#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof IntervalUnion)) {
			return false;
		}
		IntervalUnion<?> in = (IntervalUnion<?>) obj;
		return CollectionSup.listEqual(is, in.is, (obj1,obj2)->obj1.equals(obj2));
	}
	/**
	 * @see MathObject#valueEquals(MathObject)
	 */
	@Override
    public boolean valueEquals(@NotNull MathObject<T> obj) {
		if(!(obj instanceof IntervalUnion)) {
			return false;
		}
		IntervalUnion<T> in = (IntervalUnion<T>) obj;
		return CollectionSup.listEqual(is, in.is, (v1,v2)->v1.valueEquals(v2));
	}
	
	/**
	 * @see MathObject#valueEquals(MathObject, java.util.function.Function)
	 */
	@Override
    public <N> boolean valueEquals(@NotNull MathObject<N> obj, @NotNull Function<N, T> mapper) {
		if(!(obj instanceof IntervalUnion)) {
			return false;
		}
		IntervalUnion<N> in = (IntervalUnion<N>) obj;
		return CollectionSup.listEqual(is, in.is, (v1,v2)->v1.valueEquals(v2,mapper));
	}
	/**
	 * @see MathObject#toString(NumberFormatter)
	 */
	@Override
    public String toString(@NotNull FlexibleNumberFormatter<T, MathCalculator<T>> nf) {
		if(is.isEmpty()) {
			return "∅";
		}else {
			StringBuilder sb = new StringBuilder();
			for(Interval<T> in : is){
				sb.append(in.toString(nf));
				sb.append('∪');
			}
			sb.deleteCharAt(sb.length()-1);
			return sb.toString();
		}
	}
	
	
	private static <T> int findSmallerDownerBound0(List<Interval<T>> is,T t,MathCalculator<T> mc) {
		int n = ModelPatterns.binarySearch(0, is.size(), x -> {
			T downer = is.get(x).downerBound();
			if (downer == null) {
				return -1;
			}
			return mc.compare(downer, t);
		});
		if(n<-1) {
			n = -n-2;
		}
		return n;
	}
	
	private static <T> void unionWith0(List<Interval<T>> is,Interval<T> v,MathCalculator<T> mc) {
		if(is.isEmpty()) {
			is.add(v);
			return;
		}
		int downer, upper;
		if(v.downerBound() == null) {
			downer = -1;
		}else {
			downer = findSmallerDownerBound0(is,v.downerBound(),mc);
		}
		if(v.upperBound() == null) {
			upper = is.size()-1;
		}else {
			upper = findSmallerDownerBound0(is,v.upperBound(),mc);
		}
		if(upper == -1) {
			//there is no interval smaller than it, it must be the lower one.
			is.add(0, v);
			return;
		}
//		if(downer==upper) {
//			//the two interval is the identity
//			Interval<T> in = is.get(downer);
//			int t = mc.compare(in.upperBound(), v.downerBound());
//			if(t>=0) {
//				if(in.isUpperBoundInclusive() || v.isDownerBoundInclusive() || t!=0) {
//					//the new interval is the union of the two
//					Interval<T> nin = in.expandUpperBound(v.upperBound(), v.isUpperBoundInclusive());
//					return replaceInterval(downer, nin);
//				}
//				// the case that (a,b) (b,c)
//			}
//			return insertInterval(downer+1, v);
//			
//		}
		T ndowner,nupper;
		boolean ndownerIn,nupperIn;
		int indexDowner,indexUpper;
		if(downer==-1) {
			ndownerIn = v.isDownerBoundInclusive();
			ndowner = v.downerBound();
			indexDowner = 0;
		}else {
			Interval<T> in = is.get(downer);
			final T vdb= v.downerBound();
			if(in.downerBound()!=null && vdb != null && mc.isEqual(in.downerBound(), vdb)) {
				if(shouldUnionWithThePrevInterval0(v, downer,is,mc)) {
					Interval<T> prev = is.get(downer-1);
					indexDowner = downer-1;
					ndownerIn = prev.isDownerBoundInclusive();
					ndowner = prev.downerBound();
				}else {
					ndownerIn = v.isDownerBoundInclusive() || in.isDownerBoundInclusive();
					ndowner = in.downerBound();
					indexDowner = downer;
				}
			}else {
				int t2 = vdb == null ? 1 : in.upperBound() == null ? 1 : mc.compare(in.upperBound(), vdb);
				if(t2>=0 && (t2!=0 || in.isUpperBoundInclusive() || v.isDownerBoundInclusive() )) {
					indexDowner = downer;
					ndownerIn = in.isDownerBoundInclusive();
					ndowner = in.downerBound();
				}else {
					indexDowner = downer+1;
					ndownerIn = v.isDownerBoundInclusive();
					ndowner = v.downerBound();
				}
			}
		}
		if(v.upperBound() == null){
			indexUpper = upper;
			nupper = v.upperBound();
			nupperIn = v.isUpperBoundInclusive();
		}else{
			//deal with upper
			Interval<T> in = is.get(upper);
			final T vub= v.upperBound();
			if(vub!=null && in.downerBound()!=null && mc.isEqual(in.downerBound(), vub)) {
				if(shouldUnionWithTheNextInterval0(v,in)) {
					nupperIn = in.isUpperBoundInclusive();
					nupper = in.upperBound();
					indexUpper = upper;
				}else {
					nupperIn = false;
					nupper = vub;
					indexUpper = upper-1;
				}
			}else {
				int t2 = vub ==null ? -1 : in.upperBound()== null ? 1 : mc.compare(in.upperBound(),vub);
				indexUpper= upper;
				if(t2==0) {
					nupper = in.upperBound();
					nupperIn = in.isUpperBoundInclusive() || v.isUpperBoundInclusive();
				}else if(t2>0) {
					nupper = in.upperBound();
					nupperIn = in.isUpperBoundInclusive();
				}else {
					nupper = v.upperBound();
					nupperIn = v.isUpperBoundInclusive();
				}
			}
		}
		Interval<T> nin = new IntervalI<>(mc, ndowner, nupper, ndownerIn, nupperIn);
		indexUpper++;
		replaceIntervalRange0(indexDowner,indexUpper,nin,is);
	}
	
	private static <T> boolean shouldUnionWithThePrevInterval0(Interval<T> v,int downer,List<Interval<T>> is,MathCalculator<T> mc) {
		if(v.isDownerBoundInclusive() && downer>0) {
			Interval<T> prev = is.get(downer-1);
			if(!prev.isUpperBoundInclusive() && mc.isEqual(v.downerBound(), prev.upperBound())) {
				return true;
			}
		}
		return false;
	}
	
	private static <T> boolean shouldUnionWithTheNextInterval0(Interval<T> v,Interval<T> in) {
		return v.isUpperBoundInclusive() || in.isDownerBoundInclusive();
	}
	/**
	 * Replaces the intervals.
	 * @param downer inclusive
	 * @param upper exclusive
	 * @param v
	 * @return
	 */
	private static <T> void replaceIntervalRange0(int downer,int upper,Interval<T> v,List<Interval<T>> is){
		if(downer==upper) {
			is.add(downer,v);
			return;
		}else if(downer == upper-1) {
			is.set(downer, v);
			return;
		}
		is.set(downer, v);
		for(int i=upper-1;i>downer;i--) {
			is.remove(i);
		}
	}
	/**
	 * Creates an IntervalUnion with a list of intervals. The {@link MathCalculator} will
	 * be taken from the first Interval.
	 * @param intervals
	 * @return a new IntervalUnion
	 */
	public static <T> IntervalUnion<T> valueOf(List<Interval<T>> intervals){
		MathCalculator<T> mc = intervals.get(0).getMathCalculator();
		List<Interval<T>> is = new ArrayList<>(intervals.size());
		for(Interval<T> inv : intervals) {
			unionWith0(is,inv,mc);
		}
		return new IntervalUnion<T>(mc,is);
	}
	/**
	 * Creates an IntervalUnion with an array of intervals. The {@link MathCalculator} will
	 * be taken from the first Interval.
	 * @param interval an interval
	 * @param intervals the remaining
	 * @return a new IntervalUnion
	 */
	@SafeVarargs
	public static <T> IntervalUnion<T> valueOf(Interval<T> interval,Interval<T>...intervals){
		MathCalculator<T> mc = interval.getMathCalculator();
		List<Interval<T>> is = new ArrayList<>(intervals.length);
		is.add(interval);
		for(Interval<T> inv : intervals) {
			unionWith0(is,inv,mc);
		}
		return new IntervalUnion<T>(mc,is);
	}
	/**
	 * Creates an IntervalUnion that only contains one interval.
	 * @param interval an interval
	 * @return a new IntervalUnion
	 */
	public static <T> IntervalUnion<T> valueOf(Interval<T> interval){
		return new IntervalUnion<>(interval.getMathCalculator(), interval);
	}
	
	private static final Map<MathCalculator<?>,IntervalUnion<?>> universemap = new HashMap<>(),
			emptymap = new HashMap<>();
	
	/**
	 * Creates an IntervalUnion that contains no interval. This is equal to 
	 * create an empty set.
	 * @param interval an interval
	 * @return {@literal ∅}
	 */
	public static <T> IntervalUnion<T> empty(MathCalculator<T> mc){
		@SuppressWarnings("unchecked")
		IntervalUnion<T> in = (IntervalUnion<T>) emptymap.get(mc);
		if(in == null) {
			in = new IntervalUnion<>(mc);
			emptymap.put(mc, in);
		}
		return in;
	}
	/**
	 * Creates an IntervalUnion that only contains one interval:(-∞,+∞). This 
	 * is equal to create an universe math set.
	 * @return {@literal (-∞,+∞)}
	 */
	public static <T> IntervalUnion<T> universe(MathCalculator<T> mc){
		@SuppressWarnings("unchecked")
		IntervalUnion<T> in = (IntervalUnion<T>) universemap.get(mc);
		if(in == null) {
			in = new IntervalUnion<>(mc,Interval.universe(mc));
			universemap.put(mc, in);
		}
		return in;
	}
	/**
	 * Creates an IntervalUnion that contains each element from the {@code set}.
	 * @param set an AbstractLimitedSet
	 * @return a new {@link IntervalUnion}
	 */
	public static <T> IntervalUnion<T> fromLimitedSet(AbstractLimitedSet<T> set){
		if(set.size()>Integer.MAX_VALUE) {
			throw new IllegalArgumentException("The size of the set is too big!");
		}
		@SuppressWarnings("unchecked")
		T[] list = (T[]) new Object[(int)set.size()];
		int n=0;
		for(T t : set) {
			list[n++] = t;
		}
		MathCalculator<T> mc = set.getMathCalculator();
		Arrays.sort(list, mc::compare);
		List<Interval<T>> ins = new ArrayList<>(n);
		for(int i=0;i<n;i++) {
			ins.add(Interval.single(list[i],mc));
		}
		return new IntervalUnion<>(mc, ins);
	}
	
	/**
	 * Returns the interval representing a single real number, whose downer bound 
	 * and upper bound are both {@code x}
	 * @param x a number
	 * @param mc a {@link MathCalculator}
	 * @return {@literal [x,x]}
	 */
	public static <T> IntervalUnion<T> single(T x,MathCalculator<T> mc){
		return new IntervalUnion<T>(mc,Interval.single(x, mc));
	}
	/**
	 * Returns the interval representing the real numbers except {@code x}.
	 * @param x a number
	 * @param mc a {@link MathCalculator}
	 * @return {@literal (-∞,x) ∪ (x,+∞)}
	 */
	public static <T> IntervalUnion<T> except(T x,MathCalculator<T> mc){
		List<Interval<T>> list = new ArrayList<>(2);
		list.add(Interval.fromNegativeInf(x, false, mc));
		list.add(Interval.toPositiveInf(x, false, mc));
		return new IntervalUnion<>(mc, list);
	}
	/**
	 * Returns the interval representing the real numbers between {@code x1} and {@code x2},
	 * the order will be adjusted properly. If {@code x1==x2}, then an empty set will be returned.
	 * @param x1 a number
	 * @param x2 another number
	 * @param mc
	 * @return {@literal (x1,x2)} (or maybe {@literal (x2,x1)})
	 */
	public static <T> IntervalUnion<T> between(T x1,T x2,MathCalculator<T> mc){
		if(mc.isEqual(x1, x2)) {
			return empty(mc);
		}
		if(mc.compare(x1, x2)>0) {
			T t = x1;
			x1 = x2;
			x2 = t;
		}
		return new IntervalUnion<>(mc, Interval.openInterval(x1, x2, mc));
	}
}

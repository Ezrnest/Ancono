/**
 * 2018-03-05
 */
package cn.timelives.java.math.abstractAlgebra;

import cn.timelives.java.math.Composable;
import cn.timelives.java.math.Invertible;
import cn.timelives.java.math.abstractAlgebra.calculator.EqualPredicate;
import cn.timelives.java.math.abstractAlgebra.calculator.GroupCalculator;
import cn.timelives.java.math.abstractAlgebra.calculator.SemigroupCalculator;
import cn.timelives.java.math.function.Bijection;
import cn.timelives.java.math.numberModels.MathCalculator;
import cn.timelives.java.math.numberModels.MathCalculatorAdapter;

/**
 * Contains method to construct group calculators.
 * @author liyicheng
 * 2018-03-05 17:52
 *
 */
public final class GroupCalculators {

	/**
	 * 
	 */
	private GroupCalculators() {
	}
	
	/**
	 * Returns a group calculator from a composable and invertible type.
	 * @param id the identity element of type T
	 * @param equalPredicate
	 * @return
	 */
	public static <T extends Composable<T>&Invertible<T>> GroupCalculator<T> createComposing(T id,EqualPredicate<T> equalPredicate){
		return new GroupCalculator<T>() {
			@Override
			public T getIdentity() {
				return id;
			}

			@Override
			public T apply(T x, T y) {
				return x.compose(y);
			}

			@Override
			public boolean isEqual(T x, T y) {
				return equalPredicate.isEqual(x, y);
			}

			@Override
			public T inverse(T x) {
				return x.inverse();
			}
		};
	}
	
	/**
	 * Returns a group calculator from a composable and invertible type. The equal relation in this group is defined by {@link Object#equals(Object)}.
	 * @param id
	 * @return
	 */
	public static <T extends Composable<T>&Invertible<T>> GroupCalculator<T> createComposing(T id){
		return new GroupCalculator<T>() {
			@Override
			public T getIdentity() {
				return id;
			}

			@Override
			public T apply(T x, T y) {
				return x.compose(y);
			}

			@Override
			public boolean isEqual(T x, T y) {
				return x.equals(y);
			}

			@Override
			public T inverse(T x) {
				return x.inverse();
			}
		};
	}
	
	/**
	 * Returns a semigroup calculator from a composable type. The equal relation in this group is defined by {@link Object#equals(Object)}.
	 * @return
	 */
	public static <T extends Composable<T>> SemigroupCalculator<T> createComposingSemi(){
		return new SemigroupCalculator<T>() {

			@Override
			public T apply(T x, T y) {
				return x.compose(y);
			}

			@Override
			public boolean isEqual(T x, T y) {
				return x.equals(y);
			}
		};
	}
	/**
	 * Returns a semigroup calculator from a composable type. 
	 * @return
	 */
	public static <T extends Composable<T>> SemigroupCalculator<T> createComposingSemi(EqualPredicate<T> equalPredicate){
		return new SemigroupCalculator<T>() {

			@Override
			public T apply(T x, T y) {
				return x.compose(y);
			}

			@Override
			public boolean isEqual(T x, T y) {
				return equalPredicate.isEqual(x, y);
			}
		};
	}

	/**
	 * Returns a {@link MathCalculator} from the GroupCalculator, mapping the group's operation to "add" in MathCalculator
	 * @param gc
	 * @return
	 */
	public static <T> MathCalculator<T> toMathCalculatorAdd(GroupCalculator<T> gc){
		return new MathCalculatorAdapter<T>() {
			/*
			 * @see cn.timelives.java.math.numberModels.MathCalculatorAdapter#isEqual(java.lang.Object, java.lang.Object)
			 */
			@Override
			public boolean isEqual(T para1, T para2) {
				return gc.isEqual(para1, para2);
			}
			
			/*
			 * @see cn.timelives.java.math.numberModels.MathCalculatorAdapter#add(java.lang.Object, java.lang.Object)
			 */
			@Override
			public T add(T para1, T para2) {
				return gc.apply(para1, para2);
			}
			
			/*
			 * @see cn.timelives.java.math.numberModels.MathCalculatorAdapter#negate(java.lang.Object)
			 */
			@Override
			public T negate(T para) {
				return gc.inverse(para);
			}
			
			/*
			 * @see cn.timelives.java.math.numberModels.MathCalculatorAdapter#getZero()
			 */
			@Override
			public T getZero() {
				return gc.getIdentity();
			}
			
			/*
			 * @see cn.timelives.java.math.numberModels.MathCalculatorAdapter#multiplyLong(java.lang.Object, long)
			 */
			@Override
			public T multiplyLong(T p, long l) {
				return gc.gpow(p, l);
			}
		};
	}
	
	
	
	/**
	 * Returns a isomorphism calculator of the original calculator through bijection {@code f}.
	 * @param gc
	 * @param f
	 * @return
	 */
	public static <T,S> GroupCalculator<S> isomorphism(GroupCalculator<T> gc,Bijection<T,S> f){
		return new IsoGC<>(gc, f);
	}
	
	
	
	static class IsoGC<T,S> implements GroupCalculator<S>{
		final GroupCalculator<T> origin;
		final Bijection<T, S> f;
		final S id;
		/**
		 * 
		 */
		IsoGC(GroupCalculator<T> origin,Bijection<T, S> f) {
			this.f = f;
			this.origin = origin;
			id = f.apply(origin.getIdentity());
		}
		
		
		/*
		 * @see cn.timelives.java.math.abstractAlgebra.calculator.MonoidCalculator#getIdentity()
		 */
		@Override
		public S getIdentity() {
			return id;
		}

		/*
		 * @see cn.timelives.java.math.abstractAlgebra.calculator.SemigroupCalculator#apply(java.lang.Object, java.lang.Object)
		 */
		@Override
		public S apply(S x, S y) {
			return f.apply(origin.apply(f.deply(x), f.deply(y)));
		}

		/*
		 * @see cn.timelives.java.math.abstractAlgebra.calculator.SemigroupCalculator#isEqual(java.lang.Object, java.lang.Object)
		 */
		@Override
		public boolean isEqual(S x, S y) {
			return origin.isEqual(f.deply(x), f.deply(y));
		}

		/*
		 * @see cn.timelives.java.math.abstractAlgebra.calculator.GroupCalculator#inverse(java.lang.Object)
		 */
		@Override
		public S inverse(S x) {
			return f.apply(origin.inverse(f.deply(x)));
		}
		
		/*
		 * @see cn.timelives.java.math.abstractAlgebra.calculator.GroupCalculator#gpow(java.lang.Object, long)
		 */
		@Override
		public S gpow(S x, long n) {
			T y = f.deply(x);
			T re = origin.gpow(y, n);
			return f.apply(re);
		}
	}

}

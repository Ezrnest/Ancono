/**
 * 2018-03-05
 */
package cn.timelives.java.math.algebra.abstractAlgebra;

import cn.timelives.java.math.algebra.abstractAlgebra.calculator.*;
import cn.timelives.java.math.algebra.abstractAlgebra.structure.DivisionRing;
import cn.timelives.java.math.property.Composable;
import cn.timelives.java.math.property.Invertible;
import cn.timelives.java.math.function.Bijection;
import cn.timelives.java.math.MathCalculator;
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
		return new GroupCalculator<>() {
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
		return new GroupCalculator<>() {
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
		return new MathCalculatorAdapter<>() {
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
	 * Returns a {@link MathCalculator} from the EqualPredicate, which only supports {@code isEqual(Object,Object)} method.
	 * @param gc
	 * @return
	 */
	public static <T> MathCalculator<T> toMathCalculatorEqual(EqualPredicate<T> gc){
		if(gc instanceof MathCalculator){
			return (MathCalculator<T>)gc;
		}
		return new MathCalculatorAdapter<>() {
			/*
			 * @see cn.timelives.java.math.numberModels.MathCalculatorAdapter#isEqual(java.lang.Object, java.lang.Object)
			 */
			@Override
			public boolean isEqual(T para1, T para2) {
				return gc.isEqual(para1, para2);
			}
		};
	}

    /**
     * Returns a {@link MathCalculator} from the RingCalculator, mapping add, subtract, multiply.
     * @param rc
     * @param <T>
     * @return
     */
	public static <T> MathCalculator<T> toMathCalculatorRing(RingCalculator<T> rc){
	    return new MathCalculatorAdapter<>() {
            @Override
            public boolean isEqual(T para1, T para2) {
                return rc.isEqual(para1, para2);
            }

            @Override
            public T add(T para1, T para2) {
                return rc.add(para1, para2);
            }

            @Override
            public T negate(T para) {
                return rc.negate(para);
            }

            @Override
            public T subtract(T para1, T para2) {
                return rc.subtract(para1, para2);
            }

            @Override
            public T multiply(T para1, T para2) {
                return rc.multiply(para1, para2);
            }

            @Override
            public T multiplyLong(T p, long l) {
                return rc.multiplyLong(p, l);
            }

            @Override
            public T getZero() {
                return rc.getZero();
            }

            @Override
            public T pow(T p, long exp) {
                return rc.pow(p,exp);
            }
        };
    }

    /**
     * Returns a {@link MathCalculator} from the RingCalculator, mapping add, subtract, multiply.
     * @param fc
     * @param <T>
     * @return
     */
    public static <T> MathCalculator<T> toMathCalculatorDR(DivisionRingCalculator<T> fc){
        return new MathCalculatorAdapter<>() {
            @Override
            public boolean isEqual(T para1, T para2) {
                return fc.isEqual(para1, para2);
            }

            @Override
            public T add(T para1, T para2) {
                return fc.add(para1, para2);
            }

            @Override
            public T negate(T para) {
                return fc.negate(para);
            }

            @Override
            public T subtract(T para1, T para2) {
                return fc.subtract(para1, para2);
            }

            @Override
            public T multiply(T para1, T para2) {
                return fc.multiply(para1, para2);
            }

            @Override
            public T divide(T para1, T para2) {
                return fc.divide(para1, para2);
            }

            @Override
            public T divideLong(T p, long l) {
                return fc.divideLong(p,l);
            }

            @Override
            public T multiplyLong(T p, long l) {
                return fc.multiplyLong(p, l);
            }

            @Override
            public T getOne() {
                return fc.getOne();
            }

            @Override
            public T getZero() {
                return fc.getZero();
            }

            @Override
            public T reciprocal(T p) {
                return fc.reciprocal(p);
            }

            @Override
            public T pow(T p, long exp) {
                return fc.pow(p, exp);
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
		 * @see cn.timelives.java.math.algebra.abstractAlgebra.calculator.MonoidCalculator#getIdentity()
		 */
		@Override
		public S getIdentity() {
			return id;
		}

		/*
		 * @see cn.timelives.java.math.algebra.abstractAlgebra.calculator.SemigroupCalculator#apply(java.lang.Object, java.lang.Object)
		 */
		@Override
		public S apply(S x, S y) {
			return f.apply(origin.apply(f.deply(x), f.deply(y)));
		}

		/*
		 * @see cn.timelives.java.math.algebra.abstractAlgebra.calculator.SemigroupCalculator#isEqual(java.lang.Object, java.lang.Object)
		 */
		@Override
		public boolean isEqual(S x, S y) {
			return origin.isEqual(f.deply(x), f.deply(y));
		}

		/*
		 * @see cn.timelives.java.math.algebra.abstractAlgebra.calculator.GroupCalculator#inverse(java.lang.Object)
		 */
		@Override
		public S inverse(S x) {
			return f.apply(origin.inverse(f.deply(x)));
		}
		
		/*
		 * @see cn.timelives.java.math.algebra.abstractAlgebra.calculator.GroupCalculator#gpow(java.lang.Object, long)
		 */
		@Override
		public S gpow(S x, long n) {
			T y = f.deply(x);
			T re = origin.gpow(y, n);
			return f.apply(re);
		}
	}

}

/**
 * 2017-10-09
 */
package cn.timelives.java.math.equation;

import cn.timelives.java.math.MathCalculator;

/**
 * An enumeration that describes all the types of a compare structure.
 * <ul>
 * <li>Greater
 * <li>Greater or Equal
 * <li>Less
 * <li>Less or Equal
 * <li>Equal
 * <li>Not Equal
 * </ul>
 * @author liyicheng
 * 2017-10-06 19:09
 * @see CompareStructure
 */
public enum Type{
	GREATER(">") {
		@Override
		public boolean matches(int signum) {
			return signum>0;
		}

		@Override
		public Type complement() {
			return LESS_OR_EQUAL;
		}

		@Override
		public Type negative() {
			return LESS;
		}
	},
	GREATER_OR_EQUAL(">=") {
		@Override
		public boolean matches(int signum) {
			return signum>=0;
		}

		@Override
		public Type complement() {
			return LESS;
		}

		@Override
		public Type negative() {
			return LESS_OR_EQUAL;
		}
	},
	LESS("<") {
		@Override
		public boolean matches(int signum) {
			return signum<0;
		}

		@Override
		public Type complement() {
			return GREATER_OR_EQUAL;
		}

		@Override
		public Type negative() {
			return GREATER;
		}
	},
	LESS_OR_EQUAL("<=") {
		@Override
		public boolean matches(int signum) {
			return signum<=0;
		}

		@Override
		public Type complement() {
			return GREATER;
		}

		@Override
		public Type negative() {
			return GREATER_OR_EQUAL;
		}
	},
	EQUAL("=") {
		@Override
		public boolean matches(int signum) {
			return signum == 0;
		}

		@Override
		public Type complement() {
			return NOT_EQUAL;
		}

		@Override
		public Type negative() {
			return EQUAL;
		}
	},
	NOT_EQUAL("!="){
		/*
		 * @see cn.timelives.java.math.Inequation.Type#matches(int)
		 */
		@Override
		public boolean matches(int signum) {
			return signum!=0;
		}

		@Override
		public Type complement() {
			return EQUAL;
		}

		@Override
		public Type negative() {
			return NOT_EQUAL;
		}
	};
	private final String operation;
	
	private Type(String op){
		this.operation = op;
	}
	
	/**
	 * Determines whether the {@code signum}, which is often the 
	 * result of {@link MathCalculator#compare(Object, Object)}, 
	 * matches the inequation operation type.
	 * @param signum
	 * @return
	 */
	public abstract boolean matches(int signum);
	
	/**
	 * Returns the complement of the operation. For example, 
	 * the complement of {@link #GREATER} is {@link #LESS_OR_EQUAL}.
	 * @return
	 */
	public abstract Type complement();
	
	/**
	 * Returns the operation that returns the same result when a negative sign number 
	 * is given, in other words, {@code this.matches(x) == this.negative().matches(-x)}.
	 * For example, the negative of {@link #GREATER_OR_EQUAL} is {@link #LESS_OR_EQUAL}.
	 * @return
	 */
	public abstract Type negative();
	
	/**
	 * Gets the String representing this operation. 
	 */
	public String toString() {
		return operation;
	}
	
	/**
	 * Determines whether the type is {@link #EQUAL}, {@link #GREATER_OR_EQUAL} or {@link #LESS_OR_EQUAL}.
	 * @param t an operator type
	 * @return {@code true} if it is.
	 */
	public static boolean hasEqual(Type t) {
		return t == EQUAL || t == GREATER_OR_EQUAL || t == Type.LESS_OR_EQUAL;
	}
	
}
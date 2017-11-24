package cn.timelives.java.math.numberModels;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.timelives.java.math.addableSet.AdditiveSet;
import cn.timelives.java.math.addableSet.MathAddableSet;
import cn.timelives.java.math.addableSet.MathAdder;
import cn.timelives.java.math.addableSet.SortedAdditiveSet;

/**
 * 多项式是由多个formula组成的表达式
 * <p>
 * 每个formula之间以加号相连
 * 
 * 
 * 
 * 
 * 
 * @author lyc
 *
 */
public class Polynomial implements Comparable<Polynomial> {
	/**
	 * fs 是用于储存多项式内容的SortedAdditiveSet
	 */
	private SortedAdditiveSet<Formula> fs;

	private static final FormulaCalculator fc = FormulaCalculator.DEFAULT_FORMULA_CALCULATOR;

	/**
	 * this is a Polynomial which value is one and it doesn't have any
	 * MathCalculator because it is special __
	 */
	public static final Polynomial ONE = new Polynomial(fc, Formula.ONE);
	public static final Polynomial ZERO = new Polynomial(fc, Formula.ZERO);

	/**
	 * 要建立Polynomial对象，必须指定一个计算器对象
	 * 
	 * @param ma
	 */
	public Polynomial(MathAdder<Formula> ma, Formula[] fs) {
		this.fs = new SortedAdditiveSet<Formula>(ma);
		for (Formula f : fs) {
			this.fs.add(f);
		}
		this.fs.add(Formula.ZERO);
	}

	public Polynomial(MathAdder<Formula> ma, Formula f) {
		this.fs = new SortedAdditiveSet<Formula>(ma);
		this.fs.add(f);
	}

	public Polynomial(MathAdder<Formula> ma) {
		this.fs = new SortedAdditiveSet<Formula>(ma);
		this.fs.add(Formula.ZERO);
	}

	public Polynomial(MathAddableSet<Formula> fs) {
		this.fs = new SortedAdditiveSet<Formula>(fs.getAdder());
		for (Formula f : fs) {
			this.fs.add(f);
		}
		this.fs.add(Formula.ZERO);
	}

	/**
	 * 根据字符串表达式来建立的Polynomial
	 * 
	 * @param ma
	 * @param str
	 */
	public Polynomial(MathAdder<Formula> ma, String expression) {
		this.fs = new SortedAdditiveSet<Formula>(ma);
		if (expression.trim().isEmpty()) {
			return;
		}
		char c;
		String temp = "";
		for (int i = 0; i < expression.length(); i++) {
			c = expression.charAt(i);
			if (c == '+' || c == '-') {

				if (i != 0) {
					char cr = expression.charAt(i - 1);
					if (cr != '+' && cr != '-' && cr != '^') {
						fs.add(Formula.valueOf(temp));
						temp = "";
					}
				}
			}
			temp += c;
		}
		fs.add(Formula.valueOf(temp));
		this.fs.add(Formula.ZERO);
	}

	public Polynomial(MathAdder<Formula> ma, int value) {
		this(ma, Formula.valueOf(BigInteger.valueOf(value)));
	}

	/**
	 * the method will return a String that expression this polynomial
	 * <p>
	 * each Formula will connect with {@code -} or {@code +}
	 * <p>
	 * notice that the first Formula will not have {@code +} despite it is positive
	 * 
	 * @see Formula,Formula.toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		boolean isFirst = true;
		for (Formula f : fs) {
			// if(!f.equalsIgnoreDecimal(Formula.ZERO))
			sb.append(f.toString());
			if (isFirst && sb.charAt(0) == '+') {
				sb.deleteCharAt(0);
				isFirst = false;
			}
		}
		return sb.toString();
	}

	public int getNumOfFormula() {
		return fs.size();
	}

	SortedAdditiveSet<Formula> getFormulas() {
		return fs;
	}

	public List<Formula> getFormulaList() {
		return new ArrayList<Formula>(fs);
	}

	/**
	 * 在该多项式中添加一项
	 * 
	 * @param f
	 * @return
	 */
	boolean addFormula(Formula f) {
		return fs.add(f);
	}

	@Override
	public Polynomial clone() {
		return new Polynomial(fs);
	}

	public boolean equals(Polynomial p) {
		return this.fs.equals(p.fs);
	}

	private int hashCode = 0;

	@Override
	public int hashCode() {
		if (hashCode == 0) {
			int hash = 0;
			for (Formula f : this.fs) {
				hash += f.hashCode() * 31;
			}
			hashCode = hash;
		}
		return hashCode;
	}

	@Override
	public int compareTo(Polynomial o) {
		SortedAdditiveSet<Formula> sas1 = this.fs;
		SortedAdditiveSet<Formula> sas2 = o.fs;
		int re = sas1.size() - sas2.size();
		if (re == 0) {
			Iterator<Formula> i1 = sas1.iterator();
			Iterator<Formula> i2 = sas2.iterator();
			for (int i = 0; i < this.fs.size(); i++) {
				re = i1.next().compareTo(i2.next());
				if (re != 0)
					return re;
			}
		}
		return re;
	}

	public void removeZero() {
		if (fs.size() > 1) {
			fs.remove(Formula.ZERO);
		}
	}

	public static MathCalculator<Polynomial> getCalculator() {
		return PolyCalculator.DEFALUT_CALCULATOR;
	}

	/**
	 * Creates a new Polynomial using the default formula calculator.
	 * 
	 * @param expr
	 * @return
	 */
	public static Polynomial valueOf(String expr) {
		return new Polynomial(fc, expr);
	}
}

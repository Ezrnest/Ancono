package cn.timelives.java.math.numberModels;

import cn.timelives.java.utilities.CollectionSup;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Version 1.1
 * <p>
 * Formula类表达一个简单的代数式，所有元素之间以乘法相连，不含有加法，例如 "123a","2e*Pi*a",...
 * 其中小写e,大写Pi表示常数e，π 代数式的常数部分有两种表达方式，分别是代数表示和小数表示。
 * 当代数式接受到：1.含有不可以表示为简单根式(如根号2，根号10)的无理数 2.含有超过MAX_NUM的数字或者为根式中的数大于MAX_RAD
 * 会将数据表现为科学计数法形式，输出时只保留一定的有效数字。
 * 当代数式接收到位数小于{@code MAX_FRACTION_DIGITAL}的小数，可以化成分数
 * 
 * 
 * 
 * 
 * 
 * --------
 * <p>
 * 任何表达式都有用于记录字母的{@code character},{@code numOfChar},可以通过get方法分别获取
 * <p>
 * {@code numOfChar}=0表示没有字母系数,此时{@code character}是一个空的集合
 * 
 * <p>
 * 以{@code decimal}区分改式子是否以小数表示，{@code decimal}=true：用小数表示，
 * *默认情况下{@code decimal}=false
 * <p>
 * 当以小数表示时，表达式只有以下的变量有意义：{@code number},{@code character},{@code numOfChar}
 * <p>
 * 当以分数表示时，表达式只有以下的变量有意义：{@code positive,numerator,denominator,radical,character,numOfChar,number}
 * 
 * <p>
 * {@code number} 为表达式常系数的值，在分数表示时并不会真正参与计算，但可以作为表达式的值来表示
 * 
 * <p>
 * 版本更新记录：
 * <p>
 * 1.1:将{@code characterT}移除,字母系数用{@code HashMap<String,BigDecimal> character}记录
 * 
 * <P>
 * 1.2:将所有的{@code numerator}{@code denominator}{@code radical}都从int类型替换为BigInteger
 * 
 * <P>
 * 1.3:将Formula变为不可变对象
 * 
 * @author lyc
 * @see PolynomialOld ExpressionTree Calculator
 */
@Deprecated
public class Formula implements Comparable<Formula> {
	/**
	 * positive 表示表达式的正负 decimal 表示表达式是否为分数
	 * 
	 */
	private final boolean decimal = false;
	private final int signum;
	/**
	 * numerator 分子 denominator 分母 radical 根号内的数
	 * 
	 */
	private final BigInteger numerator, denominator, // 分母不为0
			radical;

	// private int logarithm=0;
	/**
	 * number 表达式值
	 */
	private BigDecimal number;
	/**
	 * character 表达式的字母系数 按照特定的顺序排列
	 */
	private final HashMap<String, BigDecimal> character;

	private int hashCode;
	/**
	 * 支持表示在分数中的最大位数，就是说，如果分数中出现了大于这个位数的分子或分母，就会化成小数
	 */
	private static int MAX_FRACTION_DIGIT = 32;// you can set these number
	/**
	 * the max digit
	 */
	private static int MAX_SHOWN_DIGIT = 9;
	private static String DECIMAL_PATTERN = "0.########E0";
	private static DecimalFormat DECIMAL_FORMAT = new DecimalFormat(DECIMAL_PATTERN);

	/*
	 * 存储常用的Formula对象
	 */
	/**
	 * ZERO : a formula that isn't decimal having 0 as numerator,radical,number and
	 * 1 as denominator with no character
	 */
	public static final Formula ZERO = new Formula(0, BigInteger.ZERO, BigInteger.ONE, BigInteger.ZERO,
			new HashMap<String, BigDecimal>());
	/**
	 * ONE : a formula that isn't decimal having 1 as numerator,radical,number and 1
	 * as denominator with no character
	 */
	public static final Formula ONE = new Formula(1, BigInteger.ONE, BigInteger.ONE, BigInteger.ONE,
			new HashMap<String, BigDecimal>());
	/**
	 * The character with special meaning should be constant
	 * 
	 */
	public static final String PI_STR = "Pi";
	public static final String E_STR = "e";
	public static final String I_STR = "i";
	public static String SQR_STR = "Sqr";

	public static String PI_SHOW = "π";
	public static String SQR_SHOW = "Sqr";// √

	public static final Formula PI;
	static {
		HashMap<String, BigDecimal> h = new HashMap<String, BigDecimal>();
		h.put(PI_STR, BigDecimal.ONE);
		PI = new Formula(1, BigInteger.ONE, BigInteger.ONE, BigInteger.ONE, h);
	}

	public static Comparator<Formula> getNormalOrder(){
		return normalOrder;
	}

	private static final Comparator<Formula> normalOrder = getNormalOrder0();
	static Comparator<Formula> getNormalOrder0(){
		return (fA,formula)->{
			int result = 0;// the result will be 0 at first and waited to be check by following ways
			TreeSet<String> aChar = new TreeSet<String>(fA.character.keySet());
			TreeSet<String> bChar = new TreeSet<String>(formula.character.keySet());
			aChar.remove(E_STR);
			aChar.remove(I_STR);
			aChar.remove(PI_STR);
			bChar.remove(E_STR);
			bChar.remove(I_STR);
			bChar.remove(PI_STR);
			if (aChar.size() == 0) {
				if (bChar.size() != 0) {
					return 1;// fA is the case that A doesn't have any other character while B does.A
					// should be in front of B
				}
				result = 0;// the situation will be considered after the part dealing with character
			} else if (bChar.size() == 0 && aChar.size() != 0) {
				return -1;// fA is the case that B doesn't have any other character while A does.B
				// should be in front of A
			} else {
				// fA is the situation that both A and B have character except i,e,Pi
				Iterator<String> ia = aChar.iterator();
				Iterator<String> ib = bChar.iterator();
				while (ia.hasNext()) {
					// deal with A first
					if (ib.hasNext()) {
						String ta = ia.next();
						String tb = ib.next();
						result = ta.compareTo(tb);// compare the character in the first or second
						if (result == 0) {
							result = formula.character.get(tb).compareTo(fA.character.get(ta));// fA step will compare
							// the character
							if (result != 0) {
								return result;
							}
						} else {
							return result;
						}
					} else {
						// result=-1;it is the situation that A has character while B doesn't.See the
						// Third rule.
						return -1;
					}
				}
				if (ib.hasNext()) {
					return 1;// result=1;it is the situation that B has character while A doesn't.See the
					// Third rule
				}
			}
			// now let's deal with i,e,Pi
			if (fA.character.containsKey(I_STR)) {
				if (false == formula.character.containsKey(I_STR)) {
					return 1;// fA is the situation that A has "i" while B doesn't
				}
			} else if (formula.character.containsKey(I_STR)) {
				if (false == fA.character.containsKey(I_STR)) {
					return -1;// fA is the situation that B has "i" while A doesn't
				}
			}
			if (fA.character.containsKey(E_STR)) {
				if (formula.character.containsKey(E_STR)) {
					result = formula.character.get(E_STR).compareTo(fA.character.get(E_STR));
					if (result != 0) {
						return result;
					}
				} else {
					return -1;// fA is the situation that A has "e" while B doesn't
				}
			} else if (formula.character.containsKey(E_STR)) {
				if (false == fA.character.containsKey(E_STR)) {
					return 1;// fA is the situation that B has "e" while A doesn't
				}
			}
			if (fA.character.containsKey(PI_STR)) {
				if (formula.character.containsKey(PI_STR)) {
					result = formula.character.get(PI_STR).compareTo(fA.character.get(PI_STR));
					if (result != 0) {
						return result;
					}
				} else {
					return -1;// fA is the situation that A has "Pi" while B doesn't
				}
			} else if (formula.character.containsKey(PI_STR)) {
				if (false == fA.character.containsKey(PI_STR)) {
					return 1;// fA is the situation that B has "Pi" while A doesn't
				}
			}
			// finally,let's deal with the number part
			if (fA.decimal) {
				if (formula.decimal) {
					return fA.getNumber().compareTo(formula.getNumber());// compare the number if both of them are decimal
				} else {
					return 1;// fA is the situation that A is decimal while B isn't.A should be after B
				}
			} else {
				if (formula.decimal) {
					return -1;// fA is the situation that B is decimal while A isn't.A should be in front of
					// B
				}
			}
			// both of them are not decimal
			result = fA.radical.compareTo(formula.radical);
			if (result == 0) {
				result = fA.getNumber().compareTo(formula.getNumber());
				if (result == 0) {// fA shouldn't happen because the two formula strictly can be added
					if (fA.signum > 0) {
						if (formula.signum < 0) {
							return 1;
						}
					} else {
						if (formula.signum > 0) {
							return -1;
						}
					}
				}
			}
			return result;// fA is rarely possible.....
		};
	}

	private Formula(int signum, BigInteger[] ndr, HashMap<String, BigDecimal> character) {
		this(signum, ndr[0], ndr[1], ndr[2], character);
	}

	private Formula(int signum, BigInteger numerator, BigInteger denominator, BigInteger radical,
			HashMap<String, BigDecimal> character) {
		this.signum = signum;
		this.numerator = numerator;
		this.denominator = denominator;
		this.radical = radical;

		this.character = character;
		// calculate the hashCode of this Formula

		hashCode = numerator.hashCode();
		hashCode = hashCode * 31 + denominator.hashCode();
		hashCode = hashCode * 31 + radical.hashCode();
		hashCode = hashCode * 31 + character.hashCode();
		hashCode = hashCode * signum;
	}

	/*
	 * readChar读取一串字符串，将其整理归纳成为formula的字母系数 由于方法较为低级，只能读取普通的表达式，例如a1b2,abcd,
	 * 不能读取类似于aaa的表达式 但是支持e与P代表常量e与Pi
	 */

	/*
	 * 显示代数式,若d为false且代数式为分数表示，则显示分数形式 若d为true则直接显示小数
	 */
	public void printFormula(boolean decimal) {
		System.out.println(this.toString());
	}

	public void printFormula() {
		printFormula(false);
	}

	public void printCharacter() {
		System.out.print(this.getCharacterStr());

		// for(int i=0;i<numOfChar;i++){
		// System.out.print(character[i]);
		// if(characterT[i].compareTo(BigDecimal.ONE)!=0){
		// System.out.print("^"+characterT[i].toString());
		// if(i!=numOfChar-1){
		// System.out.print("*");
		// }
		// }
		// }
	}

	public int getSignum() {
		return signum;
	}

	public boolean isDecimal() {
		return decimal;
	}

	public BigInteger getNumerator() {
		return numerator;
	}

	public BigInteger getDenominator() {
		return denominator;
	}

	public BigInteger getRadical() {
		return radical;
	}

	public BigDecimal getNumber() {
		if (number == null) {
			BigDecimal t = new BigDecimal(numerator).divide(new BigDecimal(denominator), MathContext.DECIMAL128);
			if (signum < 0) {
				t = t.negate();
			}
			if (radical.compareTo(BigInteger.ONE) == 0) {
				this.number = t;
			} else {
				this.number = t.multiply(new BigDecimal(Math.sqrt(radical.doubleValue())));
			}
		}
		return number;
	}

	/**
	 * the method will return a copy of this formula's character
	 * <p>
	 * notice that the HashMap returned is a shallow copy ,but it doesn't matter
	 * because String and BigDecimal stored in the HashMap is final
	 * 
	 * @return
	 */
	public HashMap<String, BigDecimal> getCharacter() {
		return new HashMap<String, BigDecimal>(character);
	}

	/**
	 * return this.character
	 * <p>
	 * notice the change in the returned map will be shown in this formula too
	 * 
	 * @return
	 */
	final HashMap<String, BigDecimal> getCharacterS() {
		return this.character;
	}
	
	
	/**
	 * Gets the power of the character, if the character is not contained 
	 * in this formula, then zero will be returned.
	 * @param cha
	 * @return
	 */
	public BigDecimal getCharacterPower(String cha) {
		BigDecimal pow =  character.get(cha);
		if(pow == null) {
			return BigDecimal.ZERO;
		}
		return pow;
	}

	/**
	 * getCharStr方法返回的是简单的字母表示,可以用来比较两个表达式的字母是否相等
	 * 
	 * @return
	 */
	public String getCharStr() {
		StringBuilder str = new StringBuilder("");
		TreeSet<String> character = new TreeSet<String>(this.character.keySet());
		// for(int i=0;i<numOfChar;i++){
		// str+=character[i];
		// if(characterT[i].intValue()!=1){
		// str+=characterT[i].toString();
		// }
		// }
		for (String t : character) {
			str.append(t + this.character.get(t));
		}
		return str.toString();
	}

	/**
	 * getCharacterStr function:
	 * <p>
	 * it will return a string that express the character and their power (like a*b
	 * ,a^2*bcd,...)
	 * 
	 * @return a string that express the character and their power (like a*b
	 *         ,a^2*bcd,...)
	 */
	public String getCharacterStr() {
		StringBuilder result = new StringBuilder("");
		getCharacterStr(result);
		return result.toString();
	}

	private void getCharacterStr(StringBuilder result) {
		if (character.size() == 0) {
			return;
		}
		TreeSet<String> character = new TreeSet<String>(this.character.keySet());// this.character.keySet()
		if (this.character.containsKey(I_STR)) {
			this.getOneChar(I_STR, result);
		}
		if (this.character.containsKey(E_STR)) {
			// System.out.print(E_STR);
			this.getOneChar(E_STR, result);
		}
		if (this.character.containsKey(PI_STR)) {
			// System.out.print(PI_STR);
			result.append(PI_SHOW);
			if (this.character.get(PI_STR).compareTo(BigDecimal.ONE) != 0) {
				// System.out.print("^"+this.character.get(s).toString());
				result.append("^" + this.character.get(PI_STR).toString());
				result.append("*");
			}
		}
		for (String s : character) {
			if (!(s.equals(I_STR) || s.equals(E_STR) || s.equals(PI_STR))) {
				this.getOneChar(s, result);
			}

		}
		if (result.charAt(result.length() - 1) == '*') {
			result.deleteCharAt(result.length() - 1);
		}
		return;
	}

	private void getOneChar(String s, StringBuilder result) {
		// System.out.print(s);
		result.append(s);
		if (this.character.get(s).compareTo(BigDecimal.ONE) != 0) {
			// System.out.print("^"+this.character.get(s).toString());
			result.append("^" + this.character.get(s).toPlainString());
			result.append("*");

		}
		return;
	}

	public int getNumOfChar() {
		return character.size();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Formula) {// class 需要相等
			Formula f = (Formula) obj;
			if (this.haveSameChar(f)) {// 首先字母系数需要相等
				if (f.decimal == this.decimal) {// 小数表示与分数表示不可以相等
					if (f.decimal) {
						if (f.getNumber().equals(this.getNumber())) {
							return true;// 在小数状态数字相等返回true
						}
					} else {
						if (f.signum == this.signum && f.numerator.compareTo(this.numerator) == 0) {
							if (f.denominator.compareTo(this.denominator) == 0
									&& f.radical.compareTo(this.radical) == 0) {
								return true;// 仅当分子，分母，根式都相等时才相等
							}
						}
					}
				}
			}
		}
		return false;// 都不满足返回 false
	}

	/**
	 * Determines whether the two formulas' absolute value is equal.
	 * @param f
	 * @return
	 */
	public boolean absEquals(Formula f) {
		if (this.haveSameChar(f)) {// 首先字母系数需要相等
			if (f.numerator.compareTo(this.numerator) == 0 && f.denominator.compareTo(this.denominator) == 0
					&& f.radical.compareTo(this.radical) == 0) {
				return true;
			}
		}
		return false;
	}

	public boolean equalsIgnoreDecimal(Formula f) {
		if (this.haveSameChar(f) && this.getNumber().compareTo(f.getNumber()) == 0) {// 首先字母系数需要相等
			return true;
		}
		return false;// 都不满足返回 false
	}

	/**
	 * toString function
	 * <p>
	 * 
	 * 
	 * 
	 */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();

		String nu = this.numerator.toString();
		String de = this.denominator.toString();

		boolean toFraction = nu.length() < MAX_FRACTION_DIGIT && de.length() < MAX_FRACTION_DIGIT;
		boolean putNu = numerator.compareTo(BigInteger.ONE) != 0 || (radical.compareTo(BigInteger.ONE) == 0
				&& (denominator.compareTo(BigInteger.ONE) != 0 || character.size() == 0));

		if (false == this.decimal && toFraction) {
			if (numerator.compareTo(BigInteger.ZERO) == 0) {
				return "+0";
			}
			if (signum >= 0) {
				result.append("+");
			} else {
				result.append("-");
			}
			if (putNu) {

				result.append(nu);
			}
			if (radical.compareTo(BigInteger.ONE) == 0) {
				if (denominator.compareTo(BigInteger.ONE) != 0) {
					result.append("/");
					result.append(de);
				}
			} else {
				result.append(SQR_SHOW);
				result.append("(");
				result.append(radical);
				result.append(')');
				if (denominator.compareTo(BigInteger.ONE) != 0) {
					result.append("/");
					result.append(de);
				}
			}
		} else {
			String num = getNumber().toEngineeringString();
			if (num.length() - 1 > MAX_SHOWN_DIGIT) {
				result.append(DECIMAL_FORMAT.format(number));
			} else {
				result.append(number.toString());
			}
		}
		if (character.size() > 0) {
			if (decimal || putNu) {
				result.append("*");
			}
			this.getCharacterStr(result);
		}
		return result.toString();
	}

	/**
	 * this method is like toString() but you can tell it whether the expression
	 * will be shown as fraction or decimal
	 * 
	 * @return a String represent the formula
	 */
	public String getExpression(boolean decimal) {
		return null;

	}

	/**
	 * compareTo function:
	 * <p>
	 * It will make a comparison between two Formulas
	 * <p>
	 * rules:
	 * <ul>
	 * <li><b>First</b> :If A and B neither have character or A and B have the same
	 * character.
	 * <ul>
	 * <li><b>#1.</b>If A isn't decimal while B is decimal,then A should be in front
	 * of B(return -1)
	 * <li><b>#2.</b>If A and B are both decimal,then return -1,0,or 1 if
	 * <i>A.number</i> is numerically less than, equal to, or greater than
	 * <i>B.number</i> (This shouldn't happen when a calculator works,because A and
	 * B can be added.)
	 * <li><b>#3.</b>If A and B are neither decimal,then it will compare the
	 * <i>radical</i> of A and B. If their radicals are the same,then it will
	 * compare the <i>number</i> of A and B (In principle,two Formula having the
	 * same radical and character(or no character) should be added when calculating
	 * that it is not necessary to compare them.
	 * </ul>
	 * <li><b>Second</b>: If A and B both only have "i,e,Pi" as their character or
	 * have no character
	 * <ul>
	 * <li><b>#1.</b>If A doesn't have character and B does,it will return -1(A
	 * should be in front of B)
	 * <li><b>#2.</b>If A have I_STR as character and B doesn't,then it will return
	 * 1(A should be after B)
	 * <li><b>#3.</b>If A and B both have I_STR or both not,it will compare the
	 * power of E_STR,after it will be PI_STR. The one with larger power of the
	 * character should be in front of the other. If the power of E_STR and PI_STR
	 * are both the same,it will compare the remaining part of A and B by using the
	 * <b>First</b> rule.
	 * </ul>
	 * <li><b>Third</b>:If A has character except "i,e,Pi" but B doesn't,A should be
	 * in front of B(return -1)
	 * 
	 * <li><b>Fourth</b> :If A and B both have character except "i,e,Pi",then it
	 * will compare them by using the natural order of character and their power.The
	 * one having larger character power will be in front of the other.If they have
	 * the same character, it will then compare them by using the <b>First</b> rule.
	 * <li>
	 * <p>
	 * <b>Final</b> :If A and B are normal Formulas,it will compare the character
	 * part not having "i,e,Pi" by using the <b>Fourth</b> rule. Then compare their
	 * remaining part by using the <b>Second</b> rule or the <b>First</b> rule.
	 * </ul>
	 * <p>
	 * Generally speaking,the function will compare A and B following natural
	 * order,and make the formulas more easy to organize and recognize.
	 * 
	 * <p>
	 * Examples: Here are some sorted Expression whose order is the order given by
	 * this method.
	 * <ul>
	 * <li>1+Sqr(2)+Sqr(3)+i+Sqr(2)*i , 1/7+1.1231435436342 , 1/2 + Sqr(2) ,
	 * <li>a+Sqr(2)*a
	 * <li>2+3i , 5+ 5e^2 + 3ePi^2 + 4ePi + 4iePi
	 * <li>a^2 + 2ab + b^2 , a^2+ ab^2c + abc^2 + abc + 12 + Sqr(2)
	 * </ul>
	 * 
	 * <p>
	 * <b>Attention</b>: The method may return a value that smaller(larger) than
	 * zero when it should return -1(1).
	 * <p>
	 * You should use the method like this:{@code (x.compareTo(y)} &lt;<i>op</i>&gt;
	 * {@code 0)}, where &lt;<i>op</i>&gt; is one of the six comparison
	 * operators.({@literal <}, ==,{@literal >}, {@literal >=}, !=, {@literal <=}) ,
	 * 
	 * @param formula
	 *            that will be compared with this formula
	 * @return -1 0 1 if this formula should be in front of,equals,or after the
	 *         formula given in one expression
	 */
	@Override
	public int compareTo(Formula formula) {
		// System.out.println(times++);
		return normalOrder.compare(this,formula);
	}

	/**
	 * Returns the power of all the characters' sum.
	 * @return
	 */
	public BigDecimal getPower(){
		BigDecimal sum = BigDecimal.ZERO;
		for (BigDecimal p : character.values()) {
			sum = sum.add(p);
		}
		return sum;
	}


	
	
	/**
	 * it will return a result that whether this one and another one have the same
	 * character
	 * 
	 * @param formula
	 * @return a result that whether this one and another one have the same
	 *         character
	 */
	public boolean haveSameChar(Formula formula) {
		boolean have =  CollectionSup.setEqual(character.entrySet(), formula.character.entrySet(),
				(Entry<String, BigDecimal> e1, Entry<String, BigDecimal> e2) -> e1.getKey().equals(e2.getKey())
						&& e1.getValue().compareTo(e2.getValue()) == 0);
		return have;
	}

	/**
	 * 
	 * @return -this
	 */
	public Formula negate() {
		if (signum == 0) {
			return ZERO;
		}
		return new Formula(-signum, numerator, denominator, radical, character);
	}

	/**
	 * the method will return a Formula equals 1/this
	 * <p>
	 * notice that the i part of the formula will be calculate for example : 1/i =
	 * i^-1 = -i
	 * 
	 * @return a formula
	 * @throws ArithmeticException
	 *             if this==ZERO
	 */
	public Formula reciprocal() {
		if (this.equals(Formula.ZERO)) {
			throw new ArithmeticException("Cannot cal reciprocal:" + this.toString());
		}
		BigInteger[] ndr = new BigInteger[3];
		ndr[0] = this.denominator;
		ndr[1] = this.numerator.multiply(this.radical);
		ndr[2] = this.radical;

		BigInteger temp = ndr[0].gcd(ndr[1]);
		ndr[0] = ndr[0].divide(temp);
		ndr[1] = ndr[1].divide(temp);

		// deal with all chracter
		HashMap<String, BigDecimal> character = new HashMap<String, BigDecimal>();
		for (Entry<String, BigDecimal> e : this.character.entrySet()) {
			character.put(e.getKey(), e.getValue().negate());
		}
		int signum = this.signum;
		// do with i
		if (character.containsKey(I_STR)) {
			signum = -signum;
			BigDecimal times = this.character.get(I_STR);
			character.put(I_STR, BigDecimal.valueOf(2).subtract(times));
			// the times of i should always be in [0,2) so the result of 1/i^a will be
			// -i^(2-a)
		}

		return new Formula(signum, ndr, character);

	}

	// private class FormulaBuilder{
	// BigInteger numerator;
	// BigInteger denominator;
	// BigInteger radical;
	// int signum;
	// HashMap<String,BigDecimal> character;
	// }

	public boolean isPositive() {
		return signum > 0;
	}

	/**
	 * 
	 * @return a formula that equals -this
	 */

	/**
	 * an easy way to show how formula is quite different
	 * 
	 * @return
	 */
	@Override
	public int hashCode() {
		return hashCode;
	}

	/**
	 * Returns a Formula that doesn't have the given character and all of the other
	 * number remains the same.
	 * @param ch the character
	 * @return a new formula
	 */
	public Formula removeChar(String ch) {
		HashMap<String, BigDecimal> nc = new HashMap<String, BigDecimal>(character);
		nc.remove(ch);
		return new Formula(signum, numerator, denominator, radical, nc);
	}
	
	/**
	 * Calls this formula to regular its exponent, converting {@code 1.0} to {@code 1}.
	 */
	public Formula regularizeExponent() {
		HashMap<String, BigDecimal> nc = new HashMap<String, BigDecimal>(character);
		for(Entry<String,BigDecimal> en : nc.entrySet()) {
			en.setValue(en.getValue().stripTrailingZeros());
		}
		return new Formula(signum, numerator, denominator, radical, nc);
	}

	/**
	 * set the max shown digit of the number if d<=0 the method will do nothing
	 * 
	 * @param d
	 * @throws IllegalArgumentException
	 *             if d>DECIMAL128.getPrecision()(34)
	 */
	public static void setMaxShownDigit(int d) {
		if (d > MathContext.DECIMAL128.getPrecision()) {
			throw new IllegalArgumentException("The digit is too big" + d);
		}
		if (d == 1) {
			// only show one digit
			Formula.MAX_SHOWN_DIGIT = d;
			Formula.DECIMAL_PATTERN = "0E0";
			Formula.DECIMAL_FORMAT = new DecimalFormat(Formula.DECIMAL_PATTERN);
		} else if (d > 1) {
			Formula.MAX_SHOWN_DIGIT = d;
			StringBuilder sb = new StringBuilder("0.");// create the pattern
			for (int i = 0; i < d; i++) {

				sb.append('#');
			}
			sb.append("E0");
			Formula.DECIMAL_PATTERN = sb.toString();
			Formula.DECIMAL_FORMAT = new DecimalFormat(Formula.DECIMAL_PATTERN);
		}
	}

	/**
	 * set the max shown digit of the Fraction if d<=0 the method will do nothing
	 * 
	 * @param d
	 * @throws IllegalArgumentException
	 *             if d>DECIMAL128.getPrecision()(34)
	 */
	public static void setMaxFraShowDigit(int d) {
		if (d > 0) {
			MAX_FRACTION_DIGIT = d;
		}
	}

	private static Pattern numberPattern = Pattern.compile("[+-]?[\\d\\.]+");
	private static Pattern charPattern = Pattern.compile("(" + PI_STR + ")|([a-zA-Z](\\[\\w+\\])?)");

	/**
	 * 创建表达式的主方法，通过读取一串字符串来产生新的公式
	 * 
	 * 
	 * @param str
	 * @return
	 */
	public static Formula valueOf(final String str) {
		// take full advantage of the stored formula
		if (str.equals("1") || str.trim().equals("1"))
			return ONE;
		else if (str.equals("0") || str.trim().equals("0")) {
			return ZERO;
		}
		if (str.trim().isEmpty()) {
			throw new FormulaFormatException("Nothing");
		}

		BigInteger[] ndr = new BigInteger[3];
		ndr[0] = BigInteger.ONE;
		ndr[1] = BigInteger.ONE;
		ndr[2] = BigInteger.ONE;
		int signum = 1;
		HashMap<String, BigDecimal> character = new HashMap<String, BigDecimal>();
		int pos = 0;
		char c;
		while (true) {
			c = str.charAt(pos);
			if (c == '-') {
				signum = -signum;
			} else if (c != '+') {
				break;
			}
			pos++;
		}
		int state = 0;
		/*
		 * state: 0 : top no-sqr 1 : top sqr 2 : down no-sqr 3 : down sqr
		 * 
		 */
		boolean hasChar = false;
		boolean hasOpe = false;
		Matcher nm = numberPattern.matcher(str);// 匹配数字的匹配器
		Matcher cm = charPattern.matcher(str);// 匹配字母
		BigInteger[] uad;
		int end = str.length();
		while (pos < end) {
			// System.out.println("Start "+pos);
			nm.region(pos, end);
			cm.region(pos, end);
			c = str.charAt(pos);
			if (nm.lookingAt()) {//
				if (hasChar) {
					throw new FormulaFormatException("Number after char:", str, pos);
				}
				try {
					BigDecimal tempNum = new BigDecimal(nm.group());
					uad = toFraction(tempNum);
				} catch (NumberFormatException e) {
					throw new FormulaFormatException("Number Format is Wrong", str, pos);
				}
				switch (state) {
				case 0: {
					ndr[0] = ndr[0].multiply(uad[0]);
					ndr[1] = ndr[1].multiply(uad[1]);
					break;
				}
				case 1: {
					ndr[2] = ndr[2].multiply(uad[0].multiply(uad[1]));
					ndr[1] = ndr[1].multiply(uad[1]);
					break;
				}
				case 2: {
					ndr[0] = ndr[0].multiply(uad[1]);
					ndr[1] = ndr[1].multiply(uad[0]);
					break;
				}
				case 3: {
					ndr[2] = ndr[2].multiply(uad[0].multiply(uad[1]));
					ndr[1] = ndr[1].multiply(uad[0]).multiply(uad[1]);
					break;
				}
				}
				hasOpe = false;
				pos = nm.end();
				continue;
			}
			if (c == '*' || c == '/' || c == 'S') {
				if (c == '*') {
					if (hasOpe) {
						throw new FormulaFormatException("Muliplied Operation * :", str, pos);
					}
					state = 0;
					hasOpe = true;
				} else if (c == '/') {
					if (hasOpe) {
						throw new FormulaFormatException("Muliplied Operation / :", str, pos);
					}
					state = 2;
					hasOpe = true;
				} else if (c == SQR_STR.charAt(0) && pos + 2 < str.length() && str.charAt(pos + 1) == SQR_STR.charAt(1)
						&& str.charAt(pos + 2) == SQR_STR.charAt(2)) {
					if (state == 0) {
						state = 1;
					} else if (state == 2) {
						state = 3;
					}
					pos = pos + 2;
					hasOpe = true;
				}
				if (hasOpe) {
					hasChar = false;
				}
				pos++;
				continue;
			}
			if (cm.lookingAt()) {
				BigDecimal time;
				int tpos = cm.end();
				if (tpos < str.length() && str.charAt(tpos) == '^') {
					nm.region(tpos + 1, end);
					if (nm.lookingAt()) {
						try {
							time = new BigDecimal(nm.group());
							tpos = nm.end();
						} catch (NumberFormatException e) {
							throw new FormulaFormatException("Number Format is Wrong", str, tpos);
						}
					} else {
						throw new FormulaFormatException("Illgeal Formula Expression using ^", str, tpos);
					}
				} else {
					time = BigDecimal.ONE;
				}
				switch (state) {
				case 1: {
					time = time.divide(new BigDecimal("2"));
					break;
				}
				case 2: {
					time = time.negate();
					break;
				}
				case 3: {
					time = time.divide(new BigDecimal("2")).negate();
				}
				}
				// System.out.println(character+"---"+cm.group()+"---"+time);
				addChar(character, cm.group(), time);
				hasOpe = false;
				hasChar = true;
				pos = tpos;
				continue;
			} else {
				throw new FormulaFormatException("Unspported Character", str, pos);
			}

		}
		// read part is over
		return newInstanceP(signum, ndr, character);
	}

	public static Formula valueOf(BigInteger val) {
		int signum = val.compareTo(BigInteger.ZERO);
		if (signum == 0) {
			return ZERO;
		}
		if (val.equals(BigInteger.ONE)) {
			return ONE;
		}
		if (signum < 0) {
			val = val.negate();
		}
		return new Formula(signum, val, BigInteger.ONE, BigInteger.ONE, new HashMap<String, BigDecimal>());

	}

	public static Formula valueOf(long l) {
		return valueOf(BigInteger.valueOf(l));
	}

	public static Formula valueOf(BigDecimal number) {
		int signum = number.compareTo(BigDecimal.ZERO);
		if (signum == 0) {
			return ZERO;
		}
		if (number.compareTo(BigDecimal.ONE) == 0) {
			return ONE;
		}
		if (signum < 0) {
			number = number.negate();
		}
		BigInteger[] uad = toFraction(number);
		return new Formula(signum, uad[0], uad[1], BigInteger.ONE, new HashMap<String, BigDecimal>());
	}

	public static Formula asFraction(long numerator, long denominator, long radical) {
		return newInstanceP(1, BigInteger.valueOf(numerator), BigInteger.valueOf(denominator),
				BigInteger.valueOf(radical), new HashMap<String, BigDecimal>());
	}

	public static Formula asFraction(BigInteger numerator, BigInteger denominator, BigInteger radical) {
		return newInstanceP(1, numerator, denominator, radical, new HashMap<String, BigDecimal>());
	}

	static void addChar(HashMap<String, BigDecimal> character, String cha, BigDecimal t) {
		if (character.containsKey(cha)) {
			t = t.add(character.get(cha));
		}
		if (t.compareTo(BigDecimal.ZERO) == 0) {
			character.remove(cha);
		} else {
			character.put(cha, t);
		}
	}

	static BigInteger[] toFraction(BigDecimal n) {
		return Term.toFraction(n);
	}

	/**
	 * i[0] radical i[1] numerator outside i[2] current step
	 * 
	 * @param i
	 * @return
	 */
	static void sortRad(BigInteger[] i) {
		Term.sortRad(i);
	}

	/**
	 * create a Formula which sign number={@code signum} and its numerator=ndr[0]
	 * denominator=ndr[1] radical=ndr[2] if any of the BigInteger in ndr is less
	 * than zero , the method will turn them to positive and change the signum
	 * 
	 * @param signum
	 * @param character
	 * @return
	 */

	public static Formula newInstance(int signum, BigInteger numerator, BigInteger denominator, BigInteger radical,
			Map<String, BigDecimal> character) {
		return newInstanceP(signum, numerator, denominator, radical, new HashMap<String, BigDecimal>(character));
	}

	/**
	 * this method will not create a new HashMap
	 * <p>
	 * notice :make sure the HashMap won't be change after the method
	 * 
	 * @param signum
	 * @param numerator
	 * @param denominator
	 * @param radical
	 * @param character
	 * @return
	 */
	static Formula newInstanceP(int signum, BigInteger numerator, BigInteger denominator, BigInteger radical,
			HashMap<String, BigDecimal> character) {
		if (signum == 0) {// first check
			return ZERO;
		}
		signum = signum * denominator.compareTo(BigInteger.ZERO);
		if (signum == 0) {
			throw new ArithmeticException("Denominator is ZERO: ");
		}
		signum = signum * numerator.compareTo(BigInteger.ZERO);
		if (signum == 0 || radical.compareTo(BigInteger.ZERO) == 0) {
			// double check to save storage
			return ZERO;
		}

		// make sure the radical is positive and add i if necessary
		if (radical.compareTo(BigInteger.ZERO) < 0) {
			radical = radical.negate();// 将根号内的-1提出
			addChar(character, "i", BigDecimal.ONE);
		}

		// make sure that denominator and numerator are both positive
		denominator = denominator.abs();
		numerator = numerator.abs();

		// deal with i
		if (character.containsKey(I_STR)) {
			BigDecimal[] rd = character.get(I_STR).divideAndRemainder(new BigDecimal("2"));
			if (rd[1].compareTo(BigDecimal.ZERO) == 0) {
				character.remove(I_STR);
			} else {

				character.put(I_STR, rd[1]);
			}
			signum = (rd[0].remainder(new BigDecimal("2")).compareTo(BigDecimal.ZERO) == 0) ? (signum) : (-signum);
		}
		// sort radical
		BigInteger[] i = new BigInteger[3];
		i[0] = radical;
		i[1] = BigInteger.ONE;
		i[2] = BigInteger.valueOf(2L);
		sortRad(i);
		radical = i[0];
		numerator = numerator.multiply(i[1]);
		// divide part
		BigInteger temp = numerator.gcd(denominator);
		numerator = numerator.divide(temp);
		denominator = denominator.divide(temp);
		// Printer.print("R="+radical);
		return new Formula(signum, numerator, denominator, radical, character);
	}

	/**
	 * another way using array to create a Formula with the given character
	 * <p>
	 * notice :make sure the HashMap won't be change after the method
	 * 
	 * @param signum
	 * @param ndr
	 * @param character
	 * @return
	 */
	static Formula newInstanceP(int signum, BigInteger[] ndr, HashMap<String, BigDecimal> character) {
		return newInstanceP(signum, ndr[0], ndr[1], ndr[2], character);
	}

	/**
	 * create a Formula without check ndr
	 * 
	 * @param signum
	 * @param ndr
	 * @param f
	 * @return
	 */
	static Formula sameCharFormula(int signum, BigInteger[] ndr, Formula f) {
		if (signum == 0) {
			return ZERO;
		}
		BigInteger temp = ndr[0].gcd(ndr[1]);
		ndr[0] = ndr[0].divide(temp);
		ndr[1] = ndr[1].divide(temp);

		return new Formula(signum, ndr, f.character);
	}

	static Formula createWithoutCheck(int signum, BigInteger[] ndr, HashMap<String, BigDecimal> character) {
		if (signum == 0) {
			return ZERO;
		}
		return new Formula(signum, ndr, character);
	}

	public static MathCalculator<Formula> getCalculator() {
		return FormulaCalculator.DEFAULT_FORMULA_CALCULATOR;
	}

	/**
	 * Returns the gcd of the formulas.
	 * 
	 * @param fs
	 * @return
	 */
	public static Formula gcd(Formula... fs) {
		if (fs.length == 1) {
			return fs[0];
		}
		// find GCD for numerator and radical,
		// find LCM for denominator
		BigInteger gcd_n = null;
		BigInteger gcd_r = null;
		BigInteger lcm_d = null;
		/**
		 * The power to minus
		 */
		HashMap<String, BigDecimal> commonCharP = null;
		boolean first = true;
		// fill the map with the first one's characters
		for (Formula f : fs) {
			if (first) {
				gcd_n = f.getNumerator();
				lcm_d = f.getDenominator();
				gcd_r = f.getRadical();
				commonCharP = new HashMap<String, BigDecimal>(f.getCharacterS());
				first = false;
			} else {
				gcd_n = gcd_n.gcd(f.getNumerator());
				lcm_d = lcm_d.multiply(f.getDenominator().divide(lcm_d.gcd(f.getDenominator())));
				gcd_r = gcd_r.gcd(f.getRadical());
				HashMap<String, BigDecimal> commonCharPN = new HashMap<String, BigDecimal>();
				Map<String, BigDecimal> fchars = f.getCharacterS();
				for (Entry<String, BigDecimal> en : commonCharP.entrySet()) {
					BigDecimal fv = fchars.get(en.getKey());
					if (fv != null) {
						BigDecimal p = en.getValue();
						if (p.compareTo(fv) > 0) {
							p = fv;
						}
						commonCharPN.put(en.getKey(), p);
					} else {
						// does not contain this char
						if (en.getValue().compareTo(BigDecimal.ZERO) < 0) {
							commonCharPN.put(en.getKey(), en.getValue());
						}
					}
				}
				fchars.forEach((k, v) -> {
					if (v.compareTo(BigDecimal.ZERO) < 0) {
						commonCharPN.putIfAbsent(k, v);
					}
				});
				commonCharP = commonCharPN;
			}
		}
		gcd_n = gcd_n.equals(BigInteger.ZERO) ? BigInteger.ONE : gcd_n;
		gcd_r = gcd_r.equals(BigInteger.ZERO) ? BigInteger.ONE : gcd_r;
		return Formula.newInstanceP(1, gcd_n, lcm_d, gcd_r, commonCharP);
	}

	public static Formula gcdAndDivide(Formula... fs) {
		// find GCD for numerator and radical,
		// find LCM for denominator
		BigInteger gcd_n = null;
		BigInteger gcd_r = null;
		BigInteger lcm_d = null;
		/**
		 * The power to minus
		 */
		HashMap<String, BigDecimal> commonCharP = null;
		boolean first = true;
		// fill the map with the first one's characters
		for (Formula f : fs) {
			if (first) {
				gcd_n = f.getNumerator();
				lcm_d = f.getDenominator();
				gcd_r = f.getRadical();
				commonCharP = new HashMap<String, BigDecimal>(f.getCharacterS());
				first = false;
			} else {
				gcd_n = gcd_n.gcd(f.getNumerator());
				lcm_d = lcm_d.multiply(f.getDenominator().divide(lcm_d.gcd(f.getDenominator())));
				gcd_r = gcd_r.gcd(f.getRadical());
				HashMap<String, BigDecimal> commonCharPN = new HashMap<String, BigDecimal>();
				HashMap<String, BigDecimal> fchars = f.getCharacterS();
				for (Entry<String, BigDecimal> en : commonCharP.entrySet()) {
					BigDecimal fv = fchars.get(en.getKey());
					if (fv != null) {
						BigDecimal p = en.getValue();
						if (p.compareTo(fv) > 0) {
							p = fv;
						}
						commonCharPN.put(en.getKey(), p);
					} else {
						// does not contain this char
						if (en.getValue().compareTo(BigDecimal.ZERO) < 0) {
							commonCharPN.put(en.getKey(), en.getValue());
						}
					}
				}
				fchars.forEach((k, v) -> {
					if (v.compareTo(BigDecimal.ZERO) < 0) {
						commonCharPN.putIfAbsent(k, v);
					}
				});
				commonCharP = commonCharPN;
			}
		}
		gcd_n = gcd_n.equals(BigInteger.ZERO) ? BigInteger.ONE : gcd_n;
		gcd_r = gcd_r.equals(BigInteger.ZERO) ? BigInteger.ONE : gcd_r;
		for (int i = 0; i < fs.length; i++) {
			Formula f = fs[i];
			BigInteger nume = f.getNumerator().divide(gcd_n).multiply(lcm_d.divide(f.getDenominator()));
			BigInteger deno = BigInteger.ONE;
			BigInteger rad = f.getRadical().divide(gcd_r);
			HashMap<String, BigDecimal> cha = new HashMap<>();
			Map<String, BigDecimal> fchars = f.getCharacterS();
			for (Entry<String, BigDecimal> en : commonCharP.entrySet()) {
				BigDecimal bd = fchars.get(en.getKey());
				if (bd == null) {
					cha.put(en.getKey(), en.getValue().negate());
				} else {
					bd = bd.subtract(en.getValue());
					cha.put(en.getKey(), bd);
				}
			}
			fchars.forEach(cha::putIfAbsent);
			cha.entrySet().removeIf(en -> BigDecimal.ZERO.equals(en.getValue()));

			fs[i] = Formula.newInstanceP(f.getSignum(), nume, deno, rad, cha);
		}
		return Formula.newInstanceP(1, gcd_n, lcm_d, gcd_r, commonCharP);
	}

	/**
	 * Returns an integer if the formula actually represents an integer, or
	 * {@code null}.
	 * 
	 * @param f
	 * @return
	 */
	public static BigInteger asInteger(Formula f) {
		if (f.signum == 0) {
			return BigInteger.ZERO;
		}
		if (f.character.isEmpty()) {
			if (f.radical.equals(BigInteger.ONE) && f.denominator.equals(BigInteger.ONE)) {
				return f.signum < 0 ? f.numerator.negate() : f.numerator;
			}
		}
		return null;
	}

	public static Formula characterPower(String character, BigDecimal times) {
		if (character.isEmpty() || times == null) {
			throw new IllegalArgumentException();
		}
		if (BigDecimal.ZERO.equals(times)) {
			return ONE;
		}
		HashMap<String, BigDecimal> map = new HashMap<>();
		map.put(character, times);
		return new Formula(1, BigInteger.ONE, BigInteger.ONE, BigInteger.ONE, map);
	}

//	public static void main(String[] args) {
//		Formula x = Formula.valueOf("x^2*y");
//		print(x);
//		// print(fc.add(x1, x2));
//	}
}

/**
 * 2017-11-23
 */
package cn.ancono.math.numberModels.expression;

import cn.ancono.math.MathCalculator;
import cn.ancono.math.algebra.IPolynomial;
import cn.ancono.math.function.SVFunction;
import cn.ancono.math.numberModels.Fraction;
import cn.ancono.math.numberModels.Multinomial;
import cn.ancono.math.numberModels.ParserUtils;
import cn.ancono.math.numberModels.Term;
import cn.ancono.math.numberModels.api.Computable;
import cn.ancono.math.numberModels.structure.Polynomial;
import cn.ancono.utilities.Printer;
import cn.ancono.utilities.StringSup;
import cn.ancono.utilities.structure.WithInt;
import kotlin.Pair;
import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

import static cn.ancono.math.numberModels.expression.ExprFunction.createBasicCalculatorFunctions;

/**
 * Expression is the most universal number model to represent a number or a complex
 * algebraic expression.
 * <p>
 * To get a calculator of Expression, please use <code>ExprCalculator.getInstance()</code>
 *
 * @author liyicheng 2017-11-23 21:31
 * @see ExprCalculator
 */
public final class Expression implements Computable, Serializable {

    /**
     * The root node of the expression.
     */
    final Node root;

    /**
     * Construct an expression with its root node.
     */
    public Expression(Node root) {
        this.root = Objects.requireNonNull(root);
    }

    private String expr;

    /*
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if (expr == null) {
            expr = root.toString();
        }
        return expr;
    }

    /**
     * Converts the whole expression to a string in latex format.
     */
    public String toLatexString() {
        return root.toLatexString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Expression)) return false;
        Expression that = (Expression) o;
        return Objects.equals(root, that.root);
    }

    @Override
    public int hashCode() {
        return Objects.hash(root);
    }

    public void listNode(PrintWriter out) {
        PrintWriter pw = Printer.getOutput();
        Printer.reset(out);
        root.listNode(0);
        Printer.reset(pw);
    }

    public void listNode() {
        root.listNode(0);
    }

    /**
     * Gets the root of this expression
     *
     * @return
     */
    public Node getRoot() {
        return root;
    }

    @Override
    public double computeDouble(ToDoubleFunction<String> valueMap) {
        return root.computeDouble(valueMap);
    }

    @Override
    public <T> T compute(Function<String, T> valueMap, MathCalculator<T> mc) {
        return root.compute(valueMap, mc);
    }

    /**
     * Returns a single variable function from this expression.
     *
     * @param varName the variable of the function
     * @param mc      a expression calculator
     * @return a function
     */
    public SVFunction<Expression> asFunction(String varName, ExprCalculator mc) {
        return x -> mc.substitute(this, varName, x);
    }

    /**
     * Creates an expression from a multinomial.
     */
    public static Expression fromMultinomial(Multinomial p) {
        return new Expression(Node.newPolyNode(p, null));
    }


    public static Expression fromTerm(Term t) {
        return fromMultinomial(Multinomial.monomial(t));
    }

    public static Expression ofCharacter(String character) {
        return fromTerm(Term.singleChar(character));
    }

    /**
     * Returns an expression that represents a polynomial.
     */
    public static Expression fromPolynomialT(Polynomial<Term> p, String variableName) {
        return fromMultinomial(Multinomial.fromPolynomialT(p, variableName));
    }

    /**
     * Returns an expression that represents a polynomial.
     */
    public static Expression fromPolynomialM(Polynomial<Multinomial> p, String variableName) {
        return fromMultinomial(Multinomial.fromPolynomialM(p, variableName));
    }

    public static Expression fromPolynomialE(IPolynomial<Expression> p, String variableName) {
        List<Node> terms = new ArrayList<>();
        for (int i = 0; i <= p.getLeadingPower(); i++) {
            Expression coeExpr = p.get(i);
            Node root = coeExpr.root;
            if (root.getType() == Node.Type.POLYNOMIAL) {
                if (((Node.Poly) root).p.isZero()) {
                    //skip zeros
                    continue;
                }
            }
            var variable = Term.characterPower(variableName, Fraction.of(i));
            Node pow = Node.newPolyNode(Multinomial.monomial(variable));
            Node coe = root.cloneNode();
            Node term = Node.wrapNodeAM(false, coe, pow);
            terms.add(term);
        }
        Node root = Node.wrapNodeAM(true, terms);
        return new Expression(root);
    }


    /**
     * Creates an expression from a string without performing any simplification.
     * <h3>Variables:</h3>
     * The expression supports variables of single characters.
     * <h3>Functions:</h3>
     * All the basic math functions are included and will be recognized. Also, customized functions can
     * be recognized with a '_' suffix, such as myFunction_(x). All the letter in front of _ directly will
     * be parsed to the name of the function.
     *
     * @param expr a string
     * @return a new Expression
     */
    @NotNull
    public static Expression valueOf(String expr) {
        return new ExprParser(expr).parse();
    }


    /**
     * Gets the default calculator of <code>Expression</code>.
     */
    public static ExprCalculator getCalculator() {
        return ExprCalculator.getInstance();
    }

    /**
     * The identifier for a function as a suffix
     */
    public static final char FUNCTION_IDENTIFIER = '_';

    private static final Set<String> FUNCTION_NAMES;

    static {
        FUNCTION_NAMES = new HashSet<>();
        for (ExprFunction f : createBasicCalculatorFunctions(Multinomial.getCalculator())) {
            FUNCTION_NAMES.add(f.getName());
        }
        //some common names
        FUNCTION_NAMES.add("f");
        FUNCTION_NAMES.add("g");
        FUNCTION_NAMES.add("h");
    }

    static class ExprParser {
        String expr;
        Set<String> functionName;

        ExprParser(String expr) {
            this.expr = expr;
            functionName = FUNCTION_NAMES;
        }

        public Expression parse() {
            Node root = partAdd(expr, 0, null);
            return new Expression(root);
        }

        void throwFor(String msg, int index) {
            ParserUtils.throwFor(expr, msg, index);
        }

        void throwFor(int index) {
            throwFor("Wrong format: ", index);
        }

        Node negateOrNot(Node n, Boolean isPositive) {
            if (!isPositive) {
                if (Node.isPolynomial(n)) {
                    Node.Poly p = (Node.Poly) n;
                    return Node.newPolyNode(p.p.negate(), p.parent);
                }
                Node.NodeWithChildren parent = n.parent;
                Node re = Node.wrapNodeMultiply(n, Multinomial.NEGATIVE_ONE);
                re.parent = parent;
                return re;
            }
            return n;
        }

        Node partPoly(String str, int offset, Node.NodeWithChildren parent) {
            return Node.newPolyNode(parseWithExceptionDetail(str, offset), parent);
        }

        /**
         * @param str
         * @param offset indicating the string's position in the expr
         * @param parent
         * @return
         */
        Node partAdd(String str, int offset, Node.NodeWithChildren parent) {
            str = str.trim();
            if (!str.contains("(")) {
                return partPoly(str, offset, parent);
            }
            //split first:
            List<Pair<String, Boolean>> list = ParserUtils.splitByAdd(str, offset);
            if (list.size() <= 0) {
                throwFor("Empty: ", offset);
            }
            if (list.size() == 1) {
                Pair<String, Boolean> p = list.get(0);
                String s = p.getFirst();
                try {
                    Multinomial l = Multinomial.parse(s);
                    return new Node.Poly(parent, p.getSecond() ? l : l.negate());
                } catch (NumberFormatException ignored) {
                }
                return negateOrNot(partMultiply(s, offset, parent), p.getSecond());
            } else {
                var nodeList = new ArrayList<Node>(list.size());
                for (var p : list) {
                    Node n = negateOrNot(partMultiply(p.getFirst(), offset, null), p.getSecond());
                    offset += p.getFirst().length();
                    nodeList.add(n);
                }
                var addNode = Node.wrapNodeAM(true, nodeList);
                addNode.parent = parent;
                return addNode;
            }
        }

        Node partMultiply(String str, int offset, Node.NodeWithChildren parent) {
            str = str.trim();
            if (!str.contains("(")) {
                return partPoly(str, offset, parent);
            }
            int pos = 0;
            Multinomial nume = Multinomial.ONE;
            List<Node> nNode = new ArrayList<>(4);
            List<Node> dNode = new ArrayList<>(4);
            while (pos < str.length()) {
                int nextLeft = findLeftFrac(str, pos);
                if (nextLeft < 0) {
                    //failed to find, no bracket
                    String expr = str.substring(pos).trim();
                    if (expr.isEmpty()) {
                        break;
                    }
                    nume = nume.multiply(parseRemaining(expr, pos));
                    break;
                }
//                String expr = str.substring(pos,nextLeft);
                int endPos = nextLeft;
                boolean nextDeno = false;
                String fName;
                //determine function
                WithInt<String> temp = backFindFunction(str, endPos, offset);
                fName = temp.getObj();
                endPos = temp.getInt();
                if (StringSup.endWith(str, "*", endPos)) {
                    endPos--;
                } else if (StringSup.endWith(str, "/", endPos)) {
                    endPos--;
                    nextDeno = true;
                }
                if (endPos > pos) {
                    Multinomial m = parseRemaining(str.substring(pos, endPos), offset + pos);
                    nume = nume.multiply(m);
                }


                int nextRight = findRightFrac(str, nextLeft + 1);
                if (nextRight < 0) {
                    throwFor("Missing bracket: ", nextLeft);
                }
                String sub = str.substring(nextLeft + 1, nextRight);
                Node n;
                if (fName == null) {
                    n = partAdd(sub, offset + nextLeft + 1, null);
                } else {
                    n = parseToFunction(fName, sub, offset, nextLeft);
                }
                if (nextDeno) {
                    dNode.add(n);
                } else {
                    nNode.add(n);
                }

                pos = nextRight + 1;
            }
            Node result = buildMultiply(nume, nNode, dNode);
            result.parent = parent;
            return result;
        }

        int findLeftFrac(String expression, int start) {
            return expression.indexOf('(', start);
        }

        int findRightFrac(String expression, int start) {
            int count = 1;
            for (int i = start; i < expression.length(); i++) {
                if (expression.charAt(i) == '(') {
                    count++;
                } else if (expression.charAt(i) == ')') {
                    count--;
                    if (count == 0) {
                        return i;
                    }
                }
            }
            return -1;
        }


        /**
         * @param str    a str
         * @param endPos position of (
         * @param offset offset of str.0
         * @return
         */
        WithInt<String> backFindFunction(String str, int endPos, int offset) {
            if (endPos == 0) {
                return new WithInt<>(endPos);
            }
            if (str.charAt(endPos - 1) == FUNCTION_IDENTIFIER) {
                //find function
                int pos = endPos - 2;
                while (pos > -1) {
                    char ch = str.charAt(pos);
                    if (Character.isLetter(ch) || Character.isDigit(ch)) {
                        pos--;
                    } else {
                        break;
                    }
                }
                pos++;
                if (pos == endPos - 1) {
                    throwFor("Empty function name", offset + pos);
                }
                String fName = str.substring(pos, endPos - 1);
                return new WithInt<>(pos, fName);
            }

            String fName = null;
            for (String candidateName : functionName) {
                if (StringSup.endWith(str, candidateName, endPos)) {
                    if (fName == null || candidateName.length() > fName.length()) {
                        fName = candidateName;
                    }
                }
            }
            if (fName == null) {
                return new WithInt<>(endPos);
            }
            return new WithInt<>(endPos - fName.length(), fName);
        }

        Node parseToFunction(String fName, String sub, int offset, int nextLeft) {
            String[] ss = StringSup.splitWithMatching(sub, ',');
            if (ss.length == 1) {
                //single
                return Node.wrapNodeSF(fName, partAdd(sub, offset + nextLeft + 1, null));
            } else if (ss.length == 2) {
                Node c1 = partAdd(ss[0], offset + nextLeft + 1, null);
                Node c2 = partAdd(ss[1], offset + nextLeft + ss[0].length() + 2, null);
                return Node.wrapNodeDF(fName, c1, c2);
            } else {
                List<Node> list = new ArrayList<>(ss.length);
                int toffset = offset + 1;
                for (String s : ss) {
                    list.add(partAdd(s, toffset, null));
                    toffset += s.length();
                    toffset++;
                }
                return Node.wrapNodeMF(fName, list, false);
            }
        }

        Node buildMultiply(Multinomial m, List<Node> nNode, List<Node> dNode) {
            if (dNode.isEmpty()) {
                return Node.wrapNodeAM(false, nNode, m);
            } else {
                Node nume = Node.wrapNodeAM(false, nNode, m);
                Node deno = Node.wrapNodeAM(false, dNode);
                return Node.wrapNodeFraction(nume, deno);
            }
        }

        Multinomial parseRemaining(String expr, int offset) {
            char c = expr.charAt(0);
            if (c == '/' || c == '*') {
                expr = "1" + expr;
            }
            return parseWithExceptionDetail(expr, offset);
        }

        Multinomial parseWithExceptionDetail(String expr, int offset) {
            try {
                return Multinomial.parse(expr);
            } catch (NumberFormatException ex) {
                throwFor(ex.getMessage() + ": ", offset);
                //exception here
                return null;
            }
        }
    }

    public static Expression valueOf(long val) {
        return fromTerm(Term.valueOf(val));
    }

    public static Expression valueOf(Fraction val) {
        return fromTerm(Term.valueOf(val));
    }

    /**
     * Expression constant zero
     */
    public static final Expression ZERO = fromMultinomial(Multinomial.ZERO);
    /**
     * Expression constant one.
     */
    public static final Expression ONE = fromMultinomial(Multinomial.ONE);

    /**
     * Expression constant ten.
     */
    public static final Expression TEN = fromMultinomial(Multinomial.of(10L));

    /**
     * Expression constant negative one
     */
    public static final Expression NEGATIVE_ONE = fromMultinomial(Multinomial.NEGATIVE_ONE);

    /**
     * Expression constant <code>pi</code>, the ratio of the circumference of a circle to its diameter.
     */
    public static final Expression PI = fromMultinomial(Multinomial.PI);

    /**
     * Expression constant <code>e</code>, the base of natural logarithm.
     */
    public static final Expression E = fromMultinomial(Multinomial.E);

    /**
     * Expression constant <code>i</code>, the square root of <code>-1</code>.
     */
    public static final Expression I = fromMultinomial(Multinomial.I);

//    public static void main(String[] args){
//	    Expression expr = valueOf("(a+b)/(a-b)+(a+2b)/(a-b)");
//	    ExprCalculator mc = ExprCalculator.getInstance();
//        SimplificationStrategies.setCalRegularization(mc);
//	    print(mc.simplify(expr));
//	    print(mc.substitute(expr,"a",Expression.valueOf("exp(a,2)")));
//	    Function<String,Expression> f = x ->{
//	        switch(x){
//                case "a" : return valueOf("x");
//                case "b" : return valueOf("y");
//            }
//            return valueOf("1");
//        };
//	    ToDoubleFunction<String> f2 = x->{
//	        switch(x){
//                case "a" : return 5d;
//                case "b" : return 2d;
//                case "x" : return Math.PI/2;
//            }
//            return 1d;
//        };
//	    print(mc.simplify(expr).compute(f,mc));
//	    print(mc.simplify(expr).computeDouble(f2));
//	    print(mc.parseExpr("sin(x)").computeDouble(f2));
//    }


}
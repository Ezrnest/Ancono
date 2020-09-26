package cn.ancono.math.algebra.linearAlgebra;

import cn.ancono.math.MathCalculator;
import cn.ancono.math.algebra.linearAlgebra.LinearEquationSolution.Situation;
import cn.ancono.math.algebra.linearAlgebra.LinearEquationSolution.SolutionBuilder;
import cn.ancono.math.equation.EquationSolver;
import cn.ancono.math.equation.SVPEquation;
import cn.ancono.math.function.MathFunction;
import cn.ancono.math.numberModels.Fraction;
import cn.ancono.math.numberModels.structure.Polynomial;
import cn.ancono.math.numberTheory.IntCalculator;
import cn.ancono.utilities.ArraySup;
import kotlin.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cn.ancono.utilities.Printer.print;
import static cn.ancono.utilities.Printer.printnb;

/**
 * Provides some supportive methods for matrix.
 *
 * @author lyc
 */
public class MatrixSup {

    /**
     * Copy an area in the src to des.<P>
     * (src[xs][ys] ~ src[xe][ye]) -> (des[xd][yd] ~ des[xd+(xe-xs)][yd+(ye-ys)])
     *
     * @param src copy the area from this matrix
     * @param des the matrix to paste the data
     * @param xs  the starting index in the src
     * @param ys  another starting index in the src
     * @param xe  the ending index in the src
     * @param ye  another ending index in the src
     * @param xd  the starting index in the des
     * @param yd  another starting index in the des
     * @throws IndexOutOfBoundsException if any of the index is out of bound
     */
    public static void copyMatrix(int[][] src, int[][] des, int xs, int ys, int xe, int ye, int xd, int yd) {
        int len = ye - ys;
        for (; xs < xe; xs++) {
            System.arraycopy(src[xs], ys, des[xd], yd, len);
            xd++;
        }
    }

    /**
     * Return det(mat), this method use elementary operation to simplify the matrix first and then
     * calculate the result.
     *
     * @param mat a square Matrix
     * @return det(mat)
     * @deprecated use {@link Matrix#calDet()} instead.
     */
    @Deprecated
    public static <T> T fastDet(Matrix<T> mat) {
        if (mat.row != mat.column) {
            throw new ArithmeticException("Cannot calculate det for: " + mat.row + "*" + mat.column);
        }
        @SuppressWarnings("unchecked") T[][] mar = (T[][]) mat.getValues();
        List<MatrixOperation<T>> ops = mat.toUpperTri0(mar, mat.row, mat.column);
        boolean nega = false;
        for (MatrixOperation<T> mo : ops) {
            if (mo.ope == MatrixOperation.Operation.EXCHANGE_ROW) {
                nega = !nega;
            }
        }
        var mc = mat.getMathCalculator();
        T re = mar[0][0];
        for (int i = 1; i < mat.row; i++) {
            re = mc.multiply(re, mar[i][i]);
        }
        return nega ? mc.negate(re) : re;
    }

    /**
     * Exchange two row in the matrix.Throw IndexOutOfBoundsException if r1 or r2 is out of range.
     *
     * @throws IndexOutOfBoundsException if r1 or r2 is out of range.
     */
    public static void exchangeRow(Object[][] ma, int r1, int r2) {
        Object[] t = ma[r1];
        ma[r1] = ma[r2];
        ma[r2] = t;
    }

    /**
     * Exchange two column in the matrix.Throw IndexOutOfBoundsException if c1 or c2 is out of range.
     *
     * @throws IndexOutOfBoundsException if c1 or c2 is out of range.
     */
    public static void exchangeColumn(Object[][] ma, int c1, int c2) {
        for (int i = 0; i < ma.length; i++) {
            Object t = ma[i][c1];
            ma[i][c1] = ma[i][c2];
            ma[i][c2] = t;
        }
    }


    public static <T> void multiplyAndAddColumn(T[][] mat, int c1, int c2, T f, MathCalculator<T> mc) {
        for (int i = 0; i < mat.length; i++) {
            mat[i][c2] = mc.add(mat[i][c2], mc.multiply(mat[i][c1], f));
        }
    }

    public static <T> void multiplyAndAddRow(T[][] mat, int r1, int r2, T f, MathCalculator<T> mc) {
        for (int i = 0; i < mat[r1].length; i++) {
            mat[r2][i] = mc.add(mat[r2][i], mc.multiply(mat[r1][i], f));
        }
    }

    public static <T> void multiplyNumberColumn(T[][] mat, int c, T f, MathCalculator<T> mc) {
        for (int i = 0; i < mat.length; i++) {
            mat[i][c] = mc.multiply(mat[i][c], f);
        }
    }

    public static <T> void multiplyNumberRow(T[][] mat, int r, T f, MathCalculator<T> mc) {
        for (int i = 0; i < mat[r].length; i++) {
            mat[r][i] = mc.multiply(mat[r][i], f);
        }
    }

    public static <T> void addMatrix(T[][] dest, T[][] toAdd, MathCalculator<T> mc) {
        for (int i = 0; i < dest.length; i++) {
            for (int j = 0; j < dest[i].length; j++) {
                dest[i][j] = mc.add(dest[i][j], toAdd[i][j]);
            }
        }
    }

    public static <T> void multiplyMatrix(T[][] dest, T k, MathCalculator<T> mc) {
        for (int i = 0; i < dest.length; i++) {
            for (int j = 0; j < dest[i].length; j++) {
                dest[i][j] = mc.multiply(k, dest[i][j]);
            }
        }
    }

    /**
     * Return a upper triangle matrix filled with one.For example,the following matrix is
     * the result when n = 3 :
     * <pre>
     * 1 1 1
     * 0 1 1
     * 0 0 1
     * </pre>
     *
     * @param n the size
     * @return a matrix as description
     */
    public static <T> Matrix<T> upperTriWithOne(int n, MathCalculator<T> mc) {
        @SuppressWarnings("unchecked")
        T[][] mat = (T[][]) new Object[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < i; j++) {
                mat[i][j] = mc.getZero();
            }
            for (int j = i; j < n; j++) {
                mat[i][j] = mc.getOne();
            }
        }
        return new DMatrix<>(mat, n, n, mc);

    }

    /**
     * Identify the given expression
     */
    private static final Pattern P_FOR_FRACTION = Pattern.compile(" *([+\\-]?\\d+(/\\d+)?) *");

    /**
     * A simple but useful method to get a matrix input by user through console.
     *
     * @return a matrix
     */
    public static Matrix<Fraction> readMatrix(Scanner scn) {
        print("Enter row and column");
        printnb(">>> ");
        int row = scn.nextInt();
        int column = scn.nextInt();
        print("Enter number:");
        scn.nextLine();
        Fraction[][] ma = new Fraction[row][column];
        for (int i = 0; i < row; i++) {
            printnb(">>> ");
            String str = scn.nextLine();
            Matcher mach = P_FOR_FRACTION.matcher(str);
            for (int j = 0; j < column; j++) {
                mach.find();
                ma[i][j] = Fraction.of(mach.group(1));
            }
        }
        return Matrix.of(ma, Fraction.getCalculator());
    }

    /**
     * Parse a two-dimension string array to a matrix.
     */
    public static <T> Matrix<T> parseMatrix(String[][] mat, MathCalculator<T> mc, Function<String, ? extends T> parser) {
        Objects.requireNonNull(mc);
        int rowCount = mat.length;
        int columnCount = mat[0].length;
        Object[][] data = new Object[rowCount][columnCount];
        for (int i = 0; i < mat.length; i++) {
            String[] row = mat[i];
            if (row.length != columnCount) {
                throw new IllegalArgumentException("Column counts aren't the same!");
            }
            for (int j = 0; j < columnCount; j++) {
                data[i][j] = Objects.requireNonNull(parser.apply(row[j]));
            }
        }
        return new DMatrix<>(data, rowCount, columnCount, mc);
    }

    private static final Pattern ROW_PATTERN = Pattern.compile("\\[(.+?)]");

    private static final Pattern SPACE = Pattern.compile(" +");

    private static final Pattern LINE = Pattern.compile("^.*?$");

    private static final Pattern LINE_SEPARATOR = Pattern.compile("(\r\n)|(\r)|(\n)");

    /**
     * Parse a string to matrix, each row should be wrapped with a pair of '[]' and the matrix can be optionally
     * wrapped. The deliminator of elements in a row is one or more spaces.
     * <p>
     * For example, <pre>[[1 2 3][4 5 6][7 8 9]]</pre> is a valid matrix.
     */
    public static <T> Matrix<T> parseMatrix(String str, MathCalculator<T> mc, Function<String, ? extends T> parser) {
        if (str.startsWith("[")) {
            str = str.substring(1, str.length() - 1);
        }
        int column = -1;
        List<Object[]> mat = new ArrayList<>();
        var matcher = ROW_PATTERN.matcher(str);
        while (matcher.find()) {
            String row = matcher.group(1);
            String[] rowElements = SPACE.split(row);
            if (column == -1) {
                column = rowElements.length;
            } else {
                if (column != rowElements.length) {
                    throw new IllegalArgumentException("Column counts aren't the same!");
                }
            }
            mat.add(ArraySup.mapTo(rowElements, parser));
        }
        if (mat.isEmpty() || column == 0) {
            throw new IllegalArgumentException("Empty!");
        }
        Object[][] data = mat.toArray(new Object[0][]);
        return new DMatrix<>(data, data.length, column, mc);
    }

    public static <T> Matrix<T> parseMatrix(String str, String rowDeliminator, String columnDeliminator,
                                            MathCalculator<T> mc, Function<String, ? extends T> parser) {
        String[] rows = str.split(rowDeliminator);
        String[][] data = new String[rows.length][];
        for (int i = 0; i < rows.length; i++) {
            data[i] = rows[i].split(columnDeliminator);
        }
        return parseMatrix(data, mc, parser);
    }

    /**
     * Parse a matrix by default deliminator: for row is line separator and for column is several spaces.
     *
     * @param str a string of matrix to parse
     */
    public static <T> Matrix<T> parseMatrixD(String str,
                                             MathCalculator<T> mc, Function<String, ? extends T> parser) {
        String[] rows = LINE_SEPARATOR.split(str);
        String[][] data = new String[rows.length][];
        for (int i = 0; i < rows.length; i++) {
            data[i] = SPACE.split(rows[i]);
        }
        return parseMatrix(data, mc, parser);
    }

    public static Matrix<Fraction> parseFMatrix(String str) {
        return parseMatrixD(str, Fraction.getCalculator(), Fraction::of);
    }

    /**
     * Parse a string to vector. The deliminator of elements in a row is one or more spaces.
     * <p>
     * For example, <pre>1 2 3</pre> is a valid vector.
     */
    public static <T> Vector<T> parseVector(String str, MathCalculator<T> mc, Function<String, ? extends T> parser) {
        return parseVector0(str, SPACE, mc, parser);
    }

    private static <T> Vector<T> parseVector0(String str, Pattern deliminator, MathCalculator<T> mc, Function<String, ? extends T> parser) {
        if (str.startsWith("[")) {
            str = str.substring(1, str.length() - 1);
        }
        String[] elements = deliminator.split(str);
        @SuppressWarnings("unchecked")
        T[] data = (T[]) ArraySup.mapTo(elements, parser, Object.class);

        return new DVector<>(data, false, mc);
    }

    /**
     * Parses a vector with the given deliminator.
     *
     * @param deliminator the deliminator which will be treated as regex
     */
    public static <T> Vector<T> parseVector(String str, String deliminator,
                                            MathCalculator<T> mc, Function<String, ? extends T> parser) {
        return parseVector0(str, Pattern.compile(deliminator), mc, parser);
    }

    /**
     * According to the given matrix representing the coefficient of the linear equation,this method will
     * calculate the result with almost full precision (overflowing and underflowing are not considered.
     *
     * @param expandedMatrix all the coefficient should be contained in this matrix as well as
     *                       the constant part.
     * @return the solution of the equation.
     */
    public static <T> LinearEquationSolution<T> solveLinearEquation(T[][] expandedMatrix, MathCalculator<T> mc) {
        return solveLinearEquation(Matrix.valueOfNoCopy(expandedMatrix, mc));
    }


    /**
     * According to the given matrix representing the coefficient of the linear equation,this method will
     * calculate the result with almost full precision (overflowing and underflowing are not considered.
     *
     * @param expandedMatrix all the coefficient should be contained in this matrix as well as
     *                       the constant part.
     * @return the solution of the equation.
     */
    @SuppressWarnings({"unchecked"})
    public static <T> LinearEquationSolution<T> solveLinearEquation(Matrix<T> expandedMatrix) {
        MathCalculator<T> mc = expandedMatrix.getMathCalculator();
        Matrix.MatResult<T> matRe = expandedMatrix.toStepMatrix();
        DMatrix<T> step = (DMatrix<T>) matRe.result;
//		step.printMatrix();
        Object[][] data = step.data;
        T[][] mat = (T[][]) new Object[data.length][];
        for (int i = 0; i < data.length; i++) {
            mat[i] = (T[]) Arrays.copyOf(data[i], data[i].length);
        }
        //seek rows to get rank
        int rank = 0;
        final int len = step.column - 1;
        int[] baseColumns = new int[len];
//		printMatrix(mat);
        for (int i = 0; i < step.row; i++) {
            //column-1 avoid the constant
            for (int j = 0; j < len; j++) {
                if (!mc.isZero(mat[i][j])) {
                    baseColumns[rank++] = j;
                    break;
                }
            }
        }
        //test whether the equation has solution
        if (rank < step.row && !mc.isZero(mat[rank][len])) {
            //the rank of the expanded matrix is bigger.
            //NO SOLUTION
            return LinearEquationSolution.noSolution(expandedMatrix);
        }
        //calculate the result by using vector.
        T[] baseF = (T[]) new Object[len];
        for (int i = 0; i < rank; i++) {
            baseF[i] = mat[i][len];
        }
        for (int i = rank; i < len; i++) {
            baseF[i] = mc.getZero();
        }
        DVector<T> base = new DVector<>(baseF, false, mc);
        SolutionBuilder<T> sb = LinearEquationSolution.getBuilder();
        sb.setEquation(expandedMatrix);
        sb.setBase(base);
        //extract the k solution
        final int numberOfKSolution = len - rank;
        if (numberOfKSolution == 0) {
            sb.setSituation(Situation.SINGLE_SOLUTION);
            return sb.build();
        } else {
            sb.setSituation(Situation.UNBOUNDED_SOLUTION);
            DVector<T>[] vs = new DVector[numberOfKSolution];
            int searchPos = 0;
            int curCol = 0;
            for (int s = 0; s < numberOfKSolution; s++) {
                //find the next column
                while (baseColumns[searchPos] == curCol) {
                    searchPos++;
                    curCol++;
                }
                //x for current column is one.
                T[] solution = (T[]) new Object[len];
                int sPos = 0;
                for (int i = 0; i < len; i++) {
                    if (i == baseColumns[sPos]) {
                        solution[i] = mc.negate(mat[sPos][curCol]);
                        sPos++;
                    } else {
                        solution[i] = mc.getZero();
                    }
                }
                solution[curCol] = mc.getOne();
                vs[s] = new DVector<>(solution, false, mc);
                curCol++;
            }
            sb.setVariableSolution(vs);
            return sb.build();
        }
    }

    public static <T> LinearEquationSolution<T> solveLinearEquation(Matrix<T> coeMatrix, Vector<T> constance) {
        return solveLinearEquation(Matrix.concatColumn(coeMatrix, constance));
    }

    /**
     * Solves the linear equation
     * <pre><b>A</b><b>X</b> = 0</pre>
     * where <b>A</b> is the given {@code coefficientMatrix}.
     */
    @SuppressWarnings("unchecked")
    public static <T> LinearEquationSolution<T> solveHomogeneousLinearEquation(Matrix<T> coefficientMatrix) {
        Matrix<T> cm = coefficientMatrix;
        MathCalculator<T> mc = coefficientMatrix.getMathCalculator();
        final int n = cm.column;
        //shorten the name
        DMatrix<T> step = (DMatrix<T>) cm.toStepMatrix().result;
        T[][] mat = (T[][]) step.data;
        cm = null;
        int rank = 0;
        int[] baseColumns = new int[n];
//		printMatrix(mat);
        for (int i = 0; i < step.row; i++) {
            //column-1 avoid the constant
            for (int j = 0; j < n; j++) {
                if (!mc.isZero(mat[i][j])) {
                    baseColumns[rank++] = j;
                    break;
                }
            }
        }


        if (rank == n) {
            return LinearEquationSolution.zeroSolution(n, null, coefficientMatrix.getMathCalculator());
        }
        SolutionBuilder<T> sb = LinearEquationSolution.getBuilder();
        Vector<T> base = Vector.zeroVector(n, mc);
        sb.setBase(base);
        final int numberOfKSolution = n - rank;
        sb.setSituation(Situation.UNBOUNDED_SOLUTION);
        DVector<T>[] vs = new DVector[numberOfKSolution];
        int searchPos = 0;
        int curCol = 0;
        T negativeOne = mc.negate(mc.getOne());
        T zero = mc.getZero();
        for (int s = 0; s < numberOfKSolution; s++) {
            // find the next column
            while (baseColumns[searchPos] == curCol) {
                searchPos++;
                curCol++;
            }
            // x for current column is one.
            T[] solution = (T[]) new Object[n];
            int sPos = 0;
            for (int i = 0; i < n; i++) {
                if (i == baseColumns[sPos]) {
                    solution[i] = mat[sPos][curCol];
                    sPos++;
                } else {
                    solution[i] = zero;
                }
            }
            solution[curCol] = negativeOne;
            vs[s] = new DVector<T>(solution, false, mc);
            curCol++;
        }
        sb.setVariableSolution(vs);
        return sb.build();
//		sb.setSituation(Situation.UNBOUNDED_SOLUTION);
//		
//		Vector<T>[] vs = new Vector[numberOfKSolution];
//		
//		for(int i=0;i<numberOfKSolution;i++) {
//			T[] solution = (T[]) new Object[n];
//			int column = i+ rank;
//			for(int j=0;j<rank;j++) {
//				solution[j] =(T) step.data[j][column];
//			}
//			for(int j=rank;j<n;j++) {
//				if(j==column) {
//					solution[j]= netagiveOne;
//				}else {
//					solution[j] = zero;
//				}
//			}
//			vs[i] = new DVector<T>(solution, false, mc);
//		}
//		sb.setVariableSolution(vs);
//		return sb.build();


    }

    @SuppressWarnings("unchecked")
    public static <T> LinearEquationSolution.Situation determineSolutionType(Matrix<T> expandedMatrix) {
        //TODO NEED simplification
        MathCalculator<T> mc = expandedMatrix.getMathCalculator();
        Matrix.MatResult<T> matRe = expandedMatrix.toStepMatrix();
        DMatrix<T> step = (DMatrix<T>) matRe.result;
//		step.printMatrix();
        T[][] data = (T[][]) step.data;
        //seek rows to get rank
        int rank = 0;
        final int len = step.column - 1;
        //column-1 to avoid the constant part
        OUTER:
        for (int i = step.row - 1; i > -1; i--) {

            for (int j = len - 1; j > -1; j--) {
                if (!mc.isZero(data[i][j])) {
                    rank = Math.min(i + 1, len);
                    break OUTER;
                }
            }
        }
        //test whether the equation has solution
        if (rank < step.row && !mc.isZero(data[rank][len])) {
            //the rank of the expanded matrix is bigger.
            //NO SOLUTION
            return Situation.NO_SOLUTION;
        }
        final int numberOfKSolution = len - rank;
        if (numberOfKSolution == 0) {
            return Situation.SINGLE_SOLUTION;
        } else {
            return Situation.UNBOUNDED_SOLUTION;
        }
//        Matrix<T> coeMatrix = expandedMatrix.subMatrix(0,0,expandedMatrix.row-1,expandedMatrix.column-2);
//        boolean isHomogeneous = expandedMatrix.getColumn(expandedMatrix.column-1).isZeroVector();
//        int coeMatrixRank = coeMatrix.calRank();
    }

    /**
     * Returns the solution of ax = b, where <code>a, x, b</code> are all matrices. It
     * is required that the row count of <code>a</code> and <code>b</code> is the same.
     */
    public static <T> Matrix<T> solveMatrixEquation(Matrix<T> a, Matrix<T> b) {
        if (a.row != b.row) {
            throw new IllegalArgumentException("The row count of a and b isn't the same!");
        }
        var steps = a.toIdentityWay();
        return b.doOperation(steps);
    }

    /**
     * Computes the determinant of a 3*3 matrix given as an array, make sure the
     * array contains right type of element.
     */
    @SuppressWarnings("unchecked")
    public static <T> T det3(Object[][] mat, MathCalculator<T> mc) {
        T sum = mc.multiply(mc.multiply((T) mat[0][0], (T) mat[1][1]), (T) mat[2][2]);
        sum = mc.add(sum, mc.multiply(mc.multiply((T) mat[0][1], (T) mat[1][2]), (T) mat[2][0]));
        sum = mc.add(sum, mc.multiply(mc.multiply((T) mat[0][2], (T) mat[1][0]), (T) mat[2][1]));
        sum = mc.subtract(sum, mc.multiply(mc.multiply((T) mat[0][0], (T) mat[1][2]), (T) mat[2][1]));
        sum = mc.subtract(sum, mc.multiply(mc.multiply((T) mat[0][1], (T) mat[1][0]), (T) mat[2][2]));
        sum = mc.subtract(sum, mc.multiply(mc.multiply((T) mat[0][2], (T) mat[1][1]), (T) mat[2][0]));
        return sum;
    }

    public static <T> T det2(T[][] mat, MathCalculator<T> mc) {
        return mc.subtract(mc.multiply(mat[0][0], mat[1][1]), mc.multiply(mat[0][1], mat[1][0]));
    }

    /**
     * Returns a matrix which is similar to the matrix given and is a diagonal matrix.
     *
     * @param mat            a matrix
     * @param equationSolver a MathFunction to solve the equation, the length of the list should be equal to
     *                       the degree of the equation.
     */
    @SuppressWarnings("unchecked")
    public static <T> Matrix<T> similarDiag(Matrix<T> mat, EquationSolver<T, SVPEquation<T>> equationSolver) {
        SVPEquation<T> equation = mat.charEquation();
        List<T> eigenvalues = equationSolver.solve(equation);
        return Matrix.diag((T[]) eigenvalues.toArray(), mat.getMathCalculator());
    }

    /**
     * Returns a matrix which is similar to the matrix given and is a diagonal matrix.
     *
     * @param mat            a matrix
     * @param equationSolver a MathFunction to solve the equation, the length of the list should be equal to
     *                       the degree of the equation.
     */
    @SuppressWarnings("unchecked")
    public static <T> Matrix<T> similarDiag(Matrix<T> mat, MathFunction<SVPEquation<T>, List<T>> equationSolver) {
        SVPEquation<T> equation = mat.charEquation();
        List<T> eigenvalues = equationSolver.apply(equation);
        return Matrix.diag((T[]) eigenvalues.toArray(), mat.getMathCalculator());
    }

    public static <T> Matrix<T> sylvesterDet(Polynomial<T> p1, Polynomial<T> p2) {
        int n = p1.getDegree();
        int m = p2.getDegree();
        int size = m + n;
        var builder = Matrix.getBuilder(size, size, p1.getMathCalculator());
        for (int row = 0; row < m; row++) {
            for (int i = 0; i <= n; i++) {
                builder.set(p1.getCoefficient(n - i), row, i + row);
            }
        }
        for (int row = m; row < size; row++) {
            for (int i = 0; i <= m; i++) {
                builder.set(p2.getCoefficient(m - i), row, i + row - m);
            }
        }
        return builder.build();
    }

    /**
     * Returns the eigenmatrix of `M`, which is equal to `λI-M`
     */
    public static <T> Matrix<Polynomial<T>> charMatrix(Matrix<T> m, MathCalculator<Polynomial<T>> mcp) {
        if (m.row != m.column) {
            throw new IllegalArgumentException("M must be square!");
        }
        var mc = m.getMathCalculator();
        @SuppressWarnings({"unchecked"})
        Polynomial<T>[][] data = (Polynomial<T>[][]) new Polynomial[m.row][m.column];
        for (int i = 0; i < m.row; i++) {
            for (int j = 0; j < m.column; j++) {
                if (i == j) {
                    data[i][j] = Polynomial.ofRoot(mc, m.get(i, j));
                } else {
                    data[i][j] = Polynomial.constant(mc, mc.negate(m.get(i, j)));
                }
            }
        }
        return new DMatrix<>(data, m.row, m.column, mcp);

    }

    /**
     * Returns the eigenmatrix of `M`, which is equal to `λI-M`
     */
    public static <T> Matrix<Polynomial<T>> charMatrix(Matrix<T> m) {
        return charMatrix(m, Polynomial.getCalculator(m.getMathCalculator()));
    }

    /**
     * Determines whether the two matrices are similar.
     */
    public static <T> boolean isSimilar(Matrix<T> a, Matrix<T> b) {
        var pc = Polynomial.getCalculator(a.getMathCalculator());
        var x = charMatrix(a, pc);
        var y = charMatrix(b, pc);
        x = LambdaMatrixKt.toNormalForm(x);
        y = LambdaMatrixKt.toNormalForm(y);
        return x.valueEquals(y);
    }

    /**
     * Returns the Frobenius normal form of the given matrix, which is a
     * matrix similar to the given matrix.
     */
    public static <T> Matrix<T> frobeniusForm(Matrix<T> mat) {
        var pc = Polynomial.getCalculator(mat.getMathCalculator());
        var x = charMatrix(mat, pc);
        return LambdaMatrixKt.toFrobeniusForm(x, mat.getMathCalculator());
    }

    /**
     * Returns the Jordan normal form of the given matrix and the transformation matrix.
     */
    public static @Nullable Pair<Matrix<Fraction>, Matrix<Fraction>> jordanFormAndTrans(Matrix<Fraction> mat) {
        return LambdaMatrixSup.INSTANCE.jordanFormAndTrans(mat);
    }

    public static <T> Polynomial<Matrix<T>> matrixPolynomial(int n, Polynomial<T> p) {
        var cal = p.getMathCalculator();
        var cm = Matrix.getCalculator(n, cal);
        return p.mapTo(e -> Matrix.diag(e, n, cal), cm);
    }

    public static <T> Matrix<T> toHermitFrom(Matrix<T> m) {
        //TODO
        var mc = (IntCalculator<T>) m.getMathCalculator();
        @SuppressWarnings("unchecked")
        T[][] mat = (T[][]) m.getValues();
//        @SuppressWarnings("unchecked")
//        T[] temp = (T[]) new Object[m.row];
        int i = m.row - 1;
        int j = m.column - 1;
        int k = m.column - 1;
        int l;
        if (m.row <= m.column) {
            l = 0;
        } else {
            l = m.row - m.column + 1;
        }
        while (true) {
            while (j > 0) {
                j--;
                if (!mc.isZero(mat[i][j])) {
                    break;
                }

            }
            if (j > 0) {
                var triple = mc.gcdUV(mat[i][k], mat[i][j]);
                var d = triple.getFirst();
                var u = triple.getSecond();
                var v = triple.getThird();
                var k1 = mc.exactDivide(mat[i][k], d);
                var k2 = mc.exactDivide(mat[i][j], d);
                for (int p = 0; p < m.row; p++) {
                    var t = mc.add(mc.multiply(u, mat[k][p]), mc.multiply(v, mat[j][p]));
                    mat[j][p] = mc.subtract(mc.multiply(k1, mat[j][p]), mc.multiply(k2, mat[k][p]));
                    mat[k][p] = t;
                }
            } else {
                var b = mat[i][k];
                if (mc.isNegative(b)) {
                    for (int p = 0; p < m.row; p++) {
                        mat[k][p] = mc.negate(mat[k][p]);
                    }
                    b = mc.negate(b);
                }
                if (mc.isZero(b)) {
                    k++;
                } else {
                    for (int t = k + 1; t < m.column; t++) {
                        var q = mc.divideToInteger(mat[i][t], b);
                        for (int p = 0; p < m.row; p++) {
                            mat[j][p] = mc.subtract(mat[j][p], mc.multiply(q, mat[k][p]));
                        }
                    }
                }
                if (i <= l) {
                    break;
                } else {
                    i--;
                    k--;
                    j = k;
                }
            }

        }
        var zero = mc.getZero();
        for (int r = 0; r < m.row; r++) {
            for (int c = 0; c < k; c++) {
                mat[r][c] = zero;
            }
        }

        return new DMatrix<>(mat, m.row, m.column, mc);
    }


//
//    public static void main(String[] args) {
//        var mc = Calculators.getCalculatorInteger();
////        var mc = ExprCalculator.Companion.getNewInstance();
//        PolynomialX<Integer> p1 = PolynomialX.valueOf(mc,1,2,3);
//        PolynomialX<Integer> p2 = PolynomialX.valueOf(mc,4,5,6);
//        sylvesterDet(p1,p2).printMatrix();
//        printMatrix(parseMatrix("[[1 2][1 4]]", Fraction.Companion.getCalculator(),Fraction.Companion::valueOf));
//        printMatrix(parseMatrixD("1 2\n1 4", Fraction.Companion.getCalculator(),Fraction.Companion::valueOf));
//    }
}

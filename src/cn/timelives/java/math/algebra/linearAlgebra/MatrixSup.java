package cn.timelives.java.math.algebra.linearAlgebra;

import cn.timelives.java.math.equation.EquationSolver;
import cn.timelives.java.math.equation.SVPEquation;
import cn.timelives.java.math.function.MathFunction;
import cn.timelives.java.math.algebra.linearAlgebra.LinearEquationSolution.Situation;
import cn.timelives.java.math.algebra.linearAlgebra.LinearEquationSolution.SolutionBuilder;
import cn.timelives.java.math.numberModels.Fraction;
import cn.timelives.java.math.MathCalculator;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cn.timelives.java.utilities.Printer.print;
import static cn.timelives.java.utilities.Printer.printnb;

/**
 * Provides some supportive methods for matrix.
 * @author lyc
 *
 */
public class MatrixSup {
	
	/**
	 * Copy an area in the src to des.<P>
	 * (src[xs][ys] ~ src[xe][ye] )-> (des[xd][yd] ~ des[xd+(xe-xs)][yd+(ye-ys)])
	 * 
	 *  
	 * @param src copy the area from this matrix
	 * @param des the matrix to paste the data
	 * @param xs the starting index in the src
	 * @param ys another starting index in the src
	 * @param xe the ending index in the src
	 * @param ye another ending index in the src
	 * @param xd the starting index in the des
	 * @param yd another starting index in the des
	 * @throws IndexOutOfBoundsException if any of the index is out of bound
	 */
	public static void copyMatrix(int[][] src,int[][] des,int xs,int ys,int xe,int ye,int xd,int yd){
		int len = ye - ys;
		for(;xs<xe;xs++){
			System.arraycopy(src[xs], ys, des[xd], yd, len);
			xd++;
		}
	}
	
	/**
	 * Return det(mat), this method use elementary operation to simplify the matrix first and then
	 * calculate the result.
	 * @param mat a square Matrix
	 * @return det(mat) 
	 */
	public static Fraction fastDet(Matrix<Fraction> mat){
		if(mat.row!=mat.column){
			throw new ArithmeticException("Cannot calculate det for: "+ mat.row + "��" + mat.column);
		}
		Fraction[][] mar = (Fraction[][]) mat.getValues();
		List<MatrixOperation<Fraction>> ops = mat.toUpperTri0(mar,mat.row,mat.column);
		boolean nega = false;
		for(MatrixOperation<Fraction> mo : ops){
			if(mo.ope==MatrixOperation.Operation.EXCHANGE_ROW){
				nega = !nega;
			}
		}
		Fraction re = mar[0][0];
		for(int i=1;i<mat.row;i++){
			re = re.multiply(mar[i][i]);
		}
        return nega ? re.negate() : re;
	}
	
	/**
	 * Exchange two row in the matrix.Throw IndexOutOfBoundsException if r1 or r2 is out of range.
	 * @param ma
	 * @param r1
	 * @param r2
	 * @throws IndexOutOfBoundsException if r1 or r2 is out of range.
	 */
	public static void exchangeRow(Object[][] ma,int r1,int r2){
		Object[] t = ma[r1];
		ma[r1] = ma[r2];
		ma[r2] = t;
	}
	/**
	 * Exchange two column in the matrix.Throw IndexOutOfBoundsException if c1 or c2 is out of range.
	 * @param ma
	 * @param c1
	 * @param c2
	 * @throws IndexOutOfBoundsException if c1 or c2 is out of range.
	 */
	public static void exchangeColumn(Object[][] ma,int c1,int c2){
		for(int i=0;i<ma.length;i++){
			Object t = ma[i][c1];
			ma[i][c1] = ma[i][c2];
			ma[i][c2] = t;
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
	 * 
	 * @param n the size
	 * @return a matrix as description
	 */
	public static <T> Matrix<T> upperTriWithOne(int n,MathCalculator<T> mc){
		@SuppressWarnings("unchecked")
		T[][] mat = (T[][]) new Object[n][n];
		for(int i=0;i<n;i++){
			for(int j=0;j<i;j++){
				mat[i][j] = mc.getZero();
			}
			for(int j=i;j<n;j++){
				mat[i][j] = mc.getOne();
			}
		}
		return new DMatrix<>(mat, n, n, mc);
		
	}
	/**
	 * Identify the given expression
	 */
	private static final Pattern P_FOR_FRACTION = Pattern.compile(" *([\\+\\-]?\\d+(\\/\\d+)?) *");
	/**
	 * A simple but useful method to get a matrix input by user through console.
	 * @param scn
	 * @return a matrix
	 */
	public static Matrix<Fraction> readMatrix(Scanner scn){
		print("Enter row and column");
		printnb(">>> ");
		int row = scn.nextInt();
		int column = scn.nextInt();
		print("Enter number:");
		scn.nextLine();
		Fraction[][] ma= new Fraction[row][column];
		for(int i=0;i<row;i++){
			printnb(">>> ");
			String str = scn.nextLine();
			Matcher mach = P_FOR_FRACTION.matcher(str);
			for(int j=0;j<column;j++){
				mach.find();
				ma[i][j] = Fraction.Companion.valueOf(mach.group(1));
			}
		}
		return Matrix.valueOf(ma, Fraction.Companion.getCalculator());
	}
	
	
	
	
	
	/**
	 * According to the given matrix representing the coefficient of the linear equation,this method will
	 * calculate the result with almost full precision (overflowing and underflowing are not considered.
	 * @param expandedMatrix all the coefficient should be contained in this matrix as well as 
	 * the constant part.
	 * @return the solution of the equation.
	 */
	public static <T> LinearEquationSolution<T> solveLinearEquation(T[][] expandedMatrix,MathCalculator<T> mc){
		return solveLinearEquation(Matrix.valueOfNoCopy(expandedMatrix, mc));
	}
	
	
	/**
	 * According to the given matrix representing the coefficient of the linear equation,this method will
	 * calculate the result with almost full precision (overflowing and underflowing are not considered.
	 * @param expandedMatrix all the coefficient should be contained in this matrix as well as 
	 * the constant part.
	 * @return the solution of the equation.
	 */
	@SuppressWarnings({"unchecked", "ConstantConditions"})
	public static <T> LinearEquationSolution<T> solveLinearEquation(Matrix<T> expandedMatrix){
		MathCalculator<T> mc = expandedMatrix.getMathCalculator();
		Matrix.MatResult<T> matRe = expandedMatrix.toStepMatrix();
		DMatrix<T> step = (DMatrix<T>)matRe.result;
//		step.printMatrix();
		Object[][] data = step.data;
		T[][] mat = (T[][]) new Object[data.length][];
		for(int i=0;i<data.length;i++){
			mat[i] = (T[]) Arrays.copyOf(data[i], data[i].length);
		}
		//seek rows to get rank
		int rank = 0;
		final int len = step.column - 1;
		int[] baseColumns = new int[len];
//		printMatrix(mat);
		for(int i=0;i<step.row;i++){
			//column-1 avoid the constant
			for(int j=0;j<len;j++){
				if(!mc.isZero(mat[i][j])){
					baseColumns[rank++] = j;
					break;
				}
			}
		}
		//test whether the equation has solution
		if(rank<step.row&& !mc.isZero(mat[rank][len])){
			//the rank of the expanded matrix is bigger.
			//NO SOLUTION
			return LinearEquationSolution.noSolution(expandedMatrix);
		}
		//calculate the result by using vector.
		T[] baseF = (T[]) new Object[len];
		for(int i=0;i<rank;i++){
			baseF[i] = mat[i][len];
		}
		for(int i=rank;i<len;i++){
			baseF[i] = mc.getZero();
		}
		DVector<T> base = new DVector<>(baseF,false,mc);
		SolutionBuilder<T> sb = LinearEquationSolution.getBuilder();
		sb.setEquation(expandedMatrix);
		sb.setBase(base);
		//extract the k solution
		final int numberOfKSolution = len-rank;
		if(numberOfKSolution==0){
			sb.setSituation(Situation.SINGLE_SOLUTION);
			return sb.build();
		}else{
			sb.setSituation(Situation.UNBOUNDED_SOLUTION);
			DVector<T>[] vs = new DVector[numberOfKSolution];
			int searchPos = 0;
			int curCol = 0;
			for(int s=0;s<numberOfKSolution;s++){
				//find the next column
				while(baseColumns[searchPos]==curCol){
					searchPos++;
					curCol ++;
				}
				//x for current column is one.
				T[] solution = (T[]) new Object[len];
				int sPos = 0;
				for(int i=0;i<len;i++){
					if(i == baseColumns[sPos]){
						solution[i] = mc.negate(mat[sPos][curCol]);
						sPos ++;
					}else{
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

	/**
	 * Solves the linear equation 
	 * <pre><b>A</b><b>X</b> = 0</pre>
	 * where <b>A</b> is the given {@code coefficientMatrix}.
	 * @param coefficientMatrix
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> LinearEquationSolution<T> solveHomogeneousLinearEquation(Matrix<T> coefficientMatrix){
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
		for(int i=0;i<step.row;i++){
			//column-1 avoid the constant
			for(int j=0;j<n;j++){
				if(!mc.isZero(mat[i][j])){
					baseColumns[rank++] = j;
					break;
				}
			}
		}
		
		
		if(rank==n) {
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
		T netagiveOne = mc.negate(mc.getOne());
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
			solution[curCol] = netagiveOne;
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
	public static <T> LinearEquationSolution.Situation determineSolutionType(Matrix<T> expandedMatrix){
	    //TODO NEED simplification
		MathCalculator<T> mc = expandedMatrix.getMathCalculator();
		Matrix.MatResult<T> matRe = expandedMatrix.toStepMatrix();
		DMatrix<T> step = (DMatrix<T>)matRe.result;
//		step.printMatrix();
		T[][] data = (T[][]) step.data;
		//seek rows to get rank
		int rank = 0;
		final int len = step.column - 1;
//		printMatrix(mat);
		for(int i=0;i<step.row;i++){
			//column-1 avoid the constant part
			for(int j=0;j<len;j++){
				if(!mc.isZero(data[i][j])){
					break;
				}
			}
		}
		//test whether the equation has solution
		if(rank<step.row&& !mc.isZero(data[rank][len])){
			//the rank of the expanded matrix is bigger.
			//NO SOLUTION
			return Situation.NO_SOLUTION;
		}
        final int numberOfKSolution = len-rank;
		if(numberOfKSolution==0){
			return Situation.SINGLE_SOLUTION;
		}else {
            return Situation.UNBOUNDED_SOLUTION;
        }
//        Matrix<T> coeMatrix = expandedMatrix.subMatrix(0,0,expandedMatrix.row-1,expandedMatrix.column-2);
//        boolean isHomogeneous = expandedMatrix.getColumn(expandedMatrix.column-1).isZeroVector();
//        int coeMatrixRank = coeMatrix.calRank();
	}
	
	
	/**
	 * Computes the determinant of a 3*3 matrix given as an array, make sure the 
	 * array contains right type of element.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T det3(Object[][] mat,MathCalculator<T> mc){
		T sum = mc.multiply(mc.multiply((T)mat[0][0], (T)mat[1][1]), (T)mat[2][2]);
		sum = mc.add(sum, mc.multiply(mc.multiply((T)mat[0][1], (T)mat[1][2]), (T)mat[2][0]));
		sum = mc.add(sum, mc.multiply(mc.multiply((T)mat[0][2], (T)mat[1][0]), (T)mat[2][1]));
		sum = mc.subtract(sum, mc.multiply(mc.multiply((T)mat[0][0], (T)mat[1][2]), (T)mat[2][1]));
		sum = mc.subtract(sum, mc.multiply(mc.multiply((T)mat[0][1], (T)mat[1][0]), (T)mat[2][2]));
		sum = mc.subtract(sum, mc.multiply(mc.multiply((T)mat[0][2], (T)mat[1][1]), (T)mat[2][0]));
		return sum;
	}
	
	public static <T> T det2(T[][] mat,MathCalculator<T> mc){
		return mc.subtract(mc.multiply(mat[0][0], mat[1][1]), mc.multiply(mat[0][1], mat[1][0]));
	}
	
	/**
	 * Returns a matrix which is similar to the matrix given and is a diagonal matrix.
	 * @param mat a matrix
	 * @param equationSolver a MathFunction to solve the equation, the length of the list should be equal to 
	 * the degree of the equation.
	 * @return 
	 */
	@SuppressWarnings("unchecked")
	public static <T> Matrix<T> similarDiag(Matrix<T> mat,EquationSolver<T,SVPEquation<T>> equationSolver){
		SVPEquation<T> equation = mat.eigenvalueEquation();
		List<T> eigenvalues = equationSolver.solve(equation);
		return Matrix.diag((T[])eigenvalues.toArray(), mat.getMathCalculator());
	}
	/**
	 * Returns a matrix which is similar to the matrix given and is a diagonal matrix.
	 * @param mat a matrix
	 * @param equationSolver a MathFunction to solve the equation, the length of the list should be equal to 
	 * the degree of the equation.
	 */
	@SuppressWarnings("unchecked")
	public static <T> Matrix<T> similarDiag(Matrix<T> mat,MathFunction<SVPEquation<T>,List<T>> equationSolver){
		SVPEquation<T> equation = mat.eigenvalueEquation();
		List<T> eigenvalues = equationSolver.apply(equation);
		return Matrix.diag((T[])eigenvalues.toArray(), mat.getMathCalculator());
	}
}

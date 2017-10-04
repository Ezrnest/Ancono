package cn.timelives.java.math.linearAlgebra;

import static cn.timelives.java.utilities.Printer.print;
import static cn.timelives.java.utilities.Printer.printnb;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.timelives.java.math.LinearEquationSolution;
import cn.timelives.java.math.LinearEquationSolution.Situation;
import cn.timelives.java.math.LinearEquationSolution.SolutionBuilder;
import cn.timelives.java.math.linearAlgebra.Matrix.MatResult;
import cn.timelives.java.math.numberModels.Fraction;
import cn.timelives.java.math.numberModels.MathCalculator;

/**
 * 
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
	 * Return det(mat),this method use elementary operation to simplify the matrix first and then 
	 * calculate the result.
	 * @param mat a square Matrix
	 * @return det(mat) 
	 */
	public static Fraction fastDet(Matrix<Fraction> mat){
		if(mat.row!=mat.column){
			throw new ArithmeticException("Cannot calculate det for: "+ mat.row + "¡Á" + mat.column);
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
		return nega ? re.negative() : re;
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
		return new DMatrix<T>(mat, n, n,mc);
		
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
				ma[i][j] = Fraction.valueOf(mach.group(1));
			}
		}
		return Matrix.valueOf(ma,Fraction.getCalculator());
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
	@SuppressWarnings("unchecked")
	public static <T> LinearEquationSolution<T> solveLinearEquation(Matrix<T> expandedMatrix){
		MathCalculator<T> mc = expandedMatrix.getMathCalculator();
		MatResult<T> matRe = expandedMatrix.toStepMatrix();
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
//		print(baseColumns);
		for(int i=0;i<rank;i++){
//			baseF[baseColumns[i]] = mat[i][len];
			baseF[i] = mat[i][len];//TODO check whether the change here is right.
		}
		for(int i=rank;i<len;i++){
			baseF[i] = mc.getZero();
		}
		Vector<T> base = new Vector<>(baseF,false,mc);
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
			Vector<T>[] vs = new Vector[numberOfKSolution];
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
				vs[s] = new Vector<T>(solution,false,mc);
				curCol++;
			}
			sb.setVariableSolution(vs);
			return sb.build();
		}
	}
	/**
	 * Computes the determinant of a 3*3 matrix given as an array, make sure the 
	 * array contains right type of element.
	 * @param mat
	 * @param mc
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
}

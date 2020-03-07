package cn.ancono.math.algebra.linearAlgebra;

import cn.ancono.math.MathCalculator;
import cn.ancono.math.algebra.linearAlgebra.space.AffineSpace;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static cn.ancono.utilities.Printer.print;
import static cn.ancono.utilities.Printer.printnb;

/**
 * A wrapper for the solution of a linear equation.This class objects are often
 * returned by {@link MatrixSup#solveLinearEquation(Matrix)}.
 *
 * @author lyc
 */
public class LinearEquationSolution<T> {

    public enum Situation {
        NO_SOLUTION,
        SINGLE_SOLUTION,
        UNBOUNDED_SOLUTION,
    }


    private final Matrix<T> equation;
    private final Situation sit;


    private final Vector<T> specialSolution;
    private final Vector<T>[] baseSolutions;

    LinearEquationSolution(Matrix<T> equ, Situation sit, Vector<T> specialSolution, Vector<T>[] baseSolutions) {
        equation = equ;
        this.sit = sit;
        this.specialSolution = specialSolution;
        this.baseSolutions = baseSolutions;
    }


    @Nullable
    public AffineSpace<T> asLinearSpace() {
        if (sit == Situation.NO_SOLUTION) {
            return null;
        }
        return AffineSpace.Companion.valueOf(specialSolution, Objects.requireNonNull(solutionSpace()));
    }

    @Nullable
    public VectorBase<T> solutionSpace() {
        if (sit == Situation.NO_SOLUTION) {
            return null;
        }
        if (baseSolutions == null) {
            //only one solution
            return VectorBase.zeroBase(specialSolution.getSize(), specialSolution.getMathCalculator());
        }
        return VectorBase.createBaseWithoutCheck(baseSolutions);
    }


    /**
     * Return a LinearEquationSolution that represent no solution
     */
    public static <T> LinearEquationSolution<T> noSolution(Matrix<T> equ) {
        return new LinearEquationSolution<>(equ, Situation.NO_SOLUTION, null, null);
    }

    /**
     * Returns a LinearEquationSolution representing the only solution is all zero.
     *
     * @param n the number of variables.
     */
    public static <T> LinearEquationSolution<T> zeroSolution(int n, Matrix<T> equation, MathCalculator<T> mc) {
        return new LinearEquationSolution<T>(equation, Situation.SINGLE_SOLUTION, Vector.zeroVector(n, mc), null);
    }


    public Situation getSolutionSituation() {
        return sit;
    }

    public static <T> SolutionBuilder<T> getBuilder() {
        return new SolutionBuilder<T>();
    }

    /**
     * Returns the expanded matrix of the original linear equation, or {@code null} if
     * it is not supported.
     *
     * @return the equation or {@code null}.
     */
    public Matrix<T> getEquation() {
        return equation;
    }

    /**
     * Gets the base solution. If there is no solution, {@code null} will be
     * returned.
     *
     * @return the base
     */
    public Vector<T> getSpecialSolution() {
        return specialSolution;
    }

    /**
     * Get the part of k*vector
     *
     * @return the solution
     */
    public Vector<T>[] getBaseSolutions() {
        return baseSolutions;
    }


    /**
     * Returns one solution(non-zero if possible) in the solution space. If there is no solution, {@code null} will be
     * returned.
     */
    public Vector<T> getOneSolution() {
        if (sit == Situation.NO_SOLUTION) {
            return null;
        }
        if (sit == Situation.SINGLE_SOLUTION) {
            return specialSolution;
        }
        return specialSolution.isZeroVector() ? Vector.addVector(specialSolution, baseSolutions[0]) : specialSolution;
    }


    /**
     * Show the solution through printer.
     */
    public void printSolution() {
        switch (sit) {
            case NO_SOLUTION:
                print("NO solution");
                break;
            case SINGLE_SOLUTION:
                //print the solution
                specialSolution.printMatrix();
                break;
            case UNBOUNDED_SOLUTION:
                printSolu0();
                break;
            default:
                break;

        }
    }

    private void printSolu0() {
        specialSolution.transportMatrix().printMatrix();
        for (int k = 0; k < baseSolutions.length; k++) {
            printnb("+k" + k);
            baseSolutions[k].transportMatrix().printMatrix();
        }
    }


    public static class SolutionBuilder<T> {
        private boolean isBuilding = true;

        private SolutionBuilder() {

        }

        private Matrix<T> equation;
        private Situation situation;
        private Vector<T> base;
        private Vector<T>[] ss;

        /**
         * @param equation the equation to set
         */
        public void setEquation(Matrix<T> equation) {
            if (isBuilding)
                this.equation = equation;
            else
                throw new IllegalStateException("Build complete");
        }

        /**
         * @param situation the situation to set
         */
        public void setSituation(Situation situation) {
            if (isBuilding)
                this.situation = situation;
            else
                throw new IllegalStateException("Build complete");
        }

        /**
         * @param base the base to set
         */
        public void setBase(Vector<T> base) {
            if (base.isRow()) {
                this.base = base.transportMatrix();
            } else {
                this.base = base;
            }
        }

        public void setVariableSolution(Vector<T>[] ss) {
            for (int i = 0; i < ss.length; i++) {
                if (ss[i].isRow()) {
                    ss[i] = ss[i].transportMatrix();
                }
            }
            this.ss = ss;
        }


        public LinearEquationSolution<T> build() {
            if (situation != null) {
                boolean pass = false;
                switch (situation) {
                    case NO_SOLUTION:
                        break;
                    case SINGLE_SOLUTION:
                        if (base != null) {
                            pass = true;
                        }
                        break;
                    case UNBOUNDED_SOLUTION:
                        if (base != null && ss != null) {
                            pass = true;
                        }
                        break;
                    default:
                        break;
                }
                if (pass) {
                    isBuilding = false;
                    return new LinearEquationSolution<>(equation,
                            situation,
                            base, ss);
                }

            }
            throw new IllegalArgumentException("Lack of argument");

        }

    }


}

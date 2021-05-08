/**
 * 2017-11-25
 */
package cn.ancono.math.numberModels.expression;

import cn.ancono.math.numberModels.Multinomial;
import cn.ancono.math.numberModels.MultinomialCalculator;

import java.util.*;

;

/**
 * @author liyicheng
 * 2017-11-25 19:30
 */
public final class ExprFunctionHolder {

    private final Map<String, PolyFunctionS> singleFunctions;
    private final Map<String, PolyFunctionB> binaryFunctions;
    private final Map<String, PolyFunctionM> multiFunctions;
    private final Set<ExprFunction> functions;

    /**
     *
     */
    public ExprFunctionHolder() {
        singleFunctions = new HashMap<>();
        binaryFunctions = new HashMap<>();
        multiFunctions = new HashMap<>();
        functions = new TreeSet<>((ExprFunction x, ExprFunction y) -> x.getName().compareTo(y.getName()));
    }

    void addExprFunction(ExprFunction f) {
        functions.add(f);
        int n = f.getParamNumber();
        String name = f.getName();
        if (n <= 0) {
            throw new IllegalArgumentException();
        }
        Object pf = f.asPolyFunction();
        if (pf != null) {
            if (n == 1) {
                singleFunctions.put(name, (PolyFunctionS) pf);
            } else if (n == 2) {
                binaryFunctions.put(name, (PolyFunctionB) pf);
            } else {
                multiFunctions.put(name, (PolyFunctionM) pf);
            }
        }

    }

    /**
     * Returns the result of applying the single function, returns {@code null} if there
     * is no such function or the function cannot perform calculation.
     *
     * @param fname
     * @param x
     * @return
     */
    public Multinomial computeSingle(String fname, Multinomial x) {
        PolyFunctionS f = singleFunctions.get(fname);
        if (f == null) {
            return null;
        }
        try {
            return f.apply(x);
        } catch (UnsupportedOperationException uce) {
            return null;
        }
    }

    /**
     * Returns the result of applying the double function, returns {@code null} if there
     * is no such function or the function cannot perform calculation.
     *
     * @param fname
     * @param x
     * @return
     */
    public Multinomial computeDouble(String fname, Multinomial x, Multinomial y) {
        PolyFunctionB f = binaryFunctions.get(fname);
        if (f == null) {
            return null;
        }
        try {
            return f.apply(x, y);
        } catch (UnsupportedOperationException uce) {
            return null;
        }
    }

    /**
     * Returns the result of applying the multiple function, returns {@code null} if there
     * is no such function or the function cannot perform calculation.
     *
     * @param fname
     * @param xs
     * @return
     */
    public Multinomial computeMultiple(String fname, Multinomial[] xs) {
        PolyFunctionM f = multiFunctions.get(fname);
        if (f == null) {
            return null;
        }
        try {
            return f.apply(xs);
        } catch (UnsupportedOperationException uce) {
            return null;
        }
    }

    private int hash;


    /*
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        if (hash == 0) {
            int result = 1;
            result = prime * result + ((binaryFunctions == null) ? 0 : binaryFunctions.hashCode());
            result = prime * result + ((functions == null) ? 0 : functions.hashCode());
            result = prime * result + ((multiFunctions == null) ? 0 : multiFunctions.hashCode());
            result = prime * result + ((singleFunctions == null) ? 0 : singleFunctions.hashCode());
            hash = result;
        }
        return hash;
    }

    /*
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof ExprFunctionHolder))
            return false;
        ExprFunctionHolder other = (ExprFunctionHolder) obj;
        if (binaryFunctions == null) {
            if (other.binaryFunctions != null)
                return false;
        } else if (!binaryFunctions.equals(other.binaryFunctions))
            return false;
        if (functions == null) {
            if (other.functions != null)
                return false;
        } else if (!functions.equals(other.functions))
            return false;
        if (multiFunctions == null) {
            if (other.multiFunctions != null)
                return false;
        } else if (!multiFunctions.equals(other.multiFunctions))
            return false;
        if (singleFunctions == null) {
            if (other.singleFunctions != null)
                return false;
        } else if (!singleFunctions.equals(other.singleFunctions))
            return false;
        return true;
    }

    public static ExprFunctionHolder getDefaultKit(MultinomialCalculator pc) {
        return createFunctionHolder(ExprFunction.createBasicCalculatorFunctions(pc));
    }

    public static ExprFunctionHolder createFunctionHolder(List<ExprFunction> fs) {
        ExprFunctionHolder holder = new ExprFunctionHolder();
        for (ExprFunction f : fs) {
            holder.addExprFunction(f);
        }
        return holder;
    }

}

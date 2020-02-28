package test.math.comp;

import cn.ancono.math.MathCalculator;
import cn.ancono.math.equation.SVPEquation;
import cn.ancono.math.equation.Type;
import cn.ancono.math.equation.inequation.SVPInequation;
import cn.ancono.math.numberModels.Calculators;
import test.math.comp.studyUtils.Run;
import test.math.comp.studyUtils.StudyMethodRunner;

import static cn.ancono.utilities.Printer.print;

public class Equation {
    public static void main(String[] args) {
        StudyMethodRunner.runStudyClass(Equation.class);
    }

    MathCalculator<Double> mcd = Calculators.getCalculatorDoubleDev();

    @Run
    public void m1(){
        var ineqa = SVPInequation.quadratic(1d,-2d,-3d, Type.LESS,mcd);
        print(ineqa.getSolution());
    }
}

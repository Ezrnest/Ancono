package test.math.comp;

import cn.timelives.java.math.MathCalculator;
import cn.timelives.java.math.equation.SVPEquation;
import cn.timelives.java.math.equation.Type;
import cn.timelives.java.math.equation.inequation.SVPInequation;
import cn.timelives.java.math.numberModels.Calculators;
import test.math.comp.studyUtils.Run;
import test.math.comp.studyUtils.StudyMethodRunner;

import static cn.timelives.java.utilities.Printer.print;

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

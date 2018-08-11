package cn.timelives.java.math



abstract class OpMapper<T>(val mc:MathCalculator<T>){
    operator fun T.plus(y : T) = mc.add(this,y)

    operator fun T.minus(y : T) = mc.subtract(this,y)

}
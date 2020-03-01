package samples;

import cn.ancono.math.geometry.analytic.planeAG.Point;
import cn.ancono.math.geometry.analytic.planeAG.Triangle;
import cn.ancono.math.numberModels.expression.Expression;

public class PlaneAGSample {
    public static void triangle() {
        var mc = Expression.getCalculator();
        var str = "x1,y1,x2,y2,x3,y3".split(",");
        var A = Point.valueOf(mc.parse(str[0]), mc.parse(str[1]), mc);
        var B = Point.valueOf(mc.parse(str[2]), mc.parse(str[3]), mc);
        var C = Point.valueOf(mc.parse(str[4]), mc.parse(str[5]), mc);
        var triangle = Triangle.fromVertex(A, B, C);
        var G = triangle.centerG(); //gravity center
        var area = triangle.area();
        System.out.println(G);
        System.out.println(area);
    }


    public static void main(String[] args) {
        triangle();
    }
}

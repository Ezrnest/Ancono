# Ancono

An object-oriented Math library.

---

## Introduction

Ancono provides a wide range of basic and advanced math tools, focusing both on simplicity and efficiency. 
This library includes the following modules: 

1. Math functions: 
    
    power, gcd, lcm, mod, power and mod, Chinese remainder, Miller-Rabin prime test and more...
    
2. Number models:
 
    Ancono provides classes for fraction, complex and so on. Users can choose their desired 
    number models and operate the numbers with build-in methods. It is also possible for users 
    to customize or add new number models, and they can be used with full compatibility.
    
    See samples <a href="#numbers">here</a>.
    
3. Polynomials, multinomials and expression:

    Ancono enable users to operate polynomials and multinomials on a field. It is also possible to compute 
    and simplify complex expressions. 
    
    See samples <a href="#expressions">here</a>.

4. Linear algebra

    Ancono supports computations of matrices and vectors on a field. Linear spaces and linear transformations
    are also included. 
    
    See samples <a href="#matrix">here</a>.
    
5. Plane analytic geometry
    
    Line, triangle, conic sections (including circle, ellipse, hyperbola and parabola) and affine transformations 
    are provided in Ancono. 
    Useful methods are available, including computing the intersection points, computing the four kinds of centers 
    of a triangle and so much more. Ancono also supports visualization for curves and objects on plane.
    
    See samples <a href="#planeAG">here</a>.
     
6. Number theory and Combinations
    
    Ancono provides useful tools for number theory and combination, such as enumerating primes, factorization and 
    permutations.
    
    See samples <a href="#finiteGroups">here</a>.

7. Logic
    
    With Ancono, user can construct and operate formulas of proposition logic and first order logic. 
    
    See samples <a href="#logic">here</a>.
    
8. Calculus

    Utilities for derivatives, Taylor series, limits, differential forms and so on are provided.
    
    See samples <a href="#calculus">here</a>.
    
9. And more...
## Samples

#### <a name="numbers">Using number models</a>: 

Using Fraction:
```java
Fraction a = Fraction.valueOf("1/2");
System.out.println(a);
Fraction b = Fraction.ONE;
var c = a.add(b);
c = c.subtract(Fraction.ZERO);
c = c.multiply(a);
c = c.add(1);
System.out.println(c); 
//Result: 7/4
```

Using Complex:
```java
var cal = Calculators.getCalDouble();
Complex<Double> z1 = Complex.real(1.0, cal);
z1 = z1.squareRoot();
Complex<Double> z2 = Complex.of(1.0, 2.0, cal);
Complex<Double> z3 = z1.multiply(z2);
System.out.println(z3);
//Result: (1.0)+(2.0)i
```

#### <a name="polynomials">Polynomials:</a>

Multiplication:
```java
var cal = Calculators.getCalDouble();
// we use double as the type of the coefficient of the polynomials
var f = Polynomial.valueOf(cal, 1.0, 1.0, 2.0, 3.0); // 1 + x + 2x^2 + 3 x^3
var g = Polynomial.binomialPower(2.0, 3, cal); // (x-2)^3
System.out.println("f(x) = " + f);
System.out.println("g(x) = " + g);
var h = f.multiply(g);
System.out.println("f(x)g(x) = " + h);
```

Greatest common divisor:
```java
var calInt = Calculators.getCalInteger();
var cal = Fraction.getCalculator();
var f = Polynomial.valueOf(calInt,1,2,1).mapTo(Fraction::of,cal); // 1 + 2x + x^2
var g = Polynomial.valueOf(calInt,-2,-1,1).mapTo(Fraction::of,cal); // -2 - x + x^2
System.out.println("f(x) = " + f);
System.out.println("g(x) = " + g);
var h = f.gcd(g);
System.out.println("gcd(f(x),g(x)) = " + h);
```


#### <a name="expressions">Expression</a>
```java
var cal = Expression.getCalculator();
var f1 = cal.parseExpr("(x^2+3x+2)/(x+1)+sin(Pi/2)+exp(t)");
System.out.println(f1);
var f2 = cal.parseExpr("y+1");
System.out.println(f2);
var f3 = cal.divide(f1, f2);
System.out.println(f3);
```

#### <a name="matrix">Matrix</a>
```java
var cal = Calculators.getCalInteger();
var calFrac = Fraction.getCalculator();
var m1 = Matrix.of(cal, 2, 2,
        1, 2,
        4, 5).mapTo(Fraction::of, calFrac);
var m2 = Matrix.of(cal, 2, 2,
        3, -6,
        -4, 8).mapTo(Fraction::of, calFrac);
var m3 = Matrix.multiply(m1, m2);
m3.printMatrix();
var det = m3.calDet();
var rank = m3.calRank();
System.out.println("Det of the matrix: " + det);
System.out.println("Rank of the matrix: " + rank);
```

#### <a name="finiteGroups">Finite groups</a>
```java
var G = PermutationGroup.symmetricGroup(4);
var H = PermutationGroup.generateFrom(
        Permutations.swap(4, 0, 1),
        Permutations.swap(4, 2, 3));
var H1 = G.normalizer(H);
System.out.println(H1.getSet());
System.out.println(G.indexOf(H1));
```

#### <a name="planeAG">Plane Analytic Geometry</a>
Triangle:
```java
var mc = Expression.getCalculator();
var str = "x1,y1,x2,y2,x3,y3".split(","); // coordinates
var A = Point.valueOf(mc.parse(str[0]), mc.parse(str[1]), mc);
var B = Point.valueOf(mc.parse(str[2]), mc.parse(str[3]), mc);
var C = Point.valueOf(mc.parse(str[4]), mc.parse(str[5]), mc);
var triangle = Triangle.fromVertex(A, B, C);

var G = triangle.centerG(); //gravity center
var area = triangle.area(); // area of the triangle
System.out.println(G);
System.out.println(area);
```
#### <a name="calculus">Calculus</a>
Computing limit:
```java
var mc = Expression.getCalculator();
var expr = mc.parse("sin(x)/x");
var result = Limit.limitOf(expr, LimitProcess.Companion.toZero(mc),mc);
System.out.println("as x -> 0, lim sin(x)/x = "+result);
//result = 1
```


#### <a name="logic">Logic</a>
Proposition logic (written in Kotlin):
```kotlin
val formula = (p implies q) and (q implies r) implies (q implies r)
println(formula)
println("Is tautology: ${formula.isTautology}")
println("Main disjunctive norm: ${formula.toMainDisjunctiveNorm()}")
println("Conjunctive norm: ${formula.toConjunctiveNorm()}")
println("Is equivalent to T: ${formula valueEquals T}")
```

More samples are available in src/samples.


## Number models

To be capable of dealing with different representations of numbers and expressions, Ancono defines the interface of 
`MathCalculator`, which contains a basic set of operations. Math objects in Ancono usually requires an instance of
`MathCalculator` when construction. Implementations of `MathCalculator` of build-in number types(int, long, double) are 
provided in `Calculators`. 

Various number models are defined in Ancono, such as fraction, complex and expression. Generally, you can get the 
corresponding calculator by calling the static method `getCalculator()`.


### Math Calculator

`MathCalculator` is an interface defining a set of basic operations that might be used. The generic parameter of a 
`MathCalculator` defines the class that this `MathCalculator` operates. Ancono uses a calculator to 
define all the operations other than creates general interface for all number models, because multiple kinds of 
operations can be defined on a number model class, and some number classes may be unmodifiable (such as primitive types). 



### Math Object

The abstract class `MathObject` is the superclass of almost all Math objects in Ancono. The generic parameter of a 
`MathObject` represents the type of number model that it uses. You can get the corresponding `MathCalculator`.


## Usage
Users can utilize this library by simply import the jar file downloaded from /out/artifacts. If the visualization module 
will be used, please make sure [JavaFx](https://www.javafxdeveloper.com) is installed.

## Language
Ancono is written by both Java and [Kotlin](https://kotlinlang.org) and you can use it with Java only. However, some 
extra features (such as operator override) are only possible if you use Kotlin. 

## Dependencies
Core:
* Java: JDK 11
* JavaFx: 11
* Kotlin: 1.3

Test:
* JUnit 4

## Development
Project Ancono welcomes anyone to join in the development. 

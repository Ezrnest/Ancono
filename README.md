# Ancono

An object-oriented math library on JVM platform.

---

## Introduction

Ancono aims to provide a programming view of mathematics, as well as a wide range of basic and advanced math tools,
focusing both on simplicity and efficiency. It is also good to use it for studying purpose.

This library contains the following modules:

1. Math functions:

   power, gcd, lcm, mod, power and mod, Chinese remainder, Miller-Rabin prime test, gamma function and more...

2. Number models:

   Ancono provides classes for fraction, complex and so on. Users can choose their desired number models and operate the numbers with build-in methods. It is also possible for users 
    to customize or add new number models, and they can be used with full compatibility.
    
    See samples <a href="#numbers">here</a>.
    
3. Polynomials, multinomials and expression:

    Ancono enables users to operate polynomials and multinomials over a field (or even only over some kind of ring).
    
    General expression internally stored as abstract syntax tree is also supported. Expressions can be simplified 
    using simplification rules. Currently, basic simplification rules are implemented, and user can also add extensions
    if necessary.
    
    See samples: <a href="#polynomials">polynomial</a>, <a href="#expressions">expression</a>.

4. Linear algebra

    Ancono supports computations of matrices and vectors on a field. Linear spaces and linear transformations
    are also included. 
	
	Matrix class and vector class are provided with full functionality.
	There are all kinds of methods related to matrix, such as `det`, `rank`, computing solution space,
	characteristic polynomial, QR decomposition, lambda-matrix and so on. 
    
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

9. Differential geometry

   Curve and plane in 3-dimensional Euclidean space, computing curvature, torsion, Frenet frame, fundamental forms ...

   See samples <a href="#dgeometry">here</a>.

10. Graph theory

    Graphs, priority-first search, connected components, Euclidean cycle and so on.

This project is still being developed, more features will be available in the future.

## Number models

Generally speaking, we treat mathematical objects in different ways in different situations. For example, the number `1`
can be viewed as an integer, a rational number, a real number, a complex number or even a constant polynomial, depending
on the implicit context. In addition, in different cases we define different sets of operations on them. For example, we
can compare two real numbers (in natural order) but not two complex numbers. On the other hand, for computational and
complexity reasons, even the same mathematical object have to be treated differently in programming, such as `int`
,`long`, and `BigInteger`. Therefore, there does not exist a most general way to treat those objects, and we call a
concrete implementation for such an object in programming as a 'number model'.

Since we can use the same type in programming to represent different mathematical objects
(for example, `int` for both natural numbers and integers), we separate the number model type from the operations on it.
The latter is provided in the interface `MathCalculator`, while the former can be arbitrary. We use the name 'number
model' to refer to both the type and the operations.

Math objects (in Ancono the class `MathObject`) are usually based on number models. For example, we have matrices of
integers, rational numbers or so on. Although the number model can be different, the operations(or properties) defined
in math objects are in general the same (for example the matrix multiplication). When creating a math object,
a `MathCalculator` is generally required.

Various number models are defined in Ancono, such as fraction, complex and expression. Generally, you can get the
corresponding calculator by calling the static method `getCalculator()`. Implementations of `MathCalculator` of build-in
number types(int, long, double) are provided in `Calculators`.

If user want to use an external number model, simply implement the `MathCalculator` interface and pass the instance of
the calculator when needed. Then, it can be used just as other number models.

### Math Calculator

`MathCalculator` is an interface defining a set of basic operations that might be used. The generic parameter of a
`MathCalculator` defines the class that this `MathCalculator` operates. Ancono uses a calculator to define all the
operations other than creates general interface for all number models, because multiple kinds of operations can be
defined on a number model class, and some number classes may be unmodifiable (such as primitive types).

(To be detailed)

### Math Object

The abstract class `MathObject` is the superclass of almost all Math objects in Ancono. The generic parameter of a
`MathObject` represents the type of number model that it uses. You can get the corresponding `MathCalculator`.

(To be detailed)

## Samples

#### <a name="numbers">Using number models</a>:

Using Fraction:

```java
Fraction a=Fraction.valueOf("1/2");
        System.out.println(a);
        Fraction b=Fraction.ONE;
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

Greatest common divisor of multinomials:
```
var f = Multinomial.valueOf("x^2+2xy+y^2"); // = (x+y)^2
var g = Multinomial.valueOf("x^2+xy+xz+yz"); // = (x+y)(x+z)
var h = Multinomial.gcd(f,g);
System.out.println("f = " + f);
System.out.println("g = " + g);
System.out.println("gcd(f,g) = " + h);
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

#### <a name="dgeometry">Differential geometry</a>
Computing curvature and torsion of a curve: (written in Kotlin)
```kotlin
val t = mc.parse("t")
val r1 = makeCurve("t+Sqr3*sin(t), 2cos(t),Sqr3*t-sin(t)") // a helper method
val r2 = makeCurve("2cos(t/2),2sin(t/2),-t")
println("r1:")
println(r1.curvature(t))
println(r1.torsion(t))
println("r2:")
println(r2.curvature(t))
println(r2.torsion(t))
```

Computing coefficients of fundamental forms of a surface:

```kotlin
val expr = "a*cos(u)cos(v),a*cos(u)sin(v),a*sin(u)"
val r = makeSurface(expr) // a helper method
val u = mc.parse("u")
val v = mc.parse("v")
println(r.E(u, v))
println(r.F(u, v))
println(r.G(u, v))
```


More samples are available in the `samples` folder.

## Usage
Users can utilize this library by simply import the jar file downloaded from `build` or from the 
[release page](https://github.com/140378476/Ancono/releases). 

## Language
Ancono is written by both Java and [Kotlin](https://kotlinlang.org) and you can use it with Java only. However, some 
extra features (such as operator override) are only possible if you use Kotlin. 

## Dependencies
Core:
* Java: JDK 14
* Kotlin: 1.4

Test:
* JUnit 4

## Related project
[AnconoGraphic](https://github.com/140378476/AnconoGraphic) provides graphic extension of this library.  

## Development
Project Ancono welcomes anyone to join in the development. 

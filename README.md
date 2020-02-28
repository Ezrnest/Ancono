
#Ancono

A Java-based Math library.

---

## Introduction

Ancono provides a wide range of basic and advanced math tools, focusing both on simplicity and efficiency. 
This library includes the following modules: 

1. Math functions: 
    
    power, gcd, lcm, mod, power and mod, Miller-Rabin prime test and more...
    
2. Number models:
 
    Ancono provides classes for fraction, complex and so on. Users can choose their desired 
    number models and operate the numbers with build-in methods. It is also possible for users 
    to customize or add new number models, and they can be used with full compatibility.
    See more <a href="#numbers">here</a>.
    
3. Polynomials, multinomials and expression:

    Ancono enable users to operate polynomials and multinomials on a field. It is also possible to compute 
    and simplify complex expressions. See more <a href="#expressions">here</a>.

4. Linear algebra

    Ancono supports computations of matrices and vectors on a field. Linear spaces and linear transformations
    are also included. 
    
5. Plane analytic geometry
    
    Line, triangle, conic sections (including circle, ellipse, hyperbola and parabola) and affine transformations 
    are provided in Ancono. 
    Useful methods are available, including computing the intersection points, computing the 4 kinds of centers 
    of a triangle and so much more.  
     
6. Number theory and Combinations
    
    Ancono provides useful tools for number theory and combination, such as enumerating primes, factorization and 
    permutations.

7. Logic
    
    With Ancono, user can construct and operate formulas of proposition logic and first order logic. 
    
8. Calculus

    Utilities for derivatives, Taylor series and so on are provided.
    
9. And more...
## Examples

### <a name="numbers">Using number models</a>: 

Using Fraction:
```java
Fraction a = Fraction.valueOf("1/2");
System.out.println(a);
Fraction b = Fraction.ONE;
var c = a.add(b);
c = c.subtract(Fraction.ZERO);
System.out.println(c);
c = c.multiply(a);
System.out.println(c);
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

### <a name="polynomials">Polynomials:</a>

Multiplication:
```java
var cal = Calculators.getCalDouble();
// we use double as the type of the coefficient of the polynomials  
var f = Polynomial.valueOf(cal, 1.0, 1.0, 2.0, 3.0); // 1 + x + 2x^2 + 3 x^3
var g = Polynomial.binomialPower(2.0, 3, cal); // (x-2)^3
System.out.println(f);
System.out.println(g);
var h = f.multiply(g);
System.out.println(h);
```


### <a name="expressions">Using Expression</a>
```java
var cal = Expression.getCalculator();
var f1 = cal.parseExpr("(x^2+3x+2)/(x+1)+sin(Pi/2)+exp(t)");
System.out.println(f1);
var f2 = cal.parseExpr("y+1");
System.out.println(f2);
var f3 = cal.divide(f1, f2);
System.out.println(f3);
```



## Number models

## Usage
Users can utilize this library by simply import the jar file downloaded from /out/artifacts  




## Development
Project Ancono welcomes anyone to join in the development. 
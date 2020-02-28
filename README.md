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
    
    
3. Polynomials, multinomials and expression:

    Ancono enable users to operate polynomials and multinomials on a field.

          
    
## Examples

### Using number models:

Using Fraction:
```
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
```
var cal = Calculators.getCalDouble();
Complex<Double> z1 = Complex.real(1.0, cal);
z1 = z1.squareRoot();
Complex<Double> z2 = Complex.of(1.0, 2.0, cal);
Complex<Double> z3 = z1.multiply(z2);
System.out.println(z3);
//Result: (1.0)+(2.0)i
```


## Number models

## Usage
Users can utilize this library by simply import the jar file downloaded from /out/artifacts  




## Development
Project Ancono welcomes anyone to join in the development. 
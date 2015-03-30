package kotlinfp.funfun

import org.junit.Assert.*
import org.junit.Test as test


// Part 1: simple experiments
// more or less direct port from http://cpptruths.blogspot.de/2014/03/fun-with-lambdas-c14-style-part-1.html

class part1_simple {

    // 1. Write an Identity function that takes an argument and returns the same argument.

    fun identity<T>(x: T) = x

    test fun _1_identity() =  assertEquals(identity(3), 3)

    // 2. Write 3 functions add, sub, and mul that take 2 parameters each and return their sum, difference, and product respectively.
    // Note that it is not possible to express them generically in kotlin, so they are written with pointless upper bound just to refer to some hypothetical solution
    // Some combination of changes needed in Kotlin in order to be able to write generic functions like this
    //   - metaprogramming in a form of "resolving" generic functions at call site
    //   - dynamic resolving of extension methods
    //   - extension traits
    // These are function generators, because so far it is not possible to make a callable reference from a generic function

    fun add<T: Int>() = { x: T, y: T -> x + y }
    fun sub<T: Int>() = { x: T, y: T -> x - y }
    fun mul<T: Int>() = { x: T, y: T -> x * y }

    // 3. Write a function, identityf, that takes an argument and returns a function that returns that argument.

    fun identityf<T>(x: T) = { x }

    test fun _3_identityf() = assertEquals(identityf(5)(), 5)

    // 4. Write a function that produces a function that returns values in a range.
    // Note: the same problem as with 2, even worse, since we need to define type of i

    fun fromto<T: Int>(from: T, to: T): () -> Int {
        var i: Int = from
        return { if (i < to) i = i + 1; i }
    }

    test fun _4_range() {
        val range = fromto(0, 10)
        assertEquals(range(), 1)
        assertEquals(range(), 2)
        assertEquals(range(), 3)
    }

    // 5. Write a function that adds from two invocations.
    // Note: the same problem as with 2.

    fun addf<T: Int>(x: T) = fun (y: T) = x.plus(y)

    test fun _5_addf() = assertEquals(addf(3)(4), 7)

    // 6. Write a function swap that swaps the arguments of a binary function.

    fun swap<T>(f: (T, T) -> T) = fun (x: T, y: T) = f(y, x)

    test fun _6_swap() = assertEquals(swap(sub<Int>())(3,2), -1)

    // 7. Write a function twice that takes a binary function and returns a unary
    //    function that passes its argument to the binary function twice.

    fun twice<T>(f: (T, T) -> T) = fun (x: T) = f(x, x)

    test fun _7_twice() = assertEquals(twice(add<Int>())(11), 22)

    // 8. Write a function that takes a binary function and makes it callable with two invocations.

    fun applyf<T>(f: (T, T) -> T) = fun (x: T) = fun (y: T) = f(x, y)

    test fun _8_applyf() = assertEquals(applyf(mul<Int>())(3)(4), 12)

    // 9. Write a function that takes a function and an argument and returns a function that takes
    //    the second argument and applies the function.

    fun curry<T>(f: (T, T) -> T, x: T) = fun (y: T) = f(x, y)

    test fun _9_curry() = assertEquals(curry(mul<Int>(),5)(6), 30)

    // 10. Partial function application
    // addFour is a generator too, see comment at 2. And same bound problem as in 2 here.

    fun addFour<T: Int>() = fun (a: T, b: T, c: T, d: T) = a + b + c + d

    fun partialFourToTwo<T>(f: (T, T, T, T) -> T, a: T, b: T) = fun (c: T, d: T) = f(a, b, c, d)

    test fun _10_partialFourToTwo() = assertEquals(partialFourToTwo( addFour<Int>(), 1, 2)(3, 4), 10)

    // 11. Without creating a new function show 3 ways to create the inc function.

    val inc1 = curry(add<Int>(), 1)
    val inc2 = addf(1)
    val inc3 = applyf(add<Int>())(1)

    test fun _11_inc() {
        assertEquals(inc1(7), 8)
        assertEquals(inc2(7), 8)
        assertEquals(inc3(7), 8)
    }

    // 12. Write a function composeu that takes two unary functions and returns a unary function that calls them both.

    fun composeu<T>(f1: (T) -> T, f2: (T) -> T) = fun (x: T) = f2(f1(x))

    test fun _12_composeu() = assertEquals(composeu(inc1, curry(mul<Int>(), 5))(3), 20)

    // 13. Write a function that returns a function that allows a binary function to be called exactly once.

    fun once<T>(binary: (T, T) -> T): (T, T) -> T {
        var done = false
        return fun(x: T, y: T) =
                if (!done) { done = true; binary(x, y) }
                else throw java.lang.Exception("already called")
    }

    test fun _13_once() = assertEquals(once(add<Int>())(3,4), 7)

    // 14. Write a function that takes a binary function and returns a function that takes two arguments and a callback.

    fun binaryc<T>(binary: (T, T) -> T) = fun (x: T, y: T, f: (T) -> T) = f(binary(x,y))

    test fun _14_binaryc() = assertEquals(binaryc(mul<Int>())(5, 6, inc1), 31)

    // 15. Write 3 functions:
    //       unit – same as Identityf
    //       stringify – that stringifies its argument and applies unit to it
    //       bind – that takes a result of unit and returns a function that takes a callback and returns
    //          the result of callback applied to the result of unit.

    fun unit<T>(x: T) = { x }
    fun stringify<T>(x: T) = unit(x.toString())
    class bind<T>(val u: () -> T) {
        public fun invoke<R>(callback: (T) -> () -> R): () -> R = callback(u())
    }
    // just helpers to reduce boilerplate (since we cannot make callable reference out of generic function)
    fun genStringify<T>() = fun (x: T) = stringify(x)
    fun genUnit<T>() = fun (x: T) = unit(x)

    test fun _15_unit_stringify_bind() {
        assertEquals(stringify(15)(), bind(unit(15))(genStringify<Int>())())
        assertEquals(stringify(5)(), bind(stringify(5))(genUnit<String>())())
    }

}

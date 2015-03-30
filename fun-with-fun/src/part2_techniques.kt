package kotlinfp.funfun

import org.junit.Assert.*
import org.junit.Test as test


// Part 2: some useful techniques
// port of some stuff from http://cpptruths.blogspot.de/2014/05/fun-with-lambdas-c14-style-part-2.html
// (the skipped part is either irrelevant or not [easily] portable)

class part2_techniques {

    // 1. Recursive lambdas

    fun make_fibo() = fun (n: Int): Int {
        var f: kotlin.Function1<Int, Int>? = null
        f = fun (n: Int) = if (n <= 2) 1 else f!!(n-1) + f!!(n-2)
        return f!!(n)
    }

    test fun _1_fibo() = assertEquals(make_fibo()(10), 55)

    // 2. Memoization

    fun memoize1<R, T1>(f: (T1) -> R): (T1) -> R {
        val cache = hashMapOf<T1, R>()
        return fun (x: T1): R {
            var v = cache.get(x)
            if (v == null) {
                v = f(x)
                cache.put(x, v)
            }
            return v
        }
    }

    test fun _2_memoize() {
        var memtest_called = false

        fun memtest1(x: Int): Int {
            memtest_called = true
            return x * 2
        }

        val memoizedTest = memoize1(::memtest1)

        memtest_called = false
        assertEquals(memoizedTest(3), 6)
        assertTrue(memtest_called)

        memtest_called = false
        assertEquals(memoizedTest(3), 6)
        assertFalse(memtest_called)
    }
}


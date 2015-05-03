package kotlinfp.funfun

import org.junit.Assert.*
import org.junit.Test as test

// Part 3: monads
// attempt to port working parts from http://cpptruths.blogspot.de/2014/08/fun-with-lambdas-c14-style-part-3.html

class part3_monads {

    // 1. list monad
    // two problems here - lack of tuples prevent to use heterogeneous list, and more importantly a need to use
    // helper m() function that actually returns the monad allowing to specify/deduce return type

    class list<T>(val data: Iterable<T>) {
        constructor(vararg arg: T) : this(arg.asIterable()) {}
        // this is the desired function that would make list<T> a monad, but it doesn't work, may be due to generic param
        //public fun invoke<R>(access: (a: Iterable<T>) -> R): R = access(data)
        // so this function is actually returns list monad, allowing also to specify or deduce result type
        public fun m<R>(): ((Iterable<T>) -> R) -> R = fun (access: (a: Iterable<T>) -> R): R = access(data)
    }

    fun head<T>(xs: (access: (a: Iterable<T>) -> T) -> T ) : T = xs({ a: Iterable<T> -> a.first() })
    fun tail<T>(xs: (access: (a: Iterable<T>) -> Iterable<T>) -> Iterable<T>): Iterable<T> = xs({ a: Iterable<T> -> a.drop(1) })
    fun length<T>(xs: (access: (a: Iterable<T>) -> Int) -> Int ) : Int = xs({ a: Iterable<T> -> a.count() })

    public fun Iterable<Any>.length(xs: (p: (i: Iterable<Any>) -> Any) -> Int): Int = xs({ i: Iterable<Any> -> i.count() })

    test fun _1_list() {
        val l1 = list(1, 3.0, "123")
        val len1 = length(l1.m())
        assertEquals(len1 as Int, 3)
        val h1 = head(l1.m())
        assertEquals(h1 as Int, 1)
        val t1 = tail(l1.m()).toList()
        assertEquals(t1, listOf(3.0, "123"))
    }

    // 2. Continuator / forward composition
    // (same implementation as andThen from funKtionale - https://github.com/MarioAriasC/funKTionale/wiki/Function-composition)

    fun<P1, R1, R2> Function1<P1, R1>.andThen(fn: (R1) -> R2): (P1) -> R2 = { fn(this(it)) }

    test fun _2_cont() {
        val plus3 = { i: Int -> i + 3 }
        val double = { i: Int -> i * 2 }
        assertEquals( (plus3 andThen double)(1), 8)
    }

    // 3. Functor
    // fmap sample for list monad
    // desired signature is (((A) -> B) -> listM<A>) -> listM<B>
    // but due to lack of some features list<X> types are returned, requiring to call m() at places

    fun<A, B> fmap(fn: (A) -> B) = fun (alistm: ((Iterable<A>) -> list<B>) -> list<B> ) = alistm({ la: Iterable<A> -> list<B>(la.map(fn)) })

    test fun _3_functor() {
        val twice = fun (i: Int) = i * 2
        val li = list( 1, 2, 3)
        val l2 = fmap(twice)(li.m())
        val t1 = tail(l2.m()).toList()
        assertEquals(t1, listOf(4, 6))
    }

}
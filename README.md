# DBC-assertions
Java unit-testing assertions to assist a *Design By Contract* style of programming.
And in particular, to ensure that the *Liskov substitution principle* is honoured.

This library should be test framework agnostic;
it should work with any test framework that recognizes an `AssertionError` exception as indicating a test failure.
It delegates to [Hamcrest](http://hamcrest.org/) for checking assertions,
and directly throws [opentest4j](https://github.com/ota4j-team/opentest4j) assertions,
rather than using a test framework.
It should not pull in a dependency on a test framework.

## License

This is licensed using the *Eclipse Public License - v 2.0*.
That is deliberately the same [license as JUnit 5](https://github.com/junit-team/junit5/blob/main/LICENSE.md),
so if you are happy to use *JUnit 5* for your unit tests, you should have no licensing objection to using this library.

## Design by Contract

[Design by Contract](https://en.wikipedia.org/wiki/Design_by_contract)
is a software design and programming technique
in which program behaviour is specified in terms of precise
[preconditions](https://en.wikipedia.org/wiki/Precondition),
[postconditions](https://en.wikipedia.org/wiki/Postcondition) and
[invariants](https://en.wikipedia.org/wiki/Invariant).
Post conditions are *assertions* about the visible (public) state of an item after an operation.
Invariants are *assertions* that are true before and after an operation.
Some invariants apply to all operations, and to the state after creation (construction) of the item;
sometimes the term *invariant* is applied to only those kinds of invariants;
when it is not, these are sometimes called *class invariants*.

Design by Contract is a helpful approach when writing automated unit tests,
because postconditions and invariants
can be directly and easily recast into
assertions in the unit test code.

The [Liskov substitution principle](https://en.wikipedia.org/wiki/Liskov_substitution_principle)
is an important principle for object oriented programming,
and thus for all Java programming.
It requires that objects of a derived class can be correctly used
in any place where an object of the base class can be used.
That in turn requires that a derived class maintains all the invariants of its base class
(but may have further constraints),
and that the operations (mutators) meet all the postconditions specified for that operation of the base class.

The *Liskov substitution principle* is important for unit testing of Java classes
because all Java class are derived from the `Object` class **and**
the `Object` class specifies some invariants.
Furthermore, several commonly implemented interfaces of standard Java,
such as `Comparable`,
specify further invariants and postconditions that the principle requires an implementing class to meet.

## Using this Library

In your unit tests of a class you might have test code similar to this:

```
@Test
public void increment_1() {
   final var amount = new Amount(1L);

   amount.increment();

   assertEquals(2L, amount.longValue());
}

@Test
public void compareTo_1_2() {
   final var a1 = new Amount(1L);
   final var a2 = new Amount(2L);

   assertTrue(a1.compareTo(a2) < 0);
}
```

### Add Calls to this Library

But you can do do better than that.
The class you are testing does not only have the behaviour that you have specified for it.
It must also conform to some invariants imposed by the `Object` base class,
and also for any interfaces your class implements.
And you probably specify that the class has either *value semantics* or *entity semantics*.
You should also check that the objects conform to those invariants.
There are several of them. Checking them can be fiddly.
Explicitly checking them all directly in your test method would be verbose, error prone,
and in some cases provide low value
(because in that particular test, it is unlikely that the invariant would be broken).

The methods of this library provide a convenient and abstract way to check that
objects conform to those invariants.
In your tests, simply delegate to methods in this library, like this:

```
@Test
public void increment_1() {
   final var amount = new Amount(1L);

   amount.increment();

   ObjectTest.assertInvariants(amount);
   ComparableTest.assertInvariants(amount);
   assertEquals(2, amount.longValue());
}

@Test
public void compareTo_1_2() {
   final var a1 = new Amount(1L);
   final var a2 = new Amount(2L);

   ObjectTest.assertInvariants(a1, a2);
   ComparableTest.assertInvariants(a1, a2);
   ComparableTest.assertNaturalOrderingIsConsistentWithEquals(a1, a2);
   EqualsSemanticsTest.assertLongValueSemantics(a1, a2, "longValue", (amount) -> amount.longValue());
   assertTrue(a1.compareTo(a2) < 0);
}
```

### Refactor

You will probably find your tests perform many similar calls to the methods of this library.
You might therefore consider refactoring your test code to extract methods that reduce duplication in your test code,
and increase abstraction. Like this:

```
private static assertInvariants(Amount a) {
   ObjectTest.assertInvariants(a);
   ComparableTest.assertInvariants(a);
}

private static assertInvariants(Amount a1, Amount a2) {
   ObjectTest.assertInvariants(a1, a2);
   ComparableTest.assertInvariants(a1, a2);
   ComparableTest.assertNaturalOrderingIsConsistentWithEquals(a1, a2);
   EqualsSemanticsTest.assertLongValueSemantics(a1, a2, "longValue", (amount) -> amount.longValue());
}

@Test
public void increment_1() {
   final var amount = new Amount(1L);

   amount.increment();

   assertInvariants(amount);
   assertEquals(2, amount.longValue());
}

@Test
public void compareTo_1_2() {
   final var a1 = new Amount(1L);
   final var a2 = new Amount(2L);

   assertInvariants(a1, a2);
   assertTrue(a1.compareTo(a2) < 0);
}
```

## Technologies Used

* Java 11
* Annotations:
    * [SpotBugs annotations](https://javadoc.io/doc/com.github.spotbugs/spotbugs-annotations)
* Development environment:
    * [IntelliJ IDEA](https://www.jetbrains.com/idea/)
    * previously [Eclipse IDE](https://www.eclipse.org/ide/)
    * previously [Jenkins Editor](https://github.com/de-jcup/eclipse-jenkins-editor)
    * previously [SpotBugs Eclipse plugin](https://marketplace.eclipse.org/content/spotbugs-eclipse-plugin)
* Software configuration management:
     * [Git](https://git-scm.com/)
     * [GitHub](https://github.com)
* Building:
    * [Maven](https://maven.apache.org/)
    * [SpotBugs Maven plugin](https://spotbugs.github.io/spotbugs-maven-plugin/index.html)
    * [Jenkins](https://jenkins.io/)
    * [Ubuntu](http://ubuntu.com)
* Static analysis and testing of the library itself:
    * [JUnit 5](https://junit.org/junit5/)
    * [Java Hamcrest](http://hamcrest.org/JavaHamcrest/)
    * [Open Test Alliance for the JVM opentest4j](https://github.com/ota4j-team/opentest4j)
    * [SpotBugs](https://spotbugs.github.io/)

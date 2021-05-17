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
Post conditions are *assertions* that about the visible (public) state of an item after an operation.
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

## Technologies Used

* Java 11
* Annotations:
    * [SpotBugs annotations](https://javadoc.io/doc/com.github.spotbugs/spotbugs-annotations)
* Development environment:
    * [Eclipse IDE](https://www.eclipse.org/ide/)
    * [Jenkins Editor](https://github.com/de-jcup/eclipse-jenkins-editor)
    * [SpotBugs Eclipse plugin](https://marketplace.eclipse.org/content/spotbugs-eclipse-plugin)
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

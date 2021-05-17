# DBC-assertions
Java unit-testing assertions to assist a Design By Contract style of programming.
And in particular, to ensure that the *Liskov Substitution Principle* is honoured.

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
* Static analysis and testing:
    * [JUnit 5](https://junit.org/junit5/)
    * [Java Hamcrest](http://hamcrest.org/JavaHamcrest/)
    * [Open Test Alliance for the JVM](https://github.com/ota4j-team/opentest4j)
    * [SpotBugs](https://spotbugs.github.io/)

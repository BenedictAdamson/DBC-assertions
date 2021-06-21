package uk.badamson.dbc.assertions;
/*
 * Copyright 2018,2021 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * https://www.eclipse.org/legal/epl-v20.html
 */

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.opentest4j.AssertionFailedError;

import javax.annotation.Nonnull;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;

/**
 * <p>
 * Verification methods to assist in unit testing classes that implement the
 * {@link Comparable} interface.
 */
@SuppressFBWarnings(justification = "Checking exceptions", value = "RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
public final class ComparableVerifier {

    private ComparableVerifier() {
        assert false;// must not instance
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static <T extends Comparable<T>> void assertCompareToNullThrowsNPE(@Nonnull final T object) {
        try {
            //noinspection ConstantConditions
            object.compareTo(null);
        } catch (final NullPointerException e) {
            return;// OK: the required behaviour
        } catch (final Exception e) {
            /*
             * It is unlikely that a faulty compareTo would throw any other kind of
             * exception, but provide good diagnostics just in case.
             */
            throw new AssertionError("compareToNull(null) throws only a NullPointerException", e);
        }
        /*
         * An overly careful implementation might attempt to give a result for this
         * case, rather than throw a NPE.
         */
        throw new AssertionFailedError("compareTo(null) throws NullPointerException");
    }

    /**
     * <p>
     * Provide a {@linkplain Matcher matcher}
     * that matches if, and only if, the object being matched
     * satisfies all the invariants imposed by the
     * {@link Comparable} interface.
     * not.
     * </p>
     *
     * <h2>How to Use this Method</h2>
     * <p>
     * In your unit tests of mutators and constructors of classes that implement the
     * {@link Comparable} class, you will check that the mutators and constructors
     * make the desired changes, using test code similar to this:
     * </p>
     *
     * <pre>
     * {@code @Test}
     * public void increment_1() {
     *    final var amount = new Amount(1);
     *
     *    amount.increment();
     *
     *    assertThat(amount.intValue(), is(2));
     * }
     * </pre>
     *
     * <p>
     * But you can do do better than that. The class you are testing does not only
     * have the behaviour that you have specified for it. It must also conform to
     * some invariants imposed by the {@link Comparable} interface. You should also
     * check that the mutated object (still) conforms to those invariants. There are
     * a couple of them. Checking them can be fiddly. Explicitly checking them all
     * directly in your test method would be verbose, error prone, and in some cases
     * provide low value (because in that particular test, it is unlikely that the
     * invariant would be broken).
     * <p>
     * </p>
     * This method provides a convenient and abstract way to check that the mutated
     * object still conforms to the invariants imposed by the {@link Comparable}
     * interface. Simply delegate to this method in your test, like this:
     * </p>
     *
     * <pre>
     * {@code @Test}
     * public void increment_1() {
     *    final var amount = new Amount(1);
     *
     *    amount.increment();
     *
     *    assertThat(amount, ObjectVerifier.satisfiesInvariants());
     *    assertThat(amount, ComparableVerifier.satisfiesInvariants());
     *    assertThat(amount.intValue(), is(2));
     * }
     * </pre>
     */
    public static <T extends Comparable<T>> Matcher<T> satisfiesInvariants() {
        return Matchers.describedAs("satisfies Comparable interface invariants",
                allOf(
                        new CompareToNullThrowsNPE<T>(),
                        /*
                         * For completeness, check that this.compareTo(this) does not throw an
                         * exception, although it is unlikely that a faulty implementation would throw
                         * an exception.
                         */
                        new CompareToSelfDoesNotThrowException<T>()
                ));
    }

    /**
     * <p>
     * Assert that a given object conforms to all the invariants imposed by the
     * {@link Comparable} interface, throwing an {@link AssertionError} if it does
     * not.
     * </p>
     * <p>
     * This is a convenience method, equivalent to {@code assertThat(object, ComparableVerifier.satisfiesInvariants())}
     * </p>
     *
     * @param <T>    The class of {@code object}
     * @param object The object to test.
     * @throws NullPointerException If {@code object} is null.
     * @throws AssertionError       If {@code object} breaks an invariant.
     */
    public static <T extends Comparable<T>> void assertInvariants(@Nonnull final T object) {
        Objects.requireNonNull(object, "object");
        assertThat(object, satisfiesInvariants());
    }

    /**
     * <p>
     * Provide a {@linkplain Matcher matcher}
     * that matches if, and only if, the object being matched
     * satisfies all the  relationship (pairwise)
     * invariants imposed by the  {@link Comparable} interface.
     * </p>
     *
     * <h2>How to Use this Method</h2>
     * <p>
     * If the type you are testing implements the {@link Comparable} interface, you
     * will be testing that your {@link Comparable#compareTo(Object)} method is
     * correct, by constructing significant pairs of objects, then checking that
     * their comparison is as expected, using test code similar to this:
     * </p>
     *
     * <pre>
     * {@code @Test}
     * public void compareTo_1_2() {
     *    final var a1 = new Amount(1);
     *    final var a2 = new Amount(2);
     *
     *    {@code assertThat(a1.compareTo(a2) < 0);}
     * }
     * </pre>
     *
     * <p>
     * But you can do do better than that. The class you are testing does not only
     * have the behaviour that you have specified for it. It must also conform to
     * some invariants imposed by the {@link Comparable} interface. You should check
     * that the objects conform to those invariants. There are couple of them.
     * Explicitly checking them all directly in your test method would be verbose,
     * error prone, and in some cases provide low value (because in that particular
     * test, it is unlikely that the invariant would be broken).
     * <p>
     * </p>
     * This method provides a convenient and abstract way to check that the objects
     * conform to the relationship invariants imposed by the {@link Comparable}
     * class. Simply delegate to this method in your test, like this:
     * </p>
     *
     * <pre>
     * {@code @Test}
     * public void compareTo_1_2() {
     *    final var a1 = new Amount(1);
     *    final var a2 = new Amount(2);
     *
     *    assertThat(a1, ObjectVerifier.satisfiesInvariantsWith(a2));
     *    assertThat(a1, ComparableVerifier.satisfiesInvariantsWith(a2));
     *    {@code assertThat(a1.compareTo(a2) < 0);}
     * }
     * </pre>
     * <p>
     * In many cases you will also want to delegate to the
     * {@link #assertNaturalOrderingIsConsistentWithEquals(Comparable, Comparable)}
     * method.
     * </p>
     *
     * @see #assertNaturalOrderingIsConsistentWithEquals(Comparable, Comparable)
     */
    public static <T extends Comparable<T>> Matcher<T> satisfiesInvariantsWith(@Nonnull T other) {
        Objects.requireNonNull(other, "other");
        return Matchers.describedAs("satisfies pairwise Comparable interface invariants with " + ObjectVerifier.safeToString(other), allOf(
                new CompareToIsSymmetric<T>(other)
        ));
    }

    /**
     * <p>
     * Assert that a pair of objects conform to all the relationship (pairwise)
     * invariants imposed by the {@link Comparable} interface, throwing an
     * {@link AssertionError} if they do not.
     * </p>
     * <p>
     * This is a convenience method, equivalent to {@code assertThat(object1, ComparableVerifier.satisfiesInvariantsWith(object2))}
     * </p>
     */
    public static <T extends Comparable<T>> void assertInvariants(@Nonnull final T object1, @Nonnull final T object2) {
        assertThat(object1, satisfiesInvariantsWith(object2));
    }

    /**
     * <p>
     * Assert that a triplet of objects conform to the relationship invariant
     * imposed by the {@link Comparable} interface, throwing an
     * {@link AssertionError} if they do not.
     * </p>
     *
     * <p>
     * This is a supplement to the {@link #assertInvariants(Comparable, Comparable)}
     * method, provided out of completeness, for use in very thorough unit tests. In
     * practice, its invariants are unlikely to be broken if the
     * {@link #assertInvariants(Comparable, Comparable)} invariants are all met.
     * </p>
     *
     * @param <T>     The class of {@code object1}, {@code object2} and {@code object3}
     * @param object1 An object to test.
     * @param object2 An object to test.
     * @param object3 An object to test.
     * @throws NullPointerException <ul>
     *                              <li>If {@code object1} is null.</li>
     *                              <li>If {@code object2} is null.</li>
     *                              <li>If {@code object3} is null.</li>
     *                              </ul>
     * @throws AssertionError       If {@code object1}, {@code object2} and {@code object3} break an
     *                              invariant.
     * @see #assertInvariants(Comparable, Comparable)
     */
    public static <T extends Comparable<T>> void assertInvariants(@Nonnull final T object1, @Nonnull final T object2,
                                                                  @Nonnull final T object3) {
        Objects.requireNonNull(object1, "object1");
        Objects.requireNonNull(object2, "object2");
        Objects.requireNonNull(object3, "object3");

        /*
         * Provide good diagnostics if compareTo throws an exception.
         */
        final int c12 = compareTo(object1, object2);
        final int c23 = compareTo(object2, object3);
        final int c13 = compareTo(object1, object3);

        assertThat("compareTo is transitive [" + ObjectVerifier.safeToString(object1) + ", "
                        + ObjectVerifier.safeToString(object2) + ", " + ObjectVerifier.safeToString(object3) + "]",
                !(c12 > 0 && c23 > 0 && !(c13 > 0)));
    }

    /**
     * <p>
     * Assert that a pair of objects conform to the relationship (pairwise)
     * invariant required for their {@link Comparable#compareTo(Object)} method to
     * be consistent with {@linkplain Object#equals(Object)}, throwing an
     * {@link AssertionError} if they do not.
     * </p>
     *
     * <p>
     * This is a supplement to the {@link #assertInvariants(Comparable, Comparable)}
     * method, for use in the typical case that the natural ordering is consistent
     * with equals.
     * </p>
     *
     * @param <T>     The class of {@code object1} and {@code object2}
     * @param object1 An object to test.
     * @param object2 An object to test.
     * @throws NullPointerException <ul>
     *                              <li>If {@code object1} is null.</li>
     *                              <li>If {@code object2} is null.</li>
     *                              </ul>
     * @throws AssertionError       If {@code object1} and {@code object2} break the invariant.
     * @see #assertInvariants(Comparable, Comparable)
     */
    public static <T extends Comparable<T>> void assertNaturalOrderingIsConsistentWithEquals(@Nonnull final T object1,
                                                                                             @Nonnull final T object2) {
        Objects.requireNonNull(object1, "object1");
        Objects.requireNonNull(object2, "object2");

        /*
         * Provide good diagnostics if compareTo or equals throws an exception.
         */
        final var compareTo = compareTo(object1, object2);
        final var equals = ObjectVerifier.equals(object1, object2);

        assertThat("Natural ordering is consistent with equals [" + ObjectVerifier.safeToString(object1) + ", "
                + ObjectVerifier.safeToString(object2) + "]", compareTo == 0 == equals);
    }

    private static <T extends Comparable<T>> int compareTo(@Nonnull final T object1, @Nonnull final T object2) {
        try {
            return object1.compareTo(object2);
        } catch (final Exception e) {
            /*
             * A typical compareTo implementation will delegate to the compareTo methods of
             * some attributes of the object. A naive implementation might throw a
             * NullPointerException if the object has any null attributes.
             */
            throw new AssertionError("compareTo must not throw exceptions for non null objects of the same class ["
                    + ObjectVerifier.safeToString(object1) + ", " + ObjectVerifier.safeToString(object2) + "]", e);
        }
    }

    private static final class CompareToIsSymmetric<T extends Comparable<T>> extends PairwiseMatcher<T> {
        CompareToIsSymmetric(@Nonnull T other) {
            super(other);
        }

        @Override
        protected boolean matchesSafely(@Nonnull T item1, @Nonnull T item2, @Nonnull Description mismatchDescription) {
            final int c12;
            final int c21;
            try {
                c12 = item1.compareTo(item2);
                c21 = item2.compareTo(item1);
            } catch (Exception e) {
                mismatchDescription.appendText("but compareTo() threw exception ");
                mismatchDescription.appendValue(e);
                return false;
            }
            final boolean ok = Integer.signum(c12) == -Integer.signum(c21);
            return ok;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("compareTo is symmetric");
            description.appendValue(description);
        }
    }// class

    private static final class CompareToNullThrowsNPE<T extends Comparable<T>> extends TypeSafeDiagnosingMatcher<T> {
        @Override
        protected boolean matchesSafely(T item, Description mismatchDescription) {
            try {
                //noinspection ConstantConditions
                item.compareTo(null);
            } catch (final NullPointerException e) {
                return true;// the required behaviour
            } catch (final Exception e) {
                /*
                 * It is unlikely that a faulty compareTo would throw any other kind of
                 * exception, but provide good diagnostics just in case.
                 */
                mismatchDescription.appendText("but threw a ");
                mismatchDescription.appendValue(e);
                return false;
            }
            mismatchDescription.appendText("but did not throw an exception");
            return false;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("compareToNull(null) throws a NullPointerException");
        }
    }// class

    private static final class CompareToSelfDoesNotThrowException<T extends Comparable<T>> extends MethodDoesNotThrowException<T> {
        @Override
        protected void callMethod(@Nonnull T item) throws Throwable {
            item.compareTo(item);
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("this.compareTo(this) does not throw an exception");
        }
    }// class

}

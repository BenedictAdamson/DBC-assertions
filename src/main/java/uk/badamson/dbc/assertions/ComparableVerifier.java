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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
        return Matchers.describedAs("satisfies pairwise Comparable interface invariants with " + Safe.toString(other), allOf(
                new CompareToIsSymmetric<>(other)
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
     * Provide a {@linkplain Matcher matcher}
     * that matches if, and only if, the object being matched
     * satisfies all the  three-way relationship
     * invariants imposed by the  {@link Comparable} interface.
     * </p>
     *
     * <p>
     * This is a supplement to the {@link #satisfiesInvariantsWith(Comparable)}
     * method, provided out of completeness, for use in very thorough unit tests. In
     * practice, its invariants are unlikely to be broken if the
     * {@link #satisfiesInvariantsWith(Comparable)} invariants are all met.
     * </p>
     */
    public static <T extends Comparable<T>> Matcher<T> satisfiesInvariantsWith(@Nonnull T item2, @Nonnull T item3) {
        return new CompareToIsTransitive<>(item2, item3);
    }

    /**
     * <p>
     * Assert that a triplet of objects conform to the relationship invariant
     * imposed by the {@link Comparable} interface, throwing an
     * {@link AssertionError} if they do not.
     * </p>
     * <p>
     * This is a convenience method, equivalent to {@code assertThat(object1, ComparableVerifier.satisfiesInvariantsWith(object2, object3))}
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
        assertThat(object1, satisfiesInvariantsWith(object2, object3));
    }

    /**
     * <p>
     * Provide a {@linkplain Matcher matcher}
     * that matches if, and only if, the object being matched
     * satisfies  the relationship (pairwise)
     * invariant required for the {@link Comparable#compareTo(Object)} method to
     * be consistent with {@linkplain Object#equals(Object)}.
     * </p>
     *
     * <p>
     * This is a supplement to the {@link #satisfiesInvariantsWith(Comparable)}
     * method, for use in the typical case that the natural ordering is consistent
     * with equals.
     * </p>
     */
    public static <T extends Comparable<T>> Matcher<T> naturalOrderingIsConsistentWithEqualsWith(@Nonnull T other) {
        return new NaturalOrderingIsConsistentWithEquals<>(other);
    }

    /**
     * <p>
     * Assert that a pair of objects conform to the relationship (pairwise)
     * invariant required for their {@link Comparable#compareTo(Object)} method to
     * be consistent with {@linkplain Object#equals(Object)}, throwing an
     * {@link AssertionError} if they do not.
     * </p>
     * <p>
     * This is a convenience method, equivalent to {@code assertThat(object1, ComparableVerifier.naturalOrderingIsConsistentWithEqualsWith(object2))}
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
        assertThat(object1, naturalOrderingIsConsistentWithEqualsWith(object2));
    }

    @Nullable
    private static <T extends Comparable<T>> Integer compareTo(@Nonnull T item1, @Nonnull T item2, @Nonnull Description mismatchDescription) {
        try {
            return item1.compareTo(item2);
        } catch (Exception e) {
            mismatchDescription.appendText("but compareTo() threw exception ");
            mismatchDescription.appendValue(e);
            return null;
        }
    }

    private static final class NaturalOrderingIsConsistentWithEquals<T extends Comparable<T>> extends PairMatcher<T> {
        NaturalOrderingIsConsistentWithEquals(@Nonnull T other) {
            super(other);
        }

        @Override
        protected boolean matchesSafely(@Nonnull T item1, @Nonnull T item2, @Nonnull Description mismatchDescription) {
            boolean ok = true;
            final Integer compareTo = ComparableVerifier.compareTo(item1, item2, mismatchDescription);
            ok = ok && (compareTo != null);
            final Boolean equals = Safe.equals(item1, item2, mismatchDescription);
            ok = ok && (equals != null);
            if (ok && !(compareTo == 0 == equals)) {
                mismatchDescription.appendText("not satisfied");
                ok = false;
            }
            return ok;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("natural ordering is consistent with equals");
        }
    }// class

    private static class CompareToIsTransitive<T extends Comparable<T>> extends TripleMatcher<T> {
        CompareToIsTransitive(@Nonnull T item2, @Nonnull T item3) {
            super(item2, item3);
        }

        @Override
        protected boolean matchesSafely(@Nonnull T item1, @Nonnull T item2, @Nonnull T item3, @Nonnull Description mismatchDescription) {
            boolean ok = true;
            final Integer c12 = ComparableVerifier.compareTo(item1, item2, mismatchDescription);
            final Integer c23 = ComparableVerifier.compareTo(item2, item3, mismatchDescription);
            final Integer c13 = ComparableVerifier.compareTo(item1, item3, mismatchDescription);
            if (ok && c12 > 0 && c23 > 0 && !(c13 > 0)) {
                mismatchDescription.appendText("not satisfied");
                ok = false;
            }
            return ok;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("compareTo is transitive");
        }
    }// class

    private static final class CompareToIsSymmetric<T extends Comparable<T>> extends PairMatcher<T> {
        CompareToIsSymmetric(@Nonnull T other) {
            super(other);
        }

        @Override
        protected boolean matchesSafely(@Nonnull T item1, @Nonnull T item2, @Nonnull Description mismatchDescription) {
            boolean ok = true;
            final Integer c12 = ComparableVerifier.compareTo(item1, item2, mismatchDescription);
            final Integer c21 = ComparableVerifier.compareTo(item2, item1, mismatchDescription);
            ok = ok && c12 != null && c21 != null;
            if (ok && Integer.signum(c12) != -Integer.signum(c21)) {
                mismatchDescription.appendText("not satisfied");
                ok = false;
            }
            return ok;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("compareTo is symmetric");
            description.appendValue(description);
        }
    }// class

    private static final class CompareToNullThrowsNPE<T extends Comparable<T>> extends TypeSafeDiagnosingMatcher<T> {
        @SuppressWarnings({"ResultOfMethodCallIgnored", "ConstantConditions"})
        @Override
        protected boolean matchesSafely(T item, Description mismatchDescription) {
            try {
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
        @SuppressWarnings({"EqualsWithItself", "ResultOfMethodCallIgnored"})
        @Override
        protected void callMethod(@Nonnull T item) {
            item.compareTo(item);
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("this.compareTo(this) does not throw an exception");
        }
    }// class
}

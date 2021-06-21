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
 * Verification methods to assist in unit testing all classes (which are
 * directly or indirectly derived from the {@link Object} class).
 * </p>
 */
@SuppressFBWarnings(justification = "Checking contract", value = "EC_NULL_ARG")
public final class ObjectVerifier {

    ObjectVerifier() {
        assert false;// must not instance
    }

    /**
     * <p>
     * Provide a {@linkplain Matcher matcher}
     * that matches if, and only if, the object being matched
     * satisfies all the invariants imposed by the
     * {@link Object} base class.
     * </p>
     *
     * <h2>How to Use this Method</h2>
     * <p>
     * In your unit tests of mutators, you will check that the mutators make the
     * desired changes, using test code similar to this:
     * </p>
     *
     * <pre>
     * {@code @Test}
     * public void mutate_a() {
     *    final var thing = new Monster();
     *
     *    thing.mutate("a");
     *
     *    assertThat(thing.getSpecies(), is("a"));
     * }
     * </pre>
     *
     * <p>
     * But you can do do better than that. The class you are testing does not only
     * have the behaviour that you have specified for it. It must also conform to
     * some invariants imposed by the {@link Object} class. You should also check
     * that the mutated object (still) conforms to those invariants. There are
     * several of them. Checking them can be fiddly. Explicitly checking them all
     * directly in your test method would be verbose, error prone, and in some cases
     * provide low value (because in that particular test, it is unlikely that the
     * invariant would be broken).
     * <p>
     * </p>
     * This method provides a convenient and abstract way to check that the mutated
     * object still conforms to the invariants imposed by the {@link Object} class.
     * Simply delegate to this method in your test, like this:
     * </p>
     *
     * <pre>
     * {@code @Test}
     * public void mutate_a() {
     *    final var thing = new Monster();
     *
     *    thing.mutate("a");
     *
     *    assertThat(thing, ObjectVerifier.satisfiesInvariants());
     *    assertThat(thing.getSpecies(), is("a"));
     * }
     * </pre>
     *
     * <h2>How to Use this Method for Thorough Testing</h2>
     * <p>
     * The invariants imposed by {@link Object} pertain to the
     * {@link Object#equals(Object)} and {@link Object#hashCode()} methods. In
     * practice, only mishandling of {@code null} values is likely to cause code to
     * break those invariants. Therefore if you wish to thoroughly unit test your
     * code, you should have some test cases that set values to {@code null}. For
     * example:
     * </p>
     *
     * <pre>
     * {@code @Test}
     * public void mutate_a() {
     *    final var thing = new Monster();
     *
     *    thing.mutate(null);
     *
     *    assertThat(thing, ObjectVerifier.satisfiesInvariants());
     *    assertThat(thing.getSpecies(), nullValue());
     * }
     */
    @Nonnull
    public static Matcher<Object> satisfiesInvariants() {
        return Matchers.describedAs("satisfies Object class invariants",
                Matchers.allOf(
                        new ToStringDoesNotThrowException(),
                        new HashCodeDoesNotThrowException(),
                        new EqualsSelf(),
                        new NeverEqualsNull()
                ));
    }

    /**
     * <p>
     * Assert that a given object conforms to all the invariants imposed by the
     * {@link Object} base class, throwing an {@link AssertionError} if it does not.
     * </p>
     * <p>
     * This is a convenience method, equivalent to {@code assertThat(object, ObjectVerifier.satisfiesInvariants())}
     * </p>
     *
     * @param object The object to test.
     * @throws NullPointerException If {@code object} is null.
     * @throws AssertionError       If {@code object} breaks an invariant.
     */
    public static void assertInvariants(@Nonnull final Object object) {
        Objects.requireNonNull(object, "object");
        assertThat(object, satisfiesInvariants());
    }

    /**
     * <p>
     * Provide a {@linkplain Matcher matcher}
     * that matches if, and only if, the object being matched
     * satisfies all the  relationship (pairwise)
     * invariants imposed by the {@link Object} base class.
     * </p>
     *
     * <h2>How to Use this Method</h2>
     * <p>
     * In your unit tests, you will check factory methods and getters, using test
     * code similar to this:
     * </p>
     *
     * <pre>
     * {@code @Test}
     * public void spawn_a() {
     *    final var parent = new Monster();
     *
     *    final var child = parent.spawn();
     *
     *    assertThat(child, notNullValue());// guard
     *    assertThat(child.getParent(), sameInstance(parent));
     * }
     * </pre>
     *
     * <p>
     * But you can do do better than that. The class you are testing and the return
     * type of the method do not only have the behaviour that you have specified for
     * them. They must also conform to some invariants imposed by the {@link Object}
     * class and that constrain relationships between those two objects. You should
     * check that the objects conform to those invariants. There are several of
     * them. Checking them can be fiddly. Explicitly checking them all directly in
     * your test method would be verbose, error prone, and in some cases provide low
     * value (because in that particular test, it is unlikely that the invariant
     * would be broken).
     * <p>
     * </p>
     * This method provides a convenient and abstract way to check that the objects
     * conform to the relationship invariants imposed by the {@link Object} class.
     * Simply delegate to this method in your test, like this:
     * </p>
     *
     * <pre>
     * {@code @Test}
     * public void spawn_a() {
     *    final var parent = new Monster();
     *
     *    final var child = parent.spawn();
     *
     *    assertThat(child, notNullValue());// guard
     *    assertThat(parent, ObjectVerifier.satisfiesInvariants());
     *    assertThat(child, ObjectVerifier.satisfiesInvariants());
     *    assertThat(child, ObjectVerifier.satisfiesInvariantsWith(parent));
     *    assertThat(child.getParent(), sameInstance(parent));
     * }
     * </pre>
     *
     * <h2>How to Use this Method for Thorough Testing</h2>
     * <p>
     * The relationship invariants imposed by {@link Object} pertain to the
     * {@link Object#equals(Object)} and {@link Object#hashCode()} methods, so using
     * this method is mostly useful only if you have overridden
     * {@link Object#equals(Object)}. If you have done that, you should be testing
     * that your {@code equals} method is correct, by constructing significant pairs
     * of objects, then checking that their equality is as expected. You should also
     * pass those pairs of objects to this method for checking. For example, like
     * this:
     * </p>
     *
     * <pre>
     * {@code @Test}
     * public void equals_a_b() {
     *    final var a = new Monster("a");
     *    final var b = new Monster("b");
     *
     *    assertThat(a, ObjectVerifier.satisfiesInvariantsWith(b));
     *    assertThat(a, not(is(b)));
     * }
     * </pre>
     *
     * @param other An object to verify with respect to
     * @throws NullPointerException If {@code other} is null.
     */
    public static Matcher<Object> satisfiesInvariantsWith(@Nonnull Object other) {
        Objects.requireNonNull(other, "other");
        return Matchers.describedAs("satisfies pairwise Object class invariants with " + safeToString(other), allOf(
                new EqualityIsSymmetric(other),
                new HashCodeIsConsistentWithEquals(other)
        ));
    }

    /**
     * <p>
     * Assert that a pair of objects conform to all the relationship (pairwise)
     * invariants imposed by the {@link Object} base class, throwing an
     * {@link AssertionError} if they do not.
     * </p>
     * <p>
     * This is a convenience method, equivalent to {@code assertThat(object1, ObjectVerifier.satisfiesInvariantsWith(object2))}
     * </p>
     *
     * @param object1 An object to test.
     * @param object2 An object to test.
     * @throws NullPointerException <ul>
     *                              <li>If {@code object1} is null.</li>
     *                              <li>If {@code object2} is null.</li>
     *                              </ul>
     * @throws AssertionError       If {@code object1} and {@code object1} break an invariant.
     */
    public static void assertInvariants(@Nonnull final Object object1, @Nonnull final Object object2) {
        Objects.requireNonNull(object1, "object1");
        Objects.requireNonNull(object2, "object2");

        assertThat(object1, satisfiesInvariantsWith(object2));
    }

    static boolean equals(@Nonnull final Object object1, @Nullable final Object object2) {
        try {
            return object1.equals(object2);
        } catch (final Exception e) {
            /*
             * A typical equals implementation will delegate to the equals methods of the
             * attributes of the object. A naive implementation might throw a
             * NullPointerException if the object has any null attributes.
             */
            throw new AssertionError("equals() must not throw exceptions [" + safeToString(object1) + ", " + safeToString(object2) + "]", e);
        }
    }

    @Nonnull
    private static String identityString(@Nullable final Object object) {
        if (object == null) {
            return "null";
        } else {
            return object.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(object));
        }
    }

    /**
     * <p>
     * Provide a {@link String} representation of a given object, in a manner that
     * is safe in cases where the {@link Object#toString()} method (or its override)
     * throws an exception.
     * </p>
     * <p>
     * This method is intended for use in test assertion failure messages, to
     * identify the object that failed the assertion. However, because the
     * {@link Object#toString()} method (and its overrides) can <em>themselves</em>
     * be faulty and throw exceptions, and {@code object} could be null, directly
     * calling a {@code object.toString()} method in test code is unwise. This
     * method returns the text given by {@code object.toString()}, if possible, but
     * if {@code object} is {@code null} it returns "null", and if
     * {@code object.toString()} throws an exception, it instead returns a fall-back
     * value. The fall-back value is the text that {@link Object#toString()} returns
     * if {@link Object#hashCode()} is not overridden.
     * </p>
     */
    @Nonnull
    public static String safeToString(@Nullable final Object object) {
        try {
            return Objects.requireNonNull(object).toString();
        } catch (final Exception e) {
            return identityString(object);
        }
    }

    private static final class EqualsSelf extends TypeSafeDiagnosingMatcher<Object> {
        @Override
        protected boolean matchesSafely(Object item, Description mismatchDescription) {
            final boolean ok;
            try {
                ok = item.equals(item);
            } catch (Exception e) {
                mismatchDescription.appendText("but equals() threw exception ");
                mismatchDescription.appendValue(e);
                return false;
            }
            if (!ok) {
                mismatchDescription.appendText("not satisfied");
            }
            return ok;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("is equivalent to itself");
        }
    }// class

    private static final class NeverEqualsNull extends TypeSafeDiagnosingMatcher<Object> {
        @SuppressWarnings("ConstantConditions")
        @Override
        protected boolean matchesSafely(Object item, Description mismatchDescription) {
            final boolean ok;
            try {
                ok = !item.equals(null);
            } catch (Exception e) {
                mismatchDescription.appendText("but equals() threw exception ");
                mismatchDescription.appendValue(e);
                return false;
            }
            if (!ok) {
                mismatchDescription.appendText("not satisfied");
            }
            return ok;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("is not equivalent to null");
        }
    }// class

    private static final class ToStringDoesNotThrowException extends MethodDoesNotThrowException<Object> {

        @Override
        protected void callMethod(@Nonnull Object item) throws RuntimeException {
            item.toString();
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("toString() does not throw exceptions");
        }
    }// class

    private static final class HashCodeDoesNotThrowException extends MethodDoesNotThrowException<Object> {

        @Override
        protected void callMethod(@Nonnull Object item) throws RuntimeException {
            item.hashCode();
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("hashCode() does not throw exceptions");
        }
    }// class

    private static final class EqualityIsSymmetric extends PairMatcher<Object> {


        EqualityIsSymmetric(@Nonnull Object other) {
            super(other);
        }

        @Override
        protected boolean matchesSafely(@Nonnull Object item1, @Nonnull Object item2, @Nonnull Description mismatchDescription) {
            final boolean equals12;
            final boolean equals21;
            try {
                equals12 = item1.equals(item2);
                equals21 = item2.equals(item1);
            } catch (Exception e) {
                mismatchDescription.appendText("but equals() threw exception ");
                mismatchDescription.appendValue(e);
                return false;
            }
            final boolean ok = equals12 == equals21;
            if (!ok) {
                mismatchDescription.appendText("not satisfied");
            }
            return ok;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("equality is symmetric");
        }

    }// class

    private static final class HashCodeIsConsistentWithEquals extends PairMatcher<Object> {


        HashCodeIsConsistentWithEquals(@Nonnull Object other) {
            super(other);
        }

        @Override
        protected boolean matchesSafely(@Nonnull Object item1, @Nonnull Object item2, @Nonnull Description mismatchDescription) {
            boolean ok = true;
            boolean equals = false;
            try {
                equals = item1.equals(item2);
            } catch (Exception e) {
                mismatchDescription.appendText("but equals() threw exception ");
                mismatchDescription.appendValue(e);
                ok = false;
            }
            int hasCode1 = 0;
            int hashCode2 = 0;
            try {
                hasCode1 = item1.hashCode();
                hashCode2 = item2.hashCode();
            } catch (Exception e) {
                mismatchDescription.appendText("but hashCode() threw exception ");
                mismatchDescription.appendValue(e);
                ok = false;
            }
            if (ok && equals && hasCode1 != hashCode2) {
                mismatchDescription.appendText("not satisfied");
                ok = false;
            }
            return ok;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("hashCode() is consistent with equals()");
        }

    }// class
}

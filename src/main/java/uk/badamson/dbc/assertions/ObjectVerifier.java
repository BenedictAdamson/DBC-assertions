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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anything;
import static uk.badamson.dbc.assertions.AssertAll.assertAll;

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

    private static void assertEqualsSelf(@Nonnull final Object object) {
        /*
         * A faulty equals method is unlikely to give !this.equals(this), but check for
         * completeness.
         */
        assertThat("An object is always equivalent to itself", equals(object, object));
    }

    /**
     * <p>
     * Assert that a given object conforms to all the invariants imposed by the
     * {@link Object} base class, throwing an {@link AssertionError} if it does not.
     * </p>
     *
     * <h4>How to Use this Method</h4>
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
     *    assertEquals(thing.getSpecies(), "a");
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
     * </p>
     * <p>
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
     *    ObjectVerifier.assertInvariants(thing);
     *    assertEquals(thing.getSpecies(), "a");
     * }
     * </pre>
     *
     * <h4>How to Use this Method for Thorough Testing</h4>
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
     *    ObjectVerifier.assertInvariants(thing);
     *    assertEquals(thing.getSpecies(), null);
     * }
     * </pre>
     *
     * @param object The object to test.
     * @throws NullPointerException If {@code object} is null.
     * @throws AssertionError       If {@code object} breaks an invariant.
     */
    public static void assertInvariants(@Nonnull final Object object) {
        Objects.requireNonNull(object, "object");
        assertAll("Object invariants [" + safeToString(object) + "]",
                () -> assertThat("toString", toString(object), anything()), // check for exception
                () -> assertThat("hashCode", hashCode(object), anything()), // check for exception
                () -> assertAll("equals", () -> assertEqualsSelf(object), () -> assertNeverEqualsNull(object)));
    }

    /**
     * <p>
     * Assert that a pair of objects conform to all the relationship (pairwise)
     * invariants imposed by the {@link Object} base class, throwing an
     * {@link AssertionError} if they do not.
     * </p>
     *
     * <h4>How to Use this Method</h4>
     * <p>
     * In your unit tests, you will check factory methods and getters, using test
     * code similar to this:
     * </p>
     *
     * <pre>
     * {@code @Test}
     * public void mutate_a() {
     *    final var parent = new Monster();
     *
     *    final var child = parent.spawn();
     *
     *    assertNotNull(child);// guard
     *    assertSame(parent, child.getParent());
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
     * </p>
     * <p>
     * This method provides a convenient and abstract way to check that the objects
     * conform to the relationship invariants imposed by the {@link Object} class.
     * Simply delegate to this method in your test, like this:
     * </p>
     *
     * <pre>
     * {@code @Test}
     * public void mutate_a() {
     *    final var parent = new Monster();
     *
     *    final var child = parent.spawn();
     *
     *    assertNotNull(child);// guard
     *    ObjectVerifier.assertInvariants(parent);
     *    ObjectVerifier.assertInvariants(child);
     *    ObjectVerifier.assertInvariants(parent, child);
     *    assertSame(thing, child.getParent());
     * }
     * </pre>
     *
     * <h4>How to Use this Method for Thorough Testing</h4>
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
     *    ObjectVerifier.assertInvariants(a, b);
     *    assertNotEquals(a, b);
     * }
     * </pre>
     *
     * @param object1 An object to test.
     * @param object2 An object to test.
     * @throws NullPointerException <ul>
     *                                          <li>If {@code object1} is null.</li>
     *                                          <li>If {@code object2} is null.</li>
     *                                          </ul>
     * @throws AssertionError       If {@code object1} and {@code object1} break an invariant.
     */
    public static void assertInvariants(@Nonnull final Object object1, @Nonnull final Object object2) {
        Objects.requireNonNull(object1, "object1");
        Objects.requireNonNull(object2, "object2");

        final boolean equals12 = equals(object1, object2);
        final boolean equals21 = equals(object2, object1);
        final int hashCode1 = hashCode(object1);
        final int hashCode2 = hashCode(object2);

        assertAll("equals [" + safeToString(object1) + ", " + safeToString(object2) + "]",
                /*
                 * A faulty equals method is unlikely to be asymmetric (except for failing to
                 * handle null attributes, which we have already checked), but check for
                 * completeness.
                 */
                () -> assertThat("Equality is symmetric", equals12 == equals21),
                /*
                 * The programmer might have implemented the equals method but forgot to also
                 * implement the hashCode() method, which will fail this assertion for the case
                 * that equals12 && (object1 != object2).
                 */
                () -> assertThat("hashCode() is consistent with equals()", !(equals12 && hashCode1 != hashCode2)));
    }

    private static void assertNeverEqualsNull(@Nonnull final Object object) {
        /*
         * A faulty equals method is unlikely to give this.equals(null). But a naive
         * implementation might throw a NullPointerException, because a null argument
         * must be handled as a special case.
         */
        assertThat("An object is never equivalent to null", !equals(object, null));
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

    private static int hashCode(@Nonnull final Object object) {
        try {
            return object.hashCode();
        } catch (final Exception e) {
            /*
             * A typical hashCode implementation will delegate to the hashCode methods of
             * the attributes of the object. A naive implementation might throw a
             * NullPointerException if the object has any null attributes.
             */
            throw new AssertionError("hashCode() must not throw exceptions [" + safeToString(object) + "]", e);
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

    private static String toString(@Nonnull final Object object) {
        try {
            return object.toString();
        } catch (final Exception e) {
            /*
             * The (default) Object.toString() method delegates to hashCode(). A typical
             * hashCode implementation will delegate to the hashCode methods of the
             * attributes of the object. A naive implementation might throw a
             * NullPointerException if the object has any null attributes. If the programmer
             * does not also override the toString() method, toString() can then throw an
             * exception.
             */
            throw new AssertionError("toString() must not throw exceptions", e);
        }
    }
}

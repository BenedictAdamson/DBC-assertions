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

import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import javax.annotation.Nonnull;

/**
 * <p>
 * Verification methods to assist in unit testing classes that have domain
 * semantics for their override of the {@link Object#equals(Object)} method.
 * </p>
 * <p>
 * In <a href="https://en.wikipedia.org/wiki/Domain-driven_design">domain driven
 * design</a> we distinguish between classes that are for <i>entities</i> and
 * classes that are for <i>values<i>. These semantic distinctions have
 * implications for the required behaviour of the {@code equals(Object)} method
 * of those classes.
 * </p>
 * <p>
 * <dfn>Value semantics</dfn> requires that the {@code equals(Object)} method
 * returns {@code true} if, and only if, the given object is an instance of the
 * same class <em>and</em> the pairs of object <i>attributes</i> of the two
 * objects are equivalent.
 * </p>
 * <p>
 * <dfn>Entity semantics</dfn> requires that the {@code equals(Object)} method
 * returns {@code true} if, and only if, the given object is an instance of the
 * same class <em>and</em> the rqo objects have equivalent values for an
 * <i>identifier attribute</i>.
 * </p>
 * <p>
 * This class provides several methods to supplement the
 * {@link ObjectTest#assertInvariants(Object, Object)} method. The methods are
 * most useful for test cases where the two objects you have are expected to be
 * equivalent, or they are expected to be <i>almost equivalent</i>: not
 * equivalent, but having many attributes that are equivalent.
 * </p>
 */
public final class EqualsSemanticsTest {

    private static <T, U> U access(@Nonnull final T object, @Nonnull final String attributeName,
            @Nonnull final Function<T, U> valueOfAttribute) {
        Objects.requireNonNull(object, "object");
        Objects.requireNonNull(attributeName, "attributeName");
        Objects.requireNonNull(valueOfAttribute, "valueOfAttribute");

        try {
            return valueOfAttribute.apply(object);
        } catch (final Exception e) {
            throw createUnexpectedAccessException(object, attributeName, e);
        }
    }

    private static <T> int access(@Nonnull final T object, @Nonnull final String attributeName,
            @Nonnull final ToIntFunction<T> valueOfAttribute) {
        Objects.requireNonNull(object, "object");
        Objects.requireNonNull(attributeName, "attributeName");
        Objects.requireNonNull(valueOfAttribute, "valueOfAttribute");

        try {
            return valueOfAttribute.applyAsInt(object);
        } catch (final Exception e) {
            throw createUnexpectedAccessException(object, attributeName, e);
        }
    }

    private static <T> long access(@Nonnull final T object, @Nonnull final String attributeName,
            @Nonnull final ToLongFunction<T> valueOfAttribute) {
        Objects.requireNonNull(object, "object");
        Objects.requireNonNull(attributeName, "attributeName");
        Objects.requireNonNull(valueOfAttribute, "valueOfAttribute");

        try {
            return valueOfAttribute.applyAsLong(object);
        } catch (final Exception e) {
            throw createUnexpectedAccessException(object, attributeName, e);
        }
    }

    /**
     * <p>
     * Assert that a pair of objects of a class satisfy a pairwise invariant
     * necessary for the class to have <i>value semantics</i>, throwing an
     * {@link AssertionError} if they do not.
     * </p>
     * <p>
     * Value semantics implies that if you have two non-null instances of the value
     * class, and you access the values of one of the {@code int} attributes for
     * both objects, an invariant is that if the {@code equals(Object)} method
     * returns {@code true}, the attributes must be equal. This method tests that
     * invariant for one attribute. So the method can be general, you provide it
     * with an accessor function for getting the value of the attribute from the two
     * objects.
     * </p>
     *
     * <h2>How to Use this Method</h2>
     * <p>
     * Use this as a supplement to the
     * {@link ObjectTest#assertInvariants(Object, Object)} method, when testing a
     * class that you have defined to have <i>value semantics</i>. Call the method
     * for each {@code int} <i>attribute</i> of the class, in addition to asserting
     * equality or non equality of the objects, to provide better test failure
     * diagnostic messages.
     * </p>
     *
     * <pre>
     * {@code @Test}
     * public void equals_equivalent() {
     *    final var amount1 = new Amount(1L);
     *    final var amount2 = new Amount(1L);
     *
     *    ObjectTest.assertInvariants(amount1, amount2);
     *    ObjectTest.assertIntValueSemantics(amount1, amount2, "intValue", (amount) -> amount.intValue());
     *    assertEquals(amount1, amount2);
     * }
     * </pre>
     *
     * @param <T>
     *            The class of {@code object1} and {@code object2}
     * @param object1
     *            An object to test.
     * @param object2
     *            An object to test.
     * @param attributeName
     *            The name of the attribute to examine.
     * @param valueOfAttribute
     *            A function for accessing the value of the attribute for the two
     *            objects under test. This should delegate to the getter method of
     *            the class. This test method assumes that the getter should never
     *            throw exceptions; it will throw an {@link AssertionError} if the
     *            the function does throw an exception.
     * @throws NullPointerException
     *             <ul>
     *             <li>If {@code object1} is null.</li>
     *             <li>If {@code object2} is null.</li>
     *             <li>If {@code attributeName} is null.</li>
     *             <li>If {@code valueOfAttribute} is null.</li>
     *             </ul>
     * @throws AssertionError
     *             If {@code object1} and {@code object2} break the invariant.
     */
    public static <T> void assertIntValueSemantics(@Nonnull final T object1, @Nonnull final T object2,
            @Nonnull final String attributeName, @Nonnull final ToIntFunction<T> valueOfAttribute) {
        final int attribute1 = access(object1, attributeName, valueOfAttribute);
        final int attribute2 = access(object2, attributeName, valueOfAttribute);
        final boolean equals = ObjectTest.equals(object1, object2);

        assertThat("Value semantics with attribute [" + attributeName + "] for [" + ObjectTest.safeToString(object1)
                + ", " + ObjectTest.safeToString(object2) + "]", !(equals && attribute1 != attribute2));
    }

    /**
     * <p>
     * Assert that a pair of objects of a class satisfy a pairwise invariant
     * necessary for the class to have <i>value semantics</i>, throwing an
     * {@link AssertionError} if they do not.
     * </p>
     * <p>
     * Value semantics implies that if you have two non-null instances of the value
     * class, and you access the values of one of the {@code long} attributes for
     * both objects, an invariant is that if the {@code equals(Object)} method
     * returns {@code true}, the attributes must be equal. This method tests that
     * invariant for one attribute. So the method can be general, you provide it
     * with an accessor function for getting the value of the attribute from the two
     * objects.
     * </p>
     *
     * <h2>How to Use this Method</h2>
     * <p>
     * Use this as a supplement to the
     * {@link ObjectTest#assertInvariants(Object, Object)} method, when testing a
     * class that you have defined to have <i>value semantics</i>. Call the method
     * for each {@code long} <i>attribute</i> of the class, in addition to asserting
     * equality or non equality of the objects, to provide better test failure
     * diagnostic messages.
     * </p>
     *
     * <pre>
     * {@code @Test}
     * public void equals_equivalent() {
     *    final var amount1 = new Amount(1L);
     *    final var amount2 = new Amount(1L);
     *
     *    ObjectTest.assertInvariants(amount1, amount2);
     *    ObjectTest.assertLongValueSemantics(amount1, amount2, "longValue", (amount) -> amount.longValue());
     *    assertEquals(amount1, amount2);
     * }
     * </pre>
     *
     * @param <T>
     *            The class of {@code object1} and {@code object2}
     * @param object1
     *            An object to test.
     * @param object2
     *            An object to test.
     * @param attributeName
     *            The name of the attribute to examine.
     * @param valueOfAttribute
     *            A function for accessing the value of the attribute for the two
     *            objects under test. This should delegate to the getter method of
     *            the class. This test method assumes that the getter should never
     *            throw exceptions; it will throw an {@link AssertionError} if the
     *            the function does throw an exception.
     * @throws NullPointerException
     *             <ul>
     *             <li>If {@code object1} is null.</li>
     *             <li>If {@code object2} is null.</li>
     *             <li>If {@code attributeName} is null.</li>
     *             <li>If {@code valueOfAttribute} is null.</li>
     *             </ul>
     * @throws AssertionError
     *             If {@code object1} and {@code object2} break the invariant.
     */
    public static <T> void assertLongValueSemantics(@Nonnull final T object1, @Nonnull final T object2,
            @Nonnull final String attributeName, @Nonnull final ToLongFunction<T> valueOfAttribute) {
        final long attribute1 = access(object1, attributeName, valueOfAttribute);
        final long attribute2 = access(object2, attributeName, valueOfAttribute);
        final boolean equals = ObjectTest.equals(object1, object2);

        assertThat("Value semantics with attribute [" + attributeName + "] for [" + ObjectTest.safeToString(object1)
                + ", " + ObjectTest.safeToString(object2) + "]", !(equals && attribute1 != attribute2));
    }

    /**
     * <p>
     * Assert that a pair of objects of a class satisfy a pairwise invariant
     * necessary for the class to have <i>value semantics</i>, throwing an
     * {@link AssertionError} if they do not.
     * </p>
     * <p>
     * Value semantics implies that if you have two non-null instances of the value
     * class, and you access the values of one of the {@code Object} attributes for
     * both objects, an invariant is that if the {@code equals(Object)} method
     * returns {@code true}, the attributes must be
     * {@linkplain Object#equals(Object)} This method tests that invariant for one
     * attribute. So the method can be general, you provide it with an accessor
     * function for getting the value of the attribute from the two objects.
     * </p>
     *
     * </p>
     *
     * <h2>How to Use this Method</h2>
     * <p>
     * Use this as a supplement to the
     * {@link ObjectTest#assertInvariants(Object, Object)} method, when testing a
     * class that you have defined to have <i>value semantics</i>. Call the method
     * for each object <i>attribute</i> of the class, in addition to asserting
     * equality or non equality of the objects, to provide better test failure
     * diagnostic messages.
     * </p>
     *
     * <pre>
     * {@code @Test}
     * public void equals_equivalent() {
     *    final var species1 = new Species("Homo sapiens");
     *    final var species2 = new Monster("Homo sapiens");
     *
     *    ObjectTest.assertInvariants(species1, species2);
     *    ObjectTest.assertValueSemantics(species1, species2, "name", (species) -> species.getName());
     *    assertEquals(species1, species2);
     * }
     * </pre>
     *
     * @param <T>
     *            The class of {@code object1} and {@code object2}
     * @param <U>
     *            The class of the attribute to examine.
     * @param object1
     *            An object to test.
     * @param object2
     *            An object to test.
     * @param attributeName
     *            The name of the attribute to examine.
     * @param valueOfAttribute
     *            A function for accessing the value of the attribute for the two
     *            objects under test. This should delegate to the getter method of
     *            the class. This test method assumes that the getter should never
     *            throw exceptions; it will throw an {@link AssertionError} if the
     *            the function does throw an exception.
     * @throws NullPointerException
     *             <ul>
     *             <li>If {@code object1} is null.</li>
     *             <li>If {@code object2} is null.</li>
     *             <li>If {@code attributeName} is null.</li>
     *             <li>If {@code valueOfAttribute} is null.</li>
     *             </ul>
     * @throws AssertionError
     *             If {@code object1} and {@code object2} break the invariant.
     */
    public static <T, U> void assertValueSemantics(@Nonnull final T object1, @Nonnull final T object2,
            @Nonnull final String attributeName, @Nonnull final Function<T, U> valueOfAttribute) {
        final U attribute1 = access(object1, attributeName, valueOfAttribute);
        final U attribute2 = access(object2, attributeName, valueOfAttribute);
        final boolean equals = ObjectTest.equals(object1, object2);

        assertThat("Value semantics with attribute [" + attributeName + "] for [" + ObjectTest.safeToString(object1)
                + ", " + ObjectTest.safeToString(object2) + "]", !(equals && !Objects.equals(attribute1, attribute2)));
    }

    private static <T> AssertionError createUnexpectedAccessException(final T object, final String attributeName,
            final Exception e) {
        return ObjectTest.createUnexpectedException("Acccessing attribute " + attributeName
                + " should not throw exception for [" + ObjectTest.safeToString(object) + "]", e);
    }
}

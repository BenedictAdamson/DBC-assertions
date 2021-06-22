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

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import static org.hamcrest.MatcherAssert.assertThat;

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
 * same class <em>and</em> the two objects have equivalent values for an
 * <i>identifier attribute</i>.
 * </p>
 * <p>
 * This class provides several methods to supplement the
 * {@link ObjectVerifier#assertInvariants(Object, Object)} method. The methods are
 * most useful for test cases where the two objects you have are expected to be
 * equivalent, or they are expected to be <i>almost equivalent</i>: not
 * equivalent, but having many attributes that are equivalent.
 * </p>
 */
public final class EqualsSemanticsVerifier {

    private EqualsSemanticsVerifier() {
        assert false;// must not instance
    }

    @Nullable
    private static <T, U> U access(@Nonnull final T object, final String stringId, @Nonnull final String attributeName,
                                   @Nonnull final Function<T, U> valueOfAttribute) {
        try {
            return valueOfAttribute.apply(object);
        } catch (final Exception e) {
            throw createUnexpectedAccessException(stringId, attributeName, e);
        }
    }

    private static <T> int access(@Nonnull final T object, final String stringId, @Nonnull final String attributeName,
                                  @Nonnull final ToIntFunction<T> valueOfAttribute) {
        try {
            return valueOfAttribute.applyAsInt(object);
        } catch (final Exception e) {
            throw createUnexpectedAccessException(stringId, attributeName, e);
        }
    }

    private static <T> long access(@Nonnull final T object, final String stringId, @Nonnull final String attributeName,
                                   @Nonnull final ToLongFunction<T> valueOfAttribute) {
        try {
            return valueOfAttribute.applyAsLong(object);
        } catch (final Exception e) {
            throw createUnexpectedAccessException(stringId, attributeName, e);
        }
    }

    /**
     * <p>
     * Provide a {@linkplain Matcher matcher}
     * that matches if, and only if, the object being matched
     * satisfies the   pairwise invariant, with respect to a given object,
     * necessary for the class to have <i>entity semantics</i>.     *
     * </p>
     * <p>
     * Entity semantics implies that if you have two non-null instances of the
     * entity class that has an {@link Object} <i>ID attribute</i>, two invariants
     * are that <i>ID attribute</i> are never {@code null} and that the
     * {@code equals(Object)} for the two instances returns {@code true} if, and
     * only if, {@code equals(Object)} returns {@code true} for the <i>ID
     * attribute</i>. This method tests that invariant. So the method can be
     * general, you provide it with an accessor function for getting the value of
     * the <i>ID attribute</i> from the two objects.
     * </p>
     *
     * </p>
     *
     * <h2>How to Use this Method</h2>
     * <p>
     * Use this as a supplement to the
     * {@link ObjectVerifier#satisfiesInvariantsWith(Object)}  method, when testing a
     * class that you have defined to have <i>entity semantics</i>.
     * </p>
     *
     * <pre>
     * {@code @Test}
     * public void equals_equivalent() {
     *    final var id = UUID.randomUUID();
     *    final var person1 = new Person(id, "Bobby");
     *    final var person2 = new Person(id, "Hilary");
     *
     *    assertThat(person1, satisfiesInvariants(person2));
     *    assertThat(person1, EqualsSemanticsVerifier.hasEntitySematicsWith(person2));
     *    assertThat(person1, is(person2));
     * }
     * </pre>
     *
     * @param <T>       The class of object to match.
     * @param <U>       The class of the ID attribute.
     * @param other     The other to test the invariant with respect to.
     * @param valueOfId A function for accessing the value of the ID attribute for the two
     *                  objects under test. This should delegate to the getter method of
     *                  the class. This test method assumes that the getter should never
     *                  throw exceptions.
     * @throws NullPointerException If any argument is null
     */
    public static <T, U> Matcher<T> hasEntitySemanticsWith(@Nonnull final T other, @Nonnull final Function<T, U> valueOfId) {
        return new HasEntitySemantics<>(other, valueOfId);
    }

    /**
     * <p>
     * Assert that a pair of objects of a class satisfy the pairwise invariant
     * necessary for the class to have <i>entity semantics</i>, throwing an
     * {@link AssertionError} if they do not.
     * </p>
     * <p>
     * This is a convenience method, equivalent to {@code assertThat(object1, EntitySemanticsVerifier.hasEntitySemanticsWith(object2, valueOfId))}
     * </p>
     */
    public static <T, U> void assertEntitySemantics(@Nonnull final T object1, @Nonnull final T object2,
                                                    @Nonnull final Function<T, U> valueOfId) {
        Objects.requireNonNull(object1, "object1");
        assertThat(object1, hasEntitySemanticsWith(object2, valueOfId));
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
     * {@link ObjectVerifier#assertInvariants(Object, Object)} method, when testing a
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
     *    {@code EqualsSemanticsVerifier.assertIntValueSemantics(amount1, amount2, "intValue", (amount) -> amount.intValue());}
     *    assertEquals(amount1, amount2);
     * }
     * </pre>
     *
     * @param <T>              The class of {@code object1} and {@code object2}
     * @param object1          An object to test.
     * @param object2          An object to test.
     * @param attributeName    The name of the attribute to examine.
     * @param valueOfAttribute A function for accessing the value of the attribute for the two
     *                         objects under test. This should delegate to the getter method of
     *                         the class. This test method assumes that the getter should never
     *                         throw exceptions; it will throw an {@link AssertionError} if the
     *                         the function does throw an exception.
     * @throws NullPointerException <ul>
     *                              <li>If {@code object1} is null.</li>
     *                              <li>If {@code object2} is null.</li>
     *                              <li>If {@code attributeName} is null.</li>
     *                              <li>If {@code valueOfAttribute} is null.</li>
     *                              </ul>
     * @throws AssertionError       If {@code object1} and {@code object2} break the invariant.
     */
    public static <T> void assertIntValueSemantics(@Nonnull final T object1, @Nonnull final T object2,
                                                   @Nonnull final String attributeName, @Nonnull final ToIntFunction<T> valueOfAttribute) {
        Objects.requireNonNull(object1, "object1");
        Objects.requireNonNull(object2, "object2");
        Objects.requireNonNull(attributeName, "attributeName");
        Objects.requireNonNull(valueOfAttribute, "valueOfAttribute");

        final var stringId1 = ObjectVerifier.safeToString(object1);
        final var stringId2 = ObjectVerifier.safeToString(object2);
        /*
         * Provide good diagnostics if the getter throws an exception.
         */
        final int attribute1 = access(object1, stringId1, attributeName, valueOfAttribute);
        final int attribute2 = access(object2, stringId2, attributeName, valueOfAttribute);
        /*
         * Provide good diagnostics if equals throws an exception.
         */
        final boolean equals = ObjectVerifier.equals(object1, object2);

        assertThat("Value semantics with attribute [" + attributeName + "] for [" + stringId1 + ", " + stringId2 + "]",
                !(equals && attribute1 != attribute2));
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
     * {@link ObjectVerifier#assertInvariants(Object, Object)} method, when testing a
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
     *    {@code EqualsSemanticsVerifier.assertLongValueSemantics(amount1, amount2, "longValue", (amount) -> amount.longValue());}
     *    assertEquals(amount1, amount2);
     * }
     * </pre>
     *
     * @param <T>              The class of {@code object1} and {@code object2}
     * @param object1          An object to test.
     * @param object2          An object to test.
     * @param attributeName    The name of the attribute to examine.
     * @param valueOfAttribute A function for accessing the value of the attribute for the two
     *                         objects under test. This should delegate to the getter method of
     *                         the class. This test method assumes that the getter should never
     *                         throw exceptions; it will throw an {@link AssertionError} if the
     *                         the function does throw an exception.
     * @throws NullPointerException <ul>
     *                              <li>If {@code object1} is null.</li>
     *                              <li>If {@code object2} is null.</li>
     *                              <li>If {@code attributeName} is null.</li>
     *                              <li>If {@code valueOfAttribute} is null.</li>
     *                              </ul>
     * @throws AssertionError       If {@code object1} and {@code object2} break the invariant.
     */
    public static <T> void assertLongValueSemantics(@Nonnull final T object1, @Nonnull final T object2,
                                                    @Nonnull final String attributeName, @Nonnull final ToLongFunction<T> valueOfAttribute) {
        Objects.requireNonNull(object1, "object1");
        Objects.requireNonNull(object2, "object2");
        Objects.requireNonNull(attributeName, "attributeName");
        Objects.requireNonNull(valueOfAttribute, "valueOfAttribute");

        final var stringId1 = ObjectVerifier.safeToString(object1);
        final var stringId2 = ObjectVerifier.safeToString(object2);
        /*
         * Provide good diagnostics if the getter throws an exception.
         */
        final long attribute1 = access(object1, stringId1, attributeName, valueOfAttribute);
        final long attribute2 = access(object2, stringId2, attributeName, valueOfAttribute);
        /*
         * Provide good diagnostics if equals throws an exception.
         */
        final boolean equals = ObjectVerifier.equals(object1, object2);

        assertThat("Value semantics with attribute [" + attributeName + "] for [" + stringId1 + ", " + stringId2 + "]",
                !(equals && attribute1 != attribute2));
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
     * <h2>How to Use this Method</h2>
     * <p>
     * Use this as a supplement to the
     * {@link ObjectVerifier#assertInvariants(Object, Object)} method, when testing a
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
     *    final var species2 = new Species("Homo sapiens");
     *
     *    ObjectTest.assertInvariants(species1, species2);
     *    {@code EqualsSemanticsVerifier.assertValueSemantics(species1, species2, "name", (species) -> species.getName());}
     *    assertEquals(species1, species2);
     * }
     * </pre>
     *
     * @param <T>              The class of {@code object1} and {@code object2}
     * @param <U>              The class of the attribute to examine.
     * @param object1          An object to test.
     * @param object2          An object to test.
     * @param attributeName    The name of the attribute to examine.
     * @param valueOfAttribute A function for accessing the value of the attribute for the two
     *                         objects under test. This should delegate to the getter method of
     *                         the class. This test method assumes that the getter should never
     *                         throw exceptions; it will throw an {@link AssertionError} if the
     *                         the function does throw an exception.
     * @throws NullPointerException <ul>
     *                              <li>If {@code object1} is null.</li>
     *                              <li>If {@code object2} is null.</li>
     *                              <li>If {@code attributeName} is null.</li>
     *                              <li>If {@code valueOfAttribute} is null.</li>
     *                              </ul>
     * @throws AssertionError       If {@code object1} and {@code object2} break the invariant.
     */
    public static <T, U> void assertValueSemantics(@Nonnull final T object1, @Nonnull final T object2,
                                                   @Nonnull final String attributeName, @Nonnull final Function<T, U> valueOfAttribute) {
        Objects.requireNonNull(object1, "object1");
        Objects.requireNonNull(object2, "object2");
        Objects.requireNonNull(attributeName, "attributeName");
        Objects.requireNonNull(valueOfAttribute, "valueOfAttribute");

        final var stringId1 = ObjectVerifier.safeToString(object1);
        final var stringId2 = ObjectVerifier.safeToString(object2);
        /*
         * Provide good diagnostics if the getter throws an exception.
         */
        @Nullable final U attribute1 = access(object1, stringId1, attributeName, valueOfAttribute);
        @Nullable final U attribute2 = access(object2, stringId2, attributeName, valueOfAttribute);
        /*
         * Provide good diagnostics if equals throws an exception. Handle null
         * attributes.
         */
        final boolean equals = ObjectVerifier.equals(object1, object2);
        final boolean attributesEquals = attribute1 == null ? attribute2 == null
                : ObjectVerifier.equals(attribute1, attribute2);

        assertThat("Value semantics with attribute [" + attributeName + "] for [" + stringId1 + ", " + stringId2 + "]",
                !(equals && !attributesEquals));
    }

    private static AssertionError createUnexpectedAccessException(final String stringId, final String attributeName,
                                                                  final Exception e) {
        return new AssertionError("Accessing attribute " + attributeName + " should not throw exception for [" + stringId + "]", e);
    }

    private static final class HasEntitySemantics<T, U> extends PairMatcher<T> {

        @Nonnull
        private final Function<T, U> valueOfId;

        HasEntitySemantics(@Nonnull final T other, @Nonnull final Function<T, U> valueOfId) {
            super(other);
            this.valueOfId = Objects.requireNonNull(valueOfId, "valueOfId");
        }

        @Nullable
        private U getId(@Nonnull T item, @Nonnull Description mismatchDescription) {
            U id = null;
            try {
                id = valueOfId.apply(item);
                if (id == null) {
                    mismatchDescription.appendText("but the ID was null");
                }
            } catch (Exception e) {
                mismatchDescription.appendText("but getting the ID threw exception ");
                mismatchDescription.appendValue(e);
            }
            return id;
        }

        @Override
        protected boolean matchesSafely(@Nonnull T item1, @Nonnull T item2, @Nonnull Description mismatchDescription) {
            final U id1 = getId(item1, mismatchDescription);
            final U id2 = getId(item2, mismatchDescription);
            boolean ok = id1 != null && id2 != null;
            boolean equals = false;
            try {
                equals = item1.equals(item2);
            } catch (Exception e) {
                mismatchDescription.appendText("but equals() threw exception ");
                mismatchDescription.appendValue(e);
                ok = false;
            }
            boolean equalIds = false;
            try {
                equalIds = Objects.equals(id1, id2);
            } catch (Exception e) {
                mismatchDescription.appendText("but equals() threw exception ");
                mismatchDescription.appendValue(e);
                ok = false;
            }
            if (ok && (equals != equalIds)) {
                mismatchDescription.appendText("not satisfied");
                ok = false;
            }
            return ok;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("has entity semantics");
        }
    }// class
}

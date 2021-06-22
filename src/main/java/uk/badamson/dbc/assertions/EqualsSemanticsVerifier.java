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
     * This is a convenience method, equivalent to {@code assertThat(object1, EqualsSemanticsVerifier.hasEntitySemanticsWith(object2, valueOfId))}
     * </p>
     */
    public static <T, U> void assertEntitySemantics(@Nonnull final T object1, @Nonnull final T object2,
                                                    @Nonnull final Function<T, U> valueOfId) {
        Objects.requireNonNull(object1, "object1");
        assertThat(object1, hasEntitySemanticsWith(object2, valueOfId));
    }

    /**
     * <p>
     * Provide a {@linkplain Matcher matcher}
     * that matches if, and only if, the object being matched
     * satisfies a   pairwise invariant, with respect to a given object,
     * necessary for the class to have <i>value semantics</i>,.
     * </p>
     * <p>
     * Value semantics implies that if you have two non-null instances of the value
     * class, and you access the values of one of the {@code Object} attributes for
     * both objects, an invariant is that if the {@code equals(Object)} method
     * returns {@code true}, the attributes also must be
     * {@linkplain Object#equals(Object)} The matcher tests that invariant for one
     * attribute. So the method can be general, you provide it with an accessor
     * function for getting the value of the attribute from the two objects.
     * </p>
     *
     * <h2>How to Use this Method</h2>
     * <p>
     * Use this as a supplement to the
     * {@link ObjectVerifier#satisfiesInvariantsWith(Object)} method, when testing a
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
     *    assertThat(species1, ObjectVerifier.satisfiesInvariantsWith(species2));
     *    {@code assertThat(species1, EqualsSemanticsVerifier.hasValueSemanticsWith(species2, "name", (species) -> species.getName()));}
     *    assertThat(species1, is(species2));
     * }
     * </pre>
     *
     * @param <T>              The class of object to match
     * @param <U>              The class of the attribute to examine.
     * @param other            The other object to test the invariant with respect to.
     * @param attributeName    The name of the attribute to examine.
     * @param valueOfAttribute A function for accessing the value of the attribute for the two
     *                         objects under test. This should delegate to the getter method of
     *                         the class. This matcher assumes that the getter should never
     *                         throw exceptions.
     * @throws NullPointerException \If any argument is null.
     */
    public static <T, U> Matcher<T> hasValueSemanticsWith(@Nonnull T other, @Nonnull String attributeName, @Nonnull Function<T, U> valueOfAttribute) {
        return new HasValueSemantics<>(other, attributeName, valueOfAttribute);
    }

    /**
     * <p>
     * Assert that a pair of objects of a class satisfy a pairwise invariant
     * necessary for the class to have <i>value semantics</i>, throwing an
     * {@link AssertionError} if they do not.
     * </p>
     * <p>
     * This is a convenience method, equivalent to {@code assertThat(object1, EqualsSemanticsVerifier.hasValueSemanticsWith(object2, attributeName, valueOfAttribute))}
     * </p>
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
        assertThat(object1, hasValueSemanticsWith(object2, attributeName, valueOfAttribute));
    }

    private static AssertionError createUnexpectedAccessException(final String stringId, final String attributeName,
                                                                  final Exception e) {
        return new AssertionError("Accessing attribute " + attributeName + " should not throw exception for [" + stringId + "]", e);
    }

    private static final class HasValueSemantics<T, U> extends PairMatcher<T> {

        @Nonnull
        private final String attributeName;
        @Nonnull
        private final Function<T, U> valueOfAttribute;

        HasValueSemantics(@Nonnull T other, @Nonnull String attributeName, @Nonnull Function<T, U> valueOfAttribute) {
            super(other);
            this.attributeName = Objects.requireNonNull(attributeName, "attributeName");
            this.valueOfAttribute = Objects.requireNonNull(valueOfAttribute, "valueOfAttribute");
        }

        @Override
        protected boolean matchesSafely(@Nonnull T item1, @Nonnull T item2, @Nonnull Description mismatchDescription) {
            boolean ok = true;
            U attribute1 = null;
            U attribute2 = null;
            try {
                attribute1 = valueOfAttribute.apply(item1);
                attribute2 = valueOfAttribute.apply(item2);
            } catch (Exception e) {
                mismatchDescription.appendText("but getting the attribute threw exception ");
                mismatchDescription.appendValue(e);
                ok = false;
            }
            boolean equals = false;
            try {
                equals = item1.equals(item2);
            } catch (Exception e) {
                mismatchDescription.appendText("but equals() threw exception ");
                mismatchDescription.appendValue(e);
                ok = false;
            }
            boolean equalAttributes = false;
            try {
                equalAttributes = Objects.equals(attribute1, attribute2);
            } catch (Exception e) {
                mismatchDescription.appendText("but equals() for the attributes threw exception ");
                mismatchDescription.appendValue(e);
                ok = false;
            }
            if (ok && equals && !equalAttributes) {
                mismatchDescription.appendText("not satisifed");
                ok = false;
            }
            return ok;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("has value semantics with attribute ");
            description.appendText(attributeName);
        }
    }// class

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

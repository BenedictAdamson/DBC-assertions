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
import org.hamcrest.Matcher;

import javax.annotation.Nonnull;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.describedAs;

/**
 * @see org.hamcrest.Matchers
 */
@SuppressFBWarnings(value = {"SA_LOCAL_SELF_COMPARISON"}, justification = "checking invariant")
public class Matchers {


    /**
     * <p>
     * Create a {@linkplain Matcher matcher}
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
     * import static org.hamcrest.MatcherAssert.assertThat;
     * import static org.hamcrest.Matchers.*;
     *
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
     * import static org.hamcrest.MatcherAssert.assertThat;
     * import static org.hamcrest.Matchers.*;
     * import static uk.badamson.dbc.assertions.Matchers.*;
     *
     * {@code @Test}
     * public void mutate_a() {
     *    final var thing = new Monster();
     *
     *    thing.mutate("a");
     *
     *    assertThat(thing, satisfiesObjectInvariants());
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
     * import static org.hamcrest.MatcherAssert.assertThat;
     * import static org.hamcrest.Matchers.*;
     * import static uk.badamson.dbc.assertions.Matchers.*;
     *
     * {@code @Test}
     * public void mutate_a() {
     *    final var thing = new Monster();
     *
     *    thing.mutate(null);
     *
     *    assertThat(thing, satisfiesObjectInvariants());
     *    assertThat(thing.getSpecies(), nullValue());
     * }
     */
    @Nonnull
    public static Matcher<Object> satisfiesObjectInvariants() {
        return SatisfiesObjectInvariants.create();
    }

    /**
     * <p>
     * Create a {@linkplain Matcher matcher}
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
     * import static org.hamcrest.MatcherAssert.assertThat;
     * import static org.hamcrest.Matchers.*;
     *
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
     * import static org.hamcrest.MatcherAssert.assertThat;
     * import static org.hamcrest.Matchers.*;
     * import static uk.badamson.dbc.assertions.Matchers.*;
     *
     * {@code @Test}
     * public void spawn_a() {
     *    final var parent = new Monster();
     *
     *    final var child = parent.spawn();
     *
     *    assertThat(child, notNullValue());// guard
     *    assertThat(parent, satisfiesObjectInvariants());
     *    assertThat(child, satisfiesObjectInvariants());
     *    assertThat(child, satisfiesObjectInvariantsWith(parent));
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
     * import static org.hamcrest.MatcherAssert.assertThat;
     * import static org.hamcrest.Matchers.*;
     * import static uk.badamson.dbc.assertions.Matchers.*;
     *
     * {@code @Test}
     * public void equals_a_b() {
     *    final var a = new Monster("a");
     *    final var b = new Monster("b");
     *
     *    assertThat(a, satisfiesObjectInvariantsWith(b));
     *    assertThat(a, not(is(b)));
     * }
     * </pre>
     *
     * @param other An object to verify with respect to
     * @throws NullPointerException If {@code other} is null.
     */
    @Nonnull
    public static Matcher<Object> satisfiesObjectInvariantsWith(@Nonnull Object other) {
        return SatisfiesObjectInvariantsWith.create(other);
    }

    /**
     * <p>
     * Create a {@linkplain Matcher matcher}
     * that matches if, and only if, the object being matched
     * satisfies  the relationship (pairwise)
     * invariant required for the {@link Comparable#compareTo(Object)} method to
     * be consistent with {@linkplain Object#equals(Object)}.
     * </p>
     *
     * <p>
     * This is a supplement to the {@link SatisfiesComparableInvariantsWith#create(Comparable)}
     * method, for use in the typical case that the natural ordering is consistent
     * with equals.
     * </p>
     */
    @Nonnull
    public static <T extends Comparable<T>> Matcher<T> naturalOrderingIsConsistentWithEqualsWith(@Nonnull T other) {
        return new NaturalOrderingIsConsistentWithEquals<>(other);
    }


    /**
     * <p>
     * Create a {@linkplain Matcher matcher}
     * that matches if, and only if, the object being matched
     * satisfies all the invariants imposed by the
     * {@link Comparable} interface.
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
     * import static org.hamcrest.MatcherAssert.assertThat;
     * import static org.hamcrest.Matchers.*;
     *
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
     * import static org.hamcrest.MatcherAssert.assertThat;
     * import static org.hamcrest.Matchers.*;
     * import static uk.badamson.dbc.assertions.Matchers.*;
     *
     * {@code @Test}
     * public void increment_1() {
     *    final var amount = new Amount(1);
     *
     *    amount.increment();
     *
     *    assertThat(amount, satisfiesObjectInvariants());
     *    assertThat(amount, satisfiesComparableInvariants());
     *    assertThat(amount.intValue(), is(2));
     * }
     * </pre>
     */
    @Nonnull
    public static <T extends Comparable<T>> Matcher<T> satisfiesComparableInvariants() {
        // noinspection ConstantConditions,EqualsWithItself
        return describedAs("satisfies Comparable interface invariants",
                allOf(
                        methodThrows("compareTo(null)", NullPointerException.class, c -> c.compareTo(null)),
                        /*
                         * For completeness, check that this.compareTo(this) does not throw an
                         * exception, although it is unlikely that a faulty implementation would throw
                         * an exception.
                         */
                        methodDoesNotThrow("compareTo(this)", c -> c.compareTo(c))
                ));
    }


    /**
     * <p>
     * Create a {@linkplain Matcher matcher}
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
     * import static org.hamcrest.MatcherAssert.assertThat;
     * import static org.hamcrest.Matchers.*;
     *
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
     * import static org.hamcrest.MatcherAssert.assertThat;
     * import static org.hamcrest.Matchers.*;
     * import static uk.badamson.dbc.assertions.Matchers.*;
     *
     * {@code @Test}
     * public void compareTo_1_2() {
     *    final var a1 = new Amount(1);
     *    final var a2 = new Amount(2);
     *
     *    assertThat(a1, satisfiesObjectInvariantsWith(a2));
     *    assertThat(a1, satisfiesComparableInvariantsWith(a2));
     *    {@code assertThat(a1.compareTo(a2) < 0);}
     * }
     * </pre>
     * <p>
     * In many cases you will also want to use the
     * {@link #naturalOrderingIsConsistentWithEqualsWith(Comparable)}
     * matcher.
     * </p>
     *
     * @see #naturalOrderingIsConsistentWithEqualsWith(Comparable)
     */
    @Nonnull
    public static <T extends Comparable<T>> Matcher<T> satisfiesComparableInvariantsWith(@Nonnull T other) {
        return SatisfiesComparableInvariantsWith.create(other);
    }

    /**
     * <p>
     * Create a {@linkplain Matcher matcher}
     * that matches if, and only if, the object being matched
     * satisfies all the  three-way relationship
     * invariants imposed by the  {@link Comparable} interface.
     * </p>
     *
     * <p>
     * This is a supplement to the {@link #satisfiesComparableInvariantsWith(Comparable)}
     * method, provided out of completeness, for use in very thorough unit tests. In
     * practice, its invariants are unlikely to be broken if the
     * {@link #satisfiesComparableInvariantsWith(Comparable)} invariants are all met.
     * </p>
     */
    @Nonnull
    public static <T extends Comparable<T>> Matcher<T> satisfiesComparableInvariantsWith(@Nonnull T item2, @Nonnull T item3) {
        return SatisfiesComparableInvariantsWith2.create(item2, item3);
    }


    /**
     * <p>
     * Create a {@linkplain Matcher matcher}
     * that matches if, and only if, the object being matched
     * satisfies the   pairwise invariant, with respect to a given object,
     * necessary for the class to have <i>entity semantics</i>.     *
     * </p>
     * <p>
     * In <a href="https://en.wikipedia.org/wiki/Domain-driven_design">domain driven
     * design</a> we distinguish between classes that are for <i>entities</i> and
     * classes that are for <i>values<i>. These semantic distinctions have
     * implications for the required behaviour of the {@code equals(Object)} method
     * of those classes.
     * <dfn>Entity semantics</dfn> requires that the {@code equals(Object)} method
     * returns {@code true} if, and only if, the given object is an instance of the
     * same class <em>and</em> the two objects have equivalent values for an
     * <i>identifier attribute</i>.
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
     * {@link SatisfiesObjectInvariantsWith#create(Object)}  method, when testing a
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
     *    assertThat(person1, EqualsSemanticsVerifier.hasEntitySemanticsWith(person2));
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
    @Nonnull
    public static <T, U> Matcher<T> hasEntitySemanticsWith(@Nonnull final T other, @Nonnull final Function<T, U> valueOfId) {
        return new HasEntitySemantics<>(other, valueOfId);
    }


    /**
     * <p>
     * Create a {@linkplain Matcher matcher}
     * that matches if, and only if, the object being matched
     * satisfies a   pairwise invariant, with respect to a given object,
     * necessary for the class to have <i>value semantics</i>.
     * </p>
     * <p>
     * In <a href="https://en.wikipedia.org/wiki/Domain-driven_design">domain driven
     * design</a> we distinguish between classes that are for <i>entities</i> and
     * classes that are for <i>values<i>. These semantic distinctions have
     * implications for the required behaviour of the {@code equals(Object)} method
     * of those classes.
     * <dfn>Value semantics</dfn> requires that the {@code equals(Object)} method
     * returns {@code true} if, and only if, the given object is an instance of the
     * same class <em>and</em> the pairs of object <i>attributes</i> of the two
     * objects are equivalent.
     * </p>
     * <p>
     * Value semantics implies that if you have two non-null instances of the value
     * class, and you access the values of one of the  attributes for
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
     * {@link SatisfiesObjectInvariantsWith#create(Object)} method, when testing a
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
     * @throws NullPointerException If any argument is null.
     */
    @Nonnull
    public static <T, U> Matcher<T> hasValueSemanticsWith(@Nonnull T other, @Nonnull String attributeName, @Nonnull Function<T, U> valueOfAttribute) {
        return new HasValueSemantics<>(other, attributeName, valueOfAttribute);
    }

    /**
     * <p>
     * Create a {@linkplain Matcher matcher}
     * that matches if, and only if, a method of the object being matched
     * does not throw an exception when called.
     * </p>
     * <p>
     * So the {@link Matcher} can be general, you provide it with an accessor
     * function for calling the method.
     * </p>
     *
     * @param <T>        The class of object to match.
     * @param methodName The name of the method to call.
     * @param method     A method reference (such as {@code Object::toString}) for the method to call,
     *                   or a lambda that indirectly calls the method.
     * @throws NullPointerException If any argument is null.
     */
    @Nonnull
    public static <T, R> Matcher<T> methodDoesNotThrow(@Nonnull String methodName, @Nonnull Function<T, R> method) {
        return new MethodDoesNotThrow<>(methodName, method);
    }

    /**
     * <p>
     * Create a {@linkplain Matcher matcher}
     * that matches if, and only if, a method of the object being matched
     * throws an exception, of a given class, when called.
     * </p>
     * <p>
     * So the {@link Matcher} can be general, you provide it with an accessor
     * function for calling the method.
     * </p>
     *
     * @param <T>        The class of object to match.
     * @param <R>        The return type of the method.
     * @param <E>        The class of the exception that is expected to be thrown.
     * @param methodName The name of the method to call.
     * @param exception  The class object of the exception that is expected to be thrown.
     * @param method     A method reference (such as {@code Stack::pop}) for the method to call,
     *                   or a lambda that indirectly calls the method.
     * @throws NullPointerException If any argument is null.
     */
    @Nonnull
    public static <T, R, E extends Throwable> Matcher<T> methodThrows(@Nonnull String methodName, @Nonnull final Class<E> exception, @Nonnull Function<T, R> method) {
        return new MethodThrows<>(methodName, exception, method);
    }

    /**
     * <p>
     * Create a {@linkplain Matcher matcher}
     * that matches if, and only if, the object being matched
     * has a <i>feature</i>
     * (which is typically an attribute)
     * that successfully matches using a given matcher
     * </p>
     *
     * @param name           The name of the feature
     * @param get            A method reference (such as {@code Object::toString}) to get the feature,
     *                       or a lambda that indirectly gets the feature.
     * @param featureMatcher The matcher to apply to the feature value.
     *                       The returned matcher will pass null to this feature matcher
     *                       if the {@code get} function throws an exception.
     *                       See {@link #methodDoesNotThrow(String, Function)} for a means of distinguishing
     *                       between a null feature and a feature getter that throws an exception.
     * @param <T>            The type of the object to match
     * @param <U>            The type of the feature
     */
    @Nonnull
    public static <T, U> Matcher<T> feature(@Nonnull String name, @Nonnull Function<T, U> get, @Nonnull Matcher<U> featureMatcher) {
        return new DelegatingFeatureMatcher<>(featureMatcher, name, get);
    }

    /**
     * <p>
     * Create a {@linkplain Matcher matcher}
     * that matches if, and only if, the object being matched
     * has two <i>features</i>
     * (which are typically attributes)
     * that have a given relationship.
     * </p>
     *
     * @param description the description of the expected relationship between the two features
     * @param get1        A method reference (such as {@code Object::toString}) to get the first feature,
     *                    or a lambda that indirectly gets the first feature.
     * @param get2        A method reference to get the second feature,
     *                    *                           or a lambda that indirectly gets the second feature.
     * @param predicate   A method reference
     *                    *                    or a lambda that evaluates whether the relationship holds between the two features;
     *                    *                    returns {@code true} if, and only if, the relationship holds.
     *                    *                    The matcher treats an exception thrown from the predicate as a failure.
     * @param <T>         The type of the object to match.
     * @param <U>         The type of the first feature.
     * @param <V>         The type of the second feature.
     */
    @Nonnull
    public static <T, U, V> Matcher<T> featuresHaveRelationship(@Nonnull String description, @Nonnull Function<T, U> get1, @Nonnull Function<T, V> get2, @Nonnull BiPredicate<U, V> predicate) {
        return new FeaturesHaveRelationshipMatcher<>(description, get1, get2, predicate);
    }

    /**
     * <p>
     * Create a {@linkplain Matcher matcher}
     * that matches if, and only if, the object being matched
     * has a particular relationship
     * with a given other object
     * </p>
     *
     * @param description The description of the relationship
     * @param other       The other object, that ought tp have the specified relationship
     * @param predicate   A method reference or a lambda that evaluates whether the relationship holds between the two objects;
     *                    returns {@code true} if, and only if, the relationship holds.
     *                    The matcher treats an exception thrown from the predicate as a failure.
     * @param <T>         The type of the object to match.
     * @param <U>         The type of the other object.
     */
    @Nonnull
    public static <T, U> Matcher<T> hasRelationship(@Nonnull String description, @Nonnull final U other, @Nonnull BiPredicate<T, U> predicate) {
        return new DelegatingPairRelationshipMatcher<>(other, description, predicate);
    }

    /**
     * <p>
     * Create a {@linkplain Matcher matcher}
     * that matches if, and only if, the object being matched
     * satisfies a constraint given by a given predicate.
     * </p>
     *
     * @param description The constraint expressed by the predicate
     * @param predicate   A method reference (such as {@code List::isEmpty}) or a lambda that evaluates whether the constraint is satisfied.
     *                    returns {@code true} if, and only if, the constraint is satisfied.
     *                    The matcher treats an exception thrown from the predicate as a failure.
     * @param <T>         The type of the object to match.
     */
    @Nonnull
    public static <T> Matcher<T> satisfies(@Nonnull String description, @Nonnull Predicate<T> predicate) {
        return new DelegatingMatcher<>(description, predicate);
    }
}
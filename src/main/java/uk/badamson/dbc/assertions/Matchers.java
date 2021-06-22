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

import org.hamcrest.Matcher;

import javax.annotation.Nonnull;

/**
 * @see org.hamcrest.Matchers
 */
public class Matchers {


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
    public static Matcher<Object> satisfiesObjectInvariantsWith(@Nonnull Object other) {
        return SatisfiesObjectInvariantsWith.create(other);
    }

}

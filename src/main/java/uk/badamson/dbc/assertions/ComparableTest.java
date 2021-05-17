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
import static uk.badamson.dbc.assertions.AssertAll.assertAll;

import java.util.Objects;

import javax.annotation.Nonnull;

import org.opentest4j.AssertionFailedError;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * <p>
 * Verification methods to assist in unit testing classes that implement the
 * {@link Comparable} interface.
 */
@SuppressFBWarnings(justification = "Checking exceptions", value = "RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
public class ComparableTest {

    private static <T extends Comparable<T>> void assertCompareToNullThrowsNPE(@Nonnull final T object) {
        try {
            object.compareTo(null);
        } catch (final NullPointerException e) {
            return;// OK: the required behaviour
        } catch (final Exception e) {
            /*
             * It is unlikely that a faulty compareTo would throw any other kind of
             * exception, but provide good diagnostics just in case.
             */
            final var exception = new AssertionFailedError("compareToNull(null) throws only a NullPointerException",
                    NullPointerException.class, e);
            exception.initCause(e);
            throw exception;
        }
        /*
         * An overly careful implementation might attempt to give a result for this
         * case, rather than throw a NPE.
         */
        throw new AssertionFailedError("compareTo(null) throws NullPointerException", NullPointerException.class, null);
    }

    /**
     * <p>
     * Assert that a given object conforms to all the invariants imposed by the
     * {@link Comparable} interface, throwing an {@link AssertionError} if it does
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
     *    assertEquals(2, amount.intValue());
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
     *    ObjectTest.assertInvariants(amount);
     *    ComparableTest.assertInvariants(amount);
     *    assertEquals(2, amount.intValue());
     * }
     * </pre>
     * 
     * @param <T>
     *            The class of {@code object}
     * @param object
     *            The object to test.
     * @throws NullPointerException
     *             If {@code object} is null.
     * @throws AssertionError
     *             If {@code object} breaks an invariant.
     */
    public static <T extends Comparable<T>> void assertInvariants(@Nonnull final T object) {
        assert object != null;
        assertAll(
                /*
                 * For completeness, check that this.compareTo(this) does not throw an
                 * exception, although it is unlikely that a faulty implementation would throw
                 * an exception.
                 */
                () -> compareTo(object, object), () -> assertCompareToNullThrowsNPE(object));
    }

    public static <T extends Comparable<T>> void assertInvariants(@Nonnull final T object1, @Nonnull final T object2) {
        assert object1 != null;
        assert object2 != null;

        final int c12 = compareTo(object1, object2);
        final int c21 = compareTo(object2, object1);
        assertThat("compareTo is symmetric", Integer.signum(c12) == -Integer.signum(c21));
    }

    public static <T extends Comparable<T>> void assertInvariants(@Nonnull final T object1, @Nonnull final T object2,
            @Nonnull final T object3) {
        final int c12 = compareTo(object1, object2);
        final int c23 = compareTo(object2, object3);
        final int c13 = compareTo(object1, object3);
        assertThat("compareTo is transitive", !(c12 > 0 && c23 > 0 && !(c13 > 0)));
    }

    public static <T extends Comparable<T>> void assertNaturalOrderingIsConsistentWithEquals(@Nonnull final T object1,
            @Nonnull final T object2) {
        final var compareTo = compareTo(object1, object2);
        final var equals = ObjectTest.equals(object1, object2);
        assertThat("Natural ordering is consistent with equals", compareTo == 0 == equals);
    }

    private static <T extends Comparable<T>> int compareTo(@Nonnull final T object1, @Nonnull final T object2) {
        Objects.requireNonNull(object1);
        Objects.requireNonNull(object2);
        try {
            return object1.compareTo(object2);
        } catch (final Exception e) {
            /*
             * A typical compareTo implementation will delegate to the compareTo methods of
             * some attributes of the object. A naive implementation might throw a
             * NullPointerException if the object has any null attributes.
             */
            throw ObjectTest.createUnexpectedException(
                    "compareTo must not throw exceptions for non null objects of the same class", e);
        }
    }

}

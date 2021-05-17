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

import javax.annotation.Nonnull;

import org.opentest4j.AssertionFailedError;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * <p>
 * Auxiliary test code for classes that implement the {@link Comparable}
 * interface.
 */
@SuppressFBWarnings(justification = "Checking exceptions", value = "RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
public class ComparableTest {

    public static <T extends Comparable<T>> void assertComparableConsistentWithEquals(@Nonnull final T object1,
            @Nonnull final T object2) {
        final var compareTo = compareTo(object1, object2);
        final var equals = ObjectTest.equals(object1, object2);
        assertThat("Natural ordering is consistent with equals", compareTo == 0 == equals);
    }

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
            throw new AssertionFailedError("compareToNull(null) throws only a NullPointerException",
                    NullPointerException.class, e);
        }
        /*
         * An overly careful implementation might attempt to give a result for this
         * case, rather than throw a NPE.
         */
        throw new AssertionFailedError("compareTo(null) throws NullPointerException", NullPointerException.class, null);
    }

    public static <T extends Comparable<T>> void assertInvariants(@Nonnull final T object) {
        assertInvariants(object, object);
        assertCompareToNullThrowsNPE(object);
    }

    public static <T extends Comparable<T>> void assertInvariants(@Nonnull final T object1, @Nonnull final T object2) {
        compareTo(object1, object2);
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
            throw new AssertionFailedError("compareTo must not throw exceptions for non null objects of the same class",
                    null, e);
        }
    }

}

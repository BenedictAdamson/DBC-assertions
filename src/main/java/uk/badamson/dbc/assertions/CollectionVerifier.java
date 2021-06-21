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

import org.opentest4j.MultipleFailuresError;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Objects;

import static uk.badamson.dbc.assertions.AssertAll.assertAll;

/**
 * <p>
 * Verification methods to assist in unit testing classes that directly or
 * indirectly implement the {@link Collection} interface, and for verifying the
 * content of collections.
 * </p>
 */
public final class CollectionVerifier {

    CollectionVerifier() {
        assert false;// must not instance
    }

    /**
     * @see #assertForAllElements(String, Collection, Verifier) this convenience
     * method is equivalent to calling assertForAllElements(String, Collection,
     * Verifier) with a null heading argument
     */
    public static <T> void assertForAllElements(@Nonnull final Collection<T> collection,
                                                @Nonnull final Verifier<T> verifier) throws MultipleFailuresError {
        assertForAllElements(null, collection, verifier);
    }

    /**
     * <p>
     * Asserts that all the elements of a collection verify as correct according to
     * a supplied verification method.
     * </p>
     * <p>
     * The method verifies all the elements of the collection. It does not halt
     * examining the collection on the first verification failure. It instead
     * aggregates and reports all the verification failures as a single
     * {@link MultipleFailuresError}.
     * </p>
     * <p>
     * The method interprets the throwing of any exception by the verification
     * method as indicating a verification failure. However, if the verification
     * method throws an exception that is one of a few severe <i>unrecoverable</i>
     * {@link Error}s, such as {@link OutOfMemoryError}, the method halts
     * verification of the remaining elements of the collection and propagates the
     * exception to the caller.
     * </p>
     * <p>
     * The method also propagates exceptions thrown by methods of the collection
     * used for streaming through the content of the collection. The method is
     * therefore unsuitable for testing implementations of the {@link Collection}
     * interface, but is rather useful for testing classes that have or return
     * collections.
     * </p>
     * <p>
     * The supplied heading will be included in the message string for the
     * MultipleFailuresError.
     *
     * @param <T>        The type of the collection elements.
     * @param heading    A message to include in the {@link MultipleFailuresError} thrown
     *                   on failure of any verification, or null if there is no such
     *                   message.
     * @param collection A collection to verify
     * @param verifier   The function for verifying each element
     * @throws NullPointerException  If any {@link Nonnull} argument is null.
     * @throws MultipleFailuresError If any of the elements of the {@code collection} fail
     *                               verification.
     */
    public static <T> void assertForAllElements(@Nullable final String heading, @Nonnull final Collection<T> collection,
                                                @Nonnull final Verifier<T> verifier) throws MultipleFailuresError {
        Objects.requireNonNull(collection, "collection");
        Objects.requireNonNull(verifier, "verifier");

        assertAll(heading, collection.stream().map(element -> () -> verifier.verify(element)));
    }

}

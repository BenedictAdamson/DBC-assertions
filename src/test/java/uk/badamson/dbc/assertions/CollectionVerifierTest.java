package uk.badamson.dbc.assertions;
/*
 * Copyright 2021 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * https://www.eclipse.org/legal/epl-v20.html
 */

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.opentest4j.MultipleFailuresError;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.fail;

public class CollectionVerifierTest {

    @Test
    public void assertForAllElementsWithoutHeadingbad() {
        final Collection<String> collection = List.of("x");
        try {
            CollectionVerifier.assertForAllElements(collection, x -> {
                throw new AssertionError("Inevitable");
            });
        } catch (final MultipleFailuresError e) {
            assertThat("one exception thrown", e.getFailures(), hasSize(1));
            return;
        }
        fail("No exception thrown");
    }

    @Nested
    public class AssertForAllElementsWithHeading {

        @Test
        public void bad() {
            final var heading = "heading";
            final Collection<String> collection = List.of("x");
            try {
                CollectionVerifier.assertForAllElements(heading, collection, x -> {
                    throw new AssertionError("Inevitable");
                });
            } catch (final MultipleFailuresError e) {
                assertAll(() -> assertThat("one exception thrown", e.getFailures(), hasSize(1)),
                        () -> assertThat("exception message", e.getMessage(), stringContainsInOrder(heading)));
                return;
            }
            fail("No exception thrown");
        }

        @Test
        public void empty() {
            final Collection<Object> collection = List.of();
            CollectionVerifier.assertForAllElements("heading", collection, x -> {
                // Tough test: should not call the verification method at all
                throw new AssertionError("Never called");
            });
        }

        @Test
        public void good() {
            final Collection<String> collection = List.of("x");
            CollectionVerifier.assertForAllElements("heading", collection, x -> {
                // Do nothing
            });
        }

        @Test
        public void outOfMemoryError() {
            final Collection<String> collection = List.of("x");
            try {
                CollectionVerifier.assertForAllElements("heading", collection, x -> {
                    throw new OutOfMemoryError("Inevitable");
                });
            } catch (final OutOfMemoryError e) { // Expected
                return;
            }
            fail("No exception thrown");
        }

        @Test
        public void runtimeException() {
            final var heading = "heading";
            final Collection<String> collection = List.of("x");
            try {
                CollectionVerifier.assertForAllElements(heading, collection, x -> {
                    throw new RuntimeException("Inevitable");
                });
            } catch (final MultipleFailuresError e) {// Expected
                assertAll(() -> assertThat("one exception thrown", e.getFailures(), hasSize(1)),
                        () -> assertThat("exception message", e.getMessage(), stringContainsInOrder(heading)));
                return;
            }
            fail("No exception thrown");
        }

    }// class

}

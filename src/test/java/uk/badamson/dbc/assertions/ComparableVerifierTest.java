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

import javax.annotation.Nonnull;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * <p>
 * Unit tests for the {@link ComparableVerifier} class.
 * </p>
 */
public class ComparableVerifierTest {

    private static final class CompareToNullDoesNotThrowNPE implements Comparable<CompareToNullDoesNotThrowNPE> {

        CompareToNullDoesNotThrowNPE() {
        }

        @SuppressWarnings("NullableProblems")
        @Override
        public int compareTo(final CompareToNullDoesNotThrowNPE that) {
            return 0;
        }

    }

    private static final class CompareToSelfThrows implements Comparable<CompareToSelfThrows> {

        CompareToSelfThrows() {
        }

        @Override
        public int compareTo(@Nonnull final CompareToSelfThrows that) {
            if (this == that) {
                throw new RuntimeException("Fake");
            }
            return 0;
        }

    }

    private static final class CompareToThrows implements Comparable<CompareToThrows> {

        private final int value;

        CompareToThrows(final int value) {
            this.value = value;
        }

        @Override
        public int compareTo(final CompareToThrows that) {
            if (value == that.value) {
                return 0;

            } else {
                throw new RuntimeException("Fake");
            }
        }

    }

    @Nested
    public class AssertInvariants {

        @Test
        public void compareToNullDoesNotThrowNPE() {
            final var object = new CompareToNullDoesNotThrowNPE();
            assertThrows(AssertionError.class, () -> ComparableVerifier.assertInvariants(object));
        }

        @Test
        public void compareToSelfThrows() {
            final var object = new CompareToSelfThrows();
            assertThrows(AssertionError.class, () -> ComparableVerifier.assertInvariants(object));
        }

        @Test
        public void integer() {
            ComparableVerifier.assertInvariants(0);
        }

        @Test
        public void string() {
            ComparableVerifier.assertInvariants("a");
        }
    }// class

    @Nested
    public class AssertInvariants2 {

        @Test
        public void compareToThrows() {
            final var object1 = new CompareToThrows(0);
            final var object2 = new CompareToThrows(1);
            assertThrows(AssertionError.class, () -> ComparableVerifier.assertInvariants(object1, object2));
        }

        @Test
        public void integer() {
            ComparableVerifier.assertInvariants(0, 1);
        }

        @Test
        public void string() {
            ComparableVerifier.assertInvariants("a", "b");
        }
    }// class

    @Nested
    public class AssertInvariants3 {

        @Test
        public void integer() {
            ComparableVerifier.assertInvariants(3, 2, 1);
        }

        @Test
        public void string() {
            ComparableVerifier.assertInvariants("c", "b", "a");
        }
    }// class

    @Nested
    public class AssertNaturalOrderingIsConsistentWithEquals {

        @Test
        public void bigger() {
            ComparableVerifier.assertNaturalOrderingIsConsistentWithEquals(0, 1);
        }

        @Test
        public void compareToNotConsistentWithEquals() {
            assertThrows(AssertionError.class, () -> ComparableVerifier
                    .assertNaturalOrderingIsConsistentWithEquals(new BigDecimal("1.0"), new BigDecimal("1.00")));
        }

        @Test
        public void equivalent() {
            ComparableVerifier.assertNaturalOrderingIsConsistentWithEquals(0, 0);
        }

        @Test
        public void smaller() {
            ComparableVerifier.assertNaturalOrderingIsConsistentWithEquals(1, 0);
        }
    }// class
}

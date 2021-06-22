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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;

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
    public class SatisfiesInvariants {

        @Test
        public void compareToNullDoesNotThrowNPE() {
            final var object = new CompareToNullDoesNotThrowNPE();
            assertThat(object, not(ComparableVerifier.satisfiesInvariants()));
        }

        @Test
        public void compareToSelfThrows() {
            final var object = new CompareToSelfThrows();
            assertThat(object, not(ComparableVerifier.satisfiesInvariants()));
        }

        @Test
        public void integer() {
            assertThat(0, ComparableVerifier.satisfiesInvariants());
        }

        @Test
        public void string() {

            assertThat("a", ComparableVerifier.satisfiesInvariants());
        }
    }// class

    @Nested
    public class SatisfiesInvariantsWith1 {

        @Test
        public void compareToThrows() {
            final var object1 = new CompareToThrows(0);
            final var object2 = new CompareToThrows(1);
            assertThat(object1, not(ComparableVerifier.satisfiesInvariantsWith(object2)));
        }

        @Test
        public void integer() {
            assertThat(0, ComparableVerifier.satisfiesInvariantsWith(1));
        }

        @Test
        public void string() {
            assertThat("a", ComparableVerifier.satisfiesInvariantsWith("b"));
        }
    }// class

    @Nested
    public class SatisfiesInvariantsWith2 {

        @Test
        public void integer() {
            assertThat(3, ComparableVerifier.satisfiesInvariantsWith(2, 1));
        }

        @Test
        public void string() {
            assertThat("a", ComparableVerifier.satisfiesInvariantsWith("b", "c"));
        }
    }// class

    @Nested
    public class NaturalOrderingIsConsistentWithEqualsWith {

        @Test
        public void bigger() {
            assertThat(0, ComparableVerifier.naturalOrderingIsConsistentWithEqualsWith(1));
        }

        @Test
        public void compareToNotConsistentWithEquals() {
            assertThat(new BigDecimal("1.0"), not(ComparableVerifier.naturalOrderingIsConsistentWithEqualsWith(new BigDecimal("1.00"))));
        }

        @Test
        public void equivalent() {
            assertThat(0, ComparableVerifier.naturalOrderingIsConsistentWithEqualsWith(0));
        }

        @Test
        public void smaller() {
            assertThat(1, ComparableVerifier.naturalOrderingIsConsistentWithEqualsWith(0));
        }
    }// class
}

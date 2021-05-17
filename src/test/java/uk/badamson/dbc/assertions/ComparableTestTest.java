package uk.badamson.dbc.assertions;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

/*
 * © Copyright Benedict Adamson 2021.
 *
 * This file is part of MC-des.
 *
 * MC-des is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MC-des is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MC-des.  If not, see <https://www.gnu.org/licenses/>.
 */

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * <p>
 * Unit tests for the {@link ComparableTest} class.
 * </p>
 */
public class ComparableTestTest {

    @Nested
    public class AssertCompareToConsistentWithEquals {

        @Test
        public void bigger() {
            ComparableTest.assertCompareToConsistentWithEquals(Integer.valueOf(0), Integer.valueOf(1));
        }

        @Test
        public void compareToNotConsistentWithEquals() {
            assertThrows(AssertionError.class, () -> ComparableTest
                    .assertCompareToConsistentWithEquals(new BigDecimal("1.0"), new BigDecimal("1.00")));
        }

        @Test
        public void equivalent() {
            ComparableTest.assertCompareToConsistentWithEquals(Integer.valueOf(0), Integer.valueOf(0));
        }

        @Test
        public void smaller() {
            ComparableTest.assertCompareToConsistentWithEquals(Integer.valueOf(1), Integer.valueOf(0));
        }
    }// class

    @Nested
    public class AssertInvariants {

        @Test
        public void compareToNullDoesNotThrowNPE() {
            final var object = new CompareToNullDoesNotThrowNPE(0);
            assertThrows(AssertionError.class, () -> ComparableTest.assertInvariants(object));
        }

        @Test
        public void compareToSelfThrows() {
            final var object = new CompareToSelfThrows(0);
            assertThrows(AssertionError.class, () -> ComparableTest.assertInvariants(object));
        }

        @Test
        public void integer() {
            ComparableTest.assertInvariants(Integer.valueOf(0));
        }

        @Test
        public void string() {
            ComparableTest.assertInvariants("a");
        }
    }// class

    @Nested
    public class AssertInvariants2 {

        @Test
        public void compareToThrows() {
            final var object1 = new CompareToThrows(0);
            final var object2 = new CompareToThrows(1);
            assertThrows(AssertionError.class, () -> ComparableTest.assertInvariants(object1, object2));
        }

        @Test
        public void integer() {
            ComparableTest.assertInvariants(Integer.valueOf(0), Integer.valueOf(1));
        }

        @Test
        public void string() {
            ComparableTest.assertInvariants("a", "b");
        }
    }// class

    @Nested
    public class AssertInvariants3 {

        @Test
        public void integer() {
            ComparableTest.assertInvariants(Integer.valueOf(3), Integer.valueOf(2), Integer.valueOf(1));
        }

        @Test
        public void string() {
            ComparableTest.assertInvariants("c", "b", "a");
        }
    }// class

    private static final class CompareToNullDoesNotThrowNPE implements Comparable<CompareToNullDoesNotThrowNPE> {

        private final int value;

        CompareToNullDoesNotThrowNPE(final int value) {
            this.value = value;
        }

        @Override
        public int compareTo(final CompareToNullDoesNotThrowNPE that) {
            return Integer.compare(value, that == null ? 0 : that.value);
        }

    }

    private static final class CompareToSelfThrows implements Comparable<CompareToSelfThrows> {

        private final int value;

        CompareToSelfThrows(final int value) {
            this.value = value;
        }

        @Override
        public int compareTo(final CompareToSelfThrows that) {
            if (this == that) {
                throw new RuntimeException("Fake");
            }
            return Integer.compare(value, that.value);
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
}

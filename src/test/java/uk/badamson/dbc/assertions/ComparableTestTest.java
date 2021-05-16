package uk.badamson.dbc.assertions;

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
    public class AssertComparableConsistentWithEquals {

        @Test
        public void bigger() {
            ComparableTest.assertComparableConsistentWithEquals(Integer.valueOf(0), Integer.valueOf(1));
        }

        @Test
        public void equivalent() {
            ComparableTest.assertComparableConsistentWithEquals(Integer.valueOf(0), Integer.valueOf(0));
        }

        @Test
        public void smaller() {
            ComparableTest.assertComparableConsistentWithEquals(Integer.valueOf(1), Integer.valueOf(0));
        }
    }// class

    @Nested
    public class AssertInvariants {

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
        public void integer() {
            ComparableTest.assertInvariants(Integer.valueOf(0), Integer.valueOf(1));
        }

        @Test
        public void string() {
            ComparableTest.assertInvariants("a", "b2");
        }
    }// class

}

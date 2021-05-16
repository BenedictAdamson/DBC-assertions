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
 * Unit tests for code in the {@link ObjectTest} class
 * </p>
 */
public class ObjectTestTest {

    @Nested
    public class AssertInvariants {

        @Test
        public void object() {
            ObjectTest.assertInvariants(new Object());
        }

        @Test
        public void string() {
            ObjectTest.assertInvariants("string");
        }
    }// class

    @Nested
    public class AssertInvariants2 {

        @Test
        public void object() {
            ObjectTest.assertInvariants(new Object(), new Object());
        }

        @Test
        public void string() {
            ObjectTest.assertInvariants("a", "b");
        }
    }// class

}

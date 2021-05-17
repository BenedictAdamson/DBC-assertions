package uk.badamson.dbc.assertions;
/*
 * © Copyright Benedict Adamson 2018,2021.
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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * <p>
 * Unit tests for the {@link Object} class.
 * </p>
 */
@SuppressFBWarnings(justification = "Checking contract", value = "EC_NULL_ARG")
public final class ObjectTest {

    private static void assertEqualsSelf(@Nonnull final Object object) {
        /*
         * A faulty equals method is unlikely to given !this.equals(this), but check for
         * completeness.
         */
        assertTrue(equals(object, object), "An object is always equivalent to itself");
    }

    public static void assertInvariants(@Nonnull final Object object) {
        assert object != null;
        assertAll("equals", () -> assertEqualsSelf(object), () -> assertNeverEqualsNull(object));
    }

    public static void assertInvariants(@Nonnull final Object object1, @Nonnull final Object object2) {
        assert object1 != null;
        assert object2 != null;

        final boolean equals12 = equals(object1, object2);
        final boolean equals21 = equals(object2, object1);
        final int hashCode1 = hashCode(object1);
        final int hashCode2 = hashCode(object2);

        assertAll("equals",
                /*
                 * A faulty equals method is unlikely to be asymmetric (except for failing to
                 * handle null attributes, which we have already checked), but check for
                 * completeness.
                 */
                () -> assertTrue(equals12 == equals21, "Equality is symmetric"),
                /*
                 * The programmer might have implemented the equals method but forgot to also
                 * implement the hashCode() method, which will fail this assertion for the case
                 * that equals12 && (object1 == object2).
                 */
                () -> assertFalse(equals12 && hashCode1 != hashCode2, "hashCode() is consistent with equals()"));
    }

    private static void assertNeverEqualsNull(@Nonnull final Object object) {
        /*
         * A faulty equals method is unlikely to give this.equals(null). But a naive
         * implementation might throw a NullPointerException, because a null argument
         * must be handled as a special case.
         */
        assertFalse(equals(object, null), "An object is never equivalent to null");
    }

    static boolean equals(@Nonnull final Object object1, @Nullable final Object object2) {
        try {
            return object1.equals(object2);
        } catch (final Exception e) {
            /*
             * A typical equals implementation will delegate to the equals methods of the
             * attributes of the object. A naive implementation might throw a
             * NullPointerException if the object has any null attributes.
             */
            throw new AssertionError("equals() must not throw exceptions", e);
        }
    }

    private static int hashCode(@Nonnull final Object object) {
        try {
            return object.hashCode();
        } catch (final Exception e) {
            /*
             * A typical hashCode implementation will delegate to the hashCode methods of
             * the attributes of the object. A naive implementation might throw a
             * NullPointerException if the object has any null attributes.
             */
            throw new AssertionError("equals() must not throw exceptions", e);
        }
    }
}

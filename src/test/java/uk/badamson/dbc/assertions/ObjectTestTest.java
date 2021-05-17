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

import static org.junit.jupiter.api.Assertions.assertThrows;

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
        public void equalsNull() {
            final var object = new Object() {

                @Override
                public boolean equals(final Object obj) {
                    return true;
                }

                // Satisfy static checks
                @Override
                public int hashCode() {
                    return 0;
                }

            };
            assertThrows(AssertionError.class, () -> ObjectTest.assertInvariants(object));
        }

        @Test
        public void equalsThrows() {
            final var object = new Object() {

                @Override
                public boolean equals(final Object obj) {
                    if (obj == null) {
                        return false;
                    }
                    throw new NullPointerException();
                }

                // Satisfy static checks
                @Override
                public int hashCode() {
                    return super.hashCode();
                }

            };
            assertThrows(AssertionError.class, () -> ObjectTest.assertInvariants(object));
        }

        @Test
        public void hashCodeThrows() {
            final var object = new Object() {

                // Satisfy static checks
                @Override
                public boolean equals(final Object obj) {
                    return super.equals(obj);
                }

                @Override
                public int hashCode() {
                    throw new RuntimeException();
                }

                /*
                 * Override because the default toString delegates to hashCode()
                 */
                @Override
                public String toString() {
                    return "Fake";
                }

            };
            assertThrows(AssertionError.class, () -> ObjectTest.assertInvariants(object));
        }

        @Test
        public void notEqualsSelf() {
            final var object = new Object() {

                @Override
                public boolean equals(final Object obj) {
                    return false;
                }

                // Satisfy static checks
                @Override
                public int hashCode() {
                    return 0;
                }

            };
            assertThrows(AssertionError.class, () -> ObjectTest.assertInvariants(object));
        }

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
        public void equalsAsymmetric() {
            assertThrows(AssertionError.class,
                    () -> ObjectTest.assertInvariants(new AsymmetricEquals(1), new AsymmetricEquals(2)));
        }

        @Test
        public void equalsHashCodeInconsistentWithEquals() {
            assertThrows(AssertionError.class, () -> ObjectTest.assertInvariants(new HashCodeInconsistentWithEquals(1),
                    new HashCodeInconsistentWithEquals(1)));
        }

        @Test
        public void object() {
            ObjectTest.assertInvariants(new Object(), new Object());
        }

        @Test
        public void string() {
            ObjectTest.assertInvariants("a", "b");
        }
    }// class

    private static final class AsymmetricEquals {

        private final int value;

        public AsymmetricEquals(final int value) {
            this.value = value;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof AsymmetricEquals)) {
                return false;
            }
            final AsymmetricEquals other = (AsymmetricEquals) obj;
            return value <= other.value;
        }

        @Override
        public int hashCode() {
            return 0;
        }

    }// class

    private static final class HashCodeInconsistentWithEquals {

        private final int value;

        public HashCodeInconsistentWithEquals(final int value) {
            this.value = value;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof HashCodeInconsistentWithEquals)) {
                return false;
            }
            final HashCodeInconsistentWithEquals other = (HashCodeInconsistentWithEquals) obj;
            return value == other.value;
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

    }// class

}

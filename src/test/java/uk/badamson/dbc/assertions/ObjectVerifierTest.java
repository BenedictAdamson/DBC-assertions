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

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * <p>
 * Unit tests for code in the {@link ObjectVerifier} class
 * </p>
 */
public class ObjectVerifierTest {

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
            assertThrows(AssertionError.class, () -> ObjectVerifier.assertInvariants(object));
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
            assertThrows(AssertionError.class, () -> ObjectVerifier.assertInvariants(object));
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
            assertThrows(AssertionError.class, () -> ObjectVerifier.assertInvariants(object));
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
            assertThrows(AssertionError.class, () -> ObjectVerifier.assertInvariants(object));
        }

        @Test
        public void object() {
            ObjectVerifier.assertInvariants(new Object());
        }

        @Test
        public void string() {
            ObjectVerifier.assertInvariants("string");
        }

        @Test
        public void toStringThrows() {
            final var object = new Object() {

                @Override
                public String toString() {
                    throw new RuntimeException("Fake");
                }

            };
            assertThrows(AssertionError.class, () -> ObjectVerifier.assertInvariants(object));
        }
    }// class

    @Nested
    public class AssertInvariants2 {

        @Test
        public void equalsAsymmetric() {
            assertThrows(AssertionError.class,
                    () -> ObjectVerifier.assertInvariants(new AsymmetricEquals(1), new AsymmetricEquals(2)));
        }

        @Test
        public void equalsHashCodeInconsistentWithEquals() {
            assertThrows(AssertionError.class, () -> ObjectVerifier.assertInvariants(new HashCodeInconsistentWithEquals(1),
                    new HashCodeInconsistentWithEquals(1)));
        }

        @Test
        public void object() {
            ObjectVerifier.assertInvariants(new Object(), new Object());
        }

        @Test
        public void string() {
            ObjectVerifier.assertInvariants("a", "b");
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

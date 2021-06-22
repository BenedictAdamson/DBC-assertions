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
 * Unit tests for code in the {@link Matchers} class
 * </p>
 */
public class MatchersTest {

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

        @Override
        public String toString() {
            return "AsymmetricEquals{" + value +
                    '}';
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
            return 13 * super.hashCode();
        }

    }// class

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
    public class SatisfiesObjectInvariants {

        @Test
        public void equalsNull() {
            final var object = new Object() {

                @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
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
            assertThat(object, not(Matchers.satisfiesObjectInvariants()));
        }

        @Test
        public void equalsThrows() {
            final var object = new Object() {

                @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
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
                    return 13 * super.hashCode();
                }

            };
            assertThat(object, not(Matchers.satisfiesObjectInvariants()));
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
            assertThat(object, not(Matchers.satisfiesObjectInvariants()));
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
            assertThat(object, not(Matchers.satisfiesObjectInvariants()));
        }

        @Test
        public void object() {
            assertThat(new Object(), Matchers.satisfiesObjectInvariants());
        }

        @Test
        public void string() {
            assertThat("string", Matchers.satisfiesObjectInvariants());
        }

        @Test
        public void toStringThrows() {
            final var object = new Object() {

                @Override
                public String toString() {
                    throw new RuntimeException("Fake");
                }

            };
            assertThat(object, not(Matchers.satisfiesObjectInvariants()));
        }
    }// class

    @Nested
    public class SatisfiesObjectInvariantsWith {

        @Test
        public void equalsAsymmetric() {
            assertThat(new AsymmetricEquals(1), not(Matchers.satisfiesObjectInvariantsWith(new AsymmetricEquals(2))));
        }

        @Test
        public void equalsHashCodeInconsistentWithEquals() {
            assertThat(new HashCodeInconsistentWithEquals(1), not(Matchers.satisfiesObjectInvariantsWith(new HashCodeInconsistentWithEquals(1))));
        }

        @Test
        public void object() {
            assertThat(new Object(), Matchers.satisfiesObjectInvariantsWith(new Object()));
        }

        @Test
        public void string() {
            assertThat("a", Matchers.satisfiesObjectInvariantsWith("b"));
        }
    }// class

    @Nested
    public class SatisfiesComparableInvariants {

        @Test
        public void compareToNullDoesNotThrowNPE() {
            final var object = new CompareToNullDoesNotThrowNPE();
            assertThat(object, not(Matchers.satisfiesComparableInvariants()));
        }

        @Test
        public void compareToSelfThrows() {
            final var object = new CompareToSelfThrows();
            assertThat(object, not(Matchers.satisfiesComparableInvariants()));
        }

        @Test
        public void integer() {
            assertThat(0, Matchers.satisfiesComparableInvariants());
        }

        @Test
        public void string() {
            assertThat("a", Matchers.satisfiesComparableInvariants());
        }
    }// class

    @Nested
    public class SatisfiesComparableInvariantsWith1 {

        @Test
        public void compareToThrows() {
            final var object1 = new CompareToThrows(0);
            final var object2 = new CompareToThrows(1);
            assertThat(object1, not(Matchers.satisfiesComparableInvariantsWith(object2)));
        }

        @Test
        public void integer() {
            assertThat(0, Matchers.satisfiesComparableInvariantsWith(1));
        }

        @Test
        public void string() {
            assertThat("a", Matchers.satisfiesComparableInvariantsWith("b"));
        }
    }// class

    @Nested
    public class SatisfiesComparableInvariantsWith2 {

        @Test
        public void integer() {
            assertThat(3, Matchers.satisfiesComparableInvariantsWith(2, 1));
        }

        @Test
        public void string() {
            assertThat("a", Matchers.satisfiesComparableInvariantsWith("b", "c"));
        }
    }// class

    @Nested
    public class NaturalOrderingIsConsistentWithEqualsWith {

        @Test
        public void bigger() {
            assertThat(0, Matchers.naturalOrderingIsConsistentWithEqualsWith(1));
        }

        @Test
        public void compareToNotConsistentWithEquals() {
            assertThat(new BigDecimal("1.0"), not(Matchers.naturalOrderingIsConsistentWithEqualsWith(new BigDecimal("1.00"))));
        }

        @Test
        public void equivalent() {
            assertThat(0, Matchers.naturalOrderingIsConsistentWithEqualsWith(0));
        }

        @Test
        public void smaller() {
            assertThat(1, Matchers.naturalOrderingIsConsistentWithEqualsWith(0));
        }
    }// class
}

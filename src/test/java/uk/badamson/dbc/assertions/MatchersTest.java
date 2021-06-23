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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
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

    private static final class Entity {
        final String name;
        final long version;
        final int balance;
        private final UUID id;

        Entity(final UUID id, final String name, final long version, final int balance) {
            this.id = id;
            this.name = name;
            this.version = version;
            this.balance = balance;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Entity)) {
                return false;
            }
            final Entity other = (Entity) obj;
            return id.equals(other.id);
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }

        @Override
        public String toString() {
            return "Entity [id=" + id + ", name=" + name + ", version=" + version + ", balance=" + balance + "]";
        }

    }// class

    private static final class Value {
        final String name;

        Value(final String name) {
            this.name = name;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Value)) {
                return false;
            }
            final Value other = (Value) obj;
            return Objects.equals(name, other.name);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + (name == null ? 0 : name.hashCode());
            return result;
        }

        @Override
        public String toString() {
            return "Value [name=" + name + "]";
        }

    }// class

    private static final class ValueAttributeEqualsThrows {

        final ValueEqualsThrows attribute = new ValueEqualsThrows(null);

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            return obj instanceof ValueAttributeEqualsThrows;
        }

        @Override
        public int hashCode() {
            return 31;
        }

    }// class

    private static final class ValueEqualsThrows {

        final String name;

        ValueEqualsThrows(final String name) {
            this.name = name;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof ValueEqualsThrows)) {
                return false;
            }
            final ValueEqualsThrows other = (ValueEqualsThrows) obj;
            // Throws NPE for this.name == null
            return name.equals(other.name);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + (name == null ? 0 : name.hashCode());
            return result;
        }

    }// class

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

    @Nested
    public class HasEntitySemanticsWith {

        @Test
        public void equivalent() {
            final UUID id = UUID.randomUUID();
            final var entity1 = new Entity(id, "A", 1L, 100);
            final var entity2 = new Entity(id, "B", 1L, 100);
            assert entity1.equals(entity2);

            assertThat(entity1, Matchers.hasEntitySemanticsWith(entity2, (entity) -> entity.id));
        }

        @Test
        public void different() {
            final var entity1 = new Entity(UUID.randomUUID(), "A", 1L, 100);
            final var entity2 = new Entity(UUID.randomUUID(), "B", 2L, 200);
            assert !entity1.equals(entity2);

            assertThat(entity1, Matchers.hasEntitySemanticsWith(entity2, (entity) -> entity.id));
        }

        @Test
        public void nullId1() {
            final var entity1 = new Entity(null, "A", 1L, 100);
            final var entity2 = new Entity(UUID.randomUUID(), "B", 1L, 100);

            assertThat(entity1, not(Matchers.hasEntitySemanticsWith(entity2, (entity) -> entity.id)));
        }

        @Test
        public void nullId2() {
            final var entity1 = new Entity(UUID.randomUUID(), "A", 1L, 100);
            final var entity2 = new Entity(null, "B", 1L, 100);

            assertThat(entity1, not(Matchers.hasEntitySemanticsWith(entity2, (entity) -> entity.id)));
        }

        @SuppressWarnings("StringOperationCanBeSimplified")
        @Test
        public void value() {
            final String object1 = "A";
            final String object2 = new String(object1);
            assertThat(object1, not(Matchers.hasEntitySemanticsWith(object2, System::identityHashCode)));
        }

    }// class

    @Nested
    public class HasValueSemanticsWith {

        @Test
        public void attributeEqualsThrows() {
            final var value1 = new ValueAttributeEqualsThrows();
            final var value2 = new ValueAttributeEqualsThrows();
            assertThat(value1, not(Matchers.hasValueSemanticsWith(value2, "attribute", value -> value.attribute)));
        }

        @Test
        public void entity() {
            final UUID id = UUID.randomUUID();
            final var entity1 = new Entity(id, "A", 1L, 100);
            final var entity2 = new Entity(id, "B", 1L, 100);
            assert entity1.equals(entity2);

            assertThat(entity1, not(Matchers.hasValueSemanticsWith(entity2, "name", entity -> entity.name)));
        }

        @Test
        public void equalsThrows() {
            final var value1 = new ValueEqualsThrows(null);
            final var value2 = new ValueEqualsThrows("B");
            assertThat(value1, not(Matchers.hasValueSemanticsWith(value2, "name", value -> value.name)));
        }

        @Test
        public void nullValue1() {
            final var value1 = new Value(null);
            final var value2 = new Value("B");
            assertThat(value1, Matchers.hasValueSemanticsWith(value2, "attribute", value -> value.name));
        }

        @Test
        public void nullValue2() {
            final var value1 = new Value("A");
            final var value2 = new Value(null);
            assertThat(value1, Matchers.hasValueSemanticsWith(value2, "attribute", value -> value.name));
        }

        @SuppressWarnings("StringOperationCanBeSimplified")
        @Test
        public void value() {
            final String name = "A";
            final var value1 = new Value(name);
            final var value2 = new Value(new String(name));
            assertThat(value1, Matchers.hasValueSemanticsWith(value2, "attribute", value -> value.name));
        }

    }// class

    @Nested
    public class MethodDoesNotThrow {
        @Test
        public void doesNotThrow() {
            assertThat(1, Matchers.methodDoesNotThrow("toString()", Object::toString));
        }

        @Test
        public void doesThrow() {
            assertThat(2, not(Matchers.methodDoesNotThrow("fake()", i -> {
                throw new RuntimeException("inevitable");
            })));
        }
    }// class

    @Nested
    public class MethodThrows {
        @Test
        public void doesNotThrow() {
            assertThat(1, not(Matchers.methodThrows("toString()", NullPointerException.class, Object::toString)));
        }

        @Test
        public void doesThrow() {
            assertThat(2, Matchers.methodThrows("fake()", RuntimeException.class, i -> {
                throw new RuntimeException("inevitable");
            }));
        }

        @Test
        public void throwsWrongClass() {
            assertThat(3, not(Matchers.methodThrows("fake()", IllegalStateException.class, i -> {
                throw new RuntimeException("inevitable");
            })));
        }
    }// class

    @Nested
    public class Feature {

        @Test
        public void matches() {
            assertThat(1, Matchers.feature("string", Object::toString, is("1")));
        }

        @Test
        public void doesNotMatch() {
            assertThat(1, not(Matchers.feature("string", Object::toString, is("2"))));
        }

        @Test
        public void accessorThrows() {
            assertThat(1, not(Matchers.feature("fake", i -> {
                throw new IllegalStateException("inevitable");
            }, is("1"))));
        }

    }// class

    @Nested
    public class HasRelationship {

        @Test
        public void matches() {
            assertThat(1, Matchers.hasRelationship("< 2", 2, (i, j) -> i.compareTo(j) < 0));
        }

        @Test
        public void doesNotMatch() {
            assertThat(3, not(Matchers.hasRelationship("< 1", 1, (i, j) -> i.compareTo(j) < 0)));
        }

        @Test
        public void predicateThrows() {
            assertThat(1, not(Matchers.hasRelationship("< 2", 2, (i, j) -> {
                throw new IllegalArgumentException("inevitable");
            })));
        }
    }// class

    @Nested
    public class FeaturesHaveRelationship {

        @Test
        public void matches() {
            assertThat(new ArrayList<>(), Matchers.featuresHaveRelationship("empty iff has zero size", List::isEmpty, List::size, (empty, size) -> empty == (size == 0)));
        }

        @Test
        public void doesNotMatch() {
            assertThat(new ArrayList<>(), not(Matchers.featuresHaveRelationship("the impossible", List::isEmpty, List::size, (empty, size) -> false)));
        }

        @Test
        public void predicateThrows() {
            assertThat(new ArrayList<>(), not(Matchers.featuresHaveRelationship("the impossible", List::isEmpty, List::size, (empty, size) -> {
                throw new IllegalArgumentException("inevitable");
            })));
        }

        @Test
        public void get1Throws() {
            assertThat(new ArrayList<>(), not(Matchers.<List<Integer>, Boolean, Integer>featuresHaveRelationship("empty iff has zero size", l -> {
                throw new IllegalArgumentException("inevitable");
            }, List::size, (empty, size) -> empty == (size == 0))));
        }

        @Test
        public void get2Throws() {
            assertThat(new ArrayList<>(), not(Matchers.<List<Integer>, Boolean, Integer>featuresHaveRelationship("empty iff has zero size", List::isEmpty, l -> {
                throw new IllegalArgumentException("inevitable");
            }, (empty, size) -> empty == (size == 0))));
        }
    }// class
}

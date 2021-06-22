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

import java.util.Objects;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;

/**
 * <p>
 * Unit tests for code in the {@link EqualsSemanticsVerifier} class
 * </p>
 */
public class EqualsSemanticsVerifierTest {

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
    public class HasEntitySemanticsWith {

        @Test
        public void equivalent() {
            final UUID id = UUID.randomUUID();
            final var entity1 = new Entity(id, "A", 1L, 100);
            final var entity2 = new Entity(id, "B", 1L, 100);
            assert entity1.equals(entity2);

            assertThat(entity1, EqualsSemanticsVerifier.hasEntitySemanticsWith(entity2, (entity) -> entity.id));
        }

        @Test
        public void different() {
            final var entity1 = new Entity(UUID.randomUUID(), "A", 1L, 100);
            final var entity2 = new Entity(UUID.randomUUID(), "B", 2L, 200);
            assert !entity1.equals(entity2);

            assertThat(entity1, EqualsSemanticsVerifier.hasEntitySemanticsWith(entity2, (entity) -> entity.id));
        }

        @Test
        public void nullId1() {
            final var entity1 = new Entity(null, "A", 1L, 100);
            final var entity2 = new Entity(UUID.randomUUID(), "B", 1L, 100);

            assertThat(entity1, not(EqualsSemanticsVerifier.hasEntitySemanticsWith(entity2, (entity) -> entity.id)));
        }

        @Test
        public void nullId2() {
            final var entity1 = new Entity(UUID.randomUUID(), "A", 1L, 100);
            final var entity2 = new Entity(null, "B", 1L, 100);

            assertThat(entity1, not(EqualsSemanticsVerifier.hasEntitySemanticsWith(entity2, (entity) -> entity.id)));
        }

        @SuppressWarnings("StringOperationCanBeSimplified")
        @Test
        public void value() {
            final String object1 = "A";
            final String object2 = new String(object1);
            assertThat(object1, not(EqualsSemanticsVerifier.hasEntitySemanticsWith(object2, System::identityHashCode)));
        }

    }// class

    @Nested
    public class HasValueSemanticsWith {

        @Test
        public void attributeEqualsThrows() {
            final var value1 = new ValueAttributeEqualsThrows();
            final var value2 = new ValueAttributeEqualsThrows();
            assertThat(value1, not(EqualsSemanticsVerifier.hasValueSemanticsWith(value2, "attribute", value -> value.attribute)));
        }

        @Test
        public void entity() {
            final UUID id = UUID.randomUUID();
            final var entity1 = new Entity(id, "A", 1L, 100);
            final var entity2 = new Entity(id, "B", 1L, 100);
            assert entity1.equals(entity2);

            assertThat(entity1, not(EqualsSemanticsVerifier.hasValueSemanticsWith(entity2, "name", entity -> entity.name)));
        }

        @Test
        public void equalsThrows() {
            final var value1 = new ValueEqualsThrows(null);
            final var value2 = new ValueEqualsThrows("B");
            assertThat(value1, not(EqualsSemanticsVerifier.hasValueSemanticsWith(value2, "name", value -> value.name)));
        }

        @Test
        public void nullValue1() {
            final var value1 = new Value(null);
            final var value2 = new Value("B");
            assertThat(value1, EqualsSemanticsVerifier.hasValueSemanticsWith(value2, "attribute", value -> value.name));
        }

        @Test
        public void nullValue2() {
            final var value1 = new Value("A");
            final var value2 = new Value(null);
            assertThat(value1, EqualsSemanticsVerifier.hasValueSemanticsWith(value2, "attribute", value -> value.name));
        }

        @SuppressWarnings("StringOperationCanBeSimplified")
        @Test
        public void value() {
            final String name = "A";
            final var value1 = new Value(name);
            final var value2 = new Value(new String(name));
            assertThat(value1, EqualsSemanticsVerifier.hasValueSemanticsWith(value2, "attribute", value -> value.name));
        }

    }// class

}

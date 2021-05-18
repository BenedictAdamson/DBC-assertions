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

import java.util.UUID;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * <p>
 * Unit tests for code in the {@link EqualsSemanticsTest} class
 * </p>
 */
public class EqualsSemanticsTestTest {

    @Nested
    public class AssertEntitySemantics {

        @Test
        public void entity() {
            final UUID id = UUID.randomUUID();
            final var entity1 = new Entity(id, "A", 1L, 100);
            final var entity2 = new Entity(id, "B", 1L, 100);
            assert entity1.equals(entity2);

            EqualsSemanticsTest.assertEntitySemantics(entity1, entity2, (entity) -> entity.id);
        }

        @Test
        public void nullId1() {
            final var entity1 = new Entity(null, "A", 1L, 100);
            final var entity2 = new Entity(UUID.randomUUID(), "B", 1L, 100);

            assertThrows(AssertionError.class,
                    () -> EqualsSemanticsTest.assertEntitySemantics(entity1, entity2, (entity) -> entity.id));
        }

        @Test
        public void nullId2() {
            final var entity1 = new Entity(UUID.randomUUID(), "A", 1L, 100);
            final var entity2 = new Entity(null, "B", 1L, 100);

            assertThrows(AssertionError.class,
                    () -> EqualsSemanticsTest.assertEntitySemantics(entity1, entity2, (entity) -> entity.id));
        }

        @Test
        public void value() {
            final String object1 = "A";
            final String object2 = new String(object1);
            assertThrows(AssertionError.class, () -> EqualsSemanticsTest.assertEntitySemantics(object1, object2,
                    (object) -> Integer.valueOf(System.identityHashCode(object))));
        }

    }// class

    @Nested
    public class AssertIntValueSemantics {

        @Test
        public void entity() {
            final UUID id = UUID.randomUUID();
            final var entity1 = new Entity(id, "A", 1L, 100);
            final var entity2 = new Entity(id, "B", 2L, 50);
            assert entity1.equals(entity2);
            assertThrows(AssertionError.class, () -> EqualsSemanticsTest.assertIntValueSemantics(entity1, entity2,
                    "balance", (entity) -> entity.balance));
        }

        @Test
        public void string() {
            final String object = "A";
            EqualsSemanticsTest.assertIntValueSemantics(object, object, "length", (string) -> string.length());
        }

    }// class

    @Nested
    public class AssertLongValueSemantics {

        @Test
        public void entity() {
            final UUID id = UUID.randomUUID();
            final var entity1 = new Entity(id, "A", 1L, 100);
            final var entity2 = new Entity(id, "B", 2L, 50);
            assert entity1.equals(entity2);

            assertThrows(AssertionError.class, () -> EqualsSemanticsTest.assertLongValueSemantics(entity1, entity2,
                    "version", (entity) -> entity.version));
        }

        @Test
        public void uuid() {
            final UUID object = UUID.randomUUID();
            EqualsSemanticsTest.assertLongValueSemantics(object, object, "leastSignificantBits",
                    (uuid) -> uuid.getLeastSignificantBits());
        }

    }// class

    @Nested
    public class AssertValueSemantics {

        @Test
        public void attributeEqualsThrows() {
            final var value1 = new ValueAttributeEqualsThrows();
            final var value2 = new ValueAttributeEqualsThrows();
            assertThrows(AssertionError.class, () -> EqualsSemanticsTest.assertValueSemantics(value1, value2,
                    "attribute", (value) -> value.attribute));
        }

        @Test
        public void entity() {
            final UUID id = UUID.randomUUID();
            final var entity1 = new Entity(id, "A", 1L, 100);
            final var entity2 = new Entity(id, "B", 1L, 100);
            assert entity1.equals(entity2);

            assertThrows(AssertionError.class,
                    () -> EqualsSemanticsTest.assertValueSemantics(entity1, entity2, "name", (entity) -> entity.name));
        }

        @Test
        public void equalsThrows() {
            final var value1 = new ValueEqualsThrows(null);
            final var value2 = new ValueEqualsThrows("B");
            assertThrows(AssertionError.class,
                    () -> EqualsSemanticsTest.assertValueSemantics(value1, value2, "name", (value) -> value.name));
        }

        @Test
        public void nullValue1() {
            final var value1 = new Value(null);
            final var value2 = new Value("B");
            EqualsSemanticsTest.assertValueSemantics(value1, value2, "name", (value) -> value.name);
        }

        @Test
        public void nullValue2() {
            final var value1 = new Value("A");
            final var value2 = new Value(null);
            EqualsSemanticsTest.assertValueSemantics(value1, value2, "name", (value) -> value.name);
        }

        @Test
        public void value() {
            final String name = "A";
            final var value1 = new Value(name);
            final var value2 = new Value(new String(name));
            EqualsSemanticsTest.assertValueSemantics(value1, value2, "name", (value) -> value.name);
        }

    }// class

    private static final class Entity {
        private final UUID id;
        final String name;
        final long version;
        final int balance;

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
            if (name == null) {
                if (other.name != null) {
                    return false;
                }
            } else if (!name.equals(other.name)) {
                return false;
            }
            return true;
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
            if (!(obj instanceof ValueAttributeEqualsThrows)) {
                return false;
            }
            return true;
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

}

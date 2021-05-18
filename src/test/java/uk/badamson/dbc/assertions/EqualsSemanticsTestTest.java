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
    public class AssertLongValueSemantics {

        @Test
        public void entity() {
            final UUID id = UUID.randomUUID();
            final var entity1 = new Entity(id, "A", 1L);
            final var entity2 = new Entity(id, "B", 2L);
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
        public void entity() {
            final UUID id = UUID.randomUUID();
            final var entity1 = new Entity(id, "A", 1L);
            final var entity2 = new Entity(id, "B", 2L);
            assert entity1.equals(entity2);

            assertThrows(AssertionError.class,
                    () -> EqualsSemanticsTest.assertValueSemantics(entity1, entity2, "name", (entity) -> entity.name));
        }

        @Test
        public void uuid() {
            final UUID object = UUID.randomUUID();
            EqualsSemanticsTest.assertValueSemantics(object, object, "leastSignificantBits",
                    (uuid) -> Long.valueOf(uuid.getLeastSignificantBits()));
        }

    }// class

    private static final class Entity {
        private final UUID id;
        final String name;
        final long version;

        Entity(final UUID id, final String name, final long version) {
            this.id = id;
            this.name = name;
            this.version = version;
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

    }// class

}

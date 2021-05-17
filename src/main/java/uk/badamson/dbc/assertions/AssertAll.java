package uk.badamson.dbc.assertions;
/*
 * Copyright 2015-2021 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * This is an adapted copy of the AssertAll class of JUnit 5:
 * https://github.com/junit-team/junit5/blob/main/junit-jupiter-api/src/main/java/org/junit/jupiter/api/AssertAll.java
 * The uk.badamson.dbc.assertions package has it so it does not have a dependency on JUnit 5.
 */

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.opentest4j.MultipleFailuresError;

final class AssertAll {

    static void assertAll(final Collection<Executable> executables) {
        assertAll(null, executables);
    }

    static void assertAll(final Executable... executables) {
        assertAll(null, executables);
    }

    static void assertAll(final Stream<Executable> executables) {
        assertAll(null, executables);
    }

    static void assertAll(final String heading, final Collection<Executable> executables) {
        assertAll(heading, executables.stream());
    }

    static void assertAll(final String heading, final Executable... executables) {
        assertAll(heading, Arrays.stream(executables));
    }

    static void assertAll(final String heading, final Stream<Executable> executables) {
        final List<Throwable> failures = executables //
                .map(executable -> {
                    try {
                        executable.execute();
                        return null;
                    } catch (final Throwable t) {
                        if (t instanceof OutOfMemoryError) {
                            throw (OutOfMemoryError) t;
                        }
                        return t;
                    }
                }) //
                .filter(Objects::nonNull) //
                .collect(Collectors.toList());

        if (!failures.isEmpty()) {
            final MultipleFailuresError multipleFailuresError = new MultipleFailuresError(heading, failures);
            failures.forEach(multipleFailuresError::addSuppressed);
            throw multipleFailuresError;
        }
    }

    private AssertAll() {
        /* no-op */
    }

}

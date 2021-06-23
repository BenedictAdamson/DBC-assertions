package uk.badamson.dbc.assertions;
/*
 * Copyright 2018,2021 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * https://www.eclipse.org/legal/epl-v20.html
 */

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Function;

final class MethodDoesNotThrow<T, R> extends TypeSafeDiagnosingMatcher<T> {

    @Nonnull
    private final String methodName;

    @Nonnull
    private final Function<T, R> method;

    MethodDoesNotThrow(@Nonnull String methodName, @Nonnull Function<T, R> method) {
        this.methodName = Objects.requireNonNull(methodName, "methodName");
        this.method = Objects.requireNonNull(method, "method");
    }

    @Override
    protected final boolean matchesSafely(T item, Description mismatchDescription) {
        try {
            method.apply(item);
        } catch (Throwable e) {
            mismatchDescription.appendText("failed because it threw exception ");
            mismatchDescription.appendValue(e);
            return false;
        }
        return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("method ");
        description.appendText(methodName);
        description.appendText(" does not throw an exception");
    }

}

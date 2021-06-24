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

final class MethodThrows<T, R, E extends Throwable> extends TypeSafeDiagnosingMatcher<T> {


    @Nonnull
    private final String methodName;

    @Nonnull
    private final Class<E> exception;

    @Nonnull
    private final Function<T, R> method;

    MethodThrows(@Nonnull String methodName, @Nonnull final Class<E> exception, @Nonnull Function<T, R> method) {
        this.methodName = Objects.requireNonNull(methodName, "methodName");
        this.exception = Objects.requireNonNull(exception, "exception");
        this.method = Objects.requireNonNull(method, "method");
    }

    @Override
    protected final boolean matchesSafely(T item, Description mismatchDescription) {
        try {
            method.apply(item);
        } catch (Throwable e) {
            if (exception.isInstance(e)) {
                return true;
            } else {
                mismatchDescription.appendText("failed because it instead threw exception ");
                mismatchDescription.appendValue(e);
                return false;
            }
        }
        mismatchDescription.appendText("failed because it did not throw an exception");
        return false;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("method ");
        description.appendText(methodName);
        description.appendText(" throws an exception of class ");
        description.appendValue(exception);
    }
}

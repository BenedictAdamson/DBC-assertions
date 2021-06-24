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

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Predicate;

final class DelegatingMatcher<T> extends TypeSafeDiagnosingMatcher<T> {

    @Nonnull
    private final String description;

    @Nonnull
    private final Predicate<T> predicate;

    public DelegatingMatcher(@Nonnull String description, @Nonnull Predicate<T> predicate) {
        this.description = Objects.requireNonNull(description, "description");
        this.predicate = Objects.requireNonNull(predicate, "predicate");
    }

    @Override
    protected boolean matchesSafely(T item, Description mismatchDescription) {
        final boolean ok;
        try {
            ok = predicate.test(item);
        } catch (Exception e) {
            mismatchDescription.appendText("failed because predicate threw ");
            mismatchDescription.appendValue(e);
            return false;
        }
        if (!ok) {
            mismatchDescription.appendText("not satisfied");
        }
        return ok;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(this.description);
    }
}

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
import java.util.function.BiPredicate;
import java.util.function.Function;

final class FeaturesHaveRelationshipMatcher<T, U, V> extends TypeSafeDiagnosingMatcher<T> {

    @Nonnull
    private final String description;

    @Nonnull
    private final Function<T, U> get1;

    @Nonnull
    private final Function<T, V> get2;

    @Nonnull
    private final BiPredicate<U, V> predicate;

    FeaturesHaveRelationshipMatcher(@Nonnull String description, @Nonnull Function<T, U> get1, @Nonnull Function<T, V> get2, @Nonnull BiPredicate<U, V> predicate) {
        this.description = Objects.requireNonNull(description, "description");
        this.get1 = Objects.requireNonNull(get1, "get1");
        this.get2 = Objects.requireNonNull(get2, "get2");
        this.predicate = Objects.requireNonNull(predicate, "predicate");
    }

    @Override
    protected boolean matchesSafely(T item, Description mismatchDescription) {
        final U f1;
        final V f2;
        try {
            f1 = get1.apply(item);
            f2 = get2.apply(item);
        } catch (Exception e) {
            mismatchDescription.appendText("failed because an accessor function threw ");
            mismatchDescription.appendValue(e);
            return false;
        }
        final boolean ok;
        try {
            ok = predicate.test(f1, f2);
        } catch (Exception e) {
            mismatchDescription.appendText("failed because the predicate threw ");
            mismatchDescription.appendValue(e);
            return false;
        }
        if (!ok) {
            mismatchDescription.appendText("not satisfied, with attribute values ");
            mismatchDescription.appendValue(f1);
            mismatchDescription.appendText(" and ");
            mismatchDescription.appendValue(f2);
        }
        return ok;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(this.description);
    }
}

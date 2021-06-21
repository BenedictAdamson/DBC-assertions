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

/**
 * <p>A {@linkplain org.hamcrest.Matcher  matcher} that checks whether the object being  matched has a particular relationship with another given object of the same class.</p>
 * <p>To use, implement {@link #matchesSafely(Object, Object, Description)}.</p>
 */
public abstract class PairwiseMatcher<T> extends TypeSafeDiagnosingMatcher<T> {
    @Nonnull
    private final T other;

    protected PairwiseMatcher(@Nonnull final T other) {
        this.other = Objects.requireNonNull(other, "other");
    }

    @Override
    protected final boolean matchesSafely(T item, Description mismatchDescription) {
        return matchesSafely(item, other, mismatchDescription);
    }

    /**
     * <p>Evaluates the relationship for two items.</p>
     *
     * @param item1               The first item to evaluate the relationship against.
     * @param item2               The second item to evaluate the relationship against.
     * @param mismatchDescription The description to be built or appended to.
     * @return whether the expected relationship holds between {@code item1} and {@code item2}.
     */
    protected abstract boolean matchesSafely(@Nonnull T item1, @Nonnull T item2, @Nonnull Description mismatchDescription);
}// class

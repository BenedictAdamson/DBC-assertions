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
 * <p>A {@linkplain org.hamcrest.Matcher  matcher} that checks whether the object being  matched has a particular relationship with two other given objects of the same class.</p>
 * <p>To use, implement {@link #matchesSafely(Object, Object, Object, Description)}.</p>
 */
public abstract class TripleMatcher<T> extends TypeSafeDiagnosingMatcher<T> {
    @Nonnull
    private final T item2;
    @Nonnull
    private final T item3;

    protected TripleMatcher(@Nonnull final T item2, @Nonnull final T item3) {
        this.item2 = Objects.requireNonNull(item2, "item2");
        this.item3 = Objects.requireNonNull(item3, "item3");
    }

    @Override
    protected final boolean matchesSafely(T item1, Description mismatchDescription) {
        return matchesSafely(item1, item2, item3, mismatchDescription);
    }

    /**
     * <p>Evaluates the relationship for three items.</p>
     *
     * @param item1               The first item to evaluate the relationship against.
     * @param item2               The second item to evaluate the relationship against.
     * @param item3               The third item to evaluate the relationship against.
     * @param mismatchDescription The description to be built or appended to.
     * @return whether the expected relationship holds between {@code item1}, {@code item2} and {@code item3}.
     */
    protected abstract boolean matchesSafely(@Nonnull T item1, @Nonnull T item2, @Nonnull T item3, @Nonnull Description mismatchDescription);
}// class

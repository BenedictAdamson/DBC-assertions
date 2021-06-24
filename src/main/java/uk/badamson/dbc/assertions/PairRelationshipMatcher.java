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
 * <p>A {@linkplain org.hamcrest.Matcher  matcher} that checks whether the object being matched has a particular relationship with another given object.</p>
 * <p>To use, implement {@link #matchesSafely(Object, Object, Description)}.</p>
 */
public abstract class PairRelationshipMatcher<T, U> extends TypeSafeDiagnosingMatcher<T> {
    @Nonnull
    private final U other;

    /**
     * @param other The other object to check the relationship with.
     */
    protected PairRelationshipMatcher(@Nonnull final U other) {
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
     * @param mismatchDescription The description appended a failure message to if, and only if, tje relationship does not hold between {@code item1} and {@code item2}.
     * @return whether the expected relationship holds between {@code item1} and {@code item2}.
     */
    protected abstract boolean matchesSafely(@Nonnull T item1, @Nonnull U item2, @Nonnull Description mismatchDescription);
}// class

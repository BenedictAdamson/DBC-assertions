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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Function;

final class HasEntitySemantics<T, U> extends PairRelationshipMatcher<T> {

    @Nonnull
    private final Function<T, U> valueOfId;

    HasEntitySemantics(@Nonnull final T other, @Nonnull final Function<T, U> valueOfId) {
        super(other);
        this.valueOfId = Objects.requireNonNull(valueOfId, "valueOfId");
    }

    @Nullable
    private U getId(@Nonnull T item, @Nonnull Description mismatchDescription) {
        U id = null;
        try {
            id = valueOfId.apply(item);
            if (id == null) {
                mismatchDescription.appendText("failed because the ID was null");
            }
        } catch (Exception e) {
            mismatchDescription.appendText("failed because getting the ID threw exception ");
            mismatchDescription.appendValue(e);
        }
        return id;
    }

    @Override
    protected boolean matchesSafely(@Nonnull T item1, @Nonnull T item2, @Nonnull Description mismatchDescription) {
        final U id1 = getId(item1, mismatchDescription);
        final U id2 = getId(item2, mismatchDescription);
        boolean ok = id1 != null && id2 != null;
        final Boolean equals = Safe.equals(item1, item2, mismatchDescription);
        ok = ok && equals != null;
        final Boolean equalIds = Safe.equals(id1, id2, mismatchDescription);
        if (equalIds == null) {
            mismatchDescription.appendText(" for IDs");
            ok = false;
        }
        if (ok && (equals.booleanValue() != equalIds.booleanValue())) {
            mismatchDescription.appendText("not satisfied");
            ok = false;
        }
        return ok;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("has entity semantics");
    }
}// class

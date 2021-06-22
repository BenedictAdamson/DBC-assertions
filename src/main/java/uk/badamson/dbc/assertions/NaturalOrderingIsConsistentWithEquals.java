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

final class NaturalOrderingIsConsistentWithEquals<T extends Comparable<T>> extends PairMatcher<T> {
    NaturalOrderingIsConsistentWithEquals(@Nonnull T other) {
        super(other);
    }

    @Override
    protected boolean matchesSafely(@Nonnull T item1, @Nonnull T item2, @Nonnull Description mismatchDescription) {
        boolean ok = true;
        final Integer compareTo = Safe.compareTo(item1, item2, mismatchDescription);
        ok = ok && (compareTo != null);
        final Boolean equals = Safe.equals(item1, item2, mismatchDescription);
        ok = ok && (equals != null);
        if (ok && !(compareTo == 0 == equals)) {
            mismatchDescription.appendText("not satisfied");
            ok = false;
        }
        return ok;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("natural ordering is consistent with equals");
    }
}// class

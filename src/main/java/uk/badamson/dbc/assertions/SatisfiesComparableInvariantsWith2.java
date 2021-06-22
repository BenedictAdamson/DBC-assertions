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
import org.hamcrest.Matcher;

import javax.annotation.Nonnull;

final class SatisfiesComparableInvariantsWith2 {
    static <T extends Comparable<T>> Matcher<T> create(@Nonnull T item2, @Nonnull T item3) {
        return new CompareToIsTransitive<>(item2, item3);
    }

    private static class CompareToIsTransitive<T extends Comparable<T>> extends TripleMatcher<T> {
        CompareToIsTransitive(@Nonnull T item2, @Nonnull T item3) {
            super(item2, item3);
        }

        @SuppressWarnings("ConstantConditions")
        @Override
        protected boolean matchesSafely(@Nonnull T item1, @Nonnull T item2, @Nonnull T item3, @Nonnull Description mismatchDescription) {
            boolean ok = true;
            final Integer c12 = Safe.compareTo(item1, item2, mismatchDescription);
            final Integer c23 = Safe.compareTo(item2, item3, mismatchDescription);
            final Integer c13 = Safe.compareTo(item1, item3, mismatchDescription);
            ok = ok && c12 != null && c23 != null && c13 != null;
            if (ok && c12 > 0 && c23 > 0 && !(c13 > 0)) {
                mismatchDescription.appendText("not satisfied");
                ok = false;
            }
            return ok;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("compareTo is transitive");
        }
    }// class
}

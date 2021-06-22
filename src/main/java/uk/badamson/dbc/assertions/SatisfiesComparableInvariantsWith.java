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
import org.hamcrest.Matchers;

import javax.annotation.Nonnull;
import java.util.Objects;

import static org.hamcrest.Matchers.allOf;

final class SatisfiesComparableInvariantsWith {

    static <T extends Comparable<T>> Matcher<T> create(@Nonnull T other) {
        Objects.requireNonNull(other, "other");
        return Matchers.describedAs("satisfies pairwise Comparable interface invariants with " + Safe.toString(other), allOf(
                new CompareToIsSymmetric<>(other)
        ));
    }

    private static final class CompareToIsSymmetric<T extends Comparable<T>> extends PairMatcher<T> {
        CompareToIsSymmetric(@Nonnull T other) {
            super(other);
        }

        @Override
        protected boolean matchesSafely(@Nonnull T item1, @Nonnull T item2, @Nonnull Description mismatchDescription) {
            boolean ok = true;
            final Integer c12 = Safe.compareTo(item1, item2, mismatchDescription);
            final Integer c21 = Safe.compareTo(item2, item1, mismatchDescription);
            ok = ok && c12 != null && c21 != null;
            if (ok && Integer.signum(c12) != -Integer.signum(c21)) {
                mismatchDescription.appendText("not satisfied");
                ok = false;
            }
            return ok;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("compareTo is symmetric");
            description.appendValue(description);
        }
    }// class
}

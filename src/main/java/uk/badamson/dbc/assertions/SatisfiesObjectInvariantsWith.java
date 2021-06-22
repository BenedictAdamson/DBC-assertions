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

public class SatisfiesObjectInvariantsWith {

    private SatisfiesObjectInvariantsWith() {
        assert false;// should not instance
    }

    static Matcher<Object> create(@Nonnull Object other) {
        Objects.requireNonNull(other, "other");
        return Matchers.describedAs("satisfies pairwise Object class invariants with " + Safe.toString(other), allOf(
                new EqualityIsSymmetric(other),
                new HashCodeIsConsistentWithEquals(other)
        ));
    }

    private static final class EqualityIsSymmetric extends PairMatcher<Object> {


        EqualityIsSymmetric(@Nonnull Object other) {
            super(other);
        }

        @Override
        protected boolean matchesSafely(@Nonnull Object item1, @Nonnull Object item2, @Nonnull Description mismatchDescription) {
            final boolean equals12;
            final boolean equals21;
            try {
                equals12 = item1.equals(item2);
                equals21 = item2.equals(item1);
            } catch (Exception e) {
                mismatchDescription.appendText("but equals() threw exception ");
                mismatchDescription.appendValue(e);
                return false;
            }
            final boolean ok = equals12 == equals21;
            if (!ok) {
                mismatchDescription.appendText("not satisfied");
            }
            return ok;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("equality is symmetric");
        }

    }// class

    private static final class HashCodeIsConsistentWithEquals extends PairMatcher<Object> {


        HashCodeIsConsistentWithEquals(@Nonnull Object other) {
            super(other);
        }

        @Override
        protected boolean matchesSafely(@Nonnull Object item1, @Nonnull Object item2, @Nonnull Description mismatchDescription) {
            boolean ok = true;
            boolean equals = false;
            try {
                equals = item1.equals(item2);
            } catch (Exception e) {
                mismatchDescription.appendText("but equals() threw exception ");
                mismatchDescription.appendValue(e);
                ok = false;
            }
            int hasCode1 = 0;
            int hashCode2 = 0;
            try {
                hasCode1 = item1.hashCode();
                hashCode2 = item2.hashCode();
            } catch (Exception e) {
                mismatchDescription.appendText("but hashCode() threw exception ");
                mismatchDescription.appendValue(e);
                ok = false;
            }
            if (ok && equals && hasCode1 != hashCode2) {
                mismatchDescription.appendText("not satisfied");
                ok = false;
            }
            return ok;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("hashCode() is consistent with equals()");
        }


    }// class
}

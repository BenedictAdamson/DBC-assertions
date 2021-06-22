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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import javax.annotation.Nonnull;

import static org.hamcrest.Matchers.allOf;

@SuppressFBWarnings(justification = "Checking exceptions", value = "RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
final class SatisfiesComparableInvariants {

    private SatisfiesComparableInvariants() {
        assert false;// must not instance
    }


    static <T extends Comparable<T>> Matcher<T> create() {
        return Matchers.describedAs("satisfies Comparable interface invariants",
                allOf(
                        new CompareToNullThrowsNPE<T>(),
                        /*
                         * For completeness, check that this.compareTo(this) does not throw an
                         * exception, although it is unlikely that a faulty implementation would throw
                         * an exception.
                         */
                        new CompareToSelfDoesNotThrowException<T>()
                ));
    }

    private static final class CompareToNullThrowsNPE<T extends Comparable<T>> extends TypeSafeDiagnosingMatcher<T> {
        @SuppressWarnings({"ResultOfMethodCallIgnored", "ConstantConditions"})
        @Override
        protected boolean matchesSafely(T item, Description mismatchDescription) {
            try {
                item.compareTo(null);
            } catch (final NullPointerException e) {
                return true;// the required behaviour
            } catch (final Exception e) {
                /*
                 * It is unlikely that a faulty compareTo would throw any other kind of
                 * exception, but provide good diagnostics just in case.
                 */
                mismatchDescription.appendText("but threw a ");
                mismatchDescription.appendValue(e);
                return false;
            }
            mismatchDescription.appendText("but did not throw an exception");
            return false;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("compareToNull(null) throws a NullPointerException");
        }
    }// class

    private static final class CompareToSelfDoesNotThrowException<T extends Comparable<T>> extends MethodDoesNotThrowException<T> {
        @SuppressWarnings({"EqualsWithItself", "ResultOfMethodCallIgnored"})
        @Override
        protected void callMethod(@Nonnull T item) {
            item.compareTo(item);
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("this.compareTo(this) does not throw an exception");
        }
    }// class
}

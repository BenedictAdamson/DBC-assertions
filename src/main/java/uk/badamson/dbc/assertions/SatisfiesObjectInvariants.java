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

@SuppressFBWarnings(justification = "Checking contract", value = "EC_NULL_ARG")
final class SatisfiesObjectInvariants {

    private SatisfiesObjectInvariants() {
        assert false;// must not instance
    }

    @Nonnull
    static Matcher<Object> create() {
        return Matchers.describedAs("satisfies Object class invariants",
                Matchers.allOf(
                        new ToStringDoesNotThrowException(),
                        new HashCodeDoesNotThrowException(),
                        new EqualsSelf(),
                        new NeverEqualsNull()
                ));
    }

    private static final class EqualsSelf extends TypeSafeDiagnosingMatcher<Object> {
        @Override
        protected boolean matchesSafely(Object item, Description mismatchDescription) {
            final Boolean equals = Safe.equals(item, item, mismatchDescription);
            if (equals == Boolean.FALSE) {
                mismatchDescription.appendText("not satisfied");
            }
            return equals == Boolean.TRUE;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("is equivalent to itself");
        }
    }// class

    private static final class NeverEqualsNull extends TypeSafeDiagnosingMatcher<Object> {
        @Override
        protected boolean matchesSafely(Object item, Description mismatchDescription) {
            final Boolean equals = Safe.equals(item, null, mismatchDescription);
            if (equals == Boolean.TRUE) {
                mismatchDescription.appendText("not satisfied");
            }
            return equals == Boolean.FALSE;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("is not equivalent to null");
        }
    }// class

    private static final class ToStringDoesNotThrowException extends MethodDoesNotThrowException<Object> {

        @SuppressWarnings("ResultOfMethodCallIgnored")
        @Override
        protected void callMethod(@Nonnull Object item) throws RuntimeException {
            item.toString();
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("toString() does not throw exceptions");
        }
    }// class

    private static final class HashCodeDoesNotThrowException extends MethodDoesNotThrowException<Object> {

        @SuppressWarnings("ResultOfMethodCallIgnored")
        @Override
        protected void callMethod(@Nonnull Object item) throws RuntimeException {
            item.hashCode();
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("hashCode() does not throw exceptions");
        }
    }// class

}

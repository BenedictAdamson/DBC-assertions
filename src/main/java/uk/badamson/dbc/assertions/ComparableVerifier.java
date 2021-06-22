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
import java.util.Objects;

import static org.hamcrest.Matchers.allOf;

/**
 * <p>
 * Verification methods to assist in unit testing classes that implement the
 * {@link Comparable} interface.
 */
@SuppressFBWarnings(justification = "Checking exceptions", value = "RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
public final class ComparableVerifier {

    private ComparableVerifier() {
        assert false;// must not instance
    }

    static <T extends Comparable<T>> Matcher<T> satisfiesInvariants() {
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


    static <T extends Comparable<T>> Matcher<T> satisfiesInvariantsWith(@Nonnull T other) {
        Objects.requireNonNull(other, "other");
        return Matchers.describedAs("satisfies pairwise Comparable interface invariants with " + Safe.toString(other), allOf(
                new CompareToIsSymmetric<>(other)
        ));
    }

    /**
     * <p>
     * Provide a {@linkplain Matcher matcher}
     * that matches if, and only if, the object being matched
     * satisfies all the  three-way relationship
     * invariants imposed by the  {@link Comparable} interface.
     * </p>
     *
     * <p>
     * This is a supplement to the {@link #satisfiesInvariantsWith(Comparable)}
     * method, provided out of completeness, for use in very thorough unit tests. In
     * practice, its invariants are unlikely to be broken if the
     * {@link #satisfiesInvariantsWith(Comparable)} invariants are all met.
     * </p>
     */
    public static <T extends Comparable<T>> Matcher<T> satisfiesInvariantsWith(@Nonnull T item2, @Nonnull T item3) {
        return new CompareToIsTransitive<>(item2, item3);
    }

    private static class CompareToIsTransitive<T extends Comparable<T>> extends TripleMatcher<T> {
        CompareToIsTransitive(@Nonnull T item2, @Nonnull T item3) {
            super(item2, item3);
        }

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

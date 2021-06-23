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
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import static org.hamcrest.Matchers.allOf;

@SuppressFBWarnings(justification = "Checking exceptions", value = "RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
final class SatisfiesComparableInvariants {

    private SatisfiesComparableInvariants() {
        assert false;// must not instance
    }


    static <T extends Comparable<T>> Matcher<T> create() {
        return Matchers.describedAs("satisfies Comparable interface invariants",
                allOf(
                        new MethodThrows<>("compareTo(null)", NullPointerException.class, c -> c.compareTo(null)),
                        /*
                         * For completeness, check that this.compareTo(this) does not throw an
                         * exception, although it is unlikely that a faulty implementation would throw
                         * an exception.
                         */
                        new MethodDoesNotThrow<T>("compareTo(this)", c -> c.compareTo(c))
                ));
    }


}

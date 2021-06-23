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

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Function;

final class DelegatingFeatureMatcher<T, U> extends FeatureMatcher<T, U> {

    @Nonnull
    private final Function<T, U> get;

    DelegatingFeatureMatcher(@Nonnull Matcher<? super U> subMatcher, @Nonnull String featureName, @Nonnull Function<T, U> get) {
        super(subMatcher, featureName, featureName);
        this.get = Objects.requireNonNull(get, "get");
    }

    @Override
    protected U featureValueOf(T actual) {
        try {
            return get.apply(actual);
        } catch (Exception e) {
            return null;
        }
    }
}

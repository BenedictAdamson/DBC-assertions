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
import java.util.Objects;
import java.util.function.Function;

final class HasValueSemantics<T, U> extends PairRelationshipMatcher<T> {

    @Nonnull
    private final String attributeName;
    @Nonnull
    private final Function<T, U> valueOfAttribute;

    HasValueSemantics(@Nonnull T other, @Nonnull String attributeName, @Nonnull Function<T, U> valueOfAttribute) {
        super(other);
        this.attributeName = Objects.requireNonNull(attributeName, "attributeName");
        this.valueOfAttribute = Objects.requireNonNull(valueOfAttribute, "valueOfAttribute");
    }

    @Override
    protected boolean matchesSafely(@Nonnull T item1, @Nonnull T item2, @Nonnull Description mismatchDescription) {
        boolean ok = true;
        U attribute1 = null;
        U attribute2 = null;
        try {
            attribute1 = valueOfAttribute.apply(item1);
            attribute2 = valueOfAttribute.apply(item2);
        } catch (Exception e) {
            mismatchDescription.appendText("failed because getting the attribute threw exception ");
            mismatchDescription.appendValue(e);
            ok = false;
        }
        final Boolean equals = Safe.equals(item1, item2, mismatchDescription);
        ok = ok && equals != null;
        final Boolean equalAttributes = Safe.equals(attribute1, attribute2, mismatchDescription);
        if (equalAttributes == null) {
            mismatchDescription.appendText(" for the attributes");
            ok = false;
        }
        if (ok && equals && !equalAttributes) {
            mismatchDescription.appendText("not satisfied");
            ok = false;
        }
        return ok;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("has value semantics with attribute ");
        description.appendText(attributeName);
    }
}// class

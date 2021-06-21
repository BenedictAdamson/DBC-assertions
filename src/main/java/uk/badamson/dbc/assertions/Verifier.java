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

import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * A {@linkplain FunctionalInterface functional interface} representing a method
 * that <i>verifies</i> whether a value is correct, throwing an
 * {@link AssertionError} if the value is incorrect.
 *
 * @see Consumer
 */
@FunctionalInterface
public interface Verifier<T> {

    /**
     * <p>
     * Verify whether a given object reference is <dfn>correct</dfn>.
     * </p>
     * <p>
     * In practice, if the method throws any kind of exception it is likely to be
     * interpreted as a failure of the verification check, but an
     * {@link AssertionError} should be the normal manner of doing so. If a
     * verification check can fail by throwing any other kind of exception, the
     * implementation should consider catching that exception and rethrowing a
     * {@linkplain AssertionError#AssertionError(Object) chained AssertionError}
     * exception.
     * </p>
     *
     * @param t The object reference to verify.
     * @throws AssertionError If, and only if, {@code t} fails the verification check.
     */
    void verify(@Nullable T t) throws AssertionError;
}

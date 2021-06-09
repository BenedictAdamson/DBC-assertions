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

    void verify(T t) throws AssertionError;
}

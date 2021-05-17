package uk.badamson.dbc.assertions;
/*
 * Copyright 2015-2021 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * This is an adapted copy of the Executable interface of JUnit 5:
 * https://github.com/junit-team/junit5/blob/main/junit-jupiter-api/src/main/java/org/junit/jupiter/api/function/Executable.java
 * The uk.badamson.dbc.assertions package has it so it does not have a dependency on JUnit 5.
 */

@FunctionalInterface
interface Executable {

    void execute() throws Throwable;

}

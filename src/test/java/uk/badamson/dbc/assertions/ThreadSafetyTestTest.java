package uk.badamson.dbc.assertions;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/*
 * Copyright 2018,2021 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * https://www.eclipse.org/legal/epl-v20.html
 */

public class ThreadSafetyTestTest {

    @Nested
    public class Multiple {

        @Test
        public void ok() {
            final CountDownLatch ready = new CountDownLatch(1);
            final List<Future<Void>> futures = new ArrayList<>(2);
            futures.add(ThreadSafetyTest.runInOtherThread(ready, () -> {
                // Do nothing
            }));
            ready.countDown();
            ThreadSafetyTest.get(futures);
        }

        @Test
        public void throwsAssertionError() {
            final CountDownLatch ready = new CountDownLatch(1);
            final List<Future<Void>> futures = new ArrayList<>(2);
            futures.add(ThreadSafetyTest.runInOtherThread(ready, () -> {
                throw new AssertionError("Fake");
            }));
            ready.countDown();
            assertThrows(AssertionError.class, () -> ThreadSafetyTest.get(futures));
        }

        @Test
        public void throwsRuntimeException() {
            final CountDownLatch ready = new CountDownLatch(1);
            final List<Future<Void>> futures = new ArrayList<>(2);
            futures.add(ThreadSafetyTest.runInOtherThread(ready, () -> {
                throw new RuntimeException("Fake");
            }));
            ready.countDown();
            assertThrows(RuntimeException.class, () -> ThreadSafetyTest.get(futures));
        }
    }// class

    @Nested
    public class One {

        @Test
        public void ok() {
            final CountDownLatch ready = new CountDownLatch(1);
            final var future = ThreadSafetyTest.runInOtherThread(ready, () -> {
                // Do nothing
            });
            ready.countDown();
            ThreadSafetyTest.get(future);
        }

        @Test
        public void throwsAssertionError() {
            final CountDownLatch ready = new CountDownLatch(1);
            final var future = ThreadSafetyTest.runInOtherThread(ready, () -> {
                throw new AssertionError("Fake");
            });
            ready.countDown();
            assertThrows(AssertionError.class, () -> ThreadSafetyTest.get(future));
        }

        @Test
        public void throwsRuntimeException() {
            final CountDownLatch ready = new CountDownLatch(1);
            final var future = ThreadSafetyTest.runInOtherThread(ready, () -> {
                throw new RuntimeException("Fake");
            });
            ready.countDown();
            assertThrows(RuntimeException.class, () -> ThreadSafetyTest.get(future));
        }
    }// class

}

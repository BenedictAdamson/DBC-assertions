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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ThreadTest {

    public static void get(final Future<Void> future) {
        try {
            future.get();
        } catch (final InterruptedException e) {
            throw new AssertionError(e);
        } catch (final ExecutionException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof AssertionError) {
                throw (AssertionError) cause;
            } else if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            } else {
                throw new AssertionError(e);
            }
        }
    }

    public static void get(final List<Future<Void>> futures) {
        final List<Throwable> exceptions = new ArrayList<>(futures.size());
        for (final var future : futures) {
            try {
                get(future);
            } catch (Exception | AssertionError e) {
                exceptions.add(e);
            }
        }
        final int nExceptions = exceptions.size();
        if (0 < nExceptions) {
            final Throwable e = exceptions.get(0);
            for (int i = 1; i < nExceptions; ++i) {
                e.addSuppressed(exceptions.get(i));
            }
            if (e instanceof AssertionError) {
                throw (AssertionError) e;
            } else if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new AssertionError(e);
            }
        }
    }

    public static Future<Void> runInOtherThread(final CountDownLatch ready, final Runnable operation) {
        final CompletableFuture<Void> future = new CompletableFuture<Void>();
        final Thread thread = new Thread(() -> {
            try {
                ready.await();
                operation.run();
            } catch (final Throwable e) {
                future.completeExceptionally(e);
                return;
            }
            future.complete(null);
        });
        thread.start();
        return future;
    }
}

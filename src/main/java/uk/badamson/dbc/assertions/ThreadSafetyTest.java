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
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import org.opentest4j.AssertionFailedError;

/**
 * <p>
 * Auxiliary methods to assist in unit testing classes that should be
 * {@link ThreadSafe}.
 * </p>
 * <p>
 * Testing thread safety requires setting up and coordinating multiple threads
 * attempting to access the objects being tested, then collecting test results
 * from those threads. The methods in this class assist that by using a
 * {@link CountDownLatch} to coordinate the threads, and {@link Future} objects
 * to access the results provided by each thread. The
 * {@link #runInOtherThread(CountDownLatch, Runnable)} method is for creating
 * one other thread for a test. The {@link #get(Future)} method provides a
 * convenient means for accessing the result of a thread. The {@link #get(List)}
 * method provides a convenient means for accessing the results of multiple
 * threads.
 * </p>
 * <p>
 * Use the methods in tests like this:
 * </p>
 *
 * <pre>
 * {@code @RepeatedTest(4)}
 * public void multipleThreads() {
 *     final int nThreads = 32;
 *     final {@code List<Future<Void>> futures = new ArrayList<>(nThreads);}
 *     final CountDownLatch ready = new CountDownLatch(1);
 *     final var random = new Random();
 *
 *     final var thing = new Thing();
 *     {@code for (int t = 0; t < nThreads; ++t) {}
 *         {@code futures.add(ThreadSafetyTest.runInOtherThread(ready, () -> {}
 *             thing.mutate(random.nextInt());
 *
 *             ObjectTest.assertInvariants(thing);
 *         }));
 *     }
 *
 *     ready.countDown();
 *
 *     ThreadSafetyTest.get(futures);
 * }
 * </pre>
 */
public final class ThreadSafetyTest {

    /**
     * <p>
     * A convenience method for delegating to a {@link Future#get()} that provides
     * access to the result of running some unit testing code.
     * </p>
     * This method rethrows an {@link InterruptedException} as an
     * {@link AssertionError}: it interprets thread interruption as a test failure.
     * It unwraps a throw {@link ExecutionException}, rethrowing the
     * {@linkplain Throwable#getCause() wrapped exception}, unless the wrapped
     * exception is a checked exception, in which case it is rethrown wrapped in a
     * {@link RuntimeException}.
     * </p>
     * <p>
     * This method is intended to be used with the result of
     * {@link #runInOtherThread(CountDownLatch, Runnable)}. See {@link #get(List)}
     * for a similar method for accessing the results of multiple threads.
     * </p>
     *
     * @param future
     *            The result of the asynchronous computation to be examined.
     * @throws NullPointerException
     *             If {@code future} is null
     * @throws Error
     *             If the asynchronous computation completed by throwing an
     *             {@link Error}. The exception thrown will be the same as the
     *             exception that the asynchronous computation threw. In particular,
     *             this case includes {@link AssertionError}, and thus any test
     *             failures found by the asynchronous computation.
     * @throws RuntimeException
     *             <ul>
     *             <li>If the asynchronous computation completed by throwing an
     *             {@link RuntimeException}. The exception thrown will be the same
     *             as the exception that the asynchronous computation threw.</li>
     *             <li>If the asynchronous computation completed by throwing a
     *             checked exception (An {@link Exception} that is not a
     *             {@link RuntimeException}), in which case the checked exception
     *             can be accessed through {@link RuntimeException#getCause()}.</li>
     *             </ul>
     */
    public static void get(final Future<Void> future) {
        Objects.requireNonNull(future, "future");

        try {
            future.get();
        } catch (final InterruptedException e) {
            throw new AssertionFailedError("Test threads should not be interrupted", null, e);
        } catch (final ExecutionException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof Error) {
                throw (Error) cause;
            } else if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            } else {// A checked exception
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * <p>
     * A convenience method for delegating to {@link Future#get()} for a
     * {@link List} of {@link Future}s that provide access to the results of running
     * some unit testing code.
     * </p>
     * This method delegates to {@link #get(Future)} for each of the futures in the
     * list. If those calls result in an exception being thrown the method rethrows
     * that exception. If more than one of those calls throws an exception, the
     * method rethrows the first such exception, with the other exceptions
     * {@linkplain Exception#addSuppressed(Throwable) added as suppressed
     * exceptions}.
     * </p>
     * <p>
     * This method is intended to be used with the result of
     * {@link #runInOtherThread(CountDownLatch, Runnable)}. See {@link #get(List)}
     * for a similar method for accessing the results of multiple threads.
     * </p>
     *
     * @param futures
     *            The results of the asynchronous computations to be examined.
     *
     * @throws NullPointerException
     *             <ul>
     *             <li>If {@code futures} is null.</li>
     *             <li>If {@code futures} has a null element.</li>
     *             </ul>
     * @throws Error
     *             If an asynchronous computation completed by throwing an
     *             {@link Error}.
     * @throws RuntimeException
     *             <ul>
     *             <li>If an asynchronous computation completed by throwing an
     *             {@link RuntimeException}.</li>
     *             <li>If the asynchronous computation completed by throwing a
     *             checked exception.</li>
     *             </ul>
     */
    public static void get(final List<Future<Void>> futures) {
        Objects.requireNonNull(futures, "futures");

        final List<Throwable> exceptions = new ArrayList<>(futures.size());
        for (final var future : futures) {
            try {
                get(future);
            } catch (RuntimeException | Error e) {
                exceptions.add(e);
            }
        }
        final int nExceptions = exceptions.size();
        if (0 < nExceptions) {
            final Throwable e = exceptions.get(0);
            for (int i = 1; i < nExceptions; ++i) {
                e.addSuppressed(exceptions.get(i));
            }
            if (e instanceof Error) {
                throw (Error) e;
            } else if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {// Never happens
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * <p>
     * Create a blocked thread that will perform a given {@linkplain Runnable
     * operation} once a {@linkplain CountDownLatch count-down latch} reaches zero,
     * and providing a {@link Future} to access the result of that operation.
     * </p>
     * <p>
     * The result of the operation can be got from the returned future. The
     * {@link Future#get()} method will return immediately if the operation
     * completed normally. However, if the operation threw a {@link Throwable}, the
     * {@link Future#get()} method will instead throw an {@link ExecutionException}
     * wrapping the thrown {@link Throwable}. However, rather than using
     * {@link Future#get()} directly, you will probably find {@link #get(Future)}
     * more convenient.
     * </p>
     *
     * @param ready
     *            The count-down latch for controlling the start of the
     *            {@code operation}.
     * @param operation
     *            The operation to be done in the thread.
     * @return A means of accessing the result of the operation.
     *
     * @throws NullPointerException
     *             <ul>
     *             <li>If {@code ready} is null.</li>
     *             <li>If {@code operation} is null.</li>
     *             </ul>
     */
    @Nonnull
    public static Future<Void> runInOtherThread(@Nonnull final CountDownLatch ready,
            @Nonnull final Runnable operation) {
        Objects.requireNonNull(ready, "ready");
        Objects.requireNonNull(operation, "operation");

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

    private ThreadSafetyTest() {
        assert false;// must not instance
    }
}

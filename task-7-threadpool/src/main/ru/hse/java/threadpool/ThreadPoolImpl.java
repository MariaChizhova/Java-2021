package ru.hse.java.threadpool;

import org.jetbrains.annotations.NotNull;
import ru.hse.java.threadpool.exceptions.LightExecutionException;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.function.Supplier;

public class ThreadPoolImpl implements ThreadPool {
    private final Thread[] threads;
    private final LinkedList<LightFutureImpl<?>> tasks;

    public ThreadPoolImpl(int numberOfThreads) {
        if (numberOfThreads <= 0) {
            throw new IllegalArgumentException("Number of threads should be positive.");
        }
        threads = new Thread[numberOfThreads];
        tasks = new LinkedList<>();
        for (int i = 0; i < getNumberOfThreads(); i++) {
            threads[i] = new Thread(new ThreadPoolWorker());
            threads[i].start();
        }
    }

    @Override
    public @NotNull <R> LightFuture<R> submit(Supplier<@NotNull R> supplier) {
        LightFutureImpl<R> task = new LightFutureImpl<>(supplier);
        synchronized (tasks) {
            tasks.add(task);
            tasks.notify();
        }
        return task;
    }

    @Override
    public void shutdown() {
        Arrays.stream(threads).forEach(Thread::interrupt);
    }

    @Override
    public int getNumberOfThreads() {
        return threads.length;
    }

    private class ThreadPoolWorker implements Runnable {
        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    LightFutureImpl<?> task;
                    synchronized (tasks) {
                        while (tasks.isEmpty()) {
                            tasks.wait();
                        }
                        task = tasks.poll();
                    }
                    task.getResult();
                }
            } catch (InterruptedException ignored) {
            }
        }
    }

    private class LightFutureImpl<T> implements LightFuture<T> {
        private volatile boolean isReady = false;
        private final Supplier<T> supplier;
        private T result;
        private LightExecutionException exception;

        private LightFutureImpl(Supplier<T> supplier) {
            this.supplier = supplier;
        }

        public synchronized void getResult() {
            try {
                result = supplier.get();
            } catch (Exception e) {
                if (e.getCause() != null && e.getCause().getClass() == LightExecutionException.class) {
                    exception = new LightExecutionException(e.getCause().getCause());
                } else {
                    exception = new LightExecutionException(e);
                }
            }
            isReady = true;
            notifyAll();
        }

        @Override
        public boolean isReady() {
            return isReady;
        }

        @Override
        public @NotNull T get() throws LightExecutionException {
            if (!isReady) {
                synchronized (this) {
                    while (!isReady) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            throw new LightExecutionException(exception);
                        }
                    }
                }
            }
            if (exception != null) {
                throw exception;
            }
            return result;
        }

        @Override
        public @NotNull <R1> LightFuture<R1> thenApply(Function<T, @NotNull R1> function) {
            return ThreadPoolImpl.this.submit(() -> {
                try {
                    return function.apply(LightFutureImpl.this.get());
                } catch (LightExecutionException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
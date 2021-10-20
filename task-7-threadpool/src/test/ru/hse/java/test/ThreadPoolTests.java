package ru.hse.java.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.hse.java.threadpool.LightFuture;
import ru.hse.java.threadpool.ThreadPool;
import ru.hse.java.threadpool.exceptions.LightExecutionException;
import static org.junit.jupiter.api.Assertions.*;


public class ThreadPoolTests {
    @Test
    void testOneThreadPrimitiveTypes() throws LightExecutionException {
        ThreadPool pool = ThreadPool.create(1);
        LightFuture<Integer> task1 = pool.submit(() -> 2 + 3);
        LightFuture<Integer> task2 = pool.submit(() -> 2 * 4);
        assertEquals(Integer.valueOf(5), task1.get());
        assertEquals(Integer.valueOf(8), task2.get());
    }

    @Test
    void testManyThreadsPrimitiveTypes() throws LightExecutionException {
        ThreadPool pool = ThreadPool.create(5);
        LightFuture<Integer> task1 = pool.submit(() -> 2 + 3);
        LightFuture<Integer> task2 = pool.submit(() -> 2 * 4);
        assertEquals(Integer.valueOf(5), task1.get());
        assertEquals(Integer.valueOf(8), task2.get());
    }

    @Test
    void testNotPositiveNumberOfThreads() {
        assertThrows(IllegalArgumentException.class, () -> ThreadPool.create(-5));
        assertThrows(IllegalArgumentException.class, () -> ThreadPool.create(-3));
        assertThrows(IllegalArgumentException.class, () -> ThreadPool.create(0));
    }

    @Test
    void testException() {
        ThreadPool pool = ThreadPool.create(5);
        for (int i = 0; i < 10; i++) {
            pool.submit(() -> "Not Exception");
        }
        LightFuture<String> taskException = pool.submit(() -> {
            throw new RuntimeException();
        });
        assertThrows(LightExecutionException.class, taskException::get);
        assertThrows(LightExecutionException.class, taskException::get);
    }

    @Test
    void testOneThreadNotPrimitiveTypes() throws LightExecutionException {
        ThreadPool pool = ThreadPool.create(1);
        LightFuture<String> task1 = pool.submit(() -> Integer.valueOf(2 + 3).toString());
        LightFuture<String> task2 = pool.submit(() -> Integer.valueOf(2 * 3).toString());
        assertEquals("5", task1.get());
        assertEquals("6", task2.get());
    }

    @Test
    void testManyThreadsNotPrimitiveTypes() throws LightExecutionException {
        ThreadPool pool = ThreadPool.create(5);
        LightFuture<String> task1 = pool.submit(() -> Integer.valueOf(3 + 3).toString());
        LightFuture<String> task2 = pool.submit(() -> Integer.valueOf(5 * 3).toString());
        assertEquals("6", task1.get());
        assertEquals("15", task2.get());
    }

    @Test
    void testOneThreadMultipleCalls() throws LightExecutionException {
        ThreadPool pool = ThreadPool.create(1);
        LightFuture<String> task1 = pool.submit(() -> "Task1");
        LightFuture<String> task2 = pool.submit(() -> "Task2");
        for (int i = 1; i < 10; i++) {
            assertEquals("Task1", task1.get());
        }
        for (int i = 1; i < 5; i++) {
            assertEquals("Task2", task2.get());
        }
        assertEquals("Task1", task1.get());
    }

    @Test
    void testManyThreadsMultipleCalls() throws LightExecutionException {
        ThreadPool pool = ThreadPool.create(10);
        LightFuture<String> task1 = pool.submit(() -> "Task1");
        LightFuture<String> task2 = pool.submit(() -> "Task2");
        for (int i = 1; i < 10; i++) {
            assertEquals("Task1", task1.get());
        }
        for (int i = 1; i < 5; i++) {
            assertEquals("Task2", task2.get());
        }
        assertEquals("Task1", task1.get());
    }

    @Test
    void testOneThreadThenApplyForExceptionTask() {
        ThreadPool pool = ThreadPool.create(1);
        LightFuture<String> task1 = pool.submit(() -> {
            throw new RuntimeException();
        });
        LightFuture<String> task2 = task1.thenApply(x -> x + "1");
        assertThrows(LightExecutionException.class, task1::get);
        assertThrows(LightExecutionException.class, task2::get);
    }

    @Test
    void testManyThreadsThenApplyForExceptionTask() {
        ThreadPool pool = ThreadPool.create(10);
        LightFuture<String> task1 = pool.submit(() -> {
            throw new RuntimeException();
        });
        LightFuture<String> task2 = task1.thenApply(String::toLowerCase);
        assertThrows(LightExecutionException.class, task1::get);
        assertThrows(LightExecutionException.class, task2::get);
    }

    @Test
    void testOneThreadThenApply() throws LightExecutionException {
        ThreadPool pool = ThreadPool.create(1);
        LightFuture<Integer> task1 = pool.submit(() -> 3);
        LightFuture<Integer>[] tasks = new LightFuture[10];
        for (int i = 0; i < 10; i++) {
            final int ind = i;
            tasks[i] = task1.thenApply(x -> x + ind * 3);
        }
        assertEquals(Integer.valueOf(3), task1.get());
        for (int i = 0; i < 10; i++) {
            assertEquals(Integer.valueOf((i + 1) * 3), tasks[i].get());
        }
    }

    @Test
    void testManyThreadsThenApply() throws LightExecutionException {
        ThreadPool pool = ThreadPool.create(10);
        LightFuture<Integer> task1 = pool.submit(() -> 3);
        LightFuture<Integer>[] tasks = new LightFuture[10];
        for (int i = 0; i < 10; i++) {
            final int ind = i;
            tasks[i] = task1.thenApply(x -> x + ind * 3);
        }
        assertEquals(Integer.valueOf(3), task1.get());
        for (int i = 0; i < 10; i++) {
            assertEquals(Integer.valueOf((i + 1) * 3), tasks[i].get());
        }
    }

    @Test
    void testOneThreadsThenApplyPowers() throws LightExecutionException {
        ThreadPool pool = ThreadPool.create(1);
        LightFuture<Integer>[] tasks = new LightFuture[10];
        tasks[0] = pool.submit(() -> 1);
        for (int i = 1; i < 10; i++) {
            tasks[i] = tasks[i - 1].thenApply(x -> x * 2);
        }
        for (int i = 0; i < 10; i++) {
            assertEquals(Integer.valueOf(1 << i), tasks[i].get());
        }
    }

    @Test
    void testManyThreadsThenApplyPowers() throws LightExecutionException {
        ThreadPool pool = ThreadPool.create(10);
        LightFuture<Integer>[] tasks = new LightFuture[10];
        tasks[0] = pool.submit(() -> 1);
        for (int i = 1; i < 10; i++) {
            tasks[i] = tasks[i - 1].thenApply(x -> x * 2);
        }
        for (int i = 0; i < 10; i++) {
            assertEquals(Integer.valueOf(1 << i), tasks[i].get());
        }
    }

    @Test
    void testThenApplyDifferentTypes() throws LightExecutionException {
        ThreadPool pool = ThreadPool.create(5);
        LightFuture<Integer> task1 = pool.submit(() -> 3);
        LightFuture<String> task2 = task1.thenApply(x -> Integer.valueOf(x + 1).toString());
        LightFuture<String> task3 = task1.thenApply(Object::toString);
        assertEquals(Integer.valueOf(3), task1.get());
        assertEquals("4", task2.get());
        assertEquals("3", task3.get());
    }

    @Test
    void testOneThreadIsReady() throws LightExecutionException {
        ThreadPool pool = ThreadPool.create(1);
        LightFuture<String> task1 = pool.submit(() -> "Task is ready");
        assertEquals("Task is ready", task1.get());
        assertTrue(task1.isReady());
        LightFuture<String> task2 = pool.submit(() -> {
            while (true) {
                Thread.yield();
            }
        });
        assertFalse(task2.isReady());
    }

    @Test
    void testManyThreadsIsReady() throws LightExecutionException {
        ThreadPool pool = ThreadPool.create(10);
        LightFuture<String> task1 = pool.submit(() -> "Task is ready");
        assertEquals("Task is ready", task1.get());
        assertTrue(task1.isReady());
        LightFuture<String> task2 = pool.submit(() -> {
            while (true) {
                Thread.yield();
            }
        });
        assertFalse(task2.isReady());
    }

    @Test
    void testOneThreadShutdown() throws NoSuchFieldException, IllegalAccessException, InterruptedException {
        ThreadPool pool = ThreadPool.create(1);
        for (int i = 0; i < 3; i++) {
            pool.submit(() -> 1);
        }
        var threads = pool.getClass().getDeclaredField("threads");
        threads.setAccessible(true);
        pool.shutdown();
        Thread.sleep(3000);
        for (Thread thread : (Thread[]) threads.get(pool)) {
            assertFalse(thread.isAlive());
        }
    }

    @Test
    void testManyThreadsShutdown() throws NoSuchFieldException, IllegalAccessException, InterruptedException {
        ThreadPool pool = ThreadPool.create(10);
        for (int i = 0; i < 3; i++) {
            pool.submit(() -> 1);
        }
        var threads = pool.getClass().getDeclaredField("threads");
        threads.setAccessible(true);
        pool.shutdown();
        Thread.sleep(3000);
        for (Thread thread : (Thread[]) threads.get(pool)) {
            assertFalse(thread.isAlive());
        }
    }

    @Test
    void testManyThreadsCorrectCountInPool() throws NoSuchFieldException, IllegalAccessException {
        ThreadPool pool = ThreadPool.create(10);
        for (int i = 0; i < 8; i++) {
            pool.submit(() -> {
                double result = 1;
                for (int j = 0; j < 100000; j++) {
                    result += j;
                }
                return result;
            });
        }
        var threads = pool.getClass().getDeclaredField("threads");
        threads.setAccessible(true);
        int countAlive = 0;
        for (Thread thread : (Thread[]) threads.get(pool)) {
            if (thread.isAlive()) {
                countAlive++;
            }
        }
        assertTrue(countAlive > 9);
    }

    @Test
    void chained() {
        ThreadPool pool = ThreadPool.create(5);
        var task1 = pool.submit(() -> 2 / 0);
        var future = task1.thenApply(x -> x + 1).thenApply(y -> y + 1);

        try{
            future.get();
        }catch (LightExecutionException e){
            Assertions.assertEquals(ArithmeticException.class , e.getCause().getClass());
        }
    }


}
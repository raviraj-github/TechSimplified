/**
 * VirtualThreadComparisonExample
 * This Java class serves as an illustrative example, demonstrating how virtual threads can outperform regular
 * execution thread pools in certain cases.
 *
 * <p>
 * Copyright 2023 Ravi Raj
 * <p>
 * MIT License
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

/**
 * This class demonstrates the concurrent execution of tasks using different ExecutorService implementations
 * and measures the total elapsed time for task completion. It compares the performance of a FixedThreadPool
 * and a VirtualThreadPerTaskExecutor (requires JDK 19+).
 *
 * The main method creates a set of tasks represented by the 'runnable' lambda expression and executes them
 * using both ExecutorService implementations. The tasks simulate some work that takes 1 second to complete.
 *
 * The 'executeTasksWithExecutorService' method handles the task execution with the given ExecutorService.
 * It submits the tasks for execution, shuts down the ExecutorService, and waits for all tasks to finish
 * before measuring the total elapsed time.
 *
 * The 'AtomicInteger' is used to maintain a shared counter across the tasks to track the number of completed
 * tasks.
 *
 * The program provides insightful output, displaying the elapsed time for both FixedThreadPool and
 * VirtualThreadPerTaskExecutor, allowing developers to compare the performance of the two approaches.
 *
 * Requirements:
 * 1. JDK 8+ for standard features, JDK 19+ for VirtualThreadPerTaskExecutor.
 *
 * Note: VirtualThreadPerTaskExecutor requires JDK 19 or later to run.
 *       Ensure to update the 'numTasks' variable to adjust the number of tasks to be executed for comparison.
 */

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;


public class VirtualThreadComparisonExample {

    public static void main(String[] args) {
        AtomicInteger atomicInteger = new AtomicInteger();
        Runnable runnable = () -> {
            try {
                Thread.sleep(Duration.ofSeconds(1).toMillis());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            atomicInteger.incrementAndGet();
            //System.out.println("Work Done - " + atomicInteger.incrementAndGet());
        };

        int numTasks = 1000;

        // Using FixedThreadPool
        System.out.println("Execution started using FixedThreadPool..");
        long timeElapsedFT = executeTasksWithExecutorService(Executors.newFixedThreadPool(100), runnable, numTasks);
        System.out.println("Total elapsed time with FixedThreadPool: " + timeElapsedFT + "ms");

        // Using VirtualThreadPerTaskExecutor (requires JDK 19 or later release)
        System.out.println("Execution started using VirtualThreadPerTaskExecutor..");
        long timeElapsedVT = executeTasksWithExecutorService(Executors.newVirtualThreadPerTaskExecutor(), runnable, numTasks);
        System.out.println("Total elapsed time with VirtualThreadPerTaskExecutor: " + timeElapsedVT + "ms");
    }

    /**
     * Executes a given number of tasks using the provided ExecutorService and measures the elapsed time.
     *
     * @param executor   The ExecutorService to execute tasks.
     * @param runnable   The task to be executed.
     * @param numTasks   The number of tasks to execute.
     * @return The total elapsed time in milliseconds.
     */
    private static long executeTasksWithExecutorService(ExecutorService executor, Runnable runnable, int numTasks) {
        Instant start = Instant.now();

        // Submit tasks to the ExecutorService for execution
        for (int i = 0; i < numTasks; i++) {
            executor.submit(runnable);
        }

        // Shutdown the ExecutorService and wait for all tasks to finish
        executor.shutdown();
        while (!executor.isTerminated()) {
            // Wait for all tasks to finish.
        }

        Instant finish = Instant.now();
        return Duration.between(start, finish).toMillis();
    }
}

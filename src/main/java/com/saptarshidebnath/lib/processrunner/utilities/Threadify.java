package com.saptarshidebnath.lib.processrunner.utilities;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class Threadify {
  private static AtomicInteger threadCounter = new AtomicInteger(1);

  public ExecutorService getProcessRunnerExecutorService() {
    ThreadFactory processRunnerThreadFactory;
    processRunnerThreadFactory =
        runnable ->
            new Thread(
                runnable,
                Constants.PROCESS_RUNNER_THREAD_GROUP_NAME + "-" + threadCounter.getAndIncrement());
    return Executors.newCachedThreadPool(processRunnerThreadFactory);
  }
}

package com.saptarshidebnath.processrunner.lib.utilities;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class Threadify {

  public static ExecutorService getProcessRunnerExecutorService() {
    ThreadFactory processRunnerThreadFactory;
    ExecutorService processRunnerExecutorService;
    AtomicInteger threadCounter;
    ThreadGroup processRunnerThreadGroup;
    threadCounter = new AtomicInteger(1);
    processRunnerThreadGroup = new ThreadGroup(Constants.PROCESS_RUNNER_THREAD_GROUP_NAME);
    processRunnerThreadFactory =
        runnable ->
            new Thread(
                processRunnerThreadGroup,
                runnable,
                Utilities.joinString(
                    Constants.PROCESS_RUNNER_THREAD_GROUP_NAME,
                    "-",
                    threadCounter.getAndIncrement() + ""));
    return Executors.newCachedThreadPool(processRunnerThreadFactory);
  }
}

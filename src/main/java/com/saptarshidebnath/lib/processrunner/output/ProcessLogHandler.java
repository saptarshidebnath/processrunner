package com.saptarshidebnath.lib.processrunner.output;

import com.saptarshidebnath.lib.processrunner.process.Configuration;
import com.saptarshidebnath.lib.processrunner.utilities.Constants;
import com.saptarshidebnath.lib.processrunner.utilities.Threadify;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringJoiner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Writes the process logs as single line json of format {@link OutputRecord} to the file as
 * configured in {@link Configuration#getMasterLogFile()}. If {@link
 * Configuration#getMasterLogFile()} returns null, the logs will not be written.
 */
public class ProcessLogHandler {

  private Logger logger = LoggerFactory.getLogger(ProcessLogHandler.class);
  private PrintWriter printWriter;
  private BlockingQueue<OutputRecord> queue;
  private boolean streamingEnabled;
  private ArrayList<Future> inputStreamReadingThreads;
  private ExecutorService executorService;
  private Configuration processConfiguration;
  private Future diskWritingThread;

  /**
   * The construcor of the class {@link ProcessLogHandler}.
   *
   * <p>The class receives a reference of the {@link Process} and {@link Configuration}. The class
   * then internally takes care of creating all the process output reading threads i.e. both {@link
   * OutputSourceType#SYSERR} and {@link OutputSourceType#SYSOUT} and writing the same to the disk
   * in separate thread. The class also provides a blocking method {@link
   * ProcessLogHandler#waitForShutdown()} so that you can wait for all logs to be streamed, written
   * to disk or both.
   *
   * <p>The class internally uses an executor service from {@link
   * Threadify#getProcessRunnerExecutorService()}
   *
   * @param process a object of type {@link Process}
   * @param processConfiguration a reference of type {@link Configuration}
   * @throws IOException in case of oany IOError
   */
  public ProcessLogHandler(Process process, Configuration processConfiguration) throws IOException {
    this.processConfiguration = processConfiguration;
    File masterLogFile = processConfiguration.getMasterLogFile();
    streamingEnabled = processConfiguration.isEnableLogStreaming();
    boolean logsNeedTobeWritten = processConfiguration.getMasterLogFile() != null;

    boolean logsNeedTobeRead = logsNeedTobeWritten || streamingEnabled;
    if (logsNeedTobeRead) {
      this.queue = new LinkedBlockingQueue<>(Constants.CACHE_SIZE);
      executorService = new Threadify().getProcessRunnerExecutorService();
      if (streamingEnabled) {
        logger.info("Logs will be streamed on real time.");
      } else {
        logger.warn("Logs streaming disabled.");
      }
      this.inputStreamReadingThreads = new ArrayList<>();
      //
      // Track SYSOUT
      //
      this.saveInpuStreamToDisk(process.getInputStream(), OutputSourceType.SYSOUT);
      //
      // Track SYSERR
      //
      this.saveInpuStreamToDisk(process.getErrorStream(), OutputSourceType.SYSERR);
      //
      // Write content to DISK
      //
      if (logsNeedTobeWritten) {
        this.printWriter =
            new PrintWriter(
                new OutputStreamWriter(
                    new FileOutputStream(masterLogFile), processConfiguration.getCharset()));
        this.diskWritingThread = executorService.submit(this::writeToDisk);
      } else {
        logger.warn(
            "Logs not written to file as per configuration : {}", processConfiguration.toString());
      }
      //
      // Mark for shutdown after execution is complete.
      //
      this.executorService.shutdown();
      logger.trace("Created ProcessLogHandler. Tracking SYSOUT and SYSERROR");
    } else {
      logger.warn("Log Streaming is not enabled and Master logfile not set. Discarding logs.");
      logger.warn("Configuration received : {}", processConfiguration.toString());
    }
  }

  /**
   * Threadify the writing of the inputStream to disk.
   *
   * <p>Internal method and shouldn't be used externally.
   *
   * @param inputStream {@link InputStream} from {@link Process#getInputStream()} and {@link
   *     Process#getErrorStream()}.
   * @param outputSourceType Type of Output as per {@link OutputSourceType}
   */
  private void saveInpuStreamToDisk(InputStream inputStream, OutputSourceType outputSourceType) {
    inputStreamReadingThreads.add(
        this.executorService.submit(() -> this.readInputStream(inputStream, outputSourceType)));
  }

  /**
   * Blocking method to wait for all all 3 threads to be finished. 2 threads are to read {@link
   * OutputSourceType#SYSOUT} and {@link OutputSourceType#SYSERR}. The last thread is to write the
   * read {@link InputStream} to disk.
   *
   * <p>Call this method to wait for the log handler threads to be finished execution.
   *
   * @throws InterruptedException when the {@link ExecutorService#awaitTermination(long, TimeUnit)}
   *     is interrupted by some other {@link Thread}.
   * @throws ExecutionException when waiting for the disk writer to finish.
   */
  public void waitForShutdown() throws InterruptedException, ExecutionException {
    if (processConfiguration.getMasterLogFile() != null) {
      logger.info("Waiting for all the logs writing thread to shutdown.");
      //
      // Wait for the disk writing thread to stop.
      //
      this.diskWritingThread.get();
      //
      // Wait for the termination of executor service.
      //
      this.executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
      logger.info("Waiting for all the logs writing thread to shutdown.");

    } else {
      logger.error("Masterfile Configuration is missing : {}", processConfiguration.toString());
      logger.error("Discarding logs.");
    }
  }

  /**
   * Write the content of {@link ProcessLogHandler#queue} to the file as recived from {@link
   * Configuration} object.
   *
   * <p>Internal method, shouldn't be used externallyy. Automatically called when a object of {@link
   * ProcessLogHandler} is created.
   *
   * @return int depciting the number of lines written.
   */
  private int writeToDisk() throws InterruptedException {
    String threadName =
        new StringJoiner("")
            .add(Thread.currentThread().getName())
            .add(Constants.DISK_WRITER_THREAD_NAME_SUFFIX)
            .toString();
    Thread.currentThread().setName(threadName);
    logger.info("Starting {}  to write to disk", threadName);
    //
    // Look for log while the intputstream still have data or the queue have not
    // finished writing to
    // disk
    //
    int counter = -1;
    while (this.inputStreamReadingThreads.stream().anyMatch(future -> !future.isDone())
        || !queue.isEmpty()) {
      if (queue.isEmpty()) {
        //
        // If queue is empty wait for some time
        //
        logger.debug("Queue is empty, waiting for {} milliseconds", Constants.THREAD_WAIT_TIME);
        Thread.sleep(Constants.THREAD_WAIT_TIME);
      } else {
        //
        // Write all the element in the queue to the disk.
        //
        List<OutputRecord> record = new ArrayList<>(Constants.FILE_WRITER_OBJECT_SIZE);
        int numberOfelementDrained = queue.drainTo(record, Constants.FILE_WRITER_OBJECT_SIZE);
        assert numberOfelementDrained == record.size();
        counter += record.size();
        record
            .stream()
            .map(Constants.GSON::toJson)
            .forEach(
                line -> {
                  printWriter.println(line);
                });
      }
      //
      // Force flush
      //
      printWriter.flush();
    }
    printWriter.close();
    logger.debug("Wrote {} lines to master log file.", ++counter);
    return counter;
  }

  /**
   * Reads the {@link InputStream} and write them to a {@link ProcessLogHandler#queue} as {@link
   * OutputRecord}.
   *
   * <p>This is a internal method and shouldn't be used by any body in the library.
   *
   * @param inputStream the {@link InputStream} to be read. The inputStream is received from {@link
   *     Process#getErrorStream()} and {@link Process#getInputStream()}.
   * @param outputSourceType either as input {@link OutputSourceType#SYSOUT} or {@link
   *     OutputSourceType#SYSERR}.
   */
  private void readInputStream(InputStream inputStream, OutputSourceType outputSourceType) {
    logger.trace("Saving inputstream for : {}", outputSourceType.toString());
    String threadName =
        new StringJoiner("")
            .add(Thread.currentThread().getName())
            .add(Constants.STREAM_READER_THREAD_NAME_SUFFIX)
            .add(outputSourceType.toString())
            .toString();
    Thread.currentThread().setName(threadName);
    logger.trace("Starting {} to read {}", threadName, outputSourceType.toString());
    Scanner scanner = new Scanner(inputStream, Charset.defaultCharset().toString());
    String currentLine;
    String logginMessage;
    while (scanner.hasNextLine()) {
      currentLine = scanner.nextLine();
      logginMessage =
          new StringJoiner(" >> ").add(outputSourceType.toString()).add(currentLine).toString();
      if (streamingEnabled) {
        logger.info(logginMessage);
      } else {
        logger.trace(logginMessage);
      }
      boolean response = this.queue.add(new OutputRecord(outputSourceType, currentLine));
      assert response;
    }
  }
}

package com.saptarshidebnath.processrunner.lib.jsonutils;

import static com.saptarshidebnath.processrunner.lib.utilities.Constants.CACHE_SIZE;
import static com.saptarshidebnath.processrunner.lib.utilities.Constants.DISK_WRITER_THREAD_NAME_SUFFIX;
import static com.saptarshidebnath.processrunner.lib.utilities.Constants.STREAM_READER_THREAD_NAME_SUFFIX;

import com.google.gson.Gson;
import com.saptarshidebnath.processrunner.lib.output.OutputRecord;
import com.saptarshidebnath.processrunner.lib.output.OutputSourceType;
import com.saptarshidebnath.processrunner.lib.process.Configuration;
import com.saptarshidebnath.processrunner.lib.utilities.Constants;
import com.saptarshidebnath.processrunner.lib.utilities.Threadify;
import com.saptarshidebnath.processrunner.lib.utilities.Utilities;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Writes the process logs as single line json of format {@link OutputRecord} to the file as
 * configured in {@link Configuration#getMasterLogFile()}. If {@link
 * Configuration#getMasterLogFile()} returns null, the logs will not be written.
 */
public class ProcessLogHanndler {
  private static Gson gson;
  private Logger logger = LoggerFactory.getLogger(ProcessLogHanndler.class);
  private PrintWriter printWriter;
  private BlockingQueue<OutputRecord> queue;
  private boolean streamingEnabled;
  private ArrayList<Future> inputStreamReadingThreads;
  private ExecutorService executorService;
  private Configuration processConfiguration;

  /**
   * The construcor of the class {@link ProcessLogHanndler}.
   *
   * <p>The class receives a reference of the {@link Process} and {@link Configuration}. The class
   * then internally takes care of creating all the process output reading threads i.e. both {@link
   * OutputSourceType#SYSERR} and {@link OutputSourceType#SYSOUT} and writing the same to the disk
   * in separate thread. The class also provides a blocking method {@link
   * ProcessLogHanndler#waitForShutdown()} so that you can wait for all logs to be streamed, written
   * to disk or both.
   *
   * <p>The class internally uses an executor service from {@link
   * Threadify#getProcessRunnerExecutorService()}
   *
   * @param process a object of type {@link Process}
   * @param processConfiguration a reference of type {@link Configuration}
   * @throws IOException in case of oany IOError
   */
  public ProcessLogHanndler(Process process, Configuration processConfiguration)
      throws IOException {
    this.processConfiguration = processConfiguration;
    File masterLogFile = processConfiguration.getMasterLogFile();
    boolean logsNeedTobeWritten = processConfiguration.getMasterLogFile() != null;
    boolean logsNeedTobeRead = processConfiguration.isEnableLogStreaming() || logsNeedTobeWritten;
    if (logsNeedTobeRead) {
      this.queue = new ArrayBlockingQueue<>(CACHE_SIZE);
      gson = new Gson();
      executorService = Threadify.getProcessRunnerExecutorService();
      streamingEnabled = processConfiguration.isEnableLogStreaming();
      if (streamingEnabled) {
        logger.trace("Logs will be streamed on real time.");
      } else {
        logger.warn("Log streaming disabled.");
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
        executorService.submit(this::writeToDisk);
      } else {
        logger.warn(
            Utilities.joinString(
                "Logs not written to file as per configuration : ",
                processConfiguration.toString()));
      }
      logger.trace("Created ProcessLogHandler. Tracking SYSOUT and SYSERROR");
    } else {
      logger.warn(
          Utilities.joinString(
              "Log Streaming is not enabled and Master logfile not set.",
              " Discarding logs. Configuration received.",
              processConfiguration.toString()));
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
   */
  public void waitForShutdown() {
    if (processConfiguration.getMasterLogFile() != null) {
      logger.trace(Utilities.joinString("Waiting for all the logs writing thread to shutdown."));
      this.executorService.shutdown();
    } else {
      logger.error(
          Utilities.joinString(
              "Masterfile Configuration is missing : ", processConfiguration.toString()));
      logger.error("Discarding logs.");
    }
  }

  /**
   * Write the content of {@link ProcessLogHanndler#queue} to the file as recived from {@link
   * Configuration} object.
   *
   * <p>Internal method, shouldnt be used externallly. Automatically called when a object of {@link
   * ProcessLogHanndler} is created.
   */
  private void writeToDisk() {
    String threadName =
        Utilities.joinString(Thread.currentThread().getName(), DISK_WRITER_THREAD_NAME_SUFFIX);
    Thread.currentThread().setName(threadName);
    logger.info(Utilities.joinString("Starting \"", threadName, "\" to write to disk"));
    //
    // Look for log while the intputstream still have data or the queue have not finished writing to
    // disk
    //
    while (this.inputStreamReadingThreads.stream().anyMatch(future -> !future.isDone())
        || !queue.isEmpty()) {
      String currentJson;
      if (queue.isEmpty()) {
        //
        // If queue is empty wait for some time
        //
        logger.trace(
            Utilities.joinString(
                "Queue is empty, waiting for ", Constants.THREAD_WAIT_TIME + "", " milliseconds"));
        try {
          Thread.sleep(Constants.THREAD_WAIT_TIME);
        } catch (InterruptedException e) {
          logger.error("Thread waiting is interrupted", e);
        }
      } else {
        //
        // Write all the element in the queue to the disk.
        //
        while (!queue.isEmpty()) {
          try {
            currentJson = gson.toJson(queue.take());
            logger.trace(Utilities.joinString("Writing >> ", currentJson));
            printWriter.println(gson.toJson(currentJson));
          } catch (InterruptedException e) {
            logger.error("Unable to write current head data", e);
          }
        }
      }
      //
      // Force flush
      //
      printWriter.flush();
    }
    printWriter.close();
  }

  /**
   * Reads the {@link InputStream} and write them to a {@link ProcessLogHanndler#queue} as {@link
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
    logger.trace(Utilities.joinString("Saving inpiutstream for : ", outputSourceType.toString()));
    String threadName =
        Utilities.joinString(
            Thread.currentThread().getName(),
            STREAM_READER_THREAD_NAME_SUFFIX,
            outputSourceType.toString());
    Thread.currentThread().setName(threadName);
    logger.trace(
        Utilities.joinString(
            "Starting \"", threadName, "\" to read ", outputSourceType.toString()));
    Scanner scanner = new Scanner(inputStream, Charset.defaultCharset().toString());
    String currentLine;
    String logginMessage;
    while (scanner.hasNextLine()) {
      currentLine = scanner.nextLine();
      logginMessage = Utilities.joinString(outputSourceType.toString() + " >> " + currentLine);
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

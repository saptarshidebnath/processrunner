package com.saptarshidebnath.processrunner.lib.process;

import com.saptarshidebnath.processrunner.lib.exception.ProcessException;
import com.saptarshidebnath.processrunner.lib.output.Output;

import java.util.concurrent.Future;

/** Process Runner interface is the base interface to run any system process or shell script */
public interface ProcessRunner {

  /**
   * Triggers the process or command;
   *
   * @return the {@link Integer} exit code for the process
   * @throws ProcessException Throws {@link ProcessException} to denote that there is an error. You
   *     can get the cause by {@link ProcessException#getCause()}
   */
  Output run() throws ProcessException;

  /**
   * Runs the process as a {@link java.util.concurrent.Callable} thread. The method returns a {@link
   * Future} reference from which the response of the method can be rtrived.
   *
   * @param threadEnabled The {@link Boolean} input is a flag input. The value of the passed
   *     parameter doesnt matter. The process will run thread enabled even if you pass {@link
   *     Boolean#FALSE}.
   * @return A reference to the {@link Future<Integer>} from which the user can retrieve the method
   *     output.
   */
  Future<Output> run(final boolean threadEnabled);
}

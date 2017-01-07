package com.saptarshidebnath.processrunner.lib.output;

import com.saptarshidebnath.processrunner.lib.process.ProcessConfiguration;

/** Created by saptarshi on 1/4/2017. */
public class OutputFactory {
  private OutputFactory() {}

  /**
   *
   * @param configuration
   * @param returnCode
   * @return
   */
  public static Output createOutput(
      final ProcessConfiguration configuration, final int returnCode) {
    return new OutputImpl(configuration, returnCode);
  }
}

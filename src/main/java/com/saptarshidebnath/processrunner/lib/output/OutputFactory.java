package com.saptarshidebnath.processrunner.lib.output;

import java.io.File;

/** Created by saptarshi on 1/4/2017. */
public class OutputFactory {
  private OutputFactory() {}

  public static Output createOutput(final File jsonLogDump, final int returnCode) {
    return new OutputImpl(jsonLogDump, returnCode);
  }
}

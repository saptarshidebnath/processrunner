package com.saptarshidebnath.processrunner.lib.output;

import com.saptarshidebnath.processrunner.lib.process.ProcessConfiguration;

/**
 * Factory class to create reference for {@link Output}. It uses the {@link OutputImpl} which
 * implementes {@link Output}.
 */
public class OutputFactory {
  /** private constructor. */
  private OutputFactory() {}

  /**
   * Creates a object of type {@link Output}
   *
   * @param configuration Accepts a valid {@link ProcessConfiguration} reference.
   * @param returnCode Accepts the exit code of process / script executed.
   * @return a reference of type {@link Output}
   */
  public static Output createOutput(
      final ProcessConfiguration configuration, final int returnCode) {
    return new OutputImpl(configuration, returnCode);
  }
}

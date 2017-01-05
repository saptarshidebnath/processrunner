package com.saptarshidebnath.processrunner.lib.output;

import com.saptarshidebnath.processrunner.lib.exception.ProcessException;

import java.io.File;

/** Created by saptarshi on 1/3/2017. */
public interface Output {
  File saveSysOut(final File sysOut) throws ProcessException;

  File saveSysError(final File sysError) throws ProcessException;

  File getMasterLog();

  int getReturnCode();

  boolean searchMasterLog(final String regex) throws ProcessException;
}

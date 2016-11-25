package com.saptarshidebnath.processrunner.main;

import com.saptarshidebnath.processrunner.lib.ProcessRunner;
import com.saptarshidebnath.processrunner.lib.ProcessRunnerFactory;
import com.saptarshidebnath.processrunner.lib.exception.ProcessConfigurationException;
import com.saptarshidebnath.processrunner.lib.utilities.Constants;

import java.io.File;
import java.io.IOException;

/** Created by saptarshi on 11/18/2016. */
public class App implements Constants {
  public static void main(final String... args)
      throws ProcessConfigurationException, InterruptedException {
    try {
      final ProcessRunner p =
          ProcessRunnerFactory.getProcess("cmd.exe /c", "dir kkr & echo pass", DEFAULT_CURRENT_DIR);
      p.run();
      System.out.println(p.search(".*DIR.*"));
      p.saveSysError(new File("d:\\error.txt"));
      p.saveSysOut(new File("d:\\sysout.txt"));
      p.getJsonLogDump().delete();
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }
}

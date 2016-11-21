package name.saptarshidebnath.processrunner.main;

import name.saptarshidebnath.processrunner.lib.ProcessRunner;
import name.saptarshidebnath.processrunner.lib.ProcessRunnerFactory;
import name.saptarshidebnath.processrunner.lib.exception.ProcessConfigurationException;
import name.saptarshidebnath.processrunner.lib.utilities.Constants;

import java.io.File;
import java.io.IOException;

/** Created by saptarshi on 11/18/2016. */
public class App implements Constants {
  public static void main(final String... args)
      throws ProcessConfigurationException, InterruptedException {
    try {
      final ProcessRunner p =
          ProcessRunnerFactory.getProcess(
              "cmd.exe /c",
              "dir",
              DEFAULT_CURRENT_DIR,
              new File("C:\\Users\\saptarshi\\out.txt"),
              new File("C:\\Users\\saptarshi\\err.txt"),
              true);
      p.run();
      System.out.println(p.search(".*saptarshi"));

    } catch (final IOException e) {
      e.printStackTrace();
    }
  }
}

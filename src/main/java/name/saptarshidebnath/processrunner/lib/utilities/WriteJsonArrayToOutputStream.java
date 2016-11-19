package name.saptarshidebnath.processrunner.lib.utilities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/** Created by saptarshi on 11/18/2016. */
public class WriteJsonArrayToOutputStream<T> {

  public WriteJsonArrayToOutputStream(final File outputFileName) throws IOException {
    final FileWriter fileWriter = new FileWriter(outputFileName);
  }
}

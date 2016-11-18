package name.saptarshidebnath.processrunner.lib.utilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/** Stores each line of output from the Process */
public class Output {
  private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
  private final long timeStamp;
  private final OutputSource outputSource;
  private final String outputText;

  /**
   * Constructor to store the Output line by line
   *
   * @param outputSource {@link OutputSource}
   * @param outputText
   */
  Output(final long timeStamp, final OutputSource outputSource, final String outputText) {
    this.timeStamp = getCurrentTime();
    this.outputSource = outputSource;
    this.outputText = outputText;
  }

  /**
   * Get the current timestamp in nano seconds
   *
   * @return
   */
  private static synchronized long getCurrentTime() {
    return System.nanoTime();
  }

  /**
   * Get the current object in JSON format
   *
   * @return
   */
  public String getAsJson() {
    return gson.toJson(this);
  }
}

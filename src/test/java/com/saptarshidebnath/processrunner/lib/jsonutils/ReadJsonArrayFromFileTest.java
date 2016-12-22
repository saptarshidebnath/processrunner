package com.saptarshidebnath.processrunner.lib.jsonutils;

import com.google.gson.GsonBuilder;
import com.saptarshidebnath.processrunner.lib.exception.JsonArrayReaderException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.logging.Logger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsArrayWithSize.arrayWithSize;
import static org.hamcrest.core.IsCollectionContaining.hasItems;

public class ReadJsonArrayFromFileTest {

  private final String jsonContent;
  private final String[] dataArrayToWrite;
  private final Logger logger;
  private ReadJsonArrayFromFile<String> testObject;
  private File testFile;

  {
    this.dataArrayToWrite = new String[] {"1", "2", "3", "4", "5"};
    this.jsonContent = new GsonBuilder().create().toJson(this.dataArrayToWrite);
    this.logger = Logger.getLogger(this.getClass().getCanonicalName());
  }

  @Before
  public void setUp() throws Exception {
    this.testFile = File.createTempFile("test-read-object-prefix", "-suffix");
    final PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(this.testFile)));
    pw.print(this.jsonContent);
    pw.flush();
    pw.close();
    this.testObject = new ReadJsonArrayFromFile<>(this.testFile);
  }

  @After
  public void tearDown() throws Exception {
    if (this.testFile.delete()) {
      this.logger.severe(
          "Unable to delete file : "
              + this.testFile.getCanonicalPath()
              + ". Please delete manually");
    }
    this.testObject.cleanUp();
  }

  @Test
  public void textReadNext() throws IOException, JsonArrayReaderException {
    final ArrayList<String> readData = new ArrayList<>();
    String data;
    do {
      data = this.testObject.readNext(String.class);
      if (data != null) {
        readData.add(data);
      }
    } while (data != null);
    assertThat(
        "Checking array length",
        readData.toArray(new String[readData.size()]),
        arrayWithSize(this.dataArrayToWrite.length));

    assertThat("Checking array contents", readData, hasItems(this.dataArrayToWrite));
  }

  @Test(expected = JsonArrayReaderException.class)
  public void writeJsonObjectAfterEndingJsonObject() throws IOException, JsonArrayReaderException {
    this.testObject.cleanUp();
    this.testObject.readNext(String.class);
  }
}

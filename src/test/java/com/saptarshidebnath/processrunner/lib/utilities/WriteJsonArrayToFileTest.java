package com.saptarshidebnath.processrunner.lib.utilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.saptarshidebnath.processrunner.lib.exception.JsonArrayWriterException;
import com.saptarshidebnath.processrunner.lib.jsonutils.WriteJsonArrayToFile;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsArrayWithSize.arrayWithSize;
import static org.hamcrest.core.IsCollectionContaining.hasItems;

/** Created by saptarshi on 11/25/2016. */
public class WriteJsonArrayToFileTest {
  private File tempFile;
  private String[] arrayOfDataToWrite;
  private String[] arrayOfDataReadFromTestFile;
  private WriteJsonArrayToFile<String> testObject;
  private Gson gson;

  @Before
  public void setUp() throws IOException {
    this.tempFile = File.createTempFile("test-prefix", "test-suffix");
    final ArrayList<String> dataList = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      dataList.add(new Date().toString());
    }
    this.arrayOfDataToWrite = new String[dataList.size()];
    this.arrayOfDataToWrite = dataList.toArray(this.arrayOfDataToWrite);
    this.testObject = new WriteJsonArrayToFile<>(this.tempFile);
    this.gson = new GsonBuilder().create();
  }

  @After
  public void tearDown() {
    this.tempFile.delete();
  }

  @Test
  public void writeJsonObject() throws Exception {
    this.testObject.startJsonObject();
    for (int i = 0; i < this.arrayOfDataToWrite.length; i++)
      this.testObject.writeJsonObject(this.arrayOfDataToWrite[i]);
    this.testObject.endJsonObjectWrite();
    this.testObject.cleanup();
    final JsonReader reader = new JsonReader(new FileReader(this.tempFile));
    this.arrayOfDataReadFromTestFile = this.gson.fromJson(reader, String[].class);

    assertThat(
        "Checking array length",
        this.arrayOfDataReadFromTestFile,
        arrayWithSize(this.arrayOfDataToWrite.length));

    assertThat(
        "Checking array contents",
        Arrays.asList(this.arrayOfDataReadFromTestFile),
        hasItems(this.arrayOfDataToWrite));
  }

  @Test(expected = JsonArrayWriterException.class)
  public void writeJsonObjectAfterEndingJsonObject() throws IOException, JsonArrayWriterException {
    this.testObject.startJsonObject();
    this.testObject.endJsonObjectWrite();
    this.testObject.cleanup();
    this.testObject.writeJsonObject("Test String");
  }
}

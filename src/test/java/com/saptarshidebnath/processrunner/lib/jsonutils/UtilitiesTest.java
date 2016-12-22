package com.saptarshidebnath.processrunner.lib.jsonutils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created by saptarshi on 12/7/2016.
 */
public class UtilitiesTest {
    private File tempFile;

    @Before
    public void setUp() throws Exception {
        this.tempFile = Utilities.createTempLogDump();
    }

    @After
    public void tearDown() throws Exception {
        this.tempFile.delete();
    }

    @Test
    public void createTempLogDump() throws Exception {
        assertThat(
                "Checking if File exists",
                this.tempFile.exists(),
                is(true));
    }

}
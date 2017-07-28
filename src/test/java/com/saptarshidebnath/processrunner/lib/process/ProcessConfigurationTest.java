/*
 *
 * MIT License
 *
 * Copyright (c) [2016] [Saptarshi Debnath]
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.saptarshidebnath.processrunner.lib.process;

import com.saptarshidebnath.processrunner.lib.exception.ProcessConfigurationException;
import com.saptarshidebnath.processrunner.lib.utilities.Constants;
import com.saptarshidebnath.processrunner.lib.utilities.Utilities;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import static com.saptarshidebnath.processrunner.lib.utilities.Constants.FILE_PREFIX_NAME_LOG_DUMP;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created by saptarshi on 1/1/2017.
 */
public class ProcessConfigurationTest {
    @Test
    public void testObjectCreationWithCorrectValue()
            throws IOException, ProcessConfigurationException {
        final String interpreter = "bash";
        final String command = "echo";
        final String commandParam = "Saptarshi";
        final String[] commandParamArray = {"a", "b", "c"};
        final File tempFile =
                File.createTempFile(FILE_PREFIX_NAME_LOG_DUMP, Constants.FILE_SUFFIX_JSON);
        final boolean fileSetToBeAutoDeleted = true;
        final Level logLevel = Level.OFF;
        final Configuration configuration =
                new ConfigurationBuilder(interpreter, command)
                        .setParam(commandParam)
                        .setParamList(Arrays.asList(commandParamArray))
                        .setWorkigDir(Constants.DEFAULT_CURRENT_DIR_PATH)
                        .setMasterLogFile(tempFile, fileSetToBeAutoDeleted)
                        .setLogLevel(logLevel)
                        .build();
        assertThat(
                "Validating process runner Interpreter : ",
                configuration.getInterpreter(),
                is(interpreter));
        assertThat("Validating process runner command: ",
                configuration.getCommand(),
                is(command + " " + commandParam + " " + String.join(" ", commandParamArray)));
        assertThat(
                "Validating process configuration default work dir: ",
                configuration.getWorkingDir().toFile().getCanonicalPath(),
                is(Constants.DEFAULT_CURRENT_DIR.getCanonicalPath()));
        assertThat(
                "Validating process configuration json log dump: ",
                configuration.getMasterLogFile().getCanonicalPath(),
                is(tempFile.getCanonicalPath()));
        assertThat(
                "Validating process configuration json log file set for auto deletion : ",
                configuration.getAutoDeleteFileOnExit(),
                is(fileSetToBeAutoDeleted));
        assertThat(
                "Validating process configuration debug value : ",
                configuration.getLogLevel(),
                is(logLevel));
    }

    @Test(expected = ProcessConfigurationException.class)
    public void testObjectCreationWithEmptyInterpreter()
            throws IOException, ProcessConfigurationException {
        final String interpreter = "";
        final String command = "echo Saptarshi";
        final File tempFile =
                File.createTempFile(FILE_PREFIX_NAME_LOG_DUMP, Constants.FILE_SUFFIX_JSON);
        tempFile.deleteOnExit();
        final Level logLevel = Level.ALL;
        final boolean fileSetToBeAutoDeleted = true;
        new ConfigurationBuilder(interpreter, command)
                .setWorkigDir(Constants.DEFAULT_CURRENT_DIR_PATH)
                .setMasterLogFile(tempFile, fileSetToBeAutoDeleted)
                .setLogLevel(logLevel)
                .build();
    }

    @Test(expected = ProcessConfigurationException.class)
    public void testObjectCreationWithEmptyCommand()
            throws IOException, ProcessConfigurationException {
        final String interpreter = "bash";
        final String command = "";
        final File tempFile =
                File.createTempFile(FILE_PREFIX_NAME_LOG_DUMP, Constants.FILE_SUFFIX_JSON);
        tempFile.deleteOnExit();
        final Level logLevel = Level.FINER;
        final boolean fileSetToBeAutoDeleted = true;
        new ConfigurationBuilder(interpreter, command)
                .setWorkigDir(Constants.DEFAULT_CURRENT_DIR_PATH)
                .setMasterLogFile(tempFile, fileSetToBeAutoDeleted)
                .setLogLevel(logLevel)
                .build();
    }

    @Test(expected = ProcessConfigurationException.class)
    public void testObjectCreationNonExistentCurrentDir()
            throws IOException, ProcessConfigurationException {
        final String interpreter = "bash";
        final String command = "echo saptarshi";
        final File tempFile =
                File.createTempFile(FILE_PREFIX_NAME_LOG_DUMP, Constants.FILE_SUFFIX_JSON);
        tempFile.deleteOnExit();
        final Level logLevel = Level.FINER;
        final boolean fileSetToBeAutoDeleted = true;
        final Configuration configuration =
                new ConfigurationBuilder(interpreter, command)
                        .setWorkigDir(new File("\\root\\").toPath())
                        .setMasterLogFile(tempFile, fileSetToBeAutoDeleted)
                        .setLogLevel(logLevel)
                        .build();
        //
        // Un reachable step.
        //
        configuration.getMasterLogFile();
    }

    @Test(expected = ProcessConfigurationException.class)
    public void testObjectCreationCurrentDirSetAsFile()
            throws IOException, ProcessConfigurationException {
        final String interpreter = "bash";
        final String command = "echo saptarshi";
        final File tempFile =
                File.createTempFile(FILE_PREFIX_NAME_LOG_DUMP, Constants.FILE_SUFFIX_JSON);
        tempFile.deleteOnExit();
        final boolean fileSetToBeAutoDeleted = true;
        final Level logLevel = Level.FINEST;
        new ConfigurationBuilder(interpreter, command)
                .setWorkigDir(tempFile.toPath())
                .setMasterLogFile(tempFile, fileSetToBeAutoDeleted)
                .setLogLevel(logLevel)
                .build();
    }

    @Test(expected = ProcessConfigurationException.class)
    public void testNullParamToConfigurationBuilder() throws ProcessConfigurationException, IOException {
        Configuration configuration = new ConfigurationBuilder("bash", "echo hello!")
                .setParam(null)
                .build();
    }

    @Test(expected = ProcessConfigurationException.class)
    public void testEmptyParamToConfigurationBuilder() throws ProcessConfigurationException, IOException {
        Configuration configuration = new ConfigurationBuilder("bash", "echo hello!")
                .setParam("")
                .build();
    }

    @Test(expected = ProcessConfigurationException.class)
    public void testNullParamListToConfigurationBuilder() throws ProcessConfigurationException, IOException {
        Configuration configuration = new ConfigurationBuilder("bash", "echo hello!")
                .setParamList(new ArrayList<String>(0))
                .build();
    }

    @Test(expected = ProcessConfigurationException.class)
    public void testEmptyParamListToConfigurationBuilder() throws ProcessConfigurationException, IOException {
        List<String> nullList = null;
        Configuration configuration = new ConfigurationBuilder("bash", "echo hello!")
                .setParamList(nullList)
                .build();
    }

    @Test(expected = ProcessConfigurationException.class)
    public void testNullInterpreterToConfigurationBuilder() throws ProcessConfigurationException, IOException {
        Configuration configuration = new ConfigurationBuilder(null, "echo hello!")
                .build();
    }

    @Test(expected = ProcessConfigurationException.class)
    public void testEmptyInterpreterToConfigurationBuilder() throws ProcessConfigurationException, IOException {
        Configuration configuration = new ConfigurationBuilder("", "echo hello!")
                .build();
    }


    @Test(expected = ProcessConfigurationException.class)
    public void testNullCommandToConfigurationBuilder() throws ProcessConfigurationException, IOException {
        Configuration configuration = new ConfigurationBuilder("bash", null)
                .build();
    }

    @Test(expected = ProcessConfigurationException.class)
    public void testEmptyCommandToConfigurationBuilder() throws ProcessConfigurationException, IOException {
        Configuration configuration = new ConfigurationBuilder("bash", "")
                .build();
    }

    @Test
    public void testToStringMethod() throws ProcessConfigurationException, IOException {
        File masterLogFile = Utilities.createTempLogDump();
        String expectedValue = "Configuration{interpreter='bash', command='echo Saptarshi works on java', workingDir=" +
                Constants.DEFAULT_CURRENT_DIR_PATH.toString() +
                ", masterLogFile=" + masterLogFile.getAbsolutePath() +
                ", autoDeleteFileOnExit=true, logLevel=INFO, printStream=set}";
        Configuration configuration = new ConfigurationBuilder("bash", "echo")
                .setParam("Saptarshi")
                .setParamList(Arrays.asList(new String[]{"works", "on", "java"}))
                .setLogLevel(Level.INFO)
                .setWorkigDir(Constants.DEFAULT_CURRENT_DIR_PATH)
                .setMasterLogFile(masterLogFile, true, Constants.UTF_8)
                .setStreamingDestination(System.out)
                .build();
        assertThat("Validating toString method", configuration.toString(), is(expectedValue));
    }
}

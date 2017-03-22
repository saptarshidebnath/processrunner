
[![Build Status](https://travis-ci.org/saptarshidebnath/processrunner.svg?branch=master)](https://travis-ci.org/saptarshidebnath/processrunner) [![codecov](https://codecov.io/gh/saptarshidebnath/processrunner/branch/master/graph/badge.svg)](https://codecov.io/gh/saptarshidebnath/processrunner) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.saptarshidebnath.utilities/ProcessRunner/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.saptarshidebnath.utilities/ProcessRunner) [![SonarQube Coverage](https://img.shields.io/sonar/http/sonar.qatools.ru/ru.yandex.qatools.allure:allure-core/coverage.svg)](https://sonarqube.com/dashboard?id=com.saptarshidebnath.utilities%3AProcessRunner) [![SonarQube Tech Debt](https://img.shields.io/sonar/http/sonar.qatools.ru/ru.yandex.qatools.allure:allure-core/tech_debt.svg)](https://sonarqube.com/dashboard?id=com.saptarshidebnath.utilities%3AProcessRunner)

***Process Runner*** is java based **thread enabled** library using which one can **execute system process or scripts [shell scripts, batch file, python scripts, ruby scripts etc] from inside your java program**.

## Mini Tutorial
Using the library is very easy. You can **start executiing os commands / scripts in litterally a single line of code** as follows :

1. Add the following as build / test dependency as **in your maven's pom.xml** or put the jar in the class path.

    ``` xml
    <dependency>
        <groupId>com.saptarshidebnath.utilities</groupId>
        <artifactId>ProcessRunner</artifactId>
        <version>[LATEST_VERSION]</version>
    </dependency>
    ```

2. Use the following **code to run any command or script** :-

    ``` java
     Output commandOuput =
        ProcessRunnerFactory.startProcess("cmd.exe /c", "echo GNU is not unix", Level.WARNING);
    System.out.println("Return code of command is : " + commandOuput.getReturnCode());
    System.out.println("Master log file is : " + commandOuput.getMasterLog().getAbsolutePath());
    ```

Voilla, you just ran a windows command uisng the command interface.

## Understanding ProcessRunner library

[processrunner](http://code.saptarshidebnath.com/processrunner/) is a java based library primarily available in [maven](https://maven.apache.org/). It should work correctly in Apache Builder, Apache Ivy, Groovy grape, Gradle/Grails, Scala SBT, Leiningen. Or if you choose to manually download the jar and put in the class path [You are kidding me right ? If not do check out maven and other great build and dependency management tools.] that will work too. I would recomend to use the JRE version 1.8 or higher.

**Some of the features of the ProcessRunner library are:-**

1. Written in java, so can be used by any JVM based languages.
1. Both Sysout and Syserror are captured using seperate `Thread`s and immediately siphoned to the disk as a `JSON` file. Each and evey output from both the sources are timestamped and classified by their source when being dumped to the secondary storage. This result in low JVM memory overhead at the cost of relatively high disk activity. The aproach has been tested on scripts which generates hundereds of mega byte of log output on execution and it runs correctly.
1. The process execution can be executed as part of the main `Thread` or in a seperate `Thread` just by passing a boolean variable.
1. Have helper methods to:-
    1. Seperate SYSOUT and SYSERROR
    1. Search master log file for regular expression pattern.
    1. Get master log file in json format.
    1. Get reurn code.


**To understand processrunner, you need to understand working principles of the following classes :-**
1. [ProcesConfiguration](#process-configuration-details) : Configuration POJO.
1. [ProcessRunnerFactory](#process-runner-factory-details) : Factory class for [ProcessRunner](https://github.com/saptarshidebnath/processrunner/blob/master/src/main/java/com/saptarshidebnath/processrunner/lib/process/ProcessRunner.java) instance creation.
1. [ProcessRunner](#process-runner-details) : Runner interface.
1. [Output](#output-details) : Results.

Please find the details as follows :-

1. <a name="process-configuration-details"></a>[ProcesConfiguration](https://github.com/saptarshidebnath/processrunner/blob/master/src/main/java/com/saptarshidebnath/processrunner/lib/process/ProcessConfiguration.java) : The `ProcesConfiguration` class depicts the configuration which is used by the library to trigger a command or script.
`ProcesConfiguration` class instance is consumed by the `ProcessRunnerFactory` to create a instance of `ProcessRunner` class. We are going to discuss about the details below.
To create a `ProcesConfiguration` you can use the following example.
    
    ``` java
    import com.saptarshidebnath.processrunner.lib.exception.ProcessConfigurationException;
    import com.saptarshidebnath.processrunner.lib.process.ProcessConfiguration;
    import com.saptarshidebnath.processrunner.lib.utilities.Constants;

    ...
    ...

    try {
      ProcessConfiguration processConfiguration =
          new ProcessConfiguration(
              "cmd.exe /c",                     // Interpreter to be used.
              "echo GNU is not Unix",           // Command to ran.
              Constants.DEFAULT_CURRENT_DIR,    // a File object representing where current working dir the command / script is going to be executed. Constant class is availble from the lib package.
              new File("file-log.json"),        // File object for writing the Master Log file.
              false,                            // Autodelete log file indicator. True for auto delete of master log file on command execution end.
              Level.WARNING);                   // Mimimum java.util.logging.Level for printing infomration.
    } catch (ProcessConfigurationException | IOException e) {
      e.printStackTrace();
    }
    ```


1. <a name="process-runner-factory-details"></a>[ProcessRunnerFactory](https://github.com/saptarshidebnath/processrunner/blob/master/src/main/java/comThe/saptarshidebnath/processrunner/lib/process/ProcessRunnerFactory.java) : The factory is the generator class to create an instance of `ProcessRunner`. The `ProcessRunnerFactory` have multiple methods facilating different kind of scenarion. The `ProcessRunnerFactory` methods can be broadly classified in 2 overloaded types. Both of them are detailed below.
    1. **ProcessRunnerFactory.startProcess(...)** : The idea behind these methods is to start a process or command immediately. Some uses re usable `ProcessConfiguration` class object and some just creates the configuration dynamically from the provided confiuration. Please find all the details about the methods below.
        1. **Output ProcessRunnerFactory.startProcess(String, String, Level)** : This method is used for running a system command / script on a adhoc basis. This is the most basic implementation there is. 
            * Runs in the same thread.
            * Paramters are :-
                1. Interpreter as `String`. Ex : python, bash, cmd.exe
                1. Command or script file name with relative path as `String`. Ex : "echo My name is Saptarshi Debnath."
                1. Minimum `Level` for ProcessRunner to start logging in console. Ex : Level.INFO.
            * Returns a refernce to `OutPut` class.
        
            Please see the below code snippet for more details :-

            ``` java
            import com.saptarshidebnath.processrunner.lib.exception.ProcessException;
            import com.saptarshidebnath.processrunner.lib.process.ProcessRunnerFactory;

            ...

            try {
                Output output = ProcessRunnerFactory.startProcess("cmd.exe /c", 
                                                  "echo GNU is not unix", 
                                                  Level.WARNING);
            } catch (ProcessException e) {
                e.printStackTrace();
            }

            ``` 
        1. **Output ProcessRunnerFactory.startProcess(ProcessConfiguration)** : This method is suitable for starting a process with a pre configured `ProcessConfiguration` object.
            * Runs in the same thread.
            * Parameter is :-
                1. configuration details as `ProcessConfiguration`
            * Returns a refernce to `OutPut` class.

            Please see the below code snippet for more details.

            ``` java
            import com.saptarshidebnath.processrunner.lib.exception.ProcessConfigurationException;
            import com.saptarshidebnath.processrunner.lib.exception.ProcessException;
            import com.saptarshidebnath.processrunner.lib.process.ProcessConfiguration;
            import com.saptarshidebnath.processrunner.lib.process.ProcessRunnerFactory;
            import com.saptarshidebnath.processrunner.lib.utilities.Constants;

            ...

            try {
                //
                // A ProcessConfiguration POJO is configured.
                //
                ProcessConfiguration processConfiguration =
                      new ProcessConfiguration(
                          "cmd.exe /c",
                          "echo GNU is not unix",
                          Constants.DEFAULT_CURRENT_DIR,
                          File.createTempFile("temp-file", ".json"),
                          false,
                          Level.WARNING);

                //
                // ProcessConfiguration POJO is being passed to 
                // ProcessRunnerFactory to start a process.
                //

                Output output = ProcessRunnerFactory.startProcess(processConfiguration);

                } catch (ProcessConfigurationException | IOException | ProcessException e) {
                  e.printStackTrace();
                }

            ```

        1. **Future\<OutPut\> ProcessRunnerFactory.startProcess(ProcessConfiguration, boolean)** : This method is suitable for starting a process with a pre configured `ProcessConfiguration` in its own `Thread`. I am going to strongly advice, to use this particular method to trigger a process when you even suspect that process is going to be a long running one or is going to generate a ton of console outputs.
            * ***Runs in a seperate thread***.
            * Parameters are :-
                1. configration details as `ProcessConfiguration`
                1. `true` or `false` as a `boolean`. Doesn't matter if its `true` or `false`. Anything `boolean` or `Boolean` will do. It is just a marker for Thread based implementation.
            * Returns a refernce to `Future<OutPut>` class.


            Please see the below code snippet for more details.
            
            ``` java
            import com.saptarshidebnath.processrunner.lib.exception.ProcessConfigurationException;
            import com.saptarshidebnath.processrunner.lib.exception.ProcessException;
            import com.saptarshidebnath.processrunner.lib.output.Output;
            import com.saptarshidebnath.processrunner.lib.process.ProcessConfiguration;
            import com.saptarshidebnath.processrunner.lib.process.ProcessRunnerFactory;
            import com.saptarshidebnath.processrunner.lib.utilities.Constants;

            ...

            try {
                ProcessConfiguration processConfiguration =
                    new ProcessConfiguration(
                      "cmd.exe /c",
                      "echo GNU is not unix",
                      Constants.DEFAULT_CURRENT_DIR,
                      File.createTempFile("temp-file", ".json"),
                      false,
                      Level.WARNING);
                Future<Output> outputFuture = ProcessRunnerFactory.startProcess(processConfiguration,true);
                Output outPut = outputFuture.get();
            } catch (ProcessConfigurationException | IOException | ProcessException | InterruptedException | ExecutionExceptione) {
              e.printStackTrace();
            }
            ```

    1. `ProcessRunnerFactory.getProcess(...)` : There are 2 sets of methods whose primary goal is to create a `ProcessRunner` class instance. On the `ProcessRunner` instance the overloaded methods `ProcessRunner.run()` to start the process. Both of them are detailed below :-
        1. **ProcessRunner ProcessRunnerFactory.getProcess(String, String, File, Level)** : This methods cretes a instance of `ProcessConfiguration` and instatiate a `ProcessRunner` instance with the same. The same `ProcessRunner` is returned from this method. The developer is supposed to start the oveloaded `ProcessRunner.run()` method to actually start the process.
            * Doesn't start the process.
            * Creates a master log file in the tempoarary directory.
            * Parameters are :-
                1. Command entrpreter as `String`.
                1. Command / Script to run as `String`.
                1. The working directory of the Script as `File`
                1. Minimum `Log` `Level`
            * Returns a reference to `ProcessRunner` interface.
            * The log file is **not marked for auto deletion**. The user have to decide if he wants to delete or keep the file.
            <a name="code-process-runner-samethread"></a>
            
            Please see the below code snippet for more details :-

            ``` java
            import com.saptarshidebnath.processrunner.lib.exception.ProcessException;
            import com.saptarshidebnath.processrunner.lib.process.ProcessRunner;
            import com.saptarshidebnath.processrunner.lib.process.ProcessRunnerFactory;
            import com.saptarshidebnath.processrunner.lib.utilities.Constants;
            
            ...

            try {
              ProcessRunner processRunner =
                  ProcessRunnerFactory.getProcess(
                      "cmd.exe /c", "echo GNU is not unix.", Constants.DEFAULT_CURRENT_DIR, Level.INFO);
             //
             // Trigger the process that have been just configured.
             //
              Output output = processRunner.run();
            } catch (ProcessException e) {
              e.printStackTrace();
            }
            ```
        1.  **ProcessRunner ProcessRunnerFactory.getProcess(String, String, File, File, Boolean, Level)** : This process is exactly similar to the above option, except you have much more choice to set where the log `File` should be set and if the master log file is going to be auto deleted on system exit.
            * Doesn't start the process.
            * Most detailed builder of `ProcessRunner`.
            * Parameters are :-
                1. Command entrpreter as `String`.
                1. Command / Script to run as `String`.
                1. The working directory of the Script as `File`.
                1. Master log file location as `File`.
                1. `true` or `false` as `Boolean` marker to denote if master log file is going to be auto deleted or not on jvm exit.
                1. Minimum `Log` `Level`.
            * Returns a reference of Future<Output>.
            <a name="code-process-runner-diff-thread"></a>

            Please see the below code snippet for more details :-

            ``` java
            import com.saptarshidebnath.processrunner.lib.exception.ProcessException;
            import com.saptarshidebnath.processrunner.lib.output.Output;
            import com.saptarshidebnath.processrunner.lib.process.ProcessRunner;
            import com.saptarshidebnath.processrunner.lib.process.ProcessRunnerFactory;
            import com.saptarshidebnath.processrunner.lib.utilities.Constants;
            
            ...

            try {
              ProcessRunner processRunner =
                  ProcessRunnerFactory.getProcess(
                      "cmd.exe /c",
                      "echo GNU is not unix",
                      Constants.DEFAULT_CURRENT_DIR,
                      File.createTempFile("master-log", ".json"),
                      false,
                      Level.WARNING);
              Future<Output> outputFuture = processRunner.run(true);
              Output output = outputFuture.get();
            } catch (ProcessException | IOException | InterruptedException | ExecutionException e) {
              e.printStackTrace();
            }
            ```

1. <a name="process-runner-details"></a>[ProcessRunner](https://github.com/saptarshidebnath/processrunner/blob/master/src/main/java/com/saptarshidebnath/processrunner/lib/process/ProcessRunner.java) : The `ProcessRunner` is a interface which provides the guide line for running the process or script. You will see the `ProcessRunner` instance when you use the overloaded `ProcessRunnerFactory.getProcess(...)` methods. The overloaded method `ProcessRunnerFactory.startProcess(...)` internally uses the method to start the process. The 2 primary method of the `ProcessRunner` class are as follows :-
    1. **Output ProcessRunner.run()** : This is one of the 2 methods. It starts the execution in same thread.
        * It runs in the same thread.
        * Returns a reference to the `Output` class.
        * No input is neccessary, it pulls the configuration from the already provided `ProcessConfiguration`.
        For implementation details please [checkout documents form ProcessRunnerFactory here](#code-process-runner-samethread).

    1. **Future\<Output\> run(boolean)** : It is almost similar to the other method. Only this method runs in a seperate thread.
        * Runs in a seperate thread.
        * Returns a reference to the `Future<Output>` class.
        * No input is neccessary, it pulls the configuration from the already provided `ProcessConfiguration`.
        For implementation details please [checkout documents form ProcessRunnerFactory here](#code-process-runner-diff-thread).

1. <a name="output-details"></a> [Output](https://github.com/saptarshidebnath/processrunner/blob/master/src/main/java/com/saptarshidebnath/processrunner/lib/output/Output.java) : The `Output` is an interface, whose sole job is to return values regarding the process execution. The `Output` instance is only available after the process have executed successfully. *As of now, the console outputs are not streamed LIVE*. The methods available in the `Output` class are :-
    * **File Output.getMasterLog()** : Returns a `File` reference to the Master Log file. The master log file is a json file as an array of the json implementation of the class [OutputRecord](https://github.com/saptarshidebnath/processrunner/blob/master/src/main/java/com/saptarshidebnath/processrunner/lib/output/OutputRecord.java).
    
        ``` javascript
        [
            {
                "timeStamp" : interger,
                "outputSourceType" : "SYSOUT/SYSERR"
                "outputText" : "String"
            }
        ]
        ```

       Please refer [OutputRecord](https://github.com/saptarshidebnath/processrunner/blob/master/src/main/java/com/saptarshidebnath/processrunner/lib/output/OutputRecord.java) foor more details on the json structure.

    * **int Output.getReturnCode()** : Returns the exit code of the process / script that was just ran. Please note that if a set of command is executed, the exit code returned will be that of the last command only.

    * **File Output.saveSysError(File) / File Output.saveSysOut(File)** : These 2 functions extracts the sysout and syserror from the master log file and creates a new `File` with the target `File` reference provided. This function reads the master log file and picks correct output depending upon the source. This is an IO intensive operation.

    * **boolean Output.searchMasterLog(String regex)** : This is a very basic implemtnation of a searching `String` regular expression in the output. It reads and checks for a match of the regular expression line by line. The method returns true after finding the first match.

    **A implemnation example for `Output` class is as follows** :-

    ``` java
    import com.saptarshidebnath.processrunner.lib.exception.ProcessException;
    import com.saptarshidebnath.processrunner.lib.output.Output;
    import com.saptarshidebnath.processrunner.lib.process.ProcessRunner;
    import com.saptarshidebnath.processrunner.lib.process.ProcessRunnerFactory;
    import com.saptarshidebnath.processrunner.lib.utilities.Constants;
    import java.io.File;
    import java.io.IOException;
    import java.util.concurrent.ExecutionException;
    import java.util.concurrent.Future;
    import java.util.logging.Level;
    
    ...

    try {
          ProcessRunner processRunner =
              ProcessRunnerFactory.getProcess(
                  "cmd.exe /c",
                  "echo GNU is not unix",
                  Constants.DEFAULT_CURRENT_DIR,
                  File.createTempFile("master-log", ".json"),
                  false,
                  Level.WARNING);
          Future<Output> outputFuture = processRunner.run(true);
          Output output = outputFuture.get();
          File masterLog = output.getMasterLog();
          File sysout = output.saveSysOut(File.createTempFile("sysout",".txt"));
          File syserr = output.saveSysError(File.createTempFile("syserror",".txt"));
          int returnCode = output.getReturnCode();
          boolean isSaptarshiPresent = output.searchMasterLog(".*Saptarshi.*");

    } catch (ProcessException | IOException | InterruptedException | ExecutionException e) {
          e.printStackTrace();
    }
    ```
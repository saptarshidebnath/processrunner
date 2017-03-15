## processrunner

[![Build Status](https://travis-ci.org/saptarshidebnath/processrunner.svg?branch=master)](https://travis-ci.org/saptarshidebnath/processrunner) [![codecov](https://codecov.io/gh/saptarshidebnath/processrunner/branch/master/graph/badge.svg)](https://codecov.io/gh/saptarshidebnath/processrunner) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.saptarshidebnath.utilities/ProcessRunner/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.saptarshidebnath.utilities/ProcessRunner) [![SonarQube Coverage](https://img.shields.io/sonar/http/sonar.qatools.ru/ru.yandex.qatools.allure:allure-core/coverage.svg)](https://sonarqube.com/dashboard?id=com.saptarshidebnath.utilities%3AProcessRunner) [![SonarQube Tech Debt](https://img.shields.io/sonar/http/sonar.qatools.ru/ru.yandex.qatools.allure:allure-core/tech_debt.svg)](https://sonarqube.com/dashboard?id=com.saptarshidebnath.utilities%3AProcessRunner)

***Process Runner*** is java based library using which one can **execute system process or scripts [shell scripts, batch file, python scripts, ruby scripts etc] from inside your java program**.

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
To understand processrunner, you need to understand working principles of the following classes :-
1. [ProcesConfiguration](https://github.com/saptarshidebnath/processrunner/blob/master/src/main/java/com/saptarshidebnath/processrunner/lib/process/ProcessConfiguration.java) : Configuration POJO.
1. [ProcessRunnerFactory](https://github.com/saptarshidebnath/processrunner/blob/master/src/main/java/com/saptarshidebnath/processrunner/lib/process/ProcessRunnerFactory.java) : Factory class for [ProcessRunner](https://github.com/saptarshidebnath/processrunner/blob/master/src/main/java/com/saptarshidebnath/processrunner/lib/process/ProcessRunner.java) instance creation.
1. [ProcessRunner](https://github.com/saptarshidebnath/processrunner/blob/master/src/main/java/com/saptarshidebnath/processrunner/lib/process/ProcessRunner.java) : Runner interface.
1. [Output](https://github.com/saptarshidebnath/processrunner/blob/master/src/main/java/com/saptarshidebnath/processrunner/lib/output/Output.java) : Results.

## Please find the details as follows :-

1. [ProcesConfiguration](https://github.com/saptarshidebnath/processrunner/blob/master/src/main/java/com/saptarshidebnath/processrunner/lib/process/ProcessConfiguration.java) : The [ProcesConfiguration](https://github.com/saptarshidebnath/processrunner/blob/master/src/main/java/com/saptarshidebnath/processrunner/lib/process/ProcessConfiguration.java) class depicts the configuration which is used by the library to trigger a command or script.
[ProcesConfiguration](https://github.com/saptarshidebnath/processrunner/blob/master/src/main/java/com/saptarshidebnath/processrunner/lib/process/ProcessConfiguration.java) class instance is consumed by the [ProcessRunnerFactory](https://github.com/saptarshidebnath/processrunner/blob/master/src/main/java/com/saptarshidebnath/processrunner/lib/process/ProcessRunnerFactory.java) to create a instance of [ProcessRunner](https://github.com/saptarshidebnath/processrunner/blob/master/src/main/java/com/saptarshidebnath/processrunner/lib/process/ProcessRunner.java) class. We are going to discuss about the details below.
To create a [ProcesConfiguration](https://github.com/saptarshidebnath/processrunner/blob/master/src/main/java/com/saptarshidebnath/processrunner/lib/process/ProcessConfiguration.java) you can use the following example.
    
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
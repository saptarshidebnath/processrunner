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

## Understanding ProcessRunner
To understand processrunner, you need to understand working principles of the following classes :-
1. [ProcesConfiguration](https://github.com/saptarshidebnath/processrunner/blob/master/src/main/java/com/saptarshidebnath/processrunner/lib/process/ProcessConfiguration.java)
1. [ProcessRunnerFactory](https://github.com/saptarshidebnath/processrunner/blob/master/src/main/java/com/saptarshidebnath/processrunner/lib/process/ProcessRunnerFactory.java)
1. [Output](https://github.com/saptarshidebnath/processrunner/blob/master/src/main/java/com/saptarshidebnath/processrunner/lib/output/Output.java)
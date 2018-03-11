# Process Runner

[![GitHub license](https://img.shields.io/github/license/saptarshidebnath/processrunner.svg)](https://github.com/saptarshidebnath/processrunner/blob/master/LICENSE.txt) [![Build Status](https://travis-ci.org/saptarshidebnath/processrunner.svg?branch=master)](https://travis-ci.org/saptarshidebnath/processrunner) [![codecov](https://codecov.io/gh/saptarshidebnath/processrunner/branch/master/graph/badge.svg)](https://codecov.io/gh/saptarshidebnath/processrunner) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.saptarshidebnath.utilities/ProcessRunner/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.saptarshidebnath.utilities/ProcessRunner) [![Quality Gate](https://sonarqube.com/api/badges/gate?key=com.saptarshidebnath.utilities%3AProcessRunner)](https://sonarcloud.io/dashboard?id=com.saptarshidebnath.utilities%3AProcessRunner) [![Technical debt ratio](https://sonarqube.com/api/badges/measure?key=com.saptarshidebnath.utilities%3AProcessRunner&metric=sqale_debt_ratio)](https://sonarcloud.io/dashboard?id=com.saptarshidebnath.utilities%3AProcessRunner) [![SonarCloud Code Coverage](https://sonarcloud.io/api/badges/measure?key=com.saptarshidebnath.utilities%3AProcessRunner&metric=coverage)](https://sonarcloud.io/dashboard?id=com.saptarshidebnath.utilities%3AProcessRunner) [![codebeat badge](https://codebeat.co/badges/138abe4e-5e31-46a1-8973-910baff2aac0)](https://codebeat.co/projects/github-com-saptarshidebnath-processrunner-master)

![codecov.io](https://codecov.io/gh/saptarshidebnath/processrunner/branch/master/graphs/commits.svg?branch=master)

##### ***Process Runner*** is java based library using which one can **execute system process or scripts [shell scripts, batch file, python scripts, ruby scripts etc] from inside your java program**.

#### Show me the code
```java
// Create configuration
Configuration configuration = new ConfigBuilder("bash", "./script.sh")
    .setMasterLogFile(new File("~/masterlog.json"), Boolean.TRUE, Charset.defaultCharset())
    .enableLogStreaming(Boolean.TRUE).setParam("param1")
    .setParamList(Arrays.asList("param2", "param3"))
    .build();
{
  // Method 1 - start the runner at later point of time in sync and async manner.
  Runner runner = RunnerFactory.getRunner(configuration);
  Output output = runner.run();
  output = runner.runAsync().get();
}
{
  // Method 2 - Start instantly synchronously and async only.
  Output output = RunnerFactory.startProcess(configuration);
  output = RunnerFactory.startAsyncProcess(configuration).get();
}
```

#### Processrunner details

ProcessRunner library can run any system process from java and have the following features :-

1. Only the interpreter and and the command to run in ConfigurationBuilder are mandatory. For optional fields please read forward.
1. **Master log file**: Master log file can be set with file marked for auto delete and charset.
1. **Log Streaming**: The project uses Slf4j api. It will try to stream the logs in realtime via Slf4j.
1. **Set a param**: Set a parameter for the command. This method can be used multiple times.
1. **Set param from a list of String**: Set a parameter for the command from a List of String
1. The process itself can run in seperate or the same thread.
1. The logs are handled in a seperate threads and is written to disk immediately. [ iff masterlog file is configured ]
1. From Output object the log can be saved as : SYSOUT only, SYSERR only or Both SYSOUT and SYSERR. [ iff masterlog file is configured ]
1. From Output pattern of text can be searched which might return a true/false or a list of matching record. [ iff masterlog file is configured ]

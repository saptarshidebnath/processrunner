# processrunner
===============

[![Build Status](https://travis-ci.org/saptarshidebnath/processrunner.svg?branch=master)](https://travis-ci.org/saptarshidebnath/processrunner)

***Process Runner*** is java based library using which one can **execute system process or scripts from inside your java program**.

#####Short Tutorial
----

* **Scenario 1 >> Just do it - [Laydh](https://www.youtube.com/watch?v=XlsIq4V_cNI "What is Laydh") level : Extraordinary**
  * Runs the command / process.
  * Auto deletes the log file. 
  * Returns the exit code.

```java
import com.saptarshidebnath.processrunner.lib.process.ProcessRunnerFactory;
...
...
int runStatus = ProcessRunnerFactory.startProcess("/bin/bash","mkdir -p ~/processrunner/created/directory");
````

* **Scenario 2 >> Lets Do it - [Laydh](https://www.youtube.com/watch?v=XlsIq4V_cNI "What is Laydh") level : Standard**
  * Creates a [ProcessConfiguration](./src/main/java/com/saptarshidebnath/processrunner/lib/process/ProcessConfiguration.java) 
  * Triggers the Porcess/command
  * Returns the [ProcessRunner](./src/main/java/com/saptarshidebnath/processrunner/lib/process/ProcessRunner.java)
  * From the [ProcessRunner](./src/main/java/com/saptarshidebnath/processrunner/lib/process/ProcessRunner.java) you can get the differnet types of log. **Please note that the logs are not auto deleted. You have to manually delete the logs if you want to.**
    * JSON Format log dump : the JSON log an array of the class [OutPut](./src/main/java/com/saptarshidebnath/processrunner/lib/output/Output.java)
    * Seperate
      * sysout
      * syserror


```java
import com.saptarshidebnath.processrunner.lib.process.ProcessRunnerFactory;
import com.saptarshidebnath.processrunner.lib.process.ProcessRunnerFactory;
...
...
ProcessRunner processRunner = ProcessRunnerFactory.getProcess(
                                  "/bin/bash -x",
                                  "mkdir -p ~/processrunner/created/directory", 
                                  new File("/my/commands/working/directory));
int runStatus = processRunner.run();
```
Checkout the logs and stuffs
```java
File jsonLog = processRunner.getJsonLogDump();
processRunner.saveSysOut(new File("~/sysout.txt"));
processRunner.saveSysError(new File("~/syserr.txt"));
jsonLog.delete();
````

* **Scenario 3 >> I want control - [Laydh](https://www.youtube.com/watch?v=XlsIq4V_cNI "What is Laydh") level : Khatua chelle**
  * Evenrthing will be exactly similar to the aove implementation, except the the configuration will be written in full detail.
  * Creates a [ProcessConfiguration](./src/main/java/com/saptarshidebnath/processrunner/lib/process/ProcessConfiguration.java) with all the details. You neeed to provide :-
    * command interpretter
    * command to be executed
    * A File object denoting the work directory
    * A File object denoting where the json log dump file need to be saved.
    * A Boolean denoting if the json file needs to be auto deleted or not
  * The method returns a ProcessRunner from which you can run, get the json log dump create sys out and syserror files
  
```java

import com.saptarshidebnath.processrunner.lib.process.ProcessRunnerFactory;
import com.saptarshidebnath.processrunner.lib.process.ProcessRunnerFactory;
...
...
ProcessRunner processRunner = ProcessRunnerFactory.getProcess(
                                  "/bin/bash -x",
                                  "mkdir -p ~/processrunner/created/directory", 
                                  new File("/my/commands/working/directory),
                                  new File("~/logdump.json"),
                                  true);
int runStatus = processRunner.run();

File jsonLog = processRunner.getJsonLogDump();
processRunner.saveSysOut(new File("~/sysout.txt"));
processRunner.saveSysError(new File("~/syserr.txt"));
```

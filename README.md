<script>
  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
  })(window,document,'script','https://www.google-analytics.com/analytics.js','ga');

  ga('create', 'UA-47766602-3', 'auto');
  ga('send', 'pageview');

</script>

# processrunner

[![Build Status](https://travis-ci.org/saptarshidebnath/processrunner.svg?branch=master)](https://travis-ci.org/saptarshidebnath/processrunner) [![codecov](https://codecov.io/gh/saptarshidebnath/processrunner/branch/master/graph/badge.svg)](https://codecov.io/gh/saptarshidebnath/processrunner) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.saptarshidebnath.utilities/ProcessRunner/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.saptarshidebnath.utilities/ProcessRunner) [![SonarQube Coverage](https://img.shields.io/sonar/http/sonar.qatools.ru/ru.yandex.qatools.allure:allure-core/coverage.svg)](https://sonarqube.com/dashboard?id=com.saptarshidebnath.utilities%3AProcessRunner) [![SonarQube Tech Debt](https://img.shields.io/sonar/http/sonar.qatools.ru/ru.yandex.qatools.allure:allure-core/tech_debt.svg)](https://sonarqube.com/dashboard?id=com.saptarshidebnath.utilities%3AProcessRunner)

***Process Runner*** is java based library using which one can **execute system process or scripts [shell scripts, batch file, python scripts, ruby scripts etc] from inside your java program**.

## Getting Started
Using the library is very easy. You can **start executiing os commands / scripts in litterally a single line of code** as follows :
* Add the following as build / test dependency as **in your maven's pom.xml** or put the jar in the class path.
```xml
<dependency>
    <groupId>com.saptarshidebnath.utilities</groupId>
    <artifactId>ProcessRunner</artifactId>
    <version>0.0.2</version>
</dependency>
```
* Use the following **code to run any command or script** :-
```java
ProcessRunnerFactory.startProcess("cmd.exe /c","echo GNU is not unix", Level.DEBUG);
```

#!/usr/bin/env bash

set -x
mvn clean install
java -jar ./jpeek-0.5-jar-with-dependencies.jar . ./target/jpeek
#google-chrome ./target/site/project-info.html
#google-chrome ./target/jpeek/index.html
#!/usr/bin/env bash

set -x
mvn clean site install
#java -jar ./jpeek-0.5-jar-with-dependencies.jar . ./target/jpeek --overwrite
google-chrome ./target/site/project-info.html
#google-chrome ./jpeek/index.html
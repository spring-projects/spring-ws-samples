#!/bin/bash

set -euo pipefail

MAVEN_OPTS="-Duser.name=jenkins -Duser.home=/tmp/jenkins-home" \
  ./mvnw -s settings.xml clean dependency:list test -Dsort -B

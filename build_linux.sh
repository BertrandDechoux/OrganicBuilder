#!/bin/sh

ANT_HOME=./tools/apache-ant-1.7.0
JUNIT_HOME=./tools/junit4.4

ANT=$ANT_HOME/bin/ant

$ANT -lib $JUNIT_HOME

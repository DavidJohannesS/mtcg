#!/bin/bash
mvn clean install
mvn compile
mvn exec:java -Dexec.mainClass="uni.local.Main"

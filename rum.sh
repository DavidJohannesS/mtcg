#!/bin/bash
mvn compile
mvn exec:java -Dexec.mainClass="uni.local.Main"

#!/bin/bash
skipTests=''

while getopts s flag
do
    case "${flag}" in
	    s) skipTests="-DskipTests ";;
    esac
done

mvn ${skipTests}-f ../pom.xml clean install

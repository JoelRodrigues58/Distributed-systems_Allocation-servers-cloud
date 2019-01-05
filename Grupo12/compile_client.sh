#!/bin/bash

mkdir buildClient
cd ./Projeto/src/
javac -d ../../buildClient/ application/network/Cliente.java
cd ../../buildClient/
jar cfe Cliente.jar application/network/Cliente *
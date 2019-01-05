#!/bin/bash

mkdir buildServer
cd ./Projeto/src/
javac -d ../../buildServer/ application/network/Servidor.java
cd ../../buildServer/
jar cfe Servidor.jar application/network/Servidor *
#!/bin/bash

mkdir buildBots
cd ./Projeto/src/
javac -d ../../buildBots/ -Xlint application/bots/BotMain.java
cd ../../buildBots/
jar cfe BotMain.jar application/bots/BotMain *
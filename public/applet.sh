#!/bin/zsh

if scalac lib.scala common.scala applet.scala
then
  jar cvf Applet.jar *.class
  java -jar proguard.jar @applet.pro
fi

rm *.class

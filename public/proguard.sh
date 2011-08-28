#!/bin/zsh

if scalac lib.scala common.scala $1.scala; then
  if [ -e $1.mf ]; then
    jar cvfm in.jar $1.mf *.class
  else
    jar cvf in.jar *.class
  fi

  java -jar proguard.jar @proguard.pro
  mv out.jar $1.jar
  rm in.jar
fi

rm *.class

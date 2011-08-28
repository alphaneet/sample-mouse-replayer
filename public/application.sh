#!/bin/zsh

if scalac lib.scala common.scala application.scala
then
  scala MouseReplayer
fi

rm *.class

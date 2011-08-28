#!/bin/zsh

if fsc lib.scala common.scala MouseReplayer.scala
then
  scala MouseReplayer
fi

rm *.class

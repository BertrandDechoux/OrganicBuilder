#!/bin/bash

#parameters
SIZE=32
SOURCE_DIR=svg
OUTPUT_DIR=icons

echo -e "\n$SIZE x $SIZE icons will be created in the [$OUTPUT_DIR] directory using the SVG files of the [$SOURCE_DIR] directory."
echo -e "******************************"

#clean before
rm -f $OUTPUT_DIR/*
mkdir -p $OUTPUT_DIR

# inkscape and ImageMagick are required

#inkscape $SOURCE_DIR/cancel.svg -e $OUTPUT_DIR/exit.png -w=$SIZE
inkscape -a 36:36:96:96 $SOURCE_DIR/information_sign_mo_01.svg -e $OUTPUT_DIR/about.png -w=$SIZE
inkscape $SOURCE_DIR/kservices.svg -e $OUTPUT_DIR/parameters.png -w=$SIZE
inkscape $SOURCE_DIR/player_end.svg -e $OUTPUT_DIR/last.png -w=$SIZE
convert -flip -rotate 180 $OUTPUT_DIR/last.png $OUTPUT_DIR//first.png
inkscape $SOURCE_DIR/player_pause.svg -e $OUTPUT_DIR/pause.png -w=$SIZE
inkscape $SOURCE_DIR/player_play.svg -e $OUTPUT_DIR/play.png -w=$SIZE
cp $OUTPUT_DIR/play.png $OUTPUT_DIR/next.png
convert -flip -rotate 180 $OUTPUT_DIR/play.png $OUTPUT_DIR//previous.png
inkscape $SOURCE_DIR/reload.svg -e $OUTPUT_DIR/reset.png -w=$SIZE
inkscape -a 223:588:532:902 $SOURCE_DIR/svg_buttons_lumen_design1.svg -e $OUTPUT_DIR/add.png -w=$SIZE

#end
echo -e "\n******************************"
echo -e "done\n"

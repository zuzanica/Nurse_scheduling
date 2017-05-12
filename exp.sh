#!/usr/bin/env bash

# Author: Zuzana Stidená
# Experiments script for SNT - Nurse rostering problem dataset NCRP 2010 
# takes generated matraces in matraces folder

make clean
make compile

echo "Starting experiments..."
echo "***********************************************"

inFiles=`find -name sprint*.xml | sort`
i=0
for entry in $inFiles
do
    i=$((i+1))
    for (( j=1; j<=1; j++ ))
    do   
        outFolder="out/$i"
        outFolder+="_out$j.txt"
        echo -e "$outFolder"
        java -cp bin/ algorithm.Main $entry >> $outFolder
        echo -e "Test $i iteration $j for $entry processors FINISED."
    done
done
    
echo "***********************************************"
echo -e "Experience FINISED."

rm -f mm

#!/bin/bash
build="NO"
while getopts bf: flag
do
    case "${flag}" in
        b) build="YES";;
        f) filename=${OPTARG};;        
    esac
done


if [ $build == "YES" ]
  then    
    gradle build
    echo "Build complete"    
fi

if [ -z $filename ]
then 
  gradle run -q --console=plain
else
  gradle run --args "$filename"
fi 
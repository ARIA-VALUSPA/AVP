#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cat $DIR/model_all.lst | while read file url; do
  echo -e "\n *** Getting `basename $file`...\n"
  wget "$url" -O $DIR/.."${file}"; 
done



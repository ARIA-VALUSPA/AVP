#!/bin/bash
(cat <<EOF
en 8888 --nbest 1
en 8887 --nbest 5
en 8886 --mode inc
de 9999 --nbest 1
de 9998 --nbest 5
de 9997 --mode inc
EOF
) \
| while read lang port opts; do
  (until ./launch-asr.sh --gpu 1 --lang $lang --socket $port $opts; do 
        echo "Respawning crashed server..."; sleep 3;
  done) &
done

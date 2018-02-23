#!/bin/bash

. ./path.sh

## Config
lang="en"      # language = "en" (English); "de" (German, i.e Deutsch) or "fr" (French)
socket=8888    # socket number = any 4 digits
epoint=true   # perform kaldi endpoint detection = "true" or "false"
nbest=1        # nbest >= 1 (number of output nbest sentences)
mode="utt"     # execution mode = "inc" (incremental ASR), "utt" (utternace based ASR)
beamv=10       # the number of hypotheses tested in the forward pass of the decoding (smaller values make the decoding faster)
lbeamv=6       # value used to  used to prune the lattice further in the backward pass (smaller values make the decoding faster; tipically lbeamv < beamv)
maxactive=2000
gpu=1
bin_root=../bin
conf_root=../conf
model_root=../models

echo "*** ASR Server running ***"

. ./parse_options.sh || exit 1;

bin=${bin_root}
conf=${conf_root}/${lang}
models=${model_root}/${lang}

if [[ "${lang}" != "en" && "${lang}" != "de" && "${lang}" != "fr" ]];then
   echo
   echo "Invalid option value: lang = ${lang}, use en, de, or fr."
   echo
   exit 1
fi

if [ ${gpu} -eq 1 ];then
  sfx=-gpu
fi

server_opts="--online=true --verbose=3 \
         --do-endpointing=${epoint} \
         --max-active=${maxactive} \
         --beam=${beamv} \
         --lattice-beam=${lbeamv} \
         --config=${conf}/online_nnet2_decoding.conf \
         --word-symbol-table=${models}/words.txt"
server_args="${models}/final.mdl ${models}/HCLG.fst ${socket}"

if [ "${mode}" == "utt" ];then
   if [ ${nbest} -eq 1 ];then
      server_cmd="${bin}/online2-audio-nnet2-latgen-faster${sfx} ${server_opts} ${server_args}"
   elif [ ${nbest} -gt 1 ];then
      server_cmd="${bin}/online2-audio-nnet2-latgen-faster${sfx} ${server_opts} --n-best=${nbest} ${server_args}"
   else
      echo
      echo "Invalid option value: nbest = ${nbest}, use nbest >= 1."
      echo
   fi

elif [ "${mode}" == "inc" ];then
     server_cmd="${bin}/online2-audio-nnet2-latgen-faster${sfx} ${server_opts} --n-best=${nbest} --streaming ${server_args}"
else
   echo
   echo "Invalid option value: mode = ${mode}, use inc or utt."
   echo
fi

$server_cmd

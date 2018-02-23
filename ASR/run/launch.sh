#!/bin/sh
gpu=1
lang="en"      # language = "en" (English); "de" (German, i.e Deutsch) or "fr" (French)
socket=8888    # socket number = any 4 digits
epoint=false    # perform kaldi endpoint detection = "true" or "false"
nbest=5        # nbest >= 1 (number of output nbest sentences)
mode="utt"     # execution mode = "inc" (incremental ASR), "utt" (utternace based ASR)
beamv=10       # the number of hypotheses tested in the forward pass of the decoding (smaller values make the decoding faster)
lbeamv=6       # value used to  used to prune the lattice further in the backward pass (smaller values make the decoding faster; tipically lbeamv < beamv)
maxactive=2000

launch_asr_cmd="./launch-asr.sh --gpu ${gpu} --lang ${lang} --socket ${socket} --epoint ${epoint} --nbest ${nbest} --mode ${mode} --beamv ${beamv} --lbeamv ${lbeamv} --maxactive ${maxactive}"

until $launch_asr_cmd; do
  echo "ASR server crashed! Restarting..."; sleep 1
done

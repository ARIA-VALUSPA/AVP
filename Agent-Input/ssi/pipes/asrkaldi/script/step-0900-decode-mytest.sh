#!/bin/bash

BASEDIR=$(dirname $0)
cd $BASEDIR

date

data=../data

. cmd.sh
. path.sh

set -e

use_gpu=false
num_jobs=1
test="mytest2"
mfccdir=mfcc

## Note: the calls of the command "date" are just for testing the time after every step
## remove it in actual test scenario, and maybe you can also remove the many echos
## to save some time

# extract features for the test data
########################################################################################

echo
echo "===> extract mfcc features"
echo
steps/make_mfcc.sh --cmd "$train_cmd" --nj $num_jobs data/$test \
  exp/make_mfcc/$test $mfccdir || exit 1


date

echo
echo "===> compute cepstral mean and variance statistics"
echo
steps/compute_cmvn_stats.sh \
  data/$test exp/make_mfcc/$test $mfccdir || exit 1


date


# decode with NN model
########################################################################################


## This is a check if GUP and cuda are available
## remove the whole if statement and just set dir
## if you are sure GPU and cuda are available
## dir=exp/nnet7a_960_gpu

if $use_gpu; then
  if ! cuda-compiled; then
    cat <<EOF && exit 1 
This script is intended to be used with GPUs but you have not compiled Kaldi with CUDA 
If you want to use GPUs (and have them), go to src/, and configure and make on a machine
where "nvcc" is installed.
EOF
  fi
  dir=exp/nnet7a_960_gpu
else
  dir=exp/nnet7a_960
fi


 
echo
echo "==> decode with NN model, 3-gram LM, and non-transformed features"
echo
steps/nnet2/decode_fast.sh --nj $num_jobs --cmd "$decode_cmd" \
  exp/tri6b/graph_tgsmall data/$test $dir/decode_tgsmall_$test || exit 1;


date


# This is a rescoring step with a 4-gram LM but it consumes
# around 13 seconds, use it if you can afford the running time!

#echo
#echo "==> rescore lattices with large 4-gram LM"
#echo
#steps/lmrescore_const_arpa_fast.sh \
#  --cmd "$decode_cmd" data/lang_test_{tgsmall,fglarge} \
#  data/$test $dir/decode_{tgsmall,fglarge}_$test || exit 1;


#date


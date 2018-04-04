#!/bin/sh
git clone https://github.com/kaldi-asr/kaldi.git
cp online-tcp-* kaldi/src/online2
cp online2-* kaldi/src/online2bin
# Apply patch
(cd kaldi; patch -N -p1 < ../kaldi.patch)
cd kaldi/tools/
echo "Building kaldi tools"
make -j4 > /dev/null 2>&1
cd ../src/
echo "Building kaldi..."
./configure --use-cuda --static > /dev/null
# Manually patch compilation options
echo "CXXFLAGS += -O3 -g0 -mtune=native -DKALDI_NO_PORTAUDIO" >> kaldi.mk
make -j4 depend > /dev/null
make -j4 online2bin > /dev/null

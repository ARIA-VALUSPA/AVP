# ARIA ASR

You can install and run the provided ASR executables on a physical Linux machine, on a Virtual Machine (VM) or on a Linux Subsystem under windows. If building from sources, any Unix or Unix-like platform should be supported; nevertheless we recommend that you use a high-spec physical machine with a GPU running Linux in order to speed up the ARIA-ASR processing.

## Build

To build the ARIA-ASR server simply copy the ASR/src folder and run the command `./build.sh` inside the folder. This will download and patch the latest version of kaldi. Note that all dependencies for kaldi need to be installed first. If you are planning to use the ARIA ASR models, make sure you have GPU support activated on the machine.

## Install

Simply replace the *relevant* executable in ASR/bin by the one you have just created, e.g. assuming you are in the root ASR directory:
```
cp src/kaldi/src/online2bin/online2-audio-nnet2-latgen-faster bin/online2-audio-nnet2-latgen-faster-gpu
```
if you have compiled kaldi with gpu support.

Then follow the instructions in the main ASR folder to install the models / run.

## Technical details

The tcp core of the server is adapted from the *online* subdirectory of kaldi; the executables should support acoustic models generated from either an nnet2 or an nnet3 online recipe. For nnet2 models, use `online2-audio-nnet2-latgen-faster`; for nnet3 models, use `online2-audio-nnet3-latgen-faster`. The ARIA-ASR server is provided under the same Apache license as the core of kaldi.



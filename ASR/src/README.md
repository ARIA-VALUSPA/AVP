# ARIA ASR

## Build

You can install the ASR on a physical Linux machine, on a Virtual Machine (VM) or on a Linux Subsystem under windows. Nonetheless, we recommend that you use a high-spec physical machine with a GPU running Linux in order to speed up the ARIA-ASR processing.

To build the ARIA-ASR server simply copy the ASR/src folder and run the command `./build.sh` inside the folder. This will download and patch the latest version of kaldi. Note that all dependencies for kaldi need to be installed already. If you are planning to use the ARIA ASR models, make sure you have GPU support activated on your machine.

## Install

Simply replace the *relevant* executable in ASR/bin by the one you have just created, e.g. assuming you are in the root ASR directory:
```
cp src/kaldi/src/online2bin/online2-audio-nnet2-latgen-faster bin/online2-audio-nnet2-latgen-faster-gpu
```
if you have compiled kaldi with gpu support.

Then follow the instructions in the main ASR folder to install the models / run.

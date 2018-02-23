

Directory contents
-------------------

This directory contains a universal script called "launch-asr.sh" used to run
the ASR server passing all the options to control the behaviour of the
execution, for example, select language, mode, end-point detection, ..etc.
The script shoud be run as:


./launch-asr.sh --lang [en|de|fr] --socket [ex: 9999] --epoint [true|false] --nbest [n >= 1] --mode [inc|utt] --beamv [ex: 10] --lbeamv [ex: 6] 

lang:

    set the desired language to English (en), German (de), or French (fr). Note
    that to the moment, French is still not available.


socket:

    used to set the socket number to wait for audio on. It could be any 4 digit number.

epoint:

    used to on/off the end-point detection from kaldi tools.

nbest:

    used to select the desired number of nbest list to output. Note that in case
    it is set to 1, a different likkte more efficient binary will be used.

mode:

    used to select the ASR mode. It may be "inc" for incremental ASR. Here, The decoded
    output starts directly when speech starts and changes over time as more speech is
    received. When the utterance ends (i.e. end-point is detected), the decoded output
    is refined once more to the final decoded utterance and outout to a separate line,
    then the server waits for the next utterance. Initial tests show that performing
    the end-point detection as a part of this binary by using "--epoint true" is better
    in this case. The other mode is "utt". Here, either 1- or n-best sentences are output
    after finishing the utterance (i.e. when end-point is detected). The choice of 1- or
    n-best depends on the previous option "--nbest".

beamv:

    used during graph search to prune ASR hypotheses at the state level. It determines the 
    number of hypotheses tested in the forward pass of the decoding.
    
lbeamv:

    used when producing word level lattices after the decoding is finished. It is used to 
    prune the lattice even further before it is saved/output. Some decoders refer to this 
    as the backward pass beam. Lattice beam is typically smaller than the normal beam and 
    it's purpose is to limit the final size of the lattice (i.e., depth, or number of 
    alternatives at each time step).

Note: This directory also contains some out-dated scripts to launch the ASR server in several
separate modes. The scripts are currently not adjusted to run correctly.

**If you want to automatically restart the ASR server (e.g., after a crash), then the `launch.sh` script should be used instead. In this case, the parameters described above should be set inside the file rather than passed inline. This script will start the ASR server by calling `launch-asr.sh` with the specified parameters, and periodically (currently every 5 seconds) monitor the status of the process. If it does not exist, then it will start it again.**

See WIKI pages for full details.

# ARIA ASR

## Installation

You can install the ASR on a physical Linux machine, on a Virtual Machine (VM) or on a Linux Subsystem under windows. Nonetheless, we recommend that you use a high-spec physical machine with a GPU running Linux in order to speed up the ARIA-ASR processing. Alternatively, you can also build the ASR server on any platform where kaldi is supported, which is pretty much any Unix system - including OSX.

To install the ARIA-ASR server simply copy the ASR folder from this repository to your target machine (virtual or physical), and run the command `./install-aria-asr.sh` inside the main folder. This will download the latest models for all languages and install the necessary libraries (you will be asked to enter your sudo password).

### Virtual Machine setup
<!--If you do not have it installed please download and install VMware Workstation Player. The player can be downloaded for free from the official [webpage]( http://www.vmware.com/products/player/playerpro-evaluation.html).-->
- If you do not have it installed please download and install VirtualBox. It can be downloaded for free from the official [webpage](https://www.virtualbox.org/).
- Then, Install the Ubuntu Linux distribution in your VM, and then follow the instructions for installing the ARIA-ASR that indicated above.
- Finally, open the network settings if the Virtual Machine. Under 'Adapter 1', click the 'Port forwarding' button. You will see that we created a rule ('Rule 1') that will forward your machine's connections to the Virtual Machine on port 8888. This rule allows you to connect to the ARIA-ASR-VM on the same machine by using 'localhost' as the ASR IP address.

<!--#### Step 3
Open VMware Workstation Player on your computer. The first time you open VM player select 'Open a Virtual Machine' from the menu and choose 'Ubuntu 64-bit.vmx' from the VM folder (select the option 'I copied it')
Open VirtualBox on your computer. The first time you open it, go to the 'File' menu and select 'Import Appliance'. Then select the Virtual Machine image you just downloaded ('ARIA-ASR-VM.ova').-->
<!--[Download](https://imperialcollegelondon.box.com/v/ARIA-ASR) a Virtual Machine image that contains a pre-installed version of the latest ASR system and models for each language to your hard drive (requires ~ 4.3 GB disk space and may occupy up to 6 GB RAM). Use the following password to access the folder: "ARIA_ASR_2017"-->

### Windows Subsystem for Linux (bash) setup
- If not already installed, follow [these instructions](https://msdn.microsoft.com/en-us/commandline/wsl/install_guide) to install  the Windows Subsystem for Linux on Windows 10.
- Then, follow the instructions for installing the ARIA-ASR as shown above.
- If you see error messages like this - `-bash: '\r': command not found` - you may need to run the command `dos2unix install-aria-asr.sh` to modify newline characters so they are Unix / Cygwin compatible.

### Building from source

1. Check that you have all the dependencies to build [kaldi](https://github.com/kaldi-asr/kaldi). 
1. Follow the instructions in the `src` subfolder to create executables. 

## Updating the models

In case there are new models available, simply run the command `./install-aria-asr.sh` inside the main folder.

## Running the ASR server

The `run` directory inside the `Module-ASR` folder of this repository contains a universal script called `launch-asr.sh` used to run the ASR server passing all the options to control the behaviour of the execution, for example, select language, mode, end-point detection, ..etc. The script should be run as:

`./launch-asr.sh --lang [en|de|fr] --socket [ex: 8888] --epoint [true|false] --nbest [n >= 1] --mode [inc|utt] --beamv [ex: 10] --lbeamv [ex: 6] --maxactive [e.g. 2000]`

* `lang`: desired language - English (en), German (de), or French (fr). 
* `socket`: socket number where the audio will be received (4 digit number).
* `epoint`: turn on (true) or off (false) the Kaldi's end-point detection.
* `nbest`: the number of items in the N-best list. The N-Best list contains N ranked hypotheses for the user’s speech, where the top entry is the engine’s best hypothesis. When the top entry is incorrect, the correct entry is often contained
lower down in the N-Best list.
* `mode`: ASR decoding mode. If set to "inc" it will start the ASR server in incremental mode. In this mode, the decoded output starts immediately when the speech is received and provides transcriptions as soon as a word is detected. The output changes over time as more speech segments are received. When the utterance ends (i.e. end-point is detected), the decoded output is refined using the full utterance. If mode is set to "utt", the ASR will only output the full utterance (i.e. when end-point is detected). 
* `beamv`: used during graph search to prune ASR hypotheses at the state level. It determines the number of hypotheses tested in the forward pass of the decoding. Default value is 10 (optimized during development).
* `lbeamv`: used when producing word level lattices after the decoding is finished. It is used to prune the lattice even further before it is saved/output. Some decoders refer to this as the backward pass beam. Lattice beam is typically smaller than the normal beam and it's purpose is to limit the final size of the lattice (i.e., depth, or number of alternatives at each time step). Default value is 10 (optimized during development).
* `max-active`: the maximum number of states that can be active at one time in the decoder (defaults to 2000 - we recommend to keep this value).

**If it is necessary to automatically restart the ASR server (e.g., after a crash), then the `launch.sh` script should be used instead. In this case, the parameters described above should be set inside the file rather than passed inline (it is simpler to do it like this anyway). This script will start the ASR server by calling `launch-asr.sh` with the specified parameters, and periodically monitor the status of the process. If it crashed, then it will start it again.**

The output returned from an ASR server to the client machine (normally a Windows machine running SSI, or the ARIA_ASR-Client script provided) include a general header with relevant information about the transcription as well as further lines that depend on the options passed to the main "launch.sh" script. Below you can find a description of the ASR server raw output. **For testing purposes we also provide a standalone ASR client that parses the server output and prints it to the screen in plain text and JSON format. To use it go to folder `standalone-client` and read the instructions provided**.   

## Output format (raw)

### Header

This is a sample header:
`RESULT:NUM=5,FORMAT=WSE,RECO-DUR=8.26,INPUT-DUR=5.31,INPUT-TIME-START=0,INPUT-TIME-END=5.31`

* `RESULT:NUM=X` - X is an integer indicates the number of words in the segment being outputted
* `FORMAT=X` - XXX is a string indicating the the format of the result to follow: WSE (word-start-end) / WSEC (word-start-end-confidence)
* `RECO-DUR=X` - X is an float indicating the amount of time (in seconds) that the ASR machine needed to transcribe the segment
* `INPUT-DUR=X` - X is an float indicating the the length (in seconds) of the segment transcribed
* `INPUT-TIME-START=X` - X is an float indicating the the start time of the segment being transcribed (this value is relative to reception of the first segment in the session)
* `INPUT-TIME-END=X` - X is an float indicating the end time of the segment being transcribed (this value is relative to reception of the first segment in the session, i.e., INPUT-TIME-START+INPUT-DUR)

### Transcription
After the header, the actual transcription follows. Below we describe the output format in each transcription mode (`utt with nbest=1`, `utt with nbest>1`, and `inc`).

#### mode="utt" and nbest=1

After the end-point is detected, the output to the socket will be "one" sentence in the form:

    RESULT:NUM=X,FORMAT=WSE,RECO-DUR=X,INPUT-DUR=X,INPUT-TIME-START=X,INPUT-TIME-END=X

    word_1
    word_2
    .....
    word_m
    RESULT:DONE  # mark of segment end

Concrete example (using the file `test_matthew.wav` under `test_audio`):

    RESULT:NUM=1,FORMAT=WSE,RECO-DUR=3.78344,INPUT-DUR=2.16,INPUT-TIME-START=0,INPUT-TIME-END=2.16

    HALLO
    RESULT:DONE

    RESULT:NUM=4,FORMAT=WSE,RECO-DUR=4.95791,INPUT-DUR=2.85,INPUT-TIME-START=2.16,INPUT-TIME-END=5.01

    MY
    NAME
    IS
    MATTHEW
    RESULT:DONE

    RESULT:NUM=4,FORMAT=WSE,RECO-DUR=4.67341,INPUT-DUR=2.64,INPUT-TIME-START=5.01,INPUT-TIME-END=7.65

    A
    YOU
    ENJOY
    YOURSELF
    RESULT:DONE

    RESULT:NUM=3,FORMAT=WSE,RECO-DUR=3.6339,INPUT-DUR=2.14,INPUT-TIME-START=7.65,INPUT-TIME-END=9.79

    WHAT'S
    THE
    DEAD
    RESULT:DONE

    RESULT:NUM=1,FORMAT=WSE,RECO-DUR=4.19895,INPUT-DUR=2.31,INPUT-TIME-START=9.79,INPUT-TIME-END=12.1

    OH
    RESULT:DONE


#### mode="utt" and nbest>1

After the end-point is detected, the output to the socket will be:

    RESULT:NUM=X,FORMAT=WSE,RECO-DUR=X,INPUT-DUR=X,INPUT-TIME-START=X,INPUT-TIME-END=X

    word_1
    word_2
    .....
    word_m
    (0)          # mark of 1st best end
    word_1
    word_2
    .....
    word_v
    (1)          # mark of 2nd best end
    ...
    word_1
    word_2
    .....
    word_w
    (n-1)        # mark of nth best end
    RESULT:DONE  # mark of segment end

Concrete example (using the file `test_matthew.wav` under `test_audio` with nbest set to 3):

    RESULT:NUM=1,FORMAT=WSE,RECO-DUR=3.62239,INPUT-DUR=2.16,INPUT-TIME-START=0,INPUT-TIME-END=2.16

    HALLO
    (0)
    RESULT:NUM=1,FORMAT=WSE,RECO-DUR=0.000172,INPUT-DUR=0,INPUT-TIME-START=2.16,INPUT-TIME-END=2.16
    HELLO
    (1)
    RESULT:DONE

    RESULT:NUM=4,FORMAT=WSE,RECO-DUR=4.86158,INPUT-DUR=2.85,INPUT-TIME-START=2.16,INPUT-TIME-END=5.01

    MY
    NAME
    IS
    MATTHEW
    (0)
    RESULT:NUM=4,FORMAT=WSE,RECO-DUR=0.000178,INPUT-DUR=0,INPUT-TIME-START=5.01,INPUT-TIME-END=5.01
    MY
    NAME
    IS
    MATHIEU
    (1)
    RESULT:NUM=4,FORMAT=WSE,RECO-DUR=7.6e-05,INPUT-DUR=0,INPUT-TIME-START=5.01,INPUT-TIME-END=5.01
    MY
    NAME
    IS
    MATTHIEU
    (2)
    RESULT:DONE

    RESULT:NUM=4,FORMAT=WSE,RECO-DUR=4.41689,INPUT-DUR=2.64,INPUT-TIME-START=5.01,INPUT-TIME-END=7.65

    A
    YOU
    ENJOY
    YOURSELF
    (0)
    RESULT:NUM=4,FORMAT=WSE,RECO-DUR=0.000124,INPUT-DUR=0,INPUT-TIME-START=7.65,INPUT-TIME-END=7.65
    A
    YOU
    ENJOYING
    YOURSELF
    (1)
    RESULT:NUM=4,FORMAT=WSE,RECO-DUR=6.9e-05,INPUT-DUR=0,INPUT-TIME-START=7.65,INPUT-TIME-END=7.65
    THE
    YOU
    ENJOY
    YOURSELF
    (2)
    RESULT:DONE

    RESULT:NUM=3,FORMAT=WSE,RECO-DUR=3.54695,INPUT-DUR=2.14,INPUT-TIME-START=7.65,INPUT-TIME-END=9.79

    WHAT'S
    THE
    DEAD
    (0)
    RESULT:NUM=3,FORMAT=WSE,RECO-DUR=9.6e-05,INPUT-DUR=0,INPUT-TIME-START=9.79,INPUT-TIME-END=9.79
    WHAT'S
    THE
    DAY
    (1)
    RESULT:NUM=3,FORMAT=WSE,RECO-DUR=5.8e-05,INPUT-DUR=0,INPUT-TIME-START=9.79,INPUT-TIME-END=9.79
    WHAT'S
    THAT
    DAY
    (2)
    RESULT:DONE

    RESULT:NUM=1,FORMAT=WSE,RECO-DUR=4.03749,INPUT-DUR=2.31,INPUT-TIME-START=9.79,INPUT-TIME-END=12.1

    OH
    (0)
    RESULT:NUM=1,FORMAT=WSE,RECO-DUR=0.000108,INPUT-DUR=0,INPUT-TIME-START=12.1,INPUT-TIME-END=12.1
    AH
    (1)
    RESULT:NUM=1,FORMAT=WSE,RECO-DUR=5.7e-05,INPUT-DUR=0,INPUT-TIME-START=12.1,INPUT-TIME-END=12.1
    IT
    (2)
    RESULT:DONE

#### mode="inc"

    By default nbest=1 is assumed. While end-point is not yet detected, the output to the socket
    will be "one" (partial) sentence with a marker of the end of partial output. When the end-point
    is detected (i.e. the segment ends completely till the end), the final refined 1-best sentence
    will be the output. The full output format takes the form: (note the number of words will be growing
    over time while outputing partial sentences)

    word_1
    RESULT:PART  # mark the end of a partial output (ex: 1 word)
    word_1
    word_2
    RESULT:PART  # mark the end of a partial output (ex: 2 words)
    .
    .
    .
    word_1
    word_2
    ...
    word_m
    RESULT:PART  # mark the end of a partial output (ex: m words)
    RESULT:DONE  # mark of segment end

Concrete example (using the file `test_matthew.wav` under `test_audio`):

    RESULT:NUM=1,FORMAT=WSE,RECO-DUR=1.34063,INPUT-DUR=0.74,INPUT-TIME-START=0,INPUT-TIME-END=0.74
    IF
    RESULT:PART
    RESULT:NUM=1,FORMAT=WSE,RECO-DUR=0.637065,INPUT-DUR=1.04,INPUT-TIME-START=0,INPUT-TIME-END=1.04
    IF
    RESULT:PART
    RESULT:NUM=1,FORMAT=WSE,RECO-DUR=0.720335,INPUT-DUR=1.34,INPUT-TIME-START=0,INPUT-TIME-END=1.34
    IF
    RESULT:PART

    RESULT:NUM=1,FORMAT=WSE,RECO-DUR=0.743907,INPUT-DUR=1.64,INPUT-TIME-START=0,INPUT-TIME-END=1.64

    HALLO
    RESULT:PART

    RESULT:NUM=1,FORMAT=WSE,RECO-DUR=0.582068,INPUT-DUR=1.94,INPUT-TIME-START=0,INPUT-TIME-END=1.94

    HALLO
    RESULT:PART

    RESULT:NUM=1,FORMAT=WSE,RECO-DUR=0.460786,INPUT-DUR=2.18,INPUT-TIME-START=0,INPUT-TIME-END=2.18

    HALLO
    RESULT:DONE

    RESULT:NUM=1,FORMAT=WSE,RECO-DUR=1.17833,INPUT-DUR=0.74,INPUT-TIME-START=2.18,INPUT-TIME-END=2.92

    MY
    RESULT:PART

    RESULT:NUM=1,FORMAT=WSE,RECO-DUR=0.74521,INPUT-DUR=1.04,INPUT-TIME-START=2.18,INPUT-TIME-END=3.22

    MY
    RESULT:PART

    RESULT:NUM=3,FORMAT=WSE,RECO-DUR=0.729053,INPUT-DUR=1.34,INPUT-TIME-START=2.18,INPUT-TIME-END=3.52

    MY
    NAME
    IS
    RESULT:PART

    RESULT:NUM=4,FORMAT=WSE,RECO-DUR=0.732342,INPUT-DUR=1.64,INPUT-TIME-START=2.18,INPUT-TIME-END=3.82

    MY
    NAME
    IS
    MATTHEW
    RESULT:PART

    RESULT:NUM=4,FORMAT=WSE,RECO-DUR=0.626944,INPUT-DUR=1.94,INPUT-TIME-START=2.18,INPUT-TIME-END=4.12

    MY
    NAME
    IS
    MATTHEW
    RESULT:PART

    RESULT:NUM=4,FORMAT=WSE,RECO-DUR=0.684079,INPUT-DUR=2.24,INPUT-TIME-START=2.18,INPUT-TIME-END=4.42

    MY
    NAME
    IS
    MATTHEW
    RESULT:PART

    RESULT:NUM=4,FORMAT=WSE,RECO-DUR=0.715558,INPUT-DUR=2.54,INPUT-TIME-START=2.18,INPUT-TIME-END=4.72

    MY
    NAME
    IS
    MATTHEW
    RESULT:PART

    RESULT:NUM=4,FORMAT=WSE,RECO-DUR=0.56241,INPUT-DUR=2.83,INPUT-TIME-START=2.18,INPUT-TIME-END=5.01

    MY
    NAME
    IS
    MATTHEW
    RESULT:DONE

    RESULT:NUM=1,FORMAT=WSE,RECO-DUR=1.24526,INPUT-DUR=0.74,INPUT-TIME-START=5.01,INPUT-TIME-END=5.75

    A
    RESULT:PART

    RESULT:NUM=3,FORMAT=WSE,RECO-DUR=0.723277,INPUT-DUR=1.04,INPUT-TIME-START=5.01,INPUT-TIME-END=6.05

    A
    YOU
    ENJOY
    RESULT:PART

    RESULT:NUM=4,FORMAT=WSE,RECO-DUR=0.895836,INPUT-DUR=1.34,INPUT-TIME-START=5.01,INPUT-TIME-END=6.35

    A
    YOU
    ENJOY
    YOURSELF
    RESULT:PART

    RESULT:NUM=4,FORMAT=WSE,RECO-DUR=0.662809,INPUT-DUR=1.64,INPUT-TIME-START=5.01,INPUT-TIME-END=6.65

    A
    YOU
    ENJOY
    YOURSELF
    RESULT:PART

    RESULT:NUM=4,FORMAT=WSE,RECO-DUR=0.609802,INPUT-DUR=1.94,INPUT-TIME-START=5.01,INPUT-TIME-END=6.95

    A
    YOU
    ENJOY
    YOURSELF
    RESULT:PART

    RESULT:NUM=4,FORMAT=WSE,RECO-DUR=0.617726,INPUT-DUR=2.24,INPUT-TIME-START=5.01,INPUT-TIME-END=7.25

    A
    YOU
    ENJOY
    YOURSELF
    RESULT:PART

    RESULT:NUM=4,FORMAT=WSE,RECO-DUR=0.621927,INPUT-DUR=2.54,INPUT-TIME-START=5.01,INPUT-TIME-END=7.55

    A
    YOU
    ENJOY
    YOURSELF
    RESULT:PART

    RESULT:NUM=4,FORMAT=WSE,RECO-DUR=0.21781,INPUT-DUR=2.64,INPUT-TIME-START=5.01,INPUT-TIME-END=7.65

    A
    YOU
    ENJOY
    YOURSELF
    RESULT:DONE

    RESULT:NUM=2,FORMAT=WSE,RECO-DUR=1.20421,INPUT-DUR=0.74,INPUT-TIME-START=7.65,INPUT-TIME-END=8.39

    WHAT'S
    THE
    RESULT:PART

    RESULT:NUM=3,FORMAT=WSE,RECO-DUR=0.78569,INPUT-DUR=1.04,INPUT-TIME-START=7.65,INPUT-TIME-END=8.69

    WHAT'S
    THE
    DEAD
    RESULT:PART

    RESULT:NUM=3,FORMAT=WSE,RECO-DUR=0.714262,INPUT-DUR=1.34,INPUT-TIME-START=7.65,INPUT-TIME-END=8.99

    WHAT'S
    THE
    DEAD
    RESULT:PART

    RESULT:NUM=3,FORMAT=WSE,RECO-DUR=0.72408,INPUT-DUR=1.64,INPUT-TIME-START=7.65,INPUT-TIME-END=9.29

    WHAT'S
    THE
    DEAD
    RESULT:PART

    RESULT:NUM=3,FORMAT=WSE,RECO-DUR=0.636414,INPUT-DUR=1.94,INPUT-TIME-START=7.65,INPUT-TIME-END=9.59

    WHAT'S
    THE
    DEAD
    RESULT:PART

    RESULT:NUM=3,FORMAT=WSE,RECO-DUR=0.391152,INPUT-DUR=2.14,INPUT-TIME-START=7.65,INPUT-TIME-END=9.79

    WHAT'S
    THE
    DEAD
    RESULT:DONE

    RESULT:NUM=1,FORMAT=WSE,RECO-DUR=3.24231,INPUT-DUR=1.34,INPUT-TIME-START=9.79,INPUT-TIME-END=11.13

    IT
    RESULT:PART

    RESULT:NUM=1,FORMAT=WSE,RECO-DUR=0.491226,INPUT-DUR=1.57,INPUT-TIME-START=9.79,INPUT-TIME-END=11.36

    AH
    RESULT:DONE

## Output format (JSON)
Within the ARIA framework the output from the ASR server is parsed and converted into a JSON stream. The JSON elements are the following:

* `content` - always set to "ASR_output" (simply to identify the stream)
* `mode` - the mode in which the ASR server was started: 'utt' or 'inc'. Note that this value is obtained from the directly from ARIA framework and not received from the ASR server. If there is a mismatch the parsing will not work as expected (make sure that you start the ARIA framework with the correct ASR parameters as set in the server)
* `nbest` - the number of best transcriptions being sent by the ASR server (>=1). Note that this value is obtained from the directly from ARIA framework and not received from the ASR server. If there is a mismatch the parsing will not work as expected (make sure that you start the ARIA framework with the correct ASR parameters as set in the server).
* `RECO-DUR=X` - X is an float indicating the amount of time (in seconds) that the ASR machine needed to transcribe the segment
* `idur` - a float value indicating the length (in seconds) of the segment transcribed (same as `INPUT-DUR`)
* `rdur` - a float value indicating the amount of time (in seconds) that the ASR machine needed to transcribe the segment (same as `RECO-DUR`)
* `partial` - a boolean value (True/False) indicating whether the transcription(s) are final or intermediate (partial). This value is only meaningful in `inc` mode.
* `transcriptions` - an array with the `nbest` transcriptions of a given segment that includes 3 parameters:
  * `id` - the id of the transcription (integer values between `0` and `nbest-1`)  
  * `nwords` - the number of words in the transcription  
  * `text` - a string with the transcription itself
  * N.B. there may be less than `nbest` lines in this array, as the ASR may not have been able to detect as many possibilities.

Below, you can find an example of a JSON stream:

    {  
      "content": "ASR_output"
      "mode": "utt",
      "nbest": 5,
      "idur": "2.02",
      "rdur": "0.526953",
      "partial": false,
      "transcriptions":
                      [    
                       {
                        "id": "0",
                        "nwords": 4,
                        "text": "ARE YOU ENJOYING YOURSELF"
                       },
                       {
                        "id": "1",
                        "nwords": 4,
                        "text": "ARE YOU ENJOY YOURSELF"
                       },
                       {
                        "id": "2",
                        "nwords": 4,
                        "text": "A YOU ENJOY YOURSELF"
                       },
                       {
                        "id": "3",
                        "nwords": 3,
                        "text": "YOU ENJOY YOURSELF"
                       }
                      ]
    }

# Module-ASR-Client
This is a standalone client written in Python that can be used to interface the ASR. You can use both live audio from your microphone, or an audio file (in which case it must be mono and sampled at 16kHz).

## Requirements
* Python 2 (it can be easily made to work with Python 3 if the print function syntax is updated)
* PyAudio (https://people.csail.mit.edu/hubert/pyaudio/)

## How to use
To start the client simply call
```bash
python Module-ASR-Client.py \
       -H IP_ADDRESS
       -p PORT_NUMBER
       -am ASR_MODE
       -an ASR_NBEST
       -m CLIENT_MODE
       [-i AUDIO_FILE]
       [-l DEBUG]
```

* `IP_ADDRESS`: IP address of ASR machine (e.g., localhost)
* `PORT_NUMBER`: Port number to communicate with the ASR machine (e.g., 8888)
* `ASR_MODE`: utt or inc
* `ASR_NBEST`: integer larger than 0
* `CLIENT_MODE`: 'live' (use live audio from microphone) or 'rec' (use recorded audio)
* `AUDIO_FILE`: if in 'rec' mode this parameters should be set to the input audio file. Defaults to "test_audio/test_matthew.wav"
* `DEBUG`: if set to 1 it display the ASR machine raw messages. If set to 0 it parses the ASR messages in an adequate format (see below). Defaults to 0.

#### Example with an audio file
```bash
python Module-ASR-Client.py -H 138.253.69.210 -p 8888 -am utt -an 1 -m rec -i test_audio/test_matthew.wav
```
To stop the client in 'rec' mode, do the following:
* `CTRL+Z`, and then `kill %1` (to stop the process)

#### Example with live audio
```bash
python Module-ASR-Client.py -H 138.253.69.210 -p 8888 -am utt -an 1 -m live
```

To stop the client in 'live' mode, do the following:
* `CTRL+C` (to stop the threads)
* `CTRL+Z`, and then `kill %1` (to stop the process)

## Output format
If option '-l' is set to 1 it will print to the terminal the raw messages sent by the ASR machine. If option '-l' is set to 0, then it will parse the ASR outputs in an appropriate format. The results will be shown in plain text and JSON format.

Below we show example outputs in each mode using the pre-recorded file `test_matthew.wav` under `test_audio`(note that these are the same examples described included in the Module-ASR Wiki, where we show the raw, non-parsed outputs).

The JSON output includes relevant information about the utterance being transcribed in the following format:

* `"content": "ASR_output"`
* `"nwords": "X"` - X is an integer indicates the number of words in the segment being transcribed
* `"istart": "X"` - X is an float indicating the start time of the segment being transcribed (this value is relative to
* `"iend": "X"` - X is an float indicating the end time of the segment being transcribed (this value is relative to reception of the first segment in the session, i.e., istart+idur)
* `"idur": "X"` - X is an float indicating the the length (in seconds) of the segment transcribed
reception of the first segment in the session)
* `"rdur": "X"` - X is an float indicating the amount of time (in seconds) that the ASR machine needed to transcribe the segment


#### ASR_MODE = 'utt' and ASR_NBEST = 1
`python Module-ASR-Client.py -H 138.253.69.177 -p 8888 -am utt -an 3 -m rec -i test_audio/test_matthew.wav`

    ***********************************************

    -----
    Text:
    -----
    HALLO

    -----
    JSON:
    -----
    {
      "istart": "0",
      "iend": "2.16",
      "idur": "2.16",
      "rdur": "3.826",
      "nwords": "1",
      "content": "ASR_output",
      "transcriptions": [
        {
          "id": "0",
          "text": "HALLO"
        }
      ]
    }
    ***********************************************

    -----
    Text:
    -----
    MY NAME IS MATTHEW

    -----
    JSON:
    -----
    {
      "istart": "2.16",
      "iend": "5.01",
      "idur": "2.85",
      "rdur": "4.90635",
      "nwords": "4",
      "content": "ASR_output",
      "transcriptions": [
        {
          "id": "0",
          "text": "MY NAME IS MATTHEW"
        }
      ]
    }
    ***********************************************

    -----
    Text:
    -----
    A YOU ENJOY YOURSELF

    -----
    JSON:
    -----
    {
      "istart": "5.01",
      "iend": "7.65",
      "idur": "2.64",
      "rdur": "4.56256",
      "nwords": "4",
      "content": "ASR_output",
      "transcriptions": [
        {
          "id": "0",
          "text": "A YOU ENJOY YOURSELF"
        }
      ]
    }
    ***********************************************

    -----
    Text:
    -----
    WHAT'S THE DEAD

    -----
    JSON:
    -----
    {
      "istart": "7.65",
      "iend": "9.79",
      "idur": "2.14",
      "rdur": "3.54299",
      "nwords": "3",
      "content": "ASR_output",
      "transcriptions": [
        {
          "id": "0",
          "text": "WHAT'S THE DEAD"
        }
      ]
    }
    ***********************************************

    -----
    Text:
    -----
    OH

    -----
    JSON:
    -----
    {
      "istart": "9.79",
      "iend": "12.1",
      "idur": "2.31",
      "rdur": "4.15142",
      "nwords": "1",
      "content": "ASR_output",
      "transcriptions": [
        {
          "id": "0",
          "text": "OH"
        }
      ]
    }

#### ASR_MODE = 'utt' and ASR_NBEST > 1
`python Module-ASR-Client.py -H 138.253.69.177 -p 8888 -am utt -an 3 -m rec -i test_audio/test_matthew.wav`:

    ***********************************************

    -----
    Text:
    -----
    HALLO (0) HELLO (1)

    -----
    JSON:
    -----
    {
      "nwords": "1",
      "rdur": "3.76818",
      "transcriptions": [
        {
          "id": "0",
          "text": "HALLO"
        },
        {
          "id": "1",
          "text": "HELLO"
        }
      ],
      "istart": "0",
      "iend": "2.16",
      "idur": "2.16",
      "content": "ASR_output"
    }
    ***********************************************

    -----
    Text:
    -----
    MY NAME IS MATTHEW (0) MY NAME IS MATHIEU (1) MY NAME IS MATTHIEU (2)

    -----
    JSON:
    -----
    {
      "nwords": "4",
      "rdur": "4.96566",
      "transcriptions": [
        {
          "id": "0",
          "text": "MY NAME IS MATTHEW"
        },
        {
          "id": "1",
          "text": "MY NAME IS MATHIEU"
        },
        {
          "id": "2",
          "text": "MY NAME IS MATTHIEU"
        }
      ],
      "istart": "2.16",
      "iend": "5.01",
      "idur": "2.85",
      "content": "ASR_output"
    }
    ***********************************************

    -----
    Text:
    -----
    A YOU ENJOY YOURSELF (0) A YOU ENJOYING YOURSELF (1) THE YOU ENJOY YOURSELF (2)

    -----
    JSON:
    -----
    {
      "nwords": "4",
      "rdur": "4.59099",
      "transcriptions": [
        {
          "id": "0",
          "text": "A YOU ENJOY YOURSELF"
        },
        {
          "id": "1",
          "text": "A YOU ENJOYING YOURSELF"
        },
        {
          "id": "2",
          "text": "THE YOU ENJOY YOURSELF"
        }
      ],
      "istart": "5.01",
      "iend": "7.65",
      "idur": "2.64",
      "content": "ASR_output"
    }
    ***********************************************

    -----
    Text:
    -----
    WHAT'S THE DEAD (0) WHAT'S THE DAY (1) WHAT'S THAT DAY (2)

    -----
    JSON:
    -----
    {
      "nwords": "3",
      "rdur": "3.62779",
      "transcriptions": [
        {
          "id": "0",
          "text": "WHAT'S THE DEAD"
        },
        {
          "id": "1",
          "text": "WHAT'S THE DAY"
        },
        {
          "id": "2",
          "text": "WHAT'S THAT DAY"
        }
      ],
      "istart": "7.65",
      "iend": "9.79",
      "idur": "2.14",
      "content": "ASR_output"
    }
    ***********************************************

    -----
    Text:
    -----
    OH (0) AH (1) IT (2)

    -----
    JSON:
    -----
    {
      "nwords": "1",
      "rdur": "4.19998",
      "transcriptions": [
        {
          "id": "0",
          "text": "OH"
        },
        {
          "id": "1",
          "text": "AH"
        },
        {
          "id": "2",
          "text": "IT"
        }
      ],
      "istart": "9.79",
      "iend": "12.1",
      "idur": "2.31",
      "content": "ASR_output"
    }



#### ASR_MODE = 'inc'
`python Module-ASR-Client.py -H 138.253.69.177 -p 8888 -am inc -an 1 -m rec -i test_audio/test_matthew.wav`

    ***********************************************

    -----
    Text:
    -----
    HALLO HELLO HELLO HALLO MY MY MY NAME IS

    -----
    JSON:
    -----
    {
      "rdur": "2.65638",
      "iend": "1.34",
      "istart": "0",
      "content": "ASR_output",
      "transcriptions": [
        {
          "id": "0",
          "text": "HALLO HELLO HELLO HALLO MY MY MY NAME IS"
        }
      ],
      "nwords": "1",
      "idur": "1.34"
    }
    ***********************************************

    -----
    Text:
    -----
    MY NAME IS MATTHEW

    -----
    JSON:
    -----
    {
      "rdur": "0.80414",
      "iend": "3.8",
      "istart": "2.16",
      "content": "ASR_output",
      "transcriptions": [
        {
          "id": "0",
          "text": "MY NAME IS MATTHEW"
        }
      ],
      "nwords": "4",
      "idur": "1.64"
    }
    ***********************************************

    -----
    Text:
    -----
    MY NAME IS MATTHEW

    -----
    JSON:
    -----
    {
      "rdur": "0.605507",
      "iend": "4.1",
      "istart": "2.16",
      "content": "ASR_output",
      "transcriptions": [
        {
          "id": "0",
          "text": "MY NAME IS MATTHEW"
        }
      ],
      "nwords": "4",
      "idur": "1.94"
    }
    ***********************************************

    -----
    Text:
    -----
    MY NAME IS MATTHEW

    -----
    JSON:
    -----
    {
      "rdur": "0.649091",
      "iend": "4.4",
      "istart": "2.16",
      "content": "ASR_output",
      "transcriptions": [
        {
          "id": "0",
          "text": "MY NAME IS MATTHEW"
        }
      ],
      "nwords": "4",
      "idur": "2.24"
    }
    ***********************************************

    -----
    Text:
    -----
    MY NAME IS MATTHEW

    -----
    JSON:
    -----
    {
      "rdur": "0.674178",
      "iend": "4.7",
      "istart": "2.16",
      "content": "ASR_output",
      "transcriptions": [
        {
          "id": "0",
          "text": "MY NAME IS MATTHEW"
        }
      ],
      "nwords": "4",
      "idur": "2.54"
    }
    ***********************************************

    -----
    Text:
    -----
    MY NAME IS MATTHEW

    -----
    JSON:
    -----
    {
      "rdur": "0.726887",
      "iend": "5",
      "istart": "2.16",
      "content": "ASR_output",
      "transcriptions": [
        {
          "id": "0",
          "text": "MY NAME IS MATTHEW"
        }
      ],
      "nwords": "4",
      "idur": "2.84"
    }
    ***********************************************

    -----
    Text:
    -----
    MY NAME IS MATTHEW

    -----
    JSON:
    -----
    {
      "rdur": "0.069562",
      "iend": "5.01",
      "istart": "2.16",
      "content": "ASR_output",
      "transcriptions": [
        {
          "id": "0",
          "text": "MY NAME IS MATTHEW"
        }
      ],
      "nwords": "4",
      "idur": "2.85"
    }
    ***********************************************

    -----
    Text:
    -----
    A

    -----
    JSON:
    -----
    {
      "rdur": "1.29273",
      "iend": "5.75",
      "istart": "5.01",
      "content": "ASR_output",
      "transcriptions": [
        {
          "id": "0",
          "text": "A"
        }
      ],
      "nwords": "1",
      "idur": "0.74"
    }
    ***********************************************

    -----
    Text:
    -----
    A YOU ENJOY

    -----
    JSON:
    -----
    {
      "rdur": "0.742711",
      "iend": "6.05",
      "istart": "5.01",
      "content": "ASR_output",
      "transcriptions": [
        {
          "id": "0",
          "text": "A YOU ENJOY"
        }
      ],
      "nwords": "3",
      "idur": "1.04"
    }
    ***********************************************

    -----
    Text:
    -----
    A YOU ENJOY YOURSELF

    -----
    JSON:
    -----
    {
      "rdur": "0.948823",
      "iend": "6.35",
      "istart": "5.01",
      "content": "ASR_output",
      "transcriptions": [
        {
          "id": "0",
          "text": "A YOU ENJOY YOURSELF"
        }
      ],
      "nwords": "4",
      "idur": "1.34"
    }
    ***********************************************

    -----
    Text:
    -----
    A YOU ENJOY YOURSELF

    -----
    JSON:
    -----
    {
      "rdur": "0.658718",
      "iend": "6.65",
      "istart": "5.01",
      "content": "ASR_output",
      "transcriptions": [
        {
          "id": "0",
          "text": "A YOU ENJOY YOURSELF"
        }
      ],
      "nwords": "4",
      "idur": "1.64"
    }
    ***********************************************

    -----
    Text:
    -----
    A YOU ENJOY YOURSELF

    -----
    JSON:
    -----
    {
      "rdur": "0.595324",
      "iend": "6.95",
      "istart": "5.01",
      "content": "ASR_output",
      "transcriptions": [
        {
          "id": "0",
          "text": "A YOU ENJOY YOURSELF"
        }
      ],
      "nwords": "4",
      "idur": "1.94"
    }
    ***********************************************

    -----
    Text:
    -----
    A YOU ENJOY YOURSELF

    -----
    JSON:
    -----
    {
      "rdur": "0.62381",
      "iend": "7.25",
      "istart": "5.01",
      "content": "ASR_output",
      "transcriptions": [
        {
          "id": "0",
          "text": "A YOU ENJOY YOURSELF"
        }
      ],
      "nwords": "4",
      "idur": "2.24"
    }
    ***********************************************

    -----
    Text:
    -----
    A YOU ENJOY YOURSELF

    -----
    JSON:
    -----
    {
      "rdur": "0.668374",
      "iend": "7.55",
      "istart": "5.01",
      "content": "ASR_output",
      "transcriptions": [
        {
          "id": "0",
          "text": "A YOU ENJOY YOURSELF"
        }
      ],
      "nwords": "4",
      "idur": "2.54"
    }
    ***********************************************

    -----
    Text:
    -----
    A YOU ENJOY YOURSELF

    -----
    JSON:
    -----
    {
      "rdur": "0.229225",
      "iend": "7.65",
      "istart": "5.01",
      "content": "ASR_output",
      "transcriptions": [
        {
          "id": "0",
          "text": "A YOU ENJOY YOURSELF"
        }
      ],
      "nwords": "4",
      "idur": "2.64"
    }
    ***********************************************

    -----
    Text:
    -----
    WHAT'S THE

    -----
    JSON:
    -----
    {
      "rdur": "1.1903",
      "iend": "8.39",
      "istart": "7.65",
      "content": "ASR_output",
      "transcriptions": [
        {
          "id": "0",
          "text": "WHAT'S THE"
        }
      ],
      "nwords": "2",
      "idur": "0.74"
    }
    ***********************************************

    -----
    Text:
    -----
    WHAT'S THE DEAD

    -----
    JSON:
    -----
    {
      "rdur": "0.730235",
      "iend": "8.69",
      "istart": "7.65",
      "content": "ASR_output",
      "transcriptions": [
        {
          "id": "0",
          "text": "WHAT'S THE DEAD"
        }
      ],
      "nwords": "3",
      "idur": "1.04"
    }
    ***********************************************

    -----
    Text:
    -----
    WHAT'S THE DEAD

    -----
    JSON:
    -----
    {
      "rdur": "0.679036",
      "iend": "8.99",
      "istart": "7.65",
      "content": "ASR_output",
      "transcriptions": [
        {
          "id": "0",
          "text": "WHAT'S THE DEAD"
        }
      ],
      "nwords": "3",
      "idur": "1.34"
    }
    ***********************************************

    -----
    Text:
    -----
    WHAT'S THE DEAD

    -----
    JSON:
    -----
    {
      "rdur": "0.729283",
      "iend": "9.29",
      "istart": "7.65",
      "content": "ASR_output",
      "transcriptions": [
        {
          "id": "0",
          "text": "WHAT'S THE DEAD"
        }
      ],
      "nwords": "3",
      "idur": "1.64"
    }
    ***********************************************

    -----
    Text:
    -----
    WHAT'S THE DEAD

    -----
    JSON:
    -----
    {
      "rdur": "0.656383",
      "iend": "9.59",
      "istart": "7.65",
      "content": "ASR_output",
      "transcriptions": [
        {
          "id": "0",
          "text": "WHAT'S THE DEAD"
        }
      ],
      "nwords": "3",
      "idur": "1.94"
    }
    ***********************************************

    -----
    Text:
    -----
    WHAT'S THE DEAD

    -----
    JSON:
    -----
    {
      "rdur": "0.390246",
      "iend": "9.79",
      "istart": "7.65",
      "content": "ASR_output",
      "transcriptions": [
        {
          "id": "0",
          "text": "WHAT'S THE DEAD"
        }
      ],
      "nwords": "3",
      "idur": "2.14"
    }
    ***********************************************

    -----
    Text:
    -----
    IF

    -----
    JSON:
    -----
    {
      "rdur": "3.10643",
      "iend": "11.13",
      "istart": "9.79",
      "content": "ASR_output",
      "transcriptions": [
        {
          "id": "0",
          "text": "IF"
        }
      ],
      "nwords": "1",
      "idur": "1.34"
    }
    ***********************************************

    -----
    Text:
    -----
    IF

    -----
    JSON:
    -----
    {
      "rdur": "0.997143",
      "iend": "11.43",
      "istart": "9.79",
      "content": "ASR_output",
      "transcriptions": [
        {
          "id": "0",
          "text": "IF"
        }
      ],
      "nwords": "1",
      "idur": "1.64"
    }
    ***********************************************

    -----
    Text:
    -----
    IF

    -----
    JSON:
    -----
    {
      "rdur": "1.44792",
      "iend": "11.73",
      "istart": "9.79",
      "content": "ASR_output",
      "transcriptions": [
        {
          "id": "0",
          "text": "IF"
        }
      ],
      "nwords": "1",
      "idur": "1.94"
    }
    ***********************************************

    -----
    Text:
    -----
    IF

    -----
    JSON:
    -----
    {
      "rdur": "1.03826",
      "iend": "12.03",
      "istart": "9.79",
      "content": "ASR_output",
      "transcriptions": [
        {
          "id": "0",
          "text": "IF"
        }
      ],
      "nwords": "1",
      "idur": "2.24"
    }
    ***********************************************

    -----
    Text:
    -----
    OH

    -----
    JSON:
    -----
    {
      "rdur": "0.242436",
      "iend": "12.1",
      "istart": "9.79",
      "content": "ASR_output",
      "transcriptions": [
        {
          "id": "0",
          "text": "OH"
        }
      ],
      "nwords": "1",
      "idur": "2.31"
    }

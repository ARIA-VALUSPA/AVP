
Peter LaValle @ gmail.com
2018-03-16

This will be the public AVP3.0.1 release.
Currently; the downloaded models are not ignored


## Update from 3.0 to 3.0.1

If you installed the project by checking it out from GitHub, upgrading from 3.0 to 3.0.1 should be possible if you;

1. commit any changes you've made (just to be safe)
2. merge the 3.0.1 version into your working copy
3. run `RunMeFirst/get_models.bat`

## Requirements (for the Windows computer)

- [Python 3.5.2](https://www.python.org/downloads/release/python-352/) (not 3.5.3, 3.6.1 or not 3.5.0) as the "main" Python on your WIndows computer
    - I tried MiniConda and Sandboxie and neither solution seemed to work.
- you'll need the following pip packages `pip install xmltojson xmldict xmljson xmltodict`
- [Docker](https://store.docker.com/editions/community/docker-ce-desktop-windows) (to run the ASR image)
    - this seems to require Windows 10, but, might work under Windows 7
    - you can also use a Linx host to run the ASR; this is discussed below
- [JDK8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) or later
- `ssh` of some form on the `PATH`
    - if you're using `git` on windows you likely have `ssh` as well
    - the "Git for Windows" `ssh` command is perfect

## Startup (on Windows)

There are four `.bat` files that should be started in order to run the system.

### Setup

#### `RunMeFirst/get_models.bat`

This `.bat` file needs to be run on each Windows working copy of the system.
The script downloads several files that were impractical to include in Git.
These files are ignored by Git, so, if you clone a new copy; you'll need to re-run the script.

#### pip install xmltojson xmljson xmltodict xmldict

These packages are needed by the Agent-Input / SSI system.
Since pip ties packages to user accounts, you'll need to run this command for each user that wishes to run the Agent-Input / SSI component.

### `start-1-amq.bat`

This starts the ActiveMQ service that the components use to communicate. Simply minimise the window and forget about it once it's running. If you're already running ActiveMQ, you do not need to launch this batch file.

### `start-2-asr.bat`

This builds a Docker image and starts the speech recognition system.
Once it says that it's "waiting for clients" it's ready to go.
The ASR system prints its results to STDOUT; if you're trying to diagnose problems you can check for output there.

You will likely see the messages `Error: No such container: avp3-ariaasr` and `SECURITY WARNING: You are building a Docker image from Windows ...`.
These do not indicate a problem.

The author's experience of this system was quite slow when running in CPU-only mode.
While times of 100 seconds for speech recognition on a computer with a Xeon E5-1620 v2 CPU are unusable, it was useful to have a simple setup for testing.

A Linux host with a GPU is discussed later - it was used for our evaluations.

### `start-3-core.bat`

This script launches the Agent-Core / Dialogue Manager which works as a sort of "brain" for ARIA.
This is done through a [Gradle wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) so it only requires the JDK installed on your machine.

There is presently an unimportant exception;

```
[main] INFO  e.a.d.m.InteractionStateManager - InteractionState Wrapper started
Exception in thread "Thread-20" java.util.NoSuchElementException: No line found
        at java.util.Scanner.nextLine(Scanner.java:1540)
        at hmi.flipper2.launcher.FlipperStop.run(FlipperStop.java:16)
        at java.lang.Thread.run(Thread.java:748)
QAM config: resourceQAMfile: data/QAM_AliceEvaluationQuest1.xml
```

While this system is running, there should be random "chatter" every 30 seconds.
As with the ASR, the output from this window can be indicative of the system's status.

### `start-4-agent.bat`

This script launches the Agent-Input and Agent-Output that act as ARIA's body.
Once it's started, there will be a prompt with several options including recording options.
If the user wishes to record audio and video; they should experiment with these options.
At this time, the remaining options should be left as-is and the system should be **Run**.

When **Run**, several smaller windows will appear along with a large amount of console output.
Alice won't *properly* start, but, she should pick up the "random chatter" from the dialogue Manager and the [SSI System][ssi] will begin sending audio to the ASR.
The smaller windows indicate the state of the [SSI System][ssi].

When you are satisfied with the positions of the windows, ticking the "SEND/RECORD" toggle will begin a session with Alice.
Unticking it will stop the session and (if you're making a recording) will finish writing audio/video to the disk.

## Linux, GPU Based ASR

In our experience; the speech recognition was not responsive when running on the CPU.
The included ASR is configured to run on a Linux (Ubuntu) system with a CUDA GPU and [cuBLAS][cuBLAS] installed.
We used a GeForce GTX 660 Ti with 3016M of RAM and had no problems.

### Linux Setup

```
linux $ cd ~
linux $ git clone https://github.com/ARIA-VALUSPA/AVP.git
linux $ cd AVP/ASR/
linux $ chmod +=rwx ./install-aria-asr.sh
linux $ chmod +=rwx run/*.sh
linux $ ./install-aria-asr.sh
```

Other packages (beyond [cuBLAS][cuBLAS]) may be required.
The setup script uses `sudo` and `apt` so will benefit from some attention.

### Windows Launch of ASR

In lieu of running, `start-2-asr.bat` executing the following steps on a command line will start ASR on the Linux machine.
The first command is run on the Windows (front end) machine and tunnels the local port 8888 to the remote host.
This removes any/most need to setup firewall rules or alters the launch configuration for the system.

```
windows $ ssh user@linux-host.com -L8888:localhost:8888
linux $ cd ~
linux $ cd AVP/ASR/run/
linux $ ./launch.sh
```

[ssi]: https://www.informatik.uni-augsburg.de/lehrstuehle/hcm/projects/tools/ssi/
[cuBLAS]: https://developer.nvidia.com/cublas

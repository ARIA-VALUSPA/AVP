# ARIA-Valuspa

First public release of ARIA-Valuspa

------------------------------------

Requirements:
 - Windows 8.1 64bit
 - CUDA for eMax
 - Java 8 64bit JDK (http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
 
 
------------------------------------

Installing:

1. ActiveMQ
	ActiveMQ is bundeled with the system. In order to make it run, the JAVA_HOME environment path has to point to the JDK install directory.
	If any problems arrise, please use this guide http://activemq.apache.org/version-5-getting-started.html .
	
2. Age Gender pipeline
	Please follow the README found at <root>\Agent-Input\ssi\pipes\agender
	
3. ASR
	Please follow the README found at <root>\Agent-Input\ssi\pipes\asrkaldi
	

Running:

	'Start ActiveMQ.bat'        - starts the ActiveMQ broker
	'Stop ActiveMQ.bat'         - stops a running instance of the broker
	'RUN-Emotion-Mimic.bat'     - runs the system in mimic mode - requires ActiveMQ
	'RUN-Dialogue-Manager.bat'  - runs the dialogue manager with text input/output
	'RUN-All.bat'               - starts the full system - requires ActiveMQ
	'Start launcher.bat'        - starts an empty launcher with no configuration. Aditional configurations can be found at <root>\Launcher\config
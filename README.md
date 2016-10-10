-------------------------------------------------
Alexandru Ghitulescu, <alexandru.ghitulescu@nottingham.ac.uk>, 26.08.2016
-------------------------------------------------

# ARIA-Valuspa

Public release of ARIA-Valuspa

------------------------------------

Requirements:
 - Windows 8.1 64bit
 - CUDA for eMax
 - Java 8 64bit JDK (http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
 
 
------------------------------------

Installing:

1. ActiveMQ
	ActiveMQ is bundeled with the system. In order to make it run, the JAVA_HOME environment path has to point to the JDK install directory.
	(for a default instalation of java this would be C:\Program Files\Java\jdk1.8.0_60)
	If any problems arrise, please use this guide http://activemq.apache.org/version-5-getting-started.html .
	
2. Detectors
	- run get_models.bat to get the models
	- follow the instructions in README-ASR to get a virtual machine with the ASR working

Running:

	'Start ActiveMQ.bat'        - starts the ActiveMQ broker
	'Stop ActiveMQ.bat'         - stops a running instance of the broker
	'RUN-Alice.bat'             - starts the full system - requires ActiveMQ to be started first ('Start ActiveMQ.bat')
	'Start launcher.bat'        - starts an empty launcher with no configuration
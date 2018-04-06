-------------------------------------------------
Jelte van Waterschoot, <j.b.vanwaterschoot@utwente.nl>, 01.04.2018
-------------------------------------------------

# ARIA-DM

------------------------------------

Requirements:
 - Java 8 64bit JDK (http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
 - Apache Maven (https://maven.apache.org/)
 - Apache ActiveMQ (http://activemq.apache.org/)
 
------------------------------------

Installing:

1. ActiveMQ
	ActiveMQ is bundeled with the entire system, but could be installed separately. In order to make it run, the JAVA_HOME environment path has to point to the JDK install directory. It's also convenient to add the /bin folder to your PATH of System Environment.
2. Maven
	The DM is built with an included Gradle wrapper.

Set-up:

1. Templates
	You can change which dialogue templates you want to use, which located in the resources/templates folder. You need to adjust the resources/aria.properties file for this, where it says 'templates'.
2. Question-Answer Matcher (QAM)
	To change the QAM files (default responses and/or question-answer pairs), you need to adjust the 'ariaQAM.properties' file

Running:
Once all is set-up, you can run the DM with run.bat and end the program with pressing 'ENTER'. All conversations are stored in the /log folder. 

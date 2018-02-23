@ECHO OFF

SET ACTIVEMQ_HOME=%~dp0\External\apache-activemq-5.12.1

CMD /C "CD External\apache-activemq-5.12.1 && bin\activemq start"

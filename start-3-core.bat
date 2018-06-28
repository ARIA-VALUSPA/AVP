@ECHO OFF

CMD /C "CD Agent-Core-Final && gradlew --console=plain --no-daemon -Dfile.encoding=UTF-8 run"

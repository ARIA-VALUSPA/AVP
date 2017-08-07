
# JAR FILES TO LOAD: PATHS WITH JARS TO ADD TO JAVA CLASSPATH
# make sure there are no spaces and delimit with ':'
modulespaths=lib/*:modules/*:modules/lib/*

# MAIN JAR TO LOAD
# mainjar=FlipperMMDS.jar

# MAIN CLASS TO LOAD
# package name and class name as defined in java file
mainclass=hmi.winger.Main

# DO NOT EDIT BELOW THIS LINE

# add all modules and libraries to classpath
# echo java -cp "$mainjar:$modulespaths" $mainclass

# java -cp "$mainjar:$modulespaths" $mainclass
java -cp $modulespaths $mainclass
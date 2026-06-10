@echo off
set CLASSPATH=.;target\dependency\mysql-connector-java-8.0.33.jar
java RegistrationDatabaseUpdater
echo Database update completed!
pause
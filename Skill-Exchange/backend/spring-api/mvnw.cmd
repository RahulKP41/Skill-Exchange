@ECHO OFF
SETLOCAL

IF NOT DEFINED JAVA_HOME (
  FOR /D %%D IN ("C:\Program Files\Eclipse Adoptium\jdk-*") DO SET "JAVA_HOME=%%~fD"
)

IF DEFINED JAVA_HOME (
  SET "PATH=%JAVA_HOME%\bin;%PATH%"
)

IF DEFINED MAVEN_HOME IF EXIST "%MAVEN_HOME%\bin\mvn.cmd" (
  CALL "%MAVEN_HOME%\bin\mvn.cmd" %*
  EXIT /B %ERRORLEVEL%
)

FOR /D %%D IN ("%LOCALAPPDATA%\Programs\Apache\Maven\apache-maven-*") DO (
  IF EXIST "%%~fD\bin\mvn.cmd" (
    SET "MAVEN_HOME=%%~fD"
  )
)

IF DEFINED MAVEN_HOME IF EXIST "%MAVEN_HOME%\bin\mvn.cmd" (
  CALL "%MAVEN_HOME%\bin\mvn.cmd" %*
  EXIT /B %ERRORLEVEL%
)

mvn %*

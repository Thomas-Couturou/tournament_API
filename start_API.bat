setlocal EnableExtensions EnableDelayedExpansion
call gradlew.bat build

set "CLASSPATH=build\classes\kotlin\main"

for %%G in (build\libs\*.jar) do (
    set "CLASSPATH=!CLASSPATH!;%%G"
)

for /F "delims=" %%H in ('gradlew.bat -q printClasspath') do (
    set "DEPENDENCIES=%%H"
)
set "CLASSPATH=!CLASSPATH!;!DEPENDENCIES!"
start "" java -cp "!CLASSPATH!" com.example.ApplicationKt

endlocal
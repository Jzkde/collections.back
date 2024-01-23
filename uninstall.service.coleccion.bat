@echo off
set SERVICE_NAME=Coleccion
set JAR_PATH=E:\Jzkd\archivo\Jzkd-0.0.1-SNAPSHOT.jar

:uninstall
sc delete %SERVICE_NAME%
echo Desinstalación completada.
goto end

:end

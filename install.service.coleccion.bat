@echo off
set SERVICE_NAME=Coleccion
set JAR_PATH=E:\Jzkd\archivo\Jzkd-0.0.1-SNAPSHOT.jar

:install
sc create %SERVICE_NAME% binPath= "java -jar %JAR_PATH%" start= auto
echo Instalación completada.
goto end

:end

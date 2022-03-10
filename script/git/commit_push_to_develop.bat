@echo off
echo #### timestamp computation
FOR /f "skip=1" %%x in ('wmic os get localdatetime') do if not defined MyDate set MyDate=%%x
FOR /f %%x in ('wmic path win32_localtime get /format:list ^| findstr "="') do set %%x
SET fmonth=00%Month%
SET fday=00%Day%
SET mydate=%Year%%fmonth:~-2%%fday:~-2%
For /f "tokens=1-2 delims=/:" %%a in ("%TIME%") do (set mytime=%%a%%b)
set mytime=%mytime: =0%
SET timestamp=v0.0.0-%mydate%%mytime%

SET commit_message="file server using quarkus publishing %timestamp%"

SET current_directory=%CD%

echo #### pom publishing
cd E:\repository\source\git\org\cyk\pom
git add .
git commit --all -m %commit_message%
git push cyk_file_server_quarkus develop:cyk_pom

echo #### quarkus pom publishing
cd E:\repository\source\git\org\cyk\quarkus\pom
git add .
git commit --all -m %commit_message%
git push cyk_file_server_quarkus develop:cyk_quarkus_pom

echo #### utility publishing
cd E:\repository\source\git\org\cyk\utility
git add .
git commit --all -m %commit_message%
git push cyk_file_server_quarkus develop_0_1_0:cyk_utility

echo #### quarkus extension publishing
cd E:\repository\source\git\org\cyk\quarkus\extension
git add .
git commit --all -m %commit_message%
git push cyk_file_server_quarkus develop:cyk_quarkus_extension

echo #### file server api publishing
cd E:\repository\source\git\org\cyk\system\file\server\api\file-server-api
git add .
git commit --all -m %commit_message%
git push cyk_file_server_quarkus develop:cyk_file_server_api

echo #### file server client rest publishing
cd E:\repository\source\git\org\cyk\system\file\server\client\file-server-client-rest
git add .
git commit --all -m %commit_message%
git push cyk_file_server_quarkus develop:cyk_file_server_client_rest

echo #### file server impl publishing
cd E:\repository\source\git\org\cyk\system\file\server\impl\file-server-impl-quarkus
git add .
git commit --all -m %commit_message%
git push github
git push github develop:develop

cd %current_directory%
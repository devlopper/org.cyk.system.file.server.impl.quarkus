call mvn clean install -f ../../api/file-server-api/pom.xml
call mvnw clean package -Dcyk.tika.server.tests.runnable=false
call docker build -f src/main/docker/Dockerfile.jvm -t "file-server:latest" .
call mvn clean install -f ../../../../../api/collectif-server-api/pom.xml
call ../../../mvnw clean package -f ../../../pom.xml
call docker build -f ../../../src/main/docker/Dockerfile.jvm -t "file-server:latest" ../../../
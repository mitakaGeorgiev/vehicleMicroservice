FROM maven:3.8.3-openjdk-11

COPY pom.xml pom.xml
COPY src/ src/

RUN mvn clean install -DskipTests

EXPOSE 8004

CMD java -jar target/*.jar
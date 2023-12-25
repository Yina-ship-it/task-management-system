FROM openjdk:17.0.2
ADD target/task-management-system-0.0.1-SNAPSHOT.jar backend.jar
ENTRYPOINT ["java", "-jar", "backend.jar"]
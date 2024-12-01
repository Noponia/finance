FROM gradle:8.11.1-jdk17 AS build
WORKDIR /app
# Can't copy directories, so went with this. Ignored files and folders added to .dockerignore
COPY . .
RUN ls /app
RUN gradle build

FROM openjdk:17-oracle
USER root
RUN useradd gradle
WORKDIR /app
COPY --from=build /app/build/libs/* /app
RUN chown -R gradle:gradle /app
RUN chmod -R o-rwx /app
USER gradle
CMD ["java", "-jar", "finance-0.0.1-SNAPSHOT.jar"]

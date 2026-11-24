FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app
COPY gradlew build.gradle settings.gradle ./
COPY gradle ./gradle
RUN chmod +x gradlew
RUN printf '%s\n' \
    'allprojects {' \
    '    tasks.register("resolveMainDependencies") {' \
    '        doLast {' \
    '            def mainSourceSet = project.extensions.getByName("sourceSets").getByName("main")' \
    '            mainSourceSet.compileClasspath.files' \
    '            mainSourceSet.runtimeClasspath.files' \
    '            mainSourceSet.annotationProcessorPath.files' \
    '        }' \
    '    }' \
    '}' > /tmp/resolve-dependencies.gradle \
    && ./gradlew resolveMainDependencies --init-script /tmp/resolve-dependencies.gradle --no-daemon
COPY src ./src
RUN ./gradlew bootJar -x test --no-daemon

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
CMD ["sh", "-c", "java -jar app.jar --server.port=${PORT:-8080}"]

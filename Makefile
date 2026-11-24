.PHONY: setup start

setup:
	./gradlew build --no-daemon

start:
	java -jar build/libs/app-0.0.1-SNAPSHOT.jar

FROM openjdk:11.0-jre
EXPOSE 8080
WORKDIR "/monumental-trees"
COPY ./build/libs/monumental.trees-0.0.1-SNAPSHOT.jar ./
CMD ["java", "-jar", "-noverify", "monumental.trees-0.0.1-SNAPSHOT.jar"]

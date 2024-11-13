# Etapa base
FROM registry.access.redhat.com/ubi8/openjdk-21:1.20 AS runtime
WORKDIR /work/
COPY target/quarkus-app/lib/ /work/lib/
COPY target/quarkus-app/*.jar /work/
COPY target/quarkus-app/app/ /work/app/
COPY target/quarkus-app/quarkus/ /work/quarkus/

EXPOSE 8080
CMD ["java", "-jar", "/work/quarkus-run.jar"]

FROM quay.io/keycloak/keycloak:19.0.2 as builder
LABEL maintainer="embesozzi@gmail.com"

ENV KC_DB=postgres
ENV KC_HTTP_RELATIVE_PATH=/auth

COPY ./target/keycloak-openfga-events-3.0.0-jar-with-dependencies.jar /opt/keycloak/providers/keycloak-openfga-events-3.0.0.jar
RUN /opt/keycloak/bin/kc.sh build

FROM quay.io/keycloak/keycloak:19.0.2

COPY --from=builder /opt/keycloak/lib/quarkus/ /opt/keycloak/lib/quarkus/
COPY --from=builder /opt/keycloak/providers/ /opt/keycloak/providers/

ENTRYPOINT ["/opt/keycloak/bin/kc.sh", "start-dev"]
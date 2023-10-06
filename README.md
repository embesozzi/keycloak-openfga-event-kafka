# Keycloak OpenFGA Custom Event to Kafka

Here is a Custom Event Listener Extension known as Service Provider Interfaces (SPI) which helps to integrate [Keycloak](https://www.keycloak.org/) and [OpenFGA](https://openfga.dev/) through Kafka.
OpenFGA is an open source solution for Fine-Grained Authorization that applies the concept of ReBAC (created by the Auth0 inspired by Zanzibar).

Nevertheless, if you want a direct integration between Keycloak and OpenFGA, please use the Keycloak new extension:

- https://github.com/embesozzi/keycloak-openfga-event-publisher


The SPI implements these steps:
1. listens to the following Keycloak events based on his own Identity, Role and Group model (e.g., User Role Assignment, Role to Role Assignment, etc)
    
2. converts this event into an OpenFGA tuple based on the following [OpenFGA Authorization Schema](openfga/keycloak-authorization-model.json):
<p align="center">
  <img width="70%" height="70%" src="images/openfga-authz-model.png">
</p>
  
3. publishes the event to Kafka. Kafka is a messaging system that safely moves data between systems. When an event has published an OpenFGA Kafka consumer sends the event to the OpenFGA solution.


## Solution Architecture Overview 

This custom Keycloak OpenFGA Event Listener [Diagram Point B] is one of the components described in following links:

:vulcan_salute: Article [Keycloak integration with OpenFGA (based on Zanzibar) for Fine-Grained Authorization at Scale (ReBAC)](https://embesozzi.medium.com/keycloak-integration-with-openfga-based-on-zanzibar-for-fine-grained-authorization-at-scale-d3376de00f9a)
  
:vulcan_salute: Workshop https://github.com/embesozzi/keycloak-openfga-workshop

A brief introduction is described here:

<p align="center">
  <img width="70%" height="70%" src="images/solution-architecture.png">
</p>

* Core:
    * Keycloak [A] is responsible for handling the authentication with the standard OpenID Connect and is managing the user access with his Role Model
    * Keycloak is configured with a custom extension :rocket: [B] [keycloak-openfga-event-listener](https://github.com/embesozzi/keycloak-openfga-event-listener) which listens to the Keycloak events (User Role Assignment, Role to Role Assignment, etc), parses this event into an OpenFGA tuple based on the [Keycloak Authz Schema](openfga/keycloak-authorization-model.json) and publishes the event to Kafka Cluster [C]
    * Kafka OpenFGA Consumer [D] that using the OpenFGA SDK will publish the tuples to the OpenFGA Solution
    * OpenFGA [E] is responsible for applying fine-grained access control. The OpenFGA service answers authorization checks by determining whether a relationship exists between an object and a user
* Other components
    * Store Web Application is integrated with Keycloak by OpenID Connect
    * Store API is protected by OAuth 2.0 and it utilizes the OpenFGA SDK for FGA

## How does it work?
The main purpose of this SPI is to listen to the Keycloak events and publish these events to an OpenFGA solution.

Here is a high level overview of the extension:

<p align="center">
  <img width="40%" height="40%" src="images/listener.png">
</p>

In this case, the extension listens to the Admin Events related to operation in Keycloak Identity, Role and Group model. So far, the extension proceeds with the following steps:

1. Parse and enrich the default Keycloak events in the following cases:

| Keycloak Event (Friendly Name) |               Description                  | 
|--------------------------------|:------------------------------------------:|
| User Role Assignment           |    User is assigned to a Keycloak Role     |
| Role To Role Assignment        | Role is assigned to a parent Keycloak Role |
| Group To Role Assignment       |    Group is assigned to a Keycloak Role    |
| User Group Membership          |        User is assigned to a Group         |


2. Transform the Keycloak event into a OpenFGA tuple and check if that is handled by [keycloak-openfga-authorization-model](keycloak-openfga-authorization-model.json):

| Keycloak Event (Friendly Name) |               OpenFGA Tuple Event                |
|--------------------------------|:------------------------------------------------:|
| User Role Assignment           |   User related to the object Role as assignee    |
| Role To Role Assignment        |    Role related to the object Role as parent     |
| Group To Role Assignment       | Group related to the object Role as parent group |
| User Group Membership          |       User related to a Group as assignee        |

This is all the OpenFGA events handled by the provided [keycloak-openfga-authorization-model](keycloak-openfga-authorization-model.json). You can edit the authorization model to handle the desired events.

3. Publish the event to  the [Kafka](https://kafka.apache.org/) Cluster

So far we don’t have an official Java SDK OpenFGA client to publish the authorization tuples to the OpenFGA. The extension is prepared for the future to use a http client for publishing the events. I will add the feature to the extension as soon as Auth0 releases a Java OpenFGA SDK.


## How to install?

Download a release (*.jar file) that works with your Keycloak version from the [list of releases](https://github.com/embesozzi/keycloak-openfga-event-kafka/releases).
Or you can build with ```bash mvn clean package```

Follow the below instructions depending on your distribution and runtime environment.

### Quarkus-based distro (Keycloak.X)

Copy the jar to the `providers` folder and execute the following command:

```shell
${kc.home.dir}/bin/kc.sh build
```

### Container image (Docker)

For Docker-based setups mount or copy the jar to
- `/opt/keycloak/providers` for Keycloak.X from version `15.1.0`

> **Warning**:
>
> With the release of Keycloak 17 the Quarkus-based distribution is now fully supported by the Keycloak team.
> Therefore, <b>I have not tested this extension in Wildfly-based distro </b> :exclamation: ️

## Module Configuration
The following properties can be set via environment variables following the Keycloak specs, thus each variable MUST use the prefix `KC_SPI_EVENTS_LISTENER_OPENFGA_EVENTS_KAFKA`.


* `KC_SPI_EVENTS_LISTENER_OPENFGA_EVENTS_KAFKA_AUTHORIZATION_MODEL`: The `authorizationModel` handled by this module. See [keycloak-openfga-authorization-model](keycloak-openfga-authorization-model.json)

* `KC_SPI_EVENTS_LISTENER_OPENFGA_EVENTS_KAFKA_SERVICE_HANDLER_NAME`: The `serviceHandlerName` is the name of the service for publishing the events. This version only supports the value: `KAFKA`

* `KC_SPI_EVENTS_LISTENER_OPENFGA_EVENTS_KAFKA_ADMIN_TOPIC` : The `adminTopic` is the name of the kafka topic to where the OpenFGA tuple events will be produced to.

* `KC_SPI_EVENTS_LISTENER_OPENFGA_EVENTS_KAFKA_CLIENT_ID`: The `clientId` used to identify the client in Kafka.

* `KC_SPI_EVENTS_LISTENER_OPENFGA_EVENTS_KAFKA_BOOTSTRAP_SERVERS`: The `bootstrapServers` is a comma separated list of available brokers.


You may want to check [docker-compose.yml](docker-compose.yml) as an example.

## Keycloak Configuration

### Enable OpenFGA Event listener extension in Keycloak
Enable the Keycloak OpenFGA Event Listener extension in Keycloak:

* Open [administration console](http://keycloak:8081)
* Choose realm
* Realm settings
* Select `Events` tab and add `openfga-events-kafka` to Event Listeners.

# Test Cases
The test cases are available in the workshop:

* Workshop: https://github.com/embesozzi/keycloak-openfga-workshop

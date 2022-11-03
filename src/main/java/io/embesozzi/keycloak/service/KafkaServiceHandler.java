package io.embesozzi.keycloak.service;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.utils.StringUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.jboss.logging.Logger;

public class KafkaServiceHandler extends ServiceHandler {
    private static final Logger LOG = Logger.getLogger(KafkaServiceHandler.class);
    private Producer<String, String> producer;

    public KafkaServiceHandler(KeycloakSession session, Config.Scope config){
        super(session, config);
        validateConfig();
        producer = createProducer();
    }

    protected static final String KAFKA_ADMIN_TOPIC = "adminTopic";
    protected static final String KAFKA_CLIENT_ID = "clientId";
    protected static final String KAFKA_BOOTSTRAP_SERVERS = "bootstrapServers";

    @Override
    public void handle(String eventId, String eventValue) throws ExecutionException, InterruptedException, TimeoutException {
        LOG.debug("[OpenFgaEventListener] Kafka producer is sending event id: " + eventId + " with value: " + eventValue + " to topic: " + getAdminTopic());
        ProducerRecord<String, String> record = new ProducerRecord<>(getAdminTopic(), eventId, eventValue);
        Future<RecordMetadata> metaData = producer.send(record);
        RecordMetadata recordMetadata = metaData.get(30, TimeUnit.SECONDS);
        LOG.debug("[OpenFgaEventListener] Received new metadata. \n" +
                "Topic:" + recordMetadata.topic() + "\n" +
                "Partition: " + recordMetadata.partition() + "\n" +
                "Key:" + record.key() + "\n" +
                "Offset: " + recordMetadata.offset() + "\n" +
                "Timestamp: " + recordMetadata.timestamp());
        }

    @Override
    public void validateConfig() {
        StringBuilder message = new StringBuilder();
        message.append(StringUtil.isBlank(getAdminTopic()) ? String.format("Parameter % name is must not be null", KAFKA_ADMIN_TOPIC) : "");
        message.append(StringUtil.isBlank(getClientId()) ? String.format("Parameter % name is must not be null", KAFKA_CLIENT_ID) : "");
        message.append(StringUtil.isBlank(getBootstrapServers()) ? String.format("Parameter % name is must not be null", KAFKA_BOOTSTRAP_SERVERS): "");
        if (message.length() > 0) {
            throw new IllegalStateException(message.toString());
        }
    }

    public Producer<String, String> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, getBootstrapServers());
        props.put(ProducerConfig.CLIENT_ID_CONFIG, getClientId());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // fix Class org.apache.kafka.common.serialization.StringSerializer could not be found.
        Thread.currentThread().setContextClassLoader(null);
        return new KafkaProducer<>(props);
    }

    public String getAdminTopic() {
        return super.config.get(KAFKA_ADMIN_TOPIC);
    }

    public String getBootstrapServers() {
        return super.config.get(KAFKA_BOOTSTRAP_SERVERS);
    }

    public String getClientId() {
        return super.config.get(KAFKA_CLIENT_ID);
    }

}

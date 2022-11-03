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

        // getOptionalConfiguration().forEach(props::put);

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


    public Map<String,Object> getOptionalConfiguration() {
        Map<String, Object> config = new HashMap<>();
        KafkaProducerProperty[] producerProperties = KafkaProducerProperty.values();
        for (KafkaProducerProperty property : producerProperties) {
            if (property.getName() != null && super.config.get(property.getName()) != null) {
                config.put(property.getName(), super.config.get(property.getName()));
            }
        }
        // LOG.debug("OpenFga Kafka optional configuration: " + config);
        return config;
    }


    enum KafkaProducerProperty {
         TRANSACTION_TIMEOUT_MS("transaction.timeout.ms"), //
         TRANSACTION_ID("transactional.id"),
         ACKS("acks"), //
         BUFFER_MEMORY("buffer.memory"), //
         COMPRESSION_TYPE("compression.type"), //
         RETRIES("retries"), //
         SSL_KEY_PASSWORD("ssl.key.password"), //
         SSL_KEYSTORE_LOCATION("ssl.keystore.location"), //
         SSL_KEYSTORE_PASSWORD("ssl.keystore.password"), //
         SSL_TRUSTSTORE_LOCATION("ssl.truststore.location"), //
         SSL_TRUSTSTORE_PASSWORD("ssl.truststore.password"), //
         BATCH_SIZE("batch.size"), //
         CLIENT_DNS_LOOKUP("client.dns.lookup"), //
         CONNECTION_MAX_IDLE_MS("connections.max.idle.ms"), //
         DELIVERY_TIMEOUT_MS("delivery.timeout.ms"), //
         LINGER_MS("linger.ms"), //
         MAX_BLOCK_MS("max.block.ms"), //
         MAX_REQUEST_SIZE("max.request.size"), //
         PARTITIONER_CLASS("partitioner.class"), //
         RECEIVE_BUFFER_BYTES("receive.buffer.bytes"), //
         REQUEST_TIMEOUT_MS("request.timeout.ms"), //
         SASL_CLIENT_CALLBACK_HANDLER_CLASS("sasl.client.callback.handler.class"), //
         SASL_JAAS_CONFIG("sasl.jaas.config"), //
         SASL_KERBEROS_SERVICE_NAME("sasl.kerberos.service.name"), //
         SASL_LOGIN_CALLBACK_HANDLER_CLASS("sasl.login.callback.handler.class"), //
         SASL_LOGIN_CLASS("sasl.login.class"), //
         SASL_MECHANISM("sasl.mechanism"), //
         SECURITY_PROTOCOL("security.protocol"), //
         SEND_BUFFER_BYTES("send.buffer.bytes"), //
         SSL_ENABLED_PROTOCOLS("ssl.enabled.protocols"), //
         SSL_KEYSTORE_TYPE("ssl.keystore.type"), //
         SSL_PROTOCOL("ssl.protocol"), //
         SSL_PROVIDER("ssl.provider"), //
         SSL_TRUSTSTORE_TYPE("ssl.truststore.type"), //
         ENABLE_IDEMPOTENCE("enable.idempotence"), //
         INTERCEPTOR_CLASS("interceptor.classes"), //
         MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION("max.in.flight.requests.per.connection"), //
         METADATA_MAX_AGE_MS("metadata.max.age.ms"), //
         METADATA_MAX_IDLE_MS("metadata.max.idle.ms"), //
         METRIC_REPORTERS("metric.reporters"), //
         METRIC_NUM_SAMPLES("metrics.num.samples"), //
         METRICS_RECORDING_LEVEL("metrics.recording.level"), //
         METRICS_SAMPLE_WINDOW_MS("metrics.sample.window.ms"), //
         RECONNECT_BACKOFF_MAX_MS("reconnect.backoff.max.ms"), //
         RECONNECT_BACKOFF_MS("reconnect.backoff.ms"), //
         RETRY_BACKOFF_MS("retry.backoff.ms"), //
         SASL_KERBEROS_KINIT_CMD("sasl.kerberos.kinit.cmd"), //
         SASL_KERBEROS_MIN_TIME_BEFORE_RELOGIN("sasl.kerberos.min.time.before.relogin"), //
         SASL_KERBEROS_TICKET_RENEW_JITTER("sasl.kerberos.ticket.renew.jitter"), //
         SASL_KERBEROS_TICKET_RENEW_WINDOW_FACTOR("sasl.kerberos.ticket.renew.window.factor"), //
         SASL_LOGIN_REFRESH_BUFFER_SECONDS("sasl.login.refresh.buffer.seconds"), //
         SASL_LOGIN_REFRESH_MIN_PERIOD_SECONDS("sasl.login.refresh.min.period.seconds"), //
         SASL_LOGIN_REFRESH_WINDOW_FACTOR("sasl.login.refresh.window.factor"), //
         SASL_LOGIN_REFRESH_WINDOW_JITTER("sasl.login.refresh.window.jitter"), //
         SECURITY_PROVIDERS("security.providers"), //
         SSL_CIPHER_SUITES("ssl.cipher.suites"), //
         SSL_ENDPOINT_IDENTIFICATION_ALGORITHM("ssl.endpoint.identification.algorithm"), //
         SSL_KEYMANAGER_ALGORITHM("ssl.keymanager.algorithm"), //
         SSL_SECURE_RANDOM_IMPLEMENTATION("ssl.secure.random.implementation"), //
         SSL_TRUSTMANAGER_ALGORITHM("ssl.trustmanager.algorithm");
        private String name;
        private KafkaProducerProperty(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}

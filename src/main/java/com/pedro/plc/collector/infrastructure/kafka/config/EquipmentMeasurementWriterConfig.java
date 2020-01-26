package com.pedro.plc.collector.infrastructure.kafka.config;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import lombok.Getter;
import lombok.Setter;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class EquipmentMeasurementWriterConfig {
    public final static String KAFKA_BROKER_LOCATION = "173.212.240.64:9092";
    public final static String SCHEMA_REGISTRY_LOCATION = "http://173.212.240.64:8081";

    private static EquipmentMeasurementWriterConfig instance = new EquipmentMeasurementWriterConfig();
    private static KafkaProducer<String, GenericRecord> kafkaProducer = null;

    @Getter
    @Setter
    private String topic = "equipment_measurements";

    @Getter
    @Setter
    private String schemaPath = "src/main/resources/schemas/sink/{}-value.avsc";

    public static EquipmentMeasurementWriterConfig getInstance() {
        return instance;
    }

    private EquipmentMeasurementWriterConfig() {
    }

    public KafkaProducer<String, GenericRecord> getKafkaProducer() {
        if (kafkaProducer == null) {
            kafkaProducer = kafkaProducer();
        }
        return kafkaProducer;
    }

    private static KafkaProducer<String, GenericRecord> kafkaProducer() {
        // create properties
        Properties properties = new Properties();
        properties.setProperty(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                KAFKA_BROKER_LOCATION);
        properties.setProperty(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());
        properties.setProperty(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                KafkaAvroSerializer.class.getName());
        properties.setProperty(
                "schema.registry.url", SCHEMA_REGISTRY_LOCATION);

        // Make Safer Producer
        //(Best schemas for balance between performance and consistency)
        properties.setProperty(
                ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        properties.setProperty(
                ProducerConfig.ACKS_CONFIG, "all");
        properties.setProperty(
                ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5");

        // High throughput producer(at the expense of a bit latency and CPU)
        properties.setProperty(
                ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
        properties.setProperty(
                ProducerConfig.LINGER_MS_CONFIG, "20");
        properties.setProperty(
                ProducerConfig.BATCH_SIZE_CONFIG, String.valueOf(32 * 1024)); //32 KB

        // create producer
        KafkaProducer<String, GenericRecord> producer = new KafkaProducer<>(properties);
        return producer;
    }

}

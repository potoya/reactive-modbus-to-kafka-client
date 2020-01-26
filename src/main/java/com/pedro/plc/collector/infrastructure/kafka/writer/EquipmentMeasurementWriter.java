package com.pedro.plc.collector.infrastructure.kafka.writer;

import com.pedro.plc.collector.infrastructure.kafka.config.EquipmentMeasurementWriterConfig;
import com.pedro.plc.collector.infrastructure.kafka.model.EquipmentMeasurement;
import com.pedro.plc.collector.infrastructure.kafka.utils.MessageLogUtils;
import com.pedro.plc.collector.infrastructure.kafka.mapper.AvroEquipmentMeasurementMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

@Slf4j
public class EquipmentMeasurementWriter {
    private KafkaProducer<String, GenericRecord> kafkaProducer;
    private String equipmentMeasurementTopic;
    private AvroEquipmentMeasurementMapper avroEquipmentMeasurementMapper;

    public EquipmentMeasurementWriter() {
        EquipmentMeasurementWriterConfig config = EquipmentMeasurementWriterConfig.getInstance();
        kafkaProducer = config.getKafkaProducer();
        equipmentMeasurementTopic = config.getTopic();
        avroEquipmentMeasurementMapper = new AvroEquipmentMeasurementMapper(equipmentMeasurementTopic);
    }

    public void write(EquipmentMeasurement equipmentMeasurement) {
        String recordKey = equipmentMeasurement.getEquipmentId();
        GenericRecord recordValue = avroEquipmentMeasurementMapper.mapRecordFrom(equipmentMeasurement);
        log.info("Writing equipment measurement [{}] to kafka with key {}", recordValue, recordKey);
        kafkaProducer.send(new ProducerRecord<>(equipmentMeasurementTopic, recordValue),
                (recordMetadata, e) -> {
                    if (e == null) log.info(MessageLogUtils.buildLogMessage(recordKey, recordMetadata));
                    else log.error("Problems writing record", e);
                });
    }

}

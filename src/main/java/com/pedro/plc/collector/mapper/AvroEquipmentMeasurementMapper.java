package com.pedro.plc.collector.mapper;

import com.pedro.plc.collector.kafka.model.EquipmentMeasurement;
import com.pedro.plc.collector.kafka.model.VariableMeasurement;
import com.pedro.plc.collector.kafka.schema.EquipmentMeasurementSinkSchema;
import com.pedro.plc.collector.kafka.utils.StringUtils;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;

import java.util.HashMap;
import java.util.Map;

public class AvroEquipmentMeasurementMapper {
    private String topic;

    public AvroEquipmentMeasurementMapper(String topic){
        this.topic = topic;
    }

    public GenericRecord mapRecordFrom(EquipmentMeasurement equipmentMeasurement) {
        Schema equipmentAvroSchema = EquipmentMeasurementSinkSchema
                .getInstanceFor(topic)
                .getEquipmentAvroSchema();

        Schema variableAvroSchema = equipmentAvroSchema
                .getField(EquipmentMeasurementSinkSchema.EQUIPMENT_MEASUREMENT_MEASUREMENTS_FIELD)
                .schema()
                .getValueType();

        Map<String, GenericRecord> genericRecordMeasurementMap = new HashMap<>();

        for (VariableMeasurement variableMeasurement : equipmentMeasurement.getMeasurements()) {
            GenericRecord avroVariableMeasurement =
                    new GenericRecordBuilder(variableAvroSchema)
                            .set(EquipmentMeasurementSinkSchema.VARIABLE_MEASUREMENT_NAME_FIELD, variableMeasurement.getName())
                            .set(EquipmentMeasurementSinkSchema.VARIABLE_MEASUREMENT_UNITS_FIELD, variableMeasurement.getUnits())
                            .set(EquipmentMeasurementSinkSchema.VARIABLE_MEASUREMENT_MEASURED_VALUE_FIELD, variableMeasurement.getMeasuredValue())
                            .build();

            String transformedVariableNameKey = variableMeasurement.getName()
                    .replaceAll(" ", "_")
                    .replaceAll(",", "")
                    .replaceAll("__", "_")
                    .toUpperCase();

            genericRecordMeasurementMap.put(StringUtils.unaccent(transformedVariableNameKey), avroVariableMeasurement);
        }

        return new GenericRecordBuilder(equipmentAvroSchema)
                .set(EquipmentMeasurementSinkSchema.EQUIPMENT_MEASUREMENT_EQUIPMENT_ID_FIELD, equipmentMeasurement.getEquipmentId())
                .set(EquipmentMeasurementSinkSchema.EQUIPMENT_MEASURMENT_TIMESTAMP_FIELD, equipmentMeasurement.getTimestamp())
                .set(EquipmentMeasurementSinkSchema.EQUIPMENT_MEASUREMENT_MEASUREMENTS_FIELD, genericRecordMeasurementMap)
                .build();
    }
}

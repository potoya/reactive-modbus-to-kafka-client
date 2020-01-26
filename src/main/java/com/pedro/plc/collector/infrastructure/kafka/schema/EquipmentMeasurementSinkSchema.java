package com.pedro.plc.collector.infrastructure.kafka.schema;

import lombok.Getter;
import lombok.Setter;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;


public class EquipmentMeasurementSinkSchema {

    //EquipmentMeasurement fields
    public static final String EQUIPMENT_MEASUREMENT_MEASUREMENTS_FIELD = "measurements";
    public static final String EQUIPMENT_MEASUREMENT_MEASURED_AT_EPOCH_FIELD = "measuredAtEpoch";
    public static final String EQUIPMENT_MEASURMENT_MEASURED_AT_FIELD = "measuredAt";
    public static final String EQUIPMENT_MEASUREMENT_EQUIPMENT_ID_FIELD = "equipmentId";

    //EquipmentVariable fields
    public static final String VARIABLE_MEASUREMENT_NAME_FIELD = "name";
    public static final String VARIABLE_MEASUREMENT_UNITS_FIELD = "units";
    public static final String VARIABLE_MEASUREMENT_MEASURED_VALUE_FIELD = "val";

    //Types
    public static final String SCHEMA_VALUE_EQUIPMENT_MEASUREMENT = "EquipmentMeasurement";
    public static final String SCHEMA_VALUE_VARIABLE = "Variable";

    //namespace
    public static final String NAMESPACE = "com.pedro.plc.collector.infrastructure.kafka";


    private static EquipmentMeasurementSinkSchema instance = new EquipmentMeasurementSinkSchema();

    private EquipmentMeasurementSinkSchema() {
    }

    @Getter
    @Setter
    private Schema equipmentAvroSchema;

    public static EquipmentMeasurementSinkSchema getInstanceFor(String topic) {
        if (instance.equipmentAvroSchema == null) {
            instance.equipmentAvroSchema = buildSchema();
        }
        return instance;
    }

    private static Schema buildSchema() {
        Schema variableSchema = SchemaBuilder.record(SCHEMA_VALUE_VARIABLE)
                .namespace(NAMESPACE)
                .fields()
                .requiredString(VARIABLE_MEASUREMENT_NAME_FIELD)
                .requiredString(VARIABLE_MEASUREMENT_UNITS_FIELD)
                .requiredFloat(VARIABLE_MEASUREMENT_MEASURED_VALUE_FIELD)
                .endRecord();

        return SchemaBuilder.record(SCHEMA_VALUE_EQUIPMENT_MEASUREMENT)
                .namespace(NAMESPACE)
                .fields()
                .requiredString(EQUIPMENT_MEASUREMENT_EQUIPMENT_ID_FIELD)
                .requiredString(EQUIPMENT_MEASURMENT_MEASURED_AT_FIELD)
                .requiredLong(EQUIPMENT_MEASUREMENT_MEASURED_AT_EPOCH_FIELD)
                .name(EQUIPMENT_MEASUREMENT_MEASUREMENTS_FIELD)
                    .type()
                    .map()
                    .values(variableSchema)
                    .noDefault()
                .endRecord();
    }

}

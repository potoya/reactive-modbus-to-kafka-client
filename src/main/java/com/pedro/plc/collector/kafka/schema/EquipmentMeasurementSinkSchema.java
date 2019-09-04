package com.pedro.plc.collector.kafka.schema;

import com.pedro.plc.collector.config.EquipmentMeasurementWriterConfig;
import lombok.Getter;
import org.apache.avro.Schema;
import java.io.File;
import java.io.IOException;

public class EquipmentMeasurementSinkSchema {

    public static final String EQUIPMENT_MEASUREMENT_MEASUREMENTS_FIELD = "measurements";
    public static final String EQUIPMENT_MEASURMENT_TIMESTAMP_FIELD = "timestamp";
    public static final String EQUIPMENT_MEASUREMENT_EQUIPMENT_ID_FIELD = "equipmentId";
    public static final String VARIABLE_MEASUREMENT_NAME_FIELD = "name";
    public static final String VARIABLE_MEASUREMENT_UNITS_FIELD = "units";
    public static final String VARIABLE_MEASUREMENT_MEASURED_VALUE_FIELD = "value";

    private static EquipmentMeasurementSinkSchema instance = new EquipmentMeasurementSinkSchema();

    private EquipmentMeasurementSinkSchema() {
    }

    @Getter
    private Schema equipmentAvroSchema;

    public static EquipmentMeasurementSinkSchema getInstanceFor(String topic) {
        if (instance.equipmentAvroSchema == null) {
            instance.equipmentAvroSchema = load(topic);
        }
        return instance;
    }

    private static Schema load(String topic) {
        String sinkSchemaPath = EquipmentMeasurementWriterConfig.getInstance().getSchemaPath();
        try {
            String path = sinkSchemaPath.replace("{}", topic);
            return new Schema.Parser().parse(new File(path));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load target equipmentAvroSchema", e);
        }
    }

}

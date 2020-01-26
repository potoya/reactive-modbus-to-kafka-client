package com.pedro.plc.collector.gateway;

import com.pedro.plc.collector.domain.entity.Variable;
import com.pedro.plc.collector.domain.valueobject.EquipmentRawMeasurements;
import com.pedro.plc.collector.infrastructure.kafka.model.EquipmentMeasurement;
import com.pedro.plc.collector.infrastructure.kafka.model.VariableMeasurement;
import com.pedro.plc.collector.infrastructure.kafka.writer.EquipmentMeasurementWriter;
import com.pedro.plc.collector.services.EquipmentService;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This data gateway will recieve messages from modbus read task and queue them up for a pool of kafka write executors.
 */
@Slf4j
public class DataGateway {

    private ExecutorService executorService = Executors.newFixedThreadPool(2);
    private EquipmentMeasurementWriter equipmentMeasurementWriter;
    private EquipmentService equipmentService;

    public DataGateway() {
        equipmentMeasurementWriter = new EquipmentMeasurementWriter();
        equipmentService = EquipmentService.getInstance();
    }

    public void send(EquipmentRawMeasurements equipmentRawMeasurements) {
        // Modbus Reader thread
        log.debug("Processing new rawMeasurement for equipment {}", equipmentRawMeasurements.getEquipmentId());
        executorService.execute(createEquipmentWritingTask(equipmentRawMeasurements));
    }

    private Runnable createEquipmentWritingTask(EquipmentRawMeasurements equipmentRawMeasurements) {
        return () -> {
            // Kafka Writer thread
            try {
                // Encode bytes as float array
                ByteBuffer byteBuffer = equipmentRawMeasurements.getByteBuffer();
                float[] rawMeasurement = new float[byteBuffer.limit() / 4];
                byteBuffer.asFloatBuffer().get(rawMeasurement);

                // Map against variables
                List<VariableMeasurement> variableMeasurements = new ArrayList<>();
                String equipmentId = equipmentRawMeasurements.getEquipmentId();
                List<Variable> equipmentVariables = equipmentService.getEquipmentVariables(equipmentId);

                for (int i = 0; i < rawMeasurement.length; i++) {
                    Variable correspondingVariable = equipmentVariables.get(i);
                    variableMeasurements.add(
                            new VariableMeasurement(
                                    correspondingVariable.getName(),
                                    correspondingVariable.getUnits(),
                                    Float.valueOf(rawMeasurement[i])
                            )
                    );
                }

                EquipmentMeasurement equipmentMeasurement =
                        new EquipmentMeasurement(equipmentId, String.valueOf(LocalDateTime.now()), variableMeasurements);

                equipmentMeasurementWriter.write(equipmentMeasurement);
            } catch (Exception e) {
                log.error("Problems writing measurement", e);
            }
        };
    }

}

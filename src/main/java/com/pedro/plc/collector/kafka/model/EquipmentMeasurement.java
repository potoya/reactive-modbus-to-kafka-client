package com.pedro.plc.collector.kafka.model;

import lombok.Value;

import java.util.List;

@Value
public class EquipmentMeasurement {
    private String equipmentId;
    private String timestamp;
    private List<VariableMeasurement> measurements;
}

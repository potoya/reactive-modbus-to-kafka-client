package com.pedro.plc.collector.infrastructure.kafka.model;

import lombok.Value;

@Value
public class VariableMeasurement {
    private String name;
    private String units;
    private Float measuredValue;
}

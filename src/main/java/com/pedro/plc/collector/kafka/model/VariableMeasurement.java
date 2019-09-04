package com.pedro.plc.collector.kafka.model;

import lombok.Getter;
import lombok.Value;

@Value
public class VariableMeasurement {
    private String name;
    private String units;
    private Float measuredValue;
}

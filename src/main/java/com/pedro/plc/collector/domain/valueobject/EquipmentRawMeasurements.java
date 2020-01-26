package com.pedro.plc.collector.domain.valueobject;

import lombok.ToString;
import lombok.Value;

import java.nio.ByteBuffer;

@ToString
@Value
public class EquipmentRawMeasurements {
    private String equipmentId;
    private ByteBuffer byteBuffer;
}

package com.pedro.plc.collector.data.valueobject;

import lombok.Value;

@Value
public class EquipmentAddressRange {
    private EquipmentId equipmentId;
    private Integer firstVariableAddress;
    private Integer lastVariableAddress;
}

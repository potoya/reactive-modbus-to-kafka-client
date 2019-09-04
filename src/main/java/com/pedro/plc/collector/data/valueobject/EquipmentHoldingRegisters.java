package com.pedro.plc.collector.data.valueobject;

import lombok.Value;

/**
 * Contiguous holding registers of one equipment.
 * They start from zero and need a logic adjusment.
 *  Range is (start register to start + numRegisters)
 */

@Value
public class EquipmentHoldingRegisters {
    private String equipmentId;
    private Integer startRegisterIndex;
    private Integer numRegisters;
}

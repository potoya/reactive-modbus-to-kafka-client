package com.pedro.plc.collector.domain.repository;

import com.pedro.plc.collector.domain.entity.Equipment;
import com.pedro.plc.collector.domain.valueobject.EquipmentAddressRange;
import com.pedro.plc.collector.domain.valueobject.EquipmentId;

import java.util.List;

public interface EquipmentRepository {
    Equipment findById(EquipmentId equipmentId);
    Equipment findById(String equipmentId);
    List<EquipmentAddressRange> findAllEquipmentAddressRanges();
}

package com.pedro.plc.collector.data.repository;

import com.pedro.plc.collector.data.valueobject.EquipmentAddressRange;
import com.pedro.plc.collector.data.loader.EquipmentDataLoader;
import com.pedro.plc.collector.data.schema.PlcEquipmentSourceSchema;
import com.pedro.plc.collector.data.entity.Equipment;
import com.pedro.plc.collector.data.valueobject.EquipmentId;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class EquipmentRepository {
    private static EquipmentRepository instance = null;
    private Map<EquipmentId, Equipment> equipmentCollection;

    public static EquipmentRepository get() {
        if (instance == null) {
            instance = new EquipmentRepository();
        }
        return instance;
    }

    private EquipmentRepository() {
        final PlcEquipmentSourceSchema plcModbusSourceSchema = PlcEquipmentSourceSchema.get();
        final Map<EquipmentId, Equipment> equipmentMap = new EquipmentDataLoader().loadFrom(plcModbusSourceSchema);
        this.equipmentCollection = equipmentMap;
        log.info("Loaded equipments variables from modbus schema into repository");
        log.info("{}", equipmentMap);
    }

    public Equipment findById(EquipmentId equipmentId) {
        return equipmentCollection.get(equipmentId);
    }

    public Equipment findById(String equipmentId) {
        return equipmentCollection.get(EquipmentId.fromString(equipmentId));
    }

    public List<EquipmentAddressRange> findAllEquipmentAddressRanges() {
        return equipmentCollection.keySet().stream()
                .map(equipmentId -> equipmentCollection.get(equipmentId))
                .map(equipment -> new EquipmentAddressRange(
                                equipment.getEquipmentId(),
                                equipment.getFirstVariable().getModbusAddress(),
                                equipment.getLastVariable().getModbusAddress()
                        ))
                .collect(Collectors.toList());
    }
}

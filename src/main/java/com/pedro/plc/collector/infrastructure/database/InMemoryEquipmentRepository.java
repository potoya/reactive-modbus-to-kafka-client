package com.pedro.plc.collector.infrastructure.database;

import com.pedro.plc.collector.domain.entity.Equipment;
import com.pedro.plc.collector.domain.entity.ModbusDevice;
import com.pedro.plc.collector.domain.repository.EquipmentRepository;
import com.pedro.plc.collector.domain.valueobject.EquipmentAddressRange;
import com.pedro.plc.collector.domain.valueobject.EquipmentId;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class InMemoryEquipmentRepository implements EquipmentRepository {
    private static InMemoryEquipmentRepository instance = null;
    private Map<EquipmentId, Equipment> equipmentCollection;

    public static InMemoryEquipmentRepository getInstance() {
        if (instance == null) {
            instance = new InMemoryEquipmentRepository();
        }
        return instance;
    }

    private InMemoryEquipmentRepository() {
        final ModbusDevice modbusDevice = ModbusDevice.getInstance();
        final Map<EquipmentId, Equipment> equipmentMap = modbusDevice.getEquipments();
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

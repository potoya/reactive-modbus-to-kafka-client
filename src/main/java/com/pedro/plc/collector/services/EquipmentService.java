package com.pedro.plc.collector.services;

import com.pedro.plc.collector.domain.entity.Variable;
import com.pedro.plc.collector.domain.repository.EquipmentRepository;
import com.pedro.plc.collector.domain.valueobject.EquipmentAddressRange;
import com.pedro.plc.collector.domain.valueobject.EquipmentHoldingRegisters;
import com.pedro.plc.collector.infrastructure.database.EquipmentRepositoryProvider;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class EquipmentService {
    private EquipmentRepository equipmentRepository;
    private static EquipmentService instance = new EquipmentService();

    public static EquipmentService getInstance() {
        return instance;
    }

    private EquipmentService() {
        this.equipmentRepository = EquipmentRepositoryProvider.get();
    }

    public List<EquipmentHoldingRegisters> findAllEquipmentHoldingRegisters() {
        List<EquipmentAddressRange> allEquipmentAddressRanges = equipmentRepository.findAllEquipmentAddressRanges();
        return allEquipmentAddressRanges.stream()
                .map(it -> new EquipmentHoldingRegisters(it.getEquipmentId().toString(),
                        it.getFirstVariableAddress() - 40001,
                        (it.getLastVariableAddress() + 1 - it.getFirstVariableAddress() + 1)))
                .collect(Collectors.toList());
    }

    public List<Variable> getEquipmentVariables(String equipmentId){
        return equipmentRepository.findById(equipmentId).getVariables();
    }
}

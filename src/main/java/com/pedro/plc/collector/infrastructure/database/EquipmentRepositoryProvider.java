package com.pedro.plc.collector.infrastructure.database;

import com.pedro.plc.collector.domain.repository.EquipmentRepository;

public class EquipmentRepositoryProvider {
    public static EquipmentRepository get() {
        return InMemoryEquipmentRepository.getInstance();
    }
}

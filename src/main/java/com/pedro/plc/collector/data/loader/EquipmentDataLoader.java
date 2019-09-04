package com.pedro.plc.collector.data.loader;


import com.pedro.plc.collector.data.schema.PlcEquipmentSourceSchema;
import com.pedro.plc.collector.data.entity.Equipment;
import com.pedro.plc.collector.data.entity.Variable;
import com.pedro.plc.collector.data.valueobject.EquipmentId;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class EquipmentDataLoader {

    public Map<EquipmentId, Equipment> loadFrom(PlcEquipmentSourceSchema config) {
        Map<EquipmentId, Equipment> equipmentMap = new HashMap<>();
        final String[] headers = config.getHeaders();
        final List<Map<String, String>> rawVariables = config.getRawVariables();

        Equipment loadedEquipment = null;
        for (Map<String, String> rawVariable : rawVariables) {
            EquipmentId potentialNewEquipmentId = EquipmentId.fromString(
                    rawVariable.get(headers[PlcEquipmentSourceSchema.EQUIPMENT]));

            if (potentialNewEquipmentId.isNotEmpty()) {
                //load new equipment
                loadedEquipment = new Equipment();
                loadedEquipment.setEquipmentId(potentialNewEquipmentId);

                equipmentMap.put(loadedEquipment.getEquipmentId(), loadedEquipment);
            }

            String name = rawVariable.get(headers[PlcEquipmentSourceSchema.VARIABLE_NAME]);
            String unit = rawVariable.get(headers[PlcEquipmentSourceSchema.UNIT]);
            String modbusAddr = rawVariable.get(headers[PlcEquipmentSourceSchema.MODBUS_ADDRESS]);

            loadedEquipment.getVariables().add(new Variable(name, unit, Double.valueOf(modbusAddr)));
        }

        return equipmentMap;
    }

}

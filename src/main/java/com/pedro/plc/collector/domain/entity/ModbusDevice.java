package com.pedro.plc.collector.domain.entity;

import com.pedro.plc.collector.domain.service.ExcelReader;
import com.pedro.plc.collector.domain.valueobject.EquipmentId;
import com.pedro.plc.collector.infrastructure.storage.ExcelReaderBuilder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * This schema schemas will read the excel file PLC_EQUIPMENT_SOURCE_SCHEMA.xlsx
 */
@Slf4j
@Getter
@Setter
public class ModbusDevice {
    private static final String EXCEL_MODBUS_TABLE_PATH = "schemas/source/PLC_EQUIPMENT_SOURCE_SCHEMA.xlsx";
    private static final int TABLE_DATA_SHEET_ID = 0;
    private static final int ATTRIBUTE_NAMES_ROW = 1;
    private static final int EQUIPMENT_DATA_START_ROW = 2;
    private static final String EOF = "%EOF%";

    public static final int EQUIPMENT = 0;
    public static final int VARIABLE_NAME = 1;
    public static final int UNIT = 2;
    public static final int MODBUS_ADDRESS = 4;

    public static final String HOST = "172.25.19.6";
    public static final int PORT = 502;
    public static final int UNIT_ID = 1;

    private static ModbusDevice singleton = new ModbusDevice();

    private List<Map<String, String>> rawVariables;
    private String[] attributes;
    private Map<EquipmentId, Equipment> equipments;
    private ModbusConnection connection;

    public static ModbusDevice getInstance() {
        return singleton;
    }

    private ModbusDevice() {
        ExcelReader reader = resourceExcelModbusTableReader();
        this.attributes = reader.collectHeadersAt(ATTRIBUTE_NAMES_ROW);
        this.rawVariables = reader.collectRows(EQUIPMENT_DATA_START_ROW, EOF);
        this.equipments = toEquipmentMap();
        this.connection = ModbusConnection.of(HOST, PORT);
        log.info("Loaded values from xlsx schemas: Headers[{}], rawVariables=[{}]", this.attributes, this.rawVariables);
    }

    private ExcelReader resourceExcelModbusTableReader() {
        return new ExcelReaderBuilder()
                .toReadFrom(ExcelReaderBuilder.Location.SYSTEM_RESOURCE)
                .atPath(EXCEL_MODBUS_TABLE_PATH)
                .onlyLoadingSheetAt(TABLE_DATA_SHEET_ID)
                .build();

    }

    private Map<EquipmentId, Equipment> toEquipmentMap() {
        Map<EquipmentId, Equipment> equipmentMap = new HashMap<>();
        final String[] headers = this.getAttributes();
        final List<Map<String, String>> rawVariables = this.getRawVariables();

        Equipment loadedEquipment = null;
        for (Map<String, String> rawVariable : rawVariables) {
            EquipmentId potentialNewEquipmentId = EquipmentId.fromString(
                    rawVariable.get(headers[ModbusDevice.EQUIPMENT]));

            if (potentialNewEquipmentId.isNotEmpty()) {
                //load new equipment
                loadedEquipment = new Equipment();
                loadedEquipment.setEquipmentId(potentialNewEquipmentId);

                equipmentMap.put(loadedEquipment.getEquipmentId(), loadedEquipment);
            }

            String name = rawVariable.get(headers[ModbusDevice.VARIABLE_NAME]);
            String unit = rawVariable.get(headers[ModbusDevice.UNIT]);
            String modbusAddr = rawVariable.get(headers[ModbusDevice.MODBUS_ADDRESS]);

            loadedEquipment.getVariables().add(new Variable(name, unit, Double.valueOf(modbusAddr)));
        }

        return equipmentMap;
    }

}

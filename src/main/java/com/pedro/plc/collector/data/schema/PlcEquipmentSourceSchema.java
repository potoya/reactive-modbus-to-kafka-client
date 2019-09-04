package com.pedro.plc.collector.data.schema;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;

import java.io.InputStream;
import java.util.*;

/**
 * This schema schemas will read the excel file PLC_EQUIPMENT_SOURCE_SCHEMA.xlsx
 */
@Slf4j
@Getter
public class PlcEquipmentSourceSchema {
    private static final String XLSX_MODBUS_SCHEMA_PATH = "schemas/source/PLC_EQUIPMENT_SOURCE_SCHEMA.xlsx";
    private static final int SCHEMA_SHEET_INDEX = 0;
    private static final int ATTRIBUTES_NAMES_ROW = 1;
    private static final int EQUIPMENT_DATA_START_ROW = 2;
    private static final String END_OF_FILE_TAG = "%EOF%";
    private static PlcEquipmentSourceSchema singleton = new PlcEquipmentSourceSchema();

    public static final int EQUIPMENT = 0;
    public static final int VARIABLE_NAME = 1;
    public static final int UNIT = 2;
    public static final int MODBUS_ADDRESS = 4;

    private List<Map<String, String>> rawVariables;
    private String[] headers;

    public static PlcEquipmentSourceSchema get() {
        return singleton;
    }

    private PlcEquipmentSourceSchema() {
        loadModbusPlcSchemaWithXlsx(XLSX_MODBUS_SCHEMA_PATH);
    }

    private void loadModbusPlcSchemaWithXlsx(String path) {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(path);
        if (inputStream == null) {
            throw new IllegalStateException("PLC_MODBUS_SCHEMA not found in resources folder");
        }

        try (Workbook plcModbusSchemaWorkbook = WorkbookFactory.create(inputStream)) {
            Sheet singleSheet = plcModbusSchemaWorkbook.getSheetAt(SCHEMA_SHEET_INDEX);
            Objects.requireNonNull(singleSheet);

            this.setHeaders(singleSheet);
            this.setRawVariables(singleSheet);

            log.info("Loaded values from xlsx schemas: Headers[{}], rawVariables=[{}]", this.headers);
        } catch (Exception e) {
            log.error("Failed loading modbus schema", e);
        }
    }

    private void setHeaders(Sheet singleSheet) {
        final Row attributeNamesRow = singleSheet.getRow(ATTRIBUTES_NAMES_ROW);
        final short numColumns = attributeNamesRow.getLastCellNum();

        String[] array = new String[numColumns];
        for (int i = 0; i < numColumns; i++) {
            array[i] = getCellStringValue(attributeNamesRow.getCell(i));
        }

        this.headers = array;
    }

    private void setRawVariables(Sheet singleSheet) {
        List<Map<String, String>> parsedRawVariables = new ArrayList<>();

        for (int i = EQUIPMENT_DATA_START_ROW; i < singleSheet.getLastRowNum(); i++) {
            Row row = singleSheet.getRow(i);
            String rowsFirstColValue = getCellStringValue(row.getCell(0));
            if (rowsFirstColValue.equals(END_OF_FILE_TAG)) break;

            short lastCellNum = getLastCellNum(row);
            Map<String, String> attributes = new HashMap<>();
            for (int j = 0; j < lastCellNum; j++) {
                Cell cell = row.getCell(j);
                String cellStringValue = getCellStringValue(cell);
                attributes.put(this.headers[j], cellStringValue);
            }
            parsedRawVariables.add(attributes);
        }

        this.rawVariables = parsedRawVariables;
    }

    private String getCellStringValue(Cell cell) {
        CellType cellType = cell.getCellType();
        switch (cellType) {
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case STRING:
                return cell.getStringCellValue();
            case BLANK:
                return "";
            default:
                throw new IllegalArgumentException("Uknonwn type please use plain strings or numbers in the file");
        }
    }

    private short getLastCellNum(Row row) {
        final short lastCellNum = row.getLastCellNum();
        if (lastCellNum > 5) {
            throw new IllegalStateException("Invalid Schema: The file should have max of 5 columns but had " + lastCellNum);
        }
        return lastCellNum;
    }

}

package com.pedro.plc.collector.infrastructure.storage;

import com.pedro.plc.collector.domain.service.ExcelReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;

import java.io.InputStream;
import java.util.*;

@Slf4j
public class FileResourceExcelReader implements ExcelReader {
    private Sheet loadedSheet;

    public FileResourceExcelReader(String filePath, int singleSheetIndex) {
        readSheet(filePath, singleSheetIndex);
    }

    private void readSheet(String path, int sheetId) {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(path);
        if (inputStream == null) {
            throw new IllegalStateException("PLC_MODBUS_SCHEMA not found in resources folder");
        }

        try (Workbook plcModbusSchemaWorkbook = WorkbookFactory.create(inputStream)) {
            Sheet singleSheet = plcModbusSchemaWorkbook.getSheetAt(sheetId);
            Objects.requireNonNull(singleSheet);
            this.loadedSheet = singleSheet;
        } catch (Exception e) {
            log.error("Failed loading modbus schema", e);
        }
    }

    public String[] collectHeadersAt(int headerRowIndex) {
        final Row attributeNamesRow = this.loadedSheet.getRow(headerRowIndex);
        final short numColumns = attributeNamesRow.getLastCellNum();

        String[] array = new String[numColumns];
        for (int i = 0; i < numColumns; i++) {
            array[i] = getCellStringValue(attributeNamesRow.getCell(i));
        }

        return array;
    }

    public List<Map<String, String>> collectRows(int offset, String eof) {
        String[] headers = collectHeadersAt(offset - 1);

        List<Map<String, String>> rows = new ArrayList<>();

        for (int i = offset; i < this.loadedSheet.getLastRowNum(); i++) {
            Row row = this.loadedSheet.getRow(i);
            String rowsFirstColValue = getCellStringValue(row.getCell(0));
            if (rowsFirstColValue.equals(eof)) break;

            short lastCellNum = getLastCellNum(row);
            Map<String, String> attributes = new HashMap<>();
            for (int j = 0; j < lastCellNum; j++) {
                Cell cell = row.getCell(j);
                String cellStringValue = getCellStringValue(cell);
                attributes.put(headers[j], cellStringValue);
            }
            rows.add(attributes);
        }

        return rows;
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
        return lastCellNum;
    }

}

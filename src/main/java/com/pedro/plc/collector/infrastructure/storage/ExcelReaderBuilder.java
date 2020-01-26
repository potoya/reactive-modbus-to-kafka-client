package com.pedro.plc.collector.infrastructure.storage;

import com.pedro.plc.collector.domain.service.ExcelReader;

public class ExcelReaderBuilder {
    public enum Location {SYSTEM_RESOURCE}

    private String path;
    private Location location;
    private int sheetIndex;

    public ExcelReaderBuilder() {}

    public ExcelReaderBuilder atPath(String path) {
        this.path = path;
        return this;
    }

    public ExcelReaderBuilder toReadFrom(Location location) {
        this.location = location;
        return this;
    }

    public ExcelReaderBuilder onlyLoadingSheetAt(int sheetIndex) {
        this.sheetIndex = sheetIndex;
        return this;
    }

    public ExcelReader build() {
        switch (this.location) {
            case SYSTEM_RESOURCE:
                return new FileResourceExcelReader(this.path, this.sheetIndex);
        }

        return null;
    }

}

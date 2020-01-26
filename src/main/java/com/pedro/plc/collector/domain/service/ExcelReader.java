package com.pedro.plc.collector.domain.service;

import java.util.List;
import java.util.Map;

public interface ExcelReader {
    String[] collectHeadersAt(int headerRowIndex);
    List<Map<String, String>> collectRows(int offset, String eof);
}

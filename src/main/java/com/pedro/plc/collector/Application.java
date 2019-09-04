package com.pedro.plc.collector;

import com.pedro.plc.collector.modbus.ModbusConnector;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        log.info("Starting plc collector");

        new ModbusConnector().start();
    }

}

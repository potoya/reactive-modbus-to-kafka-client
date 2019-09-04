/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pedro.modbus.collector;

import com.pedro.modbus.collector.slave.ModbusTcpTestSlave;
import com.pedro.plc.collector.Application;
import com.pedro.plc.collector.config.EquipmentMeasurementWriterConfig;
import com.pedro.plc.collector.config.ModbusConnectorConfig;
import com.pedro.plc.collector.modbus.ModbusConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author PC
 */
public class ApplicationTest {

    public static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws Exception {
        ModbusTcpTestSlave.asNewSprcPlcSlaveMock().start();

        ModbusConnectorConfig.getInstance()
                .setModbusTcpMasterConfig(
                        ModbusConnectorConfig.getInstance()
                                .buildModbusTcpMasterConfig("localhost", 50601));

        EquipmentMeasurementWriterConfig.getInstance().setTopic("equipment_measurements_test");
        EquipmentMeasurementWriterConfig.getInstance().setSchemaPath("src/test/resources/schemas/sink/{}-value.avsc");

        new ModbusConnector().start();
    }

}

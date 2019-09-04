/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pedro.modbus.collector.slave;

import com.digitalpetri.modbus.slave.ModbusTcpSlave;
import com.digitalpetri.modbus.slave.ModbusTcpSlaveConfig;
import com.digitalpetri.modbus.slave.ServiceRequestHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author PC
 */
@Slf4j
public class ModbusTcpTestSlave {

    private final ModbusTcpSlaveConfig config
            = new ModbusTcpSlaveConfig.Builder().build();

    private final ModbusTcpSlave slave = new ModbusTcpSlave(config);

    private ServiceRequestHandler serviceRequestHandler;

    private ModbusTcpTestSlave(ModbusTcpSlaveRequestHandler serviceRequestHandler){
        this.serviceRequestHandler = serviceRequestHandler;
    }

    public static ModbusTcpTestSlave asNewSprcPlcSlaveMock(){
        final Integer[] generatedIntRange = IntStream.range(1, 545).boxed().toArray(Integer[]::new);
        return ModbusTcpTestSlave.create().setHoldingRegisters(generatedIntRange);
    }

    public static ModbusTcpTestSlave create() {
        return new ModbusTcpTestSlave(new ModbusTcpSlaveRequestHandler());
    }

    public ModbusTcpTestSlave setHoldingRegisters(Integer... registerValues) {
        List<Short> holdingRegisters = new ArrayList<>();
        for (Integer value : registerValues) {
            holdingRegisters.add(value.shortValue());
        }
        this.serviceRequestHandler = new ModbusTcpSlaveRequestHandler(holdingRegisters);
        return this;
    }

    public void start() throws Exception {
        slave.setRequestHandler(serviceRequestHandler);
        slave.bind("localhost", 50601).get();
        log.info("Started modbus slave server on localhost:{}", 50601);
    }

    public void stop() {
        slave.shutdown();
        log.info("Shutdown modbus slave server on localhost:{}", 50601);
    }

    public String getHost(){
        return "localhost";
    }

    public int getPort(){
        return 50601;
    }
}

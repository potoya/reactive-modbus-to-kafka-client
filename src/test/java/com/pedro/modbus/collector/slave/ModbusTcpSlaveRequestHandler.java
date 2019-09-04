/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pedro.modbus.collector.slave;

import com.digitalpetri.modbus.requests.ReadHoldingRegistersRequest;
import com.digitalpetri.modbus.responses.ReadHoldingRegistersResponse;
import com.digitalpetri.modbus.slave.ModbusTcpSlave;
import com.digitalpetri.modbus.slave.ServiceRequestHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

/**
 * Responds with randoms for each holding register by default
 *
 * @author PC
 */
public class ModbusTcpSlaveRequestHandler implements ServiceRequestHandler {

    private final Logger logger = LoggerFactory.getLogger(ModbusTcpSlave.class);

    private List<Short> holdingRegisters;

    public ModbusTcpSlaveRequestHandler() {
    }

    public ModbusTcpSlaveRequestHandler(List<Short> holdingRegisters) {
        this.holdingRegisters = holdingRegisters;
    }

    @Override
    public void onReadHoldingRegisters(
            ServiceRequest<ReadHoldingRegistersRequest, ReadHoldingRegistersResponse> service) {
        ReadHoldingRegistersRequest request = service.getRequest();

        logger.info("Processing request for address "
                + request.getAddress() + " and quantity " + request.getQuantity());

        ByteBuf registers = writeFixedHoldingRegisters(request);

        service.sendResponse(new ReadHoldingRegistersResponse(registers));

        ReferenceCountUtil.release(service);
    }

    public void setHoldingRegistersToRandom() {
        Random rand = new Random();
        for (int i = 0; i < this.holdingRegisters.size(); i++) {
            final Integer randomInt = rand.nextInt(65536);
            this.holdingRegisters.set(i, randomInt.shortValue());
        }
    }

    private ByteBuf writeFixedHoldingRegisters(ReadHoldingRegistersRequest request) {
        ByteBuf registers = PooledByteBufAllocator.DEFAULT
                .buffer(request.getQuantity());

        final int addr = request.getAddress();
        final int displacement = request.getQuantity();

        if (addr + displacement > holdingRegisters.size()) {
            //modbus error code...
            throw new IllegalStateException("Addr + quantity is outside holding registers");
        }

        List<Short> subList = holdingRegisters.subList(addr, addr + displacement);
        for (Short val : subList) {
            registers.writeShort(val);
        }

        return registers;
    }

}
